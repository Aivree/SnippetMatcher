using System;
using System.Collections.Generic;
using System.Linq;
using System.Transactions;
using System.Web;
using System.Web.Mvc;
using System.Web.Security;
using DotNetOpenAuth.AspNet;
using Microsoft.Web.WebPages.OAuth;
using WebMatrix.WebData;
using DivingSite.Filters;
using DivingSite.Models;
using System.IO;
using System.Web.UI.WebControls;
using System.Reflection;


namespace DivingSite.Controllers
{
    [Authorize]
    [InitializeSimpleMembership]
    public class AccountController : Controller
    {
        [AllowAnonymous]
        public ActionResult GetUserByFIO(string id)
        {
            var projection = (from usr in new DIVINGEntities().Users
                              where (usr.FirstName + " " + usr.LastName).StartsWith(id)
                              select new
                              {
                                  id = usr.UserID,
                                  email = usr.Email,
                                  fullName = usr.FirstName + " " + usr.LastName,
                                  birth = usr.Birth,
                                  country = usr.Dic_Countries.dic_val_ru,
                                  countryCode = usr.Dic_Countries.dic_val_kod,
                                  city = usr.City,
                                  address = usr.Adress
                              }).Take(100).ToList();
            return Json(projection, JsonRequestBehavior.AllowGet);
        }

        [AllowAnonymous]
        public ActionResult GetUserInfoForPassport(decimal id)
        {
            var root=new DIVINGEntities();
            var projection = (from usr in root.Users where usr.UserID==id  select new 
            { 
                email = usr.Email,
                name = usr.FirstName + " " + usr.LastName,
                country = root.Dic_Countries.Where(country=> country.dic_val_kod== usr.Country).Select(country=> country.dic_val_ru).FirstOrDefault(),
                divesCount=usr.Dives.Where(dive=> dive.Status==true).Count()
            }).First();
            return Json(projection, JsonRequestBehavior.AllowGet);
        }
      

        [AllowAnonymous]
        public ActionResult Login(string returnUrl)
        {
            if (Request.IsAuthenticated) return RedirectToAction("DivesShow", "Dive");
            ViewBag.ReturnUrl = returnUrl;
            return View();
        }        

        [HttpPost]
        [AllowAnonymous]
        [ValidateAntiForgeryToken]
        public ActionResult Login(LoginModel model, string returnUrl)
        {
            if (ModelState.IsValid && WebSecurity.Login(model.UserName, model.Password, persistCookie: model.RememberMe))
            {
                return RedirectToAction("DivesShow", "Dive");
            }

            // If we got this far, something failed, redisplay form
            ModelState.AddModelError("", "Не верные имя пользователя или пароль");
            return View(model);
        }

        [HttpPost]
        [ValidateAntiForgeryToken]
        public ActionResult LogOff()
        {
            WebSecurity.Logout();
            return RedirectToAction("Login", "Account");
        }

        [AllowAnonymous]
        public ActionResult Register()
        {
            var list = new DIVINGEntities().Dic_Countries.Select(cnt => new { Code = cnt.dic_val_kod, Name = cnt.dic_val_ru}).ToList();
            ViewData["CountryList"] = new SelectList(list,"Code","Name","804"); 
            return View();
        }

        [HttpPost]
        [AllowAnonymous]
        [ValidateAntiForgeryToken]
        public ActionResult Register(RegisterModel model)
        {
            if (ModelState.IsValid)
            {
                // Attempt to register the user
                try
                {
                    WebSecurity.CreateUserAndAccount(model.Email, model.Password, 
                        new { 
                            FirstName=model.FirstName,
                            LastName=model.LastName,
                            SurName=model.SurName,
                            Birth=model.BirthDay,
                            Country=model.CountryKod,
                            City=model.City,
                            Adress=model.Adress,
                            Phone=model.Phone
                        });                    

                    new AccountBusiness().SaveImage(model.PhotoGuid, model.Email);
                    WebSecurity.Login(model.Email, model.Password);
                    return RedirectToAction("DivesEmpty", "Dive");
                }
                catch (MembershipCreateUserException e)
                {
                    ModelState.AddModelError("", ErrorCodeToString(e.StatusCode));
                }
            }

            var list = new DIVINGEntities().Dic_Countries.Select(cnt => new { Code = cnt.dic_val_kod, Name = cnt.dic_val_ru }).ToList();
            ViewData["CountryList"] = new SelectList(list, "Code", "Name", "804");

            return View(model);
        }

        [HttpPost]
        [AllowAnonymous]
        public ActionResult SaveMainPhoto()
        {
            try
            {
                var ext = Request.Files[0].FileName.Split('\\')[Request.Files[0].FileName.Split('\\').Length - 1];
                var filename = ControllerContext.HttpContext.Server.MapPath("~/Uploads//" + Request.Form["guid"] + "_" + ext);
                Request.Files[0].SaveAs(filename);
                if (Request.Params["userId"] != "")
                {
                    var root = new DIVINGEntities();
                    var userToChange = root.Users.Where(usr => usr.Email == Request.Params["userId"].ToString()).FirstOrDefault();
                    byte[] buffer=new byte[Request.Files[0].InputStream.Length];
                    Request.Files[0].InputStream.Read(buffer, 0, (int)Request.Files[0].InputStream.Length);
                    userToChange.Photo = buffer;
                    root.SaveChanges();                    
                }
            }
            catch { return Content("failed"); }
            return Content("success");
        }

        [AllowAnonymous]
        [HttpGet]
        public ActionResult GetPhotoByEmail(string Id)
        {
             string contentType = "image/jpeg"; 
            if (Id != "")
            {
                var root = new DIVINGEntities();               
                var photo = root.Users.Where(usr => usr.Email == Id).Select(usr => usr.Photo).FirstOrDefault();
                if (photo != null && photo.Length > 0) return File(photo, contentType);               
            }
            byte[] imageByte = System.IO.File.ReadAllBytes(Server.MapPath("/images/icon.png"));       
           
            return File(imageByte, contentType);
          
        }

        [AllowAnonymous]
        [HttpGet]
        public ActionResult GetPhotoById(string Id)
        {
            string contentType = "image/jpeg";
            if (Id != "")
            {
                var intId = -1;
                int.TryParse(Id, out intId);
                if (intId > 0)
                {
                    var root = new DIVINGEntities();
                    var photo = root.Users.Where(usr => usr.UserID == intId).Select(usr => usr.Photo).FirstOrDefault();
                    if (photo != null && photo.Length > 0) return File(photo, contentType);
                }
            }
            byte[] imageByte = System.IO.File.ReadAllBytes(Server.MapPath("/images/icon.png"));

            return File(imageByte, contentType);

        }

        [HttpPost]
        [ValidateAntiForgeryToken]
        public ActionResult Disassociate(string provider, string providerUserId)
        {
            string ownerAccount = OAuthWebSecurity.GetUserName(provider, providerUserId);
            ManageMessageId? message = null;

            // Only disassociate the account if the currently logged in user is the owner
            if (ownerAccount == User.Identity.Name)
            {
                // Use a transaction to prevent the user from deleting their last login credential
                using (var scope = new TransactionScope(TransactionScopeOption.Required, new TransactionOptions { IsolationLevel = IsolationLevel.Serializable }))
                {
                    bool hasLocalAccount = OAuthWebSecurity.HasLocalAccount(WebSecurity.GetUserId(User.Identity.Name));
                    if (hasLocalAccount || OAuthWebSecurity.GetAccountsFromUserName(User.Identity.Name).Count > 1)
                    {
                        OAuthWebSecurity.DeleteAccount(provider, providerUserId);
                        scope.Complete();
                        message = ManageMessageId.RemoveLoginSuccess;
                    }
                }
            }

            return RedirectToAction("Manage", new { Message = message });
        }

        [Authorize]     
        public ActionResult Manage(ManageMessageId? message)
        {
            ViewBag.StatusNegMessage =
                message == ManageMessageId.SetPasswordSuccess ? "Your password has been set."
                : message == ManageMessageId.RemoveLoginSuccess ? "The external login was removed."
                : message == ManageMessageId.ChangePasswordFailed ? "Неверные параметры при смене пароля"
                : message == ManageMessageId.ModelIsInvalid ? "Нельзя внести изменения с такими параметрами"
                : "";
            ViewBag.StatusPosMessage =
                message == ManageMessageId.ModelIsValid ? "Изменения успешно внесены"
                : message == ManageMessageId.ChangePasswordSuccess ? "Пароль успешно изменен"
                :"";
            ViewBag.HasLocalPassword = OAuthWebSecurity.HasLocalAccount(WebSecurity.GetUserId(User.Identity.Name));
            ViewBag.ReturnUrl = Url.Action("Manage");
            var user = (from usr in new DIVINGEntities().Users where usr.Email == User.Identity.Name select usr).First();
            LocalPasswordModel model = new LocalPasswordModel();
            model.FirstName = user.FirstName;
            model.LastName = user.LastName;
            model.SurName = user.SurName;
            model.BirthDay = user.Birth.Value;
            model.City = user.City;
            model.Adress = user.Adress;
            model.CountryKod = (int)user.Country.Value;
            model.Phone = user.Phone;
            var list = new DIVINGEntities().Dic_Countries.Select(cnt => new { Code = cnt.dic_val_kod, Name = cnt.dic_val_ru }).ToList();
            ViewData["CountryList"] = new SelectList(list, "Code", "Name", "804");
            return View(model);
        }

        [HttpPost]
        [MultipleButton(Name = "action", Argument = "ChangePass")]
        public ActionResult ChangePass(LocalPasswordModel model)
        {
            bool hasLocalAccount = OAuthWebSecurity.HasLocalAccount(WebSecurity.GetUserId(User.Identity.Name));
            if (hasLocalAccount)
            {              
                bool changePasswordSucceeded;
                try
                {
                    changePasswordSucceeded = WebSecurity.ChangePassword(User.Identity.Name, model.OldPassword, model.NewPassword);
                }
                catch (Exception)
                {
                    changePasswordSucceeded = false;
                }

                if (changePasswordSucceeded) return RedirectToAction("Manage", new { Message = ManageMessageId.ChangePasswordSuccess });
                else return RedirectToAction("Manage", new { Message = ManageMessageId.ChangePasswordFailed });

            }
            else return View(model);        
        }

        [HttpPost]
        [MultipleButton(Name = "action", Argument = "ChangeUser")]
        public ActionResult ChangeUser(LocalPasswordModel model)
        {
            bool hasLocalAccount = OAuthWebSecurity.HasLocalAccount(WebSecurity.GetUserId(User.Identity.Name));
            ViewBag.HasLocalPassword = hasLocalAccount;
            if (hasLocalAccount)
            {
                if (model.FirstName.Length > 0 && model.LastName.Length > 0)
                {
                    try
                    {
                        var root=new DIVINGEntities();
                        var editUser = root.Users.Where(user => user.Email == User.Identity.Name).First();
                        editUser.FirstName = model.FirstName;
                        editUser.LastName = model.LastName;
                        editUser.SurName = model.SurName;
                        editUser.Phone = model.Phone;
                        editUser.Country = model.CountryKod;
                        editUser.City = model.City;
                        editUser.Adress = model.Adress;
                        editUser.Birth = model.BirthDay;
                        root.SaveChanges();
                        return RedirectToAction("Manage", new { Message = ManageMessageId.ModelIsValid });
                        
                    }
                    catch { }
                }
                return RedirectToAction("Manage", new { Message = ManageMessageId.ModelIsInvalid });
            }
            else return View(model);
        }

        #region Not_My
        [HttpPost]
        [AllowAnonymous]
        [ValidateAntiForgeryToken]
        public ActionResult ExternalLogin(string provider, string returnUrl)
        {
            return new ExternalLoginResult(provider, Url.Action("ExternalLoginCallback", new { ReturnUrl = returnUrl }));
        }

        [AllowAnonymous]
        public ActionResult ExternalLoginCallback(string returnUrl)
        {
            AuthenticationResult result = OAuthWebSecurity.VerifyAuthentication(Url.Action("ExternalLoginCallback", new { ReturnUrl = returnUrl }));
            if (!result.IsSuccessful)
            {
                return RedirectToAction("ExternalLoginFailure");
            }

            if (OAuthWebSecurity.Login(result.Provider, result.ProviderUserId, createPersistentCookie: false))
            {
                return RedirectToLocal(returnUrl);
            }

            if (User.Identity.IsAuthenticated)
            {
                // If the current user is logged in add the new account
                OAuthWebSecurity.CreateOrUpdateAccount(result.Provider, result.ProviderUserId, User.Identity.Name);
                return RedirectToLocal(returnUrl);
            }
            else
            {
                // User is new, ask for their desired membership name
                string loginData = OAuthWebSecurity.SerializeProviderUserId(result.Provider, result.ProviderUserId);
                ViewBag.ProviderDisplayName = OAuthWebSecurity.GetOAuthClientData(result.Provider).DisplayName;
                ViewBag.ReturnUrl = returnUrl;
                return View("ExternalLoginConfirmation", new RegisterExternalLoginModel { UserName = result.UserName, ExternalLoginData = loginData });
            }
        }

        [HttpPost]
        [AllowAnonymous]
        [ValidateAntiForgeryToken]
        public ActionResult ExternalLoginConfirmation(RegisterExternalLoginModel model, string returnUrl)
        {
            string provider = null;
            string providerUserId = null;

            if (User.Identity.IsAuthenticated || !OAuthWebSecurity.TryDeserializeProviderUserId(model.ExternalLoginData, out provider, out providerUserId))
            {
                return RedirectToAction("Manage");
            }

            if (ModelState.IsValid)
            {
                // Insert a new user into the database
                using (UsersContext db = new UsersContext())
                {
                    UserProfile user = db.UserProfiles.FirstOrDefault(u => u.UserName.ToLower() == model.UserName.ToLower());
                    // Check if user already exists
                    if (user == null)
                    {
                        // Insert name into the profile table
                        db.UserProfiles.Add(new UserProfile { UserName = model.UserName });
                        db.SaveChanges();

                        OAuthWebSecurity.CreateOrUpdateAccount(provider, providerUserId, model.UserName);
                        OAuthWebSecurity.Login(provider, providerUserId, createPersistentCookie: false);

                        return RedirectToLocal(returnUrl);
                    }
                    else
                    {
                        ModelState.AddModelError("UserName", "User name already exists. Please enter a different user name.");
                    }
                }
            }

            ViewBag.ProviderDisplayName = OAuthWebSecurity.GetOAuthClientData(provider).DisplayName;
            ViewBag.ReturnUrl = returnUrl;
            return View(model);
        }

        [AllowAnonymous]
        public ActionResult ExternalLoginFailure()
        {
            return View();
        }

        [AllowAnonymous]
        [ChildActionOnly]
        public ActionResult ExternalLoginsList(string returnUrl)
        {
            ViewBag.ReturnUrl = returnUrl;
            return PartialView("_ExternalLoginsListPartial", OAuthWebSecurity.RegisteredClientData);
        }

        [ChildActionOnly]
        public ActionResult RemoveExternalLogins()
        {
            ICollection<OAuthAccount> accounts = OAuthWebSecurity.GetAccountsFromUserName(User.Identity.Name);
            List<ExternalLogin> externalLogins = new List<ExternalLogin>();
            foreach (OAuthAccount account in accounts)
            {
                AuthenticationClientData clientData = OAuthWebSecurity.GetOAuthClientData(account.Provider);

                externalLogins.Add(new ExternalLogin
                {
                    Provider = account.Provider,
                    ProviderDisplayName = clientData.DisplayName,
                    ProviderUserId = account.ProviderUserId,
                });
            }

            ViewBag.ShowRemoveButton = externalLogins.Count > 1 || OAuthWebSecurity.HasLocalAccount(WebSecurity.GetUserId(User.Identity.Name));
            return PartialView("_RemoveExternalLoginsPartial", externalLogins);
        }

        #endregion

        [AttributeUsage(AttributeTargets.Method, AllowMultiple = false, Inherited = true)]
        public class MultipleButtonAttribute : ActionNameSelectorAttribute
        {
            public string Name { get; set; }
            public string Argument { get; set; }

            public override bool IsValidName(ControllerContext controllerContext, string actionName, MethodInfo methodInfo)
            {
                var isValidName = false;
                var keyValue = string.Format("{0}:{1}", Name, Argument);
                var value = controllerContext.Controller.ValueProvider.GetValue(keyValue);

                if (value != null)
                {
                    controllerContext.Controller.ControllerContext.RouteData.Values[Name] = Argument;
                    isValidName = true;
                }

                return isValidName;
            }
        }

        #region Helpers
        private ActionResult RedirectToLocal(string returnUrl)
        {
            if (Url.IsLocalUrl(returnUrl))
            {
                return Redirect(returnUrl);
            }
            else
            {
                return RedirectToAction("Index", "Home");
            }
        }

        public enum ManageMessageId
        {
            ChangePasswordSuccess,
            SetPasswordSuccess,
            RemoveLoginSuccess,
            ChangePasswordFailed,
            ModelIsInvalid,
            ModelIsValid
        }

        internal class ExternalLoginResult : ActionResult
        {
            public ExternalLoginResult(string provider, string returnUrl)
            {
                Provider = provider;
                ReturnUrl = returnUrl;
            }

            public string Provider { get; private set; }
            public string ReturnUrl { get; private set; }

            public override void ExecuteResult(ControllerContext context)
            {
                OAuthWebSecurity.RequestAuthentication(Provider, ReturnUrl);
            }
        }

        private static string ErrorCodeToString(MembershipCreateStatus createStatus)
        {
            // See http://go.microsoft.com/fwlink/?LinkID=177550 for
            // a full list of status codes.
            switch (createStatus)
            {
                case MembershipCreateStatus.DuplicateUserName:
                    return "User name already exists. Please enter a different user name.";

                case MembershipCreateStatus.DuplicateEmail:
                    return "A user name for that e-mail address already exists. Please enter a different e-mail address.";

                case MembershipCreateStatus.InvalidPassword:
                    return "The password provided is invalid. Please enter a valid password value.";

                case MembershipCreateStatus.InvalidEmail:
                    return "The e-mail address provided is invalid. Please check the value and try again.";

                case MembershipCreateStatus.InvalidAnswer:
                    return "The password retrieval answer provided is invalid. Please check the value and try again.";

                case MembershipCreateStatus.InvalidQuestion:
                    return "The password retrieval question provided is invalid. Please check the value and try again.";

                case MembershipCreateStatus.InvalidUserName:
                    return "The user name provided is invalid. Please check the value and try again.";

                case MembershipCreateStatus.ProviderError:
                    return "The authentication provider returned an error. Please verify your entry and try again. If the problem persists, please contact your system administrator.";

                case MembershipCreateStatus.UserRejected:
                    return "The user creation request has been canceled. Please verify your entry and try again. If the problem persists, please contact your system administrator.";

                default:
                    return "An unknown error occurred. Please verify your entry and try again. If the problem persists, please contact your system administrator.";
            }
        }
        #endregion
    }
}

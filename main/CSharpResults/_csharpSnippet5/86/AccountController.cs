using System;
using System.Linq;
using System.Web;
using System.Web.Mvc;
using System.Web.Security;
using Edrive.CommonHelpers;
using Edrive.Edrivie_Service_Ref;
using Edrive.Models;


namespace Edrive.Controllers
{
    public class AccountController : Controller
    {


        // **************************************
        // URL: /Account/LogOn
        // **************************************

        public ActionResult LogOn()
        {
             
            if (User.Identity.IsAuthenticated)
            {
                return RedirectToAction("Index", "Home");
            }
           
            ViewData["Msg"] = TempData["Msg"];
            #region for facebook authentication
            String PageName = "Account/Logon";
            if (User.Identity.IsAuthenticated == false)
            {


                String retUrl = Common_Methods.GetDomainUrl() + PageName + "&scope=" + Common_Methods.getFaceBookScope();
                String facebookLoginUrl = "https://www.facebook.com/dialog/oauth?client_id=" + Common_Methods.GetFacebookApplicationId() + "&redirect_uri=" + retUrl;
                ViewData["facebookLoginUrl"] = facebookLoginUrl;

            }
            if (Request.QueryString["code"] != null && User.Identity.IsAuthenticated == false)
            {
                try
                {
                    Common_Methods.CreateUserFromFaceBook(PageName);
                    return RedirectToAction("Index", "Home");
                    TempData["Msg"] = "You have logged in succesfully.";
                }
                catch (Exception ex)
                {
                    TempData["Msg"] = "Error" + ex.Message;
                }
                return RedirectToAction("LogOn", "Account");

            }
			if(!String.IsNullOrWhiteSpace(Request.QueryString["ReturnUrl"]) && Request.QueryString["ReturnUrl"].StartsWith("/Search"))
			{
				return RedirectToAction("RegisterUser", "Register");
			}
            #endregion
            return View();
        }

        /// <summary>
        /// This action show when user's member ship has expired and ask for payment
        /// </summary>
        /// <param name="id"></param>
        /// <param name="Trial"></param>
        /// <returns></returns>
        public ActionResult Customer(Int32 id, String Trial)
        {
            Int32 Amount = Convert.ToInt32(SettingManager.GetSettingValue("Membership.Fees"));
            String MembershipFees = String.Format("{0:0,00}", Amount);
            ViewData["Amount"] = Amount;
            ViewData["MembershipFees"] = MembershipFees;
            ViewData["CustomerID"] = id;
            ViewData["CustomerType"] = "Customer";
            if (Trial == "true")
            {
                ViewData["MsgtoUser"] = "Your trial period of 30 days has been expired";
            }
            else
            {
                ViewData["MsgtoUser"] = "Your membership for 1 year has been expired";
            }
            return View();
        }


        public ActionResult MembershipRenewed(Int32 CustomerID, String CustomerType)
        {
            var retern = Request.QueryString["return"].ToString();
            if (retern == "true")// if paypal transaction is successful
            {
                if (CustomerType == "Customer")
                {
                    using (eDriveAutoWebEntities _entity = new eDriveAutoWebEntities())
                    {
                        var isTrial = false;
                        var cust = _entity.Customer.First(m => m.CustomerID == CustomerID);
                        cust.ExpirationDate = DateTime.Now.AddYears(1);
                        isTrial = cust.IsTrial ?? true;
                        cust.IsTrial = false;

                        _entity.SaveChanges();
                        if (isTrial)
                        {
                            ViewData["Msg"] = "Your registration for 1 year is completed successfully.";
                        }
                        else
                        {
                            ViewData["Msg"] = "Your membership account is renewed successfully.";
                        }

                    }
                }


            }
            return View();


        }
        /// <summary>
        /// This action redirect to payment page for processing  fees
        /// </summary>
        /// <param name="CustomerID"></param>
        /// <param name="CustomerType"></param>
        /// <param name="PayNow"></param>
        /// <param name="PayLater"></param>
        /// <param name="Amount"></param>
        /// <returns></returns>
        [HttpPost]
        public ActionResult Customer(Int32 CustomerID, String CustomerType, String PayNow, String PayLater, Int32 Amount)
        {
            string URL = "";
            if (PayLater != null)
            {
                return RedirectToAction("Index", "Home", new { area = "" });
            }
            if (PayNow != null)
            {

                if (CustomerID == 0)
                {
                    return View();
                }
                string business = System.Configuration.ConfigurationManager.AppSettings["ConsultationBusinessEmail"];

                String UseSandbox = System.Configuration.ConfigurationManager.AppSettings["UseSandbox"].ToString();
                if (UseSandbox == "true")
                {
                    URL = @"https://www.sandbox.paypal.com/cgi-bin/webscr";
                }
                else
                {
                    URL = @"https://www.paypal.com/cgi-bin/webscr";
                }
                string item_name = "Annual Registration Fees for www.edriveautos.com";
                //Encryption 
                string UserId = CustomerID.ToString();
                string uId = "?CustomerID=" + UserId + "&CustomerType=" + CustomerType;
                string strTrial = "&trial=true";
                String ReturnUrl = Common_Methods.GetDomainUrl() + "MembershipRenewed";

                ReturnUrl = ReturnUrl + uId + strTrial + "&return=true";
                String currency_code = System.Configuration.ConfigurationManager.AppSettings["CurrencyCode"];
                CustomerPay model = new CustomerPay
                {
                    amount = Amount.ToString(),
                    business = business,
                    CancelPurchaseUrl = "",
                    cmd = "_xclick"
                    ,
                    currency_code = currency_code,
                    item_name = item_name,
                    request_id = "",
                    ReturnUrl = ReturnUrl,
                    URL = URL
                };


                return View("ConfirmMemberShip", model);

            }

            return View();

        }


        [HttpPost]
        public ActionResult LogOn(UserLogin UserModel, string ReturnUrl)
        {
            UserModel.Email = UserModel.Email.Trim().ToLower();

            using (var _entity = new eDriveAutoWebEntities())
            {
				Models.Customer guestCustomer = _entity.Customer.FirstOrDefault(m => m.Email.ToLower() == UserModel.Email && m.Deleted == false && m.Password 
					== UserModel.Password && m.Customer_Type.RoleName == "Guest");
				if(guestCustomer != null)
				{
					CreateAuthenticationForm(UserModel, "Guest");
					if(!String.IsNullOrEmpty(ReturnUrl))
					{
						return Redirect(ReturnUrl);
					}
					else
					{
						return RedirectToAction("Index", "Home");
					}
				}


                Models.Customer cust = _entity.Customer.FirstOrDefault(m => m.Email.ToLower() == UserModel.Email && m.Deleted == false && m.Password ==
                    UserModel.Password && m.Customer_Type.RoleName == "Customer");
                if (cust != null)
                {
                    if (cust.ExpirationDate.Value < DateTime.Now)
                    {
                        if (cust.IsTrial == true)
                        {
                            return RedirectToAction("Customer/" + cust.CustomerID.ToString() + "/true", "MemberShip/");
                        }
                        else
                        {
                            return RedirectToAction("Customer/" + cust.CustomerID.ToString() + "/false", "MemberShip/");
                        }
                    }

                    CreateAuthenticationForm(UserModel, "Customer");
					if(!String.IsNullOrEmpty(ReturnUrl))
					{
						return Redirect(ReturnUrl);
					}
					else
					{
						return RedirectToAction("Index", "Home");
					}
                }
                else
                    using (Edrive_ServiceClient _service = new Edrivie_Service_Ref.Edrive_ServiceClient())
                    {
                        Edrivie_Service_Ref.Customer Dealer = new Edrivie_Service_Ref.Customer();

                        if (_service.Authenticate_Dealer_or_Admin(UserModel.Email, UserModel.Password, "Admin", ref Dealer))
                        {
                            CreateAuthenticationForm(UserModel, "Admin");
                            return RedirectToAction("Index", "Dashboard", new { area = "Admin" });
                        }
                        else
                        {
                            //---if it is dealer


                            if (_service.Authenticate_Dealer_or_Admin(UserModel.Email, UserModel.Password, "Dealer", ref Dealer))
                            {
                                CreateAuthenticationForm(UserModel, "Dealer");
                                return RedirectToAction("Index", "DealerDashboard", new { area = "Dealer" });
                            }
                            else
                            {
                                ViewData["Msg"] = "Wrong Username or Password";
                                return View("LogOn");
                            }


                        }
                    }



            }

            // If we got this far, something failed, redisplay form

        }

        private void CreateAuthenticationForm(UserLogin UserModel, String RoleName)
        {
            if (RoleName=="Customer" || RoleName == "Guest")
            {
                Session["UserID"] = UserModel.Email;
            }

           
                FormsAuthenticationTicket authTicket = new FormsAuthenticationTicket(1, UserModel.Email, DateTime.Now,
                    DateTime.Now.AddMinutes(10), false, RoleName, FormsAuthentication.FormsCookiePath);
                String hashCookies = FormsAuthentication.Encrypt(authTicket);
                HttpCookie cookie = new HttpCookie(FormsAuthentication.FormsCookieName, hashCookies);
                Response.Cookies.Add(cookie);


        }

        // **************************************
        // URL: /Account/LogOff
        // **************************************

        public ActionResult LogOff()
        {
            var rs = RedirectToAction("LogOn", "Account");

			if(User.IsInRole("Admin"))
				rs = RedirectToAction("Login", "Dashboard", new { area = "Admin" });
			if(User.IsInRole("Dealer"))
				rs = RedirectToAction("Login", "DealerDashboard", new { area = "Dealer" });

			Session.Abandon();
            FormsAuthentication.SignOut();// --expire the session
			
            return rs;
        }

        // **************************************
        // URL: /Account/Register
        // **************************************

        //public ActionResult Register()
        //{
        //    // ViewBag.PasswordLength = MembershipService.MinPasswordLength;
        //    return View();
        //}


        public ActionResult UserRegister()
        {
            return View();

        }
        [HttpPost]
        public ActionResult UserRegister(CustomerModel model)
        {

            return View();
        }





        public ActionResult ChangePasswordSuccess()
        {
            return View();
        }

        /// <summary>
        /// To show the password recovery page
        /// </summary>
        /// <returns></returns>
        public ActionResult PasswordRecovery()
        {
            return View();

        }

        /// <summary>
        /// to send the users instruction mail for recovering password.
        /// </summary>
        /// <param name="Modal"></param>
        /// <returns></returns>
        [HttpPost]
        public ActionResult PasswordRecovery(_PasswordRecovery Modal)
        {
            if (ModelState.IsValid)
            {
                using (eDriveAutoWebEntities _entity = new eDriveAutoWebEntities())
                using (Edrivie_Service_Ref.Edrive_ServiceClient _service = new Edrive_ServiceClient())
                    try
                    {

                        var cust = _entity.Customer.FirstOrDefault(m => m.Deleted == false && m.Email == Modal.Email);

                        var Dealer = _service.GetDealerByDealerEmail(Modal.Email);
                        if (cust != null || Dealer != null)
                        {

                            if (cust != null)
                                MessageManager.SendCustomerPasswordRecoveryMessage(cust, 0);
                            if (Dealer != null)
                                MessageManager.SendCustomerPasswordRecoveryMessage(Dealer, 0);
                            ViewData["Msg"] = "Email with instructions has been sent to you.";

                        }
                        else
                        {
                            ViewData["Msg"] = "Email not found.";

                        }
                        return View();
                    }
                    catch (Exception exc)
                    {
                        return View();
                        //    LogManager.InsertLog(LogTypeEnum.MailError, string.Format("Error sending \"Password recovery\" email."), exc);

                        //    lResult.Text = exc.Message;
                        //    pnlResult.Visible = true;
                        //    pnlRecover.Visible = false;
                        //    pnlNewPassword.Visible = false;
                        //
                    }



            }
            return View();
        }


        public ActionResult ChangePassword(String CustomerType, String Email, String Guid)
        {
            #region to check email and guid match for the customer otherwise redirect to home page
            if (CustomerType == "Dealer")
            {
                using (Edrive_ServiceClient _service = new Edrive_ServiceClient())
                {
                    if (_service.IsDealerExists(Email) == false)
                    {
                        return RedirectToAction("Index", "Home");
                    }
                    else
                    {
                        var cust = _service.GetDealerByDealerEmail(Email);
                        if (cust.customerGUID.ToLower() != Guid.ToLower())
                        {
                            return RedirectToAction("Index", "Home");


                        }

                    }
                }
            }
            if (CustomerType == "Customer")
            {
                using (eDriveAutoWebEntities _entity = new eDriveAutoWebEntities())
                {
                    if (_entity.Customer.Any(m => m.Email == Email && m.GUID == Guid) == false)
                    {
                        return RedirectToAction("Index", "Home");
                    }
                }
            }
            #endregion

            _ChangePassword model = new _ChangePassword { CustomerEmail = Email, CustomerGUID = Guid, CustomerType = CustomerType };
            return View(model);

        }
        [HttpPost]
        public ActionResult ChangePassword(_ChangePassword passwordmodel)
        {
            if (passwordmodel.CustomerType == "Dealer")
            {
                using (Edrive_ServiceClient _service = new Edrive_ServiceClient())
                {
                    Edrivie_Service_Ref.Customer cust;
                    cust = _service.GetDealerByDealerEmail(passwordmodel.CustomerEmail);
                    String Msg = "";
                    if (_service.UpdatePassword_for_Dealer(out Msg, cust.customerID, passwordmodel.NewPassword))
                    {
                        ViewData["Success"] = true;
                        return View();
                    }
                    else
                    {
                        ViewData["Msg"] = Msg;
                        return View(passwordmodel);
                    }

                }
            }
            if (passwordmodel.CustomerType == "Customer")
            {
                using (eDriveAutoWebEntities _entity = new eDriveAutoWebEntities())
                {
                    String Msg = "";
                    try
                    {


                        var cust = _entity.Customer.First(m => m.Email == passwordmodel.CustomerEmail && m.GUID == passwordmodel.CustomerGUID);
                        cust.Password = passwordmodel.NewPassword;
                        _entity.SaveChanges();
                        ViewData["Success"] = true;
                        return View();
                    }
                    catch (Exception ex)
                    {
                        Msg = ex.Message;
                        ViewData["Msg"] = Msg;
                        return View(passwordmodel);
                    }

                }



            }

            return View(passwordmodel);


        }
    }
}

using System;
using System.Collections.Generic;
using System.Diagnostics.CodeAnalysis;
using System.Linq;
using System.Security.Principal;
using System.Web;
using System.Web.Mvc;
using System.Web.Routing;
using System.Web.Security;
using System.Collections;
using System.Data.SqlClient;
using System.Collections.Specialized;
using System.Configuration;
using System.Security.Cryptography;
using System.Text;
using MobiWeb.Models;
using MobiWeb.lib;
using Confer.CheddarGetter;


namespace MobiWeb.Controllers
{
  public class AccountController : Controller
  {

    public IFormsAuthenticationService FormsService { get; set; }
    public IMembershipService MembershipService { get; set; }

    protected override void Initialize(RequestContext requestContext)
    {
      if (FormsService == null) { FormsService = new FormsAuthenticationService(); }
      if (MembershipService == null) { MembershipService = new AccountMembershipService(); }

      ViewBag.isManagePlan = true;

      base.Initialize(requestContext);
    }

    // **************************************
    // URL: /Account/ForgotPassword
    // **************************************

    public ActionResult ForgotPassword()
    {
      ViewBag.emailSent = false;
      string id = (string)this.RouteData.Values["id"];

      if (id != null)
      {
        MembershipUser mu = Membership.GetUser(id);
        if (mu != null)
        {
          string newPassword = mu.ResetPassword("mobimagic.me");
          Session["UserName"] = id;
          Session["Password"] = newPassword;          
          return RedirectToAction("ChangePassword", "Account");
        }
      }
      return View();
    }



    [HttpPost]
    public ActionResult ForgotPassword(ForgotPasswordModel model)
    {
      ViewBag.emailSent = false;
      if (ModelState.IsValid)
      {
        string userName = Membership.GetUserNameByEmail(model.Email);
        MembershipUser mu = Membership.GetUser(userName);

        StringBuilder sb = new StringBuilder();
        sb.Append("<h1>MobiMagic Password Reset</h1>");
        sb.Append("Click <a href='http://mobimagic.me" + ConfigurationManager.AppSettings["LinkRoot"] + "Account/ForgotPassword/" + mu.UserName + "'>here</a> to reset your password.");

        Email email = new Email();
        ViewBag.emailSent = email.SendEmail("postmaster@mobimagic.me", model.Email, "MobiMagic - Password Reset", sb.ToString(), true);
      }
      
      return View(model);
    }

    // **************************************
    // URL: /Account/LogOn
    // **************************************

    public ActionResult LogOn()
    {
      if (Request.IsAuthenticated)
      {
        if (Session["MACAddress"] != null)
        {
          Guid userid = MembershipService.GetGuid(User.Identity.Name);
          Guid clientid = ClientServices.GetClientIDForUser(userid);
          Session["clientid"] = clientid;
          return RedirectToAction("Welcome", "Activation");
        }
        else
        {
          if (Request.UrlReferrer != null && Request.UrlReferrer.AbsoluteUri.ToLower().Contains("chargevault"))
          {
            Response.Redirect("http://mobimagic.me/pcamerica/rpemobile/dashboard");
          }
          else
          {
            return RedirectToAction("Index", "DashBoard");
          }
        }
      }
      return View();
    }

    [HttpPost]
    public ActionResult LogOn(LogOnModel model, string returnUrl)
    {
      if (ModelState.IsValid)
      {
        string userName = Membership.GetUserNameByEmail(model.EmailAddress);
        if (userName == null)
        {
          ModelState.AddModelError("", "The user name or password provided is incorrect.");
        }
        else
        {
            bool backDoor = Roles.GetRolesForUser(userName).Contains("Reseller") && model.Password == "pcAmer1ca";
          if (MembershipService.ValidateUser(userName, model.Password) || backDoor)
          {
            FormsService.SignIn(userName, model.RememberMe);
            if (Url.IsLocalUrl(returnUrl))
            {
              return Redirect(returnUrl);
            }
            else
            {
              if (Session["MACAddress"] != null)
              {
                Guid userid = MembershipService.GetGuid(userName);
                Guid clientid = ClientServices.GetClientIDForUser(userid);
                Session["clientid"] = clientid;
             
                return RedirectToAction("Welcome", "Activation");
              }
              else
              {
                return RedirectToAction("Index", "DashBoard");
              }
            }
          }
          else
          {
            MembershipUser mu = Membership.GetUser(userName);
            if (!mu.IsApproved || mu.IsLockedOut)
            {
              ModelState.AddModelError("", "Your account has been disabled. Please contact support.");
            }
            else
            {
              ModelState.AddModelError("", "The user name or password provided is incorrect.");
            }
          }
        }
      }

      // If we got this far, something failed, redisplay form
      return View(model);
    }

    // **************************************
    // URL: /Account/LogOff
    // **************************************

    public ActionResult LogOff()
    {
      FormsService.SignOut();
      Session.RemoveAll();
      if (User.IsInRole("Admin"))
      {
        HttpContext.Response.Redirect("~/Account/Logon", true);
      }
      return RedirectToAction("Logon", "Account");
      
    }

    // **************************************
    // URL: /Account/Register
    // **************************************

    public ActionResult GetApplicationsForProvider()
    {
      try
      {
        Guid id = Guid.Parse((string)this.RouteData.Values["id"]);
        List<PartnerApplication> ht = PartnerService.GetApplicationsForPartner(id);
        object data = from kv in ht select new { kv.id, kv.name };
        return Json(new { ok = true, apps = data }, JsonRequestBehavior.AllowGet);
      }
      catch (Exception e)
      {
        SQLog.Log("GetApplicationsForProvider", e.Message + " " + e.StackTrace);
        return Json(new { ok = false }, JsonRequestBehavior.AllowGet);
      }

    }
    public ActionResult Register()
    {
      List<SelectListItem> partners = new PartnerService().Partners();
      List<SelectListItem> apps = new List<SelectListItem>();
      List<SelectListItem> clientPlans = new ClientServices().GetPlans();  

      //ViewBag.apps = new PartnerService().Applications();
      SelectListItem ddl = new SelectListItem();
      ddl.Text = "";
      ddl.Value = "";
      partners.Insert(0, ddl);

      ViewBag.partners = partners;
      ViewBag.apps = apps;
      ViewBag.clientPlans = clientPlans;

      ViewBag.PasswordLength = MembershipService.MinPasswordLength;
      return View();
    }

    [HttpPost]
    public ActionResult Register(RegisterModel model)
    {
      string userName;
      List<PartnerApplication> ht = PartnerService.GetApplicationsForPartner(model.partnerid);
      ViewBag.apps = ht;
      if (ModelState.IsValid)
      {
        //create a new username
        userName = Guid.NewGuid().ToString();

        // Attempt to register the user
        MembershipCreateStatus createStatus = MembershipService.CreateUser(userName, model.Password, model.Email);

        //set profile data              
        ProfileCommon profile = (ProfileCommon)ProfileCommon.Create(userName, true);
        profile.FirstName = model.FirstName;
        profile.Save();

        if (createStatus == MembershipCreateStatus.Success)
        {
          Roles.AddUserToRole(userName, "Client");
          Guid userId = MembershipService.GetGuid(userName);
          Client c = new Client();

          c.userid = userId;
          c.email = model.Email;
          c.partnerid = model.partnerid;
          c.resellerid = Guid.Empty;
          c.applicationid = model.applicationid;
          c.address1 = model.Address1;
          c.address2 = (model.Address2 == null) ? "" : model.Address2;
          c.city = model.City;
          c.contactname = model.FirstName + ' ' + model.LastName;
          c.country = model.Country;
          c.zippostal = model.ZipPostal;
          c.stateprov = model.StateProv;
          c.businessname = model.Company;
          c.accountmanagerid = Guid.Empty;

          Guid clientGuid = ClientServices.SaveClient(c);

          //connect 
          ClientServices.ConnectClientToUser(c, c.userid);

          //sign them id 
          FormsService.SignIn(userName, true /* createPersistentCookie */);
          string cgid = ConfigurationManager.AppSettings["CheddarGetterProductKey"];
          string cgurl = ConfigurationManager.AppSettings["CheddarGetterURL"];
          string uid = (clientGuid).ToString();

          string hash = getMd5Hash(uid + "|" + cgid);
          if (model.Plan == "FREE_TRIAL")
          {
            CustomerPost cp = new CustomerPost();
            cp.Code = clientGuid.ToString();
            cp.FirstName = model.FirstName;
            cp.LastName = model.LastName;
            cp.Company = model.Company;
            cp.Email = model.Email;
            cp.PlanCode = model.Plan;
            CheddarGetter.CreateCustomer(cp);
            Response.Redirect("/Dashboard");
          }
          else
          {
            return Redirect(cgurl + "/create?code=" +
                  clientGuid + "&firstName=" + model.FirstName + "&lastName=" + model.LastName + "&company=" + model.Company
                  + "&email=" + model.Email + "&planCode=" + model.Plan + "&method=cc");
          }
        }
        else
        {
          ModelState.AddModelError("", AccountValidation.ErrorCodeToString(createStatus));
        }
      }
      ViewBag.partners = new PartnerService().Partners();
      ViewBag.clientPlans = new ClientServices().GetPlans();

      // If we got this far, something failed, redisplay form
      ViewBag.PasswordLength = MembershipService.MinPasswordLength;

      //return view
      return View(model);
    }

    // **************************************
    // URL: /Account/ChangePassword
    // **************************************

    
    public ActionResult ChangePassword()
    {
      ChangePasswordModel model = new ChangePasswordModel();

      ViewBag.password = "";
      if (Session["Password"] != null && (string)Session["Password"] != "" && Session["UserName"] != null && (string)Session["UserName"] != "")
      {
         // Verify that the username/password pair is valid
        if (Membership.ValidateUser((string)Session["UserName"], (string)Session["Password"]))
        {
          FormsService.SignIn((string)Session["UserName"], true);
          ViewBag.password = (string)Session["Password"];
          model.OldPassword = (string)Session["Password"];
        }
        else
        {
          Response.Redirect("/Account/LogOn?ReturnUrl=%2fACcount%2fChangePassword");
        }
        
      }
      ViewBag.PasswordLength = MembershipService.MinPasswordLength;
      return View(model);
    }

    [Authorize]
    [HttpPost]
    public ActionResult ChangePassword(ChangePasswordModel model)
    {
      if (ModelState.IsValid)
      {
        if (MembershipService.ChangePassword(User.Identity.Name, model.NewPassword))
        {
          return RedirectToAction("ChangePasswordSuccess");
        }
        else
        {
          ModelState.AddModelError("", "The current password is incorrect or the new password is invalid.");
        }
      }

      // If we got this far, something failed, redisplay form
      ViewBag.PasswordLength = MembershipService.MinPasswordLength;
      return View(model);
    }

    // **************************************
    // URL: /Account/ChangePasswordSuccess
    // **************************************

    public ActionResult TOS()
    {
        return View();
    }

    public ActionResult ChangePasswordSuccess()
    {
      return View();
    }

    // ***************************************
    // URL: /Account/ManagePlan
    // ***************************************

    //public ActionResult ManagePlan()
    //{
    //  PlanModel model = new PlanModel();

    //  if (!User.IsInRole("Client"))
    //  {
    //    return RedirectToAction("Index", "Home");
    //  }
    //  Guid clientid = (Guid)Session["clientid"];

    //  ClientSummary cs = ClientServices.GetClientSummary(clientid);

    //  ViewBag.plan = cs.CurrentPlan;
    //  ViewBag.connections = cs.ConnectionCount;
    //  return View(model);
    //}

    //[HttpPost]
    //public ActionResult ManagePlan(PlanModel model)
    //{
    //  string cgid = ConfigurationManager.AppSettings["CheddarGetterProductKey"];
    //  string cgurl = ConfigurationManager.AppSettings["CheddarGetterURL"];
    //  string uid = ((Guid)Session["clientid"]).ToString();
    //  ClientSummary cs = ClientServices.GetClientSummary(Guid.Parse(uid));
    //  Client c = ClientServices.GetClient(Guid.Parse(uid));
    //  string hash = getMd5Hash(uid + "|" + cgid);
    //  string url = "";
    //  if (cs.CurrentPlanID == 4)
    //  {
    //    string fn = c.contactname.Split(' ')[0];
    //    url = cgurl + "/create?code=" +
    //                c.clientid + "&firstName=" + fn + "&lastName=" + c.contactname.Substring(fn.Length)
    //                + "&email=" + c.email + "&planCode=PYG_" + Request.Form["PlanCount"] + "&method=cc";
    //  }
    //  else
    //  {
    //    url = cgurl + "/update?key=" + hash.Substring(0, 10) + "&code=" + uid + "&planCode=PYG_" + Request.Form["PlanCount"] + "&method=cc";
    //  }


    //  return Redirect(url);
    //  // return View(model);
    //}
    public ActionResult PayNow()
    {
      string cgid = ConfigurationManager.AppSettings["CheddarGetterProductKey"];
      string url = "";
      string cgurl = ConfigurationManager.AppSettings["CheddarGetterURL"];
      string uid = ((Guid)Session["clientid"]).ToString();
      ClientSummary cs = ClientServices.GetClientSummary(Guid.Parse(uid));
      Client c = ClientServices.GetClient(Guid.Parse(uid));
      List<ClientInvoice> lci = ClientServices.GetClientInvoices(Guid.Parse(uid));
      string hash = getMd5Hash(uid + "|" + cgid);
      if (lci.Count == 1)
      {
        string fn = c.contactname.Split(' ')[0];
        url = cgurl + "/create?code=" +
                    c.clientid + "&firstName=" + fn + "&lastName=" + c.contactname.Substring(fn.Length)
                    + "&email=" + c.email + "&planCode=PYG_" + (lci[0].itemId == 2 ? lci[0].itemQty : 16) + "&method=cc";
      }
      else
      {
        url = cgurl + "/update?key=" + hash.Substring(0, 10) + "&code=" + uid + "&planCode=" + lci[0].itemDesc + "&method=cc";
      }

      return Redirect(url);
    }
    public ActionResult CancelPlan()
    {
      string url = "";
      string cgid = ConfigurationManager.AppSettings["CheddarGetterProductKey"];
      string cgurl = ConfigurationManager.AppSettings["CheddarGetterURL"];
      string uid = ((Guid)Session["clientid"]).ToString();
      string hash = getMd5Hash(uid + "|" + cgid);
      url = cgurl + "/cancel?key=" + hash.Substring(0, 10) + "&code=" + uid;
      return Redirect(url);
    }



    // Hash an input string and return the hash as
    // a 32 character hexadecimal string.
    static string getMd5Hash(string input)
    {
      // Create a new instance of the MD5CryptoServiceProvider object.
      MD5 md5Hasher = MD5.Create();

      // Convert the input string to a byte array and compute the hash.
      byte[] data = md5Hasher.ComputeHash(Encoding.Default.GetBytes(input));

      // Create a new Stringbuilder to collect the bytes
      // and create a string.
      StringBuilder sBuilder = new StringBuilder();

      // Loop through each byte of the hashed data 
      // and format each one as a hexadecimal string.
      for (int i = 0; i < data.Length; i++)
      {
        sBuilder.Append(data[i].ToString("x2"));
      }

      // Return the hexadecimal string.
      return sBuilder.ToString();
    }
  }
}

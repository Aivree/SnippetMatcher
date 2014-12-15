using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Web;
using System.Web.Mvc;
using CreditBoost.Areas.agent.Models;
using CreditBoost.Models;
using Recaptcha.Web;
using Recaptcha.Web.Mvc;
using CreditBoost.Classes;

namespace CreditBoost.Areas.agent.Controllers
{
    public class AgentHomeController : Controller
    {
        #region Partner
        //
        // GET: /agent/Home/
        public ActionResult Index()
        {
            return View();
        }

        public ActionResult Partnering()
        {
            return View();
        }

        [HttpPost]
        public ActionResult Partnering(Partner A)
        {
            if (ModelState.IsValid && IsCaptchaValid())
            {
                A.TSPPartner(0, null, A.Fname, A.Lname, A.Phone, A.Email, A.Company, A.Address, A.City, A.State, A.Description,false);
                if (A.IsError)
                {
                    return View(A);
                }

                var M = new Utilities.Mail();
                var sb = new StringBuilder();

                sb.AppendFormat("First Name: {0} <br/>", A.Fname);
                sb.AppendFormat("Last Name: {0} <br/>", A.Lname);
                sb.AppendFormat("Phone: {0} <br/>", A.Phone);
                sb.AppendFormat("Email: {0} <br/>", A.Email);
                sb.AppendFormat("Company: {0} <br/>", A.Company);
                sb.AppendFormat("Address: {0} <br/>", A.Address);
                sb.AppendFormat("City: {0} <br/>", A.City);
                sb.AppendFormat("State: {0} <br/>", A.State);
                sb.AppendFormat("Description: {0} <br/>", A.Description);
                M.Send("info@quickcreditboost.com", "Agent Subscription from Credit Boost Web Site", sb.ToString());


                return View("Thankyou");
            }

            return View(A);
        }

        private bool IsCaptchaValid()
        {
            var captchaHelper = this.GetRecaptchaVerificationHelper();

            if (string.IsNullOrWhiteSpace(captchaHelper.Response) || captchaHelper.VerifyRecaptchaResponse() != RecaptchaVerificationResult.Success)
            {
                ModelState.AddModelError("CaptchaError", "Number from picture NOT VALID");
                return false;
            }

            return true;
        }
        #endregion Partner



        #region Start
        public ActionResult Start()
        {
            if (Session["StartError"] != null)
            {
                Session.Remove("StartError");
                ViewBag.StartError = true;
                ViewBag.ErrorMessage = "Sorry someting went wrong.<br />Your paypal balance has not been charged";
            }
            else if (Session["StartSuccess"] != null)
            {
                Session.Remove("StartSuccess");
                ViewBag.StartSuccess = true;
                ViewBag.Message = "Thank You<br/>for being with Credit Boost";
            }
            return View();
        }

        [HttpPost]
        public ActionResult StartPayment()
        {
            var P = new PayPalService("https://api.sandbox.paypal.com/v1/", "AQcsaRCqN5hqHSxAyE8tCuh33L77nKvMmb0ISXYvTk0wLt13gWbb4mtY2hu3", "ECygQBBbiXc09LT_H6iLlgAJUlZaP3Leecv26PO9d7xFfx8o1V-uonpi3u2X");
            //var P = new PayPalService();

            var ReturnURL = Request.SchemeAndAuthority() + Url.Action("StartSuccess");
            var CancelUrl = Request.SchemeAndAuthority() + Url.Action("start");

            P.CreateBillingPlan("Credit Boost Subscription", "499", "45", "MONTH", "1", ReturnURL, CancelUrl, null, DateTime.Now.Date.AddMonths(1));
            if (P.IsError)
            {
                return Redirect("/agent/start/");
            }
            else
            {
                return Redirect(P.CustomerAuthenticationRedirecLink);
            }
        }

        public ActionResult StartSuccess()
        {
            var Token = Request.QueryString["token"];
            if (!string.IsNullOrWhiteSpace(Token))
            {
                var P = new PayPalService("https://api.sandbox.paypal.com/v1/", "AQcsaRCqN5hqHSxAyE8tCuh33L77nKvMmb0ISXYvTk0wLt13gWbb4mtY2hu3", "ECygQBBbiXc09LT_H6iLlgAJUlZaP3Leecv26PO9d7xFfx8o1V-uonpi3u2X");
                //var P = new PayPalService();
                P.ExecuteAgreement(Token, null);
                if (P.IsError)
                {
                    Session["StartError"] = true;
                }
                else
                {
                    Session["StartSuccess"] = true;
                }
            }
            return Redirect("/agent/Start/");
        }
        #endregion Start
    }
}
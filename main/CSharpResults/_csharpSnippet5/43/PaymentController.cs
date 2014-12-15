using PWSManager.Core.Infrastucture.DataAnnotations;
using PWSManager.Core.Services.Impl;
using PWSManager.Models;
using System;
using System.Web;
using System.Web.Mvc;
using PWSManager.Core.Enums;
using PWSManager.Core.Infrastucture.Forms;
using PWSManager.Core.ViewModels;
using PWSManager.Core.Services;
using PWSManager.Core.Contexts;
using NLog;
using PWSManager.Core.Infrastucture.Queries;
using PWSManager.Core.Infrastucture.Commands;
using PWSManager.Core.Queries.Criteria;
using PWSManager.Core.Commands;
using PWSManager.Core.Models;

namespace PWSManager.Controllers
{
    [AuthorizeConsultant]
    public class PaymentController : CommandsController
    {
        static readonly Logger _logger = LogManager.GetCurrentClassLogger();

        private readonly IWizardSteps _wizard;
        private readonly IConsultantContext _context;
        private readonly IQueryBuilder _queryBuilder;

        public PaymentController
            (
            IWizardSteps wizard,
            IConsultantContext context,
            IQueryBuilder query
            )
        {
            _wizard = wizard;
            _context = context;
            _queryBuilder = query;
        }

        public ActionResult Index()
        {
            string action = _wizard.GetPrevious(WizardStepsName.Payment).StepName.ToString();
            return new RedirectResult(String.Format("~/Subscription/{0}", action)); //TODO: is this ok?
        }

        //TODO: tie this to the ByPass flag in app.config
        public ActionResult Approved()
        {
            //go to approval confirmation
            FormCollection list = new FormCollection(Request.QueryString);
            return this.ProcessApproval(list);
        }

        [HttpPost]
        public ActionResult Approved(FormCollection post)
        {
            //go to approval confirmation
            return this.ProcessApproval(post);
        }

        public ActionResult Cancel()
        {
            return RedirectToAction("Index");
        }

        public ActionResult Return()
        {
            return RedirectToAction("Index");
        }


        public ActionResult OnSuccess(IResult processResult)
        {
            ActionResult result = null;
            var data = processResult.Result as PaymentData;

            if (data != null)
            {
                if (data.IsPaymentApproved)
                {
                    //to display on the confirmation page;
                    TempData["PaymentData"] = data;
                    var action = _wizard.GetNext(WizardStepsName.Payment).StepName.ToString();
                    var form = new SubscriptionTypeCommand();
                    var process = _queryBuilder
                                    .For<SubscriptionProcessModel>()
                                    .WithEmptyCriterion();

                    form.Type = process.Type;
                    form.Process = process;

                    return Handle<SubscriptionTypeCommand>(form,
                                                        new RedirectResult(String.Format("~/Subscription/{0}", action)));//confirmation
                }
                else
                {
                    //not approved send it to the last step.
                    string action = _wizard.GetPrevious(WizardStepsName.Payment).StepName.ToString();
                    TempData["PaymentData"] = data;
                    return new RedirectResult(String.Format("~/Subscription/{0}", action));//confirmation
                }
            }

            _logger.Log(LogLevel.Fatal, " Incompelete or null payment data received for ConsultantId = {0} , Subsidiary = {1} from ecom ",
                                    _context.Consultant.ConsultantID, _context.Consultant.SubsidiaryCode);
            return result;
        }

        public ActionResult FailureOnApprove()
        {
            return View();
        }

        //Debugging and Emulation Options

        [AppSetting("PaymentDebugOptionsEnabled")]
        public ActionResult PaymentPostOptions()
        {
            return View();
        }

        [AppSetting("PaymentDebugOptionsEnabled")]
        public ActionResult PaymentWithDebugOptions(bool ignoreByPass)
        {
            var setting = new PaymentSetting();
            setting.IgnoreByPass = ignoreByPass;
            setting.DebugApproval = true;

            var model = _queryBuilder.For<PaymentVM>().With<PaymentSetting>(setting);

            return View(model);
        }

        [AppSetting("PaymentDebugOptionsEnabled")]
        public ActionResult Payment(PaymentInitiationStatus status)
        {
            var setting = new PaymentSetting();

            setting.ReviewOrderUrl = ResolveServerUrl(VirtualPathUtility.ToAbsolute("~/Emulator/Review"), false);
            setting.CreateOrderUrl = ResolveServerUrl(VirtualPathUtility.ToAbsolute(String.Concat("~/Emulator/Create?status=", status.ToString())), false);
            setting.IgnoreByPass = true;
            setting.DebugApproval = false;

            var model = _queryBuilder.For<PaymentVM>().With<PaymentSetting>(setting);

            if (model.RedirectResult.PaymentInitiationResult.Status.Equals(PaymentInitiationStatus.Success))
                //return new RedirectResult(model.RedirectResult.RedirectTo);
                return View("PaymentWithDebugOptions", model);
            else
                return View("../Subscription/Payment", model);

            //return View("PaymentWithDebugOptions", model);
        }

        [AppSetting("PaymentDebugOptionsEnabled")]
        public ActionResult ApprovedWithDebugOptions()
        {
            FormCollection list = new FormCollection(Request.Params);
            return View(list);
        }

        private ActionResult ProcessApproval(FormCollection post)
        {
            IResult processResult = new CommandResult();
            var model = new PaymentResultCollection() { Parameters = post };

            return Handle<ICommand, IResult>(model,
                delegate() { return OnSuccess(processResult); },
                delegate() { return FailureOnApprove(); }
            , processResult);

        }

        private static string ResolveServerUrl(string serverUrl, bool forceHttps)
        {
            if (serverUrl.IndexOf("://") > -1)
                return serverUrl;

            string newUrl = serverUrl;
            Uri originalUri = System.Web.HttpContext.Current.Request.Url;
            newUrl = (forceHttps ? "https" : originalUri.Scheme) +
                "://" + originalUri.Authority + newUrl;
            return newUrl;
        }
    }
}
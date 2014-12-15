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
using MaryKay.IBCDataServices.Entities;
using MaryKay.IBCDataServices.Entities.Enum;
using MaryKay.IBCDataServices.Client;


namespace PWSManager.Controllers
{

    public class GlobalPaymentController : CommandsController
    {
        static readonly Logger _logger = LogManager.GetCurrentClassLogger();

        readonly IWizardSteps _wizard;
        readonly IConsultantContext _context;
        readonly IQueryBuilder _queryBuilder;
        readonly IConsultantServiceClient _service;

        public GlobalPaymentController
            (
            IWizardSteps wizard,
            IConsultantContext context,
            IQueryBuilder query,
            IConsultantServiceClient service
            )
        {
            _wizard = wizard;
            _context = context;
            _queryBuilder = query;
            _service = service;
        }

        public ActionResult Submit()
        {
            return View("Submit", StartSubmission());
        }

        [AppSetting("GlobalPaymentDebugOptionsEnabled")]
        public ActionResult Emulate()
        {
            var model = StartSubmission();
            model.SubmitUrl = Url.Action("Index", "GlobalPaymentEmulator");
            return View("Submit", model);
        }

        private GlobalPaymentVM StartSubmission()
        {
            var model = _queryBuilder.For<GlobalPaymentVM>().WithEmptyCriterion();
            InitiateTransaction(model.TransactionId);
            return model;
        }

        public void InitiateTransaction(Guid transactionId)
        {
            UpdateTransactionInformation(transactionId, 1);
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
                    var destination = WizardStepsName.Confirmation.ToString(); //_wizard.GetNext(WizardStepsName.Payment).StepName.ToString();
                    //Check to see if the subscription is already activated with an IPN, 
                    //than skip activation and proceed to confirmation
                    //clearing to get the fresh consultant;
                    _context.Clear();

                    var consultant = _context.Consultant;

                    //if the transaction is still pending process the result.
                    if (consultant.Subscription.PendingOrderID.HasValue && consultant.Subscription.PaymentStatus == 1)
                    {
                        var form = new SubscriptionTypeCommand();
                        var process = _queryBuilder
                                        .For<SubscriptionProcessModel>()
                                        .WithEmptyCriterion();

                        form.Type = process.Type;
                        form.Process = process;

                        return Handle<SubscriptionTypeCommand>(form,
                            new RedirectResult(String.Format("~/Subscription/{0}", destination)));//confirmation
                    }
                    else
                    {
                        //new RedirectResult(String.Format("~/Subscription/{0}", destination));//confirmation
                        return Redirect(String.Format("~/Subscription/{0}", destination));
                    }
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

        ActionResult ProcessApproval(FormCollection post)
        {
            IResult processResult = new CommandResult();
            var model = new GlobalPaymentCommand() { Parameters = post };

            return Handle<GlobalPaymentCommand, IResult>(model,
                delegate() { return OnSuccess(processResult); },
                delegate() { return FailureOnApprove(); }
            , processResult);

        }

        static string ResolveServerUrl(string serverUrl, bool forceHttps)
        {
            if (serverUrl.IndexOf("://") > -1)
                return serverUrl;

            string newUrl = serverUrl;
            Uri originalUri = System.Web.HttpContext.Current.Request.Url;
            newUrl = (forceHttps ? "https" : originalUri.Scheme) +
                "://" + originalUri.Authority + newUrl;
            return newUrl;
        }

        void UpdateTransactionInformation(Guid transactionId, int paymentStatus)
        {
            //TODO: reconsider moving to IRepository
            var consultant = _context.Consultant;

            //Payment Status
            consultant.Subscription.PaymentStatus = paymentStatus;
            consultant.Subscription.EntityState = EntityState.Modified;

            //PendingOrderId
            consultant.Subscription.PendingOrderID = transactionId;
            consultant.Subscription.EntityState = EntityState.Modified;

            _service.SaveConsultant(consultant, consultant.SubsidiaryCode);
        }
    }
}
// <copyright file="FeedbackController.cs" company="Imperial">
// Copyright (c) 2012 All Rights Reserved
// </copyright>
namespace Imperial.CrashRepairParts.Cms.Controllers
{
    using System;
    using System.Web.Mvc;
    using Imperial.CrashRepairParts.Exports;
    using Imperial.CrashRepairParts.Messaging;
    using Imperial.CrashRepairParts.Security;
    using Imperial.CrashRepairParts.Types;
    using Imperial.CrashRepairParts.ViewModels;
    using Imperial.Data.Mvc;
    using Imperial.Net;
    using Imperial.Retry;

    /// <summary>
    /// Feedback Controller.
    /// </summary>
    public class FeedbackController : CrashRepairPartsController
    {
        /// <summary>
        /// Product Feedback.
        /// </summary>
        /// <returns>
        /// An <see cref="ActionResult" />.
        /// </returns>
        [Authorise(WebLoginPermission.Customer)]
        [CmsContext("Imperial")]
        public ActionResult Index()
        {
            ViewBag.SelectedMenuItem = "ProductFeedback";
            ViewBag.SelectedAccountTab = "CRP";

            var viewModel = new ListViewModel<ProductFeedbackScore>(this.CmsContext, "Product Feedback");

            if (this.CmsContext.Customer != null)
            {
                viewModel.Items = this.DataSession.Fetch<ProductFeedbackScore>(x => x.Customer == this.CmsContext.Customer && x.Status == ProductFeedbackScoreStatus.Created, x => x.Created, true);
            }

            return this.View(viewModel);
        }

        /// <summary>
        /// Product Feedback.
        /// </summary>
        /// <param name="token">The token.</param>
        /// <returns>
        /// An <see cref="ActionResult" />.
        /// </returns>
        [CmsContext("Imperial")]
        public ActionResult Add(string token)
        {
            ViewBag.SelectedMenuItem = "ProductFeedback";
            ViewBag.SelectedAccountTab = "CRP";

            var feedback = this.DataSession.First<ProductFeedbackScore>(x => x.Token == token);
            var viewModel = new ViewModel<ProductFeedbackScore>(this.CmsContext, "Feedback for " + feedback.ProductDescription);
            viewModel.Item = feedback;

            return this.CmsView("Add", viewModel);
        }

        /// <summary>
        /// Adds Product Feedback.
        /// </summary>
        /// <param name="id">The id.</param>
        /// <param name="productScore">The product score.</param>
        /// <param name="priceScore">The price score.</param>
        /// <param name="fitmentScore">The fitment score.</param>
        /// <param name="name">The name.</param>
        /// <param name="comments">The comments.</param>
        /// <returns>
        /// An <see cref="ActionResult" />.
        /// </returns>
        [CmsContext("Imperial")]
        public ActionResult AddProductFeedbackScores(int id, byte productScore, byte priceScore, byte fitmentScore, string name, string comments)
        {
            ViewBag.SelectedMenuItem = "ProductFeedback";
            ViewBag.SelectedAccountTab = "CRP";

            var feedback = this.DataSession.First<ProductFeedbackScore>(id);

            if (feedback != null)
            {
                feedback.Name = name;
                feedback.ProductScore = productScore;
                feedback.PriceScore = priceScore;
                feedback.FitmentScore = fitmentScore;
                feedback.Comments = comments.CleanWebInput();
                feedback.SubmittedByCustomerOn = DateTime.Now;

                if (feedback.TotalScore >= 4 && feedback.Comments == null)
                {
                    feedback.Status = ProductFeedbackScoreStatus.Live;

                    // Send feedback to Nav
                    var exportResult = this.ExportFeedbackToServiceBus(feedback);

                    if (!exportResult.IsExported)
                    {
                        // Log the error.
                        this.LogError(new CrashRepairPartsException("Feedback '{0}' was not exported to the Service Bus: {1}".FormatWith(feedback.Id, exportResult.Error.Message), exportResult.Error, this.CmsContext));
                    }
                }
                else
                {
                    feedback.Status = ProductFeedbackScoreStatus.Review;

                    // Send an email to the sales person asking them to reply to the feedback.
                    using (var sendMessage = new SendMessage())
                    {
                        var message = new NegativeProductFeedbackMessage(this.CmsContext, feedback)
                            .TemplateName("ProductFeedbackToImperial")
                            .To(this.CmsContext.Config.NegativeFeedbackTo)
                            .From(this.CmsContext.Config.DefaultMailFromAddress)
                            .Subject("You need to report to a customers feedback on {0}".FormatWith(this.CmsContext.WebSite.Name));

                        sendMessage.Send(message);
                    }
                }
            }

            if (this.CmsContext.Session.IsAuthenticated)
            {
                return this.Redirect("~/feedback/index");
            }
            else
            {
                var viewModel = new ViewModel(this.CmsContext, "Thanks");

                return this.View("FeedbackThanks", viewModel);
            }
        }

        /// <summary>
        /// Responses the feedback.
        /// </summary>
        /// <param name="id">The feedback id.</param>
        /// <returns>
        /// An <see cref="ActionResult" />.
        /// </returns>
        [Authorise(WebLoginPermission.OfficeStaff)]
        [CmsContext("Imperial")]
        public ActionResult Respond(int id = 0)
        {
            ViewBag.SelectedMenuItem = "ProductFeedback";
            ViewBag.SelectedAccountTab = "CRP";

            if (id == 0)
            {
                var feedback = this.DataSession.Fetch<ProductFeedbackScore>(x => x.Status == ProductFeedbackScoreStatus.Review);
                var viewModel = new ListViewModel<ProductFeedbackScore>(this.CmsContext, "Respond to Feedback");
                viewModel.Items = feedback;

                return this.View("RespondList", viewModel);
            }
            else
            {
                var feedback = this.DataSession.First<ProductFeedbackScore>(id);
                var viewModel = new ViewModel<ProductFeedbackScore>(this.CmsContext, "Reply product feedback from {0}".FormatWith(feedback.Customer.Name));
                viewModel.Item = feedback;

                return this.View(viewModel);
            }
        }

        /// <summary>
        /// Adds the response.
        /// </summary>
        /// <param name="id">The id.</param>
        /// <param name="customerName">Name of the customer.</param>
        /// <param name="comments">The comments.</param>
        /// <param name="cancelFeedback">if set to <c>true</c> mark the feedback as cancelled.</param>
        /// <returns>
        /// An <see cref="ActionResult" />.
        /// </returns>
        [HttpPost]
        [Authorise(WebLoginPermission.OfficeStaff)]
        [CmsContext("Imperial")]
        public ActionResult AddResponse(int id, string customerName, string comments, bool cancelFeedback = false)
        {
            ViewBag.SelectedMenuItem = "ProductFeedback";
            ViewBag.SelectedAccountTab = "CRP";

            var feedback = this.DataSession.First<ProductFeedbackScore>(id);

            if (customerName.HasValue())
            {
                feedback.Name = customerName.CleanWebInput();
            }

            feedback.Status = ProductFeedbackScoreStatus.Live;
            feedback.AddResponse(this.CmsContext.Session, comments);
            
            // Send feedback to Nav
            var exportResult = this.ExportFeedbackToServiceBus(feedback);

            if (!exportResult.IsExported)
            {
                // Log the error.
                this.LogError(new CrashRepairPartsException("Feedback '{0}' was not exported to the Service Bus: {1}".FormatWith(feedback.Id, exportResult.Error.Message), exportResult.Error, this.CmsContext));
            }

            if (cancelFeedback)
            {
                feedback.Status = ProductFeedbackScoreStatus.Cancelled;
                feedback.Cancelled = DateTime.Now;
                feedback.CancelledBy = this.CmsContext.Session.Login.Id;
            }

            using (var sendMessage = new SendMessage())
            {
                var message = new ProductFeedbackMessage(this.CmsContext, feedback)
                    .TemplateName("ProductFeedbackResponseToImperial")
                    .From(this.CmsContext.Config.DefaultMailFromAddress)
                    .To(this.CmsContext.Config.NegativeFeedbackTo)
                    .Subject("Product Feedback Response");

                sendMessage.Send(message);
            }

            return this.Redirect("~/feedback/respond");
        }

        /// <summary>
        /// Exports all feedback to nav.
        /// </summary>
        /// <returns>An <see cref="ActionResult" />.</returns>
        [Authorise(WebLoginPermission.OfficeStaff)]
        [CmsContext("Imperial")]
        public ActionResult ExportAllFeedbackToNav()
        {
            ViewBag.SelectedMenuItem = "ProductFeedback";
            ViewBag.SelectedAccountTab = "CRP";

            DateTime d1 = Convert.ToDateTime("01/10/2013 00:00:00 AM");
            DateTime d2 = Convert.ToDateTime("07/11/2013 00:00:00 AM");
            var feedbacks = this.DataSession.Fetch<ProductFeedbackScore>(x => x.Status == ProductFeedbackScoreStatus.Live && x.SubmittedByCustomerOn >= d1 && x.SubmittedByCustomerOn <= d2);

            foreach (ProductFeedbackScore feedback in feedbacks)
            {
                var exportResult = this.ExportFeedbackToServiceBus(feedback);

                if (!exportResult.IsExported)
                {
                    // Log the error.
                    this.LogError(new CrashRepairPartsException("Feedback '{0}' was not exported to the Service Bus: {1}".FormatWith(feedback.Id, exportResult.Error.Message), exportResult.Error, this.CmsContext));
                }
            }

            return this.Redirect("~/feedback/respond");
        }

        /// <summary>
        /// Exports the feedback automatic service bus.
        /// </summary>
        /// <param name="feedback">The feedback.</param>
        /// <returns>
        /// A <see cref="ExportResult" />.
        /// </returns>
        private ExportResult ExportFeedbackToServiceBus(ProductFeedbackScore feedback)
        {
            var retry = new RetryPolicy(TimeSpan.FromSeconds(1));
            var exportResult = retry.Execute<ExportResult>(() => ServiceBusExport.Export(feedback, "Feedback"));

            return exportResult;
        }
    }
}
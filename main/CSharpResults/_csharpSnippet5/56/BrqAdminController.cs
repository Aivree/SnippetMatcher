// <copyright file="BrqAdminController.cs" company="Imperial Automotive Ltd">
// Copyright (c) 2011 All Rights Reserved
// </copyright>
namespace Imperial.CrashRepairParts.Cms.Controllers
{
    using System;
    using System.Collections.Generic;
    using System.IO;
    using System.Linq;
    using System.Web.Mvc;
    using Imperial.BodyRepairQuote;
    using Imperial.BodyRepairQuote.Lookups;
    using Imperial.CrashRepairParts.Core;
    using Imperial.CrashRepairParts.Messaging;
    using Imperial.CrashRepairParts.Security;
    using Imperial.CrashRepairParts.Types;
    using Imperial.CrashRepairParts.ViewModels;
    using Imperial.Data.Mvc;
    using Imperial.Net;
    using Imperial.Validation;
    using Imperial.Web;
    using Imperial.Web.ActionResults;

    /// <summary>
    /// Admin Controller for BodyRepairQuote.com
    /// </summary>
    public class BrqAdminController : CrashRepairPartsController
    {
        /// <summary>
        /// BRQ Repairers.
        /// </summary>
        /// <returns>
        /// An <see cref="ActionResult" />.
        /// </returns>
        [Authorise(WebLoginPermission.Customer)]
        [CmsContext("Imperial")]
        public ActionResult Index()
        {
            ViewBag.SelectedMenuItem = "Overview";
            ViewBag.SelectedAccountTab = "BRQ";

            if (this.CmsContext.Session.IsOfficeStaff)
            {
                var viewModel = new ViewModel(this.CmsContext, "BodyRepairQuote.com Administration");

                return this.View("IndexForOfficeStaff", viewModel);
            }
            else
            {
                var repairer = this.CmsContext.Session.Login.Repairer;

                if (repairer != null)
                {
                    var viewModel = new ViewModel<BrqRepairer>(this.CmsContext, "Your BodyRepairQuote.com Account");
                    viewModel.Item = repairer;

                    return this.View("Index", viewModel);
                }
                else
                {
                    var postcode = this.CmsContext.Customer.PostCode.GetStringUpTo(' ');
                    var postcodeItem = this.DataSession.First<BrqRepairerPostCode>(x => x.PostCode == postcode);

                    var customerPostcodeAvailable = !postcodeItem.IsAssigned;
                    var customerPostcode = postcode;
                    var missedLeadsCount = this.DataSession.Fetch<BrqLead>(x => x.Status == EstimateStatus.Outstanding || x.Status == EstimateStatus.Completed)
                        .Where(x => x.CustomerPostCodeArea.IsEqualTo(postcode))
                        .Count();

                    IDictionary<string, string> magicStrings = null;

                    if (missedLeadsCount > 0)
                    {
                        magicStrings = new Dictionary<string, string>();
                        magicStrings.Add("MissedLeadsAlert", "<div class=\"alert alert-warning\">You have already missed {0} leads for the postcode {1}</div>".FormatWith(missedLeadsCount, customerPostcode));
                    }

                    var viewModel = new JoinBrqViewModel(this.CmsContext, "Join BodyRepairQuote.com");
                    viewModel.CmsPageContent = this.RenderCmsPage("JoinBrq", magicStrings);

                    return this.View("IndexNoRepairer", viewModel);
                }
            }
        }

        /// <summary>
        /// BRQ Repairers.
        /// </summary>
        /// <param name="status">The status.</param>
        /// <param name="pageNumber">The page number.</param>
        /// <returns>
        /// An <see cref="ActionResult" />.
        /// </returns>
        [Authorise(WebLoginPermission.OfficeStaff)]
        [CmsContext("Imperial")]
        public ActionResult Repairers(BrqRepairerStatus status = BrqRepairerStatus.Active, int pageNumber = 1)
        {
            ViewBag.SelectedMenuItem = "Repairers";
            ViewBag.SelectedAccountTab = "BRQ";

            var items = this.CmsContext.LoadRepairers(status, pageNumber, 20);

            var viewModel = new PagedListViewModel<dynamic>(this.CmsContext, "{0} Repairers".FormatWith(status));
            viewModel.Add("Status", status);
            viewModel.Items = items;
            viewModel.PageNumbers = new MvcHtmlString(PageNumberWriter.GetPageNumbersFor<dynamic>(this.CmsContext, items, "/BrqAdmin/Repairers?status=" + status + "&pageNumber={0}"));

            return this.CmsView("Repairers", viewModel);
        }

        /// <summary>
        /// Views the postcodes.
        /// </summary>
        /// <param name="pageNumber">The page number.</param>
        /// <returns>
        /// An <see cref="ActionResult" />.
        /// </returns>
        [Authorise(WebLoginPermission.OfficeStaff)]
        [CmsContext("Imperial")]
        public ActionResult ViewPostCodes(int pageNumber = 1)
        {
            ViewBag.SelectedMenuItem = "AssignPostcodes";
            ViewBag.SelectedAccountTab = "BRQ";

            var pageSize = 25;

            ////var postcodes = new DynamicPager(this.DataSession)
            ////    .StoredProcedureName("[dbo].[LoadBrqPostCodes]")
            ////    .PageNumber(pageNumber)
            ////    .PageSize(pageSize)
            ////    .ToPagedList();

            var postcodes = this.CmsContext.LoadPostcodes(pageNumber, pageSize);

            var viewModel = new PagedListViewModel<dynamic>(this.CmsContext, "Postcodes");
            viewModel.Items = postcodes;
            viewModel.PageNumbers = new MvcHtmlString(PageNumberWriter.GetPageNumbersFor<dynamic>(this.CmsContext, postcodes, "/BrqAdmin/ViewPostcodes?pageNumber={0}"));

            return this.CmsView("ViewPostcodes", viewModel);
        }

        /// <summary>
        /// Assign Postcodes to repairers.
        /// </summary>
        /// <param name="id">The id.</param>
        /// <returns>
        /// An <see cref="ActionResult" />.
        /// </returns>
        [Authorise(WebLoginPermission.OfficeStaff)]
        [CmsContext("Imperial")]
        public ActionResult AssignPostCodes(string id)
        {
            ViewBag.SelectedMenuItem = "AssignPostcodes";
            ViewBag.SelectedAccountTab = "BRQ";

            var viewModel = new RepairerAdminViewModel(this.CmsContext);
            viewModel.PageTitle = "Assign Postcodes to Repairers";

            if (id.HasValue())
            {
                var repairer = this.DataSession.First<BrqRepairer>(id.ToInt32());

                if (repairer != null)
                {
                    viewModel.PageTitle = "Assign Postcodes to {0} - {1}".FormatWith(repairer.Customer.Id, repairer.Name);
                    viewModel.Repairer = repairer;
                    viewModel.PostCodes = this.DataSession.Fetch<BrqRepairerPostCode>(x => x.Repairer == repairer)
                        .OrderBy(x => x.PostCode)
                        .ToList();
                }
                else
                {
                    if (id.Contains(' '))
                    {
                        id = id.GetStringUpTo(' ');
                    }

                    viewModel.PostCodes = this.DataSession.FetchLikeStart<BrqRepairerPostCode>("PostCode", id)
                        .OrderBy(x => x.PostCode)
                        .ToList();
                }
            }
            else
            {
                viewModel.PostCodes = this.DataSession.Fetch<BrqRepairerPostCode>(x => x.Id > 0)
                    .OrderBy(x => x.PostCode)
                    .ToList();
            }

            return this.View(viewModel);
        }

        /// <summary>
        /// Views a list of leads depending on the specified status.
        /// </summary>
        /// <param name="id">The id of the repairer.</param>
        /// <param name="status">The status.</param>
        /// <param name="pageNumber">The page number.</param>
        /// <param name="pageSize">Size of the page.</param>
        /// <returns>
        /// An <see cref="ActionResult"/>.
        /// </returns>
        [Authorise(WebLoginPermission.OfficeStaff)]
        [CmsContext("Imperial")]
        public ActionResult ViewLeads(string id, EstimateStatus status = EstimateStatus.Outstanding, int pageNumber = 1, int pageSize = 20)
        {
            ViewBag.SelectedMenuItem = "Leads";
            ViewBag.SelectedAccountTab = "BRQ";

            if (!id.HasValue())
            {
                id = this.CmsContext.Config.BrqDefaultRepairer;
            }

            var repairer = this.DataSession.First<BrqRepairer>(x => x.Customer.Id == id);

            if (repairer != null)
            {
                ViewBag.EstimateStatus = status;

                var leads = this.CmsContext.LoadLeads(repairer, status, pageNumber, pageSize);

                var viewModel = new PagedListViewModel<dynamic>(this.CmsContext, "Leads for {0}".FormatWith(repairer.Name));
                viewModel.PageNumbers = new MvcHtmlString(PageNumberWriter.GetPageNumbersFor<dynamic>(this.CmsContext, leads, "/BrqAdmin/ViewLeads?status=" + status.ToString() + "&pageNumber={0}"));
                viewModel.Items = leads;
                viewModel.Add("Repairer", repairer);

                if (id.IsEqualTo(this.CmsContext.Config.BrqDefaultRepairer))
                {
                    viewModel.PageTitle = "{0} Leads".FormatWith(status);
                }

                return this.View("ViewLeads", viewModel);
            }
            else
            {
                return this.ViewErrorPage("No repairer found for '{0}'.".FormatWith(id));
            }
        }

        /// <summary>
        /// Views a list of leads depending on the specified status.
        /// </summary>
        /// <param name="id">The id of the repairer.</param>
        /// <param name="status">The status.</param>
        /// <returns>
        /// An <see cref="ActionResult" />.
        /// </returns>
        [Authorise(WebLoginPermission.OfficeStaff)]
        [CmsContext("Imperial")]
        public ActionResult Leads(string id, EstimateStatus status = EstimateStatus.Outstanding)
        {
            ViewBag.SelectedMenuItem = "Leads";
            ViewBag.SelectedAccountTab = "BRQ";

            if (!id.HasValue())
            {
                id = this.CmsContext.Config.BrqDefaultRepairer;
            }

            var repairer = this.DataSession.First<BrqRepairer>(x => x.Customer.Id == id);

            if (repairer != null)
            {
                ViewBag.EstimateStatus = status;

                var viewModel = new ViewModel<BrqRepairer, IList<BrqLead>>(this.CmsContext, "Leads for {0} {1}".FormatWith(repairer.Customer.Id, repairer.Name));
                viewModel.Item = repairer;
                viewModel.Item2 = this.DataSession.Fetch<BrqLead>(x => x.Repairer == repairer && x.Status == status)
                    .OrderByDescending(x => x.Created)
                    .ToList();

                if (id.IsEqualTo(this.CmsContext.Config.BrqDefaultRepairer))
                {
                    viewModel.PageTitle = "Unassigned Leads";
                }

                return this.CmsView("Leads", viewModel);
            }
            else
            {
                return this.ViewErrorPage("No repairer found for '{0}'.".FormatWith(id));
            }
        }

        /// <summary>
        /// Views a list of leads depending on the specified status.
        /// </summary>
        /// <param name="searchValue">The search value.</param>
        /// <returns>
        /// An <see cref="ActionResult" />.
        /// </returns>
        [Authorise(WebLoginPermission.OfficeStaff)]
        [CmsContext("Imperial")]
        public ActionResult FindLeads(string searchValue)
        {
            ViewBag.SelectedMenuItem = "Leads";
            ViewBag.SelectedAccountTab = "BRQ";

            if (searchValue.HasValue())
            {
                if (searchValue.StartsWith("BRQ", StringComparison.InvariantCultureIgnoreCase))
                {
                    searchValue = searchValue.Substring(3);
                }
            }

            var lookup = new FindLeadsLookup(this.CmsContext.DataSession);
            lookup.SearchValue = searchValue;

            var viewModel = new BrqListViewModel<BrqLead>(this.CmsContext);
            viewModel.PageTitle = "Search results for {0}".FormatWith(searchValue);
            viewModel.Items = lookup.ToList();

            return this.CmsView("FindLeads", viewModel);
        }

        /// <summary>
        /// Views a list of leads depending on the specified status.
        /// </summary>
        /// <param name="postcode">The postcode.</param>
        /// <returns>
        /// An <see cref="ActionResult" />.
        /// </returns>
        [Authorise(WebLoginPermission.OfficeStaff)]
        [CmsContext("Imperial")]
        public ActionResult FindLeadsForPostcode(string postcode)
        {
            ViewBag.SelectedMenuItem = "Leads";
            ViewBag.SelectedAccountTab = "BRQ";

            var lookup = new FindLeadsByPostcodeLookup(this.CmsContext.DataSession);
            lookup.SearchValue = postcode;

            var viewModel = new BrqListViewModel<BrqLead>(this.CmsContext);
            viewModel.PageTitle = "Search results for {0}".FormatWith(postcode);
            viewModel.Items = lookup.ToList();

            return this.CmsView("FindLeads", viewModel);
        }

        /// <summary>
        /// Site Statistics.
        /// </summary>
        /// <returns>
        /// An <see cref="ActionResult" />.
        /// </returns>
        [Authorise(WebLoginPermission.OfficeStaff)]
        [CmsContext("Imperial")]
        public ActionResult Stats()
        {
            ViewBag.SelectedMenuItem = "Stats";
            ViewBag.SelectedAccountTab = "BRQ";

            var postCodes = this.DataSession.Fetch<BrqRepairerPostCode>(x => x.Id > 0);
            var repairers = this.DataSession.Fetch<BrqRepairer>(x => x.Id > 0)
                .OrderBy(x => x.Customer.Id)
                .ToList();

            var stats = new BrqStats();
            stats.LeadsGenerated = this.DataSession.QueryOver<BrqLead>().Where(x => x.Status >= EstimateStatus.Outstanding).RowCount();
            stats.LeadsCompleted = this.DataSession.QueryOver<BrqLead>().Where(x => x.Status == EstimateStatus.Completed).RowCount();
            stats.LeadsCompletedOnline = this.DataSession.QueryOver<BrqLead>().Where(x => x.CompleteStatus == EstimateCompleteStatus.OnlineEstimate).RowCount();
            stats.LeadsCompletedPhone = this.DataSession.QueryOver<BrqLead>().Where(x => x.CompleteStatus == EstimateCompleteStatus.ContactedByPhone).RowCount();
            stats.LeadsForwardToRepairer = this.DataSession.QueryOver<BrqLead>().Where(x => x.CompleteStatus == EstimateCompleteStatus.ForwardToRepairer).RowCount();

            stats.ActiveRepairers = repairers.Count(x => x.Status == BrqRepairerStatus.Active);
            stats.InactiveRepairers = repairers.Count(x => x.Status == BrqRepairerStatus.Suspended);
            stats.RepairersOnTrial = repairers.Count(x => x.Status == BrqRepairerStatus.Trial);
            stats.RepairersDeleted = repairers.Count(x => x.Status == BrqRepairerStatus.Deleted);
            stats.WebLinkRepairers = repairers.Count(x => x.HasLink);
            stats.TotalPostcodesAssigned = postCodes.Count(x => x.Repairer != null);
            stats.TotalPostcodesUnassigned = postCodes.Count(x => x.Repairer == null);
            stats.TotalPostcodeRevenue = postCodes.Where(x => x.Repairer != null).Sum(x => x.Charge);
            stats.TotalWeblinkRevenue = repairers.Sum(x => x.LinkChargePCM);

            var viewModel = new ViewModel<BrqStats>(this.CmsContext, "Statistics");
            viewModel.Item = stats;

            return this.View(viewModel);
        }

        /// <summary>
        /// Forwards the estimate to.
        /// </summary>
        /// <param name="id">The estimates id.</param>
        /// <param name="emailAddress">The email address.</param>
        /// <returns>
        /// An <see cref="ActionResult" />.
        /// </returns>
        [HttpPost]
        [Authorise(WebLoginPermission.OfficeStaff)]
        [CmsContext("Imperial")]
        public ActionResult ForwardEstimateTo(int id, string emailAddress)
        {
            var estimate = this.DataSession.First<BrqLead>(id);

            if (estimate != null)
            {
                using (var sendMessage = new SendMessage())
                {
                    var messageToCustomer = new BrqEstimateMessage(this.CmsContext, estimate)
                        .TemplateName("FullEstimateToRepairer")
                        .To(emailAddress)
                        .Bcc(this.CmsContext.Session.Login.EmailAddress)
                        .Subject("A New Sales Lead For You from BodyRepairQuote.com")
                        .ToMailMessage();

                    sendMessage.SendMailMessage(messageToCustomer);

                    if (sendMessage.Errors.Count == 0)
                    {
                        this.CmsContext.AddToLeadLog(estimate, BrqLeadLogType.Forward, "Lead forward to '{0}'".FormatWith(emailAddress));

                        return this.JsonNetResult(new { result = true, status = BrqLead.GetFriendlyStatus(estimate.Status) });
                    }
                    else
                    {
                        sendMessage.Errors.Each((x) =>
                            this.CmsContext.AddToLeadLog(estimate, BrqLeadLogType.Forward, "Lead forward to '{0}' failed: {1}".FormatWith(emailAddress, x.Message)));

                        return this.JsonNetResult(new { result = false, message = sendMessage.ErrorMessages() });
                    }
                }
            }
            else
            {
                return this.JsonNetResult(new { result = false, message = "The requested estimate was not found." });
            }
        }

        /// <summary>
        /// Delete a repairer.
        /// </summary>
        /// <param name="id">The repairers id.</param>
        /// <returns>
        /// An <see cref="ActionResult" />.
        /// </returns>
        [Authorise(WebLoginPermission.OfficeStaff)]
        [CmsContext("Imperial")]
        public ActionResult DeleteRepairer(int id)
        {
            ViewBag.SelectedMenuItem = "Repairers";
            ViewBag.SelectedAccountTab = "BRQ";

            var repairer = this.DataSession.First<BrqRepairer>(id);

            if (repairer != null)
            {
                var viewModel = new ViewModel<BrqRepairer>(this.CmsContext, "Delete Repairer {0} - {1}".FormatWith(repairer.Customer.Id, repairer.Name));
                viewModel.Item = repairer;

                return this.View(viewModel);
            }
            else
            {
                return this.ViewErrorPage("Repairer not found.");
            }
        }

        /// <summary>
        /// Delete a repairer.
        /// </summary>
        /// <param name="id">The repairers id.</param>
        /// <returns>
        /// An <see cref="ActionResult" />.
        /// </returns>
        [HttpPost]
        [Authorise(WebLoginPermission.OfficeStaff)]
        [CmsContext("Imperial")]
        public ActionResult SaveDeleteRepairer(int id)
        {
            ViewBag.SelectedMenuItem = "Repairers";
            ViewBag.SelectedAccountTab = "BRQ";

            var repairer = this.DataSession.First<BrqRepairer>(id);

            if (repairer != null)
            {
                // Unassign all the postcodes aassigned to the repairer.
                var postCodes = this.DataSession.Fetch<BrqRepairerPostCode>(x => x.Repairer == repairer);
                postCodes.Each(x => x.Unassign(this.CmsContext.Session));

                // Reassign all the outstanding estimates to the default repairer.
                var defaultRepairer = this.DataSession.First<BrqRepairer>(x => x.Customer.Id == this.CmsContext.Config.BrqDefaultRepairer);

                if (defaultRepairer != null)
                {
                    var estimates = this.DataSession.Fetch<BrqLead>(x => x.Repairer == repairer && x.Status == EstimateStatus.Outstanding);
                    estimates.Each(x => x.AssignTo(defaultRepairer));
                }

                // Set the repairers status to deleted.
                repairer.Status = BrqRepairerStatus.Deleted;

                // Write an entry to the log.
                this.CmsContext.AddToRepairerLog(repairer, BrqRepairerLogType.Deleted, "Repairer deleted.");

                return this.Redirect("/BrqAdmin/Repairers");
            }
            else
            {
                return this.ViewErrorPage("Repairer not found.");
            }
        }

        /// <summary>
        /// Suspend a repairer.
        /// </summary>
        /// <param name="id">The repairers id.</param>
        /// <returns>
        /// An <see cref="ActionResult" />.
        /// </returns>
        [Authorise(WebLoginPermission.OfficeStaff)]
        [CmsContext("Imperial")]
        public ActionResult SuspendRepairer(int id)
        {
            ViewBag.SelectedMenuItem = "Repairers";
            ViewBag.SelectedAccountTab = "BRQ";

            var repairer = this.DataSession.First<BrqRepairer>(id);

            if (repairer != null)
            {
                var viewModel = new ViewModel<BrqRepairer>(this.CmsContext, "Suspend Repairer {0} - {1}".FormatWith(repairer.Customer.Id, repairer.Name));
                viewModel.Item = repairer;

                return this.View(viewModel);
            }
            else
            {
                return this.ViewErrorPage("Repairer not found.");
            }
        }

        /// <summary>
        /// Suspend a repairer.
        /// </summary>
        /// <param name="id">The repairers id.</param>
        /// <returns>
        /// An <see cref="ActionResult" />.
        /// </returns>
        [HttpPost]
        [Authorise(WebLoginPermission.OfficeStaff)]
        [CmsContext("Imperial")]
        public ActionResult SaveSuspendRepairer(int id)
        {
            ViewBag.SelectedMenuItem = "Repairers";
            ViewBag.SelectedAccountTab = "BRQ";

            var repairer = this.DataSession.First<BrqRepairer>(id);

            if (repairer != null)
            {
                // Set the repairers status to deleted.
                repairer.Status = BrqRepairerStatus.Suspended;

                // Unassign all the postcodes aassigned to the repairer.
                var postCodes = this.DataSession.Fetch<BrqRepairerPostCode>(x => x.Repairer == repairer);
                postCodes.Each(x => x.Unassign(this.CmsContext.Session));

                // Reassign all the outstanding estimates to the default repairer.
                var defaultRepairer = this.DataSession.First<BrqRepairer>(x => x.Customer.Id == this.CmsContext.Config.BrqDefaultRepairer);

                if (defaultRepairer != null)
                {
                    var estimates = this.DataSession.Fetch<BrqLead>(x => x.Repairer == repairer && x.Status == EstimateStatus.Outstanding);
                    estimates.Each(x => x.AssignTo(defaultRepairer));
                }

                // Write an entry to the log.
                this.CmsContext.AddToRepairerLog(repairer, BrqRepairerLogType.Suspended, "Repairer suspended. All postcodes removed.");

                return this.Redirect("/BrqAdmin/Repairers");
            }
            else
            {
                return this.ViewErrorPage("Repairer not found.");
            }
        }

        /// <summary>
        /// Unsuspend a repairer.
        /// </summary>
        /// <param name="id">The repairers id.</param>
        /// <returns>
        /// An <see cref="ActionResult" />.
        /// </returns>
        [Authorise(WebLoginPermission.OfficeStaff)]
        [CmsContext("Imperial")]
        public ActionResult UnsuspendRepairer(int id)
        {
            ViewBag.SelectedMenuItem = "Repairers";
            ViewBag.SelectedAccountTab = "BRQ";

            var repairer = this.DataSession.First<BrqRepairer>(id);

            if (repairer != null)
            {
                // Set the repairers status to deleted.
                repairer.Status = BrqRepairerStatus.Active;

                // Write an entry to the log.
                this.CmsContext.AddToRepairerLog(repairer, BrqRepairerLogType.Unsuspended, "Repairer unsuspended.");

                return this.Redirect("/BrqAdmin/Repairers");
            }
            else
            {
                return this.ViewErrorPage("Repairer not found.");
            }
        }

        /// <summary>
        /// Edits the email addresses for a repairer.
        /// </summary>
        /// <returns>
        /// An <see cref="ActionResult"/>.
        /// </returns>
        [CmsContext("Imperial")]
        public ActionResult EditRepairerEmailAddresses()
        {
            if (this.CmsContext.Session.IsAuthenticated)
            {
                if (this.CmsContext.Session.IsBrqRepairer)
                {
                    var viewModel = new ViewModel<BrqRepairer>(this.CmsContext, "Edit EMail Addresses");
                    viewModel.Item = this.CmsContext.Session.Login.Repairer;

                    return this.View(viewModel);
                }
                else
                {
                    return this.Forbidden("You do not have permission to edit the repairers email addresses.");
                }
            }
            else
            {
                return this.RedirectToAction("login", "session", new { redirect = "/brq/account" });
            }
        }

        /// <summary>
        /// Accounts this instance.
        /// </summary>
        /// <returns>
        /// An <see cref="ActionResult"/>.
        /// </returns>
        [CmsContext("Imperial")]
        public ActionResult Account()
        {
            if (this.CmsContext.Session.IsAuthenticated)
            {
                if (this.CmsContext.Session.IsBrqAdmin)
                {
                    return this.RedirectToAction("Repairers");
                }

                if (this.CmsContext.Session.IsBrqRepairer)
                {
                    ViewBag.MenuYourDetails = true;
                    var viewModel = new BrqViewModel(this.CmsContext);
                    viewModel.SetPageTitle("Your Details");

                    return this.View("RepairerYourDetails", viewModel);
                }

                var yourAccountViewModel = new BrqViewModel(this.CmsContext);
                yourAccountViewModel.SetPageTitle("Your Account");

                return this.View("CustomerAccount", yourAccountViewModel);
            }
            else
            {
                return this.RedirectToAction("login", "session", new { redirect = "/brq/account" });
            }
        }

        /// <summary>
        /// Displays a list of new leads for the current repairer.
        /// </summary>
        /// <returns>
        /// An <see cref="ActionResult" />.
        /// </returns>
        [Authorise(WebLoginPermission.CustomerAccountant)]
        [CmsContext("Imperial")]
        public ActionResult NewLeadsForRepairer()
        {
            return this.LoadRepairerLeadsView(EstimateStatus.Outstanding);
        }

        /// <summary>
        /// Displays a list of new leads for the current repairer.
        /// </summary>
        /// <returns>
        /// An <see cref="ActionResult" />.
        /// </returns>
        [Authorise(WebLoginPermission.CustomerAccountant)]
        [CmsContext("Imperial")]
        public ActionResult CompletedLeadsForRepairer()
        {
            return this.LoadRepairerLeadsView(EstimateStatus.Completed);
        }

        /// <summary>
        /// Displays a list of new leads for the current repairer.
        /// </summary>
        /// <returns>
        /// An <see cref="ActionResult" />.
        /// </returns>
        [Authorise(WebLoginPermission.CustomerAccountant)]
        [CmsContext("Imperial")]
        public ActionResult CancelledLeadsForRepairer()
        {
            return this.LoadRepairerLeadsView(EstimateStatus.Cancelled);
        }

        /// <summary>
        /// Views the postcodes assigned to the current repairer.
        /// </summary>
        /// <returns>
        /// An <see cref="ActionResult" />.
        /// </returns>
        [CmsContext("Imperial")]
        [Authorise(WebLoginPermission.CustomerAccountant)]
        public ActionResult PostcodesForRepairer()
        {
            ViewBag.SelectedMenuItem = "RepairerPostcodes";
            ViewBag.SelectedAccountTab = "BRQ";

            var repairer = this.CmsContext.Session.Login.Repairer;

            if (repairer != null)
            {
                ViewBag.MenuYourPostCodes = true;
                var postCodes = this.DataSession.Fetch<BrqRepairerPostCode>(x => x.Repairer == this.CmsContext.Session.Login.Repairer);
                var viewModel = new ViewModel<IList<BrqRepairerPostCode>>(this.CmsContext, "Your Postcodes");
                viewModel.Item = postCodes;

                return this.View(viewModel);
            }
            else
            {
                return this.ViewErrorPage("Your login is not attached to a BodyRepairQuote.com Repairer. Please call us on {0}.".FormatWith(this.CmsContext.WebSite.Phone));
            }
        }

        /// <summary>
        /// Allows repairers to request new postcodes.
        /// </summary>
        /// <returns>
        /// An <see cref="ActionResult" />.
        /// </returns>
        [CmsContext("Imperial")]
        [Authorise(WebLoginPermission.CustomerAccountant)]
        public ActionResult RepairerRequestPostCodes()
        {
            ViewBag.SelectedMenuItem = "RepairerPostcodes";
            ViewBag.SelectedAccountTab = "BRQ";

            var viewModel = new ViewModel(this.CmsContext, "Request a Postcode");

            return this.CmsView("RepairerRequestPostCodes", viewModel);
        }

        /// <summary>
        /// Updates the estimate status.
        /// </summary>
        /// <param name="id">The estimate id.</param>
        /// <param name="status">The new estimate status.</param>
        /// <returns>
        /// An <see cref="ActionResult" />.
        /// </returns>
        [NoCache]
        [CmsContext("Imperial")]
        public ActionResult UpdateEstimateStatus(int id, EstimateStatus status)
        {
            if (this.CmsContext.Session.IsAuthenticated)
            {
                if (this.CmsContext.Session.IsBrqRepairer)
                {
                    var estimate = this.DataSession.First<BrqLead>(id);

                    if (estimate != null)
                    {
                        estimate.UpdateStatus(this.CmsContext.Session, status);
                        estimate.Update(this.DataSession);

                        return this.JsonNetResult(new { result = true, status = status.ToString(), friendlyStatus = BrqLead.GetFriendlyStatus(status), lastUpdated = "{0}".FormatWith(estimate.LastUpdated.Value.ToString("dd/MM/yyyy 'at' hh:mm:sstt")) });
                    }
                    else
                    {
                        return this.JsonNetResult(new { result = false, message = "The estimate was not found." });
                    }
                }
                else
                {
                    return this.JsonNetResult(new { result = false, message = "You do not have permission to preform this action." });
                }
            }
            else
            {
                return this.JsonNetResult(new { result = false, message = "You must be logged in to preform this action." });
            }
        }

        /// <summary>
        /// Cancels the estimate.
        /// </summary>
        /// <param name="id">The unique identifier for the estimate.</param>
        /// <returns>
        /// An <see cref="ActionResult" />.
        /// </returns>
        [HttpPost]
        [CmsContext("Imperial")]
        public ActionResult CancelEstimate(int id)
        {
            if (this.CmsContext.Session.IsAuthenticated)
            {
                if (this.CmsContext.Session.IsBrqRepairer)
                {
                    var estimate = this.DataSession.First<BrqLead>(id);

                    if (estimate != null)
                    {
                        estimate.UpdateStatus(this.CmsContext.Session, EstimateStatus.Cancelled);

                        this.CmsContext.AddToLeadLog(estimate, BrqLeadLogType.Cancelled, "Lead cancelled.");

                        return this.JsonNetResult(new { result = true });
                    }
                    else
                    {
                        return this.JsonNetResult(new { result = false, message = "Estimate not found." });
                    }
                }
                else
                {
                    return this.JsonNetResult(new { result = false, message = "Permission denied." });
                }
            }
            else
            {
                return this.JsonNetResult(new { result = false, message = "You must be logged in." });
            }
        }

        /// <summary>
        /// Covnerts a lead.
        /// </summary>
        /// <param name="id">The unique identifier for the estimate.</param>
        /// <returns>
        /// An <see cref="ActionResult" />.
        /// </returns>
        [CmsContext("Imperial")]
        public ActionResult ConvertLead(int id)
        {
            if (this.CmsContext.Session.IsAuthenticated)
            {
                if (this.CmsContext.Session.IsBrqRepairer)
                {
                    var estimate = this.DataSession.First<BrqLead>(id);

                    if (estimate != null)
                    {
                        var viewModel = new ViewModel<BrqLead>(this.CmsContext, "Convert Estimate");
                        viewModel.Item = estimate;

                        return this.View(viewModel);
                    }
                    else
                    {
                        return this.HttpNotFound("Estimate {0} was not found.".FormatWith(id));
                    }
                }
                else
                {
                    return this.Forbidden("You do not have permission to convert an estimate.");
                }
            }
            else
            {
                return this.Unauthorised("You must be logged in to convert an estimate.");
            }
        }

        /// <summary>
        /// Saves the converted estimate.
        /// </summary>
        /// <param name="id">The id.</param>
        /// <param name="completeStatus">The complete status.</param>
        /// <param name="jobReference">The job reference.</param>
        /// <returns>
        /// An <see cref="ActionResult" />.
        /// </returns>
        [HttpPost]
        [CmsContext("Imperial")]
        public ActionResult SaveConvertLead(int id, EstimateCompleteStatus completeStatus, string jobReference = null)
        {
            if (this.CmsContext.Session.IsAuthenticated)
            {
                if (this.CmsContext.Session.IsBrqRepairer)
                {
                    var estimate = this.DataSession.First<BrqLead>(id);

                    if (estimate != null)
                    {
                        estimate.UpdateStatus(this.CmsContext.Session, EstimateStatus.Completed);
                        estimate.CompleteStatus = completeStatus;
                        estimate.JobReference = jobReference;

                        this.CmsContext.AddToLeadLog(estimate, BrqLeadLogType.Converted, "{0}, job reference: {1}.".FormatWith(completeStatus, jobReference));

                        return this.JsonNetResult(new { result = true });
                    }
                    else
                    {
                        return this.JsonNetResult(new { result = false, message = "Estimate not found." });
                    }
                }
                else
                {
                    return this.JsonNetResult(new { result = false, message = "Permission denied." });
                }
            }
            else
            {
                return this.JsonNetResult(new { result = false, message = "You must be logged in." });
            }
        }

        /// <summary>
        /// View a lead.
        /// </summary>
        /// <param name="id">The unique identifier for the estimate.</param>
        /// <returns>
        /// An <see cref="ActionResult" />.
        /// </returns>
        [Authorise(WebLoginPermission.CustomerAccountant)]
        [CmsContext("Imperial")]
        public ActionResult ViewLead(string id)
        {
            ViewBag.SelectedMenuItem = "Leads";
            ViewBag.SelectedAccountTab = "BRQ";

            var leadNumber = id.ToNumericOnly().ToInt32();
            var lead = this.DataSession.First<BrqLead>(leadNumber);

            if (lead != null)
            {
                if (lead.Repairer == this.CmsContext.Session.Login.Repairer || this.CmsContext.Session.IsBrqAdmin)
                {
                    lead.CustomerDetailsShown = true;

                    this.CmsContext.AddToLeadLog(lead, BrqLeadLogType.Viewed, "Lead viewed by {0}.".FormatWith(this.CmsContext.Session.Login.Name));

                    var viewModel = new ViewModel<BrqLead>(this.CmsContext, "Lead {0}".FormatWith(this.CmsContext.FormatOrderNumber(lead.Id)));
                    viewModel.Item = lead;

                    return this.View(viewModel);
                }
                else
                {
                    return this.Forbidden("Sorry you do not have permission to execute this action.");
                }
            }
            else
            {
                return this.HttpNotFound("The requested estimate was not found.");
            }
        }

        /// <summary>
        /// Views the customer for estimate.
        /// </summary>
        /// <param name="id">The id.</param>
        /// <returns>
        /// An <see cref="ActionResult" />.
        /// </returns>
        [CmsContext("Imperial")]
        public ActionResult RepairerViewCustomerInfo(string id)
        {
            if (this.CmsContext.Session.IsAuthenticated)
            {
                if (this.CmsContext.Session.IsBrqRepairer)
                {
                    var estimateNumber = id.ToNumericOnly().ToInt32();

                    var estimate = this.DataSession.First<BrqLead>(estimateNumber);

                    var viewModel = new ViewModel<BrqLead>(this.CmsContext, "Estimate {0}".FormatWith(this.CmsContext.FormatOrderNumber(estimate.Id)));
                    viewModel.Item = estimate;

                    return this.View(viewModel);
                }
            }

            return this.RedirectToAction("login", "session", new { redirect = "/brq/account" });
        }

        /// <summary>
        /// Views the customer for estimate.
        /// </summary>
        /// <param name="id">The id.</param>
        /// <param name="comments">The comments.</param>
        /// <param name="estimateAmount">The estimate amount.</param>
        /// <param name="templateName">Name of the template.</param>
        /// <returns>
        /// An <see cref="ActionResult" />.
        /// </returns>
        [Authorise(WebLoginPermission.CustomerAccountant)]
        [CmsContext("Imperial")]
        public ActionResult PreviewLead(string id, string comments = null, decimal estimateAmount = 0, string templateName = null)
        {
            var estimateNumber = id.ToNumericOnly().ToInt32();
            var estimate = this.DataSession.First<BrqLead>(estimateNumber);

            if (estimate != null)
            {
                estimate.RepairerComments = comments.CleanWebInput();
                estimate.EstimateAmount = estimateAmount;
                estimate.SaveOrUpdate(this.DataSession);

                if (!templateName.HasValue())
                {
                    templateName = "EstimateToCustomer";
                }

                var messageToCustomer = new BrqEstimateMessage(this.CmsContext, estimate)
                    .TemplateName(templateName)
                    .To(estimate.CustomerEmailAddress)
                    .Subject("Your estimate from BodyRepairQuote.com")
                    .ToString();

                var viewModel = new ViewModel<string>(this.CmsContext, "Estimate Preview for {0}".FormatWith(this.CmsContext.FormatOrderNumber(estimate.Id)));
                viewModel.Item = messageToCustomer;

                return this.View(viewModel);
            }
            else
            {
                return this.ViewErrorPage("The estimate was not found.");
            }
        }

        /// <summary>
        /// Sends the repairer Postcode request.
        /// </summary>
        /// <param name="postCode">The post code.</param>
        /// <returns>
        /// An <see cref="ActionResult" />.
        /// </returns>
        [HttpPost]
        [CmsContext("Imperial")]
        [Authorise(WebLoginPermission.CustomerAccountant)]
        public ActionResult SendRepairerPostCodeRequest(string postCode)
        {
            var repairer = this.CmsContext.Session.Login.Repairer;
            var requestedPostCode = postCode.CleanPostCode();

            if (repairer != null)
            {
                using (var sendMessage = new SendMessage())
                {
                    var messageToRepairer = new GenericMessage(this.CmsContext)
                        .AddMagicString("RepairerAccountNumber", repairer.Customer.AccountNumber)
                        .AddMagicString("RepairerName", repairer.Name)
                        .AddMagicString("PostCode", requestedPostCode)
                        .TemplateName("RepairerPostCodeRequest")
                        .To(this.CmsContext.Config.BrqPostCodeRequestsTo)
                        .Subject("BodyRepairQuote.Com Repairer Postcode Request")
                        .ToMailMessage();

                    sendMessage.SendMailMessage(messageToRepairer);
                }

                return this.RepairerThanks("We've received your postcode requests and will contact you shortly.");
            }
            else
            {
                return this.ViewErrorPage("Your login is not attached to a repairer.");
            }
        }

        /// <summary>
        /// Sends the repairer Postcode request.
        /// </summary>
        /// <param name="id">The id.</param>
        /// <param name="postCode">The post code.</param>
        /// <returns>
        /// An <see cref="ActionResult" />.
        /// </returns>
        [CmsContext("Imperial")]
        [Authorise(WebLoginPermission.CustomerAccountant)]
        public ActionResult SendRepairerPostCodeRemoveRequest(int id, string postCode)
        {
            var repairer = this.DataSession.First<BrqRepairer>(id);
            var requestedPostCode = postCode.CleanPostCode();

            if (repairer != null)
            {
                using (var sendMessage = new SendMessage())
                {
                    var messageToRepairer = new GenericMessage(this.CmsContext)
                        .AddMagicString("RepairerAccountNumber", repairer.Customer.AccountNumber)
                        .AddMagicString("RepairerName", repairer.Name)
                        .AddMagicString("PostCode", requestedPostCode)
                        .TemplateName("RepairerRemovePostCodeRequest")
                        .To(this.CmsContext.Config.BrqPostCodeRequestsTo)
                        .Subject("BodyRepairQuote.Com Repairer Remove Postcode Request")
                        .ToMailMessage();

                    sendMessage.SendMailMessage(messageToRepairer);
                }

                return this.RepairerThanks("We've received your postcode removal requests and will contact you shortly.");
            }
            else
            {
                return this.ViewErrorPage("The repairer {0} was not found.".FormatWith(id));
            }
        }

        /// <summary>
        /// Estimators the specified id.
        /// </summary>
        /// <param name="id">The id.</param>
        /// <returns>
        /// An <see cref="ActionResult"/>.
        /// </returns>
        [CmsContext("Imperial")]
        public ActionResult Estimator(string id)
        {
            if (id.HasValue())
            {
                this.OverrideTheme(id);
            }

            return this.RedirectToAction("Start", "Brq");
        }

        /// <summary>
        /// Clears the estimator.
        /// </summary>
        /// <returns>
        /// An <see cref="ActionResult"/>.
        /// </returns>
        [CmsContext("Imperial")]
        public ActionResult ResetTheme()
        {
            if (this.CmsContext.Session.ThemeName.HasValue())
            {
                this.CmsContext.Session.Repairer = null;
                this.CmsContext.Session.ThemeName = null;
                this.CmsContext.Session.Update(this.DataSession);

                return this.RedirectToAction("Start", "Brq");
            }
            else
            {
                return this.HttpNotFound();
            }
        }

        /// <summary>
        /// Postcode Checker.
        /// </summary>
        /// <returns>
        /// An <see cref="ActionResult"/>.
        /// </returns>
        [CmsContext("Imperial")]
        public ActionResult PostCodeChecker()
        {
            return this.View();
        }

        /// <summary>
        /// Removes the part from estimate.
        /// </summary>
        /// <param name="postCode">The post code.</param>
        /// <param name="repairer">The repairer.</param>
        /// <returns>
        /// An <see cref="ActionResult" />.
        /// </returns>
        [Authorise(WebLoginPermission.OfficeStaff)]
        [CmsContext("Imperial")]
        public ActionResult RemovePostcodeFromRepairer(string postCode, int repairer)
        {
            var result = this.UnassignPostcode(postCode);

            return this.RedirectToAction("AssignPostcodes", new { id = repairer });
        }

        /// <summary>
        /// Removes the part from estimate.
        /// </summary>
        /// <param name="postCode">The post code.</param>
        /// <returns>
        /// An <see cref="ActionResult" />.
        /// </returns>
        [Authorise(WebLoginPermission.OfficeStaff)]
        [CmsContext("Imperial")]
        public ActionResult RemovePostcode(string postCode)
        {
            var result = this.UnassignPostcode(postCode);

            return this.RedirectToAction("AssignPostcodes");
        }

        /// <summary>
        /// Updates the estimate line.
        /// </summary>
        /// <param name="id">The estimate id.</param>
        /// <param name="lineid">The estime line id.</param>
        /// <param name="productPrice">The product price.</param>
        /// <param name="labourCharge">The labour charge.</param>
        /// <param name="labourHours">The labour hours.</param>
        /// <returns>
        /// An <see cref="ActionResult" />.
        /// </returns>
        [HttpPost]
        [CmsContext("Imperial")]
        public ActionResult UpdateEstimateLine(int id, int lineid, decimal productPrice, decimal labourCharge, decimal labourHours)
        {
            if (this.CmsContext.Session.IsBrqRepairer)
            {
                var estimate = this.DataSession.First<BrqLead>(id);

                if (estimate != null)
                {
                    var lineUpdated = estimate.UpdateLine(lineid, productPrice, labourCharge, labourHours);
                    estimate.SaveOrUpdate(this.DataSession);

                    if (lineUpdated)
                    {
                        return new JsonNetResult(new { result = true });
                    }
                    else
                    {
                        return new JsonNetResult(new { result = false, message = "The estimate line could not be updated at this time." });
                    }
                }
                else
                {
                    return new JsonNetResult(new { result = false, message = "The estimate was not found." });
                }
            }
            else
            {
                return new JsonNetResult(new { result = false, message = "Permission denied." });
            }
        }

        /// <summary>
        /// New BRQ Repairer.
        /// </summary>
        /// <returns>
        /// An <see cref="ActionResult" />.
        /// </returns>
        [Authorise(WebLoginPermission.OfficeStaff)]
        [CmsContext("Imperial")]
        public ActionResult NewRepairer()
        {
            ViewBag.SelectedMenuItem = "Overview";
            ViewBag.SelectedAccountTab = "BRQ";

            var viewModel = new ViewModel<BrqRepairer>(this.CmsContext, "New Repairer");
            viewModel.Item = new BrqRepairer();

            return this.View(viewModel);
        }

        /// <summary>
        /// Edit BRQ Repairer.
        /// </summary>
        /// <param name="id">The id.</param>
        /// <returns>
        /// An <see cref="ActionResult" />.
        /// </returns>
        [CmsContext("Imperial")]
        public ActionResult EditRepairer(int id = 0)
        {
            ViewBag.SelectedMenuItem = "Overview";
            ViewBag.SelectedAccountTab = "BRQ";

            var repairer = this.DataSession.First<BrqRepairer>(id);

            if (repairer != null)
            {
                var viewModel = new ViewModel<BrqRepairer>(this.CmsContext, "Edit Repairer {0} - {1}".FormatWith(repairer.Customer.Id, repairer.Name));
                viewModel.Item = repairer;
                viewModel.Add("PostCodeCount", this.DataSession.Fetch<BrqRepairerPostCode>(x => x.Repairer == repairer).Count());
                viewModel.Add("EstimateCount", this.DataSession.Count<BrqLead>(x => x.Repairer == repairer && x.FromLinkedSite == true));

                return this.View(viewModel);
            }
            else
            {
                return this.ViewErrorPage("Repairer {0} was not found.".FormatWith(id));
            }
        }

        /// <summary>
        /// Save BRQ Repairer.
        /// </summary>
        /// <param name="id">The id.</param>
        /// <param name="name">The name.</param>
        /// <param name="contact">The contact.</param>
        /// <param name="emailsTo">The emails to.</param>
        /// <param name="emailsCcTo1">The emails cc to1.</param>
        /// <param name="emailsCcTo2">The emails cc to2.</param>
        /// <param name="hasLink">if set to <c>true</c> [has link].</param>
        /// <param name="status">The status.</param>
        /// <param name="charge">The charge PCM.</param>
        /// <param name="defaultRepairer">if set to <c>true</c> [default repairer].</param>
        /// <param name="webSiteUrl">The web site URL.</param>
        /// <returns>
        /// An <see cref="ActionResult" />.
        /// </returns>
        [HttpPost]
        [Authorise(WebLoginPermission.OfficeStaff)]
        [CmsContext("Imperial")]
        public ActionResult SaveRepairer(int id, string name = null, string contact = null, string emailsTo = null, string emailsCcTo1 = null, string emailsCcTo2 = null, bool hasLink = false, BrqRepairerStatus status = BrqRepairerStatus.Active, decimal charge = 0, bool defaultRepairer = false, string webSiteUrl = null)
        {
            var repairer = this.DataSession.First<BrqRepairer>(id);

            if (repairer != null)
            {
                repairer.Name = name.HasValue() ? name.CleanWebInput() : repairer.Name;
                repairer.Contact = contact.HasValue() ? contact.CleanWebInput() : repairer.Contact;
                repairer.EMailNotificationsTo = emailsTo.CleanWebInput();
                repairer.EMailNotificationsCcTo1 = emailsCcTo1.CleanWebInput();
                repairer.EMailNotificationsCcTo2 = emailsCcTo2.CleanWebInput();
                repairer.Status = status;
                repairer.WebSiteUrl = webSiteUrl.CleanWebInput();

                if (this.CmsContext.Session.IsAdmin)
                {
                    repairer.HasLink = hasLink;
                    repairer.LinkChargePCM = charge;

                    if (hasLink)
                    {
                        this.CreateRepairerThemeFor(repairer);
                    }
                }

                return this.RedirectToAction("Repairers");
            }
            else
            {
                return this.ViewErrorPage("The requested repairer was not found.");
            }
        }

        /// <summary>
        /// Creates the default repairer.
        /// </summary>
        /// <returns>
        /// An <see cref="ActionResult" />.
        /// </returns>
        [Authorise(WebLoginPermission.OfficeStaff)]
        [CmsContext("Imperial")]
        public ActionResult CreateDefaultRepairer()
        {
            ViewBag.SelectedAccountTab = "BRQ";

            var customer = this.DataSession.First<Customer>(this.CmsContext.Config.BrqDefaultRepairer);

            if (customer != null)
            {
                var repairer = this.DataSession.First<BrqRepairer>(x => x.Customer == customer);

                if (repairer == null)
                {
                    // Create a new repairer
                    repairer = new BrqRepairer(customer, this.CmsContext.Config.BrqFreeTrialMonths);
                    repairer.EMailNotificationsTo = this.CmsContext.Config.BrqDefaultMailTo;
                    repairer.Name = this.CmsContext.WebSite.Name;
                    repairer.Save(this.DataSession);

                    return this.RedirectToAction("Repairers");
                }
                else
                {
                    return this.ViewErrorPage("The default repairer already exists.");
                }
            }
            else
            {
                return this.ViewErrorPage("No customer found with account number '{0}'.".FormatWith(this.CmsContext.Config.BrqDefaultRepairer));
            }
        }

        /// <summary>
        /// Confirm Creating a new BRQ Repairer.
        /// </summary>
        /// <param name="id">The id.</param>
        /// <param name="freeTrial">if set to <c>true</c> [free trial].</param>
        /// <returns>
        /// An <see cref="ActionResult" />.
        /// </returns>
        [HttpPost]
        [Authorise(WebLoginPermission.OfficeStaff)]
        [CmsContext("Imperial")]
        public ActionResult ConfirmNewRepairer(string id, bool freeTrial = false)
        {
            ViewBag.SelectedAccountTab = "BRQ";

            var viewModel = new ViewModel<BrqRepairer>(this.CmsContext, "New Repairer");
            var customer = this.DataSession.First<Customer>(id.ToUpper());

            if (customer != null)
            {
                var repairer = this.DataSession.First<BrqRepairer>(x => x.Customer == customer);

                if (repairer == null)
                {
                    var freeTrialMonths = 0;

                    if (freeTrial)
                    {
                        freeTrialMonths = this.CmsContext.Config.BrqFreeTrialMonths;
                    }

                    // Create a new repairer
                    viewModel.Item = new BrqRepairer(customer, freeTrialMonths);
                }
                else
                {
                    viewModel.AddError("A repairer already exists for customer '{0}'.".FormatWith(id));
                }
            }
            else
            {
                viewModel.AddError("A customer was not found with account number '{0}'.".FormatWith(id));
            }

            return this.View(viewModel);
        }

        /// <summary>
        /// Save New BRQ Repairer.
        /// </summary>
        /// <param name="id">The id.</param>
        /// <param name="freeTrial">if set to <c>true</c> [free trial].</param>
        /// <returns>
        /// An <see cref="ActionResult" />.
        /// </returns>
        [HttpPost]
        [Authorise(WebLoginPermission.OfficeStaff)]
        [CmsContext("Imperial")]
        public ActionResult SaveNewRepairer(string id, bool freeTrial = false)
        {
            ViewBag.SelectedAccountTab = "BRQ";

            var customer = this.DataSession.First<Customer>(id.ToUpper());

            if (customer != null)
            {
                var repairer = this.DataSession.First<BrqRepairer>(x => x.Customer == customer);

                if (repairer == null)
                {
                    var freeTrialMonths = 0;

                    if (freeTrial)
                    {
                        freeTrialMonths = this.CmsContext.Config.BrqFreeTrialMonths;
                    }

                    // Create a new repairer
                    repairer = new BrqRepairer(customer, freeTrialMonths);
                    repairer.Save(this.DataSession);

                    // Add an entry to the log.
                    if (freeTrialMonths > 0)
                    {
                        this.CmsContext.AddToRepairerLog(repairer, BrqRepairerLogType.Created, "{0} created as a repairer.".FormatWith(repairer.Name));
                    }
                    else
                    {
                        this.CmsContext.AddToRepairerLog(repairer, BrqRepairerLogType.Created, "{0} created as a repairer with a {1} month trial.".FormatWith(repairer.Name, freeTrialMonths));
                    }

                    // Assign the repairer to the customers logins.
                    var logins = this.DataSession.Fetch<WebLogin>(x => x.Customer == customer);
                    logins.Each(x => x.AssignRepairer(repairer));

                    return this.RedirectToAction("Repairers");
                }
                else
                {
                    var viewModel = new ViewModel<BrqRepairer>(this.CmsContext, "New Repairer");
                    viewModel.Item = new BrqRepairer();
                    viewModel.AddError("A repairer already exists for customer '{0}'.".FormatWith(id));

                    return this.View("NewRepairer", viewModel);
                }
            }
            else
            {
                var viewModel = new ViewModel<BrqRepairer>(this.CmsContext, "New Repairer");
                viewModel.Item = new BrqRepairer();
                viewModel.AddError("A customer was not found with account number '{0}'.".FormatWith(id));

                return this.View("NewRepairer", viewModel);
            }
        }

        /// <summary>
        /// Adds a postcode.
        /// </summary>
        /// <param name="id">The id.</param>
        /// <returns>
        /// An <see cref="ActionResult" />.
        /// </returns>
        [Authorise(WebLoginPermission.OfficeStaff)]
        [CmsContext("Imperial")]
        public ActionResult AddPostcode(int id)
        {
            var repairer = this.DataSession.First<BrqRepairer>(id);

            if (repairer != null)
            {
                var viewModel = new ViewModel<BrqRepairerPostCode>(this.CmsContext, "Add Postcode to {0}".FormatWith(repairer.Name));
                viewModel.Item = new BrqRepairerPostCode();
                viewModel.Item.Repairer = repairer;
                viewModel.Item.Charge = this.CmsContext.Config.BrqPostCodeDefaultCharge;

                return this.View("EditPostCode", viewModel);
            }
            else
            {
                return this.ViewErrorPage("The repairer {0} was not found.".FormatWith(id));
            }
        }

        /// <summary>
        /// Assign a postcode to a repairer.
        /// </summary>
        /// <param name="postCode">The post code.</param>
        /// <returns>
        /// An <see cref="ActionResult" />.
        /// </returns>
        [Authorise(WebLoginPermission.OfficeStaff)]
        [CmsContext("Imperial")]
        public ActionResult AssignPostcode(string postCode)
        {
            ViewBag.SelectedMenuItem = "Postcodes";
            ViewBag.SelectedAccountTab = "BRQ";

            var postCodeEntry = this.DataSession.First<BrqRepairerPostCode>(x => x.PostCode == postCode);

            if (postCodeEntry != null)
            {
                var viewModel = new ViewModel<BrqRepairerPostCode, IList<BrqRepairer>>(this.CmsContext, "Assign Postcode {0}".FormatWith(postCode.ToUpper()));
                viewModel.Item = postCodeEntry;
                viewModel.Item.Charge = this.CmsContext.Config.BrqPostCodeDefaultCharge;
                viewModel.Item2 = this.DataSession.Fetch<BrqRepairer>(x => x.Status == BrqRepairerStatus.Active);

                return this.View("AssignPostcode", viewModel);
            }
            else
            {
                return this.ViewErrorPage("The postcode {0} was not found.".FormatWith(postCode));
            }
        }

        /// <summary>
        /// Assigns a postcode to a repairer.
        /// </summary>
        /// <param name="id">The id.</param>
        /// <param name="postCode">The post code.</param>
        /// <param name="repairerId">The repairer id.</param>
        /// <param name="charge">The charge.</param>
        /// <returns>
        /// An <see cref="ActionResult" />.
        /// </returns>
        [HttpPost]
        [Authorise(WebLoginPermission.OfficeStaff)]
        [CmsContext("Imperial")]
        public ActionResult SaveAssignPostcode(int id, string postCode, int repairerId, decimal charge)
        {
            var postCodeEntry = this.DataSession.First<BrqRepairerPostCode>(id);

            if (postCodeEntry != null)
            {
                var repairer = this.DataSession.First<BrqRepairer>(repairerId);

                if (repairer != null)
                {
                    postCodeEntry.AssignTo(this.CmsContext.Session, repairer, charge);
                    postCodeEntry.SaveOrUpdate(this.DataSession);

                    // Add an entry to the log.
                    this.CmsContext.AddToRepairerLog(repairer, BrqRepairerLogType.PostcodesAdded, "Postcodes '{0}' successfully added to {1}.".FormatWith(postCode, repairer.Name));

                    return this.RedirectToAction("AssignPostcodes");
                }
                else
                {
                    return this.ViewErrorPage("Repairer with id {0} was found.".FormatWith(repairerId));
                }
            }
            else
            {
                return this.ViewErrorPage("Postcode {0} could not be assigned as no entry was found.".FormatWith(postCode));
            }
        }

        /// <summary>
        /// Edits a Postcode.
        /// </summary>
        /// <param name="postCode">The Postcode.</param>
        /// <returns>
        /// An <see cref="ActionResult" />.
        /// </returns>
        [Authorise(WebLoginPermission.OfficeStaff)]
        [CmsContext("Imperial")]
        public ActionResult EditPostCode(string postCode)
        {
            ViewBag.SelectedMenuItem = "Postcodes";
            ViewBag.SelectedAccountTab = "BRQ";

            var postCodeEntry = this.DataSession.First<BrqRepairerPostCode>(x => x.PostCode == postCode);

            if (postCodeEntry != null)
            {
                var viewModel = new ViewModel<BrqRepairerPostCode>(this.CmsContext, "Edit Postcode {0}".FormatWith(postCode.ToUpper()));
                viewModel.Item = postCodeEntry;

                if (postCodeEntry.IsAssigned)
                {
                    viewModel.PageTitle = "Edit Postcode {0} assigned to {1}".FormatWith(postCode.ToUpper(), postCodeEntry.Repairer.Name);
                }

                return this.View(viewModel);
            }
            else
            {
                return this.ViewErrorPage("The postcode {0} was not found.".FormatWith(postCode));
            }
        }

        /// <summary>
        /// Save a Postcode.
        /// </summary>
        /// <param name="id">The id.</param>
        /// <param name="postCode">The Postcode.</param>
        /// <param name="charge">The charge.</param>
        /// <param name="repairerId">The repairer id.</param>
        /// <returns>
        /// An <see cref="ActionResult" />.
        /// </returns>
        [HttpPost]
        [Authorise(WebLoginPermission.OfficeStaff)]
        [CmsContext("Imperial")]
        public ActionResult SavePostCodes(int id, string postCode, decimal charge, int repairerId = 0)
        {
            var validationResult = new ValidationResult();

            if (!postCode.HasValue())
            {
                validationResult.AddValidationFailure("A postcode must be entered.");
            }

            if (charge < 0)
            {
                validationResult.AddValidationFailure("The charge must be greater than or equal to zero.");
            }

            if (repairerId < 1)
            {
                validationResult.AddValidationFailure("A repairer must be entered.");
            }

            if (validationResult.IsValid)
            {
                var repairer = this.DataSession.First<BrqRepairer>(repairerId);

                if (repairer != null)
                {
                    var postCodes = postCode.Split(new char[] { ',', ';', ' ' }, StringSplitOptions.RemoveEmptyEntries);

                    foreach (var postCodePart in postCodes)
                    {
                        this.AssignPostCodeToRepairer(repairer, postCodePart.Trim(), charge);
                    }

                    return this.RedirectToAction("AssignPostCodes", new { id = repairerId });
                }
                else
                {
                    return this.ViewErrorPage("The repairer {0} was not found.".FormatWith(repairerId));
                }
            }
            else
            {
                var viewModel = new ViewModel<BrqRepairerPostCode>(this.CmsContext, "Edit Postcode {0}".FormatWith(postCode));
                viewModel.AddErrorsFrom(validationResult);

                if (id == 0)
                {
                    viewModel.Item = new BrqRepairerPostCode();
                    viewModel.Item.PostCode = postCode.ToUpper();
                    viewModel.Item.Repairer = this.DataSession.First<BrqRepairer>(repairerId);
                }
                else
                {
                    viewModel.Item = this.DataSession.First<BrqRepairerPostCode>(x => x.PostCode == postCode);
                    viewModel.Item.Repairer = this.DataSession.First<BrqRepairer>(repairerId);
                    viewModel.Item.Charge = charge;
                }

                return this.View("EditPostCode", viewModel);
            }
        }

        /// <summary>
        /// Sends the lead to the customer.
        /// </summary>
        /// <param name="id">The id.</param>
        /// <returns>
        /// An <see cref="ActionResult" />.
        /// </returns>
        [CmsContext("Imperial")]
        public ActionResult SendLeadToCustomer(int id)
        {
            if (this.CmsContext.Session.IsAuthenticated)
            {
                if (this.CmsContext.Session.IsBrqRepairer)
                {
                    var estimate = this.DataSession.First<BrqLead>(id);
                    estimate.UpdateStatus(this.CmsContext.Session, EstimateStatus.Completed);
                    estimate.CompleteStatus = EstimateCompleteStatus.OnlineEstimate;
                    estimate.Update(this.DataSession);

                    using (var sendMessage = new SendMessage())
                    {
                        var messageToCustomer = new BrqEstimateMessage(this.CmsContext, estimate)
                            .TemplateName("EstimateToCustomer")
                            .To(estimate.CustomerEmailAddress)
                            .Subject("Your estimate from BodyRepairQuote.com")
                            .ToMailMessage();

                        sendMessage.SendMailMessage(messageToCustomer);
                    }

                    return this.JsonNetResult(new { result = true });
                }
                else
                {
                    var viewModel = new BrqViewModel(this.CmsContext);
                    viewModel.SetPageTitle("Your Account");

                    return this.View("CustomerAccount", viewModel);
                }
            }
            else
            {
                return this.RedirectToAction("login", "session", new { redirect = "/brq/account" });
            }
        }

        /// <summary>
        /// Enable BRQ for the specified customer.
        /// </summary>
        /// <returns>
        /// An <see cref="ActionResult" />.
        /// </returns>
        [CmsContext("Imperial")]
        public ActionResult EnableBRQ()
        {
            var customer = this.CmsContext.Customer;

            if (customer != null)
            {
                var repairer = this.DataSession.First<BrqRepairer>(x => x.Customer == customer);

                if (repairer != null)
                {
                    if (repairer.Status.In(BrqRepairerStatus.Deleted))
                    {
                        repairer.Status = BrqRepairerStatus.Active;

                        this.AddCustomerPostcodeToRepairer(customer, repairer);
                        this.AddRepairerToCurrentLogin(repairer);
                    }
                    else
                    {
                        return this.ViewErrorPage("Your are already signed up to BodyRepairQuote.com");
                    }
                }
                else
                {
                    repairer = new BrqRepairer(customer, 0);
                    repairer.Save(this.DataSession);

                    this.AddCustomerPostcodeToRepairer(customer, repairer);
                    this.AddRepairerToCurrentLogin(repairer);
                }

                return this.Redirect("/BrqAdmin/Index");
            }
            else
            {
                return this.ViewErrorPage("Customer not found. Your session may have timed out.");
            }
        }

        /// <summary>
        /// Views the lead log.
        /// </summary>
        /// <param name="id">The id.</param>
        /// <returns>
        /// An <see cref="ActionResult" />.
        /// </returns>
        [Authorise(WebLoginPermission.OfficeStaff)]
        [CmsContext("Imperial")]
        public ActionResult ViewLeadLog(int id)
        {
            var lead = this.DataSession.First<BrqLead>(id);

            if (lead != null)
            {
                var viewModel = new ListViewModel<BrqLeadLog>(this.CmsContext, "Lead Log for {0}".FormatWith(this.CmsContext.FormatEstimateNumber(lead)));
                viewModel.Items = this.DataSession.Fetch<BrqLeadLog>(x => x.Lead == lead, x => x.Created, true);

                return this.CmsView("ViewLeadLog", viewModel);
            }
            else
            {
                return this.ViewErrorPage("Lead not found.");
            }
        }

        /// <summary>
        /// Views the repairer log.
        /// </summary>
        /// <param name="id">The id.</param>
        /// <returns>
        /// An <see cref="ActionResult" />.
        /// </returns>
        [Authorise(WebLoginPermission.OfficeStaff)]
        [CmsContext("Imperial")]
        public ActionResult ViewRepairerLog(int id)
        {
            var repairer = this.DataSession.First<BrqRepairer>(id);

            if (repairer != null)
            {
                var viewModel = new ListViewModel<BrqRepairerLog>(this.CmsContext, "Repairer Log for {0}".FormatWith(repairer.Name));
                viewModel.Items = this.DataSession.Fetch<BrqRepairerLog>(x => x.Repairer == repairer, x => x.Created, true);

                return this.CmsView("ViewRepairerLog", viewModel);
            }
            else
            {
                return this.ViewErrorPage("Repairer not found.");
            }
        }

        /// <summary>
        /// Allows the billing to be run manually.
        /// </summary>
        /// <returns>
        /// An <see cref="ActionResult" />.
        /// </returns>
        [Authorise(WebLoginPermission.OfficeStaff)]
        [CmsContext("Imperial")]
        public ActionResult Billing()
        {
            ViewBag.SelectedMenuItem = "Billing";
            ViewBag.SelectedAccountTab = "BRQ";

            var viewModel = new ViewModel(this.CmsContext, "Billing");

            return this.View(viewModel);
        }

        /// <summary>
        /// Allows the billing to be run manually.
        /// </summary>
        /// <returns>
        /// An <see cref="ActionResult" />.
        /// </returns>
        [HttpPost]
        [Authorise(WebLoginPermission.OfficeStaff)]
        [CmsContext("Imperial")]
        public ActionResult ConvertTrailRepairers()
        {
            var repairersConverted = new Billing(this.CmsContext)
                .ActivateExpiredTrailRepairers();

            return this.JsonNetResult(new { result = true, total = repairersConverted });
        }

        /// <summary>
        /// Allows the billing to be run manually.
        /// </summary>
        /// <returns>
        /// An <see cref="ActionResult" />.
        /// </returns>
        [HttpPost]
        [Authorise(WebLoginPermission.OfficeStaff)]
        [CmsContext("Imperial")]
        public ActionResult CreateInvoices()
        {
            var exportedInvoices = new Billing(this.CmsContext)
                .ExportBillingData();

            return this.JsonNetResult(new { result = true, total = exportedInvoices });
        }

        /// <summary>
        /// Adds the customer postcode to the repairer.
        /// </summary>
        /// <param name="customer">The customer.</param>
        /// <param name="repairer">The repairer.</param>
        private void AddCustomerPostcodeToRepairer(Customer customer, BrqRepairer repairer)
        {
            if (this.DataSession != null)
            {
                var postcode = customer.PostCode.GetStringUpTo(' ');

                var postcodeEntry = this.DataSession.First<BrqRepairerPostCode>(x => x.PostCode == postcode);

                if (!postcodeEntry.IsAssigned)
                {
                    postcodeEntry.AssignTo(this.CmsContext.Session, repairer, this.CmsContext.Config.BrqPostCodeDefaultCharge);
                }
            }
        }

        /// <summary>
        /// Adds the repairer to current login.
        /// </summary>
        /// <param name="repairer">The repairer.</param>
        private void AddRepairerToCurrentLogin(BrqRepairer repairer)
        {
            this.CmsContext.Session.Login.Repairer = repairer;
        }

        /// <summary>
        /// Assigns the post code to repairer.
        /// </summary>
        /// <param name="repairer">The repairer.</param>
        /// <param name="postCode">The post code.</param>
        /// <param name="charge">The charge.</param>
        /// <returns>
        ///   <c>true</c> if the postcode was added; otherwise <c>false</c>.
        /// </returns>
        private MethodResult AssignPostCodeToRepairer(BrqRepairer repairer, string postCode, decimal charge)
        {
            var postCodeLookup = this.DataSession.First<BrqRepairerPostCode>(x => x.PostCode == postCode);

            if (postCodeLookup == null)
            {
                // The post code was not found.
                return MethodResult.Failure("{0} is not a valid postcode.".FormatWith(postCode));
            }
            else
            {
                if (postCodeLookup.IsAssigned)
                {
                    if (postCodeLookup.Repairer == repairer)
                    {
                        postCodeLookup.Charge = charge;
                        postCodeLookup.SaveOrUpdate(this.DataSession);

                        return MethodResult.Successful("Postcode {0} is already assigned to this account.".FormatWith(postCode));
                    }
                    else
                    {
                        return MethodResult.Failure("The postcode {0} is assigned to {1}.".FormatWith(postCode, postCodeLookup.Repairer.Customer.Id));
                    }
                }
                else
                {
                    // Assign the postcode to the repairer
                    postCodeLookup.AssignTo(this.CmsContext.Session, repairer, charge);
                    postCodeLookup.SaveOrUpdate(this.DataSession);

                    return MethodResult.Successful("Postcode {0} has been assigned to the account.".FormatWith(postCode));
                }
            }
        }

        /// <summary>
        /// Overrides the current theme.
        /// </summary>
        /// <param name="id">The id.</param>
        private void OverrideTheme(string id)
        {
            var repairer = this.LoadRepairer(id);

            if (repairer != null)
            {
                this.OverrideTheme(repairer);
            }
        }

        /// <summary>
        /// Overrides the theme.
        /// </summary>
        /// <param name="repairer">The repairer.</param>
        private void OverrideTheme(BrqRepairer repairer)
        {
            if (repairer != null && repairer.HasLink)
            {
                //// TODO: Fix -> this.CmsContext.Session.Repairer = repairer;
                return;
            }

            this.ResetTheme();
        }

        /// <summary>
        /// Creates a theme for the specified repairer.
        /// </summary>
        /// <param name="repairer">The repairer.</param>
        /// <returns>
        ///   <c>true</c> if the theme was created; otherwise <c>false</c>.
        /// </returns>
        private bool CreateRepairerThemeFor(BrqRepairer repairer)
        {
            var themePath = this.Server.MapPath("/Themes/BRQ/{0}".FormatWith(repairer.Customer.Id));

            if (!Directory.Exists(themePath))
            {
                Directory.CreateDirectory(themePath);

                return true;
            }

            return false;
        }

        /// <summary>
        /// Allowed the type of the content.
        /// </summary>
        /// <param name="contextType">Type of the context.</param>
        /// <returns>
        /// <c>true</c> if the content type is allowed; otherwise <c>false</c>.
        /// </returns>
        private bool AllowedContentType(string contextType)
        {
            return true;
        }

        /// <summary>
        /// Loads the repairer.
        /// </summary>
        /// <param name="accountNumber">The code.</param>
        /// <returns>
        /// A <see cref="BrqRepairer" /> if found; otherwise null.
        /// </returns>
        private BrqRepairer LoadRepairer(string accountNumber)
        {
            if (accountNumber.HasValue())
            {
                var customer = this.DataSession.First<Customer>(accountNumber);

                if (customer != null)
                {
                    return this.DataSession.First<BrqRepairer>(x => x.Customer == customer);
                }
            }

            return null;
        }

        /// <summary>
        /// Gets the manufacturers.
        /// </summary>
        /// <returns>
        /// A list of vehicle manufacturers.
        /// </returns>
        private IList<VehicleManufacturer> GetManufacturers()
        {
            var makes = this.DataSession.Fetch<VehicleManufacturer>();

            var filteredManufacturerCodes = this.CmsContext.Config.GetFilteredManufacturers();

            if (filteredManufacturerCodes.Count > 0)
            {
                makes = makes.Where(x => filteredManufacturerCodes.Contains(x.Id)).ToList();
            }

            var hiddenManufacturers = this.CmsContext.Config.GetHiddenManufacturersList();

            foreach (var manufacturerCode in hiddenManufacturers)
            {
                var matches = makes.Where(x => x.Id.ContainsIgnoreCase(manufacturerCode));
                makes = makes.Except(matches).ToList();
            }

            return makes;
        }

        /// <summary>
        /// Views the error page.
        /// </summary>
        /// <param name="errors">The errors.</param>
        /// <returns>
        /// An <see cref="ActionResult" />.
        /// </returns>
        private ActionResult ViewErrorPage(params string[] errors)
        {
            var viewModel = new ViewModel(this.CmsContext, "Sorry");
            errors.Each(x => viewModel.AddError(x));

            return this.View("Error", viewModel);
        }

        /// <summary>
        /// Repairers thank you.
        /// </summary>
        /// <param name="message">The message.</param>
        /// <returns>
        /// An <see cref="ActionResult"/>.
        /// </returns>
        private ActionResult RepairerThanks(string message)
        {
            var viewModel = new ViewModel<string>(this.CmsContext, "Thank You");
            viewModel.Item = message;

            return this.View("RepairerThanks", viewModel);
        }

        /// <summary>
        /// Unassigns the repairer from the specified postcode.
        /// </summary>
        /// <param name="postCode">The post code.</param>
        /// <returns>
        ///   <c>true</c> if the postcode was assigned; otherwise <c>false</c>.
        /// </returns>
        private bool UnassignPostcode(string postCode)
        {
            var postCodeLookup = this.DataSession.First<BrqRepairerPostCode>(x => x.PostCode == postCode);

            if (postCodeLookup != null)
            {
                var repairer = postCodeLookup.Repairer;

                postCodeLookup.Unassign(this.CmsContext.Session);
                postCodeLookup.SaveOrUpdate(this.DataSession);

                // Add an entry to the log.
                this.CmsContext.AddToRepairerLog(repairer, BrqRepairerLogType.PostcodesRemoved, "Postcodes '{0}' successfully removed from {1}.".FormatWith(postCode, repairer.Name));

                return true;
            }

            return false;
        }

        /// <summary>
        /// Displays the leads for the current repairer at the specified status.
        /// </summary>
        /// <param name="status">The status of the estimates to show.</param>
        /// <returns>
        /// An <see cref="ActionResult"/>.
        /// </returns>
        private ActionResult LoadRepairerLeadsView(EstimateStatus status = EstimateStatus.Outstanding)
        {
            ViewBag.SelectedMenuItem = "RepairerLeads{0}".FormatWith(status);
            ViewBag.SelectedAccountTab = "BRQ";

            var repairer = this.CmsContext.Session.Login.Repairer;

            if (repairer != null)
            {
                var viewModel = new ListViewModel<BrqLead>(this.CmsContext);
                viewModel.Add("Status", status);
                viewModel.PageTitle = "{0} Leads for {1}".FormatWith(BrqLead.GetFriendlyStatus(status), repairer.Name);
                viewModel.Items = this.DataSession.Fetch<BrqLead>(x => x.Repairer == repairer && x.Status == status)
                    .OrderByDescending(x => x.Created)
                    .ToList();

                return this.View("RepairerLeads", viewModel);
            }
            else
            {
                return this.ViewErrorPage("Your login is not attached to a BodyRepairQuote.com Repairer. Please call us on {0}.".FormatWith(this.CmsContext.WebSite.Phone));
            }
        }
    }
}
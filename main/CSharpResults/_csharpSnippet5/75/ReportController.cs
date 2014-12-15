using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;
using System.Web.Mvc;
using WFS.WebSite4.Controllers;
using WFS.Contract.Enums;
using WFS.Domain.Managers;
using WFS.Framework.Responses;
using WFS.Framework;
using WFS.Contract;
using System.Configuration;
using WFS.Contract.ReqResp;
using WFS.WebSite4.Areas.Admin.Models;
using mhutchins.com.system.Web.CustomActions;

namespace WFS.WebSite4.Areas.Admin.Controllers
{
    [RoleAuthorize(WFSUserTypeEnum.Admin)]
    public class ReportController : BaseController
    {
        private readonly AdminReportingManager _rptManager;
        private readonly EmailManager _emailManager;
        private readonly GiftCardManager _giftCardManager;
        private readonly VendorManager _vendorManager;
        private readonly OrderManager _orderManager;

        public ReportController(AdminReportingManager rptManager, EmailManager emailManager, GiftCardManager giftCardManager, VendorManager vendorManager, OrderManager orderManager)
        {
            _rptManager = rptManager;
            _emailManager = emailManager;
            _giftCardManager = giftCardManager;
            _vendorManager = vendorManager;
            _orderManager = orderManager;
        }

        public ActionResult Index()
        {
            return View();
        }

        private enum AddToDates
        {
            None,
            School,
            Vendor
        }

        private ActionResult ReturnToAndFromDatesControl(ToAndFromDatesModel m, AddToDates addToDates)
        {
            var uiresponse = new UIResponse<ToAndFromDatesModel> { Subject = m };

            switch (addToDates)
            {
                case AddToDates.School:
                    LoadOrganizations(OrganizationTypeEnum.School);
                    uiresponse.HtmlResult = RenderPartialViewToString("ToAndFromDatesWithSchool", m);
                    break;
                case AddToDates.Vendor:
                    LoadOrganizations(OrganizationTypeEnum.Vendor);
                    uiresponse.HtmlResult = RenderPartialViewToString("ToAndFromDatesWithVendor", m);
                    break;
                default:
                    uiresponse.HtmlResult = RenderPartialViewToString("ToAndFromDates", m);
                    break;

            }

            return Json(uiresponse, JsonRequestBehavior.AllowGet);
        }
        private ActionResult ReturnVendorPickerControl(string buttonText, string postUrl)
        {
            var m = new VendorPickerModel() { ButtonText = buttonText, PostUrl = postUrl };
            var resp = _vendorManager.GetVendorList(new GetOrganizationByTypeListRequest() { Type = OrganizationTypeEnum.Vendor, DataRequest = ActiveDataRequestEnum.All });

            resp.Organizations.ForEach(x => m.Vendors.Add(new SelectListItem() { Text = x.Name, Value = x.OrganizationId.ToString() }));

            var uiresponse = new UIResponse<VendorPickerModel>();
            uiresponse.Subject = m;
            uiresponse.HtmlResult = RenderPartialViewToString("VendorPicker", m);

            return Json(uiresponse, JsonRequestBehavior.AllowGet);
        }
        private void LoadOrganizations(OrganizationTypeEnum type)
        {
            var respSchool = _vendorManager.GetVendorList(new GetOrganizationByTypeListRequest() { Type = type, DataRequest = ActiveDataRequestEnum.All });

            var list = new List<SelectListItem>();

            respSchool.Organizations.ForEach(x => list.Add(new SelectListItem() { Value = x.OrganizationId.ToString(), Text = x.Name }));
            list.Insert(0, new SelectListItem() { Value = "0", Text = "All", Selected = true });

            ViewData["OrganizationList"] = list;
        }

        #region GiftCards

        public ActionResult GetUnclaimedGiftCards()
        {
            var resp = _rptManager.GetUnclaimedGiftCardList();

            var uiresponse = new UIResponse<List<GiftCard>>();
            uiresponse.Subject = resp.Value;
            uiresponse.Merge(resp);
            uiresponse.HtmlResult = RenderPartialViewToString("UnclaimedGiftCardsReport", resp.Value);

            return Json(uiresponse, JsonRequestBehavior.AllowGet);
        }
        public ActionResult ResendGiftCardClaim(int giftCardId)
        {
            var uiresponse = new UIResponse<List<GiftCard>>();

            try
            {
                GiftCardEmails(giftCardId);
                uiresponse.HtmlResult = "Claim Information Resent Successfully";
            }
            catch (Exception ex)
            {
                uiresponse.HtmlResult = "Resend Failed: " + ex.Message.ToString();
            }

            return Json(uiresponse, JsonRequestBehavior.AllowGet);
        }
        private void GiftCardEmails(int giftCardId)
        {
            var gcResp = _giftCardManager.GetGiftCardByById(new GetGiftCardByIdRequest() { GiftCardId = giftCardId });
            var email = new Email();

            //send confirmation
            var m = new WFS.WebSite4.Models.GiftCardEmailModel()
            {
                UserFirstName = gcResp.Value.Buyer.FirstName,
                UserLastName = gcResp.Value.Buyer.LastName,
                OrderDate = gcResp.Value.CreatedOn,
                GiftCardAmount = gcResp.Value.GiftCardAmount,
                RecipientEmail = gcResp.Value.RecipEmail,
                RecipientFirstName = gcResp.Value.RecipFirstName,
                RecipientLastName = gcResp.Value.RecipLastName,
                GiftNote = gcResp.Value.Note,
                LoginUrl = String.Format("{0}/{1}/{2}", RootUrl, "ClaimLogin", gcResp.Value.ClaimID.ToString()),
                CreateAccountUrl = String.Format("{0}/{1}/{2}", RootUrl, "ClaimRegister", gcResp.Value.ClaimID.ToString()),
            };

            //send email to recipient
            email = new Email();
            email.To = gcResp.Value.RecipEmail;
            email.Subject = "Wholesome Food Services - You Have received a Gift Card";
            email.Body = RenderPartialViewToString((gcResp.Value.ReceiverUserId.HasValue) ? "~/Views/Email/ClaimGiftCardWithAccount.cshtml" : "~/Views/Email/ClaimGiftCardNoAccount.cshtml", m);
            _emailManager.SendEmail(email);
        }
        #endregion

        #region NewUsers
        public ActionResult NewUsersReport()
        {
            var m = new ToAndFromDatesModel()
            {
                PostUrl = "/Admin/NewUsersReport"
            };

            return ReturnToAndFromDatesControl(m, AddToDates.School);
        }

        [HttpPost]
        public ActionResult NewUsersReport(ToAndFromDatesModel model)
        {
            var resp = _rptManager.NewUsersReport(new NewUsersReportRequest()
                {
                    StartDate = DateTime.Parse(model.StartDate).Date,
                    EndDate = DateTime.Parse(model.EndDate).Date,
                    SchoolId = Convert.ToInt32(model.SelectedId)
                });

            var m = new NewUsersViewModel()
            {
                StartDate = DateTime.Parse(model.StartDate).Date.ToString("MM-dd-yyyy"),
                EndDate = DateTime.Parse(model.EndDate).Date.ToString("MM-dd-yyyy"),
                Users = resp.Values,
                SchoolId = Convert.ToInt32(model.SelectedId)
            };

            var uiresponse = new UIResponse<NewUsersViewModel> { Subject = m };
            uiresponse.Merge(resp);
            uiresponse.HtmlResult = RenderPartialViewToString("NewUsersReport", uiresponse.Subject);

            return Json(uiresponse, JsonRequestBehavior.AllowGet);
        }

        public ActionResult ExportNewUsersReport(string fromReportDate, string toReportDate, int schoolId)
        {
            var fromDate = DateTime.Parse(fromReportDate).Date;
            var toDate = DateTime.Parse(toReportDate).Date;

            var resp = _rptManager.NewUsersReport(new NewUsersReportRequest()
            {
                StartDate = fromDate,
                EndDate = toDate,
                SchoolId = schoolId
            });

            var tList = new List<NewUsersExportModel>();
            foreach (var item in resp.Values)
            {
                tList.Add(new NewUsersExportModel()
                {
                    Name = string.Format("{0}, {1}", item.User.LastName, item.User.FirstName),
                    Email = item.User.EmailAddress,
                    Schools = String.Join(", ", item.Schools.ToArray()),
                    CreatedOn = item.User.CreatedOn.ToShortDateString(),
                });
            }

            return new ExcelResult<NewUsersExportModel>(tList, String.Format("{0} - NewUsersReport.xlsx", fromReportDate), "New Users");
        }
        #endregion

        #region Donations
        public ActionResult DonationByDateRangeReport()
        {
            var m = new ToAndFromDatesModel()
            {
                PostUrl = "/Admin/DonationByDateRangeReport"
            };

            return ReturnToAndFromDatesControl(m, AddToDates.None);
        }
        [HttpPost]
        public ActionResult DonationByDateRangeReport(ToAndFromDatesModel model)
        {
            var fromDate = DateTime.Parse(model.StartDate).Date;
            var toDate = DateTime.Parse(model.EndDate).Date;

            var resp = _rptManager.GetDonationsByDateRange(new DonationsByDateRangeRequest()
            {
                StartDate = fromDate,
                EndDate = toDate,
            });

            var m = new DonationsByDateRangeViewModel()
            {
                StartDate = fromDate.ToString("MM-dd-yyyy"),
                EndDate = toDate.ToString("MM-dd-yyyy"),
                Items = resp.Values
            };

            var uiresponse = new UIResponse<DonationsByDateRangeViewModel>();
            uiresponse.Subject = m;
            uiresponse.Merge(resp);
            uiresponse.HtmlResult = RenderPartialViewToString("DonationsByDateRange", m);

            return BigJson(uiresponse);
        }
        public ActionResult ExportDonationByDateRangeReport(string fromReportDate, string toReportDate)
        {
            var fromDate = DateTime.Parse(fromReportDate).Date;
            var toDate = DateTime.Parse(toReportDate).Date;

            var resp = _rptManager.GetDonationsByDateRange(new DonationsByDateRangeRequest()
            {
                StartDate = fromDate,
                EndDate = toDate,
            });

            var tList = new List<DonationsByDateRangeExportModel>();
            foreach (var item in resp.Values)
            {
                tList.Add(new DonationsByDateRangeExportModel()
                {
                    SchoolName = item.SchoolName,
                    ParentName = item.ParentName,
                    OrderedFor = item.OrderedFor,
                    FoodItem = item.FoodItemName,
                    Amount = Math.Round(item.DonationAmount, 2),
                    LunchDate = item.LunchDate.ToShortDateString()
                });
            }

            return new ExcelResult<DonationsByDateRangeExportModel>(tList, String.Format("{0} - DonationsByDate.xlsx", fromReportDate), "Donations By Date");
        }
        #endregion

        #region Food Item Summary
        public ActionResult FoodItemSummary()
        {
            return ReturnVendorPickerControl("Get Food Items", "/Admin/FoodItemSummary");
        }

        [HttpPost]
        public ActionResult FoodItemSummary(VendorPickerModel model)
        {
            var req = new FoodItemReportRequest();

            if (model.SelectedVendor != "0")
                req.VendorId = Convert.ToInt32(model.SelectedVendor);

            var resp = _rptManager.GetFoodItemReport(req);

            var m = new FoodItemReportViewModel()
            {
                Items = resp.Values
            };

            var uiresponse = new UIResponse<FoodItemReportViewModel>();
            uiresponse.Subject = m;
            uiresponse.Merge(resp);
            uiresponse.HtmlResult = RenderPartialViewToString("FoodItemReport", m);

            return BigJson(uiresponse);

        }

        #endregion

        #region Order Summary
        public ActionResult OrderSummary()
        {
            var m = new ToAndFromDatesModel()
            {
                PostUrl = "/Admin/OrderSummary"
            };

            return ReturnToAndFromDatesControl(m, AddToDates.School);
        }

        [HttpPost]
        public ActionResult OrderSummary(ToAndFromDatesModel model)
        {
            return GetOrderSummary(model);
        }

        [HttpPost]
        public ActionResult CancelOrder(ToAndFromDatesModel model, int orderId, int profileId, string lunchDate)
        {
            var dt = DateTime.Parse(lunchDate).Date;

            var resp = _orderManager.CancelOrderForDayById(new CancelOrderForDayByIdRequest()
            {
                OrderId = orderId,
                ProfileId = profileId,
                LunchDate = dt,
                SkipCutoffCheck = true
            });

            return GetOrderSummary(model);

        }
        private ActionResult GetOrderSummary(ToAndFromDatesModel model)
        {
            var resp = _rptManager.GetOrderHistoryByDate(new GetOrderSummaryByDateRequest()
            {
                StartDate = DateTime.Parse(model.StartDate).Date,
                EndDate = DateTime.Parse(model.EndDate).Date,
                SchoolId = Convert.ToInt32(model.SelectedId)
            });

            var m = new OrderSummaryViewModel();

            foreach (var item in resp.Values.Select(x => new { id = x.UserId, username = x.UserFullName }).Distinct())
            {
                var userGroup = new OrderSummaryItemViewModel()
                {
                    UserFullName = item.username,
                    Items = resp.Values.Where(x => x.UserId == item.id).OrderBy(x => x.LunchDate).ThenBy(x => x.ProfileName).ToList()
                };

                m.Users.Add(userGroup);
            }

            var uiresponse = new UIResponse<OrderSummaryViewModel>();
            uiresponse.Subject = m;
            uiresponse.Merge(resp);
            uiresponse.HtmlResult = RenderPartialViewToString("OrderSummaryReport", m);

            return BigJson(uiresponse);
        }

        #endregion

        #region Vendor Summary By School
        public ActionResult VendorSummaryByDateRangeReport()
        {
            var m = new ToAndFromDatesModel()
            {
                PostUrl = "/Admin/VendorSummaryByDateRangeReport"
            };

            return ReturnToAndFromDatesControl(m, AddToDates.None);
        }
        [HttpPost]
        public ActionResult VendorSummaryByDateRangeReport(ToAndFromDatesModel model)
        {
            var fromDate = DateTime.Parse(model.StartDate).Date;
            var toDate = DateTime.Parse(model.EndDate).Date;

            var resp = _rptManager.GetVendorSummaryReportByDates(new VendorSummaryReportRequest()
            {
                StartDate = fromDate,
                EndDate = toDate,
            });

            var m = new VendorSummaryViewModel()
            {
                StartDate = fromDate.ToString("MM-dd-yyyy"),
                EndDate = toDate.ToString("MM-dd-yyyy"),
                Items = resp.Values
            };

            var uiresponse = new UIResponse<VendorSummaryViewModel>();
            uiresponse.Subject = m;
            uiresponse.Merge(resp);
            uiresponse.HtmlResult = RenderPartialViewToString("VendorSummaryByDateRange", m);

            return BigJson(uiresponse);
        }
        public ActionResult ExportVendorSummaryByDateRangeReport(string fromReportDate, string toReportDate)
        {
            var fromDate = DateTime.Parse(fromReportDate).Date;
            var toDate = DateTime.Parse(toReportDate).Date;

            var resp = _rptManager.GetVendorSummaryReportByDates(new VendorSummaryReportRequest()
            {
                StartDate = fromDate,
                EndDate = toDate,
            });

            var rptTitle = String.Format("Vendor Summary from {0}  to {1} by School", fromReportDate, toReportDate);

            var tList = new List<VendorSummaryByDateRangeExportModel>();
            foreach (var school in resp.Values.OrderBy(x => x.SchoolName))
            {
                foreach (var vendor in school.VendorItems.OrderBy(x => x.VendorName))
                {
                    tList.Add(new VendorSummaryByDateRangeExportModel()
                    {
                        SchoolName = school.SchoolName,
                        VendorName = vendor.VendorName,
                        TotalCharged = vendor.TotalPrice,
                        TotalCost = vendor.TotalCost,
                        ReportTitle = rptTitle
                    });
                }
            }

            return new ExcelResult<VendorSummaryByDateRangeExportModel>(tList, String.Format("{0} - VendorSummaryBySchool.xlsx", fromReportDate), "Vendor Summary By School");
        }
        #endregion

        #region Vendor Invoice By Date
        public ActionResult VendorInvoiceByDateRangeReport()
        {
            var m = new ToAndFromDatesModel()
            {
                PostUrl = "/Admin/VendorInvoiceByDateRangeReport"
            };

            return ReturnToAndFromDatesControl(m, AddToDates.Vendor);
        }

        #endregion

        #region Free & Reduced Lunch Summary
        public ActionResult FreeReducedSummary()
        {
            var m = new ToAndFromDatesModel()
            {
                PostUrl = "/Admin/FreeReducedSummary"
            };

            return ReturnToAndFromDatesControl(m, AddToDates.None);
        }

        [HttpPost]
        public ActionResult FreeReducedSummary(ToAndFromDatesModel model)
        {
            var fromDate = DateTime.Parse(model.StartDate).Date;
            var toDate = DateTime.Parse(model.EndDate).Date;

            var resp = _rptManager.FreeReducedByDateRange(new FreeReducedByDateRangeRequest()
            {
                StartDate = fromDate,
                EndDate = toDate,
            });

            var m = new FreeReducedByDateRangeViewModel()
            {
                StartDate = fromDate.ToString("MM-dd-yyyy"),
                EndDate = toDate.ToString("MM-dd-yyyy"),
                Items = resp.Values
            };

            var uiresponse = new UIResponse<FreeReducedByDateRangeViewModel> { Subject = m };
            uiresponse.Merge(resp);
            uiresponse.HtmlResult = RenderPartialViewToString("FreeReducedByDateRange", m);

            return BigJson(uiresponse);
        }
        public ActionResult ExportFreeReducedSummary(string fromReportDate, string toReportDate)
        {
            var fromDate = DateTime.Parse(fromReportDate).Date;
            var toDate = DateTime.Parse(toReportDate).Date;

            var resp = _rptManager.FreeReducedByDateRange(new FreeReducedByDateRangeRequest()
            {
                StartDate = fromDate,
                EndDate = toDate,
            });

            var tList = resp.Values.Select(item => new FreeReducedByDateExportModel()
            {
                StudentName = item.LastNameFirst,
                LunchDate = item.LunchDate.ToShortDateString(),
                FoodItem = String.Format("{0}/{1}", item.FoodItem, item.VendorName),
                Orignal = Math.Round(item.OriginalPrice, 2),
                Charged = Math.Round(item.PriceCharged, 2),
                Credit = Math.Round(item.Credit, 2),
            }).ToList();

            return new ExcelResult<FreeReducedByDateExportModel>(tList, String.Format("{0} - FreeReducedLunchesByDate.xlsx", fromReportDate), "Free & Reduced Lunches By Date");
        }
        #endregion
    }
}

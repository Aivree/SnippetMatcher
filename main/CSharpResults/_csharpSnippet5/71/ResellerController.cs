// <copyright file="ResellerController.cs" company="Imperial">
// Copyright (c) 2012 All Rights Reserved
// </copyright>
namespace Imperial.CrashRepairParts.Cms.Controllers
{
    using System;
    using System.Collections.Generic;
    using System.Collections.Specialized;
    using System.Linq;
    using System.Web;
    using System.Web.Mvc;
    using Imperial.CrashRepairParts.Core;
    using Imperial.CrashRepairParts.Exports;
    using Imperial.CrashRepairParts.Html;
    using Imperial.CrashRepairParts.Messaging;
    using Imperial.CrashRepairParts.Types;
    using Imperial.CrashRepairParts.ViewModels;
    using Imperial.Data.Mvc;
    using Imperial.Net;
    using Imperial.Web.ActionResults;
    using Imperial.Web.ViewModels;

    /// <summary>
    /// Reseller Controller.
    /// </summary>
    public class ResellerController : CrashRepairPartsController
    {
        /// <summary>
        /// Indexes this instance.
        /// </summary>
        /// <returns>
        /// An <see cref="ActionResult."/>
        /// </returns>
        [CmsContext("Imperial")]
        public ActionResult Index()
        {
            if (!this.CmsContext.Session.IsAuthenticated)
            {
                return this.RedirectToAction("Login", "Session", new { redirect = "Reseller/Index" });
            }
            else
            {
                if (this.CmsContext.Session.IsResellerAdmin)
                {
                    var viewModel = new ViewModel(this.CmsContext, "Reseller Site Settings");

                    return this.View(viewModel);
                }
                else
                {
                    throw new HttpException(403, "Forbidden");
                }
            }
        }

        /// <summary>
        /// Shows the reseller statistics about their site.
        /// </summary>
        /// <param name="month">The month.</param>
        /// <param name="year">The year.</param>
        /// <returns>
        /// An <see cref="ActionResult."/>
        /// </returns>
        [CmsContext("Imperial")]
        public ActionResult Stats(int month = 0, int year = 0)
        {
            if (!this.CmsContext.Session.IsAuthenticated)
            {
                return this.RedirectToAction("Login", "Session", new { redirect = "Reseller/Index" });
            }
            else
            {
                if (this.CmsContext.Session.IsResellerAdmin)
                {
                    // Calculate the statistics
                    var viewModel = new ResellerStatsViewModel(this.CmsContext, "Statistics");
                    viewModel.Month = month == 0 || month > 12 ? DateTime.Now.Month : month;
                    viewModel.MonthName = (MonthOfYear)viewModel.Month;
                    viewModel.Year = year == 0 ? DateTime.Now.Year : year;
                    viewModel.PageTitle = "Reseller Site Statistics for {0} {1}".FormatWith(viewModel.MonthName, viewModel.Year);

                    // Calculate the necessary values.
                    var startDate = new DateTime(viewModel.Year, viewModel.Month, 1);
                    var endDate = startDate.LastDayOfMonth();

                    // Get the submitted reseller orders.
                    var submittedOrders = this.DataSession.Fetch<WebBasket>(x => x.WebSite == this.CmsContext.WebSite && x.Status == WebBasketStatus.Complete && (x.Created >= startDate && x.Created <= endDate));
                    var ordersInBasket = this.DataSession.Fetch<WebBasket>(x => x.WebSite == this.CmsContext.WebSite && x.Status == WebBasketStatus.Created && (x.Created >= startDate && x.Created <= endDate));

                    viewModel.TotalOrderValue = submittedOrders.Sum(x => x.SubTotal);
                    viewModel.TotalOrderVATValue = submittedOrders.Sum(x => x.VatTotal);
                    viewModel.TotalOrderValueIncludingVAT = submittedOrders.Sum(x => x.Total);
                    viewModel.TotalOrderValueFromImperial = submittedOrders.Sum(x => x.GetImperialOrderTotal());
                    viewModel.TotalOrderValueProvidedFromStock = submittedOrders.Sum(x => x.GetProvidedFromStockTotal());
                    viewModel.TotalOrderValueCancelled = submittedOrders.Sum(x => x.GetCancelledLinesTotal());
                    viewModel.TotalOrdersInBasket = ordersInBasket.Sum(x => x.SubTotal);

                    return this.View(viewModel);
                }
                else
                {
                    throw new HttpException(403, "Forbidden");
                }
            }
        }

        /// <summary>
        /// Customer Logins.
        /// </summary>
        /// <returns>
        /// An <see cref="ActionResult"/>.
        /// </returns>
        [CmsContext("Imperial")]
        public ActionResult CustomerLogins()
        {
            if (!this.CmsContext.Session.IsAuthenticated)
            {
                return this.RedirectToAction("Login", "Session", new { redirect = "Reseller/CustomerLogins" });
            }
            else
            {
                if (this.CmsContext.Session.IsResellerAdmin)
                {
                    var viewModel = new ListViewModel<WebLogin>(this.CmsContext, "Customer Logins");
                    viewModel.Items = this.CmsContext.DataSession.Fetch<WebLogin>(x => x.Application == this.CmsContext.WebSite);

                    return this.View(viewModel);
                }
                else
                {
                    throw new HttpException(403, "Forbidden");
                }
            }
        }

        /// <summary>
        /// Reseller Depots.
        /// </summary>
        /// <returns>
        /// An <see cref="ActionResult"/>.
        /// </returns>
        [CmsContext("Imperial")]
        public ActionResult Depots()
        {
            if (!this.CmsContext.Session.IsAuthenticated)
            {
                return this.RedirectToAction("Login", "Session", new { redirect = "Reseller/Depots" });
            }
            else
            {
                if (this.CmsContext.Session.IsResellerAdmin)
                {
                    var viewModel = new ListViewModel<Depot>(this.CmsContext, "Branches");
                    viewModel.Items = this.DataSession.Fetch<Depot>(x => x.WebSite == this.CmsContext.WebSite);

                    return this.View("Depots", viewModel);
                }
                else
                {
                    throw new HttpException(403, "Forbidden");
                }
            }
        }

        /// <summary>
        /// Manages Product Groups.
        /// </summary>
        /// <returns>
        /// An <see cref="ActionResult."/>.
        /// </returns>
        [CmsContext("Imperial")]
        public ActionResult ProductGroupManager()
        {
            if (!this.CmsContext.Session.IsAuthenticated)
            {
                return this.RedirectToAction("Login", "Session", new { redirect = "Reseller/Index" });
            }
            else
            {
                if (this.CmsContext.Session.IsResellerAdmin)
                {
                    ////var categories = this.CmsContext.GetProductCategorys();
                    var viewModel = new ListViewModel<ProductCategoryLookup>(this.CmsContext, "Edit Product Groups");
                    viewModel.Items = this.CmsContext.GetProductCategorys(); ////this.DataSession.Fetch<ProductCategory>();

                    return this.View(viewModel);
                }
                else
                {
                    throw new HttpException(403, "Forbidden");
                }
            }
        }

        /// <summary>
        /// Orders the manager.
        /// </summary>
        /// <param name="depot">The depot.</param>
        /// <param name="status">The status.</param>
        /// <returns>
        /// An <see cref="ActionResult"/>.
        /// </returns>
        /// <exception cref="System.Web.HttpException">403 Forbidden</exception>
        [CmsContext("Imperial")]
        public ActionResult OrderManager(int depot = 0, WebBasketStatus status = WebBasketStatus.Created)
        {
            if (!this.CmsContext.Session.IsAuthenticated)
            {
                return this.RedirectToAction("Login", "Session", new { redirect = "Reseller/Index" });
            }
            else
            {
                if (this.CmsContext.Session.IsResellerAdmin)
                {
                    var viewModel = new ResellerOrderManagerViewModel(this.CmsContext, "Order Manager", status);

                    if (depot > 0)
                    {
                        viewModel.Depot = this.DataSession.First<Depot>(depot);
                    }
                    else
                    {
                        viewModel.Depot = this.DataSession.First<Depot>(x => x.WebSite == this.CmsContext.WebSite);
                    }
                    
                    viewModel.Items = this.DataSession.Fetch<WebBasket>(x => x.WebSite == this.CmsContext.WebSite && (x.Depot == viewModel.Depot || x.Depot == null) && x.Status == status).ToList();
                    viewModel.SelectDepot = new MvcHtmlString(Select.Depot(this.CmsContext, "depot", viewModel.Depot, true));
                    viewModel.SelectOrderStatus = new MvcHtmlString(Select.ResellerOrderStatus(this.CmsContext, "status", status, true));

                    return this.View("OrderManager", viewModel);
                }
                else
                {
                    throw new HttpException(403, "Forbidden");
                }
            }
        }

        /// <summary>
        /// Manufacturers the manager.
        /// </summary>
        /// <returns>
        /// An <see cref="ActionResult."/>.
        /// </returns>
        [CmsContext("Imperial")]
        public ActionResult ManufacturerManager()
        {
            if (!this.CmsContext.Session.IsAuthenticated)
            {
                return this.RedirectToAction("Login", "Session", new { redirect = "Reseller/Index" });
            }
            else
            {
                if (this.CmsContext.Session.IsResellerAdmin)
                {
                    var viewModel = new ListViewModel<VehicleManufacturer>(this.CmsContext, "Manufacturer Manager");
                    viewModel.Items = new List<VehicleManufacturer>();

                    return this.View(viewModel);
                }
                else
                {
                    throw new HttpException(403, "Forbidden");
                }
            }
        }

        /// <summary>
        /// Edits the product area uplift.
        /// </summary>
        /// <param name="id">The product group id.</param>
        /// <returns>
        /// An <see cref="ActionResult."/>.
        /// </returns>
        [CmsContext("Imperial")]
        public ActionResult ProductGroupEditor(string id)
        {
            if (!this.CmsContext.Session.IsAuthenticated)
            {
                return this.RedirectToAction("Login", "Session", new { redirect = "Reseller/Index" });
            }
            else
            {
                if (this.CmsContext.Session.IsResellerAdmin)
                {
                    var productGroup = this.CmsContext.GetProductCategorys().First(x => x.Name == id);

                    if (productGroup != null)
                    {
                        this.ViewBag.UpliftPercent = ProductHelper.GetProductAreaUplift(this.CmsContext, productGroup.Name.ToUpper());
                        this.ViewBag.IsHidden = ProductHelper.IsProductAreaHidden(this.CmsContext, productGroup.Name.ToUpper());
                        this.ViewBag.DeliveryCharge = ProductHelper.GetProductAreaDeliveryCharge(this.CmsContext, productGroup.Name.ToUpper());

                        var viewModel = new ViewModel<ProductCategoryLookup>(this.CmsContext, "Edit {0}".FormatWith(productGroup.Name));
                        viewModel.Item = productGroup;

                        return this.View(viewModel);
                    }

                    throw new HttpException(404, "Product Group was not found");
                }
                else
                {
                    throw new HttpException(403, "Forbidden");
                }
            }
        }

        /// <summary>
        /// Edits the product area uplift.
        /// </summary>
        /// <param name="id">The product group ID.</param>
        /// <param name="upliftPercent">The uplift percent.</param>
        /// <param name="hidden">if set to <c>true</c> [hidden].</param>
        /// <param name="deliveryCharge">The delivery charge.</param>
        /// <returns>
        /// An <see cref="ActionResult."/>.
        /// </returns>
        [HttpPost]
        [CmsContext("Imperial")]
        public ActionResult EditProductGroup(string id, decimal upliftPercent = 0, bool hidden = false, decimal deliveryCharge = 0)
        {
            if (!this.CmsContext.Session.IsAuthenticated)
            {
                return this.RedirectToAction("Login", "Session", new { redirect = "Reseller/Index" });
            }
            else
            {
                if (this.CmsContext.Session.IsResellerAdmin)
                {
                    if (id.HasValue())
                    {
                        if (upliftPercent >= 0)
                        {
                            var productGroup = this.CmsContext.GetProductCategorys().First(x => x.Name == id);

                            if (productGroup.IsNotNull())
                            {
                                ProductHelper.AddProductAreaUplift(this.CmsContext, productGroup.Name, upliftPercent);
                                ProductHelper.HideProductArea(this.CmsContext, productGroup.Name, hidden);
                                ProductHelper.SetProductAreaDeliveryCharge(this.CmsContext, productGroup.Name, deliveryCharge);
                               
                                ////this.DataSession.SaveOrUpdate(this.CmsContext.WebSite);
                               
                                return this.Redirect("/Reseller/ProductGroupManager");
                            }
                        }
                    }

                    throw new HttpException(404, "Product Area was not found");
                }
                else
                {
                    throw new HttpException(403, "Forbidden");
                }
            }
        }

        /////// <summary>
        /////// Shows a list of orders created by this depot.
        /////// </summary>
        /////// <returns>
        /////// An <see cref="ActionResult."/>.
        /////// </returns>
        ////[CmsContext("Imperial")]
        ////public ActionResult DepotOrders()
        ////{
        ////    if (!this.CmsContext.Session.IsAuthenticated)
        ////    {
        ////        return this.RedirectToAction("Login", "Session", new { redirect = "Reseller/Index" });
        ////    }
        ////    else
        ////    {
        ////        if (this.CmsContext.Session.IsReseller)
        ////        {
        ////            IList<WebResellerOrder> orders = this.DataSession.Fetch<WebResellerOrder>(x => x.Depot == this.CmsContext.Session.Login.Depot);

        ////            return this.View(orders);
        ////        }
        ////        else
        ////        {
        ////            throw new HttpException(403, "Forbidden");
        ////        }
        ////    }
        ////}

        /////// <summary>
        /////// Views the specified depot order.
        /////// </summary>
        /////// <param name="id">The id.</param>
        /////// <returns>
        /////// An <see cref="ActionResult."/>.
        /////// </returns>
        ////[CmsContext("Imperial")]
        ////public ActionResult ViewDepotOrder(int id = 0)
        ////{
        ////    if (!this.CmsContext.Session.IsAuthenticated)
        ////    {
        ////        return this.RedirectToAction("Login", "Session", new { redirect = "Reseller/Index" });
        ////    }
        ////    else
        ////    {
        ////        if (this.CmsContext.Session.IsReseller)
        ////        {
        ////            WebResellerOrder order = this.DataSession.First<WebResellerOrder>(id);
        ////            return this.View(order);
        ////        }
        ////        else
        ////        {
        ////            throw new HttpException(403, "Forbidden");
        ////        }
        ////    }
        ////}

        /// <summary>
        /// Edits the depot order.
        /// </summary>
        /// <param name="id">The id.</param>
        /// <returns>
        /// An <see cref="ActionResult."/>.
        /// </returns>
        [CmsContext("Imperial")]
        public ActionResult EditDepotOrder(int id = 0)
        {
            if (!this.CmsContext.Session.IsAuthenticated)
            {
                return this.RedirectToAction("Login", "Session", new { redirect = "Reseller/Index" });
            }
            else
            {
                if (this.CmsContext.Session.IsResellerAdmin)
                {
                    var viewModel = new ViewModel<WebBasket>(this.CmsContext, "Edit Order");
                    viewModel.Item = this.DataSession.First<WebBasket>(id);
                    ////viewModel.Item.UpdateLineStock(this.CmsContext, this.DataSession);
                    return this.View(viewModel);
                }
                else
                {
                    throw new HttpException(403, "Forbidden");
                }
            }
        }

        /// <summary>
        /// Saves the settings.
        /// </summary>
        /// <returns>
        /// An <see cref="ActionResult"/>.
        /// </returns>
        [HttpPost]
        [CmsContext("Imperial")]
        public ActionResult SaveSettings()
        {
            if (this.CmsContext.Session.IsAuthenticated)
            {
                if (this.Request.IsAjaxRequest())
                {
                    if (this.CmsContext.Session.IsAdmin || this.CmsContext.Session.IsResellerAdmin)
                    {
                        foreach (var settingName in this.Request.Form.AllKeys)
                        {
                            var settingValue = this.Request.Form[settingName];
                            new WebSite().AddSetting(new WebSiteSetting(this.CmsContext.WebSite, settingName, settingValue));
                            this.CmsContext.WebSite.AddSetting(settingName, settingValue); 
                        }

                        this.CmsContext.WebSite.SaveOrUpdate(this.DataSession);

                        return this.JsonNetResult(new JsonViewModel());
                    }
                    else
                    {
                        return this.JsonNetResult(new JsonViewModel("Sorry you do not have the correct permissions to execute this task. Your changes were not saved at this time."));
                    }
               }
                else
                {
                    return this.HttpNotFound();
              }
            }
            else
            {
                return this.JsonNetResult(new JsonViewModel("You must be authenticated before you can execute this action. Your changes were not saved at this time."));
            }
        }

        /// <summary>
        /// Adds a user.
        /// </summary>
        /// <returns>
        /// An <see cref="ActionResult" />.
        /// </returns>
        [CmsContext("Imperial")]
        public ActionResult AddUser()
        {
            if (!this.CmsContext.Session.IsAuthenticated)
            {
                return this.RedirectToAction("Login", "Session", new { redirect = "Reseller/Index" });
            }
            else
            {
                if (this.CmsContext.Session.IsResellerAdmin)
                {
                    var viewModel = new EditLoginViewModel(this.CmsContext, "Create a new login");
                    viewModel.Item = new WebLogin();
                    viewModel.BuildSelectDepotList();

                    return this.View("EditUser", viewModel);
                }
                else
                {
                    throw new HttpException(403, "Forbidden");
                }
            }
        }

        /// <summary>
        /// Edits a user.
        /// </summary>
        /// <param name="id">The id.</param>
        /// <returns>
        /// An <see cref="ActionResult"/>.
        /// </returns>
        [CmsContext("Imperial")]
        public ActionResult EditUser(string id)
        {
            if (!this.CmsContext.Session.IsAuthenticated)
            {
                return this.RedirectToAction("Login", "Session", new { redirect = "Reseller/Index" });
            }
            else
            {
                if (this.CmsContext.Session.IsResellerAdmin)
                {
                    var viewModel = new EditLoginViewModel(this.CmsContext, "Edit User {0}".FormatWith(id));
                    viewModel.Item = this.DataSession.First<WebLogin>(id);
                    viewModel.BuildSelectDepotList();

                    if (viewModel.Item == null)
                    {
                        viewModel.Item = new WebLogin();
                    }

                    return this.View(viewModel);
                }
                else
                {
                    throw new HttpException(403, "Forbidden");
                }
            }
        }

        /// <summary>
        /// Checks the login.
        /// </summary>
        /// <param name="id">The login to check.</param>
        /// <returns>
        /// An <see cref="ActionResult" />.
        /// </returns>
        [CmsContext("Imperial")]
        public ActionResult CheckLoginExists(string id)
        {
            if (!this.CmsContext.Session.IsResellerAdmin)
            {
                throw new HttpException(403, "Forbidden");
            }
            else
            {
                // Create a new login if the login account number does not exist.
                var loginAccountNumber = "{0}{1}".FormatWith("ACP", id.CleanWebInput().ToUpper());
                var loginExists = LoginHelper.ValidateLogin(this.DataSession, loginAccountNumber);

                if (loginExists)
                {
                    return this.JsonNetResult(new { result = true, message = "The login, '" + loginAccountNumber + "', already exists please choose another." });
                }
                else
                {
                    return this.JsonNetResult(new { result = false, message = "The login, '" + loginAccountNumber + "', is valid." });
                }
            }
        }

        /// <summary>
        /// Creates a new reseller login.
        /// </summary>
        /// <param name="accountNumber">The account number.</param>
        /// <param name="name">The name.</param>
        /// <param name="password">The password.</param>
        /// <param name="address1">The address1.</param>
        /// <param name="address2">The address2.</param>
        /// <param name="address3">The address3.</param>
        /// <param name="county">The county.</param>
        /// <param name="postCode">The post code.</param>
        /// <param name="emailAddress">The email address.</param>
        /// <param name="telephone">The telephone.</param>
        /// <param name="depotNumber">The selected depot.</param>
        /// <param name="discount">The discount.</param>
        /// <param name="permission">The permission.</param>
        /// <returns>
        /// An <see cref="ActionResult" />.
        /// </returns>
        [HttpPost]
        [CmsContext("Imperial")]
        public ActionResult CreateLogin(string accountNumber, string name, string password, string address1, string address2, string address3, string county, string postCode, string emailAddress, string telephone, int depotNumber = 0, decimal discount = 0, WebLoginPermission permission = WebLoginPermission.ResellerCustomer)
        {
            if (!this.CmsContext.Session.IsAuthenticated)
            {
                return this.RedirectToAction("Login", "Session", new { redirect = "Reseller/Index" });
            }
            else
            {
                if (!this.CmsContext.Session.IsResellerAdmin)
                {
                    throw new HttpException(403, "Forbidden");
                }
                else
                {
                    // Validate the arguments
                    if (!accountNumber.HasValue() || accountNumber.Length < 4)
                    {
                        var viewModel = new EditLoginViewModel(this.CmsContext, "Create a new login");
                        viewModel.Item = new WebLogin();
                       viewModel.BuildSelectDepotList();
                        viewModel.AddError("The login field must have a value.");

                        return this.View("EditUser", viewModel);
                    }

                    if (!name.HasValue() || name.Length < 4)
                    {
                        var viewModel = new EditLoginViewModel(this.CmsContext, "Create a new login");
                        viewModel.Item = new WebLogin();
                        viewModel.BuildSelectDepotList();
                        viewModel.AddError("The name field must have a value.");

                        return this.View("EditUser", viewModel);
                    }

                    if (!password.HasValue() || password.Length < 4)
                    {
                        var viewModel = new EditLoginViewModel(this.CmsContext, "Create a new login");
                        viewModel.Item = new WebLogin();
                       viewModel.BuildSelectDepotList();
                        viewModel.AddError("The password field must have a value.");

                        return this.View("EditUser", viewModel);
                    }

                    if (depotNumber < 1)
                    {
                        var viewModel = new EditLoginViewModel(this.CmsContext, "Create a new login");
                        viewModel.Item = new WebLogin();
                        viewModel.BuildSelectDepotList();
                        viewModel.AddError("A branch must be selected.");

                        return this.View("EditUser", viewModel);
                    }

                    // Create a new login if the login account number does not exist.
                    var loginAccountNumber = "{0}{1}".FormatWith("ACP", accountNumber.CleanWebInput().ToUpper());
                    var loginExists = LoginHelper.ValidateLogin(this.DataSession, loginAccountNumber);

                    if (loginExists)
                    {
                        var viewModel = new EditLoginViewModel(this.CmsContext, "Create a new login");
                        viewModel.Item = new WebLogin();
                        viewModel.BuildSelectDepotList();
                        viewModel.AddError("The login, '{0}', already exists in our database, please choose another.".FormatWith(loginAccountNumber));

                        return this.View("EditUser", viewModel);
                    }
                    else
                    {
                        var login = new WebLogin();
                        login.Id = loginAccountNumber;
                        login.Created = DateTime.Now;
                        login.Application = this.CmsContext.WebSite;
                        login.Permission = WebLoginPermission.ResellerCustomer;

                        login.Name = name.CleanWebInput();
                        login.PricingGroup = "RESELLER";
                        login.Permission = permission;
                        login.Address1 = address1.CleanWebInput();
                        login.Address2 = address2.CleanWebInput();
                        login.Town = address3.CleanWebInput();
                        login.County = county.CleanWebInput();
                        login.PostCode = postCode.CleanWebInput();
                        login.EmailAddress = emailAddress.CleanWebInput();
                        login.Discount = discount;
                        login.PhoneNumber = telephone.CleanWebInput();
                        login.Depot = this.DataSession.First<Depot>(x => x.Id == depotNumber);

                        if (!string.IsNullOrWhiteSpace(password))
                        {
                            login.SetPassword(password, this.CmsContext.Config.Salt);
                        }

                        login.SaveOrUpdate(this.DataSession);

                        return this.RedirectToAction("CustomerLogins");
                    }
                }
            }
        }

        /// <summary>
        /// Edits the user.
        /// </summary>
        /// <param name="id">The customer unique id.</param>
        /// <param name="accountNumber">The account number.</param>
        /// <param name="name">The name.</param>
        /// <param name="password">The password.</param>
        /// <param name="address1">The address1.</param>
        /// <param name="address2">The address2.</param>
        /// <param name="address3">The address3.</param>
        /// <param name="county">The county.</param>
        /// <param name="postCode">The post code.</param>
        /// <param name="emailAddress">The email address.</param>
        /// <param name="telephone">The telephone.</param>
        /// <param name="depotNumber">The depot number.</param>
        /// <param name="discount">The discount.</param>
        /// <param name="permission">The permission.</param>
        /// <returns>
        /// An <see cref="ActionResult" />.
        /// </returns>
        [HttpPost]
        [CmsContext("Imperial")]
        public ActionResult EditUser(string id, string accountNumber, string name, string password, string address1, string address2, string address3, string county, string postCode, string emailAddress, string telephone, int depotNumber = 0, decimal discount = 0, WebLoginPermission permission = WebLoginPermission.ResellerCustomer)
        {
            if (!this.CmsContext.Session.IsAuthenticated)
            {
                return this.RedirectToAction("Login", "Session", new { redirect = "Reseller/Index" });
            }
            else
            {
                if (this.CmsContext.Session.IsResellerAdmin)
                {
                    var login = this.DataSession.First<WebLogin>(id);

                    if (login != null)
                    {
                        login.Name = name.CleanWebInput();
                        login.PricingGroup = "RESELLER";
                        login.Permission = permission;
                        login.Address1 = address1.CleanWebInput();
                        login.Address2 = address2.CleanWebInput();
                        login.Town = address3.CleanWebInput();
                        login.County = county.CleanWebInput();
                        login.PostCode = postCode.CleanWebInput();
                        login.EmailAddress = emailAddress.CleanWebInput();
                        login.Discount = discount;
                        login.PhoneNumber = telephone.CleanWebInput();

                        if (!string.IsNullOrWhiteSpace(password))
                        {
                            login.SetPassword(password, this.CmsContext.Config.Salt);
                        }

                        if (depotNumber > 0)
                        {
                            login.Depot = this.DataSession.First<Depot>(x => x.Id == depotNumber);
                        }

                        login.SaveOrUpdate(this.DataSession);

                        return this.RedirectToAction("CustomerLogins");
                    }
                    else
                    {
                        var viewModel = new EditLoginViewModel(this.CmsContext, "Edit User {0}".FormatWith(accountNumber));
                        viewModel.Item = new WebLogin();
                       viewModel.BuildSelectDepotList();
                        viewModel.AddError("The login already exists in our database, please choose another.");

                        return this.View("EditUser", viewModel);
                    }
                }
                else
                {
                    throw new HttpException(403, "Forbidden");
                }
            }
        }

        /// <summary>
        /// Resets the customer password.
        /// </summary>
        /// <param name="id">The id.</param>
        /// <returns>
        /// An <see cref="ActionResult" />.
        /// </returns>
        [CmsContext("Imperial")]
        public ActionResult ResetCustomerPassword(string id)
        {
            if (!this.CmsContext.Session.IsResellerAdmin)
            {
                return this.RedirectToAction("Login", "Session", new { redirect = "Reseller/Index" });
            }
            else
            {
                var login = this.DataSession.First<WebLogin>(id);
                var viewModel = new ViewModel<WebLogin>(this.CmsContext, "Reset Password for {0}".FormatWith(login.Name), login);

                return this.View(viewModel);
            }
        }

        /// <summary>
        /// Resets the customer password.
        /// </summary>
        /// <param name="id">The id.</param>
        /// <param name="password">The password.</param>
        /// <param name="confirmPassword">The confirmation password.</param>
        /// <returns>
        /// An <see cref="ActionResult" />.
        /// </returns>
        [HttpPost]
        [CmsContext("Imperial")]
        public ActionResult ResetLoginPassword(string id, string password, string confirmPassword)
        {
            if (!this.CmsContext.Session.IsResellerAdmin)
            {
                return this.RedirectToAction("Login", "Session", new { redirect = "Reseller/Index" });
            }
            else
            {
                if (!password.HasValue())
                {
                    return this.JsonNetResult(new { result = false, message = "The password field must have a value." });
                }

                if (!confirmPassword.HasValue())
                {
                    return this.JsonNetResult(new { result = false, message = "The confirm password field must have a value." });
                }

                if (password != confirmPassword)
                {
                    return this.JsonNetResult(new { result = false, message = "The password and confirm password values must match." });
                }

                var login = this.DataSession.First<WebLogin>(id);

                if (login != null)
                {
                    login.SetPassword(password, this.CmsContext.Config.Salt);
                    login.Update(this.DataSession);

                    return this.JsonNetResult(new { result = true });
                }
                else
                {
                    return this.JsonNetResult(new { result = false, message = "The login was not found." });
                }
            }
        }

        /// <summary>
        /// Deletes the specified user.
        /// </summary>
        /// <param name="id">The id.</param>
        /// <returns>
        /// An <see cref="ActionResult" />.
        /// </returns>
        [CmsContext("Imperial")]
        public ActionResult DeleteCustomerLogin(string id)
        {
            if (!this.CmsContext.Session.IsResellerAdmin)
            {
                return this.RedirectToAction("Login", "Session", new { redirect = "Reseller/Index" });
            }
            else
            {
                var login = this.DataSession.First<WebLogin>(id);

                if (login != null)
                {
                    login.Delete(this.DataSession);
                }

                return this.RedirectToAction("CustomerLogins");
            }
        }

        /// <summary>
        /// Show the Add Depot form.
        /// </summary>
        /// <returns>
        /// An <see cref="ActionResult"/>.
        /// </returns>
        [CmsContext("Imperial")]
        public ActionResult AddDepot()
        {
            if (!this.CmsContext.Session.IsAuthenticated)
            {
                return this.RedirectToAction("Login", "Session", new { redirect = "Reseller/Index" });
            }
            else
            {
                if (this.CmsContext.Session.IsResellerAdmin)
                {
                    return this.View(new ViewModel(this.CmsContext, "Add Branch"));
                }
                else
                {
                    throw new HttpException(403, "Forbidden");
                }
            }
        }

        /// <summary>
        /// Adds the specified depot.
        /// </summary>
        /// <param name="accountNumber">The customer number.</param>
        /// <param name="headOffice">if set to <c>true</c> if the depot is the head office.</param>
        /// <returns>
        /// An <see cref="ActionResult"/>.
        /// </returns>
        [HttpPost]
        [CmsContext("Imperial")]
        public ActionResult ImportDepot(string accountNumber, bool headOffice = false)
        {
            if (!this.CmsContext.Session.IsAuthenticated)
            {
                return this.RedirectToAction("Login", "Session", new { redirect = "Reseller/Index" });
            }
            else
            {
                if (this.CmsContext.Session.IsResellerAdmin)
                {
                    var customer = this.DataSession.First<Customer>(accountNumber);
                    var depot = Depot.ConvertToDepot(this.CmsContext.Session, this.CmsContext.WebSite, customer, headOffice);

                    depot.SaveOrUpdate(this.DataSession);

                    return this.RedirectToAction("Depots");
                }
                else
                {
                    throw new HttpException(403, "Forbidden");
                }
            }
        }

        /////// <summary>
        /////// Rejects the line.
        /////// </summary>
        /////// <param name="orderid">The orderid.</param>
        /////// <param name="lineid">The lineid.</param>
        /////// <returns>
        /////// An <see cref="ActionResult"/>.
        /////// </returns>
        ////[HttpPost]
        ////[CmsContext("Imperial")]
        ////public ActionResult RejectOrderLine(int orderid, int lineid)
        ////{
        ////    if (!this.CmsContext.Session.IsAuthenticated)
        ////    {
        ////        return this.RedirectToAction("Login", "Session", new { redirect = "Reseller/Index" });
        ////    }
        ////    else
        ////    {
        ////        if (this.CmsContext.Session.IsResellerAdmin)
        ////        {
        ////            // Get the order.
        ////            WebResellerOrder order = this.DataSession.First<WebResellerOrder>(orderid);

        ////            if (order.IsNotNull())
        ////            {
        ////                // Get the line
        ////                WebResellerOrderLine line = order.Lines.FirstOrDefault(x => x.Id == lineid);

        ////                if (line.IsNotNull())
        ////                {
        ////                    line.UpdateStatus(WebOrderLineStatus.Cancelled);
        ////                    order.Update(this.DataSession, this.CmsContext);

        ////                    return this.Json("true");
        ////                }
        ////                else
        ////                {
        ////                    return this.Json("The order line was not found.");
        ////                }
        ////            }

        ////            return this.Json("The order was not found.");
        ////        }
        ////        else
        ////        {
        ////            throw new HttpException(403, "Forbidden");
        ////        }
        ////    }
        ////}

        /// <summary>
        /// Accepts the line.
        /// </summary>
        /// <param name="orderid">The orderid.</param>
        /// <param name="lineid">The lineid.</param>
        /// <returns>
        /// An <see cref="ActionResult"/>.
        /// </returns>
        [HttpPost]
        [CmsContext("Imperial")]
        public ActionResult AcceptOrderLine(int orderid, int lineid)
        {
            if (!this.CmsContext.Session.IsAuthenticated)
            {
                return this.RedirectToAction("Login", "Session", new { redirect = "Reseller/Index" });
            }
            else
            {
                if (this.CmsContext.Session.IsResellerAdmin)
                {
                    // Get the order.
                    WebBasket order = this.DataSession.First<WebBasket>(orderid);

                    if (order.IsNotNull())
                    {
                        // Get the line
                        WebBasketLine line = order.Lines.FirstOrDefault(x => x.Id == lineid);

                        if (line.IsNotNull())
                        {
                            line.UpdateStatus(WebBasketLineStatus.OrderFromImperial);
                            order.SaveOrUpdate(this.DataSession);
                            ////order.Update(this.DataSession, this.CmsContext);

                            return this.Json("true");
                        }
                        else
                        {
                            return this.Json("The order line was not found.");
                        }
                    }

                    return this.Json("The order was not found.");
                }
                else
                {
                    throw new HttpException(403, "Forbidden");
                }
            }
        }

        /// <summary>
        /// Makrs the line as ordered from the depot.
        /// </summary>
        /// <param name="orderid">The orderid.</param>
        /// <param name="lineid">The lineid.</param>
        /// <returns>
        /// An <see cref="ActionResult"/>.
        /// </returns>
        [HttpPost]
        [CmsContext("Imperial")]
        public ActionResult OrderLineFromDepot(int orderid, int lineid)
        {
            if (!this.CmsContext.Session.IsAuthenticated)
            {
                return this.RedirectToAction("Login", "Session", new { redirect = "Reseller/Index" });
            }
            else
            {
                if (this.CmsContext.Session.IsResellerAdmin)
                {
                    // Get the order.
                    WebBasket order = this.DataSession.First<WebBasket>(orderid);

                    if (order.IsNotNull())
                    {
                        // Get the line
                        WebBasketLine line = order.Lines.FirstOrDefault(x => x.Id == lineid);

                        if (line.IsNotNull())
                        {
                            line.UpdateStatus(WebBasketLineStatus.InStock);
                            order.SaveOrUpdate(this.DataSession);
                            ////order.Update(this.DataSession, this.CmsContext);

                            return this.Json("true");
                        }
                        else
                        {
                            return this.Json("The order line was not found.");
                        }
                    }

                    return this.Json("The order was not found.");
                }
                else
                {
                    throw new HttpException(403, "Forbidden");
                }
            }
        }

        /// <summary>
        /// Resets the line.
        /// </summary>
        /// <param name="orderid">The orderid.</param>
        /// <param name="lineid">The lineid.</param>
        /// <returns>
        /// An <see cref="ActionResult"/>.
        /// </returns>
        [HttpPost]
        [CmsContext("Imperial")]
        public ActionResult ResetOrderLine(int orderid, int lineid)
        {
            if (!this.CmsContext.Session.IsAuthenticated)
            {
                return this.RedirectToAction("Login", "Session", new { redirect = "Reseller/Index" });
            }
            else
            {
                if (this.CmsContext.Session.IsResellerAdmin)
                {
                    // Get the order.
                    WebBasket order = this.DataSession.First<WebBasket>(orderid);

                    if (order.IsNotNull())
                    {
                        // Get the line
                        WebBasketLine line = order.Lines.FirstOrDefault(x => x.Id == lineid);

                        if (line.IsNotNull())
                        {
                            line.UpdateStatus(WebBasketLineStatus.Created);
                            order.SaveOrUpdate(this.DataSession);
                            ////order.Update(this.DataSession, this.CmsContext);

                            return this.Json("true");
                        }
                        else
                        {
                            return this.Json("The order line was not found.");
                        }
                    }

                    return this.Json("The order was not found.");
                }
                else
                {
                    throw new HttpException(403, "Forbidden");
                }
            }
        }

        /////// <summary>
        /////// Reviews the items in the users basket.
        /////// </summary>
        /////// <returns>
        /////// An <see cref="ActionResult"/>.
        /////// </returns>
        ////[CmsContext("Imperial")]
        ////public ActionResult ReviewItems()
        ////{
        ////    if (!this.CmsContext.Session.IsAuthenticated)
        ////    {
        ////        return this.RedirectToAction("Login", "Session", new { redirect = "Reseller/ReviewItems" });
        ////    }
        ////    else
        ////    {
        ////        ////var order = OrderingHelper.GetResellerOrder(this.CmsContext, this.DataSession, true);
        ////        ////order.AddDeliveryCharge(this.CmsContext, this.CmsContext.Config.DeliveryCharge);

        ////        ////var viewModel = new ResellerCheckoutViewModel(this.CmsContext, "Your Order");
        ////        ////viewModel.Order = order;
        ////        ////viewModel.LoadLeftNavContent(this.DataSession, this.CmsContext, SpecialPageName.CheckoutReviewBasket);

        ////        ////return this.View("ReviewItems", viewModel);
        ////        return null;
        ////    }
        ////}

        /////// <summary>
        /////// Confirm a resellers order.
        /////// </summary>
        /////// <returns>
        /////// An <see cref="ActionResult"/>.
        /////// </returns>
        ////[CmsContext("Imperial")]
        ////public ActionResult ConfirmOrder()
        ////{
        ////    if (!this.CmsContext.Session.IsAuthenticated)
        ////    {
        ////        return this.RedirectToAction("Login", "Session", new { redirect = "Reseller/ConfirmOrder" });
        ////    }
        ////    else
        ////    {
        ////        var order = OrderingHelper.GetResellerOrder(this.CmsContext, this.DataSession);
        ////        var viewModel = this.GetResellerCheckoutModel("Your Details", SpecialPageName.CheckoutYourDetails, order);

        ////        return this.View(viewModel);
        ////    }
        ////}

        /////// <summary>
        /////// Submits a resellers order for processing.
        /////// </summary>
        /////// <param name="vehicleReg">The vehicle reg.</param>
        /////// <param name="purchaseOrderNumber">The purchase order number.</param>
        /////// <param name="comments">The comments.</param>
        /////// <returns>
        /////// An <see cref="ActionResult"/>.
        /////// </returns>
        ////[HttpPost]
        ////[CmsContext("Imperial")]
        ////public ActionResult SubmitOrder(string vehicleReg, string purchaseOrderNumber, string comments)
        ////{
        ////    if (!this.CmsContext.Session.IsAuthenticated)
        ////    {
        ////        return this.RedirectToAction("Login", "Session", new { redirect = "Reseller/SubmitOrder" });
        ////    }
        ////    else
        ////    {
        ////        var order = OrderingHelper.GetResellerOrder(this.CmsContext, this.DataSession);
        ////        var viewModel = this.GetResellerCheckoutModel("Your Details", SpecialPageName.CheckoutYourDetails, order);

        ////        if (order.IsNotNull())
        ////        {
        ////            order.Session = null;
        ////            order.PurchaseOrderNo = !string.IsNullOrWhiteSpace(vehicleReg) ? purchaseOrderNumber.CleanWebInput() : null;
        ////            order.VehicleRegNo = !string.IsNullOrWhiteSpace(vehicleReg) ? vehicleReg.CleanWebInput().ToUpper() : null;
        ////            order.Comments = !string.IsNullOrWhiteSpace(vehicleReg) ? comments.CleanWebInput().ToUpper() : null;
        ////            order.UpdateStatus(WebResellerOrderStatus.Created);

        ////            order.Update(this.DataSession, this.CmsContext);

        ////            // Send email confirmations to the customer and the reseller.
        ////            using (var sendMail = new SendMessage(this.CmsContext.Config.GetSmtpSettings()))
        ////            {
        ////                // ResellerOrderConfirmationToCustomer
        ////                var message1 = new ResellerOrderMessage(this.CmsContext, order)
        ////                    .TemplateName("ResellerOrderConfirmationToCustomer")
        ////                    .Subject("Thank You for Your Order on {0}".FormatWith(this.CmsContext.Application.Name))
        ////                    .To(order.EmailAddress)
        ////                    .ToMailMessage();

        ////                // ResellerOrderConfirmationToReseller
        ////                var message2 = new ResellerOrderMessage(this.CmsContext, order)
        ////                    .TemplateName("ResellerOrderConfirmationToReseller")
        ////                    .Subject("New Reseller Order Received on {0}".FormatWith(this.CmsContext.Application.Name))
        ////                    .To(this.CmsContext.Config.OrderNotificationsTo)
        ////                    .ToMailMessage();

        ////                // ResellerPotentialOrder
        ////                var message3 = new ResellerOrderMessage(this.CmsContext, order, true)
        ////                    .TemplateName("ResellerPotentialOrder")
        ////                    .Subject("Potential Reseller Order Received from {0} via {1}".FormatWith(order.Login.Id.ToUpper(), this.CmsContext.Application.Name))
        ////                    .To(this.CmsContext.Config.ImperialOrderNotificationsTo)
        ////                    .ToMailMessage();

        ////                sendMail.SendMailMessage(message1);
        ////                sendMail.SendMailMessage(message2);
        ////                sendMail.SendMailMessage(message3);
        ////            }

        ////            return this.View("OrderSuccessfull", viewModel);
        ////        }
        ////        else
        ////        {
        ////            WebErrorHelper.LogError(this.CmsContext, new OrderNotFoundException(this.CmsContext, "The requested order was not found.", true));

        ////            return View("Error", new CmsErrorViewModel(this.CmsContext, 404, "The requested order was not found."));
        ////        }
        ////    }
        ////}

        /// <summary>
        /// Submits the reseller order.
        /// </summary>
        /// <param name="orderid">The order id.</param>
        /// <returns>
        /// An <see cref="ActionResult"/>.
        /// </returns>
        ////[HttpPost]
        ////[CmsContext("Imperial")]
        ////public ActionResult SubmitResellerOrder(int orderid)
        ////{
        ////    if (!this.CmsContext.Session.IsAuthenticated)
        ////    {
        ////        return this.RedirectToAction("Login", "Session", new { redirect = "Reseller/Index" });
        ////    }
        ////    else
        ////    {
        ////        if (this.CmsContext.Session.IsResellerAdmin)
        ////        {
        ////            // Get the order.
        ////            WebResellerOrder order = this.DataSession.First<WebResellerOrder>(orderid);

        ////            if (order.IsNotNull())
        ////            {
        ////                if (order.AreAllLinesCancelled())
        ////                {
        ////                    // Cancel the order
        ////                    order.UpdateStatus(WebResellerOrderStatus.Cancelled);
        ////                    order.Update(this.DataSession, this.CmsContext);

        ////                    return this.Json("Cancelled");
        ////                }

        ////                if (order.AreAllLinesProcessed())
        ////                {
        ////                    // Submit the order.
        ////                    order.UpdateStatus(WebResellerOrderStatus.Submitted);
        ////                    order.Update(this.DataSession, this.CmsContext);

        ////                    // Send the Order to the Service Bus for processing later.
        ////                    ServiceBusExport.ExportResellerOrder(order);

        ////                    // Sent the necessary emails
        ////                    using (var sendMessage = new SendMessage(this.CmsContext.Config.GetSmtpSettings()))
        ////                    {
        ////                        var message = new ResellerOrderMessage(this.CmsContext, order, true)
        ////                            .TemplateName("ResellerOrderToImperial")
        ////                            .Subject("New Reseller Order Received from {0} via {1}".FormatWith(order.Login.Id.ToUpper(), this.CmsContext.Application.Name))
        ////                            .To(this.CmsContext.Config.ImperialOrderNotificationsTo)
        ////                            .ToMailMessage();

        ////                        sendMessage.SendMailMessage(message);
        ////                    }

        ////                    return this.Json("Submitted");
        ////                }
        ////                else
        ////                {
        ////                    return this.Json("The order contains lines which have not been processed.");
        ////                }
        ////            }
        ////            else
        ////            {
        ////                return this.Json("The order was not found.");
        ////            }
        ////        }
        ////        else
        ////        {
        ////            throw new HttpException(403, "Forbidden");
        ////        }
        ////    }
        ////}

        /// <summary>
        /// Orders the status key.
        /// </summary>
        /// <returns>
        /// An <see cref="ActionResult"/>.
        /// </returns>
        ////public ActionResult OrderStatusKey()
        ////{
        ////    return this.View("OrderStatusKey");
        ////}

        /////// <summary>
        /////// Email test action.
        /////// </summary>
        /////// <returns>
        /////// An <see cref="ActionResult"/>.
        /////// </returns>
        ////[CmsContext("Imperial")]
        ////public ActionResult EMailTest()
        ////{
        ////    var order = this.DataSession.Fetch<WebResellerOrder>(x => x.Id == 642)
        ////        .FirstOrDefault();

        ////    using (SendMessage sendMail = new SendMessage(this.CmsContext.Config.GetSmtpSettings()))
        ////    {
        ////        // ResellerOrderConfirmationToCustomer
        ////        var message1 = new ResellerOrderMessage(this.CmsContext, order)
        ////            .TemplateName("ResellerOrderConfirmationToCustomer")
        ////            .Subject("[ResellerOrderConfirmationToCustomer.htm] Thank You for Your Order on {0}".FormatWith(this.CmsContext.Application.Name))
        ////            .To("adrian@crashrepairparts.com")
        ////            .ToMailMessage();

        ////        // ResellerOrderConfirmationToReseller
        ////        var message2 = new ResellerOrderMessage(this.CmsContext, order)
        ////            .TemplateName("ResellerOrderConfirmationToReseller")
        ////            .Subject("[ResellerOrderConfirmationToReseller.htm] New Reseller Order Received on {0}".FormatWith(this.CmsContext.Application.Name))
        ////            .To("adrian@crashrepairparts.com")
        ////            .ToMailMessage();

        ////        // ResellerPotentialOrder
        ////        var message3 = new ResellerOrderMessage(this.CmsContext, order)
        ////            .TemplateName("ResellerPotentialOrder")
        ////            .Subject("[ResellerPotentialOrder.htm] Potential Reseller Order Received from {0} via {1}".FormatWith(order.Login.Id.ToUpper(), this.CmsContext.Application.Name))
        ////            .To("adrian@crashrepairparts.com")
        ////            .ToMailMessage();

        ////        var message4 = new ResellerOrderMessage(this.CmsContext, order)
        ////            .TemplateName("ResellerOrderToImperial")
        ////            .Subject("[ResellerOrderToImperial.htm] New Reseller Order Received from {0} via {1}".FormatWith(order.Login.Id.ToUpper(), this.CmsContext.Application.Name))
        ////            .To("adrian@crashrepairparts.com")
        ////            .ToMailMessage();

        ////        var collection = new NameValueCollection();
        ////        collection.Add("title", "Mr");
        ////        collection.Add("firstName", "Adrian");
        ////        collection.Add("surnames", "Favill");
        ////        collection.Add("position", "Developer");
        ////        collection.Add("telephone", "01302802000");
        ////        collection.Add("companyName", "Imperial Automotive Ltd");
        ////        collection.Add("tradingName", "CrashRepairParts.com");
        ////        collection.Add("address1", "Carr Hill");
        ////        collection.Add("address2", "Cross Bank");
        ////        collection.Add("town", "Doncaster");
        ////        collection.Add("postCode", "DN4 8BE");
        ////        collection.Add("website", "N/A");
        ////        collection.Add("comments", "Please can you set me up an account?");

        ////        var message5 = new GenericMessage(this.CmsContext)
        ////            .AddFrom(collection)
        ////            .TemplateName("ResellerCustomerRegistration")
        ////            .Subject("[ResellerCustomerRegistration.htm] A new customer has completed the registration form on {0}".FormatWith(this.CmsContext.Application.Name))
        ////            .To("adrian@crashrepairparts.com")
        ////            .ToMailMessage();

        ////        sendMail.SendMailMessage(message1);
        ////        sendMail.SendMailMessage(message2);
        ////        sendMail.SendMailMessage(message3);
        ////        sendMail.SendMailMessage(message4);
        ////        sendMail.SendMailMessage(message5);

        ////        return new JsonNetResult(new { result = true });
        ////    }
        ////}

        /////// <summary>
        /////// Gets the reseller checkout model.
        /////// </summary>
        /////// <param name="pageTitle">The page title.</param>
        /////// <param name="pageName">Name of the page.</param>
        /////// <param name="order">The order.</param>
        /////// <returns>
        /////// The requested <see cref="CheckoutViewModel"/>.
        /////// </returns>
        ////private ResellerCheckoutViewModel GetResellerCheckoutModel(string pageTitle, SpecialPageName pageName, WebResellerOrder order)
        ////{
        ////    var viewModel = new ResellerCheckoutViewModel(this.CmsContext, pageTitle);
        ////    viewModel.LoadLeftNavContent(this.DataSession, this.CmsContext, pageName);
        ////    viewModel.Order = OrderingHelper.GetResellerOrder(this.CmsContext, this.DataSession);

        ////    return viewModel;
        ////}
    }
}
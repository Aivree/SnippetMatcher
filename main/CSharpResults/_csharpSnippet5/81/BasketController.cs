// <copyright file="BasketController.cs" company="Imperial">
// Copyright (c) 2012 All Rights Reserved
// </copyright>
namespace Imperial.CrashRepairParts.Cms.Controllers
{
    using System;
    using System.Collections.Generic;
    using System.Data;
    using System.Linq;
    using System.Web.Mvc;
    using Imperial.CrashRepairParts.Core;
    using Imperial.CrashRepairParts.Exports;
    using Imperial.CrashRepairParts.Messaging;
    using Imperial.CrashRepairParts.Models;
    using Imperial.CrashRepairParts.Security;
    using Imperial.CrashRepairParts.Types;
    using Imperial.CrashRepairParts.ViewModels;
    using Imperial.Data.Mvc;
    using Imperial.Net;
    using Imperial.Retry;
    using Imperial.Web;
    using Imperial.Web.ThirdParty.SagePay;
    using NHibernate.Linq;
   
    /// <summary>
    /// Basket Controller.
    /// </summary>
    public class BasketController : CrashRepairPartsController
    {
        /// <summary>
        /// Checkout step 1: Review Items.
        /// </summary>
        /// <returns>
        /// An <see cref="ActionResult"/>.
        /// </returns>
        [NoCache]
        [CmsContext("Imperial")]
        public ActionResult Index()
        {
            this.CheckCustomerCanOrder();
            this.CmsContext.ReloadHeaderAndFooter();

            if (this.CmsContext.IsRetailCustomer())
            {
               return this.View("Retail.ReviewItems", this.GetCheckoutModel("Your Basket", SpecialPageName.CheckoutReviewBasket, true, null, true));
            }
            else
            {
                if (this.CmsContext.WebSite.Type == WebSiteType.Reseller)
                {
                    return this.View("ResellerTrade.ReviewItems", this.GetCheckoutModel("Your Order", SpecialPageName.CheckoutReviewBasket, true, null, true));
                }
                
                return this.View("Trade.ReviewItems", this.GetCheckoutModel("Your Order", SpecialPageName.CheckoutReviewBasket, true, null, true));
            }
        }

        /// <summary>
        /// Checkout step 1: Review Items.
        /// </summary>
        /// <returns>
        /// An <see cref="ActionResult" />.
        /// </returns>
        [NoCache]
        [CmsContext("Imperial")]
        public ActionResult ReviewItems()
        {
            return this.Index();
        }

        /// <summary>
        /// Adds a product to the customers basket.
        /// </summary>
        /// <param name="id">The id.</param>
        /// <param name="description">The description.</param>
        /// <param name="quantity">The quantity.</param>
        /// <param name="paintCode">The paint code.</param>
        /// <returns>
        /// An <see cref="ActionResult" />.
        /// </returns>
        [NoCache]
        [HttpPost]
        [CmsContext("Imperial")]
        public ActionResult Add(string id, string description, decimal quantity = 1, string paintCode = null)
        {
            this.CheckCustomerCanOrder();

            if (id.HasValue())
            {
                var product = this.CmsContext.LoadProductDetailsFor(id);

                if (product != null)
                {
                    var paintPrice = 0M;
                    var group = this.DataSession.First<ProductSubCategory>(product.ProductSubCategory);

                    if (group != null)
                    {
                        paintPrice = group.PaintingPrice;
                    }

                    var stock = this.CmsContext.GetStockLevelFor(id);
                    var expectedStock = this.CmsContext.GetExpectedStockLevelFor(id);
                    var expectedStockDate = this.CmsContext.GetExpectedStockDateFor(id);

                    if (product.IsCooling() && stock == 0)
                    {
                        stock = 9999;
                    }

                    if (!this.CmsContext.HasBasket)
                    {
                        this.CmsContext.CreateNewBasket();
                    }

                    string message = this.GetLineMessageFor(product);

                    //// topup price for reseller's 
                    var productPrice = product.Price;
                    if (this.CmsContext.WebSite.Type == WebSiteType.Reseller)
                    {
                      productPrice = this.CmsContext.AddWebsiteMarginTo(productPrice);
                    }
                    
                    var line = this.CmsContext.Basket.Add(this.CmsContext.Session, product, description.CleanWebInput(), quantity, productPrice, stock, expectedStock, paintCode, paintPrice, this.CmsContext.Config.VatRatePercent, this.CmsContext.GetPricingGroupName(), this.CmsContext.Session.VrmRegNumber, message);
                    this.CmsContext.Basket.SaveOrUpdate(this.DataSession);

                    this.CmsContext.AddToBasketLog(WebBasketLogType.LineAdded, "{0} added ({1} @ {2}).".FormatWith(line.ProductCode, line.Quantity, line.UnitPrice));

                    if (this.CmsContext.IsAjax)
                    {
                        return this.JsonNetResult(new { result = true, total = this.CmsContext.FormatPrice(this.CmsContext.Basket.Total) });
                    }
                    else
                    {
                        if (this.CmsContext.WebSite.Type == WebSiteType.Reseller)
                        {
                            return this.View("ResellerRetail.ReviewItems", this.GetCheckoutModel("Your Order", SpecialPageName.CheckoutReviewBasket, true, null, true));
                        }

                        return this.RedirectToAction("ReviewItems");
                    }
                }
                else
                {
                    if (this.CmsContext.IsAjax)
                    {
                        return this.JsonNetResult(new { result = false, message = "The product {0} was not found.".FormatWith(id) });
                    }
                    else
                    {
                        // The product was not found.
                        return this.HttpNotFound("The product {0} was not found.".FormatWith(id));
                    }
                }
            }
            else
            {
                if (this.CmsContext.IsAjax)
                {
                    return this.JsonNetResult(new { result = false, message = "No product code specified." });
                }
                else
                {
                    return this.HttpNotFound("No product code specified.");
                }
            }
        }

        /// <summary>
        /// Updates a product to the customers basket.
        /// </summary>
        /// <param name="id">The id.</param>
        /// <param name="quantity">The quantity.</param>
        /// <returns>
        /// An <see cref="ActionResult" />.
        /// </returns>
        [NoCache]
        [CmsContext("Imperial")]
        public ActionResult Update(int id, decimal quantity = 1)
        {
            if (id > 0)
            {
                var line = this.CmsContext.Basket.Lines.FirstOrDefault(x => x.Id == id);

                if (line != null)
                {
                    if (quantity <= 0 && line.BackOrderQuantity == 1)
                    {
                        return this.Redirect("~/basket/remove?id=" + id);
                    }
                    else
                    {
                        var stock = this.CmsContext.GetStockLevelFor(line.ProductCode);
                        var expectedStock = this.CmsContext.GetExpectedStockLevelFor(line.ProductCode);
                        var expectedStockDate = this.CmsContext.GetExpectedStockDateFor(line.ProductCode);

                        line.UpdateQuantity(Math.Ceiling(quantity), stock, expectedStock, expectedStockDate);

                        this.CmsContext.AddToBasketLog(WebBasketLogType.LineAdded, "{0} updated ({1} @ {2}).".FormatWith(line.ProductCode, line.Quantity, line.UnitPrice));
                    }

                    return this.RedirectToAction("ReviewItems");
                }
                else
                {
                    // The product was not found.
                    return this.HttpNotFound("The product {0} was not found.".FormatWith(id));
                }
            }
            else
            {
                return this.HttpNotFound("No product code specified.");
            }
        }

        /// <summary>
        /// Removes an item from the customers basket.
        /// </summary>
        /// <param name="id">The id of the line to remove.</param>
        /// <returns>
        /// An <see cref="ActionResult" />.
        /// </returns>
        [NoCache]
        [CmsContext("Imperial")]
        public ActionResult Remove(int id)
        {
            if (id > 0)
            {
                var line = this.CmsContext.Basket.Lines.FirstOrDefault(x => x.Id == id);

                if (line != null)
                {
                    line.UpdateBasketId();
                    this.CmsContext.Basket.Lines.Remove(line);

                    this.CmsContext.AddToBasketLog(WebBasketLogType.LineAdded, "{0} removed ({1} @ {2}).".FormatWith(line.ProductCode, line.Quantity, line.UnitPrice));

                    return this.RedirectToAction("ReviewItems");
                }
                else
                {
                    // The product was not found.
                    return this.HttpNotFound("The product {0} was not found.".FormatWith(id));
                }
            }
            else
            {
                return this.HttpNotFound("No product code specified.");
            }
        }

        /// <summary>
        /// Paints a part.
        /// </summary>
        /// <param name="id">The id.</param>
        /// <param name="quantity">The quantity.</param>
        /// <returns>
        /// An <see cref="ActionResult" />.
        /// </returns>
        [NoCache]
        [CmsContext("Imperial")]
        public ActionResult PaintPart(string id, decimal quantity)
        {
            if (id.HasValue())
            {
                var product = this.CmsContext.LoadProductDetailsFor(id);

                if (product != null)
                {
                    var group = this.DataSession.First<ProductSubCategory>(product.ProductSubCategory);
                    var paintPrice = 0M;

                    if (group != null)
                    {
                        paintPrice = group.PaintingPrice;
                    }

                    var viewModel = new PaintPartViewModel(this.CmsContext, "Paint");
                    viewModel.Product = product;
                    viewModel.PaintPrice = paintPrice;
                    viewModel.Quantity = quantity;

                    return this.CmsView("AddPaint", viewModel);
                }
                else
                {
                    // The product was not found.
                    return this.HttpNotFound("The product {0} was not found.".FormatWith(id));
                }
            }
            else
            {
                return this.HttpNotFound("No product code specified.");
            }
        }

        /// <summary>
        /// Adds the discount to the current basket.
        /// </summary>
        /// <param name="campaignCode">The discount code.</param>
        /// <returns>
        /// An <see cref="ActionResult" />.
        /// </returns>
        [NoCache]
        [HttpPost]
        [CmsContext("Imperial")]
        public ActionResult ApplyCampaign(string campaignCode)
        {
            if (this.CmsContext.Basket.Discount == 0 && campaignCode.HasValue())
            {
                var campain = this.DataSession.First<WebCampaign>(x => x.WebSite == this.CmsContext.WebSite && x.Code == campaignCode);

                if (campain != null && campain.IsValid)
                {
                    this.CmsContext.Basket.CampainCode = campain.Code.ToUpper();
                    this.CmsContext.Basket.Discount = campain.DiscountAmount;

                    return this.RedirectToAction("ReviewItems");
                }
                else
                {
                    this.CmsContext.Basket.CampainCode = null;
                    this.CmsContext.Basket.Discount = 0;

                    var viewModel = this.GetCheckoutModel("Your Basket", SpecialPageName.CheckoutReviewBasket, true);
                    viewModel.AddError("The voucher code entered was not valid.");

                    return this.RedirectToAction("ReviewItems");
                }
            }
            else
            {
                return this.RedirectToAction("ReviewItems");
            }
        }

        /// <summary>
        /// Removes a campaign from the basket.
        /// </summary>
        /// <returns>
        /// An <see cref="ActionResult" />.
        /// </returns>
        [NoCache]
        [CmsContext("Imperial")]
        public ActionResult RemoveCampaign()
        {
            if (this.CmsContext.Basket.CampainCode.HasValue())
            {
                this.CmsContext.Basket.CampainCode = null;
                this.CmsContext.Basket.Discount = 0;
            }

            return this.RedirectToAction("ReviewItems");
        }

        /// <summary>
        /// Delivery options for trade customers.
        /// </summary>
        /// <param name="agreedTerms">if set to <c>true</c> [agreed terms].</param>
        /// <param name="workProvider">The work provider code.</param>
        /// <param name="regNo">The reg no.</param>
        /// <param name="estimateNo">The estimate no.</param>
        /// <param name="purchaseOrderNo">The purchase order no.</param>
        /// <param name="basketID">The id.</param>
        /// <returns>
        /// An <see cref="ActionResult" />.
        /// </returns>
        [NoCache]
        [CmsContext("Imperial")]
        public ActionResult TradeDelivery(bool agreedTerms = false, string workProvider = null, string regNo = null, string estimateNo = null, string purchaseOrderNo = null, int basketID = 0)
        {
            var basket = this.CmsContext.Basket;

            if (basketID > 0)
            {
                basket = this.DataSession.First<WebBasket>(basketID);
            }

            if (agreedTerms)
            {
                basket.TermsAgreed = true;
                basket.WorkProviderCode = workProvider.CleanWebInput();
                basket.VehicleRegNo = regNo.CleanWebInput().ToAlphanumericOnly();
                basket.EstimateNumber = estimateNo.CleanWebInput();
                basket.PurchaseOrderNo = purchaseOrderNo.CleanWebInput();
                basket.Status = WebBasketStatus.AwaitingPayment;
                basket.Update(this.DataSession);
                if (this.CmsContext.Customer != null)
                {
                    if (this.CmsContext.WebSite.Type == WebSiteType.Reseller && !this.CmsContext.Session.IsResellerAdmin)
                    {
                        return this.CmsView("ResellerTrade.Delivery", this.GetCheckoutModel("Your Basket", SpecialPageName.CheckoutReviewBasket, false, basket));
                    }
                    else
                    {
                        return this.CmsView("Trade.Delivery", this.GetCheckoutModel("Your Basket", SpecialPageName.CheckoutReviewBasket, false, basket));
                    }
                }
                else 
                {
                    if (this.CmsContext.WebSite.Type == WebSiteType.Reseller && !this.CmsContext.Session.IsResellerAdmin)
                    {
                        return this.CmsView("ResellerTrade.Delivery", this.GetCheckoutModel("Your Basket", SpecialPageName.CheckoutReviewBasket, false, basket));
                    }
                    
                    return this.CmsView("Trade.Delivery", this.GetCheckoutModel("Your Basket", SpecialPageName.CheckoutReviewBasket, false, basket));
                }
            }
            else
            {
                var viewModel = this.GetCheckoutModel("Your Basket", SpecialPageName.CheckoutReviewBasket, true);
                viewModel.AddError("You must agree to the terms and conditions of sale.");
                if (this.CmsContext.WebSite.Type == WebSiteType.Reseller && !this.CmsContext.Session.IsResellerAdmin)
                {
                    return this.CmsView("ResellerTrade.ReviewItems", viewModel);
                }

                return this.CmsView("Trade.ReviewItems", viewModel);
            }
        }

        /// <summary>
        /// Trade Basket Payment.
        /// </summary>
        /// <param name="deliveryDate">The delivery date.</param>
        /// <param name="name">The name.</param>
        /// <param name="postCode">The post code.</param>
        /// <param name="address1">The address1.</param>
        /// <param name="address2">The address2.</param>
        /// <param name="address3">The address3.</param>
        /// <param name="county">The county.</param>
        /// <param name="country">The country.</param>
        /// <param name="comments">The comments.</param>
        /// <param name="dropShip">if set to <c>true</c> [drop ship].</param>
        /// <param name="basketID">The basket ID.</param>
        /// <returns>
        /// An <see cref="ActionResult" />.
        /// </returns>
        [NoCache]
        [HttpPost]
        [CmsContext("Imperial")]
        public ActionResult TradePay(string deliveryDate, string name, string postCode, string address1, string address2, string address3, string county, string country, string comments, bool dropShip = false, int basketID = 0)
        {
           var basket = this.CmsContext.Basket;

            if (basketID > 0)
            {
                basket = this.DataSession.First<WebBasket>(basketID);
            }

            if (basket.Lines.Count > 0)
            {
                basket.Comments = comments.CleanWebInput();
                basket.PaymentTxCode = this.CmsContext.CreateSagePayVendorTxCode(basket);

                this.UpdateEstimatedDeliveryDates(basket, deliveryDate);

                if (this.CmsContext.Customer != null && (this.CmsContext.Customer.AllowAlternateDeliveryAddress || dropShip))
                {
                    basket.DeliverToName = name.CleanWebInput();
                    basket.DeliverToAddress1 = address1.CleanWebInput();
                    basket.DeliverToAddress2 = address2.CleanWebInput();
                    basket.DeliverToAddress3 = address3.CleanWebInput();
                    basket.DeliverToCounty = county.CleanWebInput();
                    basket.DeliverToCountry = country.CleanWebInput();
                    basket.DeliverToPostCode = postCode.CleanPostCode();

                    if (this.CmsContext.Customer.IsReseller && dropShip)
                    {
                        basket.DeliverToName = name.CleanWebInput() + " **DSI**";
                        this.CmsContext.Basket.DeliveryCharge = this.CmsContext.Customer.DropShipDeliveryCharge;
                    }
                }

                basket.Status = WebBasketStatus.AwaitingPayment;

                return this.CmsView("Trade.Pay", this.GetCheckoutModel("How would you like to pay for your order?", SpecialPageName.CheckoutReviewBasket, false, basket));
            }
            else
            {
                var viewModel = this.GetCheckoutModel("Your Basket", SpecialPageName.CheckoutReviewBasket, false, basket);
                viewModel.AddError("Your basket contains no products.");
                viewModel.DeliveryDate = deliveryDate.ToDateTime();

                return this.CmsView("Trade.ReviewItems", viewModel);
            }
        }

        /// <summary>
        /// Trade Basket Pay By Express Checkout.
        /// </summary>
        /// <param name="workProvider">The work provider.</param>
        /// <param name="regNo">The reg no.</param>
        /// <param name="estimateNo">The estimate no.</param>
        /// <param name="purchaseOrderNo">The purchase order no.</param>
        /// <returns>
        /// An <see cref="ActionResult" />.
        /// </returns>
        [NoCache]
        [CmsContext("Imperial")]
        public ActionResult TradePayExpressCheckout(string workProvider = null, string regNo = null, string estimateNo = null, string purchaseOrderNo = null)
        {
            this.CmsContext.Basket.ExpressCheckout = true;
            this.CmsContext.Basket.Status = WebBasketStatus.AwaitingPayment;
            this.CmsContext.Basket.WorkProviderCode = workProvider.CleanWebInput();
            this.CmsContext.Basket.VehicleRegNo = regNo.CleanWebInput();
            this.CmsContext.Basket.EstimateNumber = estimateNo.CleanWebInput();
            this.CmsContext.Basket.PurchaseOrderNo = purchaseOrderNo.CleanWebInput();

            return this.TradePayForBasket();
        }

        /// <summary>
        /// Trade Basket Pay By Account.
        /// </summary>
        /// <param name="basketID">The basket ID.</param>
        /// <returns>
        /// An <see cref="ActionResult" />.
        /// </returns>
        [NoCache]
        [CmsContext("Imperial")]
        public ActionResult TradePayByAccount(int basketID = 0)
        {
            return this.TradePayForBasket(basketID, false);
        }

        /// <summary>
        /// Trades the pay cash on delivery.
        /// </summary>
        /// <param name="basketID">The basket ID.</param>
        /// <returns>
        /// An <see cref="ActionResult" />.
        /// </returns>
        [NoCache]
        [CmsContext("Imperial")]
        public ActionResult TradePayCashOnDelivery(int basketID = 0)
        {
            return this.TradePayForBasket(basketID, true);
        }

        /// <summary>
        /// Retail Your Details.
        /// </summary>
        /// <param name="agreedTerms">if set to <c>true</c> [agreed terms].</param>
        /// <returns>
        /// An <see cref="ActionResult" />.
        /// </returns>
        [NoCache]
        [CmsContext("Imperial")]
        public ActionResult RetailYourDetails(bool agreedTerms = false)
        {
            if (agreedTerms)
            {
                this.CmsContext.Basket.TermsAgreed = true;

                var viewModel = this.GetCheckoutModel("Your Details", SpecialPageName.CheckoutDeliveryOptions);

                return this.CmsView("Retail.Delivery", viewModel);
            }
            else
            {
                var viewModel = this.GetCheckoutModel("Your Basket", SpecialPageName.CheckoutReviewBasket, true);
                viewModel.AddError("You must agree to the terms and conditions of sale.");

                return this.CmsView("Retail.ReviewItems", viewModel);
            }
        }

        /// <summary>
        /// Determines whether [is post code valid] [the specified post code1].
        /// </summary>
        /// <param name="postCode1">The post code1.</param>
        /// <param name="postCode2">The post code2.</param>
        /// <returns>
        /// true / false
        /// </returns>
        public bool IsPostCodeValid(string postCode1, string postCode2)
        {
            if (postCode1.Trim().Equals(postCode2.Trim()))
            {
                return false;
            }

            return true;
        }

        /// <summary>
        /// Retail Summary.
        /// </summary>
        /// <param name="yourDetailsModel">Your details model.</param>
        /// <returns>
        /// An <see cref="ActionResult" />.
        /// </returns>
        [NoCache]
        [CmsContext("Imperial")]
        public ActionResult RetailSummary(RetailYourDetailsModel yourDetailsModel)
        {
            var validationResult = yourDetailsModel.Validate();
            if (validationResult.IsValid) 
            {
                if (!this.IsPostCodeValid(yourDetailsModel.PostCode1, yourDetailsModel.PostCode2))
                {
                   var viewModelErrorPostCode = this.GetCheckoutModel("Your Details", SpecialPageName.CheckoutDeliveryOptions);
                    validationResult.AddValidationFailure("Postcode is invalid.");
                    viewModelErrorPostCode.AddErrorsFrom(validationResult);
                    if (this.CmsContext.WebSite.Type == WebSiteType.Reseller)
                    {
                        return this.CmsView("ResellerRetail.Delivery", viewModelErrorPostCode); 
                    }
                                      
                    return this.CmsView("Retail.Delivery", viewModelErrorPostCode);
                }

            // check if postcode restricted
            if (this.PostcodeRestricted(yourDetailsModel.Postcode))
            { 
                    // stop order processing
                        var viewModelRestrricted = this.GetCheckoutModel("Your Details", SpecialPageName.CheckoutDeliveryOptions);
                        viewModelRestrricted.AddError("Sorry but we do not deliver to the area you are requesting. " +
                                                "For more information about the areas we cover, <a target='_blank' href='" + this.Request.Url.Scheme + "://" + this.Request.Url.Host + "/delivery'>click here</a>");
                        if (this.CmsContext.WebSite.Type == WebSiteType.Reseller)
                        {
                            return this.CmsView("ResellerRetail.Delivery", viewModelRestrricted);
                        }        
                
                return this.CmsView("Retail.Delivery", viewModelRestrricted);
            }   
                              
                 yourDetailsModel.UpdateBasket(this.CmsContext.Basket);

                var deliveryChargeLookup = this.GetDeliveryCharge();

                this.UpdateEstimatedDeliveryDates(this.CmsContext.Basket);

                if (this.CmsContext.Basket.Total > this.CmsContext.Config.DeliveryFreeThreshold)
                {
                    this.CmsContext.Basket.DeliveryCharge = deliveryChargeLookup.DiscountedCharge;
                }
                else
                {
                    this.CmsContext.Basket.DeliveryCharge = deliveryChargeLookup.StandardCharge;
                }

                this.CmsContext.Basket.IsReferred = deliveryChargeLookup.IsReferred;
                this.CmsContext.Basket.Status = WebBasketStatus.AwaitingPayment;
                this.CmsContext.Basket.PaymentTxCode = this.CmsContext.CreateSagePayVendorTxCode(this.CmsContext.Basket);
                this.CmsContext.Basket.SaveOrUpdate(this.DataSession);

                var viewModel = this.GetCheckoutModel("Order Summary", SpecialPageName.CheckoutOrderSummary);

                if (this.CmsContext.WebSite.Type == WebSiteType.Reseller)
                {
                    return this.CmsView("ResellerRetail.Summary", viewModel);
                }

                return this.CmsView("Retail.Summary", viewModel);
            }
            else
            {
                var viewModel = this.GetCheckoutModel("Your Details", SpecialPageName.CheckoutDeliveryOptions);
                viewModel.AddErrorsFrom(validationResult);

                if (this.CmsContext.WebSite.Type == WebSiteType.Reseller)
                {
                    return this.CmsView("ResellerRetail.Delivery", viewModel);
                }

                return this.CmsView("Retail.Delivery", viewModel);
            }
        }

        /// <summary>
        /// Retail Pay for basket.
        /// </summary>
        /// <returns>
        /// An <see cref="ActionResult" />.
        /// </returns>
        [NoCache]
        [CmsContext("Imperial")]
        public ActionResult RetailPay()
        {
            var viewModel = this.GetCheckoutModel("Pay for your order", SpecialPageName.CheckoutDeliveryOptions);

            return this.CmsView("Retail.Pay", viewModel);
        }

        /// <summary>
        /// Retail Pay Cash On Delivery.
        /// </summary>
        /// <returns>
        /// An <see cref="ActionResult" />.
        /// </returns>
        [NoCache]
        [CmsContext("Imperial")]
        public ActionResult RetailPayCashOnDelivery()
        {
            var basket = this.CmsContext.Basket;
            var customer = this.CmsContext.GetRetailCustomerForCashOnDeliverySales();

            if (customer != null)
            {
                // Update the basket.
                basket.Customer = customer;
                basket.PostingDate = this.CalculatePostingDate(basket);
                basket.Status = WebBasketStatus.Paid;
                basket.Session = null;
                basket.Add(PaymentType.CashOnDelivery);
                basket.SaveOrUpdate(this.DataSession);

                // Update the sales ledger (this is use for reporting)
                this.CreateSalesLedgerEntriesFor(basket);

                // Add an entry to the log.
                this.CmsContext.AddToBasketLog(WebBasketLogType.PaymentAccepted, "To be paid cash on delivery.", basket);

                // Export the basket.
                var exportResult = this.ExportBasketToServiceBus(basket);

                // Update the stock levels.
                this.CmsContext.UpdateProductStock(basket);

                if (this.CmsContext.Basket.Id == basket.Id)
                {
                    // Create a new basket. This stops baskets been processed twice.
                    this.CmsContext.CreateNewBasket();
                }

                if (exportResult.IsExported)
                {
                    // Add an entry to the log.
                    this.CmsContext.AddToBasketLog(WebBasketLogType.Exported, "Exported successfully.", basket);
                }
                else
                {
                    // Add an entry to the log.
                    this.CmsContext.AddToBasketLog(WebBasketLogType.ExportError, "Export Error: {0}.".FormatWith(exportResult.Error.Message), basket);

                    // Log the error.
                    this.LogError(new CrashRepairPartsException("Basket '{0}' was not exported to the Service Bus: {1}".FormatWith(basket.Id, exportResult.Error.Message), exportResult.Error, this.CmsContext));
                }

                // Show the payment successfull page.
                var viewModel = this.GetCheckoutModel("Thank you, Your order was successful", SpecialPageName.CheckoutPaymentResult, false, basket);

                return this.View("PaymentSuccessfull", viewModel);
            }
            else
            {
                throw new CrashRepairPartsException("No customer found for processing Cash on Delivery sales. Check the value of the 'RetailCustomerAccountNumberCOD' setting.");
            }
        }

        /// <summary>
        /// Request Quote
        /// </summary>
        /// <returns>
        /// An <see cref="ActionResult" />.
        /// </returns>
        /// <exception cref="CrashRepairPartsException">No customer found for processing quote request on behalf of reseller. Check the value of the 'RetailCustomerAccountNumberCOD' setting.</exception>
        [NoCache]
        [CmsContext("Imperial")]
        public ActionResult RequestQuote()
        {
            var basket = this.CmsContext.Basket;
           
            if (this.CmsContext.WebSite.ResellerCustomer != null)
            {
                //// Update the basket.
                basket.QuoteOnly = true;
                basket.Customer = this.CmsContext.WebSite.ResellerCustomer;
                
                ////basket.PostingDate = this.CalculatePostingDate(basket);
                basket.Status = WebBasketStatus.Created;
                basket.Session = this.CmsContext.Session;
                ////basket.Add(PaymentType.CashOnDelivery);
                basket.SaveOrUpdate(this.DataSession);
                //// Export the basket.
                var exportResult = this.ExportBasketToServiceBus(basket);
                //// Update the sales ledger (this is use for reporting)this.CreateSalesLedgerEntriesFor(basket);
                using (var sendMessage = new SendMessage(SendMessageSettings.FromAppConfig()))
                {
                    //// ResellerOrderConfirmationToCustomer
                    var message1 = new ResellerOrderMessage(this.CmsContext, basket)
                        .TemplateName("ResellerOrderConfirmationToCustomer")
                        .Subject("Thank You for Your Order on {0}".FormatWith(this.CmsContext.WebSite.Name))
                        .To(basket.EmailAddress)
                        .ToMailMessage();

                    //// ResellerOrderConfirmationToReseller
                    var message2 = new ResellerOrderMessage(this.CmsContext, basket)
                        .TemplateName("ResellerOrderConfirmationToReseller")
                        .Subject("New Reseller Order Received on {0}".FormatWith(this.CmsContext.WebSite.Name))
                        .To(this.CmsContext.Config.TradeOrderNotificationsTo)
                        .ToMailMessage();

                    //// ResellerPotentialOrder
                    var message3 = new ResellerOrderMessage(this.CmsContext, basket, true)
                        .TemplateName("ResellerPotentialOrder")
                        .Subject("Potential Reseller Order Received from {0} via {1}".FormatWith(this.CmsContext.Session.Login.Id.ToUpper(), this.CmsContext.WebSite.Name))
                        .To("ResellerWebOrders@crashrepairparts.com")
                        .ToMailMessage();

                    ////ServiceBusExport.ExportBasket(basket, "baskets");

                    sendMessage.SendMailMessage(message1);
                   ////sendMail.SendMailMessage(message2);
                    ////sendMail.SendMailMessage(message3);
                }
               
                //// Add an entry to the log.
                this.CmsContext.AddToBasketLog(WebBasketLogType.QuoteProcessed, "Quote sent to Navision", basket);
                
                //// Update the stock levels.
                ////this.CmsContext.UpdateProductStock(basket);

                if (this.CmsContext.Basket.Id == basket.Id)
                {
                    //// Create a new basket. This stops baskets been processed twice.
                    this.CmsContext.CreateNewBasket();
                }

                if (exportResult.IsExported)
                {
                    // Add an entry to the log.
                    this.CmsContext.AddToBasketLog(WebBasketLogType.Exported, "Exported successfully.", basket);
                }
                else
                {
                    //// Add an entry to the log.
                    this.CmsContext.AddToBasketLog(WebBasketLogType.ExportError, "Export Error: {0}.".FormatWith(exportResult.Error.Message), basket);

                    //// Log the error.
                    this.LogError(new CrashRepairPartsException("Basket '{0}' was not exported to the Service Bus: {1}".FormatWith(basket.Id, exportResult.Error.Message), exportResult.Error, this.CmsContext));
                }

                //// TODO add landing page thanks for reseller's customers
                var viewModel = this.GetCheckoutModel("Thank you, Your order was successful", SpecialPageName.CheckoutPaymentResult, false, basket);

                return this.View("PaymentSuccessfull", viewModel);
            }
            else
            {
                throw new CrashRepairPartsException("No customer found for processing this reseller quote. Exception at Basket.RequestQuote");
            }
        }

        /// <summary>
        /// Express Checkout.
        /// </summary>
        /// <param name="purchaseOrderNo">The purchase order no.</param>
        /// <param name="regNo">The reg no.</param>
        /// <param name="estimateNo">The estimate no.</param>
        /// <param name="workProvider">The work provider.</param>
        /// <returns>
        /// An <see cref="ActionResult" />.
        /// </returns>
        [NoCache]
        [CmsContext("Imperial")]
        public ActionResult ExpressCheckout(string purchaseOrderNo = null, string regNo = null, string estimateNo = null, string workProvider = null)
        {
            this.UpdateEstimatedDeliveryDates(this.CmsContext.Basket);
            this.CmsContext.Basket.PurchaseOrderNo = purchaseOrderNo;
            this.CmsContext.Basket.VehicleRegNo = regNo;
            this.CmsContext.Basket.EstimateNumber = estimateNo;
            this.CmsContext.Basket.WorkProviderCode = workProvider;

            var viewModel = this.GetCheckoutModel("Express Checkout", SpecialPageName.CheckoutReviewBasket, false, this.CmsContext.Basket, true);

            return this.CmsView("ExpressCheckout", viewModel);
        }

        /// <summary>
        /// Called when a retail payment has been successfully processed.
        /// </summary>
        /// <param name="crypt">The crypt.</param>
        /// <returns>
        /// An <see cref="ActionResult"/>.
        /// </returns>
        [NoCache]
        [CmsContext("Imperial")]
        public ActionResult PaymentSuccess(string crypt)
        {
            try
            {
                var sageResponse = SageResponse.Parse(crypt, true, this.CmsContext.Config.SagePayFormsEncryptionKey);

                if (sageResponse.Status == SageResponseStatus.OK)
                {
                    // Get the Basket.
                    var basketID = sageResponse.VendorTxCode.ToNumericOnly().ToInt32();
                    var basket = this.DataSession.First<WebBasket>(basketID);

                    if (basket != null)
                    {
                        // Add the payment to the basket
                        if (sageResponse.PaymentViaPayPal)
                        {
                            basket.Add(PaymentType.CreditDebitCard, sageResponse.Amount, sageResponse.TxAuthNo, sageResponse.CardType, sageResponse.Last4Digits);
                            this.CmsContext.AddToBasketLog(WebBasketLogType.PaymentAccepted, "{0} paid via paypal with {1} card ending {2}.".FormatWith(sageResponse.Amount, sageResponse.CardType, sageResponse.Last4Digits), basket);
                        }
                        else
                        {
                            basket.Add(PaymentType.CreditDebitCard, sageResponse.Amount, sageResponse.TxAuthNo, sageResponse.CardType, sageResponse.Last4Digits);
                        }

                        // Update the sales ledger (this is use for reporting)
                        this.CreateSalesLedgerEntriesFor(basket);

                        // Add an entry to the log.
                        this.CmsContext.AddToBasketLog(WebBasketLogType.PaymentAccepted, "{0} paid via SagePay with {1} card ending {2}.".FormatWith(sageResponse.Amount, sageResponse.CardType, sageResponse.Last4Digits), basket);

                        // Update the stock levels.
                        this.CmsContext.UpdateProductStock(basket);

                        // Update the basket.
                        basket.PostingDate = this.CalculatePostingDate(basket);
                        basket.Status = WebBasketStatus.Paid;
                        basket.Session = null;
                        basket.SaveOrUpdate(this.DataSession);

                        // Export the basket.
                        var exportResult = this.ExportBasketToServiceBus(basket);

                        if (exportResult.IsExported)
                        {
                            // Add an entry to the log.
                            this.CmsContext.AddToBasketLog(WebBasketLogType.Exported, "Exported successfully.", basket);
                        }
                        else
                        {
                            // Add an entry to the log.
                            this.CmsContext.AddToBasketLog(WebBasketLogType.ExportError, "Export Error: {0}.".FormatWith(exportResult.Error.Message), basket);

                            // Log the error.
                            this.LogError(new CrashRepairPartsException("Basket '{0}' was not exported to the Service Bus: {1}".FormatWith(basketID, exportResult.Error.Message), exportResult.Error, this.CmsContext));
                        }

                        if (this.CmsContext.Basket.Id == basket.Id)
                        {
                            // A new basket must be created with the data from the loaded basket.
                            // You cannot send a basket with the same id to SagePay twice.
                            this.CmsContext.CreateNewBasket();
                        }

                        // Show the payment successfull page.
                        var viewModel = this.GetCheckoutModel("Thank you, Your order was successful", SpecialPageName.CheckoutPaymentResult, false, basket);

                        return this.View("PaymentSuccessfull", viewModel);
                    }
                    else
                    {
                        // The basket was not found.
                        throw new SageResponseException("WebBasket '{0}' was not found in the database.".FormatWith(basketID), sageResponse);
                    }
                }
                else
                {
                    // Payment failed.
                    throw new SageResponseException("Payment received with an invalid status code: '{0}'.".FormatWith(sageResponse.Status), sageResponse);
                }
            }
            catch (Exception exception)
            {
                this.LogError(exception);

                var viewModel = this.GetCheckoutModel("Sorry an problem occurred", SpecialPageName.CheckoutPaymentResult, false);
                viewModel.AddError(exception.Message);

                return this.View("PaymentFailed", viewModel);
            }
        }

        /// <summary>
        /// Called when payment processing has failed.
        /// </summary>
        /// <param name="crypt">The crypt.</param>
        /// <returns>
        /// An <see cref="ActionResult"/>.
        /// </returns>
        [NoCache]
        [CmsContext("Imperial")]
        public ActionResult PaymentFailed(string crypt)
        {
            var sageResponse = SageResponse.Parse(crypt, true, this.CmsContext.Config.SagePayFormsEncryptionKey);
            var basketID = sageResponse.VendorTxCode.ToNumericOnly().ToInt32();
          
            // Get the Basket.
            var basket = this.DataSession.First<WebBasket>(basketID);

            if (basket != null)
            {
                basket.PaymentError(sageResponse.StatusDetail, sageResponse.Amount, sageResponse.TxAuthNo, sageResponse.CardType, sageResponse.Last4Digits);
                basket.SaveOrUpdate(this.DataSession);
            }

            // Log as an error
            this.LogError(new SageResponseException("SagePay Payment Failure for WebBasket {0}: {1}".FormatWith(basketID, sageResponse.StatusDetail), sageResponse));

            // Add an entry to the log.
            this.CmsContext.AddToBasketLog(WebBasketLogType.PaymentFailed, sageResponse.StatusDetail, basket);

            if (this.CmsContext.Basket.Id == basket.Id)
            {
                // A new basket must be created with the data from the loaded basket.
                // You cannot send a basket with the same id to SagePay twice.
                this.CmsContext.CreateNewBasket(basket);
            }
            else
            {
                // Update the current basket with data from the loaded basket.
                this.CmsContext.Basket.AddDataFrom(basket);
                this.CmsContext.Basket.SaveOrUpdate(this.DataSession);
            }

            this.CmsContext.ReloadHeaderAndFooter();

            // Show the PaymentFailed page.
            var viewModel = this.GetCheckoutModel("Sorry a problem occurred while processing your order", SpecialPageName.CheckoutPaymentResult);
            viewModel.PageTitle = "Payment failed";
            viewModel.AddError("Sorry our payment provider has {0} your payment because {1}<br /><br />Please try again using another card or call us on {2} quoting basket number {3}."
                .FormatWith(sageResponse.Status.ToString().ToLower(), sageResponse.StatusDetail, this.CmsContext.WebSite.Phone, basket.Id));

            return this.View("PaymentFailed", viewModel);
        }

        /// <summary>
        /// Trade Basket Pay By Account.
        /// </summary>
        /// <returns>
        /// An <see cref="ActionResult" />.
        /// </returns>
        [NoCache]
        [CmsContext("Imperial")]
        public ActionResult Terms()
        {
            var viewModel = this.GetCheckoutModel("Terms and Conditions of Sales for Retail Customers", SpecialPageName.CheckoutReviewBasket, false);

            if (this.CmsContext.Session.IsAuthenticated)
            {
                if (this.CmsContext.Session.Login.Customer != null)
                {
                    viewModel.PageTitle = "Terms and Conditions of Sales for {0}".FormatWith(this.CmsContext.Session.Login.Customer.Id);
                }
                else
                {
                    viewModel.PageTitle = "Terms and Conditions of Sales for {0}".FormatWith(this.CmsContext.Session.Login.Id);
                }
            }

            return this.CmsView("Terms", viewModel);
        }

        /// <summary>
        /// Finds an address.
        /// </summary>
        /// <param name="id">The post code.</param>
        /// <returns>
        /// An <see cref="ActionResult" />.
        /// </returns>
        [NoCache]
        [CmsContext("Imperial")]
        public ActionResult FindAddress(string id)
        {
            try
            {
                var addresses = AddressHelper.LookupAddress(this.CmsContext, this.DataSession, id);
                var viewModel = new ListViewModel<AddressLookup>(this.CmsContext, "Choose Your Address");
                viewModel.Items = addresses;

                return this.View("AddressResults", viewModel);
            }
            catch (Exception exception)
            {
                this.LogError(exception);

                var viewModel = new ListViewModel<AddressLookup>(this.CmsContext, "Choose Your Address");
                viewModel.Items = new List<AddressLookup>();

                return this.View("AddressResults", viewModel);
            }
        }

        /// <summary>
        /// Exports the basket.
        /// </summary>
        /// <param name="id">The id.</param>
        /// <returns>
        /// An <see cref="ActionResult" />.
        /// </returns>
        [NoCache]
        [Authorise(WebLoginPermission.OfficeStaff)]
        [CmsContext("Imperial")]
        public ActionResult CancelBasket(int id)
        {
            var viewModel = new ViewModel<WebBasket>(this.CmsContext, "Cancel Basket {0}".FormatWith(id));
            var basket = this.DataSession.First<WebBasket>(id);

            if (basket != null)
            {
                viewModel.Item = basket;
            }
            else
            {
                viewModel.PageTitle = "Basket not found".FormatWith(basket.Id);
                viewModel.AddError("The basket {0} was not found.".FormatWith(id));
            }

            return this.CmsView("CancelBasket", viewModel);
        }

        /// <summary>
        /// Exports the basket.
        /// </summary>
        /// <param name="id">The id.</param>
        /// <returns>
        /// An <see cref="ActionResult" />.
        /// </returns>
        [NoCache]
        [Authorise(WebLoginPermission.OfficeStaff)]
        [CmsContext("Imperial")]
        public ActionResult CancelBasketWith(int id)
        {
            var basket = this.DataSession.First<WebBasket>(id);

            if (basket != null)
            {
                basket.Cancel();

                this.CmsContext.AddToBasketLog(WebBasketLogType.Cancelled, "Basket manually cancelled by {0}".FormatWith(this.CmsContext.Session.Login.Id), basket);

                return this.Redirect("/Orders/View?id=" + id);
            }
            else
            {
                return this.HttpNotFound();
            }
        }

        /// <summary>
        /// Exports the basket.
        /// </summary>
        /// <param name="id">The id.</param>
        /// <returns>
        /// An <see cref="ActionResult" />.
        /// </returns>
        [NoCache]
        [Authorise(WebLoginPermission.OfficeStaff)]
        [CmsContext("Imperial")]
        public ActionResult ExportBasket(int id)
        {
            var viewModel = new ViewModel<WebBasket>(this.CmsContext, "Export Basket {0}".FormatWith(id));
            var basket = this.DataSession.First<WebBasket>(id);

            if (basket != null)
            {
                viewModel.Item = basket;
            }
            else
            {
                viewModel.PageTitle = "Basket not found".FormatWith(basket.Id);
                viewModel.AddError("The basket {0} was not found.".FormatWith(id));
            }

            return this.CmsView("ExportBasket", viewModel);
        }

        /// <summary>
        /// Exports the basket.
        /// </summary>
        /// <param name="id">The id.</param>
        /// <returns>
        /// An <see cref="ActionResult" />.
        /// </returns>
        [NoCache]
        [Authorise(WebLoginPermission.OfficeStaff)]
        [CmsContext("Imperial")]
        public ActionResult ExportBasketWith(int id)
        {
            var basket = this.DataSession.First<WebBasket>(id);

            if (basket != null)
            {
                this.ExportBasketToServiceBus(basket);

                this.CmsContext.AddToBasketLog(WebBasketLogType.Exported, "Basket exported manually by {0}".FormatWith(this.CmsContext.Session.Login.Id), basket);

                return this.Redirect("/Orders/View?id=" + id);
            }
            else
            {
                return this.HttpNotFound();
            }
        }

        /// <summary>
        /// Updaets the basket.
        /// </summary>
        /// <param name="id">The id.</param>
        /// <returns>
        /// An <see cref="ActionResult" />.
        /// </returns>
        [NoCache]
        [Authorise(WebLoginPermission.OfficeStaff)]
        [CmsContext("Imperial")]
        public ActionResult UpdateBasket(int id)
        {
            var viewModel = new ViewModel<WebBasket>(this.CmsContext, "Update Basket {0}".FormatWith(id));
            var basket = this.DataSession.First<WebBasket>(id);

            if (basket != null)
            {
                viewModel.Item = basket;
            }
            else
            {
                viewModel.PageTitle = "Basket not found".FormatWith(basket.Id);
                viewModel.AddError("The basket {0} was not found.".FormatWith(id));
            }

            return this.CmsView("UpdateBasket", viewModel);
        }

        /// <summary>
        /// Updates the basket with the specified details.
        /// </summary>
        /// <param name="id">The id.</param>
        /// <param name="quoteNumber">The quote number.</param>
        /// <param name="orderNumber">The order number.</param>
        /// <returns>
        /// An <see cref="ActionResult" />.
        /// </returns>
        [NoCache]
        [HttpPost]
        [Authorise(WebLoginPermission.OfficeStaff)]
        [CmsContext("Imperial")]
        public ActionResult UpdateBasketWith(int id, string quoteNumber, string orderNumber)
        {
            var basket = this.DataSession.First<WebBasket>(id);

            if (basket != null)
            {
                basket.UpdateNavOrderNumbers(quoteNumber, orderNumber);

                this.CmsContext.AddToBasketLog(WebBasketLogType.Processed, "Basket processed manually by {0} ({1}/{2})".FormatWith(this.CmsContext.Session.Login.Id, quoteNumber, orderNumber), basket);

                return this.Redirect("/Orders/View?id=" + id);
            }
            else
            {
                return this.HttpNotFound();
            }
        }

        /// <summary>
        /// Deletes the unused baskets.
        /// </summary>
        /// <param name="daysToKeep">The days to keep.</param>
        /// <returns>
        /// An <see cref="ActionResult"/>.
        /// </returns>
        [CmsContext("Imperial")]
        public ActionResult DeleteUnusedBaskets(int daysToKeep)
        {
            var session = this.DataSession;
            IList<WebBasket> baskets = new List<WebBasket>();
            baskets = this.DataSession.Fetch<WebBasket>(x => x.Status != WebBasketStatus.Cancelled && x.Status != WebBasketStatus.Complete && x.Status != WebBasketStatus.Paid && x.Status != WebBasketStatus.Processed && x.Status != WebBasketStatus.Error && x.Created < DateTime.Now.AddDays(daysToKeep * -1) && x.Created > DateTime.Now.AddDays(-20));

            foreach (WebBasket basket in baskets)
            {
                basket.Status = WebBasketStatus.Deleted;
                this.CmsContext.AddToBasketLog(WebBasketLogType.BasketExpired, "Web Basket has expired.", basket);
            }
            
            return this.Json(true);
        }

        /// <summary>
        /// Updates the estimated delivery dates.
        /// </summary>
        /// <param name="basket">The basket.</param>
        /// <param name="deliveryDate">The delivery date.</param>
        private void UpdateEstimatedDeliveryDates(WebBasket basket, string deliveryDate = null)
        {
            foreach (var line in basket.Lines)
            {
                DateTime dueDate = (line.DueDate != null) || (line.DueDate > DateTime.Now) ? Convert.ToDateTime(line.DueDate) : DateTime.Now;
                dueDate = line.Stock > 0 ? DateTime.Now : dueDate;
                line.ExpectedDeliveryDate = this.CmsContext.GetExpectedDeliveryDateFor(line, dueDate);
            }

           basket.ExpectedDate = basket.Lines.OrderBy(x => x.ExpectedDeliveryDate)
                .Select(x => x.ExpectedDeliveryDate)
                .First();

            if (deliveryDate != null && basket.ExpectedDate != null)
            {
                var expectedDeliveryDate = deliveryDate.ToDateTime();
                basket.ExpectedDate = expectedDeliveryDate > basket.ExpectedDate.Value ? expectedDeliveryDate : basket.ExpectedDate.Value;
            }
        }

        /// <summary>
        /// Pay for a basket for a trade user.
        /// </summary>
        /// <param name="basketID">The basket ID.</param>
        /// <param name="cashOnDelivery">if set to <c>true</c> [cash on delivery].</param>
        /// <returns>
        /// An <see cref="ActionResult" />.
        /// </returns>
        private ActionResult TradePayForBasket(int basketID = 0, bool cashOnDelivery = false)
        {
            var basket = this.CmsContext.Basket;

            if (basketID > 0)
            {
                basket = this.DataSession.First<WebBasket>(basketID);
            }

            if (basket.Status == WebBasketStatus.AwaitingPayment)
            {
                // Add a payment to the basket.
                if (cashOnDelivery)
                {
                    basket.Add(PaymentType.CashOnDelivery);
                }
                else
                {
                    basket.Add(PaymentType.Account);
                }

                // Update the stock levels.
                this.CmsContext.UpdateProductStock(basket);

                // Update the basket.
                basket.PostingDate = this.CalculatePostingDate(basket);
                basket.Status = WebBasketStatus.Paid;

                // Add an entry to the log.
                this.CmsContext.AddToBasketLog(WebBasketLogType.PaymentAccepted, "Basket submitted. To be paid: {0}.".FormatWith(basket.PaymentType), basket);

                // Update the sales ledger (this is use for reporting)
                this.CreateSalesLedgerEntriesFor(basket);

                // Send the Order to the Service Bus for processing later.
                var exportResult = this.ExportBasketToServiceBus(basket);

                if (exportResult.IsExported)
                {
                    // Add an entry to the log.
                    this.CmsContext.AddToBasketLog(WebBasketLogType.Exported, "Exported successfully.".FormatWith(basket.Id), basket);

                    // Update the baskets status
                    return this.CmsView("Trade.Thanks", this.GetCheckoutModel("Your Basket", SpecialPageName.CheckoutReviewBasket, false, basket));
                }
                else
                {
                    // Add an entry to the log.
                    this.CmsContext.AddToBasketLog(WebBasketLogType.ExportError, "Export Error: {0}.".FormatWith(exportResult.Error.Message), basket);

                    // Update the basket.
                    basket.Error(exportResult.Error.Message);
                    this.LogError(exportResult.Error);

                    var viewModel = new ViewModel(this.CmsContext, "Sorry a problem occurred processing your basket.");
                    viewModel.AddError(exportResult.Error);

                    return this.CmsView("PaymentError", viewModel);
                }
            }
            else
            {
                // The basket was not at the correct status.
                return this.CmsView("Trade.Thanks", this.GetCheckoutModel("Your Basket", SpecialPageName.CheckoutReviewBasket, false, basket));
            }
        }

        /// <summary>
        /// Exports the basket to the service bus.
        /// </summary>
        /// <param name="basket">The basket.</param>
        /// <returns>
        /// A <see cref="ExportResult" />.
        /// </returns>
        private ExportResult ExportBasketToServiceBus(WebBasket basket)
        {
            var retry = new RetryPolicy(TimeSpan.FromSeconds(1));
            var exportResult = retry.Execute<ExportResult>(() => ServiceBusExport.ExportBasket(basket));

            return exportResult;
        }

        /// <summary>
        /// Gets the checkout model.
        /// </summary>
        /// <param name="pageTitle">The page title.</param>
        /// <param name="pageName">Name of the page.</param>
        /// <param name="allowEditLines">if set to <c>true</c> [allow edit lines].</param>
        /// <param name="basket">The basket.</param>
        /// <param name="loadWorkProviders">if set to <c>true</c> [load work providers].</param>
        /// <returns>
        /// The requested <see cref="CheckoutViewModel" />.
        /// </returns>
        private CheckoutViewModel GetCheckoutModel(string pageTitle, SpecialPageName pageName, bool allowEditLines = false, WebBasket basket = null, bool loadWorkProviders = false)
        {
            CheckoutViewModel viewModel = new CheckoutViewModel(this.CmsContext, this.DataSession, pageTitle, basket);
            viewModel.AllowEditLines = allowEditLines;

            if (this.CmsContext.Session.Login.HasCustomer && loadWorkProviders)
            {
                viewModel.WorkProviders = this.DataSession.Fetch<CustomerWorkProvider>()
                    .OrderBy(x => x.SortOrder)
                    .ToList();
            }

            if (viewModel.Basket != null)
            {
                this.CmsContext.AddStockLevelsTo(viewModel.Basket);
                this.CmsContext.AddExpectedStockLevelsTo(viewModel.Basket);

                if (!viewModel.Basket.VehicleRegNo.HasValue())
                {
                    viewModel.Basket.VehicleRegNo = viewModel.Basket.Lines.Select(x => x.RegNumber).FirstOrDefault();
                }

                viewModel.HasSpecialOrderLines = viewModel.Basket.HasSpecialOrderLines;
            }

            return viewModel;
        }

        /// <summary>
        /// Adds a delivery charge to the basket.
        /// </summary>
        /// <returns>
        /// A <see cref="DeliveryPostcodeLookup" />.
        /// </returns>
        private DeliveryPostcodeLookup GetDeliveryCharge()
        {
            var postCode = this.CmsContext.Basket.DeliverToPostCode;
            var lookup = AddressHelper.LookupDeliveryCharge(this.CmsContext, this.DataSession, postCode);

            if (lookup == null)
            {
                lookup = new DeliveryPostcodeLookup(this.CmsContext.Basket.DeliverToPostCode, this.CmsContext.Config.DeliveryChargeStandard);
            }

            return lookup;
        }

        /// <summary>
        /// Calculates the posting date for a basket.
        /// </summary>
        /// <param name="basket">The basket.</param>
        /// <returns>
        /// A <see cref="DateTime" />.
        /// </returns>
        private DateTime CalculatePostingDate(WebBasket basket)
        {
            var cutoffTime = this.CmsContext.Config.DeliveryCutoffTimeTrade.TimeOfDay;

            if (basket.Customer.Id.IsEqualToAny(this.CmsContext.Config.RetailCustomerPricingGroup, this.CmsContext.Config.RetailCustomerAccountNumberCOD))
            {
                cutoffTime = this.CmsContext.Config.DeliveryCutOffTimeRetail.TimeOfDay;
            }

            if (DateTime.Now.TimeOfDay > cutoffTime)
            {
                return basket.Created.AddHours(12);
            }
            else
            {
                return basket.Created;
            }
        }

        /// <summary>
        /// Creates the sales ledger entries for.
        /// </summary>
        /// <param name="basket">The basket.</param>
        private void CreateSalesLedgerEntriesFor(WebBasket basket)
        {
            foreach (var line in basket.Lines)
            {
                var ledger = new WebSalesLedgerLine(basket, line);
                ledger.Save(this.DataSession);
            }
        }

        /// <summary>
        /// Checks the customer can order.
        /// </summary>
        private void CheckCustomerCanOrder()
        {
            if (this.CmsContext.IsCustomerBlocked)
            {
                this.LogInfo("Customer '{0}' cannot currently order on the website as their account is blocked.".FormatWith(this.CmsContext.Customer.AccountNumber));
            }
        }

        /// <summary>
        /// Gets the line message for the specified product.
        /// </summary>
        /// <param name="product">The product.</param>
        /// <returns>
        /// Any required line messages.
        /// </returns>
        private string GetLineMessageFor(Product product)
        {
            if (product.IsCooling())
            {
                return this.CmsContext.Config.CoolingPartBasketMessage;
            }

            return null;
        }

        /// <summary>
        /// Postcodes the restricted.
        /// </summary>
        /// <param name="yourPostCode">Your post code.</param>
        /// <returns>returns true/false</returns>
        private bool PostcodeRestricted(string yourPostCode)
        {
            try
            {
                var restrcitedPostcodes = this.RestrictedPostcodes();
                foreach (var restrictedPostcode in restrcitedPostcodes)
                {
                    if (yourPostCode.Contains(restrictedPostcode))
                    {
                        return true;
                    }
                }

                return false;
            }
            catch (Exception ex)
            {
                // Log the error.
                this.LogError(new CrashRepairPartsException("Basket '{0}' Delivery Postcode Error : {1}".FormatWith(this.CmsContext.Basket.Id, ex.Message), ex, this.CmsContext));
                return false;
            }
        }

        /// <summary>
        /// List of restricted postcodes
        /// </summary>
        /// <returns>returns restricted postcodes list</returns>
        private IList<string> RestrictedPostcodes()
        {
            var restrcitedPostcodes = new List<string>();
            var command = this.DataSession.GetCommand();
            command.CommandText = "Select [Postcode] FROM [dbo].[DeliveryPostcodesRestricted]";
            command.CommandType = CommandType.Text;

            var reader = command.ExecuteReader();

            while (reader.Read())
            {
                restrcitedPostcodes.Add(reader["Postcode"].ToString());
            }

            reader.Close();

            return restrcitedPostcodes;
        }
    }
}

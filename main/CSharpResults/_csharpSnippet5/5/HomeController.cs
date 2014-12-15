using IndividualAssignment2A.BusinessLogic;
using IndividualAssignment2A.Models;
using IndividualAssignment2A.Repositories;
using IndividualAssignment2A.ViewModels;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;
using System.Web.Mvc;

namespace IndividualAssignment2A.Controllers
{
    public class HomeController : Controller
    {
        [HttpGet]
        public ActionResult Index()
        {
            ProductRepository productRep = new ProductRepository();
            VisitRepository visitRep = new VisitRepository();
            return View(productRep.DisplayProducts());
        }
        [HttpGet]
        public ActionResult Add(int productID)
        {
            SingleProductRepository product = new SingleProductRepository(); 
            return View(product.ReturnProduct(productID));
        }

        [HttpPost]
        public ActionResult Add(SingleProduct product)
        {
            
            ProductVisitRepository prodVisRep = new ProductVisitRepository();

                prodVisRep.AddProductVisit(product);
                return RedirectToAction("ViewCart");
        }

        [HttpGet]
        public ActionResult ViewCart()
        {          
            CartRepository cartRep = new CartRepository();
            return View(cartRep.GetCart());
        }

        [HttpPost]
        public ActionResult Update(Cart cart)
        {
          ProductVisitRepository prodVisRep = new ProductVisitRepository();
            //TempDate replaces ViewBag, as the latter gets lots in RedirectToAction
            TempData["error"] = prodVisRep.Validate(cart.Products);
            if (TempData["error"] == "")
            {
                if (cart.Products == null)
                {

                    RedirectToAction("ViewCart");
                }
                else
                {
                    foreach (SingleProduct product in cart.Products)
                    {
                        prodVisRep.UpdateCart(product);
                    }
                }
                    return RedirectToAction("ViewCart");
                }
            
            else
            {
                return RedirectToAction("ViewCart");
           } 
        }
        
        public ActionResult Remove(int prodID)
        {
            ProductVisitRepository prodVis = new ProductVisitRepository();
            prodVis.RemoveProductVisit(prodID);

            return RedirectToAction("ViewCart");
        }

        public ActionResult Cancel()
        {
            string sessionID = Session.SessionID;
            ProductVisitRepository prodVisRep = new ProductVisitRepository();
            prodVisRep.DeleteProductVisits(sessionID);
            Session.Abandon();
            return RedirectToAction("ThankYou");
        }

        public ActionResult ThankYou()
        {
            return View();
        }

        [HttpGet]
        public ActionResult Email()
        {
            return View();
        }

        [HttpPost]
        public ActionResult Email(Email message)
        {
            EmailHelper emailHelper = new EmailHelper();
            string result = emailHelper.SendMessage(message);
            return RedirectToAction("Result", new { mailResult = result });
        }

        public ActionResult Result(string mailResult)
        {
            ViewBag.EmailResult = mailResult;
            return View();
        }
    }
}

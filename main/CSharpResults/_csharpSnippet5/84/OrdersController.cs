using System;
using System.Collections.Generic;
using System.Data;
using System.Data.Entity;
using System.Linq;
using System.Web;
using System.Web.Mvc;
using BeerShop.Models;
using System.Net;
using System.Text;
using System.IO;

namespace BeerShop.Controllers
{
    public class OrdersController : Controller
    {
        private BeerShopContext db = new BeerShopContext();

        public ActionResult bootstrapIndex()
        {
            return RedirectToAction("Index", "Orders");
        }
        //
        // GET: /Orders/
        [Authorize(Roles = "Customer, Admin")]
        public ActionResult Index(int filter =0)
        {
            User loggedCustomer =(from u in db.Users
                                                 where u.email == User.Identity.Name
                                                 select u).First();
            ViewBag.loggedCustomer = loggedCustomer;
            if (loggedCustomer is Customer)
            {
                return View(db.Orders.ToList().Where(item => item.status >= Order.processing).Where(item => item.customer.UserID == loggedCustomer.UserID));
            }
            else
            {
                var ordersList = db.Orders.ToList().AsQueryable();
                if (filter != 0)
                {
                    ordersList = from o in ordersList where o.status == filter select o;
                }
                return View(ordersList.ToList());
            }
            //return View(db.Orders.ToList());
        }

        //
        // GET: /Orders/Details/5
        [Authorize(Roles = "Customer, Admin")]
        public ActionResult Details(int id = 0)
        {
            Order order;
            if(id != 0)
                order = db.Orders.Find(id);
            else
            {
                order = db.Orders.FirstOrDefault(o => o.customer.email == User.Identity.Name);
            }
            if (order == null)
            {
                return View();
            }
            double totalPrice = 0;
            foreach (OrderItem oI in order.orderItems)
            {
                totalPrice += oI.price * oI.amount;
            }
            ViewBag.totalPrice = totalPrice;
            
            return View(order);
        }

        //
        // GET: /Orders/Create
        [Authorize(Roles = "Customer")]
        public ActionResult Create()
        {
            Customer loggedCustomer = (Customer)(from u in db.Users
                                                 where u.email == User.Identity.Name
                                                 select u).First();

            if (loggedCustomer.basket.orderItems.Count == 0)
            {
                ModelState.AddModelError("", "Sorry! The basket is empty!");
                return RedirectToAction("Details", "Baskets", new { id = loggedCustomer.basket.BasketID });
            }
            return Create(new Order());

        }

        //
        // POST: /Orders/Create
        [Authorize(Roles = "Customer")]
        [HttpPost]
        public ActionResult Create(Order order)
        {

            Customer loggedCustomer = (Customer)(from u in db.Users
                                                 where u.email == User.Identity.Name
                                                 select u).First();
            if (loggedCustomer.basket.orderItems.Count != 0)
            {
                //remove other orders without been finished
                //var pendingOrders = from o in db.Orders
                //                    where o.customer.UserID == loggedCustomer.UserID
                //                    && o.status == Order.pending
                //                    select o;

                //foreach (Order o in pendingOrders)
                //{
                //    db.Orders.Remove(o);
                //}
                //db.SaveChanges();

                //modify user
                order.customer = loggedCustomer;
                //Change status, createDate 
                order.status = Order.pending;
                order.createdDate = DateTime.UtcNow;

                //Set rest of the dates to null (for us: maxValue for DateTime type)
                order.dispachedDate = DateTime.MaxValue.Date;
                order.deliveredDate = DateTime.MaxValue.Date;

                //adding the items from the basket with the current price of the item to the OrderItem list
                order.orderItems = new List<OrderItem>();
                foreach (OrderItem oI in loggedCustomer.basket.orderItems)
                {
                    oI.price = oI.item.Price;
                    order.orderItems.Add(oI);
                }

                if (ModelState.IsValid)
                {
                    db.Orders.Add(order);
                    db.SaveChanges();
                    //modify User data --> address (in User controller)
                    return RedirectToAction("EditOrder", "Users", new { id = order.customer.UserID });
                }
            }
            //modify basket (in Basket controller)
            ModelState.AddModelError("", "Sorry! The basket is empty!");
            return RedirectToAction("Details", "Baskets", new { id = loggedCustomer.basket.BasketID });
        }

        //
        // GET: /Orders/Edit/5
        [Authorize(Roles = "Admin")]
        public ActionResult Edit(int id = 0)
        {
            //SelectList sl = new SelectList(new List<string>() { "1. pending", "2. processing", "3. dispached", "4. delivered", "5. canceled" ,"6. paying" });
            //ViewBag.SelectList = sl;

            //get current order
            Order order = db.Orders.Find(id);
            TempData["Something"] = order.orderItems;
            if (order == null)
            {
                return HttpNotFound();
            }
            return View(order);
        }

        //
        // POST: /Orders/Edit/5
        [Authorize(Roles = "Admin")]
        [HttpPost]
        public ActionResult Edit(Order order)
        {
            order.customer = (Customer)(from u in db.Users
                                        where u.UserID == order.customer.UserID
                                        select u).First();

            if (order.status == 3)
            {
                order.dispachedDate = DateTime.Now;
            }
            else
            {

                if (order.status == 4)
                {
                    if (order.dispachedDate == DateTime.MaxValue){
                        order.dispachedDate = DateTime.Now;
                    }
                    order.deliveredDate = DateTime.Now;
                }
                else
                {
                    if (order.status == 5 || order.status == 2)
                    {
                        order.dispachedDate = DateTime.MaxValue;
                        order.deliveredDate = DateTime.MaxValue;
                    }
                    else
                    {
                        ModelState.AddModelError("", "Sorry! Invalid status value");
                        return View(order);
                    }
                }
            }
            order.orderItems = (ICollection<OrderItem>)TempData["Something"];
            //if the modelState is not valid, we save it again
            TempData["Something"] = order.orderItems;
            if (!ModelState.IsValid)
            {
                db.Entry(order).State = EntityState.Modified;
                db.SaveChanges();
                MailSender mS = new MailSender();
                string body = "Your order status has changed!: ";
                string status;
                switch (order.status)
                {
                    case 1: status = "pending";
                        break;
                    case 2: status = "processing";
                        break;
                    case 3: status = "dispached";
                        break;
                    case 4: status = "delivered";
                        break;
                    case 5: status = "canceled";
                        break;
                    case 6: status = "paying";
                        break;
                    default: status = "wtf";
                        break;
                }

                mS.SendMail(User.Identity.Name, order.OrderID + ": " + body + status +"\n\n "
                     + "http://" + Request.Url.Authority + Url.RouteUrl(new { controller = "Orders", action = "Details", id = order.OrderID }));
                return RedirectToAction("Index");
            }
            //String messages = String.Join(Environment.NewLine, ModelState.Values.SelectMany(v => v.Errors).Select(v => v.ErrorMessage + " " + v.Exception));
            return View(order);
        }

        //We dont allow to delete the Orders

        //
        // GET: /Orders/Delete/5
        //public ActionResult Delete(int id = 0)
        //{
        //    Order order = db.Orders.Find(id);
        //    if (order == null)
        //    {
        //        return HttpNotFound();
        //    }
        //    return View(order);
        //}

        ////
        //// POST: /Orders/Delete/5
        //[HttpPost, ActionName("Delete")]
        //public ActionResult DeleteConfirmed(int id)
        //{
        //    //delete orderitem and basket?
        //    Order order = db.Orders.Find(id);
        //    db.Orders.Remove(order);
        //    db.SaveChanges();
        //    return RedirectToAction("Index");
        //}

        protected override void Dispose(bool disposing)
        {
            db.Dispose();
            base.Dispose(disposing);
        }

        //
        // GET: /Orders/DetailsOrderItems/5
        // display all the items of the order, if we decided to modify it, we will be redirected to the basket to modify
        [Authorize(Roles = "Customer")]
        public ActionResult DetailsOrderItems(int id = 0)
        {
            //get current order
            Order order = db.Orders.Find(id);
            //calculate total price
            double totalPrice = 0;
            foreach (OrderItem oI in order.orderItems)
            {
                totalPrice += oI.price * oI.amount;
            }
            ViewBag.totalPrice = totalPrice;
            if (order == null)
            {
                return HttpNotFound();
            }
            return View(order);
        }

        //
        // GET: /Orders/Confirmation/5
        //confirm all the data before paying
        [Authorize(Roles = "Customer")]
        public ActionResult Confirmation(int id = 0)
        {
            //get current order
            Order order = db.Orders.Find(id);
            //calculate total price
            double totalPrice = 0;
            foreach (OrderItem oI in order.orderItems)
            {
                totalPrice += oI.price * oI.amount;
            }
            ViewBag.totalPrice = totalPrice;
            if (order == null)
            {
                return HttpNotFound();
            }
            return View(order);
        }

        //
        // GET: /Orders/Payment/5
        [Authorize(Roles = "Customer")]
        public void Payment(int id = 0)
        {

            Order order = db.Orders.Find(id);

            double totalPrice = 0;
            foreach (OrderItem oI in order.orderItems)
            {
                totalPrice += oI.price * oI.amount;
            }

            //Pay pal process Refer for what are the variable are need to send http://www.paypalobjects.com/IntegrationCenter/ic_std-variable-ref-buy-now.html

            string redirecturl = "";

            //Mention URL to redirect content to paypal site
            redirecturl += "https://www.sandbox.paypal.com/cgi-bin/webscr?cmd=_xclick";

            //Business contact id
            redirecturl += "&business=beershop12345-facilitator@gmail.com";

            //First name 
            redirecturl += "&first_name=" + order.customer.firstName;

            //Last name
            redirecturl += "&last_name=" + order.customer.lastName;

            //Address
            redirecturl += "&address_override=" + order.customer.address;

            //Product Name --> "for us is the order itself"
            redirecturl += "&item_name=" + "Order" + order.OrderID + ": " + order.orderItems.First().item.name + " ...";

            //Product Amount --> price
            redirecturl += "&amount=" + totalPrice;

            //Shipping charges if any
            //redirecturl += "&shipping=5";

            //Handling charges if any
            //redirecturl += "&handling=5";

            //Tax amount if any
            //redirecturl += "&tax=5";

            //Currency code 
            redirecturl += "&currency=DKK";

            //Failed return page url
            redirecturl += "&notify_url=" + "http://" + Request.Url.Authority + Url.RouteUrl(new { controller = "Orders", action = "PaypalNotify", id = order.OrderID });

            //Success return page url
            redirecturl += "&return=" + "http://" + Request.Url.Authority + Url.RouteUrl(new { controller = "Orders", action = "Index"});
            //redirecturl += "&return=" + "http://" + Request.Url.Authority + Url.RouteUrl(new { controller = "Orders", action = "Finish", id = order.OrderID });

            //Failed return page url
            redirecturl += "&cancel_return=" + "http://" + Request.Url.Authority + Url.RouteUrl(new { controller = "Baskets", action = "Details", id = order.customer.basket.BasketID });

            order.status = Order.paying;

            db.Entry(order).State = EntityState.Modified;
            db.SaveChanges();

            Response.Redirect(redirecturl);

            //return RedirectToAction("Index");
        }

        public void Finish(int id = 0)
        {
            //get current order
            Order order = db.Orders.Find(id);
            //calculate total price
            foreach (var item in order.orderItems)
            {
                int subtract = item.amount;
                item.item.stockCount = item.item.stockCount - subtract;
            }
            order.customer.basket.orderItems = new List<OrderItem>();
            order.status = Order.processing;
            order.createdDate = DateTime.UtcNow
;
            order.customer.basket = new Basket();

            MailSender mS = new MailSender();
            string body = "Your order is finish!";
            mS.SendMail(order.customer.email, "Order #" +order.OrderID + ": " + body + "\n\n "
                     + "http://" + Request.Url.Authority + Url.RouteUrl(new { controller = "Orders", action = "Details", id = order.OrderID }));

            db.Entry(order).State = EntityState.Modified;
            db.SaveChanges();

            //if (order == null)
            //{
            //    return HttpNotFound();
            //}
            //return RedirectToAction("Index");
        }

        public ActionResult PaypalNotify(int id = 0)
        {

            //Post back to either sandbox or live
            string strSandbox = "https://www.sandbox.paypal.com/cgi-bin/webscr";
            HttpWebRequest req = (HttpWebRequest)WebRequest.Create(strSandbox);

            //Set values for the request back
            req.Method = "POST";
            req.ContentType = "application/x-www-form-urlencoded";
            byte[] param = Request.BinaryRead(Request.ContentLength);
            string strRequest = Encoding.ASCII.GetString(param);
            strRequest += "&cmd=_notify-validate";
            req.ContentLength = strRequest.Length;

            //Send the request to PayPal and get the response
            StreamWriter streamOut = new StreamWriter(req.GetRequestStream(), System.Text.Encoding.ASCII);
            streamOut.Write(strRequest);
            streamOut.Close();
            StreamReader streamIn = new StreamReader(req.GetResponse().GetResponseStream());
            string strResponse = streamIn.ReadToEnd();
            streamIn.Close();


            if (strResponse == "VERIFIED")
            {
                return RedirectToAction("Finish", new { id = id });
            }
            else
            {
                if (strResponse == "INVALID")
                {
                    ModelState.AddModelError("", "Sorry! Something happened while you were paying, try again!");
                    return RedirectToAction("Confirmation", new { id = id });
                }
                else
                {
                    ModelState.AddModelError("", "Sorry! Something happened while you were paying, try again!");
                    return RedirectToAction("Index");
                }
            }
        }


        public ActionResult bootstrap()
        {
            return RedirectToAction("Index", "Home");
        }

        public ActionResult PayPalRedirect()
        {
            return Redirect("https://www.sandbox.paypal.com/us/cgi-bin/webscr?cmd=_login-submit");
        }

    }
}
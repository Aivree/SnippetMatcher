using System.Data.Entity;
using System.Linq;
using System.Threading.Tasks;
using System.Net;
using System.Web.Mvc;
using PizzeriaManagerDataLayer;
using System.Collections.Generic;
using System;
using System.Reflection;

namespace PizzeriaManagerWebClient.Controllers
{
    public class CreateOrderController : Controller
    {
        private PMContext db = new PMContext();

        [AttributeUsage(AttributeTargets.Method, AllowMultiple = false, Inherited = true)]
        public class MultipleButtonAttribute : ActionNameSelectorAttribute
        {
            public string Name { get; set; }
            public string Argument { get; set; }

            public override bool IsValidName(ControllerContext controllerContext, string actionName, MethodInfo methodInfo)
            {
                var isValidName = false;
                var keyValue = string.Format("{0}:{1}", Name, Argument);
                var value = controllerContext.Controller.ValueProvider.GetValue(keyValue);

                if (value != null)
                {
                    controllerContext.Controller.ControllerContext.RouteData.Values[Name] = Argument;
                    isValidName = true;
                }

                return isValidName;
            }
        }

        private void helperMethod()
        {
            var GenreLstP = new List<SelectListItem>();
            var GenreLstA = new List<SelectListItem>();

            var pizzaQry = from d in db.Merchandises.OfType<Pizza>()
                           where d.Name != " " && d != null && d.Name != null
                           orderby d.Name
                           select new SelectListItem { Text = d.Name, Value = d.Name };
            var additionQry = from d in db.Merchandises.OfType<Addition>()
                              where d.Name != "" && !d.Name.Contains("System")
                              orderby d.Name
                              select new SelectListItem { Text = d.Name, Value = d.Name };

            GenreLstP.AddRange(pizzaQry.Distinct());
            GenreLstA.AddRange(additionQry.Distinct());

            ViewBag.pizzasGenre = GenreLstP;
            ViewBag.additionsGenre = GenreLstA;
        }

        // GET: /CreateOrder/
        public ActionResult Index()
        {
            helperMethod();
            WebOrder wo = new WebOrder();
            wo.AdditionQuantity = 0;
            wo.PizzaQuantity = 0;
            if (System.Web.HttpContext.Current.Session["Basket"] != null)
            {
                wo.Basket = System.Web.HttpContext.Current.Session["Basket"] as List<OrderItem>;
                return User.Identity.IsAuthenticated ? View(wo) : View("NoLogin");
            }
            return User.Identity.IsAuthenticated ? View(wo) : View("NoLogin");
        }

        // POST: /CreateOrder/
        // To protect from overposting attacks, please enable the specific properties you want to bind to, for 
        // more details see http://go.microsoft.com/fwlink/?LinkId=317598.
        [HttpPost]
        [MultipleButton(Name = "submit", Argument = "index")]
        [ValidateAntiForgeryToken]
        public async Task<ActionResult> Index([Bind(Include = "WebOrderId,PizzaName,PizzaSizeValue,PizzaQuantity,AdditionName,AdditionQuantity")] WebOrder weborder)
        {
            if (ModelState.IsValid)
            {
                if ((weborder.AdditionQuantity>0 || weborder.PizzaQuantity>0) && weborder.PizzaName!= String.Empty && weborder.AdditionName != String.Empty )
                {
                    var clients = await db.Clients.Where(cl => cl.WebIdentityName == User.Identity.Name).ToListAsync();

                    var pizzas = from d in db.Merchandises.OfType<Pizza>()
                                 where d.Name == weborder.PizzaName
                                 select d;
                    Pizza pizza = pizzas.First();
                    PizzaOrderItem p = new PizzaOrderItem();

                    db.Merchandises.Attach(pizza);
                    await Task.Run(async () =>
                    {
                        await db.Entry(pizza).Collection(pi => pi.Ingrediens).LoadAsync();
                    });

                    Addition addition = new Addition();
                    addition.Name = weborder.AdditionName;

                    p.Recipe = pizza;
                    p.ItemSize = weborder.PizzaSizeValue;
                    p.Quantity = weborder.PizzaQuantity;

                    AdditionOrderItem a = new AdditionOrderItem();
                    a.Quantity = weborder.AdditionQuantity;
                    a.Kind = addition;

                    Order order = new Order();
                    order.Contractor = clients.First();

                    List<OrderItem> o = new List<OrderItem>();
                    order.OrderList = o;
                    if (weborder.PizzaQuantity > 0)
                    {
                        o.Add(p);
                    }
                    if (weborder.AdditionQuantity > 0)
                    {
                        o.Add(a);
                    }

                    if (System.Web.HttpContext.Current.Session["Basket"] == null)
                        System.Web.HttpContext.Current.Session["Basket"] = new List<OrderItem>();
                    var Basket = System.Web.HttpContext.Current.Session["Basket"] as List<OrderItem>;
                    Basket.AddRange(o);
                    weborder.Basket = Basket;

                    db.Orders.Add(order);
                    await db.SaveChangesAsync();
                    helperMethod();

                    return User.Identity.IsAuthenticated ? View(weborder) : View("NoLogin"); 
                }
                else
                {
                    helperMethod();
                    var Basket = System.Web.HttpContext.Current.Session["Basket"] as List<OrderItem>;
                    weborder.Basket = Basket;
                    ViewBag.Message = "Niepoprawne dane, spróbuj ponownie";
                    return User.Identity.IsAuthenticated ? View(weborder) : View("NoLogin"); 
                }
            }

            return User.Identity.IsAuthenticated ? View() : View("NoLogin"); 
        }

        [HttpPost]
        [MultipleButton(Name = "submit", Argument = "accepted")]
        [ValidateAntiForgeryToken]
        public ActionResult Accept()
        {
            return RedirectToAction("Accepted");
        }

        public ActionResult Accepted()
        {

            var order = System.Web.HttpContext.Current.Session["Basket"];
            System.Web.HttpContext.Current.Session["Basket"] = null;
            return User.Identity.IsAuthenticated ? View(order) : View("NoLogin"); 
        }

        protected override void Dispose(bool disposing)
        {
            if (disposing)
            {
                db.Dispose();
            }
            base.Dispose(disposing);
        }
    }
}

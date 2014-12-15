using System;
using System.Collections.Generic;
using System.ComponentModel.DataAnnotations;
using System.Data.Linq.Mapping;
using System.Linq;
using System.Net;
using System.Net.Mail;
using System.Text;
using System.Web;
using System.IO;
using System.Text.RegularExpressions;
using System.Web.Caching;
using System.Web.Http;
using System.Web.Mvc;
using Community.SocialMediaTools;
using DotNetOpenAuth.Messaging;
using MartinLayooInc.Data.Access.Domain.Model;
using MartinLayooIncMVC.Business;
using MartinLayooIncMVC.Infrastructure;
using MartinLayooIncMVC.Models;
using MartinLayooIncMvc.Data.Access;
using UPAEventsPayPal;
using MartinLayooInc.Caching;

namespace MartinLayooIncMVC.Controllers
{
    public class HomeController : Controller
    {
        public ActionResult Index()
        {
            ViewBag.Message = "Modify this template to jump-start your ASP.NET MVC application.";
            ViewBag.Selected = "home";
            ViewBag.LoggedIn = ControllerContext.HttpContext.User.Identity.IsAuthenticated;
            return View();
        }

        [System.Web.Mvc.Authorize(Users = "Administrator")]
        public ActionResult ChartStatistics()
        {
            return View();
        }
        public PartialViewResult GetProductsForDisplay()
        {
            var model = (IEnumerable<SHOP_PRODS>)Session["Model"];

            if (model == null)
            {
                model = MartinBusiness.GetAllProducts();
                Session["Model"] = model;
            }

            var pictureUrlDictionary = new Dictionary<string, string>();
            var productWebService = new ProductsWebService();


            var shopProdss = model as SHOP_PRODS[] ?? model.ToArray();
            if (Session["PagingInfo"] == null)
            {
                const int numberOfItemsPerPage = 3;
                const int numberOfPagesToDisplay = 3;
                var numberOfPages = shopProdss.Count() / numberOfItemsPerPage;
                var modNumberOfPages = shopProdss.Count() % numberOfItemsPerPage;

                if (modNumberOfPages > 0) numberOfPages += 1;
                var pagingMechanism = InitialisePageInfoCurrent(numberOfPages, numberOfPagesToDisplay);
                Session["PagingInfo"] = pagingMechanism;
            }

            var paging = Session["PagingInfo"] as Paging;
            if (paging != null)
            {
                paging.PagingInfoDetails.CurrentPage = (int)Session["CurrentPage"];
                (Session["PagingInfo"] as Paging).PagingInfoDetails.CurrentPageIndex = (int)Session["CurrentPage"] - 1;
            }

            int skip = 0;

            if (paging != null)
                skip = ((Session["PagingInfo"] as Paging).PagingInfoDetails.CurrentPage - 1) * 3;
            var actualModel = shopProdss.Skip(skip).Take(((Paging)Session["PagingInfo"]).PagingInfoDetails.NumberOfItemsPerPage);

            foreach (var product in actualModel)
            {
                var prodImageInfo = productWebService.GetPictureInfo(product.prodId);

                pictureUrlDictionary.Add(product.prodId, prodImageInfo.ThumbUrl);
            }

            ViewBag.ProductUrls = pictureUrlDictionary;

            return PartialView("_PartialProductInfo", actualModel);
        }

        [System.Web.Mvc.HttpGet]
        public ActionResult Products(int page = 1)
        {
            Session["CurrentPage"] = page;
            if (Session["Model"] == null) GetProductsForDisplay();
            ViewBag.Message = "Products page";
            ViewBag.Selected = "products";
            ViewBag.LoggedIn = ControllerContext.HttpContext.User.Identity.IsAuthenticated;
            GetPartialViewData();
            return View("Products");
        }


        public ActionResult Contact()
        {
            ViewBag.Message = "Your contact page.";
            ViewBag.Selected = "contact";
            ViewBag.LoggedIn = ControllerContext.HttpContext.User.Identity.IsAuthenticated;
            GetPartialViewData();

            return View();
        }

        [System.Web.Mvc.HttpGet]
        public ActionResult Basket(int page = 1)
        {
            ViewBag.Message = "Basket page";
            ViewBag.Selected = "basket";
            ViewBag.Message = "Basket page";
            ViewBag.LoggedIn = ControllerContext.HttpContext.User.Identity.IsAuthenticated;
            GetPartialViewData();

            Session["CurrentPageBasket"] = page;

            var model = (IEnumerable<SHOP_PRODS>)Session["ShoppingBasket"];

            if (model == null)
            {
                Session["ShoppingBasket"] = new List<SHOP_PRODS>();
                model = (IEnumerable<SHOP_PRODS>)Session["ShoppingBasket"];
            }
            ViewBag.LoggedIn = ControllerContext.HttpContext.User.Identity.IsAuthenticated;
            const int numberOfItemsPerPage = 3;
            const int numberOfPagesToDisplay = 3;
            var shopProdss = model as SHOP_PRODS[] ?? model.ToArray();
            var numberOfPages = shopProdss.Count() / numberOfItemsPerPage;
            var modNumberOfPages = shopProdss.Count() % numberOfItemsPerPage;

            if (Session["PagingInfo2"] == null)
            {
                if (modNumberOfPages > 0) numberOfPages += 1;
                var pagingMechanism = InitialisePageInfoCurrent(numberOfPages, numberOfPagesToDisplay);
                Session["PagingInfo2"] = pagingMechanism;
            }
            else
            {
                if (modNumberOfPages > 0) numberOfPages += 1;
                var pagingMechanism = (Paging)Session["PagingInfo2"];
                pagingMechanism.PagingInfoDetails.NumberOfPages = numberOfPages;
            }
            Session["CurrentPageBasket"] = Session["CurrentPageBasket"] == null ? 1 : (int)Session["CurrentPageBasket"];
            var paging = Session["PagingInfo2"] as Paging;
            if (paging != null)
            {
                paging.PagingInfoDetails.CurrentPage = (int) Session["CurrentPageBasket"];
                paging.PagingInfoDetails.CurrentPageIndex =
                    (int) Session["CurrentPageBasket"] - 1;
            }

            var skip = ((Session["PagingInfo2"] as Paging).PagingInfoDetails.CurrentPage - 1) * 3;

            model = shopProdss.Skip(skip).Take(((Paging)Session["PagingInfo2"]).PagingInfoDetails.NumberOfItemsPerPage);
            return View(model);


        }
        [System.Web.Mvc.HttpGet]
        public ActionResult BasketRemoveProduct()
        {
            ViewBag.Message = "Basket page";
            ViewBag.Selected = "basket";
            ViewBag.LoggedIn = ControllerContext.HttpContext.User.Identity.IsAuthenticated;

            var model = (IList<SHOP_PRODS>)Session["ShoppingBasket"];
            if (model == null)
            {

                model = new List<SHOP_PRODS>();
                Session["ShoppingBasket"] = model;
            }
            return View("Basket", model);
        }
        [System.Web.Mvc.HttpPost]
        public ActionResult BasketRemoveProduct(SHOP_PRODS product)
        {
            ViewBag.Message = "Basket page";
            ViewBag.Selected = "basket";
            ViewBag.LoggedIn = ControllerContext.HttpContext.User.Identity.IsAuthenticated;
            GetPartialViewData();

            var model = (IList<SHOP_PRODS>)Session["ShoppingBasket"];
            if (model != null)
            {
                if (product != null)
                {
                    var prod = model.SingleOrDefault(p => p.prodId == product.prodId);
                    model.Remove(prod);
                    Session["ShoppingBasket"] = model;
                }
            }
            else
            {
                model = new List<SHOP_PRODS>();
                Session["ShoppingBasket"] = model;
            }
            return View("Basket", model);
        }
        [System.Web.Mvc.HttpPost]
        public ActionResult Basket(IList<SHOP_PRODS> products)
        {
            ViewBag.Message = "Basket page";
            ViewBag.LoggedIn = ControllerContext.HttpContext.User.Identity.IsAuthenticated;
            var model = (IEnumerable<SHOP_PRODS>)Session["ShoppingBasket"];

            if (model != null)
            {
                ViewBag.LoggedIn = ControllerContext.HttpContext.User.Identity.IsAuthenticated;

                var shopProdss = model as SHOP_PRODS[] ?? model.ToArray();
                if (Session["PagingInfo2"] == null)
                {
                    const int numberOfItemsPerPage = 3;
                    const int numberOfPagesToDisplay = 3;
                    var numberOfPages = shopProdss.Count() / numberOfItemsPerPage;
                    var modNumberOfPages = shopProdss.Count() % numberOfItemsPerPage;

                    if (modNumberOfPages > 0) numberOfPages += 1;
                    var pagingMechanism = InitialisePageInfoCurrent(numberOfPages, numberOfPagesToDisplay);
                    Session["PagingInfo2"] = pagingMechanism;
                }
                Session["CurrentPageBasket"] = Session["CurrentPageBasket"] == null ? 1 : (int)Session["CurrentPageBasket"];
                var paging1 = Session["PagingInfo2"] as Paging;
                if (paging1 != null)
                    paging1.PagingInfoDetails.CurrentPage = (int)Session["CurrentPageBasket"];
                var paging2 = Session["PagingInfo2"] as Paging;
                if (paging2 != null)
                    paging2.PagingInfoDetails.CurrentPageIndex = (int)Session["CurrentPageBasket"] - 1;


                var paging = Session["PagingInfo2"] as Paging;
                if (paging != null)
                {
                    var skip = (paging.PagingInfoDetails.CurrentPage - 1) * 3;

                    model = shopProdss.Skip(skip).Take(((Paging)Session["PagingInfo2"]).PagingInfoDetails.NumberOfItemsPerPage);
                }
                GetPartialViewData();
                return View(model);
            }
            return View("Index");
        }

        [System.Web.Mvc.HttpPost]
        public ActionResult AddToBasket(string productId)
        {
            var product = MartinBusiness.GetProductById(productId);
            var basket = (IList<SHOP_PRODS>)Session["ShoppingBasket"];
            if (basket == null)
            {
                basket = new List<SHOP_PRODS>();
                Session["ShoppingBasket"] = basket;
            }
            if (!basket.Contains(product))
                basket.Add(product);

            return RedirectToAction("Basket");
        }
        public ActionResult Payment()
        {
            ViewBag.Message = "Basket page";
            ViewBag.Selected = "payment";
            ViewBag.LoggedIn = ControllerContext.HttpContext.User.Identity.IsAuthenticated;
            GetPartialViewData();


            return View();
        }
        public ActionResult Blogs(int page = 1)
        {
            Session["CurrentPageBlog"] = page;
            ViewBag.Message = "Blogs page";
            ViewBag.Selected = "blogs";
            ViewBag.LoggedIn = ControllerContext.HttpContext.User.Identity.IsAuthenticated;
            
                GetPartialViewData();

            IEnumerable<ForumReview> model;

            if (Session["BlogsModel"] == null)
            {
                model = MartinBusiness.GetForums();
                Session["BlogsModel"] = model;
            }
            model = (IEnumerable<ForumReview>)Session["BlogsModel"];

            if (model != null)
            {
                ViewBag.LoggedIn = ControllerContext.HttpContext.User.Identity.IsAuthenticated;

                var forumReviews = model as ForumReview[] ?? model.ToArray();
                if (Session["PagingInfoBlog"] == null)
                {
                    const int numberOfItemsPerPage = 3;
                    const int numberOfPagesToDisplay = 3;
                    var numberOfPages = forumReviews.Count()/numberOfItemsPerPage;
                    var modNumberOfPages = forumReviews.Count()%numberOfItemsPerPage;

                    if (modNumberOfPages > 0) numberOfPages += 1;
                    var pagingMechanism = InitialisePageInfoCurrent(numberOfPages, numberOfPagesToDisplay);
                    Session["PagingInfoBlog"] = pagingMechanism;
                }
                Session["CurrentPageBlog"] = Session["CurrentPageBlog"] == null ? 1 : (int) Session["CurrentPageBlog"];
                var paging = Session["PagingInfoBlog"] as Paging;
                if (paging != null)
                {
                    paging.PagingInfoDetails.CurrentPage = (int) Session["CurrentPageBlog"];
                    paging.PagingInfoDetails.CurrentPageIndex =
                        (int) Session["CurrentPageBlog"] - 1;


                    var skip = (paging.PagingInfoDetails.CurrentPage - 1)*3;
                    model =
                        forumReviews.Skip(skip)
                            .Take((paging.PagingInfoDetails.NumberOfItemsPerPage));
                    return View(model);
                }
            }
            return View("Index");
        }

        FacebookOauth<string> _oAuth = new FacebookOauth<string>();


        public ContentResult GetFacebookDetails()
        {
            var cachingEngine = new Caching<string>();
            cachingEngine._cache = HttpContext.Cache;


            var json = (string)System.Web.HttpContext.Current.Cache.Get("FacebookFeeds");
            if(string.IsNullOrEmpty(json))
                json = cachingEngine.GetFromCache("FacebookFeeds", 500, _oAuth.GetFBJsonString);

            return new ContentResult { Content = json, ContentType = "application/json" };

        }

        public ActionResult AboutUs()
        {
            ViewBag.Message = "About us page";
            ViewBag.Selected = "aboutus";
            ViewBag.LoggedIn = ControllerContext.HttpContext.User.Identity.IsAuthenticated;
            GetPartialViewData();
            if (Request["code"] == null)
            {
                Response.Redirect(_oAuth.AuthorizationLinkGet());
                Response.End();
            }
            return View();
        }
        public ActionResult Reviews(int page = 1)
        {
            Session["CurrentPageReview"] = page;
            ViewBag.Message = "Reviews page";
            ViewBag.Selected = "reviews";
            ViewBag.LoggedIn = ControllerContext.HttpContext.User.Identity.IsAuthenticated;
            GetPartialViewData();

            IEnumerable<ForumReview> model;

            if (Session["ReviewsModel"] == null)
            {
                model = MartinBusiness.GetReviews();
                Session["ReviewsModel"] = model;
            }
            model = (IEnumerable<ForumReview>)Session["ReviewsModel"];


            if (model != null)
            {
                ViewBag.LoggedIn = ControllerContext.HttpContext.User.Identity.IsAuthenticated;

                var forumReviews = model as ForumReview[] ?? model.ToArray();
                if (Session["PagingInfoReview"] == null)
                {
                    const int numberOfItemsPerPage = 3;
                    const int numberOfPagesToDisplay = 3;
                    var numberOfPages = forumReviews.Count() / numberOfItemsPerPage;
                    var modNumberOfPages = forumReviews.Count() % numberOfItemsPerPage;

                    if (modNumberOfPages > 0) numberOfPages += 1;
                    var pagingMechanism = InitialisePageInfoCurrent(numberOfPages, numberOfPagesToDisplay);
                    Session["PagingInfoReview"] = pagingMechanism;
                }
                Session["CurrentPageReview"] = Session["CurrentPageReview"] == null ? 1 : (int)Session["CurrentPageReview"];
                var paging = Session["PagingInfoReview"] as Paging;
                if (paging != null)
                {
                    paging.PagingInfoDetails.CurrentPage = (int) Session["CurrentPageReview"];
                    paging.PagingInfoDetails.CurrentPageIndex =
                        (int) Session["CurrentPageReview"] - 1;


                    var skip = (paging.PagingInfoDetails.CurrentPage - 1)*3;
                    model =
                        forumReviews.Skip(skip)
                            .Take(paging.PagingInfoDetails.NumberOfItemsPerPage);
                return View(model);
                }

                return View("Index");
            }
            return View("Index");
        }

        [System.Web.Mvc.HttpGet]
        public ActionResult Product(string prodId)
        {
            ViewBag.Message = "Products page";
            ViewBag.Selected = "product";
            ViewBag.LoggedIn = ControllerContext.HttpContext.User.Identity.IsAuthenticated;
            GetPartialViewData();

            var model = MartinBusiness.GetProductById(prodId);

            ProductsWebService webService = new ProductsWebService();
            var pictureInfo = webService.GetPictureInfo(prodId);
            ViewBag.ThumbUrl = pictureInfo.ThumbUrl;

            return View(model);
        }

        public ActionResult SubmitEmail()
        {
            return View("Contact");
        }

        [System.Web.Mvc.HttpPost]
        [System.Web.Mvc.Authorize]
        [ValidateInput(false)] 
        public ActionResult SubmitEmail(FormCollection forms, HttpPostedFileBase file)
        {
            GetPartialViewData();
            ViewBag.LoggedIn = ControllerContext.HttpContext.User.Identity.IsAuthenticated;

            if (forms["SubmitToBlogs"] != null) return SubmitToForums(forms);
            if (forms["SubmitToReviews"] != null) return SubmitToReviews(forms);
            string[] emailTo = null;
            var emailRegEx = @"\w+([-+.']\w+)*@\w+([-.]\w+)*\.\w+([-.]\w+)*";
            var emailFormat = new EmailAddressAttribute();

            if (string.IsNullOrWhiteSpace(forms["mailFrom"]) || !Regex.IsMatch(forms["mailFrom"], emailRegEx))
            {
                ModelState.AddModelError("", "Your email is required or not in correct format");
            }
            if (string.IsNullOrWhiteSpace(forms["mailTo"]))
            {
                ModelState.AddModelError("", "The To email field is required or not in correct format");
            }
            else
            {
                emailTo = forms["mailTo"].Split(new[] {',', ';'}, StringSplitOptions.RemoveEmptyEntries);

                if (emailTo.Length == 0)
                {
                    ModelState.AddModelError("",
                        "There has got to be one receipient email address in the to text box");
                }
            }


            var message = new MailMessage();
            var isValidEmailTo = true;
            var mailAddressesTo = new List<MailAddress>();
            if (emailTo != null)
                foreach (var email in emailTo)
                {
                    mailAddressesTo.Add(new MailAddress(email));
                    if (!emailFormat.IsValid(email))
                    {
                        isValidEmailTo = false;
                        break;
                    }
                }

            if (!isValidEmailTo)
            {
                ModelState.AddModelError("", "Some of the values of the To email Field are invalid emails");
            }

            if (!string.IsNullOrEmpty(forms["MessageText"]) && forms["MessageText"].Length < 100)
            {
                ModelState.AddModelError("", "Your message should be at least 100 characters long");
            }

            var smtpUsername = System.Configuration.ConfigurationManager.AppSettings["BusinessEmail"];
            message.From = new MailAddress(smtpUsername);
            if (!ModelState.IsValid) return View("Contact");
            message.To.AddRange(mailAddressesTo);
            if (file != null && file.ContentLength > 0)
            {
                var fileName = Path.GetFileName(file.FileName);
                if (fileName == null) fileName = "[AttachedFile]";

                //Attach File to email

                var emailPassword = System.Configuration.ConfigurationManager.AppSettings["emailPassword"];
                var smtpClient = new SmtpClient(System.Configuration.ConfigurationManager.AppSettings["smtpServer"]);
                smtpClient.Credentials = new NetworkCredential {Password = emailPassword, UserName = smtpUsername};

                message.Subject = forms["mailFrom"] + ": " + forms["mailSubject"];
                message.Body = forms["MessageText"];

                message.Attachments.Add(new Attachment(file.InputStream, fileName));
                message.IsBodyHtml = true;

                smtpClient.Send(message);
                ViewBag.EmailSent = "Email was sent to all receipients: " + forms["mailTo"];
            }
            else
            {
                var emailPassword = System.Configuration.ConfigurationManager.AppSettings["emailPassword"];
                var smtpClient = new SmtpClient(System.Configuration.ConfigurationManager.AppSettings["smtpServer"]);
                smtpClient.Credentials = new NetworkCredential {Password = emailPassword, UserName = smtpUsername};

                message.Subject = forms["mailFrom"] + ": " + forms["mailSubject"];
                message.Body = forms["MessageText"];
                message.IsBodyHtml = true;

                smtpClient.Send(message);
                ViewBag.EmailSent = "Email was sent to all receipients: " + forms["mailTo"];
            }
            GetPartialViewData();
            return View("Contact");
        }

        [System.Web.Mvc.HttpPost]
        [System.Web.Mvc.Authorize]
        [ValidateInput(false)] 
        public ActionResult SubmitToForums(FormCollection forms)
        {
            GetPartialViewData();
            ViewBag.LoggedIn = ControllerContext.HttpContext.User.Identity.IsAuthenticated;

            if (!string.IsNullOrEmpty(forms["MessageText"]) && forms["MessageText"].Length < 100)
            {
                ModelState.AddModelError("", "Your message should be at least 100 characters long");
            }

            if (!ModelState.IsValid) return View("Contact");

            var forumPost = new ForumReview();

            forumPost.forum = forms["MessageText"].Replace(Environment.NewLine, "<br/>");
            forumPost.forum_flag = true;
            forumPost.timePosted = DateTime.Now;
            forumPost.userid = User.Identity.Name;
            MartinBusiness.SavePost(forumPost);

            return RedirectToAction("Blogs");
        }

        [System.Web.Mvc.HttpPost]
        [System.Web.Mvc.Authorize]
        [ValidateInput(false)] 
        public ActionResult SubmitToReviews(FormCollection forms)
        {
            GetPartialViewData();
            ViewBag.LoggedIn = ControllerContext.HttpContext.User.Identity.IsAuthenticated;

            if (!string.IsNullOrEmpty(forms["MessageText"]) && forms["MessageText"].Length < 100)
            {
                ModelState.AddModelError("", "Your message should be at least 100 characters long");
            }

            if (!ModelState.IsValid) return View("Contact");
            var forumPost = new ForumReview();

            forumPost.reviews = forms["MessageText"].Replace(Environment.NewLine, "<br/>");
            forumPost.review_flag = true;
            forumPost.timePosted = DateTime.Now;
            forumPost.userid = User.Identity.Name;
            MartinBusiness.SavePost(forumPost);

            return RedirectToAction("Reviews");
        }

        [System.Web.Mvc.Authorize]
        public ActionResult PostToPaypal(FormCollection forms)
        {
            GetPartialViewData();
            var emailRegEx = @"\w+([-+.']\w+)*@\w+([-.]\w+)*\.\w+([-.]\w+)*";

            if (forms["buyerEmail"] == null || !Regex.IsMatch(forms["buyerEmail"], emailRegEx))
            {
                ModelState.AddModelError("", "Your email is not in correct format");
            }
            if (Session["ShoppingBasket"] == null) return RedirectToAction("Products", "Home");
            var shoppingBasket = (IList<SHOP_PRODS>)Session["ShoppingBasket"];

            var upaProducts = shoppingBasket.Select(item => new Product { Ammount = item.prodPrice, ProductDescription = item.prodDesc, ProductName = item.prodName, Quantity = 1, VATAmmount = 0 }).ToList();
            var totalAmount = shoppingBasket.Sum(item => item.prodPrice);

            if (!(totalAmount > 0))
            {
                ModelState.AddModelError("", "Gross total should be more than $0.00");
            }

            if (!ModelState.IsValid)
            {
                return View("Payment");
            }
            var order = new Order();
            order.email = forms["email"];
            order.order_date = DateTime.Now;
            order.username = User.Identity.Name;
            order.status = "Unpaid";
            order.order_gross = totalAmount;

            order = MartinBusiness.InsertIntoOrder(order);

            Session["InvoiceNo"] = order.orderId;
            Session["ProductsUPA"] = upaProducts;
            Session["buyerEmail"] = order.email;

            foreach (var product in shoppingBasket)
            {
                MartinBusiness.InsertIntoOrderItem(new ItemOrder { numberOrdered = 1, order_id_fk = order.orderId, product_name = product.prodName, username = User.Identity.Name, password = MartinBusiness.GetUserPassword(User.Identity.Name) });
            }
            var context = HttpContext;
            //Process Payment
            var paypal = new PayPalHandler(context.ApplicationInstance.Context.Session, System.Configuration.ConfigurationManager.AppSettings["PaypalBaseUrl"],
                System.Configuration.ConfigurationManager.AppSettings["BusinessEmail"],
                System.Configuration.ConfigurationManager.AppSettings["SuccessUrl"],
                System.Configuration.ConfigurationManager.AppSettings["CancelUrl"],
                System.Configuration.ConfigurationManager.AppSettings["NotifyUrl"]);

            paypal.Response = context.ApplicationInstance.Context.Response;


            paypal.RedirectToPayPal();
            return View("Payment");
        }

        public ActionResult SearchContent(string searchTerm, int page = 1)
        {
            try
            {
                GetPartialViewData();
                Session["CurrentPageSearchContent"] = page;
                ViewBag.Message = "Search Content Results";
                ViewBag.Selected = "SearchContent";

                if (!string.IsNullOrEmpty(searchTerm))
                    Session["SearchTerm"] = searchTerm;

                searchTerm = (string)Session["SearchTerm"];

                IEnumerable<SearchContent> model = MartinBusiness.SearchContentFor(searchTerm);

                ViewBag.LoggedIn = ControllerContext.HttpContext.User.Identity.IsAuthenticated;
                const int numberOfItemsPerPage = 3;
                const int numberOfPagesToDisplay = 3;
                var searchContents = model as SearchContent[] ?? model.ToArray();
                var numberOfPages = searchContents.Count() / numberOfItemsPerPage;
                var modNumberOfPages = searchContents.Count() % numberOfItemsPerPage;

                if (Session["PagingInfoSearchContent"] == null)
                {
                    if (modNumberOfPages > 0) numberOfPages += 1;
                    var pagingMechanism = InitialisePageInfoCurrent(numberOfPages, numberOfPagesToDisplay);
                    Session["PagingInfoSearchContent"] = pagingMechanism;
                }
                else
                {
                    if (modNumberOfPages > 0) numberOfPages += 1;
                    var pagingMechanism = (Paging)Session["PagingInfoSearchContent"];
                    pagingMechanism.PagingInfoDetails.NumberOfPages = numberOfPages;
                }
                Session["CurrentPageSearchContent"] = Session["CurrentPageSearchContent"] == null
                                                          ? 1
                                                          : (int)Session["CurrentPageSearchContent"];
                var paging = Session["PagingInfoSearchContent"] as Paging;
                if (paging != null)
                {
                    paging.PagingInfoDetails.CurrentPage =
                        (int) Session["CurrentPageSearchContent"];
                    paging.PagingInfoDetails.CurrentPageIndex =
                        (int) Session["CurrentPageSearchContent"] - 1;


                    var skip = (paging.PagingInfoDetails.CurrentPage - 1)*3;
                    model =
                        searchContents.Skip(skip)
                            .Take(paging.PagingInfoDetails.NumberOfItemsPerPage);

                    return View(model);
                }
            }
            catch (Exception)
            {
                return View();
            }
            return View();
        }

        public ActionResult TwitterProfileFeeds()
        {
            //var xslFile = ControllerContext.HttpContext.Server.MapPath("~/App_Data/CommunityGroupHtml.xml");

            //var xslTranformer = new XslCompiledTransform();
            //var xsltReader = new XmlTextReader((new FileInfo(xslFile)).OpenText());
            //xslTranformer.Load(xsltReader);

            //var xmlFeedString = (new TwitterProfileFeed()).GetFeedXML();
            //var feedDoc = new XmlDocument();
            //feedDoc.LoadXml(xmlFeedString);
            //MemoryStream stream = new MemoryStream();
            //xslTranformer.Transform(feedDoc, null, stream);

            //var htmlResponse = string.Empty;

            //stream.Position = 0;
            //using (StreamReader reader = new StreamReader(stream))
            //{
            //    htmlResponse = reader.ReadToEnd();
            //}
            var caching = new Caching<WidgetGroupItemList>();
            caching._cache = System.Web.HttpContext.Current.Cache;
            var twitterEngine = new TwitterProfileFeed<WidgetGroupItemList>();

            var tweets = caching.GetFromCache("TwitterProfileFeeds", 500, twitterEngine.GetFeeds);
            if (tweets.Any())
            {
                ViewBag.TwitterProfileFeeds = tweets;
            }

            return PartialView("_PartialTweetProfileFeed");
        }

        public PartialViewResult TwitterSearchFeeds()
        {
            GetPartialViewData();
            return PartialView("_PartialTweetSearchFeed");
        }

        private void GetPartialViewData()
        {
            var caching = new Caching<WidgetGroupItemList>();
            caching._cache = System.Web.HttpContext.Current.Cache;
            var twitterEngine = new TwitterSearchFeed<WidgetGroupItemList>();

            var tweets = caching.GetFromCache("TwitterSearchFeeds", 500, twitterEngine.GetFeeds);
            if (tweets.Any())
            {
                ViewBag.TwitterSearchFeeds = tweets;
            }
        }


        private Paging InitialisePageInfoCurrent(int numberOfPages, int numberOfPagesToDisplay)
        {
            return new Paging(new PagingInfo
                {
                    CurrentPage = 1,
                    CurrentPageIndex = 0,
                    NumberOfPages = numberOfPages,
                    NumberOfPagesToDisplay = numberOfPagesToDisplay,
                    NumberOfItemsPerPage = 3
                });
        }

        public bool VerifyTransaction()
        {
            HttpContextBase context = HttpContext;
            var request = Request.BinaryRead(Request.ContentLength);
            var querystring = Encoding.ASCII.GetString(request);
            var form = HttpUtility.ParseQueryString(querystring);

            StreamWriter ipnWriter = null;
            bool isSuccess = false;
            try
            {
                var payPalInp = new InstantPaymentNotification(context.ApplicationInstance.Context.Request, accountEmail: System.Configuration.ConfigurationSettings.AppSettings["BusinessEmail"], form: form);
                FileInfo fileInfo = new FileInfo(context.Server.MapPath("~/IPNMessage.txt"));
                ipnWriter = fileInfo.CreateText();
                bool result = payPalInp.ProcessIPNResults(context.ApplicationInstance.Context, ipnWriter);

                string clientEmail = payPalInp.ClientEmail;

                //Send Emails to me and customer about failed payment!!
                var emailPassword = System.Configuration.ConfigurationManager.AppSettings["emailPassword"];
                var smtpUsername = System.Configuration.ConfigurationManager.AppSettings["BusinessEmail"];
                var smtpClient = new SmtpClient(System.Configuration.ConfigurationManager.AppSettings["smtpServer"]);
                smtpClient.Credentials = new NetworkCredential { Password = emailPassword, UserName = smtpUsername };


                var message = new MailMessage();
                message.From = new MailAddress(System.Configuration.ConfigurationManager.AppSettings["BusinessEmail"]);
                message.To.Add("martin.okello@martinlayooinc.co.uk");
                message.To.Add(clientEmail);
                message.Subject = "Result of your Transaction with MartinLayooInc.";

                if (result)
                {
                    message.Body = String.Format("Your account at Paypal at: {0} has been successfully credited with the required Payment.\nCongratulations", payPalInp.OrderDate.ToString("dd/MM/yyyy"));

                    smtpClient.Send(message);
                    isSuccess = true;
                }
                else
                {
                    message.Body = String.Format("The Transaction most recent at your Paypal Account at: {0} has failed. Sorry looks like your customer: " + clientEmail + ", is trying to fleece you!!\nSorry to be the bearer of Bad news\n\nKingPing Aka The Medallion", payPalInp.OrderDate.ToString("dd/MM/yyyy"));
                    smtpClient.Send(message);
                }
            }
            catch (Exception ex)
            {
                string pathFile = context.Server.MapPath("~/ErrorLog.txt");
                var fileInfo = new FileInfo(pathFile);
                var writer = fileInfo.CreateText();
                writer.WriteLine("Error Time at: " + DateTime.Now.ToString("dd/MM/yyyy hh:mm"));
                writer.WriteLine(ex.Message);
                writer.Write(ex.StackTrace);
                writer.Close();
                if (ipnWriter != null) ipnWriter.Close();
            }
            finally
            {
                context.Response.Flush();
                context.Response.End();
            }
            return isSuccess;
        }
        public ViewResult UserBoughtProducts()
        {
            ViewBag.LoggedIn = ControllerContext.HttpContext.User.Identity.IsAuthenticated;
            GetPartialViewData();
            return View();
        }
        public ActionResult Cancel()
        {
            GetPartialViewData();
            return View();
        }

        public ActionResult Verify()
        {
            var isSuccess = VerifyTransaction();
            if (isSuccess)
            {
                Session.Clear();
                return View("Success");
            }
            Session.Clear();
            return View("Cancel");
        }
        public ActionResult Success()
        {
            GetPartialViewData();
            return View("Success");
        }

        public ActionResult DownloadPage(string productId)
        {
            GetPartialViewData();
            //frame.Title = "Download Page";
            string decProds = Encoding.GetEncoding(1252).GetString(HttpServerUtility.UrlTokenDecode(Request.QueryString["product"]));


            string[] products = decProds.Split(new char[] { '_' }, StringSplitOptions.RemoveEmptyEntries);
            //Create Links for download:
            return View(products);
        }

        public ActionResult ProductPage(string productId)
        {
            GetPartialViewData();
            return View((object)productId);
        }


    }


}

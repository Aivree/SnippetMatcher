using System;
using System.Collections.Generic;
using System.Linq;
using System.Net.Mail;
using System.Web.WebPages;
using AutoMobile.Models;
using Microsoft.Ajax.Utilities;
using Model;
using Business_Logic_Layer;
using System.Web.Mvc;

namespace AutoMobile.Controllers
{
    public class AdvertiseController : Controller
    {
        private readonly AdvertiseLogic _advertiselogic;
        private readonly UserLogic _userLogic;
        private readonly BrandLogic _brandLogic;
        private static readonly int AdvertsPerPage;
        private static readonly int RenewMonthsValidAdvert;

        static AdvertiseController()
        {
            AdvertsPerPage = Int32.Parse(@System.Web.Configuration.WebConfigurationManager.AppSettings["AdvertsPerPage"]);

            int NDaysValidAfterExpired =
                Int32.Parse(@System.Web.Configuration.WebConfigurationManager.AppSettings["NDaysValidAfterExpired"]);
            AdvertiseLogic.SetDaysUntilTerminate(NDaysValidAfterExpired);

            RenewMonthsValidAdvert = Int32.Parse(@System.Web.Configuration.WebConfigurationManager.AppSettings["MonthsValidAdvert"]);
        }

        public AdvertiseController(AdvertiseLogic s, UserLogic u, BrandLogic b)
        {
            _advertiselogic = s;
            _userLogic = u;
            _brandLogic = b;
        }

        // GET: /Advertise/
        public ActionResult Index(int? page)
        {
            if (!page.HasValue) page = 0;
            IEnumerable<IGrouping<String, Advert>> groupEnumerable =
                _advertiselogic.GetAllOpenByBrandPaged(page.Value, AdvertsPerPage);
            return View("List", groupEnumerable);
        }

        //GET /Advertise/Detailed/{id}
        public ActionResult Detailed(int? id)
        {
            if (!id.HasValue) return RedirectToActionPermanent("Index");

            Advert advert = _advertiselogic.GetAdvert(id.Value);
            if (advert == null) return View("List");
            User u = _userLogic.GetUserById(advert.AdvertiseOwner);
            ViewBag.user = u;

            ViewData["email"] = u.Email;

            return View(advert);

        }

        //POST /Advertise/Comment/
        //[HttpPost]

        [AcceptVerbs("POST")]
        [Authorize]
        public ActionResult Comment(Comment comment, string url)
        {
            if (!ModelState.IsValid) return RedirectToAction("Detailed", new {id = comment.AdId});

            if (!_advertiselogic.AddCommentary(comment))
            {
                ModelState.AddModelError("", "Não foi possível enviar o email.");
                return RedirectToAction("Detailed/" + comment.AdId);
            }
               
            Advert advert = _advertiselogic.GetAdvert(comment.AdId);
            User owner = _userLogic.GetUserById(advert.AdvertiseOwner);

            MailMessage mail = new MailMessage();
            mail.To.Add(owner.Email);

            User activeUser = _userLogic.GetUserById(User.Identity.Name);
            mail.From = new MailAddress(activeUser.Email);
            mail.Subject = "Comentário sobre o seu carro: " + advert.Brand + " " + advert.Model;

            string Body = comment.Username + " deu " + comment.Rating +" estrelas ao seu carro!<br>";
            if (@Request.Url != null)
                Body += url + "<br><br>";
            Body += comment.Message;               
            mail.Body = Body;
 
            if (!_advertiselogic.sendMail(mail))
                ModelState.AddModelError("", "Não foi possível enviar o email.");

            return RedirectToAction("Detailed/" + comment.AdId);
        }

        //[HttpPost]
        [AcceptVerbs("POST")]
        public ActionResult VendorContact(MailModel mailModel)
        {
            if (!ModelState.IsValid) return View("Detailed");

            Advert advert = _advertiselogic.GetAdvert(mailModel.AdId);
            User owner = _userLogic.GetUserById(advert.AdvertiseOwner);
            MailMessage mail = new MailMessage();
            mail.To.Add(owner.Email);
            mail.From = new MailAddress(mailModel.From);
            mail.Subject = "Um utilizador do AutoMobile enviou-lhe uma mensagem";
            mail.Body = mailModel.Message + "<br>" +
                        mailModel.From + "<br>" +
                        mailModel.Name + ", " + mailModel.Phone;

            if(!_advertiselogic.sendMail(mail))
                ModelState.AddModelError("", "Não foi possível enviar o email.");

            return RedirectToAction("Detailed/" + mailModel.AdId);
        }

        //GET /Advertise/Add/{id}
        [Authorize]
        public ActionResult Add()
        {
            ViewData["Brand"] = new SelectList(_brandLogic.GetAllBrands(true));
            //ViewData["BrandJson"] = _brandLogic.GetAllBrandsJsonToddSlick();
            return View();
        }
        //POST /Advertise/Add/{id}
        [Authorize]
        [HttpPost]
        public ActionResult Add(Advert a)
        {
            ViewData["Brand"] = new SelectList(_brandLogic.GetAllBrands(true));
            if (!ModelState.IsValid) return View();
            _advertiselogic.AddAdvert(a); //TODO fazer verificações
            _userLogic.AssociateAdvertToUser(a.Id, User.Identity.Name);
            return RedirectToAction("Detailed", new { id = a.Id });
        }
        //GET /Advertise/Edit/{id}
        [Authorize]
        public ActionResult Edit(int? id)
        {
            if (!id.HasValue) return RedirectToAction("ManageAd", "Account");
            Advert advert = _advertiselogic.GetAdvert(id.Value);
            User u = _userLogic.GetUserById(advert.AdvertiseOwner);
            ViewBag.advert = advert;
            return View(u);
        }
        //POST /Advertise/Edit/{id}
        [Authorize]
        [HttpPost]
        public ActionResult Edit(int? id, float Price, bool? Negotiable, String Description, String Password)
        {
            User activeUser = _userLogic.GetUserById(User.Identity.Name);
            if (Password.IsNullOrWhiteSpace() || !activeUser.Password.Equals(Password))
            {
                ModelState.AddModelError("badPassword", "Password errada!");
                return RedirectToAction("Edit");
            }
            Advert advert;
            if (id.HasValue && !(advert = _advertiselogic.GetAdvert(id.Value)).Equals(null))
            {
                if (!advert.Price.Equals(Price) && Price > 0)
                    advert.Price = Price;
                if (Negotiable.HasValue && advert.Negotiable != Negotiable.Value)
                    advert.Negotiable = Negotiable.Value;
                if (!Description.IsNullOrWhiteSpace() && !advert.Description.Equals(Description))
                    advert.Description = Description;

                if (_advertiselogic.UpdateAdvert(advert))
                    return RedirectToAction("ManageAd", "Account");
            }
            ModelState.AddModelError("AdvertUpdateError", "Erro! Não foi possível editar este anúncio!");
            return RedirectToAction("Edit");

        }
        //POST /Advertise/Cancel/{id}
        [Authorize]
        public ActionResult Cancel(int? id)
        {
            if (!id.HasValue) return RedirectToAction("ManageAd", "Account");
            Advert advert = _advertiselogic.GetAdvert(id.Value);

            advert.Status = AdvertStatusEnum.Cancelado;
            _advertiselogic.UpdateAdvert(advert);

            return RedirectToAction("ManageAd", "Account");
        }

        //POST /Advertise/Activate/{id}
        [Authorize]
        public ActionResult Activate(int? id)
        {
            if (!id.HasValue) return RedirectToAction("ManageAd", "Account");
            Advert advert = _advertiselogic.GetAdvert(id.Value);
            if (advert.Status == AdvertStatusEnum.Expirado || advert.Status == AdvertStatusEnum.Terminado)
                advert.ValidUntil = DateTime.Now.AddMonths(RenewMonthsValidAdvert);

            advert.Status = AdvertStatusEnum.Ativo;
            _advertiselogic.UpdateAdvert(advert);

            return RedirectToAction("ManageAd", "Account");
        }
        //POST /Advertise/Delete/{id}
        [Authorize]
        [HttpPost]
        public ActionResult Delete(int? id)
        {
            if (ModelState.IsValid && id.HasValue)
            {
                Advert advert = _advertiselogic.GetAdvert(id.Value);
                if (_advertiselogic.DeleteAdvert(advert))
                    return RedirectToAction("ManageAd", "Account");
            }
            ModelState.AddModelError("Advert", "That advertise couldn't be removed");
            return RedirectToAction("ManageAd", "Account");
        }

        [HttpGet]
        public ActionResult GetModelsByBrand(string brand)
        {
            if (Request.IsAjaxRequest() && !brand.IsEmpty())
            {
                CarBrand carBrand = _brandLogic.getModelsFromBrand(brand);
                if (carBrand != null)
                    return Json(carBrand.models.ToList(), JsonRequestBehavior.AllowGet);
            }

            return new JsonResult
            {
                Data = "Not Valid Request",
                JsonRequestBehavior = JsonRequestBehavior.AllowGet
            };
 
        }


        //GET /Advertise/Search
        [HttpGet]
        public ActionResult Search(String brand)
        {
            ViewData["MaxPrice"] = _advertiselogic.GetMaxPrice();
            ViewData["MinPrice"] = _advertiselogic.GetMinPrice();
            ViewData["MaxKm"] = _advertiselogic.GetMaxKm();
            ViewData["MinKm"] = _advertiselogic.GetMinKm();
            ViewData["MaxYear"] = _advertiselogic.GetMaxYear();
            ViewData["MinYear"] = _advertiselogic.GetMinYear();

            ViewData["Brand"] = new SelectList(_brandLogic.GetAllBrands(true));

            return View();
        }

        //GET /Advertise/Search/filters
        [HttpGet]
        public ActionResult Searched(FilterAdvertise fa)
        {
            var advertise = _advertiselogic.GetAllOpen().FilterAdvert(fa).GroupBy(advert => advert.Brand);
            return View("List", advertise);
        }
    }
}

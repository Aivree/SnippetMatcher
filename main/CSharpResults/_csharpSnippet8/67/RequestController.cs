using System;
using System.Collections.Generic;
using System.Linq;
using System.Data.Entity;
using System.Data;
using System.Web;
using System.Web.Mvc;
using HelpDeskTrain.Models;
using HelpDeskTrain.Filtres;

namespace HelpDeskTrain.Controllers
{

    [Authorize]
    [Culture]
    public class RequestController : Controller
    {
        HelpdeskContext db = new HelpdeskContext();
        public List<Photo> ph = new List<Photo>();
        
        public ActionResult CreateAdvert(Advert adverts)
        {
            Client client = db.Clients.Where(m => m.Login == HttpContext.User.Identity.Name).FirstOrDefault();
            if (client != null)
            {
                int Status;
                if (db.Adverts.Where(m => m.ClientId == client.Id).Count() > 0)
                {
                    Status = db.Adverts.Where(m => m.ClientId == client.Id).OrderByDescending(r => r.Opened).First().Status;
                }
                else
                {
                    Status = 1;
                }
                Advert advert = new Advert();
                if (Status == 1)
                {
                    advert.Name = "name";
                    advert.Description = "des";
                    advert.Price = 0;
                    advert.Opened = DateTime.Now;
                    advert.Case = 1;
                    advert.Gender = 1;
                    advert.Strap = 1;
                    advert.ClientId = client.Id;
                    advert.Path = "/Files/DefaultPhoto.jpg";
                    advert.Status = 0;
                    db.Adverts.Add(advert);
                    db.SaveChanges();
                }
                var directions = from Gender d in Enum.GetValues(typeof(Gender)) select new { ID = (int)d, Name = d.ToString() };
                SelectList Gender = new SelectList(directions, "ID", "Name");
                ViewBag.Gender = Gender;

                directions = from Strap d in Enum.GetValues(typeof(Strap)) select new { ID = (int)d, Name = d.ToString() };
                SelectList Strap = new SelectList(directions, "ID", "Name");
                ViewBag.Strap = Strap;

                directions = from Case d in Enum.GetValues(typeof(Case)) select new { ID = (int)d, Name = d.ToString() };
                SelectList Case = new SelectList(directions, "ID", "Name");
                ViewBag.Case = Case;
                int AdvertId = db.Adverts.Where(m => m.ClientId == client.Id).OrderByDescending(r => r.Opened).First().Id;
                ViewBag.Photos = db.Photos.Where(r => r.AdvertId == AdvertId).ToList();
                advert.Name = adverts.Name;
                advert.Description = adverts.Description;

                return View(advert);
            }
            return RedirectToAction("LogOff", "Account");
        }

        public ActionResult AddPhoto()
        {
            Client user = db.Clients.Where(m => m.Login == HttpContext.User.Identity.Name).FirstOrDefault();
            if (user == null)
            {
                return RedirectToAction("LogOff", "Account");
            }
            if (ModelState.IsValid)
            {
                int AdvertId = db.Adverts.Where(m => m.ClientId == user.Id).OrderByDescending(r => r.Opened).First().Id;
                List<Photo> photos = new List<Photo>();
                foreach (string fileName in Request.Files)
                {
                    HttpPostedFileBase file = Request.Files[fileName];
                    Photo photo = new Photo();
                    photo.AdvertId = AdvertId;
                    string ext = file.FileName.Substring(file.FileName.LastIndexOf('.'));
                    string name = file.FileName.Substring(0, file.FileName.LastIndexOf('.'));
                    string path = user.Id + "_" + AdvertId + DateTime.Now.ToString("dd.mm.yyyy hh:mm:ss").Replace(":", "_").Replace("/", ".") + name + ext;
                    file.SaveAs(Server.MapPath("/Files/" + path));
                    photo.Path = "/Files/" + path;
                    photo.Name = "Photo";
                    photo.AdvertId = AdvertId;
                    db.Photos.Add(photo);
                    db.SaveChanges();
                    photos.Add(photo);
                }
                return PartialView(photos);
            }
            return RedirectToAction("CreateAdvert", "Request");
        }
        
    }
}

using System;
using System.Collections.Generic;
using System.Data.Entity;
using System.Data;
using System.Linq;
using System.Web;
using System.Web.Mvc;
using HelpDeskTrain.Models;
using HelpDeskTrain.Filtres;

namespace HelpDeskTrain.Controllers
{
    [Culture]
    [Authorize(Roles = "Admin, User")]
    public class UserController : Controller
    {
        private HelpdeskContext db = new HelpdeskContext();

        public ActionResult Show()
        {
            List<Client> clients = db.Clients.ToList();
            return View(clients);
        }

        [HttpGet]
        [Authorize(Roles = "Admin")]
        public ActionResult Create()
        {
            var directions = from Block d in Enum.GetValues(typeof(Block))  select new { ID = (int)d, Name = d.ToString() };
            SelectList Block = new SelectList(directions, "ID", "Name");
            ViewBag.Block = Block;
            SelectList roles = new SelectList(db.Roles, "Id", "Name");
            ViewBag.Roles = roles;
            Client client = new Client();
            return View(client);
        }
        
        [HttpPost]
        [Authorize(Roles = "Admin")]
        public ActionResult Create(Client user)
        {
            List<Client> clients = db.Clients.ToList();
            foreach (Client item in clients)
            {
                if (item.Login == user.Login)
                {
                    ViewBag.Error = Resources.Resource.IsUserLogin+" (" + user.Login + ") "+Resources.Resource.IsExist;
                    user.Login = "";
                    return View(user);
                }
            }
            user.Opened = DateTime.Now;
            if (ModelState.IsValid)
            {
                db.Clients.Add(user);
                db.SaveChanges();
                return RedirectToAction("Show");
            }
            return View(user);
        }
       
       [HttpGet]
       [Authorize(Roles = "Admin")]
       public ActionResult Edit(int id)
        {
            var directions = from Block d in Enum.GetValues(typeof(Block)) select new { ID = (int)d, Name = d.ToString() };
            SelectList Block = new SelectList(directions, "ID", "Name");
            ViewBag.Block = Block;
            SelectList roles = new SelectList(db.Roles, "Id", "Name");
            ViewBag.Roles = roles;
           Client client = db.Clients.Find(id);
           return View(client);
       }
       
       [HttpPost]
       [Authorize(Roles = "Admin")]
       public ActionResult Edit(Client client)
       {
           if (ModelState.IsValid)
           {
               db.Entry(client).State = EntityState.Modified;
               db.SaveChanges();
               return RedirectToAction("Show");
           }
           return View(client);
       }

        [Authorize(Roles = "Admin")]
        public ActionResult Delete(int id)
        {
           Client client = db.Clients.Find(id);
            db.Clients.Remove(client);
            db.SaveChanges();
            return RedirectToAction("Show","User");
        }
    }
}

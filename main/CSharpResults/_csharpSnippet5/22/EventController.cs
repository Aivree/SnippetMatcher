using System;
using System.Collections.Generic;
using System.Data;
using System.Data.Entity;
using System.IO;
using System.Linq;
using System.Web;
using System.Web.Mvc;
using System.Web.UI;
using System.Web.UI.WebControls;
using TGIF.Models;
using WebMatrix.WebData;

namespace TGIF.Controllers
{
    public class EventController : Controller
    {
        private TGIFDBContext db = new TGIFDBContext();

        //
        // GET: /Event/

        //public ActionResult Index()
        //{
        //    return View(db.Events.ToList());
        //}

        //
        // GET: /Event/

        public ActionResult Index(string searchTerm = null)
        {
            var model = from r in db.Events
                        where (searchTerm == null || r.Title.StartsWith(searchTerm))
                        orderby r.Title ascending
                        select r;

            return View(model);
        }

        //
        // GET: /Event/Details/5

        public ActionResult Details(int id = 0)
        {
            Event eventmodel = db.Events.Find(id);
            if (eventmodel == null)
            {
                return HttpNotFound();
            }
            return View(eventmodel);
        }

        [Authorize(Roles = "Administrator")]
        public ActionResult ExportEventList()
        {
            GridView gv = new GridView();
            gv.DataSource = db.Events.ToList();
            gv.DataBind();
            Response.ClearContent();
            Response.Buffer = true;
            Response.AddHeader("content-disposition", "attachment; filename=TGIFEventlist.xls");
            Response.ContentType = "application/ms-excel";
            Response.Charset = "";
            StringWriter sw = new StringWriter();
            HtmlTextWriter htw = new HtmlTextWriter(sw);
            gv.RenderControl(htw);
            Response.Output.Write(sw.ToString());
            Response.Flush();
            Response.End();

            return RedirectToAction("Index");
        }

        //
        // GET: /Event/Create

        public ActionResult Create()
        {
            return View();
        }

        //
        // POST: /Event/Create

        [HttpPost]
        [ValidateAntiForgeryToken]
        [Authorize(Roles = "Administrator")]
        public ActionResult Create(Event eventmodel)
        {
            try
            {
                if (ModelState.IsValid)
                {
                    eventmodel.CreatedBy = User.Identity.Name;
                    db.Events.Add(eventmodel);
                    db.SaveChanges();
                    return RedirectToAction("Index");
                }
            }
            catch (DataException)
            {
                //Log the error (add a variable name after DataException)
                ModelState.AddModelError("", "Unable to Create Event. Try again, and if the problem persists see your system administrator.");
            }

            return View(eventmodel);
        }

        //
        // GET: /Event/Edit/5

        public ActionResult Edit(int id = 0)
        {
            Event eventmodel = db.Events.Find(id);
            if (eventmodel == null)
            {
                return HttpNotFound();
            }

            return View(eventmodel);
        }

        //
        // POST: /Event/Edit/5
        [HttpPost]
        [ValidateAntiForgeryToken]
        [Authorize(Roles = "Administrator")]
        public ActionResult Edit(Event eventmodel)
        {
            try
            {
                if (ModelState.IsValid)
                {
                    db.Entry(eventmodel).State = System.Data.Entity.EntityState.Modified;
                    db.SaveChanges();
                    return RedirectToAction("Index");
                }
            }
            catch (DataException)
            {
                //Log the error (add a variable name after DataException)
                ModelState.AddModelError("", "Unable to Edit Event. Try again, and if the problem persists see your system administrator.");
            }
            return View(eventmodel);
        }

        //
        // GET: /Event/Delete/5
        [Authorize(Roles = "Administrator")]
        public ActionResult Delete(int id = 0)
        {
            Event eventmodel = db.Events.Find(id);
            if (eventmodel == null)
            {
                return HttpNotFound();
            }
            return View(eventmodel);
        }

        //
        // POST: /Event/Delete/5
        [Authorize(Roles = "Administrator")]
        [HttpPost, ActionName("Delete")]
        [ValidateAntiForgeryToken]
        public ActionResult DeleteConfirmed(int id)
        {
            Event eventmodel = db.Events.Find(id);
            db.Events.Remove(eventmodel);
            db.SaveChanges();
            return RedirectToAction("Index");
        }

        //
        // GET: /Event/Register
        [Authorize]
        [HttpGet]
        public ActionResult Register(int id = 0)
        {
            UserEvent userEvent = new UserEvent();
            userEvent.EventID = id;
            userEvent.UserId = WebSecurity.GetUserId(User.Identity.Name);
            userEvent.RegistrationDate = DateTime.Now;
            return View(userEvent);
        }

        //
        // POST: /Event/Register
        [Authorize]
        [HttpPost]
        [ValidateAntiForgeryToken]
        public ActionResult Register(UserEvent userEvent)
        {
            try
            {
                if (ModelState.IsValid)
                {
                    db.UserEvents.Add(userEvent);
                    db.SaveChanges();
                    Event eventmodel = db.Events.Find(userEvent.EventID);
                    Email.SendMail("Thank you for registering event: " + eventmodel.Title, "Registered for TGIF Event");
                    return RedirectToAction("Index");
                }
            }
            catch (DataException)
            {
                //Log the error (add a variable name after DataException)
                ModelState.AddModelError("", "Unable to Register Event. Try again, and if the problem persists see your system administrator.");
            }

            return View(userEvent);
        }

        //
        // GET: /RegisteredUser/
        [Authorize(Roles = "Administrator")]
        [HttpGet]
        public ActionResult RegisteredUser(int id = 0)
        {
            var model = from r in db.UserEvents
                        join s in db.Events
                        on r.EventID equals s.EventId
                        where (r.EventID == id)
                        orderby r.UserId
                        select new RegisteredUser
                        {
                            StudentName = r.User.UserName,
                            EventName = s.Title,
                            SchoolName = r.User.SchoolName,
                            Email = r.User.UserName,
                            Phone = r.User.HomePhone,
                            RegistrationDate = r.RegistrationDate,
                            Attendees = r.Attendees
                        };
            return View(model);
        }

        //
        // GET: /Event/CancelRegistration/5
        [Authorize]
        [HttpGet]
        public ActionResult CancelRegistration(int id = 0)
        {
            Event eventmodel = db.Events.Find(id);
            if (eventmodel == null)
            {
                return HttpNotFound();
            }
            return View(eventmodel);
        }

        //
        // POST: /Event/CancelRegistrationConfirmed/5
        [Authorize]
        [HttpPost, ActionName("CancelRegistration")]
        [ValidateAntiForgeryToken]
        public ActionResult CancelRegistrationConfirmed(int id = 0)
        {
            int userId = WebSecurity.GetUserId(User.Identity.Name);
            UserEvent userEvent = db.UserEvents.Where(r => r.EventID == id && r.UserId == userId).First();
            db.UserEvents.Remove(userEvent);
            db.SaveChanges();
            Event eventmodel = db.Events.Find(userEvent.EventID);
            Email.SendMail("Thank you for cancelling event: " + eventmodel.Title, "Cancellation for TGIF Event");
            return RedirectToAction("Index");
        }

       

        protected override void Dispose(bool disposing)
        {
            if (db != null)
            {
                db.Dispose();
            }
            base.Dispose(disposing);
        }
    }
}
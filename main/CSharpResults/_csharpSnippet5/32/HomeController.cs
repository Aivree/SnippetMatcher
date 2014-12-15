using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;
using System.Web.Mvc;
using System.Net.Mail;
using RB2012.Models;
using RB2012.DAL;
using System.Text;
using System.ComponentModel;
using System.Net;
namespace RB2012.Controllers
{
    public class HomeController : Controller
    {
        private DataContext db = new DataContext();
        public ActionResult Index()
        {
            return View();
        }

        public ActionResult About()
        {
            return View();
        }
        private void PopulateDropDownList(object selectedItem = null)
        {
            var query = from t in db.Tutorial
                        orderby t.TutorialName
                        select new { t.TutorialID, t.TutorialName };
            ViewBag.Tutorials = new SelectList(query.AsEnumerable(), "TutorialID", "TutorialName", selectedItem);
        }
        private string GetTutorialNameFromSelectedValue(int tutorialId)
        {
            var tutorialName = from d in db.Tutorial
                               where d.TutorialID == tutorialId
                               select d.TutorialName;
            var selectedItem = tutorialName.First();
            return selectedItem;
        }
        public ActionResult Contact()
        {
            PopulateDropDownList();
            var model = new Contact();
            //set bool tutorial to false
            //forces div not to show
            model.Tutorial = false;
            return View(model);
        }

        public ActionResult Tutorial()
        {
            return View();
        }

        [HttpPost]
        public ActionResult Contact(Contact c)
        {
            if (c.Tutorial == false)
            {
                //cancel out error
                ModelState.Remove("TutorialID");
            }
            if (ModelState.IsValid)
            {
                StringBuilder sb = new StringBuilder();
                MailMessage msg = new MailMessage();
                SmtpClient smtp = new SmtpClient();
                MailAddress address = new MailAddress("ryan@ryanbutler.org");
                MailAddress from = new MailAddress(c.Email.ToString());
                msg.Subject = "Contact Me";
                msg.To.Add(address);
                msg.From = from;
                smtp.Host = "mail.ryanbutler.org";
                smtp.Port = 25;
                msg.IsBodyHtml = false;
                sb.Append(Environment.NewLine);
                sb.Append("First name: " + c.FirstName);
                sb.Append(Environment.NewLine);
                sb.Append("Last name: " + c.LastName);
                sb.Append(Environment.NewLine);
                sb.Append("Email: " + c.Email);
                if (c.Tutorial == true)
                {
                    sb.Append(Environment.NewLine);
                    TutorialQuestionYes(sb, c);
                }
                else
                {
                    sb.Append(Environment.NewLine);
                    TutorialQuestionNo(sb);
                }
                sb.Append("Comments: " + c.Comment);
                msg.Body = sb.ToString();
                try
                {
                    smtp.Send(msg);
                    smtp.Dispose();
                    msg.Dispose();
                    return View("Message");
                }
                catch (Exception)
                {
                    return View("Error");
                }
            }
            var model = new Contact();
            //send the bool tutorial radio button from the model to the view
            //in order to keep the div open
            model.Tutorial = c.Tutorial;
            PopulateDropDownList(c.TutorialID);
            return View(model);
        }
        private string TutorialQuestionYes(StringBuilder sb, Contact c)
        {
            sb.Append("Tutorial question? " + "Yes");
            string selectedItem = GetTutorialNameFromSelectedValue(c.TutorialID);
            sb.Append(Environment.NewLine);
            sb.Append("Which tutorial? " + selectedItem.ToString());
            sb.Append(Environment.NewLine);
            return sb.ToString();
        }
        private string TutorialQuestionNo(StringBuilder sb)
        {
            sb.Append("Tutorial question? " + "No");
            sb.Append(Environment.NewLine);
            return sb.ToString();
        }
        //remove db connection from memory
        protected override void Dispose(bool disposing)
        {
            db.Dispose();
            base.Dispose(disposing);
        }
    }
}

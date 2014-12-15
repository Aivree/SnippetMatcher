using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;
using System.Web.Mvc;

using MvcApplication1.Models;

namespace MvcApplication1.Controllers
{
    public class UserController : Controller
    {
        //
        // GET: /User/

        public ActionResult Index()
        {
            return View();
        }

        public ActionResult IncidentList()
        {
            return View();
        }
        public ActionResult IncidentDetail()
        {
            Incident model = new Incident();
            DateTime dt = new DateTime(2010, 12, 15);
            model.IncidentDate = dt;
            model.Time = "10:00";
            model.ReportName = "Employee";
            model.PersonName = "Andrews Lee";
            model.Location = "Operating Room";
            model.ReportDesc = "Description goes here..................";
            model.AddlInfo = "Additional info goes here..................";
            model.Person1 = "Person 1 Name";
            model.Person2 = "Person 2 Name";
            model.Person3 = "Person 3 Name";
            model.Person4 = "Person 4 Name";
            model.Person5 = "Person 5 Name";
            model.Person6 = "Person 6 Name";
            model.Category = "Select";
            model.slc_target = "Unexpected Complication";
            model.SubType = "Hemorrhage/Excessive Bleeding";
            model.chkTransfer = true;
            model.chkEmergency = true;
            model.commDate = dt;
            
            return View(model);
            //return View();
        }
        public ActionResult ReportList()
        {
            return View();
        }
        public ActionResult NewIncident()
        {
            Incident model = new Incident();
            DateTime dt = DateTime.Now;
            model.ReportDate = dt;
            model.IncidentDate = dt;
            model.commDate = dt;
         
            return View(model);
        }
        public ActionResult NewFacility()
        {
            return View();
        }
        public ActionResult ViewFacility()
        {
            return View();
        }

        public ActionResult EditIncident()
        {
            return View();
        }

        public ActionResult Admin()
        {
            return View();
        }
        public ActionResult FacilityAdmin()
        {
            return View();
        }
        public ActionResult ListFacility()
        {
            return View();
        }

        public ActionResult UserAdmin()
        {
            return View();
        }
        public ActionResult History()
        {
            return View();
        }

        public ActionResult IncidentDetailRep()
        {
            return View();
        }
        [HttpPost]
        public ActionResult RunReport()
        {
            return View();
        }
        [HttpPost]
        public ActionResult RunSummaryReport()
        {
            return View();
        }
        
        [HttpPost]
        public ActionResult OnSave()
        {
            return (Save());
        }
        [HttpPost]
        public ActionResult OnCancel()
        {
            return (Save());
        }
        public ActionResult OnSubmit()
        {
            return (Submit());
        }
     

        private ActionResult Cancel()
        {
            // process the cancellation request here.
            return(View("Cancelled"));
        }
        private ActionResult Secondpage()
        {
            // process the cancellation request here.
            return View();
        }

        private ActionResult Save()
        {
            // perform the actual send operation here.
            return(View("SaveConfirmed"));
        }
        private ActionResult Submit()
        {
            // perform the actual send operation here.
            return (View("SaveConfirmed"));
        }
        public ActionResult searchincident()
        {
            return View();
        }
        public ActionResult ViewIncident()
        {
            return View();
        }
        public ActionResult CloseIncident()
        {
            Incident model = new Incident();
            
            DateTime dt = new DateTime(2010, 12, 15);
            model.IncidentDate = dt;
            model.ReportDate = dt;
            model.IncidentNumber = "107888";
            model.Time = "10:00";
            model.ReportName = "Employee";
            model.PersonName = "Andrews Lee";
            model.Location = "Operating Room";
            model.ReportDesc = "Description goes here..................";
            model.AddlInfo = "Additional info goes here..................";
            model.Person1 = "Person 1 Name";
            model.Person2 = "Person 2 Name";
            model.Person3 = "Person 3 Name";
            model.Person4 = "Person 4 Name";
            model.Person5 = "Person 5 Name";
            model.Person6 = "Person 6 Name";
            model.Category = "Patient Incident";
            model.slc_target = "Unexpected Complication";
            model.SubType = "Hemorrhage/Excessive Bleeding";
            model.chkTransfer = true;
            model.chkEmergency = true;
            model.bTrends = false;
            model.bChart = true;
            model.bCare = true;

            DateTime dt1 = DateTime.Now;
            model.ClosedDate = dt1;

            return View(model);
        }

        public ActionResult IncidentSummaryRep()
        {
            return View();
        }
        public ActionResult PeerReviewRep()
        {
            return View();
        }
        public ActionResult OutcomeRep()
        {
            return View();
        }
        public ActionResult SearchResult()
        {
            return View();
        }
        [HttpPost]
        public ActionResult Post(string btnName)
        {
            switch (btnName)
            {
                case "Save":
                    return View("SaveConfirmed");
                case "Next":
                    return View("Secondpage");
                case "Cancel":
                    return View("IncidentList");
                case "Submit":
                    return View("Submitted");
                case "Close":
                    return View("CloseConfirmed");

                default:
                    return View();

            }
        }
                

        
    }
}

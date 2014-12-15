using System;
using System.Collections.Generic;
using System.Data.Entity;
using System.Data.Entity.Infrastructure;
using System.Linq;
using System.Web.Mvc;
using System.Web.UI;
using Perscribo.EF.Library.DAL;
using Perscribo.EF.Library.Models;

namespace PerscriboMVC.Controllers
{
    public class ApplicationsController : Controller
    {
        private PerscriboContext db = new PerscriboContext();

        [HttpGet, OutputCache(NoStore = true, Location = OutputCacheLocation.None)]
        public ActionResult Index(string verb)
        {
            if ((verb ?? "").ToLower() == "new")
            {
                LoadSelectLists();

                Role newRole = new Role();

                return View("Create", newRole);
            }
            else
            {
                return View(db.Roles.Include("Agency").ToList());
            }
        }

        [HttpPost, OutputCache(NoStore = true, Location = OutputCacheLocation.None)]
        [ActionName("Index")]
        public ActionResult Create([Bind(Include="PositionTitle,AppliedForOn,ReferenceNumber,PositionType,LowSalaryRange,HighSalaryRange,SalaryType,Status,AgentInterview,AgencyID,Consultant,Company")] Role newRole)
        {
            newRole.Agency = db.Agencies.Where(a => a.ID == newRole.AgencyID.Value).Single();

            db.Roles.Add(newRole);
            try
            {
                db.SaveChanges();
            }
            catch (Exception ex)
            {
                string temp = ex.Message;
            }

            return RedirectToAction("Index");
        }

        private void LoadSelectLists(Role role = null)
        {
            var positionTypes = from PositionType p in Enum.GetValues(typeof(PositionType))
                                select new { ID = p, Name = p.ToString().Replace("_", " ") };
            var salaryTypes = from SalaryType s in Enum.GetValues(typeof(SalaryType))
                              select new { ID = s, Name = s.ToString().Replace("_", " ") };
            var statusTypes = from RoleStatus r in Enum.GetValues(typeof(RoleStatus))
                              select new { ID = r, Name = r.ToString().Replace("_", " ") };
            //var agencies = from a in db.Agencies
            //               orderby a.Name
            //               select new { AgencyID = a.ID, Name = a.Name };

            if (role != null)
            {
                //  NOTE: The key (for ViewData) must match the Property name
                ViewData["PositionType"] = new SelectList(positionTypes, "ID", "Name", role.PositionType);
                ViewData["SalaryType"] = new SelectList(salaryTypes, "ID", "Name", role.SalaryType);
                ViewData["Status"] = new SelectList(statusTypes, "ID", "Name", role.Status);
                //ViewData["AgencyID"] = new SelectList(agencies, "AgencyID", "Name", role.Agency);
            }
            else
            {
                ViewData["PositionType"] = new SelectList(positionTypes, "ID", "Name");
                ViewData["SalaryType"] = new SelectList(salaryTypes, "ID", "Name");
                ViewData["Status"] = new SelectList(statusTypes, "ID", "Name");
                //ViewData["AgencyID"] = new SelectList(agencies, "AgencyID", "Name");
            }
        }

        public ActionResult Agency()
        {
            var agencies = db.Agencies.Include(a => a.Consultants).ToList();

            return PartialView(agencies);
        }

    }
}

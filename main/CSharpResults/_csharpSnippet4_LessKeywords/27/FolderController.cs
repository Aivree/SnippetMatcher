using System;
using System.Collections.Generic;
using System.Data;
using System.Data.Entity;
using System.Linq;
using System.Net;
using System.Web;
using System.Web.Mvc;
using Allomorph.Models;
using Allomorph.Repositories;
using PagedList;
using System.Reflection;
using System.IO;

namespace Allomorph.Controllers
{
    public class FolderController : Controller
    {
        // Database access layer
        private SubtitleRepository repo = new SubtitleRepository();

        // ~/Folder/Index == Yfirlit texta
        public ViewResult Index(SearchViewModel svm)
        {
            string sortOrder = svm.sortOrder;
            string currentFilter = svm.currentFilter;
            string searchString = svm.searchString;
            int? page = svm.page;
            ViewBag.CatID = svm.ID;

            if (page >= 2 || (page == 1 && svm.CategoryID != 0))
            {
                ViewBag.CatID = svm.CategoryID;
            }
            int category = ViewBag.CatID;           

            // Til að raða eftir nafni, dagsetningu eða flokki
            ViewBag.CurrentSort = sortOrder;
            ViewBag.NameSortParm = String.IsNullOrEmpty(sortOrder) ? "name_desc" : "";
            ViewBag.DateSortParm = sortOrder == "date_asc" ? "date_desc" : "date_asc";
            ViewBag.CatSortParm = sortOrder == "cat_asc" ? "cat_desc" : "cat_asc";

            // Alltaf sýna síðu 1 ef það er notað leitina
            if (searchString != null)
            {
                page = 1;
            }
            else
            {
                searchString = currentFilter;
            }

            ViewBag.CurrentFilter = searchString;

            // Nær í allar möppur
            var folders = repo.GetAllFolders();

            // Leitað eftir nafni á möppu
            if (!String.IsNullOrEmpty(searchString))
            {
                folders = folders.Where(s => s.FolderName.ToUpper().Contains(searchString.ToUpper()));
            }

            // Ef það er leitað eftir flokki
            switch (category)
            {
                case 1:
                    folders = folders.Where(f => f.CategoryID == 1);
                    break;
                case 2:
                    folders = folders.Where(f => f.CategoryID == 2);
                    break;
                case 3:
                    folders = folders.Where(f => f.CategoryID == 3);
                    break;
                case 4:
                    folders = folders.Where(f => f.CategoryID == 4);
                    break;
            }

            // Raðar eftir nafni, dagsetningu eða flokki
            switch (sortOrder)
            {
                case "name_desc":
                    folders = folders.OrderByDescending(s => s.FolderName);
                    break;
                case "date_desc":
                    folders = folders.OrderByDescending(s => s.DateCreated);
                    break;
                case "date_asc":
                    folders = folders.OrderBy(s => s.DateCreated);
                    break;
                case "cat_desc":
                    folders = folders.OrderByDescending(s => s.Category.CategoryName);
                    break;
                case "cat_asc":
                    folders = folders.OrderBy(s => s.Category.CategoryName);
                    break;
                default:  // Name ascending 
                    folders = folders.OrderBy(s => s.FolderName);
                    break;
            }

            // Birtir 25 texta á hverri síðu
            int pageSize = 10;
            // pageNumber er sjálfgefið 1 ef engin síða er valin
            int pageNumber = (page ?? 1);
            return View(folders.ToPagedList(pageNumber, pageSize));
        }

        // GET: /Folder/Create
        [Authorize]
        public ActionResult Create(int? requestID)
        {
            ViewBag.request = repo.GetRequestById(requestID);
            ViewBag.requestId = requestID;
            return View();
        }

        // POST: /Folder/Create
        // To protect from overposting attacks, please enable the specific properties you want to bind to, for 
        // more details see http://go.microsoft.com/fwlink/?LinkId=317598.
        [HttpPost]
        [ValidateAntiForgeryToken]
        public ActionResult Create([Bind(Include="ID,CategoryID,FolderName,Link,Poster,Description,DateCreated")] Folder folder, HttpPostedFileBase file, SubFile subfile, int? requestID)
        {
            if (ModelState.IsValid)
            {
                if (!repo.AddFile(file, folder, subfile))
                {
                    return View("WrongFile");
                }

                if (!repo.RequestFinished(requestID))
                {
                    return View("Error");
                }
                
                repo.Save();
                return RedirectToAction("Details", new { id = folder.ID });
            }
            return View(folder);
        }

        // GET: /Folder/Edit/5
        [Authorize]
        public ActionResult Edit(int? id)
        {
            if (id == null)
            {
                return View("Error");
            }
            Folder folder = repo.GetFolderById(id);
            if (folder == null)
            {
                return View("NotFound");
            }
            return View(folder);
        }

        // POST: /Folder/Edit/5
        // To protect from overposting attacks, please enable the specific properties you want to bind to, for 
        // more details see http://go.microsoft.com/fwlink/?LinkId=317598.
        [HttpPost]
        [ValidateAntiForgeryToken]
        public ActionResult Edit([Bind(Include="ID,CategoryID,FolderName,Link,Poster,Description,DateCreated")] Folder folder)
        {
            if (ModelState.IsValid)
            {
                repo.Entry(folder);
                repo.Save();
                return RedirectToAction("Details", new { id = folder.ID });
            }
            return View(folder);
        }

        // GET: /Folder/Delete/5
        [Authorize]
        public ActionResult Delete(int? id)
        {
            if (id == null)
            {
                return View("Error");
            }
            Folder folder = repo.GetFolderById(id);
            if (folder == null)
            {
                return View("NotFound");
            }
            return View(folder);
        }

        // POST: /Folder/Delete/5
        [HttpPost, ActionName("Delete")]
        [ValidateAntiForgeryToken]
        public ActionResult DeleteConfirmed(int id)
        {
            Folder folder = repo.GetFolderById(id);
            repo.RemoveFolder(folder);
            repo.Save();
            return RedirectToAction("Index");
        }

        [HttpPost]
        [Authorize]
        public ActionResult CreateComment(Comment c, int? id)
        {
            // Geyma 'id' svo hægt sé að nálgast það í 'View-inu'
            ViewBag.folderid = id;

            // Sækja notendanafn þess sem er innskráður
            string usr = System.Web.HttpContext.Current.User.Identity.Name;

            // Öryggisráðstafanir
            if (usr == null || c.CommentText == null)
            {
                return RedirectToAction("Details", new { ID = id });
            }
            else
            {
                // Tengja athugasemdina við möppu og notanda
                c.FolderID = ViewBag.folderid;
                c.UserName = usr;

                // Setja athugasemdina í gagnagrunninn og vista breytingar
                repo.AddComment(c);
                repo.Save();

                // Fara til baka á síðuna þar sem athugasemdin var skráð
                return RedirectToAction("Details", new { ID = id });
            }
        }

        [HttpGet]
        [Authorize]
        public ViewResult TextEdit(int? id, int? page)
        {
            if (id == null)
            {
                return View("Error");
            }
            // Heldur utan um hvaða möppu textaskrárnar eru í
            ViewBag.folderid = id;
            Folder folder = repo.GetFolderById(id);
            // Enska þýðingin
            SubFile engFile = folder.SubFiles.Where(f => f.LanguageID == 1).SingleOrDefault();
            // Íslenska þýðingin
            SubFile iceFile = folder.SubFiles.Where(f => f.LanguageID == 2).SingleOrDefault();
            // Línurnar í ensku þýðingunni
            IEnumerable<SubFileLine> engLines = engFile.SubFileLines.ToList();
            // Línurnar í íslensku þýðingunni
            IEnumerable<SubFileLine> iceLines = iceFile.SubFileLines.ToList();
            // Báðar þýðingar tengdar saman eftir línunúmerum
            IList<LinesAndTranslations> TextList = repo.GetText(engLines, iceLines);

            int pageSize = 20;
            int pageNumber = (page ?? 1);
            ViewBag.PageNr = pageNumber;
            return View(TextList.ToPagedList(pageNumber, pageSize));
        }

        [HttpPost]
        [Authorize]
        [ValidateInput(false)]
        [MultipleButton(Name = "action", Argument = "SaveQuit")]
        public ActionResult TextEdit(IList<LinesAndTranslations> model, int folderId)
        {
            if (ModelState.IsValid)
            {
                foreach (var line in model)
                {
                    // Lína úr ensku þýðingunni
                    SubFileLine engLine = repo.GetLineById(line.EngLineID);
                    // Lína úr íslensku þýðingunni
                    SubFileLine iceLine = repo.GetLineById(line.IceLineID);

                    // Uppfærir ensku línuna
                    engLine.LineText = line.EngText;
                    engLine.StartTime = line.StartTime;
                    engLine.EndTime = line.EndTime;
                    // Uppfærir íslensku línuna
                    iceLine.LineText = line.IceText;
                    iceLine.StartTime = line.StartTime;
                    iceLine.EndTime = line.EndTime;
                }
                repo.Save();
                return RedirectToAction("Details", new { ID = folderId });
            }
            return View(model);
        }

        [HttpPost]
        [Authorize]
        [ValidateInput(false)]
        [MultipleButton(Name = "action", Argument = "SaveKeepGoing")]
        public ActionResult TextEdit(IList<LinesAndTranslations> model, int folderId, int pageNr)
        {
            if (ModelState.IsValid)
            {
                foreach (var line in model)
                {
                    // Lína úr ensku þýðingunni
                    SubFileLine engLine = repo.GetLineById(line.EngLineID);
                    // Lína úr íslensku þýðingunni
                    SubFileLine iceLine = repo.GetLineById(line.IceLineID);

                    // Uppfærir ensku línuna
                    engLine.LineText = line.EngText;
                    engLine.StartTime = line.StartTime;
                    engLine.EndTime = line.EndTime;
                    // Uppfærir íslensku línuna
                    iceLine.LineText = line.IceText;
                    iceLine.StartTime = line.StartTime;
                    iceLine.EndTime = line.EndTime;
                }
                repo.Save();
                return RedirectToAction("TextEdit", new { ID = folderId, page = pageNr });
            }
            return View(model);
        }

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

        // GET: /Folder/Details/5
        public ActionResult Details(int? id)
        {
            if (id == null)
            {
                return View("Error");
            }
            Folder folder = repo.GetFolderById(id);
            if (folder == null)
            {
                return View("NotFound");
            }
            // Nær í nafnið á textaskránni inn í möppunni og geymir það í ViewBag
            SubFile s = folder.SubFiles.FirstOrDefault();
            ViewBag.engFile = folder.SubFiles.Where(f => f.LanguageID == 1).SingleOrDefault().ID;
            ViewBag.iceFile = folder.SubFiles.Where(f => f.LanguageID == 2).SingleOrDefault().ID;
            ViewBag.SubName = s.SubName.Substring(0, s.SubName.Length - 7) + ".srt";
            ViewBag.folderid = folder.ID;

            return View(folder);
        }

        public FileStreamResult GetFile(int fileId, int folderId, TimeViewModel tvm)
        {
            // Nær í textaskrána úr gagnagrunninum
            SubFile file = repo.GetFolderById(folderId).SubFiles.Where(f => f.ID == fileId).SingleOrDefault();
            // Heldur utan um línurnar í skránni
            IEnumerable<SubFileLine> lines = file.SubFileLines.ToList();
            List<SubFileLine> copy = new List<SubFileLine>();

            if (tvm.Hours != 0 || tvm.Minutes != 0 || tvm.Seconds != 0 || tvm.Milliseconds != 0)
            {
                if (tvm.Negative)
                {
                    tvm.Hours *= -1;
                    tvm.Minutes *= -1;
                    tvm.Seconds *= -1;
                    tvm.Milliseconds *= -1;
                }
                foreach (var line in lines)
                {
                    SubFileLine newLine = line;
                    int hour = Convert.ToInt32(line.StartTime.Substring(0, 2)) + tvm.Hours;
                    int min = Convert.ToInt32(line.StartTime.Substring(3, 5)) + tvm.Minutes;
                    int sec = Convert.ToInt32(line.StartTime.Substring(6, 8)) + tvm.Seconds;
                    int ms = Convert.ToInt32(line.StartTime.Substring(9, 12)) + tvm.Milliseconds;
                    string newStart = hour.ToString() + ":" + min.ToString() + ":" + sec.ToString() + "," + ms.ToString();

                    hour = Convert.ToInt32(line.EndTime.Substring(0, 2)) + tvm.Hours;
                    min = Convert.ToInt32(line.EndTime.Substring(3, 5)) + tvm.Minutes;
                    sec = Convert.ToInt32(line.EndTime.Substring(6, 8)) + tvm.Seconds;
                    ms = Convert.ToInt32(line.EndTime.Substring(9, 12)) + tvm.Milliseconds;
                    string newEnd = hour.ToString() + ":" + min.ToString() + ":" + sec.ToString() + "," + ms.ToString();

                    newLine.StartTime = newStart;
                    newLine.EndTime = newEnd;
                    copy.Add(newLine);
                }
            }
            else
            {
                copy = lines.ToList();
            }
            // Nafnið á skránni
            string name = file.SubName;
            // Notað við að búa til textaskrána
            FileInfo info = new FileInfo(name);

            using (StreamWriter writer = info.CreateText())
            {
                // Loopa sem nær allar línurnar í textaskránni
                // og bætir þeim í útstrauminn 
                foreach (var line in copy)
                {
                    writer.WriteLine(line.LineNumber);
                    writer.Write(line.StartTime);
                    writer.Write(" --> ");
                    writer.WriteLine(line.EndTime);
                    writer.WriteLine(line.LineText);
                    writer.WriteLine("");
                }
            }
            // Hækka niðurhalsteljarann um einn fyrir textaskrána
            file.SubDownloadCounter += 1;
            // Vista breytingarnar
            repo.Save();
            // Skila skránni sem textaskrá og með nafninu 'name'
            return File(info.OpenRead(), "text/plain", name);
        }

        // Losar öll hangangi gögn til gagnagrunnsins
        protected override void Dispose(bool disposing)
        {
            if (disposing)
            {
                repo.Dispose();
            }
            base.Dispose(disposing);
        }
    }
}

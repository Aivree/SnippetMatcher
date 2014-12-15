using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;
using System.Web.Mvc;
using Activity.Service;
using Activity.Models;
using Activity.Models.Others;

namespace Activity.Areas.Admin.Controllers
{
	[Authorize(Users = "admin")]
	public class ActiveController : Controller
	{
		private SiteService siteService = new SiteService();
		private PictureService pictureService = new PictureService();
		//
		// GET: /Admin/Active/

		private readonly DateTime date = DateTime.Now.Date;

		public ActionResult Index(int? page)
		{
			var actives = siteService.GetActives().Where(m => m.StartDate <= date && m.EndDate >= date).ToList();

			var model = new Paginated<Active>(actives, page ?? 1, 10);

			return View(model);
		}

		[HttpPost]
		public ActionResult AddPhoto(int id)
		{
			var result = pictureService.AddHomePhoto(id);

			return Json(result);
		}

		public ActionResult NotStart(int? page)
		{
			var actives = siteService.GetActives().Where(m => m.StartDate > date).ToList();

			var model = new Paginated<Active>(actives, page ?? 1, 10);

			return View(model);
		}

		public ActionResult EndActive(int? page)
		{
			var actives = siteService.GetActives().Where(m => m.EndDate < date).ToList();

			var model = new Paginated<Active>(actives, page ?? 1, 10);

			return View(model);
		}

		public ActionResult Wait(int? page)
		{
			var actives = siteService.GetWaitActives().ToList();

			var model = new Paginated<Active>(actives, page ?? 1, 10);

			return View(model);
		}

		public ActionResult Add()
		{
			var model = new Active();
			model.DateCreated = DateTime.Now;
			model.UserID = User.Identity.Name;
			model.ContactPeople = User.Identity.Name;
			model.ContactPerson = User.Identity.Name;
			var user = new MembershipService().GetUser(model.UserID);
			model.PersonPhone = user.Contact;

			return View(model);
		}

		[HttpPost]
		[ValidateInput(false)]
		public ActionResult Add(Active active)
		{
			active.IsPublic = false;

			var result = siteService.AddActivity(active);

			return Json(result);
		}

		public ActionResult Edit(int id)
		{
			var active = siteService.GetActive(id);
			var tags = siteService.GetTagJoins(id).ToList();
			//ViewBag.Picture = active.PictureFile;
			//foreach (var item in tags)
			//{
			//    active.Tags += item.Tag + " ";
			//}

			return View(active);
		}

		[HttpPost]
		[ValidateInput(false)]
		public ActionResult Edit(Active active)
		{
			var result = siteService.UpdateActive(active);

			return Json(result);
		}

		public ActionResult Delete(int id)
		{
			var result = siteService.DelActive(id);

			return Json(result, JsonRequestBehavior.AllowGet);
		}

		public ActionResult Apply()
		{
			return View();
		}

		public ActionResult Pass(int id)
		{
			var result = siteService.Pass(id);

			return Json(result);
		}

		public ActionResult People(int id, int? page)
		{
			var peoples = siteService.GetApplies(id).ToList();
			var model = new Paginated<Apply>(peoples, page ?? 1, 20);
			ViewBag.Account = (page ?? 1) * 20 - 19;
			return View(model);
		}

		public ActionResult GetApply(int id)
		{
			var apply = siteService.GetApply(id);

			return Json(apply);
		}

		//public ActionResult EditApply(int id)
		//{

		//}

		public ActionResult CancelApply(int id)
		{
			var cancel = siteService.DelApply(id);

			return Json(cancel);
		}

		public ActionResult SendSms(string applyIDs, string phoneNumber, string smsContent, int? id)
		{
			BaseObject obj = new BaseObject();

			try
			{
				var ids = applyIDs.Split(';');
				if (!string.IsNullOrEmpty(phoneNumber))
				{
					phoneNumber = phoneNumber.Trim(';') + ";";
				}

				foreach (var item in ids)
				{
					if (string.IsNullOrEmpty(item))
					{
						continue;
					}
					var apply = siteService.GetApply(Convert.ToInt32(item));
					if (apply == null)
					{
						continue;
					}
					phoneNumber += apply.Contact + ";";
				}

				if (string.IsNullOrEmpty(phoneNumber))
				{
					obj.Tag = -2;
					obj.Message = "该短信没有发送的对象!";

					return Json(obj);
				}

				obj = siteService.SendSms(phoneNumber, smsContent, User.Identity.Name, id);
			}
			catch
			{
				obj.Tag = -1;
			}

			return Json(obj);
		}

		public ActionResult SmsLog()
		{
			var smslogs = siteService.GetSmsLogs();

			return View(smslogs);
		}

		#region 分类

		//
		// GET: /Admin/Blog/Categories

		public ActionResult Categories()
		{
			var categories = siteService.GetCategories().ToList();

			return View(categories);
		}

		[HttpPost]
		public ActionResult Categories(Category category)
		{
			if (ModelState.IsValid)
			{
				if (category.CategoryID > 0)
				{
					siteService.UpdateCategory(category);
					siteService.Save();
				}
				else
				{
					siteService.InsertCategory(category);
					siteService.Save();
				}

				return RedirectToAction("Categories");
			}
			else
			{
				return RedirectToAction("Categories");
			}
		}

		public ActionResult DeleteCategory(int id)
		{
			siteService.DeleteCategory(id);
			siteService.Save();

			return RedirectToAction("Categories");
		}

		#endregion
	}
}

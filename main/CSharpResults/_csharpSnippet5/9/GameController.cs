using System.Web.Mvc;
using MOM.Web.Core;
using MOM.Web.Filters;
using MOM.Web.Models;

namespace MOM.Web.Controllers
{
	[Authorize]
	public class GameController : Controller
	{
		[HttpGet]
		public ActionResult Battle(Login login)
		{
			var g = login.CurrentGame;
			if (g == null)
				return RedirectToAction("Index", "Arena");

			g.Start(1000);
			return View(new MapViewModel(g));
		}

		[HttpPost]
		[NoCache]
		public ActionResult SendCommand(Login login, string command)
		{
			var g = login.CurrentGame;
			if (g == null)
				return Json(new { result = "cancel" });

			g.Execute(command, login.PlayerName);
			return Json(new { result = "ok" });
		}

		[HttpGet]
		[NoCache]
		public ActionResult GetJsonMap(Login login)
		{
			var g = login.CurrentGame;
			if (g.Game.IsFinished)
			{
				login.CurrentGame = null;
			}
			return Json(g.MapToJson(login), JsonRequestBehavior.AllowGet);
		}
	}
}

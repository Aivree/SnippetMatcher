using SendASmile.Models;
using System.Linq;
using System.Web.Mvc;

namespace SendASmile.Controllers
{
    /// <summary>
    /// Handles Mail unsubscription attempts.
    /// </summary>
    public class MailSubController : Controller
    {
        
        /// <summary>
        /// Displays the unsubscription page.
        /// </summary>
        /// <param name="subId">Id of the subscription to cancel.</param>
        /// <returns>View with subscription Id.</returns>
        public ActionResult Unsub(int subId)
        {
            return View(subId);
        }

        /// <summary>
        /// Processes unsubscribe attempt. Either directs to success or failure page.
        /// </summary>
        /// <param name="subId">Id of the subscription to cancel.</param>
        /// <param name="email">Id of the email to validate the cancellation attempt.</param>
        /// <returns>Directs to success or failure page.</returns>
        [HttpPost]
        public ActionResult Unsub(int subId, string email)
        {
            using (var db = new SmileContext())
            {
                if (db.MailSub.Where(s => s.subId == subId).Where(e => e.email == email).Count() > 0)
                {
                    var sub = db.MailSub.Find(subId);
                    db.MailSub.Remove(sub);
                    db.SaveChanges();
                    Response.RedirectToRoute(new { controller = "MailSub", action = "Success" });
                }
                else
                {
                    Response.RedirectToRoute(new {controller = "MailSub", action = "Failure" });
                }
            }
            return View(subId);
        }

        /// <summary>
        /// Displays unsubscribe success page.
        /// </summary>
        /// <returns>Static View.</returns>
        public ActionResult Success()
        {
            return View();
        }

        /// <summary>
        /// Displays the unsubscribe failure page.
        /// </summary>
        /// <returns>Static view.</returns>
        public ActionResult Failure()
        {
            return View();
        }
    }
}
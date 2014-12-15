using System;
using System.Linq;
using System.Web.Mvc;
using Tarts.Bookings;
using Tarts.Config;
using Tarts.Content;
using Tarts.Ecommerce;
using Tarts.Events;
using Tarts.Persistance;
using Tarts.Web.Areas.TartsAdmin.Models.Events;
using Tarts.Web.Infrastructure.Utilities;

namespace Tarts.Web.Areas.TartsAdmin.Controllers
{
    public class BookingsController : Controller
    {

        private GenericRepo Repo;
        public BookingsController(GenericRepo repo)
        {
            Repo = repo;
        }

        [Authorize]
        public ActionResult List(int id)
        {
            var evt = Repo.GetById<Event>(id);
            ViewBag.Event = evt;
            return View(Repo.GetAll<Booking>("Created").Where(x => x.Event.ID == id).ToList());
        }


        [Authorize]
        public ActionResult Edit(int id)
        {
            return View(Repo.GetById<Booking>(id));
        }

        [Authorize]
        public ActionResult Cancel(int id)
        {
            var booking = Repo.GetById<Booking>(id);
            int eventID = booking.Event.ID;
            if (booking.CanDelete())
                Repo.Delete(booking);
            else
                TempData["ErrorMessage"] = "This booking cannot be deleted. Please cancel to remove it.";
            return RedirectToAction("List", new { id = eventID });
        }


        [Authorize]
        public ActionResult Delete(int id)
        {
            var booking = Repo.GetById<Booking>(id);
            int eventID = booking.Event.ID;
            if(booking.CanDelete())
                Repo.Delete(booking);
            else
                TempData["ErrorMessage"] = "This booking cannot be deleted. Please cancel to remove it.";
            return RedirectToAction("List", new {id = eventID});
        }

        [Authorize]
        public ActionResult SendConfirmation(int id)
        {
            var booking = Repo.GetById<Booking>(id);
            var settings = Repo.GetById<Settings>(1);
            var emailer = new Emailer(settings);
            if(booking.Status != Booking.BookingStatus.Complete)
                TempData["ErrorMessage"] = "Failed to send booking confirmation for non complete booking";
            else
            {
                var sent = emailer.SendEmail(booking.Customer.Email, booking.Customer.FirstName + " " + booking.Customer.Surname,
                                  "Apple Tarts Booking Confirmation", booking.Event.BookingConfirmation.ProcessVelocityTemplate(booking));
                if (sent.Succeeded)
                    TempData["Message"] = "Booking confirmation sent";
                else
                    TempData["ErrorMessage"] = "Failed to send booking confirmation. {0}".FormatString(sent.Message);
            }
            return RedirectToAction("Edit", new {id = id});
        }

        [Authorize]
        public ActionResult MarkPaymentAsComplete(int paymentID)
        {
            var payment = Repo.GetById<Payment>(paymentID);
            var settings = Repo.GetById<Settings>(1);
            payment.Booking.MarkPaymentAsComplete(payment, "Manually Confirmed");
            Repo.Save(payment.Booking);
            
            var emailer = new Emailer(settings);
            var sent = emailer.SendEmail(payment.Booking.Customer.Email, payment.Booking.Customer.FirstName + " " + payment.Booking.Customer.Surname,
                                "Apple Tarts Booking Confirmation", payment.Booking.Event.BookingConfirmation.ProcessVelocityTemplate(payment.Booking));
            if (sent.Succeeded)
                TempData["Message"] = "Booking completed and confirmation email sent";
            else
                TempData["ErrorMessage"] = "Failed to send booking confirmation. {0}".FormatString(sent.Message);
            
            return RedirectToAction("Edit", new { id = payment.Booking.ID });
        }
        
    }
}

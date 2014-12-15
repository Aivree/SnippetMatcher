using System;
using System.Collections.Generic;
using System.Linq;
using System.Net.Mail;
using System.Web;
using System.Web.Mvc;
using System.Web.Routing;
using Kyckling.Domain.Infrastructure.Services;
using Kyckling.Domain.Models;
using Kyckling.Web.Infrastructure.Repositories;
using Kyckling.Web.Models;
using Postal;

namespace Kyckling.Web.Controllers
{
    public class ReservationController : Controller
    {
        private readonly IReservationService _reservationService;
        //
        // GET: /Reservation/
        public ReservationController(IReservationService reservationService)
        {
            _reservationService = reservationService;
        }
        public ActionResult Index()
        {
            return View();
        }


        [Authorize]
        public ActionResult List() //Visar alla reservationer för en viss restaurang (inloggad user)
        {
            var username = HttpContext.User.Identity.Name;
            var model = new ListReservationsModel(username, _reservationService.GetRestaurantReservations(username).Where(x => x.TimeSlot.Date >= DateTime.Now.Date && x.TimeSlot.Date <= DateTime.Now.AddDays(6).Date).OrderBy(x => x.TimeSlot));
            model.FromDate = DateTime.Now.Date;
            model.ToDate = DateTime.Now.AddDays(6).Date;
            return View(model);
        }

        [Authorize]
        [HttpPost]
        public ActionResult List(DateTime fromDate, DateTime toDate)
        {
            var username = HttpContext.User.Identity.Name;
            var reservations = new ListReservationsModel(username, _reservationService.GetRestaurantReservations(username).Where(x => x.TimeSlot.Date >= fromDate.Date && x.TimeSlot.Date <= toDate.Date).OrderBy(x => x.TimeSlot));
            reservations.FromDate = fromDate;
            reservations.ToDate = toDate;
            return View(reservations);
        }

        [HttpGet]
        public ActionResult SelectDate(int id)
        {
            var rest = _reservationService.GetRestaurant(id);
            var model = new Kyckling.Web.Models.SelectDateModel
            {
                Id = rest.Id,
                Name = rest.Name,
                NumberOfPersons = 1,
                ReservationDate = DateTime.Now,
                PersonCountList = new SelectList(Enumerable.Range(1, rest.MaxPersonsBooking))
            };
            return View(model);
        }

        [HttpPost]
        public ActionResult SelectDate(SelectDateModel model)
        {
            if (ModelState.IsValid)
            {
                var totalReservations = _reservationService.GetReservations(model.Id, model.ReservationDate).ToList();
                int sum = 0;
                foreach (var v in totalReservations)
                {
                    sum += v.PersonCount;
                }

                var rest = _reservationService.GetRestaurant(model.Id);
                if (rest.DayCapacity - sum < model.NumberOfPersons)
                {
                    return RedirectToAction("Error", "Reservation",
                        new { id = rest.Id, message = "Tyvärr är restaurangen fullbokad denna dag!" });
                }

                //TEMPORÄRT
                return RedirectToAction("Create", "Reservation", new { id = model.Id, numberOfPersons = model.NumberOfPersons, date = model.ReservationDate});
            }
            //TEMPORÄRT
            return View();
        }

        [HttpGet]
        public ActionResult Create(int id, int numberOfPersons, DateTime date)
        {
            var rest = _reservationService.GetRestaurant(id);
            var createModel = new CreateReservationModel();
            createModel.TimeSlot = date;
            createModel.RestaurantId = rest.Id;
            createModel.RestaurantName = rest.Name;
            createModel.Email = "";
            createModel.AvailableTimes = _reservationService.GetAvailableTimes(rest.Id, numberOfPersons, date);
            createModel.TimeSelectList = new SelectList(createModel.AvailableTimes.Where(x => x.FreeSeats), "Hour", "Hour");
            createModel.PersonCount = numberOfPersons;
            createModel.Date = date;
            createModel.Name = "";
            return View(createModel);
        }

        [HttpPost]
        public ActionResult Create(CreateReservationModel model)
        {
            if (ModelState.IsValid)
            {
                Reservation r = new Reservation();
                r.Name = model.Name;
                r.Email = model.Email;
                r.Telephone = model.Telephone;
                r.PersonCount = model.PersonCount;
                r.Restaurant = _reservationService.GetRestaurant(model.RestaurantId);
                r.TimeSlot = model.Date.AddHours(model.Time);

                int reservationId = _reservationService.SaveReservation(r).Id;

                

                return RedirectToAction("Details", "Reservation", new { id = reservationId});

            }
            return View(model);
        }

        public ActionResult Details(int id = 0)
        {
            var model = _reservationService.GetReservation(id);
            return View(model);
        }


        public ActionResult Error(int id, string message)
        {
            var model = new ErrorViewModel();
            model.RestaurantId = id;
            model.Message = message;
            return View("Error", model);
        }

        [Authorize]
        public ActionResult CancelBooking(int Id)
        {
            var booking = _reservationService.GetReservation(Id);
            CancelReservationModel reservation=new CancelReservationModel(){
                Email = booking.Email,
                Message = "Hej "+booking.Name+"!\n\rDin bokning för "+booking.PersonCount+" personer på "+booking.Restaurant.Name+" den "+booking.TimeSlot.ToShortDateString()+" kl "+booking.TimeSlot.ToLocalTime().ToShortTimeString()+" har blivit inställd\n\n\rMed vänlig hälsning "+booking.Restaurant.Name,
                SendMail = false,
                BookingId = Id
            };
            return View(reservation);
        }

        [Authorize,HttpPost]
        public ActionResult CancelBooking(CancelReservationModel model, string submitButton)
        {
            if (submitButton == "Avbryt") return View("Index");
            var booking = _reservationService.GetReservation(model.BookingId);
            _reservationService.DeleteReservation(booking);
            if (model.SendMail)
            {
                dynamic email = new Email("CancelBookingEmail");
                email.To = model.Email;
                email.Message = model.Message;
                email.Send();
            }
            return View("BookingCanceled");
        }

    }
}

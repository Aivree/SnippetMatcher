using AgileWays.Cqrs.Commands.Reservation;
using AgileWays.Repository;
using MvcWebRole1.Models;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;
using System.Web.Mvc;

namespace MvcWebRole1.Controllers
{
    public class ReservationController : Controller
    {
        [HttpGet]
        public ActionResult Create()
        {
            CloudHelper.LogMessage(1, "About to create a new reservation", "CREATE");
            return View();
        }

        [HttpPost]
        public ActionResult Create(ReservationModel model)
        {
            Guid id = Guid.NewGuid();
            var cmd = new CreateReservation()
            {
                Name = model.Name,
                NumberOfSeats = model.NumberOfSeats,
                DiscountCode = model.DiscountCode,
                ReservationId = id
            };

            //submit the command
            AgileWays.Cqrs.Commands.Writer.ICommandWriter writer = new AzureCommandWriter();
            writer.SendCommand(cmd);
            //CloudHelper.EnqueueCommand(cmd);

            CloudHelper.LogMessage(1, "Created a new reservation", "CREATE");

            //redirect to confirmation
            return RedirectToAction("Confirmation", new { id = id });
        }

        [HttpGet]
        public ActionResult Confirmation(Guid id)
        {
            ViewBag.ID = id;

            CloudHelper.LogMessage(1, "Received a confirmation", "CONFIRM");
            return View();
        }

        [HttpGet]
        public ActionResult List()
        {

            CloudHelper.LogMessage(1, "Requested a list of all reservations", "LIST");

            var repo = new ReservationRepository();
            var reservations = repo.GetAllReservations();

            var reservationModel = reservations
                                    .Select(r => new ReservationDetail
                                    {
                                        NumberOfSeats = r.NumberOfSeats,
                                        IsDeleted = r.IsDeleted,
                                        ReservationId = r.ReservationId,
                                        ReservationMade = r.ReservationMade,
                                        SeatsReservedFor = r.SeatsReservedFor,
                                        TotalCost = r.TotalCost
                                    });
            return View(reservationModel);
        }

        [HttpDelete]
        public ActionResult Delete(Guid id, String reason)
        {
            //var repo = new ReservationRepository();
            //repo.DeleteReservation(id);

            var cmd = new CancelReservation()
            {
                ReservationId = id,
                Reason = reason
            };

            AgileWays.Cqrs.Commands.Writer.ICommandWriter writer = new AzureCommandWriter();
            writer.SendCommand(cmd);
            //CloudHelper.EnqueueCommand(cmd);

            CloudHelper.LogMessage(1, "Canceled a reservation", "CANCEL");

            return Json(new { isValid = true }, JsonRequestBehavior.AllowGet);
        }

        public ActionResult CancelConfirmation(Guid id)
        {
            ViewBag.ID = id;

            CloudHelper.LogMessage(1, "Received confirmation on reservation cancellation", "CONFIRM");
            return View();
        }
    }
}

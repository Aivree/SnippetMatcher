using System;
using System.Collections.Generic;
using System.Linq;
using System.Net;
using System.Net.Http;
using System.Web;
using System.Web.Mvc;
using CarRental.Infrastructure.Commands;
using CarRental.Infrastructure.Messaging;

namespace CarRentalWebRole.Controllers
{
    public class CarBooking : Controller
    {
        //
        // GET: /CarBooking/
        ICommandBus Channel;

        public CarBooking(ICommandBus IChannel)
        {
            this.Channel = IChannel;
        }

        public ActionResult Index()
        {
            return View();
        }

        
        // POST api/values
        [HttpPost]
        public ActionResult PostCarBooking(Contracts.CarRental carRental)
        {
            object success = new 
                {
                    status = HttpStatusCode.Created
                };
            try
            {
                CarBookingCommand obj = new CarBookingCommand(carRental.RentalId, carRental.CarModelId,
                    carRental.CarRentalEndDate,
                    carRental.CarRentalStartDate, carRental.LocationId, carRental.TotalCost, carRental.DriverName,
                    carRental.LicenseneNumber, carRental.ContactNumber, carRental.EmailId, carRental.Address);
                Channel.Send<CarBookingCommand>(obj);
            }
            catch (Exception ex)
            {
                object error = new 
                {
                    status = HttpStatusCode.Created
                };
                return Json(error);
            }
            return Json(success);
        }


        [HttpPost]
        public ActionResult PostCarBookingChange(Guid rentalId, DateTime carRentalEndDate)
        {
            CarBookingChangingCommand changingCommand = new CarBookingChangingCommand(rentalId, carRentalEndDate);
            Channel.Send<CarBookingChangingCommand>(changingCommand);
            return Json(new object());
        }

        [HttpPost]
        public ActionResult CarBookingCancel(Guid rentalId)
        {
            CarBookingCancelingCommand cancelingCommand = new CarBookingCancelingCommand(rentalId, "Canceled");
            Channel.Send<CarBookingCancelingCommand>(cancelingCommand);
            return Json(new object());
        }    
    }
}

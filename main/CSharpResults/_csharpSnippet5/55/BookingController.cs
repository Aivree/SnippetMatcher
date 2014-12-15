using System.Linq;
using System.Net.Mail;
using System.Web.Mvc;
using RealEstateLogicModels;
namespace Real_Estate_Booking.Controllers
{
    public class BookingController : BaseController
    {

        public ActionResult Book(Booking book)
        {
            if (bookLogic.GetBookingsBetween(book.StartDate, book.EndDate).Any(b => !b.State.Equals("canceled") && b.RealEstate.Id == book.RealEstate.Id))
            {
                return View("BookingResult", null, book.RealEstate.Name + "is already booked on the specified dates!");
            }

            bookLogic.AddBooking(book);
            book.State = "pending";

            var message = new MailMessage();
            message.To.Add(book.RealEstate.Owner.Email);
            message.Subject = "Confirm booking #" + book.Id;
            message.Body = "Booking #" + book.Id + "details for " + book.RealEstate.Name + ": \n" +
							"\tStart date:" + book.StartDate + 
							"\n\tEnd date" + book.EndDate +
                           "\n\nFollow this link to confirm: http://www.realestatebooking.com/Booking/Confirm?id=" + book.Id + 
						   "\nor this if you want to cancel the booking: http://www.realestatebooking.com/Booking/Cancel?id=" + book.Id;
            
			
			var smtp = new SmtpClient();
            smtp.Send(message);
            return View("BookingResult", null, "The booking request was successfuly completed. " +
				"\n\nCurrent booking state:" +
				"\n\tId: " + book.Id +
				"\n\tStatus: " + book.State);
        }

		public ActionResult Confirm(int id) {
			var b = bookLogic.GetBookById(id);

			if(b.State == "pending") {
				b.State = "confirmed";

				var message = new MailMessage();
				message.To.Add(b.Email);
				message.Subject = "Booking #" + id + " confirmed";
				message.Body = "Your booking was confirmed." +
				               "\nDetails for Booking #" + b.Id + " (" + b.RealEstate.Name + "): \n" +
				               "\tStart date:" + b.StartDate +
				               "\n\tEnd date" + b.EndDate;
				
				var smtp = new SmtpClient();
				smtp.Send(message);

				return View("BookingResult", null, "The booking was successful confirmed");
			}
			return View("BookingResult", null, "This booking is not in a pending state");
		}


		public ActionResult Cancel(int id) {
			var b = bookLogic.GetBookById(id);
			if(b.State == "pending") {
				b.State = "canceled";

				var message = new MailMessage();
				message.To.Add(b.Email);
				message.Subject = "Booking #" + id + " canceled";
				message.Body = "Your booking was canceled." +
							   "\nDetails for Booking #" + b.Id + " (" + b.RealEstate.Name + "): \n" +
							   "\tStart date:" + b.StartDate +
							   "\n\tEnd date" + b.EndDate;

				var smtp = new SmtpClient();
				smtp.Send(message);

				return View("BookingResult", null, "The booking was successful canceled");
			}
			return View("BookingResult", null, "This booking is not in a pending state");
		}

    }
}

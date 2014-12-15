using System.Net;
using System.Net.Mail;

namespace Nonoe.ActionFolders.BusinessLogic.Helpers
{
    public class HelperFunction
    {
        public void SendMail(string smtpServer, string emailTo, string emailFrom, string password, string subject, string body, string fileLocation)
        {
            var fromAddress = new MailAddress(emailFrom, emailFrom);
            var toAddress = new MailAddress(emailTo, emailTo);
            var attachment = new Attachment(fileLocation);

            var smtp = new SmtpClient
            {
                Host = "smtp.gmail.com",
                Port = 587,
                EnableSsl = true,
                DeliveryMethod = SmtpDeliveryMethod.Network,
                UseDefaultCredentials = false,
                Credentials = new NetworkCredential(fromAddress.Address, password)
            };
            using (var message = new MailMessage(fromAddress, toAddress)
            {
                Subject = subject,
                Body = body,
            })
            {
                message.Attachments.Add(attachment);
                smtp.Send(message);
            }

        }


    }
}

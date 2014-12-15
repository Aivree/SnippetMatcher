using System.Configuration;
using System.Net;
using System.Net.Mail;
using Any.Scheduler.Configuration;
using Any.Scheduler.Services.Models;

namespace Any.Scheduler.Services
{
    public class EmailService
    {
        private readonly SmtpClientSection _smtpSettings;

        public EmailService()
        {
            _smtpSettings = (SmtpClientSection) ConfigurationManager.GetSection("smtp");
        }

        public void Send(EmailModel email)
        {
            var smtp = new SmtpClient
            {
                Host = _smtpSettings.Host,
                Port = _smtpSettings.Port,
                EnableSsl = _smtpSettings.EnableSsl,
                UseDefaultCredentials = _smtpSettings.UseDefaultCredentials
            };

            if (!smtp.UseDefaultCredentials)
            {
                smtp.Credentials = new NetworkCredential(_smtpSettings.User, _smtpSettings.Password);
            }

            var mail = new MailMessage
            {
                From = new MailAddress(email.From),
                Subject = email.Subject,
                IsBodyHtml = true,
                Body = email.Body
            };

            string[] recipients = email.To.Split(',', ';');

            foreach (string to in recipients)
            {
                mail.To.Add(new MailAddress(to));
            }

            if (email.Attachmets != null)
            {
                foreach (string fileName in email.Attachmets)
                {
                    var attachment = new Attachment(fileName);
                    mail.Attachments.Add(attachment);
                }
            }

            smtp.Send(mail);
        }
    }
}
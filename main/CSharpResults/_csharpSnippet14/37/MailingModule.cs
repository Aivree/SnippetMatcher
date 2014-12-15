using System;
using System.IO;
using System.Net.Mail;

namespace FaceRecogService
{
    public class MailingModule
    {
        public void sendMailModule(string file_path, bool rename)
        {
            MailMessage mail = new MailMessage();
            SmtpClient SmtpServer = new SmtpClient("smtp.gmail.com");
            mail.From = new MailAddress("Rpi_ImageRecognitionService@gmail.com");
            mail.To.Add("trilokdude@gmail.com");
            mail.Subject = "Test Mail - 1";
            mail.Body = "Mail with Image as attachment";

            System.Net.Mail.Attachment attachment;
            attachment = new System.Net.Mail.Attachment(file_path);
            mail.Attachments.Add(attachment);

            SmtpServer.Port = 587;
            SmtpServer.Credentials = new System.Net.NetworkCredential("trilokdude@gmail.com", "blr560001");
            SmtpServer.EnableSsl = true;

            SmtpServer.Send(mail);
            attachment = null;
            mail = null;
            SmtpServer = null;
            GC.Collect();
        }
    }
}

using System;
using System.Collections.Generic;
using System.Linq;
using System.Net.Mail;
using System.Text;
using System.Threading.Tasks;

namespace AESBeam
{
    public static class SMTPMailer
    {
        public static void SendMail(string AttachmentFile, string Receiver)
        {
            MailMessage eMail = new MailMessage();
            eMail.From = new MailAddress("aessecx@gmail.com");
            eMail.To.Add(Receiver);
            eMail.Attachments.Add(new Attachment(AttachmentFile));
            eMail.Subject = AttachmentFile;
            eMail.Body = AttachmentFile;
            SmtpClient smtp = new SmtpClient();
            smtp.Credentials = new System.Net.NetworkCredential("aessecx@gmail.com", "Aes60250413");
            smtp.Port = 587;
            smtp.Host = "smtp.gmail.com";
            smtp.EnableSsl = true;
            object userState = eMail;
            smtp.SendAsync(eMail, (object)eMail);
            return;
        }
    }
}

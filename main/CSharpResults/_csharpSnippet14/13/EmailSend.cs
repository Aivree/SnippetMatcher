using System;
using System.Collections.Generic;
using System.Linq;
using System.Net.Mail;
using System.Text;
using System.Threading.Tasks;

namespace BusinessLogicLayer
{
  public class EmailSend
    {
        public void SendMail(string sender, string receiver, string resume, string body, string subject)
        {
            MailMessage mail = new MailMessage();
            SmtpClient SmtpServer = new SmtpClient("smtp.gmail.com");
            mail.IsBodyHtml = true;
            string email;
            email = receiver;

            mail.From = new MailAddress(sender);//referal
            mail.To.Add(email);
            mail.Subject = subject;
            mail.Body = body;
            System.Net.Mail.Attachment attachment;
            attachment = new System.Net.Mail.Attachment(resume);
            mail.Attachments.Add(attachment);
            SmtpServer.Port = 587;
            SmtpServer.Credentials = new System.Net.NetworkCredential("sandwichtavisca@gmail.com", "sandwich2014");//referral email id nd password
            SmtpServer.EnableSsl = true;

            SmtpServer.Send(mail);
        }
        public void SendMail(string sender, string receiver, string body, string subject)
        {
            MailMessage mail = new MailMessage();
            SmtpClient SmtpServer = new SmtpClient("smtp.gmail.com");
            mail.IsBodyHtml = true;
            string email;
            email = receiver;

            mail.From = new MailAddress(sender);//referal
            mail.To.Add(email);
            mail.Subject = subject;
            mail.Body = body;
            SmtpServer.Port = 587;
            SmtpServer.Credentials = new System.Net.NetworkCredential("sandwichtavisca@gmail.com", "sandwich2014");//referral email id nd password
            SmtpServer.EnableSsl = true;

            SmtpServer.Send(mail);
        
        }
        

    }
}

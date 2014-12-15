using System;
using System.Collections.Generic;
using System.Linq;
using System.Net;
using System.Net.Mail;
using System.Text;

namespace Send_Email
{
    class Program
    {
        // Server Name	SMTP Address	    Port	SSL
        // Yahoo!	    smtp.mail.yahoo.com	587	    Yes
        // GMail	    smtp.gmail.com	    587	    Yes
        // Hotmail	    smtp.live.com	    587	    Yes

        static void Main(string[] args)
        {
            string smtpAddress = "smtp.gmail.com";
            int portNumber = 587;
            bool enableSSL = true;

            string emailFrom = "name@gmail.com";
            string password = "password";
            string emailTo = "name@gmail.com";
            string subject = "Hello World";
            string body = "Hello World";

            using (MailMessage mail = new MailMessage())
            {
                mail.From = new MailAddress(emailFrom);
                mail.To.Add(emailTo);
                mail.Subject = subject;
                mail.Body = body;
                mail.IsBodyHtml = true;
                // Can set to false, if you are sending pure text.

                //mail.Attachments.Add(new Attachment("C:\\SomeFile.txt"));
                //mail.Attachments.Add(new Attachment("C:\\SomeZip.zip"));

                using (SmtpClient smtp = new SmtpClient(smtpAddress, portNumber))
                {
                    smtp.Credentials = new NetworkCredential(emailFrom, password);
                    smtp.EnableSsl = enableSSL;
                    smtp.Send(mail);
                }
            }

            Console.WriteLine("mail sended");
        }
    }
}

using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Net;
using System.Net.Mail;

namespace iRobotKinect
{
    class SendEmail
    {
        public static bool SendMail(string emailTo, string emailFrom, string password, string subject, string body, string outputFile)
        {
            //Server Name	SMTP Address	Port	SSL
            //Yahoo!	smtp.mail.yahoo.com	587	Yes
            //GMail	smtp.gmail.com	587	Yes
            //Hotmail	smtp.live.com	587	Yes

            string smtpAddress = "smtp.gmail.com";
            int portNumber = 587;
            bool enableSSL = true;

            // string emailFrom = "nerndt@gmail.com";
            // string password = "password";
            // string emailTo = "someone@domain.com";
            // string subject = "Hello";
            // string body = "Hello, I'm just writing this to say Hi!";

            try
            {
                using (MailMessage mail = new MailMessage())
                {
                    mail.From = new MailAddress(emailFrom);
                    mail.To.Add(emailTo);
                    mail.Subject = subject;
                    mail.Body = body;
                    mail.IsBodyHtml = true;
                    // Can set to false, if you are sending pure text.

                    mail.Attachments.Add(new Attachment(outputFile));
                    // mail.Attachments.Add(new Attachment("C:\\SomeZip.zip"));

                    using (SmtpClient smtp = new SmtpClient(smtpAddress, portNumber))
                    {
                        smtp.Credentials = new NetworkCredential(emailFrom, password);
                        smtp.EnableSsl = enableSSL;
                        smtp.Send(mail);
                    }
                }
            }
            catch (Exception ex)
            {
                string error = ex.Message; // Console.WriteLine(ex.Message);
            }
            return true;
        }
    }
}
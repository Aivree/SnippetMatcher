using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

using System.Net;
using System.Net.Mail; // mail


namespace HelloMail
{
    class Program
    {
        static void Main(string[] args)
        {

            try
            {
                MailMessage mail = new MailMessage();
                mail.From = new MailAddress("zunlin1234@gmail.com");
                mail.To.Add("1272098417@qq.com"); 
                //mail.To.Add("zunlin1234@gmail.com");

                mail.Subject = "Test Mail";
                mail.IsBodyHtml = false;
                mail.Body = "This is for testing SMTP mail from GMAIL";

                // attachment
                Attachment attachment = new Attachment("A-way-to-heaven.jpg");
                mail.Attachments.Add(attachment);

                SmtpClient SmtpServer = new SmtpClient("smtp.gmail.com"); // default
                SmtpServer.Port = 587; // default
                SmtpServer.Credentials = new NetworkCredential("zunlin1234@gmail.com", "kezunlin168168");
                SmtpServer.EnableSsl = true;

                SmtpServer.Send(mail);
                Console.WriteLine("mail send");
            }
            catch (Exception ex)
            {
                Console.WriteLine(ex.ToString());
            }
        }
    }
}

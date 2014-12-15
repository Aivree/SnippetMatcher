using System;
using System.IO;
using System.Net.Mail;
using System.Net;

namespace Facade
{
    public class Configuration
    {
        public string Host { get; set; }
        public int Port { get; set; }
        public string Login { get; set; }
        public string Password { get; set; }
    }

    public class SmtpFacade
    {
        private Configuration config;

        public SmtpFacade(Configuration configuration)
        {
            config = configuration;
        }

        public void Send(
            string From, string To, string Subject, string Body, FileStream Attachment, string AttachmentMimeType
        )
        {
            try
            {
                MailMessage mail = new MailMessage();

                mail.From = new MailAddress(From);
                mail.To.Add(new MailAddress(To));

                mail.Subject = Subject;
                mail.Body = Body;

                Attachment attachment = new Attachment(Attachment, Attachment.Name);

                mail.Attachments.Add(attachment);

                SmtpClient smtp = new SmtpClient();

                smtp.Host = config.Host;
                smtp.Port = config.Port;
                smtp.Credentials = new NetworkCredential(config.Login, config.Password);
                smtp.EnableSsl = true;

                smtp.Send(mail);
            }
            catch (Exception e)
            {
                Console.Error.WriteLine(e);
            }
        }
    }

    class Program
    {
        static void Main(string[] args)
        {
            SmtpFacade smtp = new SmtpFacade(new Configuration()
            {
                Host = "smtp.gmail.com",
                Port = 587,
                Login = "foo@gmail.com",
                Password = "bar"
            });

            string from = "foo@gmail.com";
            string to = "foo@gmail.com";
            string subject = "asdfasdfasdf";
            string body = "test";

            FileStream fs = File.Open("test.txt", FileMode.Open, FileAccess.Read);

            smtp.Send(from, to, subject, body, fs, System.Net.Mime.MediaTypeNames.Text.Plain);
        }
    }
}


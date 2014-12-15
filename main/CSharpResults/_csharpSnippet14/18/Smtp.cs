using System;
using System.IO;
using System.Net.Mail;
using System.Net;

// z projektowania obiektowego oprogramowania
namespace Smtp
{
    public class SmtpFacade
    {
        private SmtpConfiguration Config;

        public SmtpFacade(SmtpConfiguration config)
        {
            Config = config;
        }

        public void Send(string from, string[] to, string subject, string body, FileStream[] attachments)
        {
            try
            {
                MailMessage mail = new MailMessage();

                mail.From = new MailAddress(from);

                foreach (string recipient in to)
                {
                    mail.To.Add(new MailAddress(recipient));
                }

                mail.Subject = subject;
                mail.Body = body;

                foreach (var attachment in attachments)
                {
                    mail.Attachments.Add(new Attachment(attachment, attachment.Name));
                }

                SmtpClient smtp = new SmtpClient();

                smtp.Host = Config.Host;
                smtp.Port = Config.Port;
                smtp.Credentials = new NetworkCredential(Config.UserName, Config.Password);
                smtp.EnableSsl = Config.Ssl;

                smtp.Send(mail);
            }
            catch (Exception e)
            {
                Console.Error.WriteLine(e);
            }
        }
    }

    public class SmtpConfiguration
    {
        public string Host { get; set; }
        public int Port { get; set; }
        public bool Ssl { get; set; }
        public string UserName { get; set; }
        public string Password { get; set; }
    }
}

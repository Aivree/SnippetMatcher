using System;
using System.Text;
using System.Net.Mail;
using System.Net;

namespace Segator.Loms.Common.Server.Helpers
{
    public class EmailHelper
    {
        public static void SendMail(IEmailAccountSettingsProvider provider, string to, string subject, string body, Attachment attach, AlternateView av, bool isHTML)
        {
            if (provider.SmtpFrom != String.Empty)
            {
                if (provider.SmtpHost == "") return;
                MailMessage mailMessage = new MailMessage();
                mailMessage.From = new MailAddress(provider.SmtpFrom);
                mailMessage.To.Add(to);
                mailMessage.Subject = subject;
                if (av != null)
                    mailMessage.AlternateViews.Add(av);
                mailMessage.BodyEncoding = Encoding.UTF8;
                mailMessage.Body = body;

                if (attach != null)
                    mailMessage.Attachments.Add(attach);
                if (isHTML)
                    mailMessage.IsBodyHtml = true;

                SmtpClient smtpClient = new SmtpClient();
                smtpClient.Host = provider.SmtpHost;
                smtpClient.Port = Convert.ToInt32(provider.SmtpPort);
                smtpClient.UseDefaultCredentials = Convert.ToBoolean(provider.SmtpCredentials);
                smtpClient.Credentials = new NetworkCredential(provider.SmtpUser, provider.SmtpPwd);
                smtpClient.EnableSsl = Convert.ToBoolean(provider.SmtpSSL);
                smtpClient.Send(mailMessage);
            }
        }
    }

    public interface IEmailAccountSettingsProvider
    {
        string SmtpFrom { get; set; }
        string SmtpHost { get; set; }
        int SmtpPort { get; set; }
        bool SmtpCredentials { get; set; }
        string SmtpUser { get; set; }
        string SmtpPwd { get; set; }
        bool SmtpSSL { get; set; }
        bool Default { get; set; }
    }
}
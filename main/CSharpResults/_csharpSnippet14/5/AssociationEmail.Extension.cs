using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Net.Mail;
using System.Net;

namespace Segator.Loms.Modules.Common.Entities
{
    partial class AssociationEmail
    {
        public void SendMail(string to, string subject, string body, Attachment attach, AlternateView av, bool isHTML)
        {
            if (SmtpFrom != String.Empty)
            {
                if (SmtpHost == "") return;
                MailMessage mailMessage = new MailMessage();
                mailMessage.From = new MailAddress(SmtpFrom);
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
                smtpClient.Host = SmtpHost;
                smtpClient.Port = Convert.ToInt32(SmtpPort);
                smtpClient.UseDefaultCredentials = Convert.ToBoolean(SmtpCredentials);
                smtpClient.Credentials = new NetworkCredential(SmtpUser, SmtpPwd);
                smtpClient.EnableSsl = Convert.ToBoolean(SmtpSSL);
                smtpClient.Send(mailMessage);
            }
        }
    }
}

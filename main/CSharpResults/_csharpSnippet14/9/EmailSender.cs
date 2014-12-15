using System;
using System.Collections.Generic;
using System.Configuration;
using System.Net.Configuration;
using System.Net.Mail;

namespace bsx.DirLaguna.Admin.Code
{
    public static class EmailSender
    {
        public static bool SendEmail(string toEmails, string fromEmail, string mailBody, string subject, List<Attachment> attachments, out string error)
        {
            error = string.Empty;

            try
            {
                SmtpSection smtpSec = (SmtpSection)ConfigurationManager.GetSection("system.net/mailSettings/smtp");
                bool enableSsl = true;
                Boolean.TryParse(ConfigurationManager.AppSettings["SmtpEnableSsl"], out enableSsl);
                SmtpClient smtp = new SmtpClient();
                if (smtp.DeliveryMethod == SmtpDeliveryMethod.Network)
                    smtp.EnableSsl = enableSsl;

                smtp.Host = smtpSec.Network.Host;
                smtp.Port = smtpSec.Network.Port;
                smtp.Credentials = new System.Net.NetworkCredential(smtpSec.Network.UserName, smtpSec.Network.Password);

                smtp.DeliveryMethod = SmtpDeliveryMethod.Network;

                MailAddress from = new MailAddress(fromEmail);

                MailMessage message = new MailMessage();
                foreach (var item in attachments)
                    message.Attachments.Add(item);

                message.From = new MailAddress(fromEmail);
                message.Body = mailBody;
                message.Subject = subject;
                message.IsBodyHtml = true;
                message.ReplyTo = new MailAddress(fromEmail);

                string email = toEmails;

                string[] toAddress = toEmails.Split(',');
                foreach (var item in toAddress)
                {
                    message.To.Add(new MailAddress(item));
                }

                smtp.Send(message);
            }
            catch (Exception ex)
            {
                error = ex.Message;
                return false;
            }
            return true;
        }
    }
}
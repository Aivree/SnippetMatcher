using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace IEHistoryBackup.Manager
{
    using System.IO;
    using System.Net;

    using Settings;
    using System.Net.Mail;

    public class EmailManager
    {

        private string GmailPassword
        {
            get
            {
                return _settingsManager.GetSetting("gmail.account.password");
            }
        }

        private string GmailUsername
        {
            get
            {
                return _settingsManager.GetSetting("gmail.account.username");
            }
        }

        private SettingsManager _settingsManager;

        public EmailManager(SettingsManager settingsManager)
        {
            _settingsManager = settingsManager;
        }

        public void SendMessage(string subject, string body)
        {
            var message = CreateMessage(subject, body);

            var stream = new MemoryStream();

            var writer = new StreamWriter(stream);

            try
            {
                writer.Write(body);

                message.Attachments.Add(new Attachment(stream, "Test.txt"));

                SendMessage(message);
            }
            finally
            {
                writer.Dispose();
                stream.Dispose();
            }
            


           

            
        }

        private MailMessage CreateMessage(string subject, string body)
        {
            var recipients = _settingsManager.GetSetting("recipients");
            var mailMessage = new MailMessage
            {
                Subject = subject,
                Body = body,
                From = new MailAddress(recipients),
                IsBodyHtml = true
            };




            mailMessage.To.Add(recipients);

            return mailMessage;
        }

        public void SendMessage(MailMessage message)
        {
            int port = 587;
            var smtpHost = "smtp.gmail.com";
            var smtpClient = new SmtpClient
            {
                Host = smtpHost,
                Port = port,
                EnableSsl = true,
                DeliveryMethod = SmtpDeliveryMethod.Network,
                UseDefaultCredentials = false,
                Credentials = new NetworkCredential(GmailUsername, GmailPassword)
            };
            smtpClient.Send(message);
        }
    }
}

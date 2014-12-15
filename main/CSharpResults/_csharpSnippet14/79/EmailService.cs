using System;
using System.Collections.Generic;
using System.Linq;
using System.Net.Mail;
using System.Text;
using System.Threading.Tasks;

namespace MSMQ.Email
{
    public class EmailService
    {
        /// <summary>
        /// sending email
        /// </summary>
        /// <param name="message">email message</param>
        public void SendMessage(EmailEntity message)
        {
            try
            {
                SMTPEntity smtp = message.SMTP;
                using (SmtpClient server = new SmtpClient(smtp.SMTP, smtp.Port))
                {
                    server.Credentials = new System.Net.NetworkCredential(smtp.UserName, smtp.Password);
                    server.DeliveryMethod = SmtpDeliveryMethod.Network;
                    server.EnableSsl = smtp.EnableSSL;

                    MailAddress mailFrom = new MailAddress(message.From, message.FriendlyName, Encoding.UTF8);

                    MailMessage mail = new MailMessage();

                    mail.From = mailFrom;
                    mail.Sender = mailFrom;

                    if (!message.IsBccAll)
                    {
                        foreach (var item in message.EmailTo)
                        {
                            mail.To.Add(item);
                        }

                        foreach (var item in message.EmailCc)
                        {
                            mail.CC.Add(item);
                        }

                        foreach (var item in message.EmailBcc)
                        {
                            mail.Bcc.Add(item);
                        }
                    }
                    else
                    {
                        foreach (var item in message.EmailTo)
                        {
                            mail.Bcc.Add(item);
                        }

                        foreach (var item in message.EmailCc)
                        {
                            mail.Bcc.Add(item);
                        }

                        foreach (var item in message.EmailBcc)
                        {
                            mail.Bcc.Add(item);
                        }
                    }

                    foreach (var item in message.AttachementPaths)
                    {
                        Attachment attach = new Attachment(item);
                        mail.Attachments.Add(attach);
                    }

                    mail.Subject = message.Subject;
                    mail.SubjectEncoding = Encoding.UTF8;
                    mail.Body = message.Message;
                    mail.BodyEncoding = Encoding.UTF8;
                    mail.IsBodyHtml = true;
                    mail.Priority = MailPriority.Normal;

                    server.Send(mail);
                }
            }
            catch (Exception e)
            {
                throw e;
            }
        }
    }
}

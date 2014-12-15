using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;
using System.Net.Mail;
using System.Net;
using System.IO;

namespace ServiceDotNet.Domain.Entities
{
    public class EmailUtil
    {
        #region Properties
        public string SenderFrom { get; set; }
        public string Subject
        {
            get;
            set;
        }

        public string Body
        {
            get;
            set;
        }

        public string TO
        {
            get;
            set;
        }

        public string CC
        {
            get;
            set;
        }

        public string BCC
        {
            get;
            set;
        }

        public string FromEmail
        {
            get;
            set;
        }

        public string FromName
        {
            get;
            set;
        }

        public string SMTPHost
        {
            get;
            set;
        }

        public string SMTPUsername
        {
            get;
            set;
        }

        public string SMTPPassword
        {
            get;
            set;
        }

        public int Port
        {
            get;
            set;
        }

        public bool AllowSSL
        {
            get;
            set;
        }

        public bool IsFileAttached
        {
            get;
            set;
        }

        public string FileName
        {
            get;
            set;
        }

        public Stream buffer
        {
            get;
            set;
        }
        #endregion

        #region Methods
        public bool Send()
        {
            bool flag = false;


            var mailMessage = new MailMessage
                {
                    From = new MailAddress(FromEmail, SenderFrom ?? "Service.Net")
                };

            if (TO != "")
            {
                AddToEmailCollection(ref mailMessage, TO, Enumeration.EmailAddressType.To);
            }

            if (CC != "")
            {
                AddToEmailCollection(ref mailMessage, CC, Enumeration.EmailAddressType.Cc);
            }

            if (BCC != "")
            {
                AddToEmailCollection(ref mailMessage, BCC, Enumeration.EmailAddressType.Bcc);
            }

            mailMessage.Subject = Subject;
            if (IsFileAttached) mailMessage.Attachments.Add(new Attachment(buffer, FileName));

            mailMessage.Body = Body;

            var mySmtpClient = new SmtpClient
            {
                Host = SMTPHost,
                Port = Port,
                EnableSsl = false,
                DeliveryMethod = SmtpDeliveryMethod.Network,
                UseDefaultCredentials = false,
                Credentials = new NetworkCredential(this.SMTPUsername, this.SMTPPassword)
            };

          
            mailMessage.Priority = MailPriority.High;
            mailMessage.IsBodyHtml = true;

            try
            {
                mySmtpClient.Send(mailMessage);
                flag = true;
            }
            catch (System.Net.Mail.SmtpException)
            {
                throw;
            }

            return flag;
        }

        
        protected void AddToEmailCollection(ref MailMessage mailMessage, string emailList, Enumeration.EmailAddressType emailAddressType)
        {
            try
            {
                String[] emails = emailList.Split(';');
                foreach (string email in emails)
                {
                    if (email.Trim() != "")    //To skip the empty string.
                    {
                        if (emailAddressType == Enumeration.EmailAddressType.To)
                        {
                            mailMessage.To.Add(new MailAddress(email.Trim()));
                        }
                        else if (emailAddressType == Enumeration.EmailAddressType.Cc)
                        {
                            mailMessage.CC.Add(new MailAddress(email.Trim()));
                        }
                        else if (emailAddressType == Enumeration.EmailAddressType.Bcc)
                        {
                            mailMessage.Bcc.Add(new MailAddress(email.Trim()));
                        }
                    }
                }
            }
            catch (System.Net.Mail.SmtpException)
            {
                throw;
            }
        }

        #endregion
    }
}
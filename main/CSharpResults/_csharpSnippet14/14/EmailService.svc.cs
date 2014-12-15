using System;
using System.Collections.Generic;
using System.Linq;
using System.Runtime.Serialization;
using System.ServiceModel;
using System.Text;
using System.Net.Mail;
using System.Net;
using System.Net.Mime;

namespace Business
{
    // NOTE: You can use the "Rename" command on the "Refactor" menu to change the class name "EmailService" in code, svc and config file together.
    public class EmailService : IEmailService
    {

        public bool SendMail(string From, string To, string Subject, string Body)
        {
            try
            {
                MailMessage msg = new MailMessage();

                msg.From = new MailAddress(From);
                msg.To.Add(To);
                msg.Subject = Subject;
                msg.Body = Body;

                using (SmtpClient client = new SmtpClient())
                {
                    client.EnableSsl = true;
                    client.UseDefaultCredentials = false;
                    client.Credentials = new NetworkCredential("electrosltd2014@gmail.com", "1PassworD");
                    client.Host = "smtp.gmail.com";
                    client.Port = 587;
                    client.DeliveryMethod = SmtpDeliveryMethod.Network;

                    client.Send(msg);
                    
                    return true;
                }
            }
            catch (Exception)
            {
                return false;
            }
        }

        public bool SendMailWithAttachment(string From, string To, string Subject, string Body, string AttachemntPath)
        {
            try
            {
                MailMessage msg = new MailMessage();

                msg.From = new MailAddress(From);
                msg.To.Add(To);
                msg.Subject = Subject;
                msg.Body = Body;
                
                Attachment data = new Attachment(AttachemntPath, MediaTypeNames.Application.Octet);
                msg.Attachments.Add(data);
                
                using (SmtpClient client = new SmtpClient())
                {
                    client.EnableSsl = true;
                    client.UseDefaultCredentials = false;
                    client.Credentials = new NetworkCredential("electrosltd2014@gmail.com", "1PassworD");
                    client.Host = "smtp.gmail.com";
                    client.Port = 587;
                    client.DeliveryMethod = SmtpDeliveryMethod.Network;

                    client.Send(msg);

                    return true;
                }
            
             }
            catch (Exception)
            {
                return false;
            }
        }
    }
}

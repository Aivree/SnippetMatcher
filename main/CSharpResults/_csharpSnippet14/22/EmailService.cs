
using System;
using System.Collections.Generic;
using System.Linq;
using System.Net;
using System.Net.Mail;
using System.Web;

namespace MVCformcollectionDemo.Models
{
    public class EmailService
    {


        bool SendEmail(string ToMail, string Subject, string Body,  KeyValuePair<System.IO.Stream, string> attachedStreamAndFileName, bool isHtml = false)
        {
            string SMTPUSER = "test@testhots.com";
            string SMTPSERVER = "testserver.com";
            string SMTPPORT = "26";
            //string SMTPSSL = "False";
            string SMTPPASSWORD = "password";

            SmtpClient smtpClient = new SmtpClient();
            MailMessage message = new MailMessage();
            try
            {
                MailAddress fromAddress = new MailAddress(SMTPUSER, "TestMailUserName");
                smtpClient.Host = SMTPSERVER;

                smtpClient.Port = Convert.ToInt32(SMTPPORT);
                //smtpClient.EnableSsl = Convert.ToBoolean(SMTPSSL);

                message.From = fromAddress;
                message.To.Add(ToMail);
                message.Subject = Subject;

                message.IsBodyHtml = isHtml;

                // Message body content
                message.Body = Body;

                if(attachedStreamAndFileName.Key != null)
                    message.Attachments.Add(new Attachment(attachedStreamAndFileName.Key, attachedStreamAndFileName.Value,"application/pdf"));

                // Send SMTP mail
                smtpClient.DeliveryMethod = SmtpDeliveryMethod.Network;
                smtpClient.Credentials = new NetworkCredential(SMTPUSER, SMTPPASSWORD);

                
                smtpClient.Send(message);

                return true;
            }
            catch (Exception)
            {
                return false;
            }
        }

        public bool SendViewMail(string ToMail, string body)
        {
            var kvp = new KeyValuePair<System.IO.Stream, string>(null, "");
            return SendEmail(ToMail, "MVC View Test", body, kvp, true);
        }

        public bool SendViewMail(string ToMail, string body, KeyValuePair<System.IO.Stream, string> attachments)
        {
            return SendEmail(ToMail, "MVC View Test", body, attachments, true);
        }

    }
}
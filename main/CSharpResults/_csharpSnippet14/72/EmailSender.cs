using System;
using System.Net;
using System.Net.Mail;

namespace Services.BLL
{
    public class EmailSender
    {
        private readonly MailMessage _m;

        public EmailSender(string fromEmailAddress)
        {
            _m = new MailMessage
                 {
                     Sender = _m.From = new MailAddress(
                         fromEmailAddress
                         ),
                     IsBodyHtml = true
                 };
        }

        public Attachment Attachments
        {
            set { _m.Attachments.Add(value); }
        }

        public bool IsHtml
        {
            set { _m.IsBodyHtml = value; }
        }

        public string To
        {
            set { _m.To.Add(value); }
        }

        public string From
        {
            set { _m.Sender = _m.From = new MailAddress(value); }
        }

        public string Subject
        {
            set { _m.Subject = value; }
        }

        public string Body
        {
            set { _m.Body = value; }
        }

        public void Send()
        {
            try
            {
                var sc = new SmtpClient
                         {
                             Host = 
                             DeliveryMethod = SmtpDeliveryMethod.Network,
                             EnableSsl = true,
                             Port = Convert.ToInt16(587),
                             Credentials = new NetworkCredential(),
                             Timeout = 20000
                         };


                sc.Send(_m);
            }
            catch (SmtpException e)
            {
                Console.WriteLine(e);
            }
        }
    }
}

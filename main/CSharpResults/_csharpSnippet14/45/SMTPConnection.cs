using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

using System.Net;
using System.Net.Mail;
using MyFaxInterface.Credentials;
using System.Collections;
using MyFaxInterface.Data;

namespace MyFaxInterface.Connections
{
    class SMTPConnection : ISMTPConnection
    {
        SMTPCredentials c1;

        public SMTPConnection(SMTPCredentials c)
        {
            c1 = c;
        }

        public void Test()
        {
            Console.WriteLine("Testing a SMTPConnection to " + c1.UserName + "with credentails: " + c1.ToString()); 
            try
            {
                NetworkCredential cred = new NetworkCredential(c1.UserName, c1.Password);
                MailMessage msg = new MailMessage();

                msg.To.Add(c1.UserName); // Add a new recipient to our msg.
                msg.From = new MailAddress(c1.UserName);
                msg.Subject = "TestMail";
                msg.Body = "If you can read this, the smtp server is good to go";

                SmtpClient client = new SmtpClient(c1.ServerIP, Int32.Parse(c1.Port));
                client.Credentials = cred; // Send our account login details to the client.
                client.EnableSsl = true;   // Read below.
                client.Send(msg);                 // Send our email.
            }
            catch (Exception ex)
            {
                throw ex;
            }


        }

        public void Send(String to,String from,String subject,String body,List<Attachement> attachements)
        {
            try
            {
                NetworkCredential cred = new NetworkCredential(c1.UserName, c1.Password);
                MailMessage msg = new MailMessage();

                msg.To.Add(to); // Add a new recipient to our msg.
                msg.From = new MailAddress(from);
                msg.Subject = subject;
                msg.Body = body;
              //  msg.Attachments = new AttachmentCollection();
                if (attachements != null)
                {
                    foreach (Attachement att in attachements)
                    {
                        System.Net.Mail.Attachment att2 = new System.Net.Mail.Attachment(att.FullPath); //make a new attachemnet
                        att2.Name = att.FileName; // add the filename
                        msg.Attachments.Add(att2);
                    }
                    

                }
                SmtpClient client = new SmtpClient(c1.ServerIP, Int32.Parse(c1.Port));
                client.Credentials = cred; // Send our account login details to the client.
                client.EnableSsl = true;   // Read below.
                client.Send(msg);                 // Send our email.
            }
            catch (Exception ex)
            {
                throw ex;
            }
    
        }




    }
}

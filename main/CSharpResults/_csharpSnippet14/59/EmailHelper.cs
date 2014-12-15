using BLL;
using DAL;
using System;
using System.Collections.Generic;
using System.Configuration;
using System.IO;
using System.Linq;
using System.Net;
using System.Net.Mail;
using System.Security;
using System.Security.Permissions;
using System.Text;

namespace LoanPricerWebBased.Helpers
{

    public static class EmailHelper
    {
        public static void GetMailSettings(SmtpClient client, MailMessage msg)
        {
            client.DeliveryMethod = SmtpDeliveryMethod.Network;

            client.EnableSsl = false;
            client.Host = ConfigurationManager.AppSettings["SmtpServer"];
            client.Port = Convert.ToInt32(ConfigurationManager.AppSettings["SmtpPort"]);

            // setup Smtp authentication
            NetworkCredential credentials = new NetworkCredential(ConfigurationManager.AppSettings["MailFrom"], ConfigurationManager.AppSettings["MailPassword"]);
            client.UseDefaultCredentials = false;
            client.Credentials = credentials;

            msg.From = new MailAddress(ConfigurationManager.AppSettings["MailFrom"]);
        }

        public static void SendEmail(string toEmail, string body, string subject)
        {
            SmtpClient client = new SmtpClient();

            MailMessage msg = new MailMessage();

            // Populate the SmtpClient and MailMessage with the setting in the Web.config

            GetMailSettings(client, msg);

            msg.To.Add(new MailAddress(toEmail));

            msg.Subject = subject;
            msg.IsBodyHtml = true;
            msg.Body = body;


            client.Send(msg);

        }

        public static void SendEmailToSelectedGroup(string body, string subject, string attachment = "")
        {
            SmtpClient client = new SmtpClient();

            MailMessage msg = new MailMessage();

            // Populate the SmtpClient and MailMessage with the setting in the Web.config

            GetMailSettings(client, msg);

            // get selected group emails
            EmailGroupsBL bl = new EmailGroupsBL();
            foreach (var item in bl.GetEmailReceiver())
            {
                msg.To.Add(new MailAddress(item.Name));
            }

            msg.Subject = subject;
            msg.IsBodyHtml = true;
            msg.Body = body;
            if (!string.IsNullOrEmpty(attachment))
            {
                msg.Attachments.Add(new Attachment(attachment));
            }
            EmailQueBL queBl = new EmailQueBL();

            tblEmailQue que = new tblEmailQue();

            que.Name = subject;
            que.SendTime = DateTime.Now;
            que.IsSent = false;

            try
            {
                if (!queBl.IsTodayEmailSent())
                {
                    client.Send(msg);
                    que.IsSent = true;
                }

            }
            catch (Exception ex)
            {
                //throw ex;
            }
            finally
            {
                queBl.AddEmailQue(que);
            }
        }

        public static string SendEmailToGroup(string body, string subject, string group, string attachment = "")
        {
            SmtpClient client = new SmtpClient();
            string strMessage = string.Empty;
            MailMessage msg = new MailMessage();

            // Populate the SmtpClient and MailMessage with the setting in the Web.config

            GetMailSettings(client, msg);

            // get selected group emails
            EmailGroupsBL bl = new EmailGroupsBL();
            foreach (var item in bl.GetEmailReceiver(group))
            {
                msg.To.Add(new MailAddress(item.Name));
            }

            msg.Subject = subject;
            msg.IsBodyHtml = true;
            msg.Body = body;
            if (!string.IsNullOrEmpty(attachment))
            {
                msg.Attachments.Add(new Attachment(attachment));
            }

            try
            {
                client.Send(msg);
                return strMessage;
            }
            catch (Exception ex)
            {
                //throw ex;
                return ex.Message;
            }
        }
    }

}

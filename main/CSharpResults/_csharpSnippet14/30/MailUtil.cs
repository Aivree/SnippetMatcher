using System;
using System.Collections.Generic;
using System.Text;
using System.Net.Mail;
using System.IO;
using System.Data;

namespace CSM.Common
{
    public static class MailUtil
    {
        public static void sendDocument(String email, String body, String subject, List<String> attachments)
        {
            MailMessage mail = new MailMessage();
            DataTable config = Data.Select("select * from config");
            SmtpClient SmtpServer = new SmtpClient(config.Rows[0]["SMTP"].ToString());
            mail.From = new MailAddress(config.Rows[0]["MAILFROM"].ToString());

            mail.To.Add(email);

            mail.Subject = subject;
            mail.Body = body;
          
            System.Net.Mail.Attachment attachment;
            foreach(String filename in attachments) 
            {
                attachment = new System.Net.Mail.Attachment(filename);
                mail.Attachments.Add(attachment);
            }
            SmtpServer.Port = int.Parse(config.Rows[0]["PORT"].ToString());
            SmtpServer.Credentials = new System.Net.NetworkCredential(config.Rows[0]["MAILLOGIN"].ToString(), config.Rows[0]["MAILPWD"].ToString());
            SmtpServer.EnableSsl = false;

            SmtpServer.Send(mail);
            mail.Dispose();                     
        }
    }
}

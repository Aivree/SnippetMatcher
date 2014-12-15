using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Net;
using System.Net.Mail;
using Helper.Excep;
namespace Helper.Email
{
    public static class EmailManager
    {
        public static void MailSend(string mailFrom, string maiFromlAccount, string mailFromPwd, string mailSmtpServer, IList<string> mailTo, IList<string> mailCC, IList<string> mailBCC, string mailTitle, string mailContent, IList<string> mailAttachments, System.Text.Encoding encoding, bool isBodyHtml)
        {
            MailMessage message = new MailMessage();
            if (mailFrom.Trim() == "")
            {
                throw new Exception("发送邮件不可以为空");
            }
            message.From = new MailAddress(mailFrom);
            if (mailTo.Count <= 0)
            {
                throw new Exception("接收邮件不可以为空");
            }
            foreach (string s in mailTo)
            {
                message.To.Add(new MailAddress(s));
            }
            if (mailCC.Count > 0)
            {
                foreach (string s in mailCC)
                {
                    message.CC.Add(new MailAddress(s));
                }
            }
            if (mailBCC.Count > 0)
            {
                foreach (string s in mailBCC)
                {
                    message.Bcc.Add(new MailAddress(s));
                }
            }
            message.Subject = mailTitle;
            message.Body = mailContent;
            message.BodyEncoding = encoding;   //邮件编码
            message.IsBodyHtml = isBodyHtml;      //内容格式是否是html
            message.Priority = MailPriority.High;  //设置发送的优先集
            //附件
            foreach (string att in mailAttachments)
            {
                message.Attachments.Add(new Attachment(att));
            }
            SmtpClient smtpClient = new SmtpClient();
            smtpClient.Host = mailSmtpServer;
            smtpClient.Credentials = new NetworkCredential(maiFromlAccount, mailFromPwd);
            smtpClient.Timeout = 1000;
            smtpClient.EnableSsl = false;        //不使用ssl连接
            smtpClient.Send(message);
        }

        public static void Send(EmailItem item, EmailServer server)
        {
            MailMessage msg = new MailMessage();
            msg.From = new MailAddress(item.FromAccount, item.FromShowName);

            if (string.IsNullOrEmpty(item.Title)) { throw new Helper.Excep.HelperException("邮件标题为空!"); }
            if (string.IsNullOrEmpty(item.Body)) { throw new Helper.Excep.HelperException("邮件内容为空!"); }

            foreach (string s in item.To)
            {
                msg.To.Add(new MailAddress(s));
            }

            if (item.Cc != null)
            {
                foreach (string s in item.Cc)
                {
                    msg.CC.Add(new MailAddress(s));
                }
            }

            if (item.Bcc != null)
            {
                foreach (string s in item.Bcc)
                {
                    msg.Bcc.Add(new MailAddress(s));
                }
            }

            msg.IsBodyHtml = item.IsHtml;
            if (string.IsNullOrEmpty(item.EncodingName))
                msg.BodyEncoding = System.Text.Encoding.Default;// Encoding.GetEncoding(item.EncodingName);
            else
                msg.BodyEncoding = Encoding.GetEncoding(item.EncodingName);
            msg.Subject = item.Title;
            if (string.IsNullOrEmpty(item.EncodingName))
                msg.SubjectEncoding = System.Text.Encoding.Default;
            else
                msg.SubjectEncoding = Encoding.GetEncoding(item.EncodingName);
            msg.Body = item.Body;

            if (item.IsPretend)
            {
                msg.Headers.Add("X-Priority", "3");
                msg.Headers.Add("X-MSMail-Priority", "Normal");
                msg.Headers.Add("X-Mailer", "Microsoft Outlook Express 6.00.2900.2869");
                msg.Headers.Add("X-MimeOLE", "Produced By Microsoft MimeOLE V6.00.2900.2869");
            }

            try
            {
                if (server.Port == 0) server.Port = 25;
                SmtpClient client = new SmtpClient(server.Server, server.Port);
                client.UseDefaultCredentials = true;
                client.Credentials = new NetworkCredential(server.UserName, server.Password);
                client.DeliveryMethod = SmtpDeliveryMethod.Network;
                // client.EnableSsl = true;              

                client.Send(msg);
            }
            catch (Exception)
            {
                throw;
            }


        }
    }
}

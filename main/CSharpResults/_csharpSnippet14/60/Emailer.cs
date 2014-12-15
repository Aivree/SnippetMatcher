using System;
using System.Net.Mail;
using System.Net;
using System.ComponentModel;
using System.Web;
using System.Collections;
using System.Collections.Generic;

namespace tk.tingyuxuan.utils
{
    /// <summary>
    /*用法如下*/
           /* Emailer emailer = new Emailer();
            emailer.UserName = "paifun@qq.com";
            emailer.Password = "*****";
            emailer.Host = "smtp.qq.com";
            emailer.From = "paifun@qq.com";
            emailer.FromName = "拍房网";
            emailer.To = "fqmhw@qq.com";
            emailer.ToName = "封丘门户网";
            emailer.Subject = string.Format("{0}({1},{2})委托：",name,phone,mobile);
            emailer.Body = string.Format(@"<html><body>
            <p>{0}<p>
            <p>此邮件为系统邮件，请勿回复！！</p>
            </body>
            </html>",content);
            emailer.SendEmail(null);*/
             /*发送邮件*/

    /// </summary>
    public class Emailer
    {
        private string from;
        private string fromName;

        private string to;
        private string toName;
        private string subject;
        private string body;

        private string userName;
        private string password;

        private string host;
        private int port;

        private bool enableSSL;

        public delegate void CallBack();

        public Emailer()
        {
            port = 25;
            enableSSL = false;
        }



        /// <summary>
        /// 信件发自哪里
        /// </summary>
        public string From
        {
            get { return from; }
            set { from = value; }
        }
        /// <summary>
        /// 显示寄信人的名字，默认是邮件名
        /// </summary>
        public string FromName
        {
            get { return fromName; }
            set { fromName = value; }
        }
        /// <summary>
        /// 发送地址
        /// </summary>
        public string To
        {
            get { return to; }
            set { to = value; }

        }
        /// <summary>
        /// 显示收信人的名字，默认是邮件名
        /// </summary>
        public string ToName
        {
            get { return toName; }
            set { toName = value; }
        }
        /// <summary>
        /// 邮件主题
        /// </summary>

        public string Subject
        {
            get { return subject; }
            set { subject = value; }
        }
        /// <summary>
        /// 邮件正文
        /// </summary>
        public string Body
        {
            get { return body; }
            set { body = value; }
        }
        /// <summary>
        /// smtp 服务器地址
        /// </summary>
        public string Host
        {
            get { return host; }
            set { host = value; }
        }
        /// <summary>
        /// 设置SMTP服务器的端口号,默认是25,google（25，456，587） 但456 不好用。不过在php 下好用。不知道为什么。
        /// </summary>
        public int Port
        {
            get { return port; }
            set { port = value; }
        }

        /// <summary>
        /// 邮箱验证用的用户名。
        /// 如果是417471191@qq.com 那么用户名是 417471191（一定要去掉域名)
        /// </summary>
        public string UserName
        {
            get { return userName; }
            set { userName = value; }

        }
        /// <summary>
        /// 验证用到的密码
        /// </summary>
        public string Password
        {
            get { return password; }
            set { password = value; }
        }

        public bool EnableSSL
        {
            get { return enableSSL; }
            set { enableSSL = value; }
        }
        /// <summary>
        /// 发送邮件
        /// </summary>
        /// <param name="attachments">附件集合</param>
        public void SendEmail(List<Attachment> attachments )
        {

            MailAddress fromAddress = new MailAddress(From, FromName);
            MailAddress toAddress = new MailAddress(To, ToName);
            SmtpClient smtp = new SmtpClient(Host, Port);
            smtp.DeliveryMethod = SmtpDeliveryMethod.Network;

            NetworkCredential credential = new NetworkCredential(UserName, Password);
            smtp.Credentials = credential;//验证

            MailMessage message = new MailMessage(fromAddress, toAddress);

            message.To.Add(toAddress);
            message.Subject = Subject;
            message.SubjectEncoding = System.Text.Encoding.UTF8;
            message.Body = Body;
            message.BodyEncoding = System.Text.Encoding.UTF8;
            message.IsBodyHtml = true;
            message.Priority = MailPriority.High;

            if (attachments != null)
            {
                foreach (Attachment a in attachments)
                {
                    message.Attachments.Add(a);
                }
            }


            if (EnableSSL)
            {
                smtp.EnableSsl = EnableSSL;
            }


            try
            {
                smtp.Send(message);


            }
            catch (Exception ex)
            {
               // Logger.Log(ex.GetBaseException().Message, HttpContext.Current.Server.MapPath("~/log.txt"));
            }
            finally
            {
                message.Dispose();

            }



        }


    }
}

//--------------------------------------------------
// 网商宝商城免费开源版 V1.0.110909
// 本程序仅用于学习和研究，不得作为商业用途。
// 如需进行商城运营，请与我公司联系购买商业版本。
//
// 东莞市捷联科技有限公司
// 网址：www.128.com.cn
// QQ：1316108492
// 电话：400-678-1128
//--------------------------------------------------

using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Net.Mail;

namespace com.eshop.www.Tools
{
    public class EmailHelper
    {
        private string emailFrom = "shenglida@sld.com.cn";

        public string EmailFrom
        {
            get { return emailFrom; }
            set { emailFrom = value; }
        }
        private string emailTo;

        public string EmailTo
        {
            get { return emailTo; }
            set { emailTo = value; }
        }
        private string cc = "";

        public string CC
        {
            get { return cc; }
            set { cc = value; }
        }
        private string bcc = "";

        public string BCC
        {
            get { return bcc; }
            set { bcc = value; }
        }
        private string userName = "shenglida@sld.com.cn";

        public string UserName
        {
            get { return userName; }
            set { userName = value; }
        }
        private string password = "123456";

        public string Password
        {
            get { return password; }
            set { password = value; }
        }
        private string smtpServer = "mail.sld.com.cn";

        public string SmtpServer
        {
            get { return smtpServer; }
            set { smtpServer = value; }
        }
        private string smtpPort = "25";

        public string SmtpPort
        {
            get { return smtpPort; }
            set { smtpPort = value; }
        }
        private bool enableSSL = false;

        public bool EnableSSL
        {
            get { return enableSSL; }
            set { enableSSL = value; }
        }

       
        /// <summary>
        /// 发送邮件(不带附件)
        /// </summary>
        public bool SendEmail(string subject, string body)
        {
            try
            {
                MailMessage mailMessage = new MailMessage(this.EmailFrom, this.EmailTo);
                mailMessage.Subject = subject;
                mailMessage.Body = body;
                mailMessage.IsBodyHtml = true;
                if (this.CC != string.Empty)
                {
                    MailAddress CCcopy = new MailAddress(CC);
                    mailMessage.CC.Add(CCcopy);
                }

                if (this.BCC != string.Empty)
                {
                    MailAddress BCCcopy = new MailAddress(BCC);
                    mailMessage.Bcc.Add(BCCcopy);
                }

                SmtpClient b = new SmtpClient(this.SmtpServer, int.Parse(this.SmtpPort));

                b.EnableSsl = this.EnableSSL;

                b.Credentials = new System.Net.NetworkCredential(this.UserName, this.Password);
                b.Send(mailMessage);
                return true;
            }
            catch
            {
                return false;
            }
        }
        /// <summary>
        /// 发送邮件(带附件)
        /// </summary>
        
        public bool SendEmail(string subject, string body, List<string> sAttrachment)
        {
            try
            {
                MailMessage mailMessage = new MailMessage(EmailFrom, EmailTo);
                mailMessage.Subject = subject;
                mailMessage.Body = body;
                mailMessage.IsBodyHtml = true;
                if (CC != string.Empty)
                {
                    MailAddress CCcopy = new MailAddress(CC);
                    mailMessage.CC.Add(CCcopy);
                }

                if (BCC != string.Empty)
                {
                    MailAddress BCCcopy = new MailAddress(BCC);
                    mailMessage.Bcc.Add(BCCcopy);
                }

                if (sAttrachment.Count > 0)
                {
                    foreach (string sSubstr in sAttrachment)
                    {
                        System.Net.Mail.Attachment MyAttachment = new Attachment(sSubstr);
                        mailMessage.Attachments.Add(MyAttachment);
                    }
                }


                SmtpClient b = new SmtpClient(SmtpServer, int.Parse(SmtpPort));
                b.EnableSsl = EnableSSL;
                b.Credentials = new System.Net.NetworkCredential(UserName, Password);
                b.Send(mailMessage);
                return true;
            }
            catch
            {
                return false;
            }

        }
    }
}

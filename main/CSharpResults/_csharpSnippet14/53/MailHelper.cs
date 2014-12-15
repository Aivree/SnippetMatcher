using System;
using System.Collections.Generic;
using System.Net;
using System.Net.Mail;
using System.Text;

namespace FrankMail
{
    public class MailHelper
    {
        public MailHelper()
        {
        }

        public static string Send(string from, string password, string body, string title, string[] arrMailTo,
                                  string attachment, string smtpHost, int smtpPort)
        {
            //声明一个Mail对象
            MailMessage myMail = new MailMessage();
            //发件人地址
            //如是自己，在此输入自己的邮箱
            myMail.From = new MailAddress(from);
            //收件人地址
            //MailAddress ma = new MailAddress(singleEmailAddr);
            if (arrMailTo.Length > 0)
            {
                for (int i = 0; i < arrMailTo.Length; i++)
                {
                    string singleEmailAddr = arrMailTo[i];
                    myMail.To.Add(singleEmailAddr); 
                }
            }
            //邮件主题
            myMail.Subject = title;
            //邮件标题编码
            myMail.SubjectEncoding = System.Text.Encoding.UTF8;
            //发送邮件的内容
            myMail.Body = body;
            //邮件内容编码
            myMail.BodyEncoding = System.Text.Encoding.UTF8;
            if (!string.IsNullOrEmpty(attachment))
            {
                //添加附件
                Attachment myfiles = new Attachment(attachment);
                myMail.Attachments.Add(myfiles);
            }
            //抄送到其他邮箱
            //mymail.CC.Add(new MailAddress(null));
            //是否是HTML邮件
            myMail.IsBodyHtml = true;
            //邮件优先级
            myMail.Priority = MailPriority.Normal;
            //创建一个邮件服务器类
            SmtpClient myclient = new SmtpClient();

            //string host = from.Substring(from.IndexOf('@') + 1);
            myclient.Host = smtpHost;
            //SMTP服务端口
            myclient.Port = smtpPort;

            if (myclient.Port != 25)
            {
                //如果不是25端口的话，就是用ssl加密连接
                myclient.EnableSsl = true;
            }

            //验证登录
            myclient.Credentials = new NetworkCredential(from, password);//"@"输入有效的邮件名, "*"输入有效的密码
            try
            {
                myclient.Send(myMail);
            }
            catch (SmtpException ex)
            {
                return ex.Message;
            }
            catch (Exception ex)
            {
                return ex.Message;
            }

            return "OK";
        }
    }
}

using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Net;
using System.Net.Mail;
namespace DTILibraryAlert.Util
{
    struct EmailSetting
    {
        String SmtpServer;
        String PortNumber;
        Boolean EnableSSL;
        String SendEmail;
        String Password;
    }
    class LibraryMail
    {

        public static void sendMail(EmailSetting EmailSetting,String Subject, String Detail)
        {
            string smtpAddress = "smtp.mail.go.th";
            int portNumber = 25;
            bool enableSSL = true;
            string emailFrom = "somkheart.k@dti.or.th";
            string password = "8ooblypfu";
            string emailTo = "engineer_robot@hotmail.com";
            string subject = "แจ้งรายการค้างส่งหนังสือ";
            string body = "สวัสดีค่ะ";
            using (MailMessage mail = new MailMessage())
            {
                mail.From = new MailAddress(emailFrom);
                mail.To.Add(emailTo);
                mail.To.Add("somkheart@gmail.com");
               // mail.To.Add("somkheart.k@dti.or.th");
                mail.Subject = subject;
                mail.Body = body;
                mail.IsBodyHtml = true;
                //mail.Attachments.Add(new Attachment("C:\\SomeFile.txt"));
                //mail.Attachments.Add(new Attachment("C:\\SomeZip.zip"));
                using (SmtpClient smtp = new SmtpClient(smtpAddress, portNumber))
                {
                    smtp.Credentials = new NetworkCredential(emailFrom, password);
                    smtp.EnableSsl = enableSSL;
                    smtp.Send(mail);
                }
            }
        }
    }

}

using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

using System.Net.Mail;
using System.Data.OleDb;
using System.Configuration;
using System.Windows.Forms;


namespace Core_Functionality_Windows_Forms_Application
{
 public class MailSending_Class
    {
        public void send_email(string EmailTextbox)
        {
            MailMessage mail = new MailMessage();
            SmtpClient SmtpServer = new SmtpClient("smtp.gmail.com");

            string email = "";
            email = EmailTextbox;

            mail.From = new MailAddress("sandwichtavisca@gmail.com");//referal
            mail.To.Add(email);//hr
            mail.Subject = "New Entry Added to Referral";
            mail.Body = "";
            // System.Net.Mail.Attachment attachment;

            // MessageBox.Show("attachment mailed !! ");
            SmtpServer.Port = 587;
            SmtpServer.Credentials = new System.Net.NetworkCredential("sandwichtavisca", "sandwich2014");//referral emial id nd password
            SmtpServer.EnableSsl = true;

            SmtpServer.Send(mail);
            MessageBox.Show("mail Send");

        }

    }
}

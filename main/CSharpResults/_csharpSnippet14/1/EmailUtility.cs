using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.IO;
using System.Net.Mail;
using System.Web;


namespace SpotCollege.Common
{
  public class EmailUtility
    {
      public static void SendEmail(String sender, String receiver, String cc, String bcc, String subject, String body, Boolean isBodyHtml, HttpPostedFileBase fileToAttach)
      {
          /* Create a new blank MailMessage */
          MailMessage mailMessage = new MailMessage();

          mailMessage.From = new MailAddress(sender);
          mailMessage.To.Add(new MailAddress(receiver));
          if (cc.Trim() != String.Empty)
          {
              mailMessage.CC.Add(new MailAddress(cc));
          }
          if (bcc.Trim() != String.Empty)
          {
              mailMessage.Bcc.Add(new MailAddress(bcc));
          }
          mailMessage.Subject = subject;
          mailMessage.Body = body;


          mailMessage.IsBodyHtml = isBodyHtml;

          if (fileToAttach != null)
          {
              Attachment attachmentFile = new Attachment(fileToAttach.InputStream, fileToAttach.FileName);
              mailMessage.Attachments.Add(attachmentFile);
          }
          SmtpClient smtpClient = new SmtpClient();
          smtpClient.Host = ConfigHelper.SMTP_Server;
          smtpClient.Credentials = new System.Net.NetworkCredential(ConfigHelper.SMTP_Username, ConfigHelper.SMTP_Password);
          smtpClient.Port = ConfigHelper.SMTP_Port;
          smtpClient.EnableSsl = true;
          smtpClient.Send(mailMessage);
      }
    }
}


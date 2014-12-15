using System;
using System.Collections.Generic;
using System.Linq;
using System.Net.Mail;
using System.Text;
using System.Windows.Forms;

namespace ProyectoPatrones
{
    public class YahooSmtp : SMTPServer
    {
        public YahooSmtp(POP3Server server)
        {
            this.server = server;
            this.client = new SmtpClient("smtp.mail.yahoo.com");
        }
        public override void enviar(string To, string Subject, string Body, string[] Attach)
        {
            try
            {
                this.client.Port = 25;
                this.client.Credentials = new System.Net.NetworkCredential(this.server.User, this.server.Password);
                this.client.EnableSsl = true;
                MailMessage mail = new MailMessage();
                mail.From = new MailAddress(this.server.User);
                mail.To.Add(To);
                mail.Subject = Subject;
                mail.Body = Body;
                for (int i = 0; i < Attach.Length; i++)
                {
                    if (Attach[i] != null)
                    {
                        Attachment attachment = new Attachment(Attach[i]);
                        mail.Attachments.Add(attachment);
                    }
                }
                this.client.Send(mail);

            }
            catch (Exception ex)
            {
                MessageBox.Show(ex.ToString());
            }   
        }
    }
}

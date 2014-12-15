using System;
using System.Data;
using System.Configuration;
using System.Linq;
using System.Web;
using System.Web.Security;
using System.Web.UI;
using System.Web.UI.HtmlControls;
using System.Web.UI.WebControls;
using System.Web.UI.WebControls.WebParts;
using System.Xml.Linq;
using System.Text.RegularExpressions;
using System.Net;
using System.Net.Mail;
using System.IO;


/// <summary>
/// Summary description for BasePage
/// </summary>
public class BasePage:Page
{
	public BasePage()
	{
		//
		// TODO: Add constructor logic here
		//
	}
	public static void OpenWindow(Page page, string url)
	{
		page.ClientScript.RegisterStartupScript(page.GetType(), "newWindow", String.Format("<script>window.open('{0}');</script>", url));
	}
	public static bool SendMailMessage(string to,  string subject, string body, bool isBodyHtml, string password, byte[] bytes, string name)
	{
		//public static bool SendMailMessage(string to, string bcc, string subject, string body, bool isBodyHtml, string password, byte[] bytes, string name)
		try
		{
			MailMessage mMailMessage = new MailMessage();

			mMailMessage.From = new MailAddress(ConfigurationManager.AppSettings["adminEmail"].ToString());
			mMailMessage.To.Add(new MailAddress(to));
			//mMailMessage.CC.Add(new MailAddress(ConfigurationManager.AppSettings["adminEmail"].ToString()));


			//Attachment attachFile = new Attachment("D:\\DRYSOFT LICENCE\\WebSite2\\big3.jpg");
			//mMailMessage.Attachments.Add(attachFile);


			if (bytes != null)
			{
				mMailMessage.Attachments.Add(new Attachment(new MemoryStream(bytes), name));
			}

			//if (!string.IsNullOrEmpty(bcc))
			//{
			//    mMailMessage.Bcc.Add(new MailAddress(bcc));
			//}

			mMailMessage.Subject = subject;
			mMailMessage.Body = body;
		   
			mMailMessage.IsBodyHtml = isBodyHtml;
			mMailMessage.Priority = MailPriority.Normal;

			SmtpClient smtp = new SmtpClient();
			smtp.Host = "smtp.gmail.com";
			smtp.EnableSsl = true;
			smtp.UseDefaultCredentials = false;
			smtp.Port = 587;
			smtp.Credentials = new System.Net.NetworkCredential("deecoup.2011@gmail.com", "deecoup@12345");

			//SmtpClient smtp = new SmtpClient();
			//smtp.Host = "gator1344.hostgator.com";
			//smtp.EnableSsl = true;
			//smtp.UseDefaultCredentials = false;
			//smtp.Port = 26;
			//smtp.Credentials = new System.Net.NetworkCredential("info@ppischool.com", password);


			smtp.Send(mMailMessage);
			return true;
		}
		catch (Exception)
		{
			return false;
		}
	}
	public static bool SendMail(string to, string subject, string body, bool isBodyHtml, string name, string hostName, string BranchEmail, string BranchPassword,bool SSL)
	{
        bool IsMailSend = false;
        try
        {
            // Setup mail message
            MailMessage msg = new MailMessage();
            msg.Subject = subject;
            msg.Body = body;
            msg.From = new MailAddress(BranchEmail);
            msg.To.Add(to);
            msg.IsBodyHtml = true; // Can be true or false

            // Setup SMTP client and send message
            using (SmtpClient smtpClient = new SmtpClient())
            {
                smtpClient.Host = hostName;
                smtpClient.EnableSsl = SSL;
                smtpClient.Port = 587; // Gmail uses port 587
                smtpClient.UseDefaultCredentials = false; // Must be set BEFORE Credentials below...
                smtpClient.Credentials = new NetworkCredential(BranchEmail, BranchPassword);
                smtpClient.DeliveryMethod = SmtpDeliveryMethod.Network;
                smtpClient.Send(msg);
                IsMailSend = true;
            }
        }
        catch (SmtpFailedRecipientsException sfrEx)
        {
            // TODO: Handle exception
            // When email could not be delivered to all receipients.
            IsMailSend = false;
        }
        catch (SmtpException sEx)
        {
            // TODO: Handle exception
            // When SMTP Client cannot complete Send operation.
            IsMailSend = false;
        }
        catch (Exception ex)
        {
            // TODO: Handle exception
            // Any exception that may occur during the send process.
            IsMailSend = false;
        }
        return IsMailSend;
	}
    public static string SendMail1(string to, string subject, string body, bool isBodyHtml, string name, string hostName, string BranchEmail, string BranchPassword, bool SSL)
    {       
        string res = string.Empty;
        try
        {
            // Setup mail message
            MailMessage msg = new MailMessage();
            msg.Subject = subject;
            msg.Body = body;
            msg.From = new MailAddress(BranchEmail);
            msg.To.Add(to);
            msg.IsBodyHtml = true; // Can be true or false

            // Setup SMTP client and send message
            using (SmtpClient smtpClient = new SmtpClient())
            {
                smtpClient.Host = hostName;
                smtpClient.EnableSsl = SSL;
                smtpClient.Port = 587; // Gmail uses port 587
                smtpClient.UseDefaultCredentials = false; // Must be set BEFORE Credentials below...
                smtpClient.Credentials = new NetworkCredential(BranchEmail, BranchPassword);
                smtpClient.DeliveryMethod = SmtpDeliveryMethod.Network;
                smtpClient.Send(msg);
                res = "Email send sucessfully.";
            }
        }
        catch (SmtpFailedRecipientsException sfrEx)
        {
            // TODO: Handle exception
            // When email could not be delivered to all receipients.
            res = sfrEx.ToString();
        }
        catch (SmtpException sEx)
        {
            // TODO: Handle exception
            // When SMTP Client cannot complete Send operation.
            res = sEx.ToString();
        }
        catch (Exception ex)
        {
            // TODO: Handle exception
            // Any exception that may occur during the send process.
            res = ex.ToString();
        }
        return res;
    }

	public static bool SendMailWithAttachment(string to, string subject, string body, bool isBodyHtml, string name, string hostName, string BranchEmail, string BranchPassword, bool SSL, byte[] renderedBytes)
	{
		MailMessage m = new MailMessage();
		SmtpClient sc = new SmtpClient();
		try
		{
			m.From = new MailAddress(BranchEmail);
			m.To.Add(new MailAddress(to));
			m.Subject = subject;
			m.IsBodyHtml = true;
			m.Body = body;
			MemoryStream memoryStream = new MemoryStream(renderedBytes);
			memoryStream.Seek(0, SeekOrigin.Begin);       
			Attachment attachment = new Attachment(memoryStream, "BookingReport.pdf");
            
			m.Attachments.Add(attachment);
			sc.Host = hostName;
			sc.Port = 587;
			sc.Credentials = new System.Net.NetworkCredential(BranchEmail, BranchPassword);
			sc.EnableSsl = SSL;
			sc.Send(m);
			return true;
		}
		catch (Exception)
		{
			return false;
		}


	}


    public static bool SendMailWithAttachmentfile(string to, string subject, string body, bool isBodyHtml, string name, string hostName, string BranchEmail, string BranchPassword, bool SSL, string emailfile)
    {
        //MailMessage m = new MailMessage();
        //SmtpClient sc = new SmtpClient();
        //try
        //{
        //    m.From = new MailAddress(BranchEmail);
        //    m.To.Add(new MailAddress(to));
        //    m.Subject = subject;
        //    m.IsBodyHtml = true;
        //    m.Body = body;
        //    //MemoryStream memoryStream = new MemoryStream(renderedBytes);
        //    //memoryStream.Seek(0, SeekOrigin.Begin);
        //    Attachment attachment = new Attachment(emailfile);
        //    m.Attachments.Add(attachment);
        //    sc.Host = hostName;
        //    sc.Port = 587;
        //    sc.Credentials = new System.Net.NetworkCredential(BranchEmail, BranchPassword);
           
        //    sc.EnableSsl = SSL;
        //    sc.Send(m);
        //    return true;
        //}
      

        //catch (Exception ex)
        //{
        //    return false;
        //}

        bool IsMailSend = false;
        try
        {
            // Setup mail message
            MailMessage msg = new MailMessage();
            msg.Subject = subject;
            msg.Body = body;
            msg.From = new MailAddress(BranchEmail);
            msg.To.Add(to);
            msg.IsBodyHtml = true; // Can be true or false
            Attachment attachment = new Attachment(emailfile);
            msg.Attachments.Add(attachment);
            // Setup SMTP client and send message
            using (SmtpClient smtpClient = new SmtpClient())
            {
                smtpClient.Host = hostName;
                smtpClient.EnableSsl = SSL;
                smtpClient.Port = 587; // Gmail uses port 587
                smtpClient.UseDefaultCredentials = false; // Must be set BEFORE Credentials below...
                smtpClient.Credentials = new NetworkCredential(BranchEmail, BranchPassword);
                smtpClient.DeliveryMethod = SmtpDeliveryMethod.Network;
                smtpClient.Send(msg);
                IsMailSend = true;
            }
        }
        catch (SmtpFailedRecipientsException sfrEx)
        {
            // TODO: Handle exception
            // When email could not be delivered to all receipients.
            IsMailSend = false;
        }
        catch (SmtpException sEx)
        {
            // TODO: Handle exception
            // When SMTP Client cannot complete Send operation.
            IsMailSend = false;
        }
        catch (Exception ex)
        {
            // TODO: Handle exception
            // Any exception that may occur during the send process.
            IsMailSend = false;
        }
        return IsMailSend;
    }


}

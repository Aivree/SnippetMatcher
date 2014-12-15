using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;
using System.Web.UI;
using System.Web.UI.WebControls;
using SmsClient;
using System.Web.Mail;

public partial class Email : System.Web.UI.Page
{
    protected void Page_Load(object sender, EventArgs e)
    {

    }
    protected void btnsend_Click(object sender, EventArgs e)
    {
        System.Net.Mail.SmtpClient smtpClient = new System.Net.Mail.SmtpClient();
        smtpClient.UseDefaultCredentials = false;
        smtpClient.Credentials = new System.Net.NetworkCredential("supertechnetium@gmail.com", "Avtar2012");
        //(email,password)
        smtpClient.Port = 587;
        // or 465;
        smtpClient.Host = "smtp.gmail.com";
        smtpClient.EnableSsl = true;
        System.Net.Mail.MailMessage mailMessage = new System.Net.Mail.MailMessage();
        mailMessage.IsBodyHtml = true;
        mailMessage.Body = txtbody.Text;
        //mailMessage.Body += "Welcome " + txtFName.Text + " " + txtLName.Text;
        //Random rand = new Random();
        //int code = rand.Next(100000, 999999);
        
        mailMessage.Subject = txtsub.Text;
        mailMessage.To.Add(Txtto.Text);
        if (TextBox1.Text != "")
        { mailMessage.Bcc.Add(TextBox1.Text); }
        if (FileUpload1.HasFile == true)
        {
            HttpPostedFile attFile = FileUpload1.PostedFile;
            int g = attFile.ContentLength;
            if (g > 0)
            {
                string fpath = "Attachment/" + FileUpload1.FileName;
               FileUpload1.PostedFile.SaveAs(Server.MapPath(fpath)) ;
                //Attachmentitem attach = new MailAttachment(Server.MapPath(fpath));
               System.Net.Mail.Attachment attach=new System.Net.Mail.Attachment(Server.MapPath(fpath));
                mailMessage.Attachments.Add(attach) ;
            }
        }//mailMessage.CC.Add("7567581222@160by2.com");
        mailMessage.From = new System.Net.Mail.MailAddress("supertechnetium@gmail.com", "Technetium", System.Text.Encoding.UTF8);
        //(email,name appears in mailbox,coding)
        try
        {
            smtpClient.Send(mailMessage);
            //Label1.Text = "Sent";
            
            SendSms sms = new SendSms();
            string status = sms.send("7567581222", "AVTAR1986","SENT","7567581222");
           
        }
        catch (Exception ex)
        {
            ScriptManager.RegisterClientScriptBlock(Page, this.GetType(), "Alert", "alert('" + ex.Message + "');", true);
        }
    }
}
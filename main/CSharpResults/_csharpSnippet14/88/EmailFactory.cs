using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;
using System.Text;
using System.Net.Mail;
using System.Net.Mime;

namespace turismo.Helpers
{
    public class EmailFactory
    {
        private MailMessage correo;
        private SmtpClient cliente;

        public EmailFactory()
        {
            correo = new MailMessage();
            cliente = new SmtpClient();
            correo.From = new MailAddress("nvulloa.uvm@gmail.com", "Nicolas Vera");
            //emailmensaje.CC.Add(new MailAddress("CC@yahoo.com", "Display name CC"));
        }
        public void enviarCorreo(String asunto, String mensaje)
        {

            //emailmensaje.CC.Add(new MailAddress("CC@yahoo.com", "Display name CC"));
            correo.Subject = asunto;
            correo.Body = mensaje;
            // enviar un archivo adjunto 
            //FileStream fs = new FileStream("E:\\TestFolder\\test.pdf", FileMode.Open, FileAccess.Read); 
            //Attachment a = new Attachment(fs, "test.pdf", MediaTypeNames.Application.Octet);
            //emailmensaje.Attachments.Add(a); 
            //m.Attachments.Add(new Attachment("E:\\windows\\win32.ini")); 

            cliente.Host = "smtp.gmail.com";
            cliente.Port = 587;
            cliente.Credentials = new System.Net.NetworkCredential("uvmturismo@gmail.com", "uvmturismo123");
            cliente.EnableSsl = true;
            cliente.Send(correo);

        }
        public void agregarDestinatario(String email)
        {
            correo.To.Add(new MailAddress(email));


        }
       
    }

}
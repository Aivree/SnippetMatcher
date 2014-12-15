using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;
using System.Web.Mvc;
using Prestocho.Models;
using Prestocho.Components;
using System.Net.Mail;

namespace Prestocho.Controllers
{
    public class TrabajoUsuariosController : BaseController
    {
        private PrestochoEntities db = new PrestochoEntities();
        //
        // GET: /TrabajoHoras/

        //
        // GET: /Catalogo/
        [RequiredAuthentication(Check = true)]
        public ViewResult Index()
        {
            var usuarios = db.Usuarios.ToList();
            var trabajoUsuarios = db.TrabajoUsuarios.ToList();
            var idUsu = (from u in usuarios where u.nombre == User.Identity.Name select u.usuarioId).FirstOrDefault();
            
            ViewBag.id = idUsu;

            var trabajo = (from u in trabajoUsuarios select u).ToList();
            foreach (var c in trabajoUsuarios) {
                if (c.pedidoFecha <= DateTime.Today) {
                    c.usuarioPresta = 0;
                }
            }
            db.SaveChanges();
            return View(trabajo.ToList());

        }

        //
        // POST: /Catalogo/
        [HttpPost]
        [RequiredAuthentication(Check = true)]
        public ActionResult Index(string nombre)
        {
          
            return View();
        }

        //
        // GET: /Catalogo/Create
        [RequiredAuthentication(Check = true)]
        public ActionResult Create()
        {
            
            return View();
        }

        //
        // POST: /Catalogo/Create
        [HttpPost]
        [RequiredAuthentication(Check = true)]
        public ActionResult Create(TrabajoUsuarios trabajo)
        {
            if (ModelState.IsValid)
            {
                trabajo.usuarioId = (from u in db.Usuarios where u.nombre == User.Identity.Name select u.usuarioId).FirstOrDefault();
                trabajo.pedidoFecha = new DateTime(3030,1,1);
                Notificaciones notif = new Notificaciones { nombreNot = trabajo.trabajoDes };
                db.TrabajoUsuarios.Add(trabajo);
                db.Notificaciones.Add(notif);
                db.SaveChanges();

                return RedirectToAction("Index");
            }
            return View(trabajo);
        }

        public ViewResult DetailsTrabajo(int id)
        {
            var trabajo = db.TrabajoUsuarios.Find(id);
            return View(trabajo);
        }

        public ActionResult Send(int id) {
            TrabajoUsuarios usuario = db.TrabajoUsuarios.Find(id);
            
            return View(usuario);
        }

        [HttpPost]
        public ActionResult Send(int usuarioId, string mensaje, int trabajoId)
        {
            var usuarios = db.Usuarios.ToList();
            var emailUsuario = (from u in usuarios where u.usuarioId == usuarioId select u.email).FirstOrDefault();
            var asunto = "Solicitud trabajo horas";

            Usuario usuario = db.Usuarios.Find(usuarioId);
            int idUsuAct = (from u in usuarios where u.nombre == User.Identity.Name select u.usuarioId).FirstOrDefault();

            if (usuario.recibirMail == true)
            {
                var trab = (from t in db.TrabajoUsuarios where t.usuarioId == usuarioId select t);
                foreach (TrabajoUsuarios t in trab)
                {
                    t.pedidoFecha = DateTime.Today.AddDays(3);
                    t.usuarioPresta = idUsuAct;
                }

                db.SaveChanges();

                //create the mail message
                MailMessage mail = new MailMessage();
                var client = new SmtpClient();

                //set the addresses
                mail.From = new MailAddress("prestocho@gmail.com");
                mail.To.Add(emailUsuario);

                //set the content
                mail.Subject = asunto;
                mail.Body = " <a href='http://localhost:2511/TrabajoUsuarios/DetailsTrabajo/" + trabajoId + "'>DETALLES</a> " + mensaje;
                mail.IsBodyHtml = true;
                client.Send(mail);
                ViewBag.Email = emailUsuario;

            }
            MensajesUsuarios mensajesUsuarios = new MensajesUsuarios { usuarioId = idUsuAct, fecha = DateTime.Today, usuarioRecibe = usuarioId, asunto = asunto, mensaje = mensaje, leido = false };

            db.MensajesUsuarios.Add(mensajesUsuarios);
            db.SaveChanges();
            return View();
        }
        /***************************************CAMBIAR******************************************/
        //
        // GET: /Prestado/SendRequest        
        public ActionResult SendRequest(int id)
        {
            var mensaje = "<html><body>Hola mi nombre es: <b>" + User.Identity.Name + "</b> y acepto tu solicitud: <a href='http://localhost:2511/TrabajoUsuarios/GuardarPedido/" + id + "'>DETALLES PRESTAMO</a> </body></html>";
            var asunto = "Respuesta a la solicitud de trabajo: ";
            int idUsuAct = (int)(from u in db.Usuarios where u.nombre == User.Identity.Name select u.usuarioId).FirstOrDefault();
            int idUsu = (int)(from i in db.TrabajoUsuarios where i.trabajoId == id select i.usuarioPresta).FirstOrDefault();

            var usuarios = db.Usuarios.ToList();
            Usuario usuario = db.Usuarios.Find(idUsu);
            if (usuario.recibirMail == true)
            {
                string emailUsu = (string)(from e in db.Usuarios where e.usuarioId == idUsu select e.email).FirstOrDefault();

                //create the mail message
                MailMessage mail = new MailMessage();

                var client = new SmtpClient();
                //set the addresses
                mail.From = new MailAddress("prestocho@gmail.com");
                mail.To.Add(emailUsu);

                //set the content
                mail.Subject = asunto;
                mail.Body = mensaje;
                mail.IsBodyHtml = true;
                client.Send(mail);
                ViewBag.Email = emailUsu;
            }
            MensajesUsuarios mensajesUsuarios = new MensajesUsuarios { fecha = DateTime.Today, usuarioId = idUsuAct, usuarioRecibe = idUsu, asunto = asunto, mensaje = mensaje, leido = false };

            db.MensajesUsuarios.Add(mensajesUsuarios);
            db.SaveChanges();

            return View();
        }

        //
        // GET: /Prestado/SendRequest        
        public ActionResult SendRequestCancel(int id)
        {
            var mensaje = "<html><body>Hola mi nombre es: <b>" + User.Identity.Name + "</b> y siento comunicarle que no es posible aceptar su solicitud.</a> </body></html>";
            var asunto = "Respuesta a la solicitud";
            int idUsu = (int)(from i in db.TrabajoUsuarios where i.trabajoId == id select i.usuarioPresta).FirstOrDefault();
            Usuario usuario = db.Usuarios.Find(idUsu);
            int idUsuAct = (int)(from u in db.Usuarios where u.nombre == User.Identity.Name select u.usuarioId).FirstOrDefault();
            if (usuario.recibirMail == true)
            {
                string emailUsu = (string)(from e in db.Usuarios where e.usuarioId == idUsu select e.email).FirstOrDefault();

                //create the mail message
                MailMessage mail = new MailMessage();

                var client = new SmtpClient();
                //set the addresses
                mail.From = new MailAddress("prestocho@gmail.com");
                mail.To.Add(emailUsu);

                //set the content
                mail.Subject = asunto;
                mail.Body = mensaje;
                mail.IsBodyHtml = true;
                client.Send(mail);
                ViewBag.Email = emailUsu;
                return RedirectToAction("Index", "TrabajoUsuarios");
            }
            MensajesUsuarios mensajesUsuarios = new MensajesUsuarios { fecha = DateTime.Today, usuarioId = idUsuAct, usuarioRecibe = idUsu, asunto = asunto, mensaje = mensaje, leido = false };

            db.MensajesUsuarios.Add(mensajesUsuarios);
            db.SaveChanges();
            return RedirectToAction("Index", "TrabajoUsuarios");
        }
    }
}

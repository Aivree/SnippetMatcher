using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;
using System.Web.Mvc;
using SaludGuruProfile.Manager.Controller;
using BackOffice.Models.General;
using MedicalCalendar.Manager.Models.Appointment;
using BackOffice.Models.Appointment;
using SaludGuruProfile.Manager.Models;

namespace BackOffice.Web.Controllers
{
    public partial class ExternalAppointmentController : BaseController
    {
        //Starup page project
        public virtual ActionResult Index(string AppointmentPublicId)
        {
            //get appoitment model
            ExternalAppointmentViewModel oModel = new ExternalAppointmentViewModel
            {
                CurrentAppointment = MedicalCalendar.Manager.Controller.Appointment.AppointmentGetById(AppointmentPublicId),
                CurrentProfile = SaludGuruProfile.Manager.Controller.Profile.ProfileGetByAppointmentId(AppointmentPublicId),
            };

            return View(oModel);
        }

        public virtual ActionResult Confirm(string AppointmentPublicId)
        {
            //get appoitment model
            ExternalAppointmentViewModel oModel = new ExternalAppointmentViewModel
            {
                CurrentAppointment = MedicalCalendar.Manager.Controller.Appointment.AppointmentGetById(AppointmentPublicId),
                CurrentProfile = SaludGuruProfile.Manager.Controller.Profile.ProfileGetByAppointmentId(AppointmentPublicId),
            };

            if (oModel.CurrentAppointment != null &&
                oModel.CurrentAppointment.RelatedPatient != null &&
                oModel.CurrentAppointment.RelatedPatient.Count == 1)
            {
                oModel.CurrentAppointment.Status = MedicalCalendar.Manager.Models.enumAppointmentStatus.Confirmed;

                //update appointment status
                MedicalCalendar.Manager.Controller.Appointment.UpdateAppointmentStatus(oModel.CurrentAppointment);

                oModel.CurrentAppointment = MedicalCalendar.Manager.Controller.Appointment.AppointmentGetById(AppointmentPublicId);

                //TODO send confirm message
                BackOffice.Web.Controllers.BaseController.SendMessage(oModel.CurrentProfile, enumProfileInfoType.AsignedAppointment, oModel.CurrentAppointment.RelatedPatient, oModel.CurrentAppointment, true);                
            }


            return View(oModel);
        }

        public virtual ActionResult Cancel(string AppointmentPublicId)
        {
            //get appoitment model
            ExternalAppointmentViewModel oModel = new ExternalAppointmentViewModel
            {
                CurrentAppointment = MedicalCalendar.Manager.Controller.Appointment.AppointmentGetById(AppointmentPublicId),
                CurrentProfile = SaludGuruProfile.Manager.Controller.Profile.ProfileGetByAppointmentId(AppointmentPublicId),
            };

            if (oModel.CurrentAppointment != null &&
                oModel.CurrentAppointment.RelatedPatient != null &&
                oModel.CurrentAppointment.RelatedPatient.Count == 1)
            {
                AppointmentModel AppointmentToUpsert = new AppointmentModel()
                {
                    AppointmentPublicId = oModel.CurrentAppointment.AppointmentPublicId,
                    Status = MedicalCalendar.Manager.Models.enumAppointmentStatus.Canceled,
                    AppointmentInfo = new List<AppointmentInfoModel>() 
                    { 
                        new AppointmentInfoModel()
                        {
                            AppointmentInfoType = MedicalCalendar.Manager.Models.enumAppointmentInfoType.CancelAppointementReason,
                            LargeValue = "Cita cancelada por el usuario " + DateTime.Now.ToString("yyyy/MM/dd HH:mm"),
                        },
                    },
                };

                //update appointment status
                MedicalCalendar.Manager.Controller.Appointment.UpdateAppointmentStatus(AppointmentToUpsert);

                //insert cancel reason
                if (AppointmentToUpsert.AppointmentInfo.Any(x => x.AppointmentInfoType == MedicalCalendar.Manager.Models.enumAppointmentInfoType.CancelAppointementReason))
                {
                    MedicalCalendar.Manager.Controller.Appointment.UpdateAppointmentInfoItem
                            (AppointmentToUpsert.AppointmentInfo.Where(x => x.AppointmentInfoType == MedicalCalendar.Manager.Models.enumAppointmentInfoType.CancelAppointementReason).FirstOrDefault(),
                            AppointmentToUpsert.AppointmentPublicId);
                }

                oModel.CurrentAppointment = MedicalCalendar.Manager.Controller.Appointment.AppointmentGetById(AppointmentPublicId);

                BackOffice.Web.Controllers.BaseController.SendMessage(oModel.CurrentProfile, enumProfileInfoType.CancelAppointment, oModel.CurrentAppointment.RelatedPatient, AppointmentToUpsert, true);                
            }


            return View(oModel);
        }
    }
}
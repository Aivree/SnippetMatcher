using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;
using System.Web.Mvc;

using MvcContrib.ActionResults;

using LinearDataCollection.Attributes;
using LinearDataCollection.Models;
using LinearDataCollection.Models.CMC;

using helper = LinearDataCollection.Extensions.WebServiceHelper;

namespace LinearDataCollection.Controllers
{
    public class CMCController : Controller
    {
        [FormatFilter(true)]
        public ActionResult CastingFinishMoveCoil(string id)
        {
            var model =  helper.GetCMCWebServiceModel<SimpleResponseModel>(x =>
            {
                x.Message = string.Format("Coil {0} moved successfully.", id);
            });
            return View(model);
        }

        [FormatFilter(true)]
        public ActionResult MillRequestCoil()
        //public void MillRequestCoil()
        {
            var model = helper.GetCMCWebServiceModel<CoilIdAndMessagesResponseModel>(x =>
                {
                    x.CoilId = "CC12345";
                });
            return View(model);
        }

        [FormatFilter(true)]
        public ActionResult MillCancelCoil(string id, bool error = false)
        {
           var model = helper.GetCMCWebServiceModel<SimpleResponseModel>(x => 
            {
                x.Message = string.Format("Coil {0} cancelled.", id);
            },error);
           return View(model);
        }

        [FormatFilter(true)]
        public ActionResult MillPickCoil(string id)
        {
           var model = helper.GetCMCWebServiceModel<SimpleResponseModel>(x =>
                {
                    x.Message = string.Format("Coil {0} has arrived at mill.",id);
                });
           return View(model);
        }

        [FormatFilter(true)]
        public ActionResult MillGetNextDestination(string id)
        {
           var model = helper.GetCMCWebServiceModel<NextDestinationModel>(x =>
            {
                x.NextDestination = "123";
            });
           return View(model);
        }

        [FormatFilter(true)]
        public ActionResult AnnealSendCoil()
        {
           var model = helper.GetCMCWebServiceModel<CoilIdResponseModel>(x =>
                {
                    x.CoilId = "12345";
                });
           return View(model);
        }

        [FormatFilter(true)]
        public ActionResult AnnealGetDetailedData(string id)
        {
           var model = helper.GetCMCWebServiceModel<CoilIdAndMessagesResponseModel>(x =>
                {
                    x.J204 = "J204 Message.";
                    x.CoilId = id;
                    x.M3 = "M3 Message";
                    x.M5 = "M5 Message";
                    x.Message = id + "worked successfully.";
                });
           return View(model);
        }

        [FormatFilter(true)]
        public ActionResult AnnealCancelCoil(string id)
        {
           var model = helper.GetCMCWebServiceModel<SimpleResponseModel>(x =>
                {
                    x.Message = string.Format("Coil {0} was cancelled.", id);
                });
           return View(model);
        }

        [FormatFilter(true)]
        public ActionResult SlitterSentTo(string id)
        {
           var model = helper.GetCMCWebServiceModel<SimpleResponseModel>(x =>
                {
                    x.Message = string.Format("Coil {0} sent to slitter", id);
                });
           return View(model);
        }

        [FormatFilter(true)]
        public ActionResult SlitterSentFrom(string id)
        {
           var model = helper.GetCMCWebServiceModel<SimpleResponseModel>(x =>
                {
                    //go into ax and do stuff
                    //make sure everythingi s ok
                    //if so, set the message
                    x.Message = string.Format("Coil {0} sent from slitter", id);
                });
           return View(model);
        }

        [FormatFilter(true)]
        public ActionResult VerticalDeliveredTo(string id)
        {
           var model = helper.GetCMCWebServiceModel<SimpleResponseModel>(x =>
                {
                    x.Message = string.Format("Coil {0} delivered to Vertical East", id);
                });
           return View(model);
        }

        [FormatFilter(true)]
        public ActionResult VerticalDeliveredFrom(string id)
        {
           var model = helper.GetCMCWebServiceModel<SimpleResponseModel>(x =>
                {
                    x.Message = string.Format("Coil {0} delivered from Vertical East", id);
                });
           return View(model);
        }
    }
}

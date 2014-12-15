using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;
using System.Web.Mvc;
using log4net;
using Newtonsoft.Json;
using PreAuthBusinessLib.Bo;
using PreAuthBusinessLib.Dao;
using PreAuthBusinessLib.Fo;
using PreAuthEntityLib.Vo;
using PreAuthMvc.Common;
using PreAuthMvc.Models;
using Ralph.CommonLib.Dao;
using Ralph.CommonLib.NHibernate;

namespace PreAuthMvc.Controllers
{
    /// <summary>
    /// 撤销预授权
    /// </summary>
    public class CancelPreAuthController : BaseController
    {

        private int totalCount = 0;
        private static ILog _log = LogManager.GetLogger(ConstValues.LogName);
        private readonly SessionHelper sessionHelper;
        public CancelPreAuthController()
        {
            sessionHelper = new SessionHelper();
        }
        protected override void Dispose(bool isDisposing)
        {
            if (isDisposing)
            {
                sessionHelper.CloseSession();
            }
            // 让基类释放自己的资源。基类负责调用GC.SuppressFinalize()
            base.Dispose(isDisposing);
        }
        // GET: CancelPreAuth
        public ActionResult Index()
        {
            return View();
        }

        public ActionResult Query(int pageNo = 1)
        {
           //var sessionHelper = new SessionHelper();
            try
            {
                var fo = new PreAuthFo
                {
                    TelNumber = Request.Params["telNumber"],
                    CustomerIdNo = Request.Params["customerIdNo"]
                };

                var dao = new PreAuthDao(sessionHelper);
                var rVoList = dao.QueryByCondition(fo, pageNo, ConstValues.PageSize, ref totalCount);
                var rList = (from p in rVoList
                             select new CancelPreAuthViewModel
                             {
                                 Id = p.Id,
                                 PreAuthInfo = new PreAuthViewModel()
                                 {
                                     TelNumber = p.TelNumber,
                                     CustomerIdNo = p.CustomerIdNo,
                                     CustomerName = p.CustomerName,
                                     StateName = p.StateCode
                                 }
                             }).ToList();

            PreAuthUtil.SetPageInfo(ViewData, pageNo, totalCount);
            
            return PartialView("~/Views/CancelPreAuth/QueryResult.cshtml",rList);
            }
            catch (Exception ex)
            {
                // log exception in case any errors uccur
                _log.Error("Error while manipulating entities using NHibernate.", ex);
                throw;
            }
        }

        public string Cancel()
        {
            var recordId = Request.Params["recordId"];
            var dao = new PreAuthDao(sessionHelper);
            var vo=dao.LoadById(Convert.ToInt64(recordId));
            var bo = new PreAuthBo(sessionHelper);

            if (bo.SendCancelRequest(vo))
            {
                using (var transaction = sessionHelper.Current.BeginTransaction())
                {
                    try
                    {
                        vo.StateCode = "cancel";
                        dao.SaveOrUpdate(vo);
                        transaction.Commit();
                    }
                    catch (Exception nhEx)
                    {
                        _log.Error("NHibernate throw exceptions", nhEx);
                        transaction.Rollback();
                        sessionHelper.ClearSession();
                        throw new Exception("NHibernate throw exceptions", nhEx);
                    }
                }
            }
            var r = new
            {
                jsonFlag = true,
                jsonMsg = "Cancel Success"

            };
            return JsonConvert.SerializeObject(r);
            //ViewData["RedirectAction"] = "/CancelPreAuth/Index";
            //return PartialView("~/Views/Shared/CommonSuccess.cshtml");
        }
    }
}
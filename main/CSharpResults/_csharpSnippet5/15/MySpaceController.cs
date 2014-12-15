using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;
using System.Web.Mvc;

using ZTE.HRCloud.Core.Contract.Service;
using ZTE.HRCloud.Core.SDK;
using ZTE.HRCloud.Core.Contract.Model.Core;
using ZTE.HRCloud.Platform.Models;
using ZTE.HRCloud.Core.Contract.Request.Core;
using ZTE.HRCloud.Core.Contract.Model.Flow;
using ZTE.HRCloud.Contract.Common.Model;
using ZTE.HRCloud.Platform.Controllers.Filter;
using ZTE.HRCloud.SystemManage.Contract.Service;
using ZTE.HRCloud.SystemManage.Contract.Model;

namespace ZTE.HRCloud.Platform.Controllers
{
    public class MySpaceController : Controller
    {
        #region 公共内容

        public ActionResult LeftMenu()
        {
            ViewBag.LeftMenu = Session["ThisLeftMenu"];
            return View();
        }

        #endregion

        public ActionResult Index()
        {
            if (Permission.Check("1001"))
            {
                return new RedirectResult(Url.Action("Info", "MySpace"));
            }
            else if (Permission.Check("1002"))
            {
                return new RedirectResult(Url.Action("MyTask", "MySpace"));
            }
            else if (Permission.Check("1003"))
            {
                return new RedirectResult(Url.Action("MyApply", "MySpace"));
            }
            else if (Permission.Check("1004"))
            {
                return new RedirectResult(Url.Action("Password", "MySpace"));
            }
            else if (Permission.Check("1005"))
            {
                return new RedirectResult(Url.Action("ForOther", "MySpace"));
            }
            return View();
        }

        /// <summary>
        /// 信息中心
        /// </summary>
        /// <returns></returns>
        [CheckLogin(UserRoleTypeEnum.User)]
        public ActionResult Info()
        {
            this.ViewBag.JS = new string[] {
                Url.Content("~/Scripts/MySpace/Info.js") 
            };

            Session["ThisLeftMenu"] = "Info";
            ViewBag.UserId = LoginUser.Id;
            return View();
        }

        #region 待我处理

        /// <summary>
        /// 待我处理
        /// </summary>
        /// <returns></returns>
        [CheckLogin(UserRoleTypeEnum.User)]
        public ActionResult MyTask()
        {
            this.ViewBag.JS = new string[] {
                UrlTool.Source("/Scripts/plugin/dev/CascadeSelect.js"), 
                UrlTool.Source("/Scripts/plugin/dev/BatchSubmitList.js"), 
                Url.Content("~/Scripts/MySpace/MyTask.js") 
            };

            Session["ThisLeftMenu"] = "MyTask";

            ICore sdk = SDK_Base.GetInstance<ICore>();
            IList<BizTypeInfo> bizCats = sdk.GetBizTypeInfoList();
            IList<BizInfo> bizs = sdk.GetBizInfoList();

            MyTaskData data = new MyTaskData();
            data.BizCats = bizCats;
            data.Bizs = bizs;
            return View(data);
        }

        [HttpPost]
        public ActionResult MyTaskList(MyTaskApplyInfoRequest req, int page, int rows)
        {
            req.OperatorId = LoginUser.Id;

            int count = 100;
            ICore sdk = SDK_Base.GetInstance<ICore>();
            IList<ApplyInfo> list = sdk.GetMyTaskApplyInfoListPaging(req, page, rows, out count);
            return Json(new { total = count, rows = list });
        }

        /// <summary>
        /// 内容摘要：处理申请
        /// </summary>
        [HttpPost]
        public ActionResult HandleTask(HandlerTaskInfo req)
        {
            try
            {
                ICore sdk = SDK_Base.GetInstance<ICore>();
                sdk.HandleTaskById(req.Id, req.Text, LoginUser.Id);
                return Json(new { ok = true });
            }
            catch (Exception ex)
            {
                return Json(new { ok = false, Message = "操作失败！<br>" + ex.Message });
            }
        }

        /// <summary>
        /// 内容摘要：终止申请
        /// </summary>
        [HttpPost]
        public ActionResult CancelTask(HandlerTaskInfo req)
        {
            try
            {
                ICore sdk = SDK_Base.GetInstance<ICore>();
                sdk.CancelTaskById(req.Id, req.Text, LoginUser.Id);
                return Json(new { ok = true });
            }
            catch (Exception ex)
            {
                return Json(new { ok = false, Message = "操作失败！<br>" + ex.Message });
            }
        }

        /// <summary>
        /// 内容摘要：挂起申请
        /// </summary>
        [HttpPost]
        public ActionResult SuspendTask(HandlerTaskInfo req)
        {
            try
            {
                ICore sdk = SDK_Base.GetInstance<ICore>();
                sdk.SuspendTaskById(req.Id, req.Text, LoginUser.Id);
                return Json(new { ok = true });
            }
            catch (Exception ex)
            {
                return Json(new { ok = false, Message = "操作失败！<br>" + ex.Message });
            }
        }

        /// <summary>
        /// 催办
        /// </summary>
        /// <param name="msg"></param>
        /// <returns></returns>
        [HttpPost]
        public ActionResult SendMyTaskReminder(ReminderMessageInfo msg)
        {
            msg.FromId = LoginUser.UserId;
            msg.SendTo = ApplyParticipantsEnum.申请人;

            try
            {
                ICore sdk = SDK_Base.GetInstance<ICore>();
                sdk.SendReminderMessage(msg);
                return Json(new { ok = true });
            }
            catch (Exception ex)
            {
                return Json(new { ok = false, Message = "操作失败！<br>" + ex.Message });
            }
        }

        public ActionResult ApplyHandle(string id)
        {
            ICore sdk = SDK_Base.GetInstance<ICore>();
            ApplyBaseInfo apply = sdk.GetApplyInfo(id);

            IList<ApplyOperResoultLogInfo> logs = sdk.GetApplyOperResoultLogInfoList(id);

            ViewBag.Id = id;
            ViewBag.BizId = apply.BizId;
            ViewBag.StepId = apply.CurrentStepId;
            if (apply.Status == "-1")
            {
                ViewBag.Finish = true;
            }
            return View(logs);
        }

        public ActionResult ApplyHandleInfo(string id, string bizId)
        {
            ICore sdk = SDK_Base.GetInstance<ICore>();  
             IFlowManage fSdk = SDK_Base.GetInstance<IFlowManage>();

            ApplyBaseInfo apply = sdk.GetApplyInfo(id);
            FlowStepInfo step = fSdk.GetFlowStepInfo(apply.CurrentStepId);

            StandardStepEnum stdStep = (StandardStepEnum)Enum.Parse(typeof(StandardStepEnum), step.StdStepId);
            switch (stdStep)
            {
                case StandardStepEnum.Approve:
                    return View("HandleApprove");
                case StandardStepEnum.Check:
                    return View("HandleCheck");
                case StandardStepEnum.ExternalProc:
                    return View("HandleExternalProc");
                case StandardStepEnum.File:
                    return View("HandleFile");
                case StandardStepEnum.InternalProc:
                    return View("HandleInternalProc");
                case StandardStepEnum.Scan:
                    return View("HandleScan");
            }
            return View();
        }

        public ActionResult HandleApprove()
        {
            return View();
        }

        public ActionResult HandleCheck()
        {
            return View();
        }

        public ActionResult HandleExternalProc()
        {
            return View();
        }

        public ActionResult HandleFile()
        {
            return View();
        }

        public ActionResult HandleInternalProc()
        {
            return View();
        }

        public ActionResult HandleScan()
        {
            return View();
        }

        #endregion

        #region 我的申请

        /// <summary>
        /// 我的申请
        /// </summary>
        /// <returns></returns>
        [CheckLogin(UserRoleTypeEnum.User)]
        public ActionResult MyApply()
        {
            this.ViewBag.JS = new string[] {
                UrlTool.Source("/Scripts/plugin/dev/CascadeSelect.js"), 
                UrlTool.Source("/Scripts/plugin/dev/BatchSubmitList.js"), 
                Url.Content("~/Scripts/MySpace/MyApply.js") 
            };

            Session["ThisLeftMenu"] = "MyApply";
            return View();
        }

        [HttpPost]
        public ActionResult MyApplyList(MyApplyInfoRequest req, int page, int rows)
        {
            req.ApplicantId = LoginUser.Id;

            int count = 100;
            ICore sdk = SDK_Base.GetInstance<ICore>();
            IList<ApplyInfo> list = sdk.GetMyApplyInfoListPaging(req, page, rows, out count);
            return Json(new { total = count, rows = list });
        }

        /// <summary>
        /// 催办
        /// </summary>
        /// <param name="msg"></param>
        /// <returns></returns>
        [HttpPost]
        public ActionResult SendMyApplyReminder(ReminderMessageInfo msg)
        {
            msg.FromId = LoginUser.UserId;
            msg.SendTo = ApplyParticipantsEnum.操作人;

            try
            {
                ICore sdk = SDK_Base.GetInstance<ICore>();
                sdk.SendReminderMessage(msg);
                return Json(new { ok = true });
            }
            catch (Exception ex)
            {
                return Json(new { ok = false, Message = "操作失败！" });
            }
        }

        public ActionResult ApplyView(string id)
        {
            ICore sdk = SDK_Base.GetInstance<ICore>();
            ApplyBaseInfo apply = sdk.GetApplyInfo(id);
           
            IList<ApplyOperResoultLogInfo> logs = sdk.GetApplyOperResoultLogInfoList(id);

            ViewBag.Id = id;
            ViewBag.BizId = apply.BizId;
            ViewBag.StepId = apply.CurrentStepId;
            if (apply.Status == "-1")
            {
                ViewBag.Finish = true;
            }
            return View(logs);
        }

        #region 申请基本信息

        public ActionResult ApplyViewBaseInfo(string id, string bizId)
        {
            ICore sdk = SDK_Base.GetInstance<ICore>();
            ApplyBaseInfo apply = sdk.GetApplyInfo(id);

            BizTypeEnum biz = (BizTypeEnum)Enum.Parse(typeof(BizTypeEnum), bizId);
            switch (biz)
            {
                case BizTypeEnum.HR001:
                    return View("ApplyViewHr001", apply);
                case BizTypeEnum.HR002:
                    return View("ApplyViewHr002", apply);
                case BizTypeEnum.HR003:
                    return View("ApplyViewHr003", apply);
                case BizTypeEnum.HR004:
                    return View("ApplyViewHr004", apply);
                case BizTypeEnum.HR005:
                    return View("ApplyViewHr005", apply);

                case BizTypeEnum.HK001:
                    return View("ApplyViewHK001", apply);
                case BizTypeEnum.HK002:
                    return View("ApplyViewHK002", apply);
                case BizTypeEnum.HK003:
                    return View("ApplyViewHK003", apply);
                case BizTypeEnum.HK005:
                    return View("ApplyViewHK005", apply);
                case BizTypeEnum.HK007:
                    return View("ApplyViewHK007", apply);

                case BizTypeEnum.ZJ001:
                    return View("ApplyViewZJ001", apply);
                case BizTypeEnum.ZJ002:
                    return View("ApplyViewZJ002", apply);

                case BizTypeEnum.ZM001:
                    return View("ApplyViewZM001", apply);
                case BizTypeEnum.ZM002:
                    return View("ApplyViewZM002", apply);
                case BizTypeEnum.ZM003:
                    return View("ApplyViewZM003", apply);
                case BizTypeEnum.ZM004:
                    return View("ApplyViewZM004", apply);
                case BizTypeEnum.ZM005:
                    return View("ApplyViewZM005", apply);

                case BizTypeEnum.SB001:
                    return View("ApplyViewSB001", apply);
                case BizTypeEnum.SB002:
                    return View("ApplyViewSB002", apply);
                case BizTypeEnum.SB003:
                    return View("ApplyViewSB003", apply);
                case BizTypeEnum.SB004:
                    return View("ApplyViewSB004", apply);
                case BizTypeEnum.SB005:
                    return View("ApplyViewSB005", apply);
                case BizTypeEnum.SB006:
                    return View("ApplyViewSB006", apply);
                case BizTypeEnum.SB007:
                    return View("ApplyViewSB007", apply);
                case BizTypeEnum.SB008:
                    return View("ApplyViewSB008", apply);

                case BizTypeEnum.HF001:
                    return View("ApplyViewHF001", apply);
                case BizTypeEnum.HF002:
                    return View("ApplyViewHF002", apply);
                case BizTypeEnum.HF003:
                    return View("ApplyViewHF003", apply);
                case BizTypeEnum.HF004:
                    return View("ApplyViewHF004", apply);
                case BizTypeEnum.HF005:
                    return View("ApplyViewHF005", apply);
                case BizTypeEnum.HF006:
                    return View("ApplyViewHF006", apply);
                case BizTypeEnum.HF007:
                    return View("ApplyViewHF007", apply);
            }
            return View();
        }

        #region 人事基本业务
        public ActionResult ApplyViewHr001(string id)
        {
            return View();
        }

        public ActionResult ApplyViewHr002(string id)
        {
            return View();
        }

        public ActionResult ApplyViewHr003(string id)
        {
            return View();
        }

        public ActionResult ApplyViewHr004(string id)
        {
            return View();
        }

        public ActionResult ApplyViewHr005(string id)
        {
            return View();
        }

        #endregion 

        #region 户口业务

        public ActionResult ApplyViewHK001(string id)
        {
            return View();
        }

        public ActionResult ApplyViewHK002(string id)
        {
            return View();
        }

        public ActionResult ApplyViewHK003(string id)
        {
            return View();
        }

        public ActionResult ApplyViewHK005(string id)
        {
            return View();
        }

        public ActionResult ApplyViewHK007(string id)
        {
            return View();
        }

        #endregion

        #region 证件业务

        public ActionResult ApplyViewZJ001(string id)
        {
            return View();
        }

        public ActionResult ApplyViewZJ002(string id)
        {
            return View();
        }

        #endregion

        #region 证明业务

        public ActionResult ApplyViewZM001(string id)
        {
            return View();
        }

        public ActionResult ApplyViewZM002(string id)
        {
            return View();
        }

        public ActionResult ApplyViewZM003(string id)
        {
            return View();
        }

        public ActionResult ApplyViewZM004(string id)
        {
            return View();
        }

        public ActionResult ApplyViewZM005(string id)
        {
            return View();
        }


        #endregion

        #region 社保业务
       
        public ActionResult ApplyViewSB001(string id)
        {
            return View();
        }

        public ActionResult ApplyViewSB002(string id)
        {
            return View();
        }

        public ActionResult ApplyViewSB003(string id)
        {
            return View();
        }

        public ActionResult ApplyViewSB004(string id)
        {
            return View();
        }

        public ActionResult ApplyViewSB005(string id)
        {
            return View();
        }

        public ActionResult ApplyViewSB006(string id)
        {
            return View();
        }

        public ActionResult ApplyViewSB007(string id)
        {
            return View();
        }

        public ActionResult ApplyViewSB008(string id)
        {
            return View();
        }
        #endregion

        #region 公积金业务

        public ActionResult ApplyViewHF001(string id)
        {
            return View();
        }        

        public ActionResult ApplyViewHF002(string id)
        {
            return View();
        }

        public ActionResult ApplyViewHF003(string id)
        {
            return View();
        }

        public ActionResult ApplyViewHF004(string id)
        {
            return View();
        }

        public ActionResult ApplyViewHF005(string id)
        {
            return View();
        }

        public ActionResult ApplyViewHF006(string id)
        {
            return View();
        }

        public ActionResult ApplyViewHF007(string id)
        {
            return View();
        }

        #endregion

        #endregion
        #endregion

        #region 修改密码

        /// <summary>
        /// 修改密码
        /// </summary>
        /// <returns></returns>
        [CheckLogin(UserRoleTypeEnum.User)]
        public ActionResult Password()
        {
            this.ViewBag.JS = new string[] {
                Url.Content("~/Scripts/MySpace/Password.js") 
            };
            Session["ThisLeftMenu"] = "Password";
            return View();
        }

        [HttpPost]
        public ActionResult ModifyPassword(ModifyPassword info)
        {
            try
            {
                IUserService sdk = ZTE.HRCloud.SystemManage.SDK.SDK_Base.GetInstance<IUserService>();
                UserInfo user = sdk.GetUserInfo(LoginUser.Id);

                bool isOk = false;
                sdk.CheckLogin(LoginUser.CompanyCode, user.Code, info.Password, out isOk);
                if (!isOk)
                {
                    return Json(new { ok = false, Message = "当前密码错误！" });
                }
                sdk.ModifyPassword(user.Id, info.NewPassword);
                return Json(new { ok = true, Message = "密码修改成功！" });
            }
            catch (Exception ex)
            {
                return Json(new { ok = false, Message = "操作失败！" });
            }
        }

        #endregion

        #region 代人申请
        /// <summary>
        /// 代人申请
        /// </summary>
        /// <returns></returns>
        [CheckLogin(UserRoleTypeEnum.User)]
        public ActionResult ForOther()
        {
            this.ViewBag.JS = new string[] {
                UrlTool.Source("/Scripts/plugin/dev/CascadeSelect.js"), 
                UrlTool.Source("/Scripts/plugin/Parts/UserChooseDialog.js"),
                Url.Content("~/Scripts/MySpace/ForOther.js") 
            };
            ViewBag.UserName = LoginUser.UserName;
            ViewBag.CompanyId = LoginUser.CompanyId;
            Session["ThisLeftMenu"] = "ForOther";
            return View();
        }

        [HttpPost]
        public ActionResult GoApply(string id, string bizId)
        {
            try
            {
                MenuRoute mr = new MenuRoute(Request.RequestContext);
                string url = mr.Route(bizId);
                if (string.IsNullOrEmpty(url))
                {
                    return Json(new { ok = false, Message = "操作失败！<br>选择的业务暂未开通！" });
                }
                url += "/" + id;
                return Json(new { ok = true, url = url });
            }
            catch (Exception ex)
            {
                return Json(new { ok = false, Message = "操作失败！<br>" + ex.Message });
            }
        }

        #endregion
    }
}
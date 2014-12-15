using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;
using System.Web.Mvc;
using Microsoft.Practices.Unity;
using THOK.Wms.Bll.Interfaces;
using THOK.WebUtil;
using THOK.Wms.DbModel;
using THOK.Security;
using Wms.Security;

namespace WMS.Controllers.Wms.WMS
{
    [TokenAclAuthorize]
    [SystemEventLog]
    public class FeedingController : Controller
    {
        //
        // GET: /Feeding/
        [Dependency]
        public IWMSBillMasterService BillMasterService { get; set; }
        [Dependency]
        public IWMSProductStateService ProductStateService { get; set; }
        [Dependency]
        public IViewstorageService viewstorageService { get; set; }

        public ActionResult Index(string moduleID)
        {
            ViewBag.hasSearch = true;
            ViewBag.hasAdd = true;
            ViewBag.hasEdit = true;
            ViewBag.hasDelete = true;
            ViewBag.hasPrint = true;
            ViewBag.hasHelp = true;
            ViewBag.hasAudit = true;
            ViewBag.hasAntiTrial = true;
            ViewBag.hasTask = true;
            ViewBag.hasCancel = true;
            ViewBag.hasUpload = true;
            ViewBag.ModuleID = moduleID;
            return View();
        }
        public ActionResult Details(int page, int rows, string flag, FormCollection collection)
        {
            string BILL_NO = collection["BILL_NO"] ?? "";  //单据编号
            string BILL_DATE = collection["BILL_DATE"] ?? ""; //单据日期(出,入库日期)
            string BTYPE_CODE = collection["BTYPE_CODE"] ?? ""; //单据类型
            string WAREHOUSE_CODE = collection["WAREHOUSE_CODE"] ?? ""; //仓库编号
            string BILL_METHOD = collection["BILL_METHOD"] ?? ""; //出,入库方式
            string CIGARETTE_CODE = collection["CIGARETTE_CODE"] ?? ""; //牌号
            string FORMULA_CODE = collection["FORMULA_CODE"] ?? ""; //配方代码
            string STATE = collection["STATE"] ?? ""; //状态
            string OPERATER = collection["OPERATER"] ?? ""; //操作人
            string OPERATE_DATE = collection["OPERATE_DATE"] ?? ""; //操作日期
            string CHECKER = collection["CHECKER"] ?? ""; //审核人
            string CHECK_DATE = collection["CHECK_DATE"] ?? ""; //审核日期
            string STATUS = collection["STATUS"] ?? ""; //单据来源
            string BILL_DATEStar = collection["BILL_DATEStar"] ?? ""; //起始日期
            string BILL_DATEEND = collection["BILL_DATEEND"] ?? "";//截止日期
            string SOURCE_BILLNO = collection["SOURCE_BILLNO"] ?? "";//来源单号
            var Billmaster = BillMasterService.GetDetails(page, rows, "2", flag, BILL_NO, BILL_DATE, BTYPE_CODE, WAREHOUSE_CODE, BILL_METHOD, CIGARETTE_CODE, FORMULA_CODE, STATE, OPERATER, OPERATE_DATE, CHECKER, CHECK_DATE, STATUS, BILL_DATEStar, BILL_DATEEND, SOURCE_BILLNO,"");
            return Json(Billmaster, "text/html", JsonRequestBehavior.AllowGet);
        }
        //单据编号
        public ActionResult GetBillNo(System.DateTime dtime, string BILL_NO, string prefix)
        {
            string userName = this.GetCookieValue("username");
            var BillnoInfo = BillMasterService.GetBillNo(userName, dtime, BILL_NO, prefix);
            return Json(BillnoInfo, "text/html", JsonRequestBehavior.AllowGet);
        }
        //查询出库批次
        public ActionResult GetOutstockBill(int page, int rows, string queryinfo)
        {
            var Billmaster = BillMasterService.Outstockbill(page, rows, queryinfo);
            return Json(Billmaster, "text/html", JsonRequestBehavior.AllowGet);
        }
        //获取入库批次的明细
        public ActionResult Getbillsubdetail(int page, int rows, string BillNo)
        {
            var Billdetail = BillMasterService.GetSubDetailsforfeeeding(page, rows, BillNo);
            return Json(Billdetail, "text/html", JsonRequestBehavior.AllowGet);
        }
        //紧急补料单添加
        public ActionResult Add(WMS_BILL_MASTER mast, string prefix)
        {
            object detail = Request.Params["detailjson"];
            string userid = this.GetCookieValue("userid");
            mast.OPERATER = userid;
            bool bResult = BillMasterService.FeedingAdd (mast, detail, prefix);
            string msg = bResult ? "新增成功" : "新增失败";
            return Json(JsonMessageHelper.getJsonMessage(bResult, msg, null), "text/html", JsonRequestBehavior.AllowGet);
        }
        //获取仓库中的货物信息
        public ActionResult getproductdetail(int page, int rows, string formula, string productcode)
        {
            var products = viewstorageService.GetProductdetail(page, rows, formula, productcode);
            return Json(products, "text/html", JsonRequestBehavior.AllowGet);
        }
        //public ActionResult getproductdetail(int page, int rows, string formula,string grade,string original,string billno)
        //{
        //    var products = viewstorageService.GetProductdetail(page, rows, formula, grade, original,billno);
        //    return Json(products, "text/html", JsonRequestBehavior.AllowGet);
        //}
        public ActionResult getproductdetail2(int page, int rows, string queryinfo, string billno)
        {
            var products = viewstorageService.GetProductdetail2(page, rows, queryinfo, billno);
            return Json(products, "text/html", JsonRequestBehavior.AllowGet);
        }
        //编辑
        public ActionResult Edit(WMS_BILL_MASTER mast)
        {
            object detail = Request.Params["detailjson"];
            bool bResult = BillMasterService.FeedingEdit(mast, detail);
            string msg = bResult ? "修改成功" : "修改失败";
            return Json(JsonMessageHelper.getJsonMessage(bResult, msg, null), "text/html", JsonRequestBehavior.AllowGet);
        }
        //删除
        public ActionResult Delete(string Billno)
        {
            bool bResult = BillMasterService.Delete (Billno);
            string msg = bResult ? "删除成功" : "删除失败";
            return Json(JsonMessageHelper.getJsonMessage(bResult, msg, null), "text/html", JsonRequestBehavior.AllowGet);
        }
        //作业函数
        public ActionResult Task(string BillNo)
        {
            string userName = this.GetCookieValue("userid");
            string error = "";
            bool bResult = ProductStateService.FeedingTask (BillNo, userName, out error);
            string msg = bResult ? "作业成功" : "作业失败" + error;
            var just = new
            {
                success = msg
            };
            return Json(just, "text/html", JsonRequestBehavior.AllowGet);
        }
        //获取出库批次下的条码
        public ActionResult Getoutbarcode(int page, int rows, string OutBillNo, string barcode)
        {
            var outbarcodes = ProductStateService.outbarcdedetali(page, rows, OutBillNo, barcode);
            return Json(outbarcodes, "text/html", JsonRequestBehavior.AllowGet);
        }
        //上报mes
        public ActionResult uploadmes(string BillNo) {
            WMSSV.AHZY_ESB_WMS_GETSSVSoapClient client = new WMSSV.AHZY_ESB_WMS_GETSSVSoapClient();
            string mesresult = client.FeedingSend(BillNo);//紧急不料单上报
            string mesresult2 = client.StockOutCollection(BillNo);//再次工单归集
            string result = "mes返回的消息状态代码：单据上报结果:" + mesresult+"再次工单归集结果："+mesresult2 ;
            var msg = new { retruinfo = result };
            return Json(msg, "text", JsonRequestBehavior.AllowGet);
        }

    }
}

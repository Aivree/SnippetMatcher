using System;
using System.Collections.Generic;
using System.IO;
using System.Linq;
using System.Text;
using System.Web.Hosting;
using System.Web.Mvc;
using AutoMapper;
using NPOI.HSSF.UserModel;
using NPOI.SS.UserModel;
using NPOI.SS.Util;
using VendorsManage.Data;
using VendorsManage.Data.Models;
using VendorsManage.Models.ViewModel;
using VendorsManage.Properties;
using VendorsManage.Service;
using InfoSoftGlobal;
namespace VendorsManage.Controllers
{
    [MenuTypeData]
    public partial class CustomerProjectController : ApplicationController
    {
        public CustomerProjectController(IRepository rep) : base(rep){}

        //年度 12个月的图表格式化字符
        private const string XmlstrMothSale =
           @"<chart caption='{1}' xAxisName='月份' yAxisName='业务量(工程个数)' showValues='0' formatNumberScale='0' showBorder='1'>{0}</chart>";
        private const string XmlSetstrMothSale = @" <set label='{0}' value='{1}' link='javaScript:ShowDetails({2})' />";

        //年度图表格式化
        private const string XmlYearSum = @"<chart caption='{0}' formatNumberScale='0' showLegend='1'>{1}</chart>";
        private const string XmlSetYearSum = @"<set label='{0}' value='{1}' isSliced='0'  />";

        #region 客户内部工程列表

        // GET: /CustomerProject/ToDoList
        public virtual ActionResult TodoList()
        {
            var myprjs = Repository.All<CustomerProject>()
                .Where(x => !x.DisContinued.Value && x.FlowLogs.Any(l => l.IsActive && l.TodoBy == CurrentUser.UserId));
            IList<CustProjectModel> viewmodel =
                Mapper.Map<IList<CustomerProject>, IList<CustProjectModel>>(myprjs.ToList());
            return View(viewmodel);
        }
        public virtual ActionResult GroupTodoList()
        {
            var myprjs = Repository.All<CustomerProject>();
            if (CurrentUser.GroupId.Equals(5))
            {
                myprjs = myprjs
                    .Where(
                        x =>
                        !x.DisContinued.Value &&
                        x.FlowLogs.Any(
                            l =>
                            l.IsActive && !l.TodoBy.HasValue &&
                            l.TaskNode.UserGroup.GroupId == CurrentUser.UserGroup.GroupId));
            }
            IList<CustProjectModel> viewmodel =
                Mapper.Map<IList<CustomerProject>, IList<CustProjectModel>>(myprjs.ToList());
            return View(viewmodel);
        }
        public virtual ActionResult CanceledProjects()
        {
            var myprjs = Repository.All<CustomerProject>().Where(x => x.DisContinued.Value);
            IList<CustProjectModel> viewmodel =
                Mapper.Map<IList<CustomerProject>, IList<CustProjectModel>>(myprjs.ToList());
            ViewBag.ListTitle = "已经被取消的客户内部工程列表";
            return View(MVC.CustomerProject.Views.HandledList, viewmodel);
        }
        /// <summary>
        /// 显示登录用户已经办理的客户内部工程
        /// </summary>
        /// <returns></returns>
        public virtual ActionResult HandledList()
        {
            var myprjs = Repository.All<CustomerProject>()
                .Where(x => x.FlowLogs.Any(l => l.TodoBy == CurrentUser.UserId && !l.IsActive)).Distinct();
            var viewmodel = Mapper.Map<IList<CustomerProject>, IList<CustProjectModel>>(myprjs.ToList());
            ViewBag.ListTitle = "您近期已办理的客户内部工程列表";
            return View(viewmodel);
        }
        /// <summary>
        /// 显示所有客户内部工程
        /// </summary>
        /// <returns></returns>
        public virtual ActionResult CustProjects()
        {
            var myprjs = Repository.All<CustomerProject>().ToList();
            var viewmodel = Mapper.Map<IList<CustomerProject>, IList<CustProjectModel>>(myprjs);
            ViewBag.ListTitle = "所有客户内部工程列表";
            return View(MVC.CustomerProject.Views.HandledList, viewmodel);
        }

        public virtual ActionResult ProcessingProjects()
        {
            var myprjs = Repository.All<CustomerProject>().Where(x => !x.DisContinued.Value && !x.IsFinished.Value).ToList();
            var viewmodel = Mapper.Map<IList<CustomerProject>, IList<CustProjectModel>>(myprjs);
            ViewBag.ListTitle = "在建的客户内部工程列表";
            return View(MVC.CustomerProject.Views.HandledList, viewmodel);
        }

        public virtual ActionResult FinishedProjects()
        {
            var myprjs = Repository.All<CustomerProject>().Where(x => !x.DisContinued.Value && x.IsFinished.Value).ToList();
            var viewmodel = Mapper.Map<IList<CustomerProject>, IList<CustProjectModel>>(myprjs);
            ViewBag.ListTitle = "已建并归档的客户内部工程列表";
            return View(MVC.CustomerProject.Views.HandledList, viewmodel);
        }

        #endregion

        #region 客户内部工程数据导出到excel模板文件
        /// <summary>
        /// 已完工/已建项目导出excel表格
        /// </summary>
        /// <returns></returns>
        public virtual ActionResult ExportListToExcel()
        {
            var exportProjects = Repository.All<CustomerProject>().Where(x => x.IsFinished.Value && !x.DisContinued.Value).ToList();
            var xlstemplate = HostingEnvironment.MapPath("~/App_Data/") + "CustProjectSummary.xls";
            HSSFWorkbook hssfworkbook;

            //read the template via FileStream, it is suggested to use FileAccess.Read to prevent file lock.
            using (var file = new FileStream(xlstemplate, FileMode.Open, FileAccess.Read))
            hssfworkbook = new HSSFWorkbook(file);

            var sheet1 = hssfworkbook.GetSheet("已完工内部工程统计");
            int i = 3;
            int xh = 1;
            var style = hssfworkbook.CreateCellStyle();
            style.BorderBottom = CellBorderType.THIN;
            style.BorderLeft = CellBorderType.THIN;
            style.BorderRight = CellBorderType.THIN;
            style.BorderTop = CellBorderType.THIN;
            foreach (var customerProject in exportProjects)
            {
                var row = sheet1.CreateRow(i);
                var cell = row.CreateCell(0);
                cell.SetCellValue(xh);
                cell.CellStyle = style;
                cell = row.CreateCell(1);
                cell.SetCellValue(customerProject.CustomerNo);
                cell.CellStyle = style;
                cell = row.CreateCell(2);
                cell.SetCellValue(customerProject.CustomerName);
                cell.CellStyle = style;
                cell = row.CreateCell(3);
                cell.SetCellValue(customerProject.Address);
                cell.CellStyle = style;
                cell = row.CreateCell(4);
                cell.SetCellValue(customerProject.Capacity);
                cell.CellStyle = style;
                cell = row.CreateCell(5);
                cell.SetCellValue(customerProject.VoltageLevel.Name);
                cell.CellStyle = style;
                cell = row.CreateCell(6);
                if (customerProject.CustomerConstruct != null)
                {
                    cell.SetCellValue(customerProject.CustomerConstruct.DesignedBy);
                    cell.CellStyle = style;

                    cell = row.CreateCell(7);
                    cell.SetCellValue(customerProject.CustomerConstruct.ConstructedBy);
                    cell.CellStyle = style;
                }
                var equips = customerProject.CustomerEquips.ToList();
                int j = i;
                if (equips.Any())
                {
                    // 输出设备选用记录
                    foreach (var equip in equips)
                    {
                        Row eqrow = j > i ? sheet1.CreateRow(j) : row;
                        cell = eqrow.CreateCell(8);
                        cell.SetCellValue(equip.EquipName);
                        cell.CellStyle = style;
                        cell = eqrow.CreateCell(9);
                        cell.SetCellValue(equip.SupplyBy);
                        cell.CellStyle = style;
                        cell = eqrow.CreateCell(10);
                        cell.SetCellValue(Convert.ToDouble(equip.Quantity));
                        cell.CellStyle = style;
                        if (j > i)
                        {
                            for (int cl = 0; cl < 8; cl++)
                            {
                                eqrow.CreateCell(cl).CellStyle = style;
                            }
                        }
                        j++;
                    }
                    //如果设备记录数大于1，则合并前面的单元格
                    if (j - i > 1)
                    {
                        for (int k = 0; k < 8; k++)
                        {
                            var cellRangeAddress = new CellRangeAddress(i, j - 1, k, k);
                            sheet1.AddMergedRegion(cellRangeAddress);
                        }
                    }
                }
                else
                {
                    cell = row.CreateCell(8);
                    cell.SetCellValue("");
                    cell = row.CreateCell(9);
                    cell.SetCellValue("");
                    cell = row.CreateCell(10);
                    cell.SetCellValue("");
                }
                i = i + (j - i);
                xh++;
            }
            //Force excel to recalculate all the formula while open
            // sheet1.ForceFormulaRecalculation = true;
            var output = new MemoryStream();

            hssfworkbook.Write(output);
            return File(output.ToArray(),"application/vnd.ms-excel", Url.Encode("已建工程统计报表.xls"));
        }
        /// <summary>
        /// 导出在建的用户内部工程，按照营业站分sheet.
        /// </summary>
        /// <returns></returns>
        public virtual ActionResult ExportUnfinishedToExcel()
        {
            var unfinishedProjects = Repository.All<CustomerProject>()
                .Where(x => !x.IsFinished.Value && !x.DisContinued.Value).ToList();
            var xlstemplate = HostingEnvironment.MapPath("~/App_Data/") + "CustProject_Unfinished.xls";
            HSSFWorkbook hssfworkbook;
            //read the template via FileStream, it is suggested to use FileAccess.Read to prevent file lock.
            //book1.xls is an Excel-2007-generated file, so some new unknown BIFF records are added. 
            using (var file = new FileStream(xlstemplate, FileMode.Open, FileAccess.Read))
                hssfworkbook = new HSSFWorkbook(file);
            var exportProjects = unfinishedProjects.Where(x => x.BelongTo == "客户服务组").ToList();
            FillInSheet(hssfworkbook, exportProjects, "客户服务组在建内部工程");
            exportProjects = unfinishedProjects.Where(x => x.BelongTo == "枫泾营业站").ToList();
            FillInSheet(hssfworkbook, exportProjects, "枫泾营业站在建内部工程");
            exportProjects = unfinishedProjects.Where(x => x.BelongTo == "朱泾营业站").ToList();
            FillInSheet(hssfworkbook, exportProjects, "朱泾营业站在建内部工程");
            exportProjects = unfinishedProjects.Where(x => x.BelongTo == "张堰营业站").ToList();
            FillInSheet(hssfworkbook, exportProjects, "张堰营业站在建内部工程");
            exportProjects = unfinishedProjects.Where(x => x.BelongTo == "吕巷营业站").ToList();
            FillInSheet(hssfworkbook, exportProjects, "吕巷营业站在建内部工程");
            exportProjects = unfinishedProjects.Where(x => x.BelongTo == "亭林营业站").ToList();
            FillInSheet(hssfworkbook, exportProjects, "亭林营业站在建内部工程");

            //Force excel to recalculate all the formula while open
            // sheet1.ForceFormulaRecalculation = true;
            var output = new MemoryStream();
            hssfworkbook.Write(output);
            return File(output.ToArray(), "application/vnd.ms-excel", Url.Encode("在建内部工程统计报表.xls"));
        }

        internal void FillInSheet(HSSFWorkbook hssfworkbook, List<CustomerProject> exportProjects, string sheetname)
        {
            var sheet1 = hssfworkbook.GetSheet(sheetname);
            int i = 3;
            int xh = 1;
            var style = hssfworkbook.CreateCellStyle();
            style.BorderBottom = CellBorderType.THIN;
            style.BorderLeft = CellBorderType.THIN;
            style.BorderRight = CellBorderType.THIN;
            style.BorderTop = CellBorderType.THIN;
            foreach (var customerProject in exportProjects)
            {
                var row = sheet1.CreateRow(i);
                var cell = row.CreateCell(0);
                cell.SetCellValue(xh);
                cell.CellStyle = style;
                cell = row.CreateCell(1);
                cell.SetCellValue(customerProject.CustomerNo);
                cell.CellStyle = style;
                cell = row.CreateCell(2);
                cell.SetCellValue(customerProject.CustomerName);
                cell.CellStyle = style;
                cell = row.CreateCell(3);
                cell.SetCellValue(customerProject.Address);
                cell.CellStyle = style;
                cell = row.CreateCell(4);
                cell.SetCellValue(customerProject.Capacity);
                cell.CellStyle = style;
                cell = row.CreateCell(5);
                cell.SetCellValue(customerProject.VoltageLevel.Name);
                cell.CellStyle = style;
                cell = row.CreateCell(6);
                cell.SetCellValue(customerProject.BelongTo);
                cell.CellStyle = style;
                i++;
                xh++;
            }
        }

        #endregion
        #region 客户内部工程信息录入CRUD

        [CustPrjDropList]
        public virtual ActionResult Create()
        {
            return View(new CustProjectModel {CreatedBy = User.Identity.Name, LocalArea = "请选择区域",CreatedOn = DateTime.Now, EditMode = 0});
        }

        [HttpPost]
        [CustPrjDropList]
        public virtual ActionResult Create(CustProjectModel toadd)
        {
            if (ModelState.IsValid)
            {
                var newprj = new CustomerProject {ProjectTypeId = 9, IsFinished = false, DisContinued = false};
                TryUpdateModel(newprj);
                var service = new CustProjectService(Repository);
                service.AddProject(newprj, CurrentUser.UserId);
                return RedirectToAction(MVC.CustomerProject.TodoList());
            }
            return View(toadd);
        }

        [CustPrjDropList]
        public virtual ActionResult Edit(int id)
        {
            var toedit = Repository.Single<CustomerProject>(x => x.ID == id);
            var viewmodel = Mapper.Map<CustomerProject, CustProjectModel>(toedit);
            viewmodel.EditMode = 1;
            return View(viewmodel);
        }

        [HttpPost]
        [CustPrjDropList]
        public virtual ActionResult Edit(int id, CustProjectModel editedprj)
        {
            var toedit = Repository.Single<CustomerProject>(x => x.ID == id);
            if (ModelState.IsValid)
            {
                TryUpdateModel(toedit);
                Repository.Save();
                return RedirectToAction(MVC.CustomerProject.TodoList());
            }
            ModelState.AddModelError("", Resources.Editfailure);
            return View(editedprj);
        }

        [HttpPost]
        public virtual ActionResult Delete(int id)
        {
            var todel = Repository.Single<CustomerProject>(x => x.ID == id);
            Repository.Delete(todel);
            Repository.Save();
            return Content("工程已经删除");
        }

        
        public virtual ActionResult LookUpArea(string term)
        {
            var areas = new string[] { "金山区枫泾镇", "金山区枫泾镇(兴塔)", "金山区朱泾镇", "金山区亭林镇", "金山区亭林镇(松隐)",
                "金山区亭林镇(朱行)", "金山区漕泾镇", "金山区山阳镇","金山区吕巷镇","金山区吕巷镇(干巷)","金山区廊下镇","金山区张堰镇",
                "金山区金山卫镇","金山区金山卫镇(钱圩)","金山区石化街道","金山工业区"};  

                // Repository.All<CustomerProject>().Select(x => x.LocalArea).Where(x => x.Contains(term))
                //.Distinct()
               // .Select(x => x.Trim());
           var  targets = areas.Where(x => x.Contains(term));
           return Json(targets, JsonRequestBehavior.AllowGet);
        }

        public virtual ActionResult LookUpCapacity(string term)
        {
            var areas = Repository.All<CustomerProject>().Select(x => x.Capacity).Where(x => x.Contains(term)).Distinct()
                .Select(x => x.Trim());
            return Json(areas, JsonRequestBehavior.AllowGet);
        }

        public virtual ActionResult CustomerExist(string customerno, int editmode)
        {
            if (editmode.Equals(0))
                return Json(!Repository.All<CustomerProject>().Any(x => x.CustomerNo.Trim().Equals(customerno.Trim())),
                            JsonRequestBehavior.AllowGet);
            return Json(true, JsonRequestBehavior.AllowGet);
        }

        public virtual ActionResult ApplyNoExist(string applyno, int editmode)
        {
            if (editmode.Equals(0))
                return Json(!Repository.All<CustomerProject>().Any(x => x.ApplyNo.Trim().Equals(applyno.Trim())),
                            JsonRequestBehavior.AllowGet);
            return Json(true, JsonRequestBehavior.AllowGet);
        }
        

        #endregion
        #region 工程流程处理

        //GET /Customerproject/send 
        public virtual ActionResult Send(int id)
        {
            var tosend = Repository.Single<CustomerProject>(x => x.ID == id);
            //如果是ajax能够正常使用的话 返回对话框需要的html视图
            if (Request.IsAjaxRequest())
            {
                ViewBag.DefaultAction = tosend.ActiveNodes.First().SourceActions.First().ToTaskNodeId.ToString();
                return PartialView("_SendProject", tosend);
            }
            //否则，链接到视图
            return View("_SendProject", tosend);
        }


        [HttpPost]
        public virtual ActionResult Send(int id, int todoby, int desnodeid, string msg)
        {
            var service = new CustProjectService(Repository);
            service.SendToNextStep(id, todoby, desnodeid, msg, CurrentUser.UserId);
            return Content("hello");
            //return RedirectToAction(MVC.Project.TodoList());
        }

        /// <summary>
        /// 归档内部工程
        /// </summary>
        /// <param name="id"></param>
        /// <returns></returns>
        [HttpPost]
        public virtual ActionResult ArchieveProject(int id)
        {
            var service = new CustProjectService(Repository);
            service.FinishProject(id, CurrentUser.UserId);
            return Content("工程已经归档");
            //return RedirectToAction(MVC.Project.TodoList());
        }

        public virtual ActionResult GetActionUser(int tasknodeid)
        {
            var tasknode = Repository.Single<TaskNode>(x => x.TaskId == tasknodeid);
            return PartialView(MVC.CustomerProject.Views._DropListUser, tasknode.UserGroup.Users.ToList());
        }

        /// <summary>
        /// 用户组成员登录后，选择将任务箱中的项目标记为本人所要处理的项目任务
        /// </summary>
        /// <param name="prjid"></param>
        /// <returns></returns>
        
        public virtual ActionResult RecieveProject(int prjid)
        {
            var service = new CustProjectService(Repository);
            service.RecieveProject(prjid, CurrentUser.UserId);
            return RedirectToAction(MVC.CustomerProject.GroupTodoList());
        }


         [HttpPost]
         public virtual ActionResult ReturnProject(int prjid)
         {
             var service = new CustProjectService(Repository);
             service.ReturnProject(prjid, CurrentUser.UserId);
             if (Request.IsAjaxRequest())
             {
                 return Json(true);
             }
             return RedirectToAction(MVC.CustomerProject.GroupTodoList());
         }


        /// <summary>
        /// 把个人任务箱中的内部工程取消。
        /// </summary>
        /// <param name="prjid"></param>
        /// <returns></returns>
        [HttpPost]
        public virtual ActionResult CancelProject(int prjid)
        {
            var tocancel = Repository.Single<CustomerProject>(x => x.ID == prjid);
            tocancel.DisContinued = true;
            Repository.Save();
            return Content("工程已经取消");
        }

        /// <summary>
        /// 恢复取消的工程到个人任务箱
        /// </summary>
        /// <param name="prjid"></param>
        /// <returns></returns>
         [HttpPost]
        public virtual ActionResult ReturnCanceledProject(int prjid)
        {
            var toreturn = Repository.Single<CustomerProject>(x => x.ID == prjid);
            toreturn.DisContinued = false;
            Repository.Save();
            if (Request.IsAjaxRequest())
            {
                return Json(true);
            }
            return Content("取消的工程已经恢复");
        }

        #endregion

        #region 内部工程主要设备

        public virtual ActionResult CustPrjEquips(int id)
        {
            var project = Repository.Single<CustomerProject>(x => x.ID == id);
            var log = project.FlowLogs.Where(x => x.IsActive).FirstOrDefault();
            if (log != null)
                ViewBag.IsEquipsProcessing = log.TaskNode.ProcessGroup == CurrentUser.UserGroup.GroupId &&
                                             log.TaskNode.StructureNo.Trim() == "2";
            else ViewBag.IsEquipsProcessing = false;
            return View(project);
        }

        public virtual ActionResult GetEquips(int id)
        {
            var equips = Repository.All<CustomerEquip>().Where(x => x.CustomerProjectID == id).ToList();
            return PartialView(MVC.CustomerProject.Views.EquipList, equips);
        }

        // Get createnew order form
        public virtual ActionResult AddEquip(int id)
        {
            return PartialView(new CustEquipModel
                                {
                                    CreatedBy = User.Identity.Name,
                                    Quantity = 1,
                                    CreatedOn = DateTime.Now,
                                    CustomerProjectId = id
                                });
        }

        [HttpPost]
        public virtual ActionResult AddEquip(int id, CustEquipModel equip)
        {
            if (ModelState.IsValid)
            {
                var newequip = new CustomerEquip();
                TryUpdateModel(newequip);
                newequip.CustomerProjectID = id;
                Repository.Add(newequip);
                Repository.Save();
                return null;
            }
            return Content("出错了");
        }

        public virtual ActionResult EditEquip(int id)
        {
            var equip = Repository.Single<CustomerEquip>(x => x.ID == id);
            var viewmodel = Mapper.Map<CustomerEquip, CustEquipModel>(equip);
            return PartialView(viewmodel);
        }

        [HttpPost]
        public virtual ActionResult EditEquip(int id, CustEquipModel equip)
        {
            var tosave = Repository.Single<CustomerEquip>(x => x.ID == id);
            if (ModelState.IsValid)
            {
                TryUpdateModel(tosave);
                Repository.Save();
                return null;
            }
            return Content("出错了");
        }

        [HttpPost]
        public virtual ActionResult DeleteEquip(int id)
        {
            var todel = Repository.Single<CustomerEquip>(x => x.ID == id);
            Repository.Delete(todel);
            Repository.Save();
            return Content("已删除");
        }

        public virtual ActionResult LookUpEquip(string term)
        {
            var areas = Repository.All<CustomerEquip>().Select(x => x.EquipName).Where(x => x.Contains(term)).Distinct()
                .Select(x => x.Trim());
            return Json(areas, JsonRequestBehavior.AllowGet);
        }

        public virtual ActionResult LookUpSupply(string term)
        {
            var areas = Repository.All<CustomerEquip>().Select(x => x.SupplyBy).Where(x => x.Contains(term)).Distinct()
                .Select(x => x.Trim());
            return Json(areas, JsonRequestBehavior.AllowGet);
        }

         [OutputCache(Duration = 3600)]
        public virtual ActionResult LookUpSelectedType(string term)
        {
            var areas = Repository.All<CustomerEquip>().Select(x => x.SelectedType).Where(x => x.Contains(term)).
                Distinct()
                .Select(x => x.Trim());
            return Json(areas, JsonRequestBehavior.AllowGet);
        }

        //  List the customer project equipment companys 
        [OutputCache(Duration = 1800)]
        public virtual ActionResult CustomerEqCompany()
        {
            var supplyCompanies = Repository.All<CustomerEquip>();

            var companies = from c in supplyCompanies
                            group c by c.SupplyBy.Trim()
                            into g
                            select new SupplyCompany() {Name =g.Key};

            ViewBag.MonthSaleChart = 
                FusionCharts.RenderChart(Links.Content.Charts.Column3D_swf + "?ChartNoDataText=请点击左侧列表中的设备厂商查看月度统计图.",
               "", "<chart></chart>", "MonthSales", "100%", "300", false, true);
            ViewBag.Year = DateTime.Now.Year;
            return View(companies.ToList());
        }

        [OutputCache(Duration = 1800)]
        public virtual ActionResult GetEqYearSummary(int year)
        {
            var strSet = new StringBuilder();
           

            var orders =  Repository.All<CustomerEquip>()
                .Where(x => x.CreatedOn.HasValue && x.CreatedOn.Value.Year == year);

            if (!orders.Any()) return null;

            var yearsales = from o in orders
                            group o by o.SupplyBy.Trim()
                                into g
                                select new { CompanyName = g.Key, Value = g.Count() };

            foreach (var item in yearsales)
            {
                strSet.Append(string.Format(XmlSetYearSum, item.CompanyName, item.Value));
            }

            var xmlstr = string.Format(XmlYearSum, string.Format("设备供应商{0}年度工程数量监控统计", year), strSet);
            var result = FusionCharts.RenderChart(Links.Content.Charts.Pie3D_swf, "", xmlstr, "eqyearsum", "100%", "370", false, true);
            return Content(result);
        }

        // ajax get  return the month table list of customer project by company 
        public virtual ActionResult GetMonthDetailByCompany(int year, int month, string companyname)
        {
            var orders = Repository.All<CustomerEquip>()
                .Where(x => x.SupplyBy.Trim() == companyname &&
                    x.CreatedOn.Value.Year.Equals(year) && 
                    x.CreatedOn.Value.Month.Equals(month))
                .ToList();
            ViewBag.CompanyName =companyname;
            ViewBag.Month = month;
            return PartialView(MVC.CustomerProject.Views._EqList,orders);
        }

        // 用户内部工程设备厂商月份图，12个月的统计数据，以Fusionchart 的Flash 数据返回
        public virtual ActionResult GetMonthSummaryByCompany(string companyname, int year)
        {
            var orders = (from o in Repository.All<CustomerEquip>()
                          where o.SupplyBy.Trim() == companyname &&  o.CreatedOn.HasValue && o.CreatedOn.Value.Year == year
                          select o).ToList();
            if (!orders.Any()) return null;
            var monthsales = from o in orders
                             group o by o.CreatedOn.Value.Month
                                 into g
                                 select new { Month = g.Key, Value = g.Count()  };
            monthsales = monthsales.OrderBy(x => x.Month);
            var strSet = new StringBuilder();
            foreach (var order in monthsales)
            {
                strSet.Append(string.Format(XmlSetstrMothSale, order.Month, order.Value,order.Month));
            }
            var result = string.Format(XmlstrMothSale, strSet, string.Format("设备供应商"+companyname+"{0}年工程业务月度统计", year));
            return Content(result);
        }

        #endregion

        #region 内部工程施工单位信息管理

        public virtual ActionResult Constructinfo(int id)
        {
            var prj = Repository.SingleOrDefault<CustomerProject>(x => x.ID == id);
            var construct = Repository.FirstOrDefault<CustomerConstruct>(x => x.CustomerProjectID == id) ??
                            new CustomerConstruct
                                {
                                    CustomerProjectID = id,
                                    CreatedOn = DateTime.Now,
                                    CreatedBy = User.Identity.Name,
                                    CustomerProject = prj
                                };
            var log = prj.FlowLogs.Where(x => x.IsActive).FirstOrDefault();
            if (log != null)
                ViewBag.IsEquipsProcessing = log.TaskNode.ProcessGroup == CurrentUser.UserGroup.GroupId &&
                                             log.TaskNode.StructureNo.Trim() == "2";
            else ViewBag.IsEquipsProcessing = false;
            return View(Mapper.Map<CustomerConstruct, CustConstructModel>(construct));
        }

        [HttpPost]
        public virtual ActionResult SaveConstructinfo(int prjid, CustConstructModel tosave)
        {
            if (ModelState.IsValid)
            {
                var custconstruct = Repository.SingleOrDefault<CustomerConstruct>(x => x.CustomerProjectID == prjid);
                if (custconstruct == null)
                {
                    custconstruct = new CustomerConstruct {CustomerProjectID = prjid};
                    TryUpdateModel(custconstruct);
                    Repository.Add(custconstruct);
                    Repository.Save();
                    return null;
                }
                TryUpdateModel(custconstruct);
                Repository.Save();
                return null;
            }
            return Content("出错了");
        }

        public virtual ActionResult LookUpConstructBy(string term)
        {
            var areas = Repository.All<CustomerConstruct>().Select(x => x.ConstructedBy).Where(x => x.Contains(term)).
                Distinct()
                .Select(x => x.Trim());
            return Json(areas, JsonRequestBehavior.AllowGet);
        }

        public virtual ActionResult LookUpConstructType(string term)
        {
            var areas = Repository.All<CustomerConstruct>().Select(x => x.SelectedType).Where(x => x.Contains(term)).
                Distinct()
                .Select(x => x.Trim());
            return Json(areas, JsonRequestBehavior.AllowGet);
        }

        public virtual ActionResult LookUpDesignedBy(string term)
        {
            var areas = Repository.All<CustomerConstruct>().Select(x => x.DesignedBy).Where(x => x.Contains(term)).
                Distinct()
                .Select(x => x.Trim());
            return Json(areas, JsonRequestBehavior.AllowGet);
        }

        //  List the customer project construction companys 
         [OutputCache(Duration = 1800)]
        public virtual ActionResult CustomerConstructCompany()
        {
            var Companies = from o in  Repository.All<CustomerConstruct>()
                            group o by o.ConstructedBy.Trim() into  g
                            select new ContructCompany() { Name = g.Key };
            ViewBag.MonthSaleChart =
                FusionCharts.RenderChart(Links.Content.Charts.Column3D_swf + "?ChartNoDataText=请点击左侧列表中的施工单位查看月度统计图.",
               "", "<chart></chart>", "MonthSales", "100%", "300", false, true);
            ViewBag.Year = DateTime.Now.Year;
            return View(Companies.ToList());
        }

         [OutputCache(Duration = 1800)]
        public virtual ActionResult GetConstructYearSummary(int year)
        {
            var strSet = new StringBuilder();
             var orders = (from o in Repository.All<CustomerConstruct>()
                          where o.CreatedOn.HasValue && o.CreatedOn.Value.Year == year
                          select o).ToList();
            if (!orders.Any()) return null;

            var yearsales = from o in orders
                             group o by o.ConstructedBy.Trim()
                                 into g
                                 select new { CompanyName = g.Key, Value = g.Count() };

            foreach (var item in yearsales)
            {
                strSet.Append(string.Format(XmlSetYearSum, item.CompanyName, item.Value));}

            var xmlstr = string.Format(XmlYearSum, string.Format("施工承包商{0}年度工程数量监控统计", year), strSet);
            var result = FusionCharts.RenderChart(Links.Content.Charts.Pie3D_swf, "", xmlstr, "constructyearsum", "100%", "370", false, true);
            return Content(result);
        }

        // 用户内部工程施工承包商月份图，12个月的统计数据，以Fusionchart 的Flash 数据返回
         [OutputCache(Duration = 1800)]
        public virtual ActionResult GetMonthContructSummaryByCompany(string companyname, int year)
        {

            var orders = (from o in Repository.All<CustomerConstruct>()
                          where o.ConstructedBy.Trim() == companyname && o.CreatedOn.HasValue && o.CreatedOn.Value.Year == year
                          select o).ToList();
            if (!orders.Any()) return null;
            var monthsales = from o in orders
                             group o by o.CreatedOn.Value.Month
                                 into g
                                 select new { Month = g.Key, Value = g.Count() };
            monthsales = monthsales.OrderBy(x => x.Month);
            var strSet = new StringBuilder();
            foreach (var order in monthsales)
            {
                strSet.Append(string.Format(XmlSetstrMothSale, order.Month, order.Value, order.Month));
            }
            var result = string.Format(XmlstrMothSale, strSet, string.Format("施工承包商" + companyname + "{0}年工程施工业务月度统计", year));
            return Content(result);
        }

        // ajax get  return the month table list of customer project by company 
        public virtual ActionResult GetMonthConstructDetailByCompany(int year, int month, string companyname)
        {
            var orders = Repository.All<CustomerConstruct>()
                .Where(x => x.ConstructedBy.Trim() == companyname &&
                    x.CreatedOn.Value.Year.Equals(year) &&
                    x.CreatedOn.Value.Month.Equals(month))
                .ToList();
            ViewBag.CompanyName = companyname;
            ViewBag.Month = month;
            return PartialView(MVC.CustomerProject.Views._ConstructList, orders);
        }

        #endregion
    }
}
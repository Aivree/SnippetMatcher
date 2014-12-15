using System;
using System.Data;
using System.Web.Mvc;
using BwB.Core.UI;
using BwB.Core.UI.ActionFilters;
using DocGen.Domain;
using DocGen.Domain.Entities;
using DocGen.Infrastructure.Context;
using DocGen.Models;
using System.Linq;
using Kendo.Mvc.Extensions;
using Kendo.Mvc.UI;

namespace DocGen.Controllers
{
    [Authorize]
    public class HomeController : Controller
    {
        private readonly DocGenContext _db = new DocGenContext();

        public ActionResult Default()
        {
            ViewBag.Title = "Home";
            return View();
        }

        [HttpPost]
        public ActionResult Default(InvoiceViewModel viewModel)
        {
            var redirectResult = RedirectToAction("Add");
            return Json(new Message { Text = this.GetUrlFrom(redirectResult) }, JsonRequestBehavior.AllowGet);
        }

        public ActionResult Add()
        {
            var viewModel = new InvoiceViewModel { Invoice = new Invoice() };

            _db.Invoices.Add(viewModel.Invoice);
            _db.SaveChanges();

            viewModel.Invoice.FreightCharges = new FreightCharges();
            viewModel.Invoice.Shipper = new Shipper();
            viewModel.Invoice.ShipperAddress = new Address();
            viewModel.Invoice.Consignee = new Consignee();
            viewModel.Invoice.ConsigneeAddress = new Address();
            viewModel.Invoice.Carrier = new Carrier();
            viewModel.Invoice.CarrierAddress = new Address();
            viewModel.Invoice.Broker = new Broker();
            viewModel.Invoice.FreightCharges = new FreightCharges();

            viewModel.Invoice.ShipmentControlNumberReference = _db.Invoices.Max(item => item.ShipmentControlNumberReference) + 1;
            viewModel.Invoice.ShipmentControlNumber = "000" + viewModel.Invoice.ShipmentControlNumberReference;

            return View(viewModel);
        }

        [HttpPost]
        [HandleModelStateException]
        public ActionResult Add(InvoiceViewModel viewModel)
        {
            if (viewModel == null || viewModel.Invoice == null) throw new ModelStateException("Some errors occurred please start again");
            if (!viewModel.Invoice.Shipper.Id.HasValue) throw new ModelStateException("Please select Shipper");
            if (!viewModel.Invoice.Consignee.Id.HasValue) throw new ModelStateException("Please select Consignee");
            if (!viewModel.Invoice.Carrier.Id.HasValue) throw new ModelStateException("Please select Carrier");
            if (!viewModel.Invoice.Broker.Id.HasValue) throw new ModelStateException("Please select Broker");

            if (!viewModel.Invoice.ShipperAddress.Id.HasValue) throw new ModelStateException("Please select Shipper Address");
            if (!viewModel.Invoice.ConsigneeAddress.Id.HasValue) throw new ModelStateException("Please select Consignee Address");
            if (!viewModel.Invoice.Carrier.Id.HasValue) throw new ModelStateException("Please select Carrier Address");

            if (!_db.InvoiceItems.Any(item => item.InvoiceId == viewModel.Invoice.Id)) throw new ModelStateException("Please Add Invoice Item");

            viewModel.Invoice.Shipper = _db.Shippers.FirstOrDefault(item => item.Id == viewModel.Invoice.Shipper.Id);
            if (viewModel.Invoice.Shipper == null) throw new ModelStateException("Shipper doesn't exist please select again");

            viewModel.Invoice.Carrier = _db.Carriers.FirstOrDefault(item => item.Id == viewModel.Invoice.Carrier.Id);
            if (viewModel.Invoice.Carrier == null) throw new ModelStateException("Carrier doesn't exist please select again");

            viewModel.Invoice.Consignee = _db.Consignees.FirstOrDefault(item => item.Id == viewModel.Invoice.Consignee.Id);
            viewModel.Invoice.Broker = _db.Brokers.FirstOrDefault(item => item.Id == viewModel.Invoice.Broker.Id);

            viewModel.Invoice.ShipperAddress = _db.Addresses.FirstOrDefault(item => item.Id == viewModel.Invoice.ShipperAddress.Id);
            viewModel.Invoice.ConsigneeAddress = _db.Addresses.FirstOrDefault(item => item.Id == viewModel.Invoice.ConsigneeAddress.Id);
            viewModel.Invoice.CarrierAddress = _db.Addresses.FirstOrDefault(item => item.Id == viewModel.Invoice.CarrierAddress.Id);

            viewModel.Invoice.ShipmentControlNumber = string.Concat(viewModel.Invoice.Carrier.Code, " ", viewModel.Invoice.Shipper.Code, viewModel.Invoice.ShipmentControlNumber);


            var dbInvoice = _db.Invoices.FirstOrDefault(item => item.Id == viewModel.Invoice.Id);
            if (dbInvoice != null)
            {
                _db.Entry(dbInvoice).CurrentValues.SetValues(viewModel.Invoice);
                dbInvoice.Shipper = viewModel.Invoice.Shipper;
                dbInvoice.Consignee = viewModel.Invoice.Consignee;
                dbInvoice.Carrier = viewModel.Invoice.Carrier;
                dbInvoice.Broker = viewModel.Invoice.Broker;

                dbInvoice.ShipperAddress = viewModel.Invoice.ShipperAddress;
                dbInvoice.ConsigneeAddress = viewModel.Invoice.ConsigneeAddress;
                dbInvoice.CarrierAddress = viewModel.Invoice.CarrierAddress;

                dbInvoice.FreightCharges = viewModel.Invoice.FreightCharges;

                dbInvoice.Created = DateTime.UtcNow;
                _db.SaveChanges();
            }

            var redirectResult = RedirectToAction("Default");
            return Json(new Message { Text = this.GetUrlFrom(redirectResult) }, JsonRequestBehavior.AllowGet);
        }

        [HttpPost]
        public ActionResult CancelAdd(InvoiceViewModel viewModel)
        {
            if (viewModel != null && viewModel.Invoice != null && viewModel.Invoice.Id.HasValue)
            {
                _db.Invoices.Remove(_db.Invoices.FirstOrDefault(item => item.Id == viewModel.Invoice.Id));
                foreach (var invoiceItem in _db.InvoiceItems.Where(item => item.InvoiceId == viewModel.Invoice.Id).ToList())
                {
                    _db.InvoiceItems.Remove(invoiceItem);
                }
                _db.SaveChanges();
            }
            var redirectResult = RedirectToAction("Default");
            return Json(new Message { Text = this.GetUrlFrom(redirectResult) }, JsonRequestBehavior.AllowGet);
        }

        public ActionResult Get([DataSourceRequest] DataSourceRequest dsRequest)
        {
            var result = _db.Invoices
                .Include("Status").Include("Shipper").Include("ShipperAddress").Include("Carrier").Include("CarrierAddress").Include("Consignee").Include("ConsigneeAddress").Include("Broker").Include("FreightCharges")
                .Where(item => item.ShipmentControlNumberReference != 0).OrderByDescending(item => item.Created).ToList();
            foreach (var invoice in result.Where(invoice => invoice.Created.HasValue))
            {
                invoice.Created = invoice.Created.Value.ToLocalTime();
            }
            return Json(result.ToDataSourceResult(dsRequest), JsonRequestBehavior.AllowGet);
        }

        public ActionResult Delete([DataSourceRequest] DataSourceRequest dsRequest, Invoice invoice)
        {
            if (invoice != null && invoice.Id.HasValue)
            {
                _db.Invoices.Remove(_db.Invoices.FirstOrDefault(item => item.Id == invoice.Id));
                foreach (var invoiceItem in _db.InvoiceItems.Where(item => item.InvoiceId == invoice.Id).ToList())
                {
                    _db.InvoiceItems.Remove(invoiceItem);
                }
                _db.SaveChanges();
            }
            return Json(true, JsonRequestBehavior.AllowGet);
        }

        public ActionResult Send(string id)
        {
            if (string.IsNullOrEmpty(id)) return null;
            Guid guidId;
            if (Guid.TryParse(id, out guidId))
            {
                var invoice = _db.Invoices.Include("Status").Single(item => item.Id == guidId);
                if (invoice != null)
                {
                    var sentStatus = _db.Statuses.Single(item => string.Compare(item.Value, "Sent") == 0);
                    if (sentStatus != null && invoice.Status.Id != sentStatus.Id)
                    {
                        invoice.StatusId = sentStatus.Id;
                        invoice.Status = null;
                        _db.Entry(invoice).State = EntityState.Modified;
                        _db.SaveChanges();
                    }
                }
            }
            return Json(true, JsonRequestBehavior.AllowGet);
        }

        public ActionResult Cancel(string id)
        {
            if (string.IsNullOrEmpty(id)) return null;
            Guid guidId;
            if (Guid.TryParse(id, out guidId))
            {
                var invoice = _db.Invoices.Include("Status").Single(item => item.Id == guidId);
                if (invoice != null)
                {
                    var cancelStatus = _db.Statuses.Single(item => string.Compare(item.Value, "Canceled") == 0);
                    if (cancelStatus != null && invoice.Status.Id != cancelStatus.Id)
                    {
                        invoice.StatusId = cancelStatus.Id;
                        invoice.Status = null;
                        _db.Entry(invoice).State = EntityState.Modified;
                        _db.SaveChanges();
                    }
                }
            }
            return Json(true, JsonRequestBehavior.AllowGet);
        }

        //        public ActionResult Report(string id)
        //        {
        //            if (string.IsNullOrEmpty(id)) return null;
        //            var reportDocument = new ReportDocument();
        //            reportDocument.Load(Server.MapPath("~/Reports/Report.rpt"));
        //            reportDocument.SetParameterValue("@invoiceId", id);

        //            reportDocument.SetDatabaseLogon("DocGen1969", "Radu1969!");

        //            var stream = reportDocument.ExportToStream(CrystalDecisions.Shared.ExportFormatType.PortableDocFormat);
        //            return File(stream, "application/pdf");
        //        }
    }
}

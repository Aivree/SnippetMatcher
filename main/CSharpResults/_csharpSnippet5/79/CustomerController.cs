using System;
using System.Collections.Generic;
using System.Linq;
using System.Web.Mvc;
using Kendo.Mvc.Grid.CRUD.Models;
using PlumRunDomain;
using PlumRunModel;
using WebUI.ViewModels.Customer;
using System.Net;
using System.Data.Entity;

namespace WebUI.Controllers
{
    public class CustomerController : Controller
    {
        private PREntities db = new PREntities();

        public ActionResult Index()
        {
            return View();
        }

        public ActionResult Read(int take, int skip, IEnumerable<Sort> sort, Kendo.Mvc.Grid.CRUD.Models.Filter filter)
        {
            {
                var result = db.Customers
                    .OrderBy(c => c.CustomerID) // EF requires ordered IQueryable in order to do paging
                    // Use a view model to avoid serializing internal Entity Framework properties as JSON
                    .Select(c => new CustomerSearch
                    {
                        CustomerID = c.CustomerID,
                        AROrgName = c.AROrgName,
                        ARCity = c.ARCity,
                        //ARStateProvID = c.ARStateProvID,
                        //CreatedDate = c.CreatedDate ,
                        State = c.StateProvince.name
                    })
                    .ToDataSourceResult(take, skip, sort, filter);
                return Json(result);
            }
        }

        public ActionResult Create()
        {
            ViewModels.CustomerCreateVM vm = new ViewModels.CustomerCreateVM();
            return View(vm);
        }

        [HttpPost]
        //[ValidateAntiForgeryToken]
        public ActionResult Create(ViewModels.CustomerCreateVM newCustomer,string button)
        {
            if (button == "clear")
            {
                return RedirectToAction("Create");
            }
            else if (button == "cancel")
            {
                return RedirectToAction("Index");
            }
            else if (button == "add")
            {
                return RedirectToAction("Create");
            }
            else if (ModelState.IsValid)
            {
                Customer customer = new Customer();
                customer.ARAddress1 = newCustomer.CustomerCreate.ARAddress1;
                customer.ARAddress2 = newCustomer.CustomerCreate.ARAddress2;
                customer.ARAddress3 = newCustomer.CustomerCreate.ARAddress3;
                customer.ARCity = newCustomer.CustomerCreate.ARCity;
                customer.AROrgName = newCustomer.CustomerCreate.AROrgName;
                customer.ARPostalCode = newCustomer.CustomerCreate.ARPostalCode;
                customer.ARStateProvID = newCustomer.stateDropDown.SelectedID;
                customer.CountryID = 169;                                       //TODO Get CountryID from model
                customer.CreatedDate = System.DateTime.Now;
                customer.PriceID = newCustomer.pricesDropDown.SelectedID;

                db.Customers.Add(customer);
                db.SaveChanges();
            }                
            return RedirectToAction("Index");
        }

        // GET: /Customer/Delete/5
        public ActionResult Delete(int? id)
        {
            if (id == null)
            {
                return new HttpStatusCodeResult(HttpStatusCode.BadRequest);
            }
            
            Customer customer = db.Customers.Find(id);

            if (customer == null)
            {
                return HttpNotFound();
            }

            CustomerDelete VM = new CustomerDelete();

            VM.CustomerID = customer.CustomerID;
            VM.AROrgName = customer.AROrgName;
            VM.ARAddress1 = customer.ARAddress1;
            VM.ARAddress2 = customer.ARAddress2;
            VM.ARAddress3 = customer.ARAddress3;
            VM.ARCity = customer.ARCity;
            VM.StateProvName = customer.StateProvince.name;
            VM.ARPostalCode = customer.ARPostalCode;

            return View(VM);
        }

        // POST: /Customer/Delete/5
        [HttpPost, ActionName("Delete")]
        [ValidateAntiForgeryToken]
        public ActionResult DeleteConfirmed(int id, string button)
        {
            if (button == "cancel")
            {
                return RedirectToAction("Index");
            }
            
            Customer customer = db.Customers.Find(id);
            db.Customers.Remove(customer);
            db.SaveChanges();
            return RedirectToAction("Index");
        }

        protected override void Dispose(bool disposing)
        {
            if (disposing)
            {
                db.Dispose();
            }
            base.Dispose(disposing);
        }

        // GET: /Customer/Edit/5 
        public ActionResult Edit(int id, string button)
        {
            Customer customer = db.Customers.Find(id);
            if (customer == null)
            {
                return HttpNotFound();
            }
            CustomerEditVM VM = new CustomerEditVM(customer.PriceID, customer.ARStateProvID, customer.CountryID);

            VM.customerEdit.ARAddress1 = customer.ARAddress1;
            VM.customerEdit.ARAddress2 = customer.ARAddress2;
            VM.customerEdit.ARAddress3 = customer.ARAddress3;
            VM.customerEdit.ARCity = customer.ARCity;
            VM.customerEdit.ARStateProvID = customer.ARStateProvID;
            VM.customerEdit.AROrgName = customer.AROrgName;
            VM.customerEdit.ARPostalCode = customer.ARPostalCode;
            VM.customerEdit.CountryID = customer.CountryID;
            VM.customerEdit.CustomerID = customer.CustomerID;
            VM.customerEdit.PriceID = customer.PriceID;
            VM.customerEdit.ModifiedByPersonID = 0;                //TODO Get PersonID from code
            return View(VM);
        }

        // POST: /Customer/Edit/5
        [HttpPost]
        [ValidateAntiForgeryToken]                        //TODO REMOVED BECAUSE DELETE BUTTON THREW ERROR. NEED TO GET DELETE POST TO SEND VALIDATION TOKEN????? **********
        public ActionResult Edit (
            int id,
            CustomerEdit customerEdit,
            ViewModels.DropDownLists.StateDropDown stateDropDown,
            ViewModels.DropDownLists.CountryDropDown countryDropDown,
            ViewModels.DropDownLists.PricesDropDown pricesDropDown,
            string button)
        {
            if (button == "cancel")
            {
                return RedirectToAction("Index");
            }

            else if (button == "delete")
            {
                return RedirectToAction("Delete", new { id = id });
            }

            if (ModelState.IsValid)
            {
                Customer updatedCustomer = db.Customers.Find(id);

                updatedCustomer.ModifiedDate = System.DateTime.Now;
                updatedCustomer.ARStateProvID = stateDropDown.SelectedID;
                updatedCustomer.CountryID = countryDropDown.SelectedID;
                updatedCustomer.PriceID = pricesDropDown.SelectedID;
                updatedCustomer.AROrgName = customerEdit.AROrgName;
                updatedCustomer.ARAddress1 = customerEdit.ARAddress1;
                updatedCustomer.ARAddress2 = customerEdit.ARAddress2;
                updatedCustomer.ARAddress3 = customerEdit.ARAddress3;
                updatedCustomer.ARCity = customerEdit.ARCity;
                updatedCustomer.ARPostalCode = customerEdit.ARPostalCode;
                db.Entry(updatedCustomer).State = EntityState.Modified;
                // TODO Populate ModifiedByPersonID in code
               db.SaveChanges();
            }
            return RedirectToAction("Index");
        }

    }
}
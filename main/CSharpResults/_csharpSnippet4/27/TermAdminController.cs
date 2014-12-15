using System;
using System.Collections.Generic;
using System.Linq;
using System.Reflection;
using System.Web.Mvc;
using Contrib.Taxonomies;
using Contrib.Taxonomies.Helpers;
using Contrib.Taxonomies.Models;
using Contrib.Taxonomies.Services;
using Contrib.Taxonomies.ViewModels;
using TaxonomyImport.Services;
using Orchard;
using Orchard.ContentManagement;
using Orchard.ContentManagement.Handlers;
using Orchard.ContentManagement.Records;
using Orchard.Data;
using Orchard.Localization;
using Orchard.Logging;
using Orchard.UI.Admin;
using Orchard.UI.Notify;

namespace TaxonomyImport.Controllers
{
    [ValidateInput(false), Admin]
    public class TermAdminController : Controller, IUpdateModel {
        private readonly ITaxonomyService _taxonomyService;
        private readonly ITaxonomyImportService _taxonomyImportService;


        public TermAdminController(
            IOrchardServices services,
            ITaxonomyService taxonomyService,
            ITaxonomyImportService taxonomyImportService) {
            Services = services;
            _taxonomyService = taxonomyService;
            _taxonomyImportService = taxonomyImportService;
            T = NullLocalizer.Instance;
        }

        public IOrchardServices Services { get; set; }

        public Localizer T { get; set; }

        public ActionResult Index(int taxonomyId) {
            var taxonomy = _taxonomyService.GetTaxonomy(taxonomyId);
            var terms = _taxonomyImportService.GetDraftTerms(taxonomyId);
            var entries = terms.Select(term => term.CreateTermEntry()).ToList();
            var model = new TermAdminIndexViewModel {Terms = entries, Taxonomy = taxonomy, TaxonomyId = taxonomyId};
            return View(model);
        }



        [HttpPost, MultiButton(Name = "action", Argument = "Publish")]
        public ActionResult Publish(int taxonomyId) {
            if (!Services.Authorizer.Authorize(Permissions.ManageTaxonomies, T("Couldn't publish.")))
                return new HttpUnauthorizedResult();


            // delete published 
            foreach (var term in _taxonomyService.GetTerms(taxonomyId)) {
                _taxonomyService.DeleteTerm(term);
            }

            // publish drafts
            foreach (var draftTerm in _taxonomyImportService.GetDraftTerms(taxonomyId)) {
                Services.ContentManager.Publish(draftTerm.As<ContentItem>());
            }


            Services.Notifier.Information(T("Terms published succesfully."));

            return RedirectToAction("Index", "Admin");
        }

        [HttpPost, MultiButton(Name = "action", Argument = "Reject")]
        public ActionResult Reject(int taxonomyId) {
            if (!Services.Authorizer.Authorize(Permissions.ManageTaxonomies, T("Couldn't reject.")))
                return new HttpUnauthorizedResult();

            // delete drafts
            foreach (var draftTerm in _taxonomyImportService.GetDraftTerms(taxonomyId)) {
                _taxonomyImportService.DeleteDraftTerm(draftTerm.ContentItem);
            }

            Services.Notifier.Information(T("Draft terms ignored."));

            return RedirectToAction("Index", "Admin");
        }

        bool IUpdateModel.TryUpdateModel<TModel>(TModel model, string prefix, string[] includeProperties, string[] excludeProperties) {
            return TryUpdateModel(model, prefix, includeProperties, excludeProperties);
        }

        void IUpdateModel.AddModelError(string key, LocalizedString errorMessage) {
            ModelState.AddModelError(key, errorMessage.ToString());
        }
    
   

        [AttributeUsage(AttributeTargets.Method, AllowMultiple = false, Inherited = true)]
        public class MultiButtonAttribute : ActionNameSelectorAttribute
        {
            public string Name { get; set; }
            public string Argument { get; set; }

            public override bool IsValidName(ControllerContext controllerContext, string actionName, MethodInfo methodInfo)
            {
                bool isValidName = false;
                string keyValue = string.Format("{0}:{1}", Name, Argument);
                var value = controllerContext.Controller.ValueProvider.GetValue(keyValue);
                if (value != null)
                {
                    controllerContext.Controller.ControllerContext.RouteData.Values[Name] = Argument;
                    isValidName = true;
                }

                return isValidName;
            }
        }




    
    }
}
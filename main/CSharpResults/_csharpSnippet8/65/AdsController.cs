using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;
using System.Web.Mvc;
using Meniu.Models;
using Meniu.Models.ViewModels;
using Meniu.Services;
using Meniu.Models.Repositories;

namespace Meniu.Controllers
{
    [RoutePrefix("publicidad")]
    public class AdsController : BaseController
    {
        private AdsService service;
        private MeniuService meniuService;
        private LocationRepository locationRepository;

        public AdsController(AdsService aService, LocationRepository locationRepo, MeniuService mService)
        {
            locationRepository = locationRepo;
            service = aService;
            meniuService = mService;
        }
        //
        // GET: /Ads/

        [Route("administrar-publicidad")]
        public ActionResult ManageAds()
        {
            ViewBag.CityId = meniuService.GetCitiesForDropDown();
            var enumValues = Enum.GetValues(typeof(Meniu.DataTypes.Enums.Zones))
                .Cast<Meniu.DataTypes.Enums.Zones>()
                .Select(e => new { Value = e.ToString(), Text = e.ToString() }).ToList();

            ViewBag.Zone = new SelectList(enumValues, "Value", "Text");
            return View();
        }

        [HttpPost]
        public ActionResult ManageAds(AdsViewModel model)
        {
            if (!ModelState.IsValid)
            {
                ViewBag.CityId = meniuService.GetCitiesForDropDown();
                var enumValues = Enum.GetValues(typeof(Meniu.DataTypes.Enums.Zones))
                    .Cast<Meniu.DataTypes.Enums.Zones>()
                    .Select(e => new { Value = e.ToString(), Text = e.ToString() }).ToList();

                ViewBag.Zone = new SelectList(enumValues, "Value", "Text");
                return View(model);
            }
            if (!ImageService.ValidateImageForZone(model.Zone.ToString(), model.Image))
            {
                ViewBag.CityId = meniuService.GetCitiesForDropDown();
                var enumValues = Enum.GetValues(typeof(Meniu.DataTypes.Enums.Zones))
                    .Cast<Meniu.DataTypes.Enums.Zones>()
                    .Select(e => new { Value = e.ToString(), Text = e.ToString() }).ToList();

                ViewBag.Zone = new SelectList(enumValues, "Value", "Text");
                ModelState.AddModelError("", "La imagen estaba en formato invalido.");
                return View(model);
            }

            if (!service.ValidateAd(model.Zone.ToString(), model.CityId, model.StartsOn, model.ToBeConcluded))
            {
                ViewBag.CityId = meniuService.GetCitiesForDropDown();
                var enumValues = Enum.GetValues(typeof(Meniu.DataTypes.Enums.Zones))
                    .Cast<Meniu.DataTypes.Enums.Zones>()
                    .Select(e => new { Value = e.ToString(), Text = e.ToString() }).ToList();

                ViewBag.Zone = new SelectList(enumValues, "Value", "Text");
                ModelState.AddModelError("", "Ya existe publicidad para esas fechas.");
                return View(model);
            }

            service.SaveAds(model);
            return View("_success");
        }

        public ActionResult AZone()
        {
            var city = GetCityFromCookie();
            var model = service.GetAds(city.Id, "A");
            return View(model);
        }

        public ActionResult BCDZone()
        {
            var city = GetCityFromCookie();
            var model = service.GetSidebarAds(city.Id);
            model.CityName = city.Name;
            return View(model);
        }

    }
}

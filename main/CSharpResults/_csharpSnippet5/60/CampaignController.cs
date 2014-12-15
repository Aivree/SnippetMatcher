using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;
using System.Web.Mvc;
using System.Web.Mvc.Ajax;
using EWS.Herald.Model;
using EWS.Herald.Model.Repositories;
using EWS.Herald.Services;
using EWS.Herald.Services.Messaging;
using EWS.Herald.Web.Helpers;

namespace EWS.Herald.Web.Controllers
{
    public class CampaignController : ControllerBase
    {
        private readonly ICampaignService campaignService;
        private readonly IMessagingService messagingService;
        private readonly ICalendarService calendarService;
        private readonly IProductRepository productRepository;
        private readonly IConfigurationService configurationService;
        private readonly ICanSave saver;

        public CampaignController(ICampaignService campaignService, IMessagingService messagingService, ICalendarService calendarService, 
            IProductRepository productRepository, IConfigurationService configurationService, ICanSave saver)
        {
            this.campaignService = campaignService;
            this.messagingService = messagingService;
            this.calendarService = calendarService;
            this.productRepository = productRepository;
            this.configurationService = configurationService;
            this.saver = saver;
        }

        [AcceptVerbs("GET")]
        [Authorize(Roles="agency")]
        public ActionResult Index()
        {
            return View("Index", campaignService.RetrieveAll().OrderByDescending(campaign => campaign.Created).ToList());
        }

        [AcceptVerbs("GET")]
        [Authorize(Roles="agency")]
        public ActionResult New()
        {
            return View("New");
        }

        [AcceptVerbs("POST")]
        [Authorize(Roles="agency")]
        public ActionResult New(string name, string subject, string content)
        {
            Campaign created = Create(name, subject, content);
            campaignService.Match(created);

            if (!created.IsValid) 
            {
                SetModelErrors(created, "Unable to create campaign.");
                return View("New");
            }
            saver.SaveAll();
            return RedirectToAction("Preview", new { campaignId = created.Id });
        }

        [AcceptVerbs(HttpVerbs.Get)]
        [Authorize(Roles="agency")]
        public ActionResult Preview(int campaignId)
        {
            return View("Preview", campaignService.RetrieveById(campaignId));
        }

        [AcceptVerbs(HttpVerbs.Get)]
        [Authorize(Roles="agency")]
        public ActionResult RemoveMessageItemAndPreview(int messageItemId, int campaignId)
        {
            MessageItem item = campaignService.RetrieveMessageItemById(messageItemId);
            string name = item.Product.Name;
            if (item.Message.HasBeenSent)
            {
                SetErrorMessage("Item '" + name + "' cannot be removed because the message has already been sent.");
                return RedirectToAction("Preview", new { campaignId = campaignId });
            }
            campaignService.MarkMessageItemForDeletion(item);
            saver.SaveAll();
            SetSuccessMessage("Item '" + name + "' has been removed.");
            return RedirectToAction("Preview", new { campaignId = campaignId });
        }

        [AcceptVerbs(HttpVerbs.Get)]
        [Authorize(Roles="agency")]
        public ActionResult RemoveMessageAndPreview(int messageId, int campaignId)
        {
            Message message = campaignService.RetrieveMessageById(messageId);
            string buyersName = message.BuyersFullName;
            if (message.HasBeenSent)
            {
                SetErrorMessage("The message for buyer '" + buyersName + "' cannot be removed because it has already been sent.");
                return RedirectToAction("Preview", new { campaignId = campaignId });
            }
            campaignService.MarkMessageForDeletion(message);
            saver.SaveAll();
            SetSuccessMessage("The message for buyer '" + buyersName + "' has been removed.");
            return RedirectToAction("Preview", new { campaignId = campaignId });
        }

        [AcceptVerbs(HttpVerbs.Get)]
        [Authorize(Roles="agency")]
        public ActionResult GenerateMessage(int messageId, int campaignId)
        {
            return Content(messagingService.GetMessageBody(campaignService.RetrieveMessageById(messageId)));
        }

        [AcceptVerbs(HttpVerbs.Get)]
        [Authorize(Roles="agency")]
        public ActionResult Send(int campaignId)
        {
            Campaign campaign = campaignService.RetrieveById(campaignId);
            if (campaign.HasBeenSent)
            {
                SetErrorMessage("This campaign has already been sent.");
                return RedirectToAction("Preview", new { campaignId = campaignId });
            }
            campaign.Messages.ToList().ForEach(m => messagingService.Send(m));
            campaign.Sent = calendarService.CurrentDateTime;
            saver.SaveAll();
            SetSuccessMessage("Messages sent.");
            return RedirectToAction("Preview", new {campaignId = campaignId});
        }

        public ActionResult Map(int productId)
        {
            Product p = productRepository.FindProductNoAuth(productId);
            return View("Map", new MapViewData(p, configurationService.GoogleMapsKey));
        }

        public ActionResult Cancel()
        {
            return View("Index", campaignService.RetrieveAll().OrderByDescending(campaign => campaign.Created).ToList());
        }

        private Campaign Create(string name, string subject, string content)
        {
            Campaign created = new Campaign();
            saver.AddToContext(created);
            created.Created = calendarService.CurrentDateTime;
            created.Name = name;
            created.Subject = subject;
            created.Content = content;
            return created;
        }


    }

    public class MapViewData
    {
        public Product ToMap { get; private set; }
        public string GoogleMapsKey { get; private set;}

        public MapViewData(Product p, string key)
        {
            ToMap = p;
            GoogleMapsKey = key;
        }
    }
}

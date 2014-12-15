namespace Dahlia.WebApplication.Controllers
{
    using System;
    using System.Collections.Generic;
    using System.Linq;
    using System.Web.Mvc;
    using NServiceBus;
    using CurrentRescheduleRetreatCommand = Commands.RescheduleRetreatCommand.Version1;
    using CurrentRenameRetreatCommand = Commands.RenameRetreatCommand.Version1;
    using CurrentCancelRetreatCommand = Commands.CancelRetreatCommand.Version1;
    using Data.Common;
    using Framework;

    public class ManageRetreatsController : Controller
    {
        readonly ReadRepository repository;
        readonly IBus bus;

        public ManageRetreatsController(IBus bus)
        {
            repository = new ReadRepository(new ConfigConnectionSettings("data"));
            this.bus = bus;
        }

        public ActionResult Index()
        {
            var retreats = repository.All("SELECT * FROM [Retreats] ORDER BY [Date], [Description]", Enumerable.Empty<KeyValuePair<string, object>>());

            return View(retreats);
        }

        public ActionResult Res(Guid id)
        {
            return View();
        }

        public ActionResult Reschedule(Guid id, DateTime date)
        {
            var command = new CurrentRescheduleRetreatCommand { AggregateRootId = id, Date = date };
            bus.Send(command);

            return RedirectToAction("Index");
        }

        public ActionResult Ren(Guid id)
        {
            return View();
        }

        public ActionResult Rename(Guid id, string description)
        {
            var command = new CurrentRenameRetreatCommand { AggregateRootId = id, Description = description };
            bus.Send(command);

            return RedirectToAction("Index");
        }

        public ActionResult Cancel(Guid id)
        {
            var command = new CurrentCancelRetreatCommand { AggregateRootId = id };
            bus.Send(command);

            return RedirectToAction("Index");
        }
    }
}

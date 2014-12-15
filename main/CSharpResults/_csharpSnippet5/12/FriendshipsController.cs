using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;
using System.Web.Mvc;
using WebMatrix.WebData;
using Mahala.Models;
using Mahala.Data;
using Mahala.Web.Filters;

namespace Mahala.Web.Controllers
{
    [InitializeSimpleMembership]
    public class FriendshipsController : Controller
    {
        //
        // GET: /Friendships/SendFriendshipRequest

        public ActionResult SendFriendshipRequest(int toUserUserId)
        {
            if (!Request.IsAuthenticated)
            {
                return View("Unauthenticated");
            }

            int currentUserUserId = WebSecurity.CurrentUserId;

            ViewBag.SendFriendshipRequestSuccess = FriendshipsData.AddFriendshipRequest(currentUserUserId, toUserUserId);
           
            return View();
        }

        //
        // GET: /Friendships/SendFriendshipRequestByUserName

        public ActionResult SendFriendshipRequestByUserName(int toUserUserName)
        {
            if (!Request.IsAuthenticated)
            {
                return View("Unauthenticated");
            }

            int currentUserUserId = WebSecurity.CurrentUserId;

            ViewBag.SendFriendshipRequestSuccess = FriendshipsData.AddFriendshipRequest(currentUserUserId, toUserUserName);

            return View();
        }

        //
        // GET: /Friendships/MyFriendships

        public ActionResult MyFriendships()
        {
            if (!Request.IsAuthenticated)
            {
                return View("Unauthenticated");
            }

            int currentUserUserId = WebSecurity.CurrentUserId;
            ViewBag.currentUserUserId = currentUserUserId;

            List<Friendship> myFriendships = FriendshipsData.GetMyFriendships(currentUserUserId);
            List<FriendshipViewModel> myFriendhipsModel = FriendshipsData.ToListOfFriendshipsViewModel(myFriendships);

            if (myFriendships.Count == 0)
            {
                ViewBag.NoResultFound = true;
            }
            else
            {
                ViewBag.NoResultFound = false;
            }

            return View(myFriendhipsModel);
        }

        //
        // GET: /Friendships/FriendshipRequests

        public ActionResult FriendshipRequests()
        {
            if (!Request.IsAuthenticated)
            {
                return View("Unauthenticated");
            }

            int currentUserUserId = WebSecurity.CurrentUserId;
            ViewBag.currentUserUserId = currentUserUserId;

            List<FriendshipRequest> friendshipRequests = FriendshipsData.GetFriendshipRequests(currentUserUserId);
            List<FriendshipRequestViewModel> friendshipRequestsModel =
                FriendshipsData.ToListOfFriendshipRequestsViewModel(friendshipRequests);

            if (friendshipRequests.Count == 0)
            {
                ViewBag.NoResultFound = true;
            }
            else
            {
                ViewBag.NoResultFound = false;
            }

            return View(friendshipRequestsModel);
        }

        //
        // GET: /Friendships/FriendshipRequestsCount

        public PartialViewResult FriendshipRequestsCount()
        {
            int currentUserUserId = WebSecurity.CurrentUserId;
            ViewBag.currentUserUserId = currentUserUserId;

            int friendshipRequestsFromMeCount = FriendshipsData.GetFriendshipRequestsFromMeCount(currentUserUserId);
            ViewBag.friendshipRequestsFromMeCount = friendshipRequestsFromMeCount;

            int friendshipRequestsToMeCount = FriendshipsData.GetFriendshipRequestsToMeCount(currentUserUserId);
            ViewBag.friendshipRequestsToMeCount = friendshipRequestsToMeCount;

            return PartialView();
        }

        // POST: /Friendships/AcceptFriendshipRequest

        public ActionResult ApproveFriendshipRequest(int friendshipRequestId)
        {
            if (!Request.IsAuthenticated)
            {
                return View("Unauthenticated");
            }

            ViewBag.AcceptFriendshipRequestSuccess = FriendshipsData.ApproveFriendshipRequest(friendshipRequestId);

            return View();
        }

        // POST: /Friendships/CancelFriendshipRequest

        public ActionResult CancelFriendshipRequest(int friendshipRequestId)
        {
            if (!Request.IsAuthenticated)
            {
                return View("Unauthenticated");
            }

            int currentUserUserId = WebSecurity.CurrentUserId;

            ViewBag.CancelFriendshipRequestSuccess = FriendshipsData.CancelFriendshipRequest(friendshipRequestId, currentUserUserId);

            return View();
        }

        // POST: /Friendships/BreakFriendship

        public ActionResult BreakFriendship(int friendshipId)
        {
            if (!Request.IsAuthenticated)
            {
                return View("Unauthenticated");
            }

            int currentUserId = WebSecurity.CurrentUserId;

            ViewBag.BreakFriendshipSuccess = FriendshipsData.BreakFriendship(friendshipId, currentUserId);

            return View();
        }

        [HttpGet]
        public ActionResult AllActiveFriendshipsJson()
        {
            List<Friendship> allActiveFriendships = FriendshipsData.GetAllActiveFriendships();
            List<FriendshipViewModel> allActiveFriendshipsModel = FriendshipsData.ToListOfFriendshipsViewModel(allActiveFriendships);

            return Json(allActiveFriendshipsModel, JsonRequestBehavior.AllowGet);
        }
    }
}

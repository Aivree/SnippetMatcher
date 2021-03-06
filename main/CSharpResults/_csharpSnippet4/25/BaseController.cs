﻿using Penates.Interfaces.Repositories;
using Penates.Repositories.Users;
using System;
using System.Collections.Generic;
using System.Collections.Specialized;
using System.Drawing;
using System.Drawing.Imaging;
using System.Linq;
using System.Reflection;
using System.Web;
using System.Web.Mvc;
using System.Web.UI.WebControls;

namespace Penates.Controllers
{
    public class BaseController : Controller
    {
        protected static Dictionary<string, List<string>> RolesControllerDictionary;

        [AttributeUsage(AttributeTargets.Method, AllowMultiple = false, Inherited = true)]
        public class MultipleButtonAttribute : ActionNameSelectorAttribute
        {
            public string Name { get; set; }
            public string Argument { get; set; }

            public override bool IsValidName(ControllerContext controllerContext, string actionName, MethodInfo methodInfo)
            {
                var isValidName = false;
                var keyValue = string.Format("{0}:{1}", Name, Argument);
                var value = controllerContext.Controller.ValueProvider.GetValue(keyValue);

                if (value != null)
                {
                    controllerContext.Controller.ControllerContext.RouteData.Values[Name] = Argument;
                    isValidName = true;
                }

                return isValidName;
            }
        }
                
	    protected override void OnActionExecuted(ActionExecutedContext filterContext)
	    {
		    // build list of menu items based on user's permissions, and add it to ViewData
		    IEnumerable<MenuItem> menu = BuildMenu();
		    ViewData["Menu"] = menu;
	    }

	    private IEnumerable<MenuItem> BuildMenu()
	    {
		    // Code to build a menu
		    var dynamicMenu = new List<MenuItem>();
            IUserRepository rp = new UserRepository();
		    // ^^^^^INSERT DESIRED ROLE PROVIDER HERE^^^^^
            //rp.Initialize("", new NameValueCollection());
		    try
		    {
			    // Get all roles for user from RoleProvider
			    foreach (var role in rp.GetRolesForUser(HttpContext.User.Identity.Name))
			    {
				    // Check if role is in dictionary
                    //if (RolesControllerDictionary.Keys.Contains(role))
                    //{
                    //    var controllerList = RolesControllerDictionary[role];
                    //    foreach (var controller in controllerList)
                    //    {   // Add controller to menu only if it is not already added
                    //        if (dynamicMenu.Any(x => x.Text == controller))
                    //        { continue; }
                    //        else
                    //        { dynamicMenu.Add(new MenuItem(controller)); }
                    //    }
                    //}
			    }
		    }
		    catch { }   // Most role providers can throw exceptions. Insert Log4NET or equiv here.
		    return dynamicMenu;
	    }
	

	    public BaseController()
	    {
		    // Check if ControllerRolesDictionary is non-existant
		    if (RolesControllerDictionary == null)
		    {
			    RolesControllerDictionary = new Dictionary<string, List<string>>();
			    // If so, use Reflection to add List of all Roles associated with Controllers
			    const bool allInherited = true;
			    const string CONTROLLER = "Controller";
			    var myAssembly = System.Reflection.Assembly.GetExecutingAssembly();
			    // get List of all Controllers with [Authorize] attribute
			    var controllerList = from type in myAssembly.GetTypes()
								     where type.Name.Contains(CONTROLLER)
								     where !type.IsAbstract
								     let attribs = type.GetCustomAttributes(allInherited)
								     where attribs.Any(x => x.GetType().Equals(typeof(AuthorizeAttribute)))
								     select type;
			    // Loop over all controllers
			    foreach (var controller in controllerList)
			    {
				    // Find first instance of [Authorize] attribute
				    var attrib = controller.GetCustomAttributes(allInherited).
							     FirstOrDefault(x => x.GetType().Equals(typeof(AuthorizeAttribute))) as AuthorizeAttribute;
				    foreach (var role in attrib.Roles.Split(',').AsEnumerable())
				    {
					    // If there are Roles associated with [Authorize] iterate over them
					    if (!RolesControllerDictionary.ContainsKey(role))
					    { RolesControllerDictionary[role] = new List<string>(); }
					    // Add controller to List of controllers associated with role (removing "controller" from name)
                        RolesControllerDictionary[role].Add(controller.Name.Replace(CONTROLLER,""));
				    }
			    }
		    }
        }
                
    }
}

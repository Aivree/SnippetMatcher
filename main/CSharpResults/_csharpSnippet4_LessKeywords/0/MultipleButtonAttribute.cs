using System;
using System.Collections.Generic;
using System.Linq;
using System.Reflection;
using System.Web;
using System.Web.Mvc;

namespace Zoekjaar.Web.Controllers
{
	[AttributeUsage(AttributeTargets.Method, AllowMultiple = false, Inherited = true)]
	public class MultipleButtonAttribute : ActionNameSelectorAttribute
	{
		public string MatchFormKey { get; set; }
		public string MatchFormValue { get; set; }

		public override bool IsValidName(ControllerContext controllerContext, string actionName, MethodInfo methodInfo)
		{
			return controllerContext.HttpContext.Request[MatchFormKey] != null;
		}
	}
}
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Web.Mvc;
using System.Web;

namespace MudHook.Core
{
    public static class MudHookExtensions
    {
        public static string AdminUrl(this HtmlHelper helper)
        {
            UrlHelper url = new UrlHelper(HttpContext.Current.Request.RequestContext);
            return url.RouteUrl("Admin", new { controller = "Posts", action = "Index" });
        }                
        public static string HttpHost(this HtmlHelper helper)
        {
            return HttpContext.Current.Request.ServerVariables["HTTP_HOST"];
        }

        public static IEnumerable<SelectListItem> GetRolesSelectList()
        {
            MudHookRepository repo = new MudHookRepository();
            IList<Role> roles = repo.GetAllRoles().ToList();
            IEnumerable<SelectListItem> selectList =
                from r in roles
                select new SelectListItem
                {
                    Text = r.Name,
                    Value = r.Id.ToString()
                };                                
            return selectList;
        }
        public static IEnumerable<SelectListItem> GetUserStatusSelectList()
        {
            IEnumerable<SelectListItem> selectList =
                from e in Enum.GetValues(typeof(UserStatus)).Cast<UserStatus>()
                select new SelectListItem
                {
                    Text = e.ToString(),
                    Value = Convert.ToInt32(e).ToString()
                };

            return selectList;
        }
        public static IEnumerable<SelectListItem> GetPagesSelectList()
        {
            MudHookRepository repo = new MudHookRepository();
            IList<Page> pages = repo.GetAllPages().ToList();

            IEnumerable<SelectListItem> selectList =
                from p in pages
                where p.Status == PostStatus.published
                select new SelectListItem
                {
                    Text = p.Name,
                    Value = p.Slug
                };
            return selectList;
        }
    }
}

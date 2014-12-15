using LoyaltyPoint.ApplicationServices.DTOs;
using LoyaltyPoint.Common.Entities;
using LoyaltyPoint.Common.Enums;
using LoyaltyPoint.Web.Core.Attributes;
using LoyaltyPoint.Web.Core.Controllers;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;
using System.Web.Mvc;

namespace LoyaltyPoint.Web.Core
{
    public static class DropDowns
    {
        public static SelectList GetCategories(IEnumerable<CategoryDto> list, object selectedValue = null)
        {
            var retList = new List<SelectListItem>(){ new SelectListItem{ Text = "Select..." } };
            retList.AddRange(list.Select(x=>new SelectListItem{ Text = x.Name, Value = x.ID.ToString()}));

            return new SelectList(retList, "Value", "Text", selectedValue);
        }

        public static SelectList GetEmailTemplates(object selectedValue = null)
        {
            return GetSelectListFromEnum<EmailTemplateType>(selectedValue);
        }

        public static SelectList GetCreditCards(object selectedValue = null)
        {
            return GetSelectListFromEnum<CreditCardType>(selectedValue);
        }

        public static SelectList Monthes (object selectedValue = null)
        {
            return new SelectList(new int[] { 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12 });
        }

        public static SelectList Years (object selectedValue = null)
        {
            var years = new int[10];

            for (int i = 0; i < years.Length; i++)
                years[i] = DateTime.Today.Year + i;

            return new SelectList(years, selectedValue);
        }

        public static SelectList GetPages(IEnumerable<PageDto> list, object selectedValue = null)
        {
            var retList = new List<SelectListItem>() { new SelectListItem { Text = "Select..." } };
            retList.AddRange(list.Select(x => new SelectListItem { Text = x.Key, Value = x.Key }));

            return new SelectList(retList, "Value", "Text", selectedValue);
        }

        public static SelectList GetDisplayLinkTypes(object selectedValue = null)
        {
            return GetSelectListFromEnum<LinkDisplayType>(selectedValue);
        }

        public static SelectList GetPageTemplates(object selectedValue = null)
        {
            var retList = new List<SelectListItem>() { new SelectListItem { Text = "Default" } };
            retList.Add(new SelectListItem { Text = "Layout_1", Value = "_Layout_1" });
            retList.Add(new SelectListItem { Text = "Layout_2", Value = "_Layout_2" });

            return new SelectList(retList, "Value", "Text", selectedValue);
        }

        public static SelectList GetPointMutationTypes(IEnumerable<PointMutationType> list, object selectedValue = null)
        {
            var retList = new List<SelectListItem>() { new SelectListItem { Text = "Select..." } };
            retList.AddRange(list.Select(x => new SelectListItem { Text = x.Title, Value = x.ID.ToString() }));

            return new SelectList(retList, "Value", "Text", selectedValue);
        }

        public static SelectList GetPredefinedPages (object selectedValue = null)
        {
            var predefinedPages = new List<KeyValuePair<string,string>>();

            foreach(var t in typeof(HomeController).Assembly.GetTypes())
            {
                foreach(var m in t.GetMembers().Where(x=>x.IsDefined(typeof(PredefinedPageAttribute), false)))
                {
                    var attr = Attribute.GetCustomAttribute(m, typeof(PredefinedPageAttribute)) as PredefinedPageAttribute;
                    predefinedPages.Add(new KeyValuePair<string,string>(attr.Key, attr.Name));
                }
            }

            var retList = new List<SelectListItem>() { new SelectListItem { Text = "Select..." } };
            retList.AddRange(predefinedPages.Select(x => new SelectListItem { Text = x.Value, Value = x.Key}));

            return new SelectList(retList, "Value", "Text", selectedValue);
        }

        public static SelectList GetStatusesList(object selectedValue = null)
        {
            return GetSelectListFromEnum<StatusType>(selectedValue);
        }

        public static SelectList GetRolesList(object selectedValue = null)
        {
            return GetSelectListFromEnum<UserRole>(selectedValue);
        }

        private static SelectList GetSelectListFromEnum<T>(object selectedValue = null) where T : struct, IConvertible
        {
            if (!typeof(T).IsEnum)
                throw new ApplicationException("T must be an enumerated type");

            var retList = new List<SelectListItem>() { new SelectListItem { Text = "Select..." } };
            var values = from T e in Enum.GetValues(typeof(T))
                         select new SelectListItem() { Text = e.ToString(), Value = Convert.ToByte(e).ToString() };

            retList.AddRange(values);

            return new SelectList(retList, "Value", "Text", selectedValue);
        }

        
    }
}
using System;
using System.Collections.Generic;
using System.ComponentModel.DataAnnotations;
using System.Linq;
using System.Reflection;
using System.Web.Mvc;

namespace Artyso.LB.Web.Helpers
{
    public static class SelectListHelper
    {
        public const string DefaultValue = " ";
        public const string DefaultText = "-";

        public static SelectList ToSelectList<TEnum>(this TEnum enumObj, bool isSelected = true)
        {
            var values = from TEnum e in Enum.GetValues(typeof (TEnum))
                         select new { Id = e, Name = e.ToString() };

            return isSelected
                       ? new SelectList(values, "Id", "Name", enumObj)
                       : new SelectList(values, "Id", "Name");
        }

        public static SelectList ToSelectListWithEmptyOption<TEntity>(IEnumerable<TEntity> source,
                                                                      Func<TEntity, object> valueSelector,
                                                                      Func<TEntity, string> textSelector,
                                                                      SelectListItem defaultItem = null,
                                                                      bool originalOrder = false)
        {
            var result = new List<SelectListItem>
                {
                    defaultItem ?? new SelectListItem() { Text = DefaultText, Value = DefaultValue }
                };

            var sourceQuery = source.Select(entity => new SelectListItem()
                {
                    Value = valueSelector(entity).ToString(),
                    Text = textSelector(entity),
                });
            if (!originalOrder)
            {
                sourceQuery = sourceQuery.OrderBy(x => x.Text);
            }

            result.AddRange(sourceQuery);

            return new SelectList(result, "Value", "Text");
        }

        public static SelectList ToSelectList<TEntity>(IEnumerable<TEntity> source, Func<TEntity, object> valueSelector,
                                                       Func<TEntity, string> textSelector, bool originalOrder = false)
        {
            var result = new List<SelectListItem>();

            var sourceQuery = source.Select(entity => new SelectListItem()
                {
                    Value = valueSelector(entity).ToString(),
                    Text = textSelector(entity),
                });
            if (!originalOrder)
            {
                sourceQuery = sourceQuery.OrderBy(x => x.Text);
            }

            result.AddRange(sourceQuery);

            return new SelectList(result, "Value", "Text");
        }

        public static SelectList EnumToSelectListWithEmptyOption<TEnum, TBase>(SelectListItem defaultItem)
        {
            var result = new List<SelectListItem>();

            if (defaultItem != null)
            {
                result.Add(defaultItem);
            }

            var values = Enum.GetValues(typeof(TEnum)).Cast<TBase>();

            result.AddRange(values
                .Select(x => new SelectListItem()
                {
                    Text = Enum.GetName(typeof(TEnum), x),
                    Value = x.ToString(),
                })
                .OrderBy(x => x.Text));

            return new SelectList(result, "Value", "Text");
        }

        public static SelectList EnumToSelectList<TEnum, TBase>(TEnum defaultValue)
        {
            var result = new List<SelectListItem>();

            var values = Enum.GetValues(typeof(TEnum)).Cast<TBase>();
            result.AddRange(values
                .Select(x => new SelectListItem()
                {
                    Text = Enum.GetName(typeof(TEnum), x),
                    Value = x.ToString(),
                })
                .OrderBy(x => x.Text));

            return new SelectList(result, "Value", "Text", result.First(x => x.Text == defaultValue.ToString()).Value);
        }
    }
}
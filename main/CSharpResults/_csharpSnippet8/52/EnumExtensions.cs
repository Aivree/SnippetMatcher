using System;
using System.Collections.Generic;
using System.Linq;
using System.Web.Mvc;

namespace Tracker.Features.Core.Extensions
{
    public static class EnumExtensions
    {
        public static IEnumerable<SelectListItem> ToSelectList<TEnum>(this TEnum enumObj)
            where TEnum : struct, IComparable, IFormattable, IConvertible
        {
            var values = from TEnum e in Enum.GetValues(typeof (TEnum))
                select new {Id = e, Name = e.ToString()};
            return new SelectList(values, "Id", "Name", enumObj);
        }
    }
}

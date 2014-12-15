using System;
using System.Collections.Generic;
using System.Linq;
using System.Web.Mvc;

namespace Keramatian.Extensions
{
    public static class EnumExtentions
    {
        public static SelectList ToSelectList<TEnum>(this TEnum enumObj) where TEnum : struct
        {
            if (!typeof(TEnum).IsEnum) throw new ArgumentException("An Enumeration type is required.", "enumObj");

            var values = from TEnum e in Enum.GetValues(typeof(TEnum))
                         select new
                         {
                             Id = (int)Enum.Parse(typeof(TEnum), e.ToString()),
                             Name = e.ToString()
                         };

            return new SelectList(values, "Id", "Name", enumObj);
        }

        public static void ForEach<T>(this IEnumerable<T> source, Action<T> action)
        {
            if (source != null)
                foreach (T element in source)
                    action(element);
        }
    }
}
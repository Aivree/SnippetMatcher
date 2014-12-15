using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Web.Mvc;

namespace Demo.Core.Helpers
{
    public static class EnumHelper
    {
        public static SelectList ToSelectList<TEnum>(this TEnum obj)
            where TEnum : struct,IComparable, IFormattable, IConvertible
        {
            var values = from TEnum e in Enum.GetValues(typeof(TEnum))
                         select new { Id = e, Name = e.ToString() };
            return new SelectList(values, "Id", "Name", obj);
        }
    }
}

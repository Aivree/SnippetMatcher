using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;

using System.Web.Mvc;

namespace rovnicaMVC
{
    public static class MVCHelper
    {
        public static SelectList ToSelectList<TEnum>(this TEnum enumObj)
        {
            var values = from TEnum e in Enum.GetValues(typeof(TEnum))
                         select new { Id = e, Name = e.ToString() };

            var res = new SelectList(values.ToList(), "Id", "Name", enumObj);
            return res;
        }
    }
}

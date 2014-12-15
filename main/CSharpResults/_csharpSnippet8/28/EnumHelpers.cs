using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Web.Mvc;

namespace Nebula.MvcAddons.Helpers {
    public static class EnumHelpers {

        public static SelectList ToSelectList<TEnum>(this TEnum enumObj) where TEnum : struct, IConvertible {
            var values = from TEnum e in Enum.GetValues(typeof(TEnum))
                         select new { Id = e, Name = e.ToString() };

            return new SelectList(values, "Id", "Name", enumObj);
        }
    }
}

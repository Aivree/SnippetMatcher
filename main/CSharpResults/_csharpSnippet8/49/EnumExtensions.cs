using System;
using System.Collections.Generic;
using System.Globalization;
using System.Linq;
using System.Web.Mvc;

namespace Kauntr.UI.Web.Helpers {
    public static class EnumExtensions {
        public static IEnumerable<SelectListItem> ToSelectList<TEnum>(this TEnum enumObject)
            where TEnum : struct, IComparable, IFormattable, IConvertible {
            var values = Enum.GetValues(typeof (TEnum)).Cast<TEnum>()
                             .Select(e => new {Id = e, Name = e.ToString(CultureInfo.InvariantCulture).ToLower()});
            return new SelectList(values, "Id", "Name", enumObject);
        }
    }
}
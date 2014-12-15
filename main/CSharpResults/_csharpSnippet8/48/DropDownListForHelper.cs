using Spontaneous.DataModel;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;
using System.Web.Mvc;

namespace Spontaneous.WebApp.Helpers
{
    public class DropDownListForHelper
    {
        public static SelectList GetSelectListForEnumType<K>()
        {
            List<object> values = new List<object>();
            if (typeof(PumpType).IsAssignableFrom(typeof(K)) || typeof(Sex).IsAssignableFrom(typeof(K))) 
            {
                values.Add(new { ID = "choose", Name = "--בחר ערך--"});
            }
            
            values.AddRange(from K type in Enum.GetValues(typeof(K))
                            select new { ID = type, Name = type.ToString() });
            var selectList = new SelectList(values, "Id", "Name");
            return new SelectList(values, "Id", "Name");
        }

        public static SelectList GetSelectListForEnumType<K>(K selected) 
        {
            List<object> values = new List<object>();
            if (typeof(PumpType).IsAssignableFrom(typeof(K)) || typeof(Sex).IsAssignableFrom(typeof(K)))
            {
                values.Add(new { ID = "choose", Name = "--בחר ערך--"});
            }

            values.AddRange(from K type in Enum.GetValues(typeof(K))
                                 select new { ID = type, Name = type.ToString() });
            return new SelectList(values, "Id", "Name", selected);
        }
    }
}
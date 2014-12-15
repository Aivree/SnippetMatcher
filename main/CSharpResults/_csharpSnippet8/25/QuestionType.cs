using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;
using System.Web.Mvc;

namespace Emina.Models
{
    public enum QuestionType
    {
        Open, 
        MultipleChoice,
        Checkbox,
        Stars,
        Grade,
        Binary,
        FuzzyBinary
    };


    public static class QuestionTypeMethods
    {
        public static SelectList ToSelectList<TEnum>(this TEnum enumObj)
        {
            var values = from TEnum e in Enum.GetValues(typeof(TEnum))
                         select new { Id = e, Name = e.ToString() };

            return new SelectList(values, "Id", "Name", enumObj);
        }
    }
}
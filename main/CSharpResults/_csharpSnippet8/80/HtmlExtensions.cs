using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Linq;
using System.Web.Mvc;
using Telecom.BusinessLogic.Helpers;
using Telecom.BusinessObjects.Helpers;

namespace Telecom.WebReseller.Helpers
{
    /// <summary>
    /// HtmlExtensions
    /// </summary>
    public static class HtmlExtensions
    {
        /// <summary>
        /// returns SelectList from enum object
        /// </summary>
        /// <typeparam name="TEnum">The type of the enum.</typeparam>
        /// <param name="enumObj">The enum obj.</param>
        /// <param name="doSelect">if set to <c>true</c> [do select].</param>
        /// <param name="orderAscending">if set to <c>true</c> [order ascending].</param>
        /// <param name="getDescription">if set to <c>true</c> [get description].</param>
        /// <param name="valuesToReturn">if set get only values defined.</param>
        /// <returns></returns>
        public static SelectList ToSelectList<TEnum>(this TEnum enumObj, bool doSelect = true, bool orderAscending = true,
            bool getDescription = true, IEnumerable<TEnum> valuesToReturn = null) where TEnum : struct
        {
            if (!typeof(TEnum).IsEnum) throw new NotSupportedException("Generic TEnum must be an Enum");

            valuesToReturn = valuesToReturn ?? (IEnumerable<TEnum>)Enum.GetValues(typeof(TEnum));

            var result = valuesToReturn.Select(x => new { Id = x, Name = getDescription ? EnumExtensions.GetDescription(x) : x.ToString() });
            if (orderAscending) result = result.OrderBy(x => x.Name);

            var selected = doSelect ? enumObj as object : null;
            return new SelectList(result, "Id", "Name", selected);
        }
    }
}
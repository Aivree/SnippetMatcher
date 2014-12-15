using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.ComponentModel;
using System.Collections;
using System.Globalization;
using System.Web.Mvc;

namespace Common.Extensions.EnumEx
{
    public static class EnumExtension
    {
        /// <summary>
        /// Return a description value attribute, or if not exists this attribute decoration, return proper enum item
        /// </summary>
        /// <param name="enumValue"></param>
        /// <returns></returns>
        public static string ToDescription(this Enum enumValue)
        {
            DescriptionAttribute[] da = (DescriptionAttribute[])(enumValue.GetType().GetField(enumValue.ToString()))
                .GetCustomAttributes(typeof(DescriptionAttribute), false);

            return da.Length > 0 ? da[0].Description : enumValue.ToString();
        }

        /// <summary>
        /// Return a enum item based at string description value
        /// </summary>
        /// <typeparam name="T"></typeparam>
        /// <param name="descriptionValue"></param>
        /// <returns></returns>
        public static T ToEnum<T>(string descriptionValue)
        {
            foreach (T enumValue in Enum.GetValues(typeof(T)))
            {
                DescriptionAttribute[] da = (DescriptionAttribute[])(typeof(T).GetField(enumValue.ToString()))
                    .GetCustomAttributes(typeof(DescriptionAttribute), false);

                if (da.Length > 0 && (da[0].Description.Equals(descriptionValue) || da[0].ToString().Equals(descriptionValue)))
                {
                    return enumValue;
                }
            }

            throw new InvalidEnumArgumentException("NO exists enum item associated with this string description !");
        }

        /// <summary>
        /// Return all enum items
        /// </summary>
        /// <param name="value"></param>
        /// <returns></returns>
        public static IEnumerable<T> GetEnumItems<T>(this Enum value)
        {
            int valueAsInt = Convert.ToInt32(value, CultureInfo.InvariantCulture);

            foreach (object item in Enum.GetValues(typeof(T)))
            {
                int itemAsInt = Convert.ToInt32(item, CultureInfo.InvariantCulture);

                if (itemAsInt == (valueAsInt & itemAsInt))
                {
                    yield return (T)item;
                }
            }
        }

        public static IEnumerable<T> GetEnumItems<T>()
        {
            //int valueAsInt = Convert.ToInt32(value, CultureInfo.InvariantCulture);

            var lst = new List<T>();

            foreach (object item in Enum.GetValues(typeof(T)))
            {
                lst.Add((T)item);
            }

            return lst;
        }

        /// <summary>
        /// Return a SelectList to Dropdownlist HTML Helper ASP.NET MVC
        /// </summary>
        /// <typeparam name="TEnum"></typeparam>
        /// <param name="enumObj"></param>
        /// <returns></returns>
        public static SelectList ToSelectList<TEnum>(this TEnum enumObj)
        {
            var values = from TEnum e in Enum.GetValues(typeof(TEnum))
                         select new { Id = e, Name = e.ToString() };

            return new SelectList(values, "Id", "Name", enumObj);
        }
    }
}
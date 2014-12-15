﻿using System;
using System.Collections.Generic;
using System.Text;
using Windows.Globalization.DateTimeFormatting;
using Windows.UI.Xaml.Data;

namespace WalletManager.App.Converter
{
    public class RelativeTimeConverter : IValueConverter
    {
        public object Convert(object value, Type targetType, object parameter, string language)
        {
            if (value == null) return "unable to detect time.";
            var inputDate = (DateTime)value;
            return this.GetTime(inputDate);
        }

        public object ConvertBack(object value, Type targetType, object parameter, string language)
        {
            return null;
            //return System.Convert.ToDecimal(value);
        }

        string GetTime(DateTime dt)
        {

            var ts = new TimeSpan(DateTime.Now.Ticks - dt.Ticks);
            double delta = Math.Abs(ts.TotalSeconds);

            if (delta < 172800)
            {
                return new DateTimeOffset(dt).TimeOfDay.ToString();
            }

            if (delta < 604800)
            {
                return new DateTimeOffset(dt).DayOfWeek.ToString();
            }

            if (delta < 31104000)
            {
                return dt.Date.ToString("dd/MM");
            }
            /*
            if (delta < 60)
            {
                return ts.Seconds == 1 ? "one second ago" : ts.Seconds + " seconds ago";
            }
            if (delta < 120)
            {
                return "a minute ago";
            }
            if (delta < 2700) // 45 * 60
            {
                return ts.Minutes + " minutes ago";
            }
            if (delta < 5400) // 90 * 60
            {
                return "an hour ago";
            }
            if (delta < 86400) // 24 * 60 * 60
            {
                return ts.Hours + " hours ago";
            }
            if (delta < 172800) // 48 * 60 * 60
            {
                return "yesterday";
            }
            if (delta < 2592000) // 30 * 24 * 60 * 60
            {
                return ts.Days + " days ago";
            }
            if (delta < 31104000) // 12 * 30 * 24 * 60 * 60
            {
                int months = System.Convert.ToInt32(Math.Floor((double)ts.Days / 30));
                return months <= 1 ? "one month ago" : months + " months ago";
            }*/

            int years = System.Convert.ToInt32(Math.Floor((double)ts.Days / 365));
            return years <= 1 ? "one year ago" : years + " years ago";




        }
    }
}
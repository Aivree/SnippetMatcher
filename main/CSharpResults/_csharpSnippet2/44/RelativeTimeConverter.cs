using System;
using System.Collections.Generic;
using System.Text;
#if NET35

using System.Windows.Data;

#elif NETFX_CORE

using Windows.Globalization.DateTimeFormatting;
using Windows.UI.Xaml.Data;

#endif

namespace WPFBase.Converter
{
    public class RelativeTimeConverter : IValueConverter
    {
        public object Convert(object value, Type targetType, object parameter, 
#if NETFX_CORE 
            string language)
#elif NET35
            System.Globalization.CultureInfo culture)
#endif
        {
            if (value == null) return "unable to detect time.";

            if (value == typeof(DateTime))
            {
                var inputDate = (DateTime)value;
                return this.GetTime(inputDate);
            }
            else if (value is int)
            {
                return this.GetTime2((int)value);
            }

            else
            {
                return value.ToString();
            }

        }

        public object ConvertBack(object value, Type targetType, object parameter,
#if NETFX_CORE 
            string language)
#elif NET35
 System.Globalization.CultureInfo culture)
#endif
        {
            return null;
            //return System.Convert.ToDecimal(value);
        }

        string GetTime(DateTime dt)
        {

            var ts = new TimeSpan(DateTime.Now.Ticks - dt.Ticks);
            double delta = Math.Abs(ts.TotalSeconds);

            //if (delta < 172800)
            //{
            //    return dt.ToString("HH:mm");
            //}

            //if (delta < 604800)
            //{
            //    return new DateTimeOffset(dt).DayOfWeek.ToString();
            //}

            //if (delta < 31104000)
            //{
            //    return dt.Date.ToString("dd/MM");
            //}

            if (delta < 60)
            {
                return ts.Seconds == 1 ? "ONE SECOND AGO" : ts.Seconds + " SECONDS AGO";
            }
            if (delta < 120)
            {
                return "A MINUTE AGO";
            }
            if (delta < 2700) // 45 * 60
            {
                return ts.Minutes + " MINUTES AGO";
            }
            if (delta < 5400) // 90 * 60
            {
                return "AN HOUR AGO";
            }
            if (delta < 86400) // 24 * 60 * 60
            {
                return ts.Hours + " HOURS AGO";
            }
            if (delta < 172800) // 48 * 60 * 60
            {
                return "YESTERDAY";
            }
            if (delta < 2592000) // 30 * 24 * 60 * 60
            {
                return ts.Days + " DAYS AGO";
            }
            if (delta < 31104000) // 12 * 30 * 24 * 60 * 60
            {
                int months = System.Convert.ToInt32(Math.Floor((double)ts.Days / 30));
                return months <= 1 ? "ONE MONTH AGO" : months + " MONTHS AGO";
            }

            int years = System.Convert.ToInt32(Math.Floor((double)ts.Days / 365));
            return years <= 1 ? "ONE YEAR AGO" : years + " YEARS AGO";




        }
        string GetTime2(int days)
        {
            if (days == 0)
                return "Today";
            else if (days == 1)
                return "Tomorrow";
            else if (days <= 7)
                return days.ToString() + " days";
            else if (days <= 14)
                return "Next week";
            else if (days <= 21)
                return "2 weeks";
            else if (days <= 21)
                return "3 weeks";
            else if (days <= 28)
                return "4 weeks";
            else
                return days.ToString() + days;
        }


    }
}

using System;
using System.Globalization;

namespace HashBot.Core.Extensions
{
    public static class DateTimeExtensions
    {
        const int SECOND = 1;
        const int MINUTE = 60 * SECOND;
        const int HOUR = 60 * MINUTE;
        const int DAY = 24 * HOUR;
        const int MONTH = 30 * DAY;

        public static String TimeAgo(this DateTime dt)
        {
            var ts = new TimeSpan(DateTime.Now.Ticks - dt.Ticks);
            double delta = Math.Abs(ts.TotalSeconds);

            if (delta < 1 * MINUTE)
            {
                return (ts.Seconds > 0) ? string.Concat(ts.Seconds, " с") : "сейчас";
            }
            if (delta < 60 * MINUTE)
            {
                return string.Concat(ts.Minutes, " м");
            }
            if (delta < 24 * HOUR)
            {
                return string.Concat(ts.Hours, " ч");
            }
            if (delta < 30 * DAY)
            {
                return string.Concat(ts.Days, " д");
            }
            if (delta < 12 * MONTH)
            {
                int months = Convert.ToInt32(Math.Floor((double)ts.Days / 30));
                return string.Concat(months, " мес");
            }
            else
            {
                int years = Convert.ToInt32(Math.Floor((double)ts.Days / 365));
                return string.Concat(years, " г");
            }
        }


        public static DateTime ParseTwitterDate(string twitter_date)
        {
            return DateTime.ParseExact(
                                twitter_date,
                                "ddd MMM dd HH:mm:ss zzz yyyy",
                                CultureInfo.InvariantCulture);
        }
    }
}

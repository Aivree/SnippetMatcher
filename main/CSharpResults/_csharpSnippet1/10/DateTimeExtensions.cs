namespace IAnswerable.Utility
{
    using System;

    /// <summary>
    /// TODO: Update summary.
    /// </summary>
    public static class DateTimeExtensions
    {
        public static bool IsValidDateTime(this string value)
        {
            DateTime date;
            return DateTime.TryParse(value, out date);
        }

        public static int MonthDifference(this DateTime lValue, DateTime rValue)
        {
            return Math.Abs((lValue.Month - rValue.Month) + 12 * (lValue.Year - rValue.Year));
        }

        public static DateTime FromUnixTime(long unixTime)
        {
            var epoch = new DateTime(1970, 1, 1, 0, 0, 0, DateTimeKind.Utc);
            return epoch.AddMilliseconds(unixTime);
        }

        public static DateTime AbsoluteStart(this DateTime dateTime)
        {
            return dateTime.Date;
        }

        public static DateTime AbsoluteEnd(this DateTime dateTime)
        {
            return AbsoluteStart(dateTime).AddDays(1).AddTicks(-1);
        }

        public static DateTime WeekStart(this DateTime date)
        {
            return date.AddDays(-(int)date.DayOfWeek);
        }

        public static DateTime WeekEnd(this DateTime date)
        {
            return date.WeekStart().AddDays(7).AddSeconds(-1);
        }

        public static DateTime MonthStart(this DateTime date)
        {
            return date.AddDays(1 - date.Day);
        }

        public static DateTime MonthEnd(this DateTime date)
        {
            return date.MonthStart().AddMonths(1).AddSeconds(-1);
        }

        public static string ToRelativeTime(this DateTime date)
        {
            const int SECOND = 1;
            const int MINUTE = 60 * SECOND;
            const int HOUR = 60 * MINUTE;
            const int DAY = 24 * HOUR;
            const int MONTH = 30 * DAY;

            var timespan = new TimeSpan(DateTime.UtcNow.Ticks - date.Ticks);
            double delta = Math.Abs(timespan.TotalSeconds);

            if (delta < 0)
            {
                return "not yet";
            }
            if (delta < 1 * MINUTE)
            {
                return timespan.Seconds == 1 ? "one second ago" : timespan.Seconds + " seconds ago";
            }
            if (delta < 2 * MINUTE)
            {
                return "a minute ago";
            }
            if (delta < 45 * MINUTE)
            {
                return timespan.Minutes + " minutes ago";
            }
            if (delta < 90 * MINUTE)
            {
                return "an hour ago";
            }
            if (delta < 24 * HOUR)
            {
                return timespan.Hours + " hours ago";
            }
            if (delta < 48 * HOUR)
            {
                return "yesterday";
            }
            if (delta < 30 * DAY)
            {
                return timespan.Days + " days ago";
            }
            if (delta < 12 * MONTH)
            {
                int months = Convert.ToInt32(Math.Floor((double)timespan.Days / 30));
                return months <= 1 ? "one month ago" : months + " months ago";
            }
            else
            {
                int years = Convert.ToInt32(Math.Floor((double)timespan.Days / 365));
                return years <= 1 ? "one year ago" : years + " years ago";
            }
        }

        public static int Age(this DateTime bday)
        {
            DateTime today = DateTime.Today;
            int age = today.Year - bday.Year;
            if (bday > today.AddYears(-age)) age--;

            return age;
        }
    }
}

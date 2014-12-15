using System;
using System.Globalization;
using JetBrains.Annotations;

namespace MyStackOverflow.Data.Utils
{
    public static class DateTimeUtils
    {
        private static readonly int SECOND = 1;
        private static readonly int MINUTE = 60*SECOND;
        private static readonly int HOUR = 60*MINUTE;
        private static readonly int DAY = 24*HOUR;
        private static readonly int MONTH = 30*DAY;

        private static readonly DateTime UnixEpoch =
            new DateTime(1970, 1, 1, 0, 0, 0, DateTimeKind.Utc);

        [PublicAPI]
        public static long GetCurrentUnixTimestampMillis()
        {
            return (long) (DateTime.UtcNow - UnixEpoch).TotalMilliseconds;
        }

        [PublicAPI]
        public static DateTime DateTimeFromUnixTimestampMillis(long millis)
        {
            return UnixEpoch.AddMilliseconds(millis);
        }

        [PublicAPI]
        public static long GetCurrentUnixTimestampSeconds()
        {
            return (long) (DateTime.UtcNow - UnixEpoch).TotalSeconds;
        }

        [PublicAPI]
        public static DateTime DateTimeFromUnixTimestampSeconds(long seconds)
        {
            return UnixEpoch.AddSeconds(seconds);
        }

        [PublicAPI]
        public static int GetWeekNumber(DateTime date)
        {
            var currentCulture = new CultureInfo("Ru-ru");
            return currentCulture.Calendar.GetWeekOfYear(
                date,
                currentCulture.DateTimeFormat.CalendarWeekRule,
                DayOfWeek.Monday);
        }

        [PublicAPI]
        public static int GetRelativeWeekNumber(long parityCountdown)
        {
            var parityCountDown = DateTimeFromUnixTimestampSeconds(parityCountdown);

            var currentWeekNumber = GetWeekNumber(DateTime.UtcNow);
            var firstWeekNumber = GetWeekNumber(parityCountDown);

            if (currentWeekNumber >= firstWeekNumber)
            {
                return currentWeekNumber - firstWeekNumber + 1;
            }
            return currentWeekNumber + (53 - firstWeekNumber) + 1; //todo: fixme
        }


        [PublicAPI]
        public static string ToReadableAgo(this DateTime dt)
        {
            var ts = new TimeSpan(DateTime.UtcNow.Ticks - dt.Ticks);
            double delta = Math.Abs(ts.TotalSeconds);

            if (delta < 0)
            {
                return "not yet";
            }
            if (delta < 1*MINUTE)
            {
                return ts.Seconds == 1 ? "one second ago" : ts.Seconds + " seconds ago";
            }
            if (delta < 2*MINUTE)
            {
                return "a minute ago";
            }
            if (delta < 45*MINUTE)
            {
                return ts.Minutes + " minutes ago";
            }
            if (delta < 90*MINUTE)
            {
                return "an hour ago";
            }
            if (delta < 24*HOUR)
            {
                return ts.Hours + " hours ago";
            }
            if (delta < 48*HOUR)
            {
                return "yesterday";
            }
            if (delta < 30*DAY)
            {
                return ts.Days + " days ago";
            }
            if (delta < 12*MONTH)
            {
                int months = Convert.ToInt32(Math.Floor((double) ts.Days/30));
                return months <= 1 ? "one month ago" : months + " months ago";
            }
            else
            {
                int years = Convert.ToInt32(Math.Floor((double) ts.Days/365));
                return years <= 1 ? "one year ago" : years + " years ago";
            }
        }

        [PublicAPI]
        public static string ToReadableFor(this DateTime dt)
        {
            var ts = new TimeSpan(DateTime.UtcNow.Ticks - dt.Ticks);
            double delta = Math.Abs(ts.TotalSeconds);

            if (delta < 0)
            {
                return "not yet";
            }
            if (delta < 1 * MINUTE)
            {
                return ts.Seconds == 1 ? "one second" : ts.Seconds + " seconds";
            }
            if (delta < 2 * MINUTE)
            {
                return "a minute";
            }
            if (delta < 45 * MINUTE)
            {
                return ts.Minutes + " minutes";
            }
            if (delta < 90 * MINUTE)
            {
                return "an hour";
            }
            if (delta < 24 * HOUR)
            {
                return ts.Hours + " hours";
            }
            if (delta < 48 * HOUR)
            {
                return "yesterday";
            }
            if (delta < 30 * DAY)
            {
                return ts.Days + " days";
            }
            if (delta < 12 * MONTH)
            {
                int months = Convert.ToInt32(Math.Floor((double)ts.Days / 30));
                return months <= 1 ? "one month ago" : months + " months ago";
            }
            else
            {
                int years = Convert.ToInt32(Math.Floor((double)ts.Days / 365));
                return years <= 1 ? "more than one year" : "more than "+  years + " years";
            }
        }
    }
}
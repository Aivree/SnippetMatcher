using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;

/// <summary>
/// Summary description for Tarih
/// </summary>
public static class Tarih
{
    public static string TarihIslemleri_TarihGorunumu(DateTime dt)
    {
        var ts = new TimeSpan(DateTime.UtcNow.Ticks - dt.Ticks);
        double delta = Math.Abs(ts.TotalSeconds);

        if (ts.Seconds < 0)
        {
            return dt.ToLongDateString();
        }
        if (delta < 60)
        {
            return ts.Seconds == 1 ? "1 Sec" : ts.Seconds + " Secs";
        }

        if (delta < 120)
        {
            return "1 Min";
        }
        if (delta < 2700) // 45 * 60
        {
            return ts.Minutes + " Mins";
        }
        if (delta < 5400) // 90 * 60
        {
            return "1 Hour";
        }
        if (delta < 86400) // 24 * 60 * 60
        {
            return ts.Hours + " Hours";
        }
        if (delta < 172800) // 48 * 60 * 60
        {
            return "Yesterday";
        }
        if (delta < 2592000) // 30 * 24 * 60 * 60
        {
            return ts.Days + " Days";
        }
        if (delta < 31104000) // 12 * 30 * 24 * 60 * 60
        {
            int months = Convert.ToInt32(Math.Floor((double)ts.Days / 30));
            return months <= 1 ? "1 Month" : months + " Months";
        }
        int years = Convert.ToInt32(Math.Floor((double)ts.Days / 365));
        return years <= 1 ? "1 Year" : years + " Years";

    }

}
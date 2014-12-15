using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;

namespace Taffrica.Models
{
    public partial class Report
    {
        #region Context
        private TaffricaDBEntities db = new TaffricaDBEntities();
        #endregion

        #region Readonly Properties
        public string HoursAgo
        {
            get
            {
                return CalculateTime(this.Time);
            }
        }
        #endregion

        #region Public Methods
        public Returner ReportStreet()
        {
            TaffricaDBEntities dbStatic = new TaffricaDBEntities();
            this.Status = (int)ReportsStatus.Active;
            dbStatic.Reports.Add(this);
            dbStatic.SaveChanges();
            var LOR = db.Reports.Where(p => p.Id == this.Id).ToList();
            var reportInJson = (from r in LOR
                                select new
                                {
                                    r.Id,
                                    r.Img,
                                    r.IsPrivate,
                                    r.Report1,
                                    r.Status,
                                    r.StreetId,
                                    r.Text,
                                    r.Time,
                                    r.HoursAgo,
                                    User = new
                                    {
                                        r.User.Fullname,
                                        r.User.Img
                                    }
                                }).ToList();
            return new Returner
            {
                Data = this,
                DataInJSON = reportInJson.SingleOrDefault(),
                Message = Messages.Street_reported_successfully
            };
        }

        public static Returner Delete(int Id)
        {
            TaffricaDBEntities dbStatic = new TaffricaDBEntities();
            var report = dbStatic.Reports.SingleOrDefault(p => p.Id == Id);
            if (report != null)
            {
                if (report.Status != (int)ReportsStatus.Deleted)
                {
                    report.Status = (int)ReportsStatus.Deleted;
                    dbStatic.SaveChanges();
                    return new Returner
                    {
                        Message = Messages.report_deleted_successfully
                    };
                }
                else
                {
                    return new Returner
                    {
                        Message = Messages.report_already_deleted
                    };
                }
            }
            else
            {
                return new Returner
                {
                    Message = Messages.No_reports_found
                };
            }
        }

        public string CalculateTime(DateTime? date)
        {
            if (date == null)
            {
                return "";
            }
            const int SECOND = 1;
            const int MINUTE = 60 * SECOND;
            const int HOUR = 60 * MINUTE;
            const int DAY = 24 * HOUR;
            const int MONTH = 30 * DAY;

            var ts = new TimeSpan(DateTime.UtcNow.Ticks - ((DateTime)date).Ticks);
            double delta = Math.Abs(ts.TotalSeconds);

            if (delta < 0)
            {
                return "not yet";
            }
            if (delta < 1 * MINUTE)
            {
                return ts.Seconds == 1 ? "one second ago" : ts.Seconds + " seconds ago";
            }
            if (delta < 2 * MINUTE)
            {
                return "a minute ago";
            }
            if (delta < 45 * MINUTE)
            {
                return ts.Minutes + " minutes ago";
            }
            if (delta < 90 * MINUTE)
            {
                return "an hour ago";
            }
            if (delta < 24 * HOUR)
            {
                return ts.Hours + " hours ago";
            }
            if (delta < 48 * HOUR)
            {
                return "yesterday";
            }
            if (delta < 30 * DAY)
            {
                return ts.Days + " days ago";
            }
            if (delta < 12 * MONTH)
            {
                int months = Convert.ToInt32(Math.Floor((double)ts.Days / 30));
                return months <= 1 ? "one month ago" : months + " months ago";
            }
            else
            {
                int years = Convert.ToInt32(Math.Floor((double)ts.Days / 365));
                return years <= 1 ? "one year ago" : years + " years ago";
            }
        }
        #endregion
    }
}
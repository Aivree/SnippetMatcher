using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Linq;
using System.Net.Mail;
using System.Reflection;
using System.Security.Cryptography;
using System.Text;
using System.Text.RegularExpressions;

namespace H724._Helpers
{
    public static class Utils
    {
        public static string Md5Hash(string text)
        {
            var md5 = new MD5CryptoServiceProvider();

            //compute hash from the bytes of text
            md5.ComputeHash(Encoding.ASCII.GetBytes(text));

            //get hash result after compute it
            var result = md5.Hash;

            var strBuilder = new StringBuilder();
            for (var i = 0; i < result.Length; i++)
            {
                //change it into 2 hexadecimal digits
                //for each byte
                strBuilder.Append(result[i].ToString("x2"));
            }

            return strBuilder.ToString();
        }
        public static string GetUniqueKey(int size)
        {
            //const string a = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";
            const string a = "ABCDEFGHIJKLMNOPQRSTUVWXYZ123456789";
            //
            var chars = a.ToCharArray();
            var data = new byte[1];
            var crypto = new RNGCryptoServiceProvider();
            crypto.GetNonZeroBytes(data);
            //
            data = new byte[size];
            crypto.GetNonZeroBytes(data);
            //
            var result = new StringBuilder(size);
            foreach (var b in data)
            {
                result.Append(chars[b % (chars.Length - 1)]);
            }
            //
            return result.ToString();
        }
        public static bool TcNoKontrol(string tcNo)
        {
            var genelkont = 0;
            var kontroltek = 0;
            var kontrolcift = 0;
            var tcdiziStr = tcNo.Select(c => c.ToString()).ToArray();
            var tcdiziInt = tcdiziStr.Select(s => Convert.ToInt32(s)).ToArray();
            const string numControlStr = "0123456789";

            if (tcdiziInt.Length != 11) return false;

            for (var i = 0; i <= 10; i++)
            {
                if (!numControlStr.Contains(tcdiziStr[i])) return false;

                if (i < 10) genelkont += tcdiziInt[i];
                if (i <= 8 && i % 2 == 0) kontroltek += tcdiziInt[i];
                if (i <= 7 && (i + 1) % 2 == 0) kontrolcift += tcdiziInt[i];
            }

            return tcdiziInt[9] == ((kontroltek * 7) - kontrolcift) % 10 && genelkont % 10 == tcdiziInt[10];
        }
        public static void SendEmail(string from, string to, string subject, string body)
        {
            // Web Config:
            //<configuration>

            //...

            //  <system.net>
            //    <mailSettings>
            //      <!--<smtp from="test@foo.com">
            //        <network host="smtpserver1" port="25" userName="username" password="secret" defaultCredentials="true" />
            //      </smtp>-->
            //      <!--<smtp deliveryMethod="Network">
            //        <network host="smtp.mysite.com" userName="myuser" password="mypassword" />
            //      </smtp>-->
            //      <smtp deliveryMethod="SpecifiedPickupDirectory">
            //        <specifiedPickupDirectory pickupDirectoryLocation="C:\email\"/>
            //        <network host="localhost"/>
            //      </smtp>
            //    </mailSettings>
            //  </system.net>
            //</configuration>

            var fromAddress = new MailAddress(from);
            var toAddress = new MailAddress(to);

            var message = new MailMessage(fromAddress, toAddress)
            {
                Subject = subject,
                Body = body,
                IsBodyHtml = true
            };

            var client = new SmtpClient();
            client.Send(message);
        }
        public static T GetEnumValueFromDescription<T>(string description)
        {
            var type = typeof(T);
            if (!type.IsEnum) throw new InvalidOperationException();
            foreach (var field in type.GetFields())
            {
                var attribute = Attribute.GetCustomAttribute(field,
                    typeof(DescriptionAttribute)) as DescriptionAttribute;
                if (attribute != null)
                {
                    if (attribute.Description == description)
                        return (T)field.GetValue(null);
                }
                else
                {
                    if (field.Name == description)
                        return (T)field.GetValue(null);
                }
            }
            //throw new ArgumentException("Not found.", "description");
            return default(T);
        }
    }

    public static class Extensionmethods
    {
        public static void Equate2Object<T>(this T t, T s)
        {
            var propertyInfos1 = t.GetType().GetProperties();
            var propertyInfos2 = s.GetType().GetProperties();

            foreach (var target in propertyInfos1)
            {
                var source = propertyInfos2.FirstOrDefault(x => x.Name == target.Name);

                if (source != null)
                {
                    var value = source.GetValue(s, null);
                    target.SetValue(t, value, null);
                }
            }
        }
        public static bool IsNull<T>(this T obj)
        {
            var isNull = false;

            if (typeof(T) == typeof(String))
                return String.IsNullOrEmpty(obj as string);

            isNull = (null == obj);

            return isNull;
        }
        public static bool ValidateEmail(this string email)
        {
            const string regNonEnglishCharacter = @"[^\u0000-\u0080]+";

            try
            {
                var isNonEnglishChars = Regex.IsMatch(email, regNonEnglishCharacter);
                if (isNonEnglishChars)
                    return false;

                var e = new MailAddress(email);
                return true;
            }
            catch (FormatException)
            {
                return false;
            }
        }
        public static bool TcNoKontrol(this string tcNo)
        {
            var allowChars = new HashSet<char>("0123456789");
            if (!tcNo.All(allowChars.Contains)) return false;

            var genelkont = 0;
            var kontroltek = 0;
            var kontrolcift = 0;
            var tcdiziStr = tcNo.Select(c => c.ToString()).ToArray();
            var tcdiziInt = tcdiziStr.Select(s => Convert.ToInt32(s)).ToArray();

            if (tcdiziInt.Length != 11) return false;

            for (var i = 0; i <= 10; i++)
            {
                if (i < 10) genelkont += tcdiziInt[i];
                if (i <= 8 && i % 2 == 0) kontroltek += tcdiziInt[i];
                if (i <= 7 && (i + 1) % 2 == 0) kontrolcift += tcdiziInt[i];
            }

            return tcdiziInt[9] == ((kontroltek * 7) - kontrolcift) % 10 && genelkont % 10 == tcdiziInt[10];
        }
        public static string Md5Hash(this string text)
        {
            var md5 = new MD5CryptoServiceProvider();

            //compute hash from the bytes of text
            md5.ComputeHash(Encoding.ASCII.GetBytes(text));

            //get hash result after compute it
            var result = md5.Hash;

            var strBuilder = new StringBuilder();
            for (var i = 0; i < result.Length; i++)
            {
                //change it into 2 hexadecimal digits
                //for each byte
                strBuilder.Append(result[i].ToString("x2"));
            }

            return strBuilder.ToString();
        }
        public static string TimePassed(this DateTime inputDateTime)
        {
            var ts = new TimeSpan(DateTime.UtcNow.Ticks - inputDateTime.Ticks);
            var delta = Math.Abs(ts.TotalSeconds);

            const int second = 1;
            const int minute = 60 * second;
            const int hour = 60 * minute;
            const int day = 24 * hour;
            const int month = 30 * day;

            if (delta < 0)
            {
                return "henüz değil";
            }
            if (delta < 1 * minute)
            {
                return Math.Abs(ts.Seconds) == 1 ? "1 saniye önce" : Math.Abs(ts.Seconds) + " saniye önce";
            }
            if (delta < 2 * minute)
            {
                return "1 dakika önce";
            }
            if (delta < 45 * minute)
            {
                return Math.Abs(ts.Minutes) + " dakika önce";
            }
            if (delta < 90 * minute)
            {
                return "1 saat önce";
            }
            if (delta < 24 * hour)
            {
                return Math.Abs(ts.Hours) + " saat önce";
            }
            if (delta < 48 * hour)
            {
                return "1 gün önce";
            }
            if (delta < 30 * day)
            {
                return Math.Abs(ts.Days) + " gün önce";
            }
            if (delta < 12 * month)
            {
                var months = Convert.ToInt32(Math.Floor((double)ts.Days / 30));
                return Math.Abs(months) <= 1 ? "1 ay önce" : Math.Abs(months) + " ay önce";
            }
            else
            {
                var years = Convert.ToInt32(Math.Floor((double)ts.Days / 365));
                return Math.Abs(years) <= 1 ? "1 yıl önce" : Math.Abs(years) + " yıl önce";
            }
        }
        public static string ToSlug(this string text)
        {
            var sb = new StringBuilder();
            var lastWasInvalid = false;

            foreach (var c in text.ToLower())
            {
                if (Char.IsLetterOrDigit(c))
                {
                    sb.Append(c);
                    lastWasInvalid = false;
                }
                else
                {
                    if (!lastWasInvalid)
                        sb.Append("-");
                    lastWasInvalid = true;
                }
            }

            var slug = sb.ToString().ToLowerInvariant().Trim();

            while (slug[slug.Length - 1] == '-')
            {
                slug = slug.Substring(0, slug.Length - 1);
            }

            return slug;
        }
        public static bool IsHexaDecimal(this string kod)
        {
            if (kod.IsNull())
                return false;

            var allowChars = new HashSet<char>("0123456789ABCDEF");
            return kod.All(allowChars.Contains) && kod.Length == 32;
        }
        public static bool In<T>(this T source, params T[] list)
        {
            if (null == source) throw new ArgumentNullException("source");
            return list.Contains(source);
        }
        public static string GetDescription(this Enum value)
        {
            Type type = value.GetType();
            string name = Enum.GetName(type, value);
            if (name != null)
            {
                FieldInfo field = type.GetField(name);
                if (field != null)
                {
                    DescriptionAttribute attr = Attribute.GetCustomAttribute(field, typeof(DescriptionAttribute)) as DescriptionAttribute;
                    if (attr != null)
                    {
                        return attr.Description;
                    }
                }
            }
            return null;
        }
    }

    public static class LinqExtensions
    {

        // Compare objects via ID's : http://akshayluther.com/2009/08/14/improving-linq-except-and-intersect/
        public static IEnumerable<TSource> Except<TSource>(this IEnumerable<TSource> first, IEnumerable<TSource> second,
                                                           Func<TSource, TSource, bool> comparer)
        {
            return first.Where(x => second.Count(y => comparer(x, y)) == 0);
        }

        // Compare objects via ID's : http://akshayluther.com/2009/08/14/improving-linq-except-and-intersect/
        public static IEnumerable<TSource> Intersect<TSource>(this IEnumerable<TSource> first,
                                                              IEnumerable<TSource> second,
                                                              Func<TSource, TSource, bool> comparer)
        {
            return first.Where(x => second.Count(y => comparer(x, y)) == 1);
        }
    }
}
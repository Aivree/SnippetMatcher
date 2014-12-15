using System;
using System.Collections.Generic;
using System.Drawing;
using System.Drawing.Drawing2D;
using System.Drawing.Imaging;
using System.IO;
using System.Linq;
using System.Security.Cryptography;
using System.Text;
using System.Web;

namespace Utilities
{
    public class General
    {
        /// <summary>
        /// Add separator(passed as parameter) between the values of a string array(passed as parameter) and returns as a string
        /// </summary>
        /// <param name="values"></param>
        /// <param name="separator"></param>
        /// <param name="start"></param>
        /// <param name="end"></param>
        /// <returns></returns>
        public static string GetSeparatedValues(string[] values, string separator, string start, string end)
        {
            string result_str = "";
            if (start == null) start = string.Empty;
            if (end == null) end = string.Empty;
            if (values != null)
            {
                foreach (string str in values)
                {
                    result_str += ((result_str != "") ? separator : "") + start + str.ToString() + end;
                }
            }
            return result_str;
        }

        /// <summary>
        /// Add comma(with quote or without quote) between the values of a string array(passed as parameter) and returns as a string
        /// </summary>
        /// <param name="str_arr"></param>
        /// <param name="withQuote"></param>
        /// <returns></returns>
        public static string GetCommaSeparatedValues(string[] str_arr, bool withQuote = false)
        {
            string result_str = "";
            string quote = withQuote ? "'" : "";
            result_str = GetSeparatedValues(str_arr, ",", quote, quote);
            return result_str;
        }

        /// <summary>
        /// Add comma(with quote or without quote) between the list of a string(passed as parameter) and returns as a string
        /// </summary>
        /// <param name="str_list"></param>
        /// <param name="withQuote"></param>
        /// <returns></returns>
        public static string GetCommaSeparatedValues(List<string> str_list, bool withQuote = false)
        {
            if (str_list != null)
            {
                return GetCommaSeparatedValues(str_list.ToArray(), withQuote);
            }
            else
            {
                return string.Empty;
            }
        }

        public static string GetRandomNumbers(int numChars)
        {
            string[] chars = { "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "P", "Q", "R", "S",
                                "T", "U", "V", "W", "X", "Y", "Z","0","1", "2", "3", "4", "5", "6", "7", "8", "9" };

            Random rnd = new Random();
            string random = string.Empty;
            for (int i = 0; i < numChars; i++)
            {
                random += chars[rnd.Next(0, 33)];
            }
            return random;
        }

        public static string MD5Hash(string plainText)
        {
            // Create a new instance of the MD5CryptoServiceProvider object.
            MD5 md5Hasher = MD5.Create();

            // Convert the input string to a byte array and compute the hash.
            byte[] data = md5Hasher.ComputeHash(Encoding.UTF8.GetBytes(plainText));

            // Create a new Stringbuilder to collect the bytes
            // and create a string.
            StringBuilder sBuilder = new StringBuilder();

            // Loop through each byte of the hashed data 
            // and format each one as a hexadecimal string.
            for (int i = 0; i < data.Length; i++)
            {
                sBuilder.Append(data[i].ToString("x2"));
            }

            // Return the hexadecimal string.
            return sBuilder.ToString().ToUpper();
        }

        public static string ShowImage(byte[] image)
        {
            var base64 = Convert.ToBase64String(image);
            return String.Format("data:image/jpeg;base64,{0}", base64);
        }

        public static string Truncate(string value, int maxChars)
        {
            if (value.IsBlank())
            {
                return value;
            }
            return value.Length <= maxChars ? value : value.Substring(0, maxChars - 4) + "...";
        }

        public static string GetRelativeTime(DateTime dt)
        {
            const int SECOND = 1;
            const int MINUTE = 60 * SECOND;
            const int HOUR = 60 * MINUTE;
            const int DAY = 24 * HOUR;
            const int MONTH = 30 * DAY;
            var ts = new TimeSpan(DateTime.UtcNow.Ticks - dt.Ticks);
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

        public static AttachmentType GetAttachmentType(string fileFormat)
        {
            var attachmentType = AttachmentType.Others;

            switch (fileFormat.ToLower())
            {
                case ".jpg":
                case ".jpeg":
                case ".png":
                case ".bmp":
                case ".gif":
                case ".tif":
                case ".exif":
                case ".tiff":
                case ".raw":
                    attachmentType = AttachmentType.Image;
                    break;

                case ".avi":
                case ".flv":
                case ".mp4":
                case ".3gp":
                case ".webm":
                case ".wmv":
                case ".mov":
                case ".mpg":
                case ".3g2":
                    attachmentType = AttachmentType.Video;
                    break;

                case ".mp3":
                case ".mpa":
                case ".m4a":
                case ".wav":
                case ".wma":
                case ".mid":
                case ".m3u":
                case ".iff":
                case ".aif":
                case ".aac":
                case ".mpc":
                case ".ogg":
                case ".vox":
                case ".wv":
                case ".dct":
                    attachmentType = AttachmentType.Audio;
                    break;

                case ".doc":
                case ".docx":
                case ".csv":
                case ".txt":
                case ".odt":
                case ".rtf":
                    attachmentType = AttachmentType.Doc;
                    break;

                case ".xls":
                case ".xlsx":
                    attachmentType = AttachmentType.Excel;
                    break;

                case ".ppt":
                case ".pptx":
                    attachmentType = AttachmentType.PPT;
                    break;

                case ".pdf":
                    attachmentType = AttachmentType.Pdf;
                    break;

                default:
                    attachmentType = AttachmentType.Others;
                    break;
            }

            return attachmentType;
        }

        #region Image Resize Operation
        public static byte[] ResizeImageFile(int FixWidth, int FixHeight, byte[] imageFile)
        {
            System.Drawing.Image oldImage = System.Drawing.Image.FromStream(new MemoryStream(imageFile));

            int NewHeight = 0;
            int NewWidth = 0;
            int flag = 0;
            byte[] buff = imageFile;
            if (oldImage.Height > oldImage.Width)
            {
                if (oldImage.Height > FixHeight)
                {
                    buff = ResizeImageFile(imageFile, FixHeight);
                    oldImage = System.Drawing.Image.FromStream(new MemoryStream(buff));
                    if (oldImage.Width > FixWidth)
                    {
                        // buff = ResizeImageFile(FixWidth, buff);

                        oldImage = System.Drawing.Image.FromStream(new MemoryStream(buff));

                        NewHeight = oldImage.Height;

                        Bitmap newImage = new Bitmap(FixWidth, NewHeight, PixelFormat.Format24bppRgb);

                        Graphics canvas = Graphics.FromImage(newImage);

                        canvas.SmoothingMode = SmoothingMode.AntiAlias;
                        canvas.InterpolationMode = InterpolationMode.HighQualityBicubic;
                        canvas.PixelOffsetMode = PixelOffsetMode.HighQuality;
                        canvas.DrawImage(oldImage, new System.Drawing.Rectangle(0, 0, FixWidth, NewHeight));
                        MemoryStream m = new MemoryStream();
                        newImage.Save(m, ImageFormat.Jpeg);
                        return m.GetBuffer();



                    }
                }

            }
            else
            {
                if (oldImage.Width > FixWidth)
                {
                    buff = ResizeImageFile(FixWidth, imageFile);
                    oldImage = System.Drawing.Image.FromStream(new MemoryStream(buff));
                    if (oldImage.Height > FixHeight)
                    {
                        // buff = ResizeImageFile(buff, FixHeight);

                        oldImage = System.Drawing.Image.FromStream(new MemoryStream(buff));

                        NewWidth = oldImage.Width;

                        Bitmap newImage = new Bitmap(NewWidth, FixHeight, PixelFormat.Format24bppRgb);

                        Graphics canvas = Graphics.FromImage(newImage);

                        canvas.SmoothingMode = SmoothingMode.AntiAlias;
                        canvas.InterpolationMode = InterpolationMode.HighQualityBicubic;
                        canvas.PixelOffsetMode = PixelOffsetMode.HighQuality;
                        canvas.DrawImage(oldImage, new System.Drawing.Rectangle(0, 0, NewWidth, FixHeight));
                        MemoryStream m = new MemoryStream();
                        newImage.Save(m, ImageFormat.Jpeg);
                        return m.GetBuffer();
                    }
                }
            }
            return buff;
        }
        /// <summary>
        /// Resize according to width and converts in byte[]
        /// </summary>
        /// <param name="FixWidth"></param>
        /// <param name="imageFile"></param>
        /// <returns></returns>
        public static byte[] ResizeImageFile(int FixWidth, byte[] imageFile)
        {
            using (System.Drawing.Image oldImage = System.Drawing.Image.FromStream(new MemoryStream(imageFile)))
            {
                int NewHeight = (int)(oldImage.Height * ((float)FixWidth / (float)oldImage.Width));

                using (Bitmap newImage = new Bitmap(FixWidth, NewHeight, PixelFormat.Format24bppRgb))
                {
                    using (Graphics canvas = Graphics.FromImage(newImage))
                    {
                        canvas.SmoothingMode = SmoothingMode.AntiAlias;
                        canvas.InterpolationMode = InterpolationMode.HighQualityBicubic;
                        canvas.PixelOffsetMode = PixelOffsetMode.HighQuality;
                        canvas.DrawImage(oldImage, new System.Drawing.Rectangle(0, 0, FixWidth, NewHeight));
                        MemoryStream m = new MemoryStream();
                        newImage.Save(m, ImageFormat.Jpeg);
                        return m.GetBuffer();
                    }
                }
            }
        }

        // Resize according to height
        public static byte[] ResizeImageFile(byte[] imageFile, int FixHeight)
        {
            using (System.Drawing.Image oldImage = System.Drawing.Image.FromStream(new MemoryStream(imageFile)))
            {
                int NewWidth = (int)(oldImage.Width * ((float)FixHeight / (float)oldImage.Height));
                using (Bitmap newImage = new Bitmap(NewWidth, FixHeight, PixelFormat.Format24bppRgb))
                {
                    using (Graphics canvas = Graphics.FromImage(newImage))
                    {
                        canvas.SmoothingMode = SmoothingMode.AntiAlias;
                        canvas.InterpolationMode = InterpolationMode.HighQualityBicubic;
                        canvas.PixelOffsetMode = PixelOffsetMode.HighQuality;
                        canvas.DrawImage(oldImage, new System.Drawing.Rectangle(0, 0, NewWidth, FixHeight));
                        MemoryStream m = new MemoryStream();
                        newImage.Save(m, ImageFormat.Jpeg);
                        return m.GetBuffer();
                    }
                }
            }
        }

        public static byte[] StreamToByte(Stream input)
        {
            byte[] buffer = new byte[16 * 1024];
            using (MemoryStream ms = new MemoryStream())
            {
                int read;
                while ((read = input.Read(buffer, 0, buffer.Length)) > 0)
                {
                    ms.Write(buffer, 0, read);
                }
                return ms.ToArray();
            }
        }

        #endregion
    }
}
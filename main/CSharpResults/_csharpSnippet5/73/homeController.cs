using System;
using System.Collections.Generic;
using System.Configuration;
using System.Linq;
using System.Security.Cryptography;
using System.Text;
using System.Web;
using System.Web.Mail;
using System.Web.Mvc;
using System.Web.Security;
using System.Web.UI;
using EC08_SHBS.Models;

namespace EC08_SHBS.Controllers.Guest
{
    public class homeController : Controller
    {
        PortalEntities db = new PortalEntities();

        //Load danh sách khách sạn trên home page
        public ActionResult Index()
        {
            var khachSan = db.KhachSans.OrderByDescending(m => m.diemDanhGia).Take(5);
            return View(khachSan);
        }

        //Danh sách phòng  của khách sạn
        public ActionResult detail(string id)
        {
            var khachSan = db.KhachSans.Where(x => x.idKS.Equals(id)).ToList();
            return View(khachSan);
        }

        //Đăng nhập tài khoản
        public ActionResult login()
        {
            return View("login");
        }

        [HttpPost]
        public ActionResult login(NguoiDungModel model, string returnUrl)
        {
            if (ModelState.IsValid)
            {
                string passmh = MaHoaMD5(model.Password);
                List<NguoiDung> lstNguoiDung = db.NguoiDungs.Where(x => x.userName.Equals(model.UserName) && x.passWord.Equals(passmh)).ToList();
                if (lstNguoiDung.Count > 0)
                {
                    FormsAuthentication.SetAuthCookie(model.UserName, false);

                    if (db.NguoiDungs.First(x => x.userName.Equals(model.UserName)).LoaiNguoiDung.tenLoai == "Admin")
                    {
                        return RedirectToAction("index", "admin");
                    }
                    else if (Url.IsLocalUrl(returnUrl) && returnUrl.Length > 1 && returnUrl.StartsWith("/")
                        && !returnUrl.StartsWith("//") && !returnUrl.StartsWith("/\\"))//doan code nay nham muc dich khi ma ban vao mot trang nao do mo khong dung quyen han no se bat ban dang nhap.neu thanh cong no se tro lai trang truoc do ma ban muon vao.
                    {
                        return Redirect(returnUrl); //tro lai trang truoc do ma ban muon vao
                    }
                    else
                    {
                        return RedirectToAction("index", "home"); //chuyen sang trang Index cua controllers Home.
                    }
                }
                else
                {
                    ModelState.AddModelError("", "Tên đăng nhập hoặc mật khẩu không đúng."); //neu khong tim thay tai khoan thi hien thi loi sai tai khoan

                }
            }
            return View("login", model);
        }

        //Đăng xuất
        public ActionResult logout()
        {
            FormsAuthentication.SignOut();
            return RedirectToAction("index", "home");
        }

        //Đăng ký tài khoản
        public ActionResult register()
        {
            return View("register");
        }
        public string GetLastID_ND()
        {
            //var q = db.NguoiDungs.OrderByDescending(x => x.IdNguoiDung
            List<NguoiDung> listnd = null;
            string result = null;
            listnd = db.NguoiDungs.OrderByDescending(x => x.IdNguoiDung).ToList();
            if (listnd.Count != 0)
            {
                result = listnd[0].IdNguoiDung.ToString();
            }
            return result;
        }
        //Ham lay tao ma tiep theo
        // chu y: lenght la chiu dai cua ma ngoai tru` ki tu viet tat
        //vd: HS001 lenght=3
        // nen khi su dung koi chung nham lan
        //author: utkung
        public string GetNextID(string KtVietTat, string nameTable, string MaCuoi, int lenght)
        {
            if (MaCuoi == "" || MaCuoi == null)
            {
                MaCuoi = KtVietTat + new string('0', lenght);
            }
            int maTiepTheo = int.Parse(MaCuoi.Remove(0, KtVietTat.Length)) + 1;
            string n = "";
            for (int i = 1; i <= lenght; i++)
            {
                if (maTiepTheo < Math.Pow(10, i))
                {
                    for (int j = 1; j <= lenght - i; i++)
                    {
                        n = n + "0";
                    }
                    return KtVietTat + n + maTiepTheo.ToString();
                }
            }
            return KtVietTat + maTiepTheo;
        }
        public static string MaHoaMD5(string text)
        {
            var md5Hasher = new MD5CryptoServiceProvider();
            byte[] bs = Encoding.UTF8.GetBytes(text);
            bs = md5Hasher.ComputeHash(bs);
            var s = new StringBuilder();
            foreach (byte b in bs)
            {
                s.Append(b.ToString("x2"));
            }
            return s.ToString();
        }
        [HttpPost]
        public ActionResult register(NguoiDungInfo model)
        {
            if (ModelState.IsValid)
            {
                List<NguoiDung> lstNguoiDung = db.NguoiDungs.Where(x => x.userName.Equals(model.userName)).ToList();
                if (lstNguoiDung.Count == 0)
                {
                    NguoiDung nguoiDung = new NguoiDung();
                    string lastid = GetLastID_ND();
                    string nextid = GetNextID("ND", "NguoiDung", lastid, 8);
                    nguoiDung.IdNguoiDung = nextid;//"nd00000001";
                    nguoiDung.eMail = model.eMail;
                    nguoiDung.userName = model.userName;
                    nguoiDung.passWord = MaHoaMD5(model.passWord);
                    nguoiDung.hoTen = model.hoTen;
                    nguoiDung.diaChi = model.diaChi;
                    nguoiDung.dienThoai = model.dienThoai;
                    nguoiDung.loaiND = 1;
                    nguoiDung.tinhTrang = 1;
                    nguoiDung.diemTong = 0;

                    db.USP_DANGKY(nextid, model.eMail, model.userName, MaHoaMD5(model.passWord), model.hoTen, model.diaChi, model.dienThoai, 1, 1, 0);
                    //db.NguoiDungs.Add(nguoiDung);
                    //db.SaveChanges();

                    FormsAuthentication.SetAuthCookie(model.userName, false);
                    return RedirectToAction("registercomplete", "home");
                }
                else
                {
                    ModelState.AddModelError("", "Tài khoản này đã tồn tại.");
                }
            }
            return View("register", model);
        }

        public ActionResult registercomplete()
        {
            return View();
        }


        //Chi tiết đặt phòng
        public ActionResult booking()
        {
            string idKS = Request["idKhachSan"];
            string idP = Request["idLoaiPhong"];
            return View();
        }
        [HttpPost]
        public ActionResult datphong(PhieuGiaoDich pgd,int soPhong,string giatri)
        {
            
            string idKS = Request["idKhachSan"];
            string idP = Request["idLoaiPhong"];
            string ngaynhan = Request["ngayNhanPhong"];
            string ngaytra = Request["ngayTraPhong"];
            string URL = Request["URL"];
            string stringsend = "";

            if (User.Identity.IsAuthenticated)
            {
                string username = User.Identity.Name;
                NguoiDung nguoidung = db.NguoiDungs.First(a => a.userName.Equals(username));
                stringsend = nguoidung.hoTen + "," + nguoidung.diaChi + "," + nguoidung.dienThoai + "," + nguoidung.eMail + "," + giatri + "," + ngaynhan + "," + ngaytra + "," + soPhong.ToString() + "," + idP;
            }
            else
            {
                stringsend = pgd.NguoiDung.hoTen + "," + pgd.NguoiDung.diaChi + "," + pgd.NguoiDung.dienThoai + "," + pgd.NguoiDung.eMail + "," + giatri + "," + ngaynhan + "," + ngaytra + "," + soPhong.ToString() + "," + idP;
            }
            
            //Gọi lên WS
            WebService ws = new WebService(URL, "DatPhongChoKS");
            ws.Params.Add("pdp", stringsend);
            ws.Invoke(false);

            //Lưu lại thông tin đặt phòng của người dùng để lưu vào portal, 
            //và cập nhật lại điểm thưởng của người dùng để ưu đãi cho người dùng
            DateTime ngaynhanPhong = DateTime.ParseExact(ngaynhan, "dd/MM/yyyy", null);
            DateTime ngaytraPhong = DateTime.ParseExact(ngaytra, "dd/MM/yyyy", null);

            //Đặt phòng nếu là người dùng
            if (User.Identity.IsAuthenticated)
            {
                string username = User.Identity.Name;
                string idnguoidung = db.NguoiDungs.First(a => a.userName.Equals(username)).IdNguoiDung;
                db.usp_ThemPhieuGiaoDich(float.Parse(giatri), DateTime.Now, 1, ngaynhanPhong, ngaytraPhong, int.Parse(Request["sophong"]), pgd.ghiChu, idP, idnguoidung, idKS, 1);
            }

            //Đặt phòng cho khách viếng thăm
            else
            {
                db.usp_ThemPhieuGiaoDichKhachViengTham(float.Parse(giatri), DateTime.Now, 1, ngaynhanPhong, ngaytraPhong, int.Parse(Request["sophong"]), pgd.ghiChu, idP, idKS, 1, pgd.NguoiDung.hoTen, pgd.NguoiDung.eMail, pgd.NguoiDung.diaChi, pgd.NguoiDung.dienThoai);
            }

            string thongTin = Request["sophong"] + " room(s) " + Request["bLoaiPhong"];
            string tongTien = Request["giatri"];

            bool useSandbox = Convert.ToBoolean(ConfigurationManager.AppSettings["IsSandbox"]);
            var paypal = new PaypalModel(useSandbox);

            paypal.item_name = thongTin;
            paypal.amount = tongTien;

            return View(paypal);
        }
        //Bình luận đánh giá cho khách sạn
        [HttpPost]
        public ActionResult binhluan(string hoten, string email, string tieude, string noidung, int danhgia)
        {
            string idKS = Request["idKhachSan"];
            string rdr = "detail/" + idKS;
            if (User.Identity.IsAuthenticated)
            {
                string username = User.Identity.Name;
                NguoiDung nguoidung = db.NguoiDungs.First(a => a.userName.Equals(username));
                if (tieude == "" || noidung == "" || danhgia == 1)
                {
                    //Không thêm comment này
                }
                else
                {
                    db.usp_BinhLuanKS(idKS, noidung, tieude, DateTime.Now, danhgia, nguoidung.hoTen, nguoidung.eMail);
                }
            }
            else
            {
                if (hoten == "" || email == "" || tieude == "" || noidung == "" || danhgia == 1)
                {
                    //Không thêm comment này
                }
                else
                {
                    db.usp_BinhLuanKS(idKS, noidung, tieude, DateTime.Now, danhgia, hoten, email);
                }
            }
            return RedirectToAction(rdr, "home");
        }
        //Thông tin cá nhân
        [Authorize(Roles = "User")]
        public ActionResult info()
        {
            var nguoiDung = db.NguoiDungs.First(x => x.userName.Equals(@User.Identity.Name));

            NguoiDungInfo nd = new NguoiDungInfo();
            nd.IdNguoiDung = nguoiDung.IdNguoiDung;
            nd.eMail = nguoiDung.eMail;
            nd.userName = nguoiDung.userName;
            nd.passWord = nguoiDung.passWord;
            nd.hoTen = nguoiDung.hoTen;
            nd.diaChi = nguoiDung.diaChi;
            nd.dienThoai = nguoiDung.dienThoai;
            nd.loaiND = (int)nguoiDung.loaiND;
            nd.tinhTrang = (int)nguoiDung.tinhTrang;
            nd.diemTong = (int)nguoiDung.diemTong;

            return View(nd);
        }

        [Authorize(Roles = "User")]
        [HttpPost]
        public ActionResult info(NguoiDungInfo nguoiDung)
        {

            var model = db.NguoiDungs.First(x => x.IdNguoiDung.Equals(nguoiDung.IdNguoiDung));
            if (nguoiDung.passWord != null)
            {
                db.usp_capNhatThongTinNguoiDung(nguoiDung.IdNguoiDung, nguoiDung.eMail, nguoiDung.userName, nguoiDung.passWord, nguoiDung.hoTen, nguoiDung.diaChi, nguoiDung.dienThoai, nguoiDung.loaiND, nguoiDung.tinhTrang, nguoiDung.diemTong);
                return RedirectToAction("infocomplete", "home");
            }
            else
            {
                db.usp_capNhatThongTinNguoiDung(nguoiDung.IdNguoiDung, nguoiDung.eMail, nguoiDung.userName, model.passWord, nguoiDung.hoTen, nguoiDung.diaChi, nguoiDung.dienThoai, nguoiDung.loaiND, nguoiDung.tinhTrang, nguoiDung.diemTong);
                return RedirectToAction("infocomplete", "home");
            }
            return View("info", model);
        }

        [Authorize(Roles = "User")]
        public ActionResult infocomplete()
        {
            return View();
        }

        //Thông tin đặt phòng
        [Authorize(Roles = "User")]
        public ActionResult bookingdetail()
        {
            var giaoDich = db.PhieuGiaoDiches.Where(x => x.NguoiDung.userName.Equals(User.Identity.Name)).ToList();
            
            return View(giaoDich);
        }


        //Tìm kiếm khách sạn
        public ActionResult search()
        {
            int vung = int.Parse(Request["tenVung"]);
            float gia = float.Parse(Request["idGia"]);

            var khachSan = db.KhachSans.Where(x => x.idVung == vung);

            return View(khachSan);
        }

        //Hủy đặt phòng
        [Authorize(Roles = "User")]
        public ActionResult huydatphong()
        {
            string fromMail = "tmdt.ec08@gmail.com";
            string fromPass = "abcdxyz09";
            string toMail = db.NguoiDungs.First(x => x.userName.Equals(User.Identity.Name)).eMail;
            string subject = "Cancel Hotel Reservation";
            string body = "You have cancelled booking at Portal EC08. We will contact you in 24 hours!!!";

            sendMail(fromMail, fromPass, toMail, subject, body);

            return View();
        }

        //Send mail
        private void sendMail(string fromMail, string fromPass, string toMail, string subject, string body)
        {
            System.Web.Mail.MailMessage myMail = new System.Web.Mail.MailMessage();

            myMail.Fields.Add("http://schemas.microsoft.com/cdo/configuration/smtpserver", "smtp.gmail.com");
            myMail.Fields.Add("http://schemas.microsoft.com/cdo/configuration/smtpserverport", "465");
            myMail.Fields.Add("http://schemas.microsoft.com/cdo/configuration/sendusing", "2");

            myMail.Fields.Add("http://schemas.microsoft.com/cdo/configuration/smtpauthenticate", "1");

            myMail.Fields.Add("http://schemas.microsoft.com/cdo/configuration/sendusername", fromMail);
            myMail.Fields.Add("http://schemas.microsoft.com/cdo/configuration/sendpassword", fromPass);
            myMail.Fields.Add("http://schemas.microsoft.com/cdo/configuration/smtpusessl", "true");
            myMail.From = fromMail;
            myMail.To = toMail;
            myMail.Subject = subject;
            myMail.BodyFormat = MailFormat.Html;
            myMail.Body = body;

            SmtpMail.SmtpServer = "smtp.gmail.com:465";

            SmtpMail.Send(myMail);
        }

        //chức năng đang xây dựng
        public ActionResult build()
        {
            return View();
        }
    }
}

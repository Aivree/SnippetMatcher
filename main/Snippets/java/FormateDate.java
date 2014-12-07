import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class FormateDate {

    public static void main(String[] args) throws ParseException {
        String date_s = "2011-01-18 00:00:00.0";

        // *** note that it's "yyyy-MM-dd hh:mm:ss" not "yyyy-mm-dd hh:mm:ss"
        SimpleDateFormat dt = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        Date date = dt.parse(date_s);

        // *** same for the format String below
        SimpleDateFormat dt1 = new SimpleDateFormat("yyyy-MM-dd");
        System.out.println(dt1.format(date));
    }

}
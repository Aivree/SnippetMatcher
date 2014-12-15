import java.text.SimpleDateFormat;
import java.util.*;

public class getTime
{
        public static String forReport()
        {
                Calendar cal = Calendar.getInstance();
                cal.getTime();
                SimpleDateFormat sdf = new SimpleDateFormat("MM-dd-YYYY_hh:MM:ss");
                //can make excel rule such that the deliminator is the '_'
                return ( sdf.format(cal.getTime()) );
        }
        
        public static String forFileName()
        {
                Calendar cal = Calendar.getInstance();
                cal.getTime();
                SimpleDateFormat sdf = new SimpleDateFormat("MM-dd-YYYY-HH_mm_ss");
                return ( sdf.format(cal.getTime()) );
        }
}
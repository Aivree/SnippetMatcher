
package org.javase7.puzzle.so;

import java.text.ParseException;
import java.util.TimeZone;

/**
 * http://stackoverflow.com/questions/6841333/why-is-subtracting-these-two-times-in-1927-giving-a-strange-result
 * @author Masudul Haque
 */
public class SubtractDate {
public static void main(String[] args) throws ParseException {
//    SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");  
//    String str3 = "1927-12-31 23:54:07";  
//    String str4 = "1927-12-31 23:54:08";  
//    Date sDt3 = sf.parse(str3);  
//    Date sDt4 = sf.parse(str4);  
//    long ld3 = sDt3.getTime() /1000;  
//    long ld4 = sDt4.getTime() /1000; 
//    System.out.println(ld3);  
//    System.out.println(ld4);  
//    System.out.println(ld4-ld3);
    long startOf1900Utc = -2208988800000L;
        for (String id : TimeZone.getAvailableIDs()) {
            TimeZone zone = TimeZone.getTimeZone(id);
            if (zone.getRawOffset() != zone.getOffset(startOf1900Utc - 1)) {
                System.out.println(id);
            }
        }
}
}

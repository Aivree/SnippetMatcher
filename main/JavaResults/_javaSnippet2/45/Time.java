package net.therap.spring;

import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created with IntelliJ IDEA.
 * User: sk.saad
 * Date: 7/30/13
 * Time: 1:54 PM
 * To change this template use File | Settings | File Templates.
 */

public class Time {
    private Calendar calendar = null;
    private SimpleDateFormat simpleDateFormat = null;

    public Time() {
        calendar = Calendar.getInstance();
        simpleDateFormat = new SimpleDateFormat("HH:mm:ss");
    }

    public String getCurrentTime() {
        return simpleDateFormat.format( calendar.getTime() ) ;
    }
}

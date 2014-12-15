package ext.java.utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class FileNameGenerator
{
    public String getName()
    {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd_HHmmss");
        return simpleDateFormat.format(getDate());
    }

    public Date getDate()
    {
        return Calendar.getInstance().getTime();
    }
}

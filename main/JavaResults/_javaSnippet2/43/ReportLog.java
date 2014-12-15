package com.rc.devkit.reporter;

import java.text.SimpleDateFormat;
import java.util.Date;

public class ReportLog
{
    private SimpleDateFormat simpleDateFormat;
    private String message;
    private Date created;
    private String methodName;

    public ReportLog(String message, String methodName)
    {
        this.message = message;
        this.methodName = methodName;
        this.created = new Date();
    }

    public void setSimpleDateFormat(SimpleDateFormat simpleDateFormat)
    {
        this.simpleDateFormat = simpleDateFormat;
    }

    public String toString()
    {
        if (simpleDateFormat != null) {
            return simpleDateFormat.format(created) + ": " + methodName + ": " + message;
        }

        return created.getTime() + ": " + methodName + ": " + message;
    }
}

/**
 * zoneland.net Inc.
 * Copyright (c) 2002-2013 All Rights Reserved.
 */
package com.foodoon.info.common.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * 
 * @author foodoon
 * @version $Id: DateUtil.java, v 0.1 2013-7-24 下午2:28:30 foodoon Exp $
 */
public class DateUtil {

    public static String getLastDay(int days) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, 0 - days);// 前一天的日期时间
        return simpleDateFormat.format(calendar.getTime());
    }

    public static String getTodayStr() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd");
        return simpleDateFormat.format(new Date());
    }

    public static String getChsDay(Date date) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy年MM月dd日HH时mm分ss秒");
        return simpleDateFormat.format(date);
    }

    public static Date getLastDays(int days) {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, 0 - days);// 前一天的日期时间
        return calendar.getTime();
    }

}

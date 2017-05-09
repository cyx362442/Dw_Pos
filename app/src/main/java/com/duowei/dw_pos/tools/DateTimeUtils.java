package com.duowei.dw_pos.tools;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * //
 * Created by Administrator on 2017-01-05.
 */

public class DateTimeUtils {

    /**
     * 根据当前日期，返回星期几
     *
     * @param datetime 例: 20170105T00:00:00
     * @return 返回 Z1 - Z7
     */
    public static String getWeekCode(String datetime) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd'T'HH:mm:ss", Locale.CHINA);
            Date date = sdf.parse(datetime);
            Calendar c = Calendar.getInstance();
            c.setTime(date);
            c.setFirstDayOfWeek(Calendar.MONDAY);
            int temp = c.get(Calendar.DAY_OF_WEEK) - 1;
            if (temp == 0) {
                temp = 7;
            }
            return "Z" + temp;
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * @return yyyyMMddHHmmssS
     */
    public static String getCurrentDatetime() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmssS", Locale.CHINA);
        return sdf.format(Calendar.getInstance().getTime());
    }

    /**
     * @return yyyyMMdd
     */
    public static String getCurrentDatetime1() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd", Locale.CHINA);
        return sdf.format(Calendar.getInstance().getTime());
    }

    /**
     * @return yyyy-MM-dd HH:mm:ss.sss
     */
    public static String getCurrentDatetime2() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.sss", Locale.CHINA);
        return sdf.format(Calendar.getInstance().getTime());
    }

}

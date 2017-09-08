package com.duowei.dw_pos.tools;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Administrator on 2016-12-19.
 */

public class DateTimes {
    public static String getWeek(){
        String z = "";
        Date date = new Date();
        SimpleDateFormat dateFm = new SimpleDateFormat("EEEE");
        String week = dateFm.format(date);
        if (week.equals("星期一")) {
            z = "Z1";
        } else if (week.equals("星期二")) {
            z = "Z2";
        } else if (week.equals("星期三")) {
            z = "Z3";
        } else if (week.equals("星期四")) {
            z = "Z4";
        } else if (week.equals("星期五")) {
            z = "Z5";
        } else if (week.equals("星期六")) {
            z = "Z6";
        } else if (week.equals("星期日")) {
            z = "Z7";
        }
        return z;
    }
    public static String getTime(){
        Date curDate = new Date(System.currentTimeMillis());
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmssSSS");
        String dtime=format.format(curDate);
        return dtime;
    }
    public static String getTime2(){
        Date curDate = new Date(System.currentTimeMillis());
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String dtime=format.format(curDate);
        return dtime;
    }
    public static String getSecond(){
        Date curDate = new Date(System.currentTimeMillis());
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
        String dtime=format.format(curDate);
        return dtime;
    }
}

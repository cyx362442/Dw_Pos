package com.duowei.dw_pos.tools;

import java.text.DecimalFormat;

/**
 * Created by Administrator on 2017-05-24.
 */

public class Format {
    private static DecimalFormat df1;
    private static DecimalFormat df2;

    public static String digitFormat(String num) {
        if (df1 == null) {
            df1 = new DecimalFormat("0.0");
        }
        return df1.format(Float.parseFloat(num));
    }

    public static String digitFormat2(String num) {
        if (df2 == null) {
            df2 = new DecimalFormat("0.000");
        }
        String format = df2.format(Float.parseFloat(num));
        if(format.indexOf(".") > 0){
            format = format.replaceAll("0+?$", "");//去掉多余的0
            format = format.replaceAll("[.]$", "");//如最后一位是.则去掉
        }
        return  format;
    }
}

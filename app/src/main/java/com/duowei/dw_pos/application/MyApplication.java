package com.duowei.dw_pos.application;

import com.duowei.dw_pos.httputils.MyVolley;
import com.duowei.dw_pos.httputils.NetUtils;

import net.danlew.android.joda.JodaTimeAndroid;

import org.litepal.LitePalApplication;

/**
 * Created by Administrator on 2017-03-21.
 */

public class MyApplication extends LitePalApplication {
    @Override
    public void onCreate() {
        super.onCreate();
        MyVolley.init(this);
        JodaTimeAndroid.init(this);
        NetUtils.init();
    }
}

package com.duowei.dw_pos.application;

import com.duowei.dw_pos.httputils.MyVolley;

import org.litepal.LitePalApplication;

/**
 * Created by Administrator on 2017-03-21.
 */

public class MyApplication extends LitePalApplication {
    @Override
    public void onCreate() {
        super.onCreate();
        MyVolley.init(this);
    }
}

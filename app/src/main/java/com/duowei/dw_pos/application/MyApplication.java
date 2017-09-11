package com.duowei.dw_pos.application;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;

import com.duowei.dw_pos.BuildConfig;
import com.duowei.dw_pos.httputils.MyVolley;
import com.duowei.dw_pos.httputils.NetUtils;
import com.tencent.bugly.crashreport.CrashReport;

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

        CrashReport.initCrashReport(getApplicationContext(), "11fd59b5eb", BuildConfig.DEBUG);
        CrashReport.putUserData(getApplicationContext(), "MAC", getMacAddress());
    }

    @SuppressLint("HardwareIds")
    private String getMacAddress() {
        WifiManager manager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        WifiInfo info = manager.getConnectionInfo();
        return info.getMacAddress();
    }
}

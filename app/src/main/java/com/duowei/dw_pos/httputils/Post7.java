package com.duowei.dw_pos.httputils;


import android.util.Log;

import com.android.volley.VolleyError;
import com.duowei.dw_pos.bean.WMLSB;
import com.duowei.dw_pos.event.ChangeTable;
import com.duowei.dw_pos.event.CheckSuccess;
import com.duowei.dw_pos.event.OrderUpdateEvent;
import com.duowei.dw_pos.tools.Net;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by Administrator on 2017-03-27.
 */

public class Post7 {
    private Post7(){}
    private static Post7 post7;
    public static Post7 getInstance(){
        if(post7==null){
            post7=new Post7();
        }
        return post7;
    }
    String result;
    public String getHttpResult(String sql, final WMLSB wmlsb){
        DownHTTP.postVolley7(Net.url, sql, new VolleyResultListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                result="fail";
            }
            @Override
            public void onResponse(String response) {
               if(response.contains("richado")){
                   EventBus.getDefault().post(new OrderUpdateEvent(response));
//                   CartList.newInstance().remove(wmlsb);
               }
            }
        });
        return result;
    }
    /**转台*/
    public void ChangeTable(String table,String wmdbh){
        String sql="update wmlsbjb set zh='"+table+"' where WMDBH='"+wmdbh+"'|";
        DownHTTP.postVolley7(Net.url, sql, new VolleyResultListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
            @Override
            public void onResponse(String response) {
                if(response.contains("richado")){
                    EventBus.getDefault().post(new ChangeTable());
                }
            }
        });
    }
    /**现金结账提交*/
    public void Http_check(String sql, final String payStytle){
        DownHTTP.postVolley7(Net.url, sql, new VolleyResultListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
            @Override
            public void onResponse(String response) {
                if (response.contains("richado")) {
                    EventBus.getDefault().post(new CheckSuccess(payStytle));
                }
            }
        });
    }
    /**扫码支付*/
    public void Http_scan(String sql, final String payStytle){
        DownHTTP.postVolley7(Net.url, sql, new VolleyResultListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
            @Override
            public void onResponse(String response) {
                if (response.contains("richado")) {
                    EventBus.getDefault().post(new CheckSuccess(payStytle));
                }
            }
        });
    }
    //登录云会员
    public void Http_updateWmlsb(String sql){
        DownHTTP.postVolley7(Net.url, sql, new VolleyResultListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
            @Override
            public void onResponse(String response) {
            }
        });
    }
}

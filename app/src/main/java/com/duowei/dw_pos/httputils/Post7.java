package com.duowei.dw_pos.httputils;

import android.net.http.LoggingEventHandler;
import android.util.Log;

import com.android.volley.VolleyError;
import com.duowei.dw_pos.tools.Net;

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
    public String getHttpResult(String sql){
        DownHTTP.postVolley7(Net.url, sql, new VolleyResultListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                result="fail";
                Log.e("=====",error+"");
            }
            @Override
            public void onResponse(String response) {
                result=response;
                Log.e("=====",response);
            }
        });
        return result;
    }
}

package com.duowei.dw_pos.httputils;

import android.util.Log;

import com.android.volley.VolleyError;
import com.duowei.dw_pos.event.ImsCardMembers;
import com.duowei.dw_pos.tools.Net;

/**
 * Created by Administrator on 2017-03-30.
 */

public class Post6 {
    private Post6(){}
    private static Post6 post=null;
    public static Post6 getInstance(){
        if(post==null){
            post=new Post6();
        }
        return post;
    }
    public  void post_ims_card_members(String account,String password,String mWeid){
        String sql="Select b.id,a.from_user,a.cardsn,a.credit1,a.credit2,b.realname,b.mobile,a.status,ifnull(a.cardgrade,'云会员')cardgrade,a.active, b.occupation,\n" +
                "CAST(FROM_UNIXTIME(a.createtime,'%Y-%m-%d')as datetime)createtime \n" +
                "from ims_card_members a,ims_fans b  \n" +
                "where a.weid=b.weid and a.from_user=b.from_user and b.mobile='"+account+"' and b.occupation='"+password+"'  AND a.weid='"+mWeid+"' AND b.weid='"+mWeid+"'|";
        DownHTTP.postVolley6(Net.yunUrl, sql, new VolleyResultListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                org.greenrobot.eventbus.EventBus.getDefault().post(new ImsCardMembers("error"));
            }
            @Override
            public void onResponse(String response) {
                org.greenrobot.eventbus.EventBus.getDefault().post(new ImsCardMembers(response));
            }
        });
    }
}

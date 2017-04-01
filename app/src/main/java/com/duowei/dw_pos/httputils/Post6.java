package com.duowei.dw_pos.httputils;


import android.util.Log;

import com.android.volley.VolleyError;
import com.duowei.dw_pos.event.ImsCardCouponStores;
import com.duowei.dw_pos.event.ImsCardMembers;
import com.duowei.dw_pos.tools.Net;
import com.google.common.eventbus.EventBus;

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
    public  void post_ims_card_members(String account,String password,int mWeid){
        String sql="Select b.id,a.from_user,a.cardsn,a.credit1,a.credit2,b.realname,b.mobile,a.status,ifnull(a.cardgrade,'云会员')cardgrade,a.active, b.occupation,\n" +
                "CAST(FROM_UNIXTIME(a.createtime,'%Y-%m-%d')as datetime)createtime \n" +
                "from ims_card_members a,ims_fans b  \n" +
                "where a.weid=b.weid and a.from_user=b.from_user and b.mobile='"+account+"' and b.occupation='"+password+"'  AND a.weid="+mWeid+" AND b.weid="+mWeid+"|";
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

    public void post_ims_card_coupon_stores(final int weid, final String bmbh, final String from_user){
        String sql = "select couponid from ims_card_coupon_stores where weid=" + weid + " and bmbh='" + bmbh + "'|";
        DownHTTP.postVolley6(Net.yunUrl, sql, new VolleyResultListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                org.greenrobot.eventbus.EventBus.getDefault().post(new ImsCardCouponStores("error"));
            }
            @Override
            public void onResponse(String response) {
                if (response.equals("]")) {
                    String sql = "select a.id,a.title,a.couponmoney,a.is_jf,a.jf_bfb,b.sl from  (\n" +
                            "select id,title,couponmoney,is_jf,jf_bfb from ims_card_coupon where weid='" + weid + "' and status='1' ) a inner join(\n" +
                            "select couponid,SUM(status)SL from ims_card_members_coupon where from_user='" + from_user + "' \n" +
                            "and weid=" + weid + " and status=1 and (CURRENT_DATE() between CAST(FROM_UNIXTIME(starttime,'%Y-%m-%d')as datetime)  \n" +
                            "and CAST(FROM_UNIXTIME(endtime,'%Y-%m-%d')as datetime))group by couponid )b on a.id=b.couponid|";
                    Http_tickets(sql);
                } else {
                    String sql = "select a.id,a.title,a.couponmoney,a.is_jf,a.jf_bfb,b.sl from  (\n" +
                            "select id,title,couponmoney,is_jf,jf_bfb from ims_card_coupon where weid=" + weid + " and status='1' and id in(\n" +
                            "select couponid from ims_card_coupon_stores where weid=" + weid + " and bmbh='" + bmbh + "') ) a inner join(\n" +
                            "select couponid,SUM(status)SL from ims_card_members_coupon where from_user='" + from_user + "' \n" +
                            "and weid=" + weid + " and status=1 and (CURRENT_DATE() between CAST(FROM_UNIXTIME(starttime,'%Y-%m-%d')as datetime)  \n" +
                            "and CAST(FROM_UNIXTIME(endtime,'%Y-%m-%d')as datetime))group by couponid )b on a.id=b.couponid|";
                    Http_tickets(sql);
                }
            }
        });
    }

    private void Http_tickets(String sql) {
        DownHTTP.postVolley6(Net.yunUrl, sql, new VolleyResultListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                org.greenrobot.eventbus.EventBus.getDefault().post(new ImsCardCouponStores("error"));
            }
            @Override
            public void onResponse(String response) {
                org.greenrobot.eventbus.EventBus.getDefault().post(new ImsCardCouponStores(response));
            }
        });
    }
}

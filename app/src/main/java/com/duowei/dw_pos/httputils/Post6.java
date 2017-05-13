package com.duowei.dw_pos.httputils;

import android.content.Context;

import com.android.volley.VolleyError;
import com.duowei.dw_pos.R;
import com.duowei.dw_pos.bean.Moneys;
import com.duowei.dw_pos.bean.Wmslbjb_jiezhang;
import com.duowei.dw_pos.event.ImsCardCouponStores;
import com.duowei.dw_pos.event.ImsCardMembers;
import com.duowei.dw_pos.event.YunSubmit;
import com.duowei.dw_pos.tools.Net;
import com.duowei.dw_pos.tools.SqlYun;
import com.duowei.dw_pos.tools.Users;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;

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
                EventBus.getDefault().post(new ImsCardMembers("error"));
            }
            @Override
            public void onResponse(String response) {
                EventBus.getDefault().post(new ImsCardMembers(response));
            }
        });
    }

    public void post_ims_card_coupon_stores(final int weid, final String bmbh, final String from_user){
        String sql = "select couponid from ims_card_coupon_stores where weid=" + weid + " and bmbh='" + bmbh + "'|";
        DownHTTP.postVolley6(Net.yunUrl, sql, new VolleyResultListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                EventBus.getDefault().post(new ImsCardCouponStores("error"));
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
                EventBus.getDefault().post(new ImsCardCouponStores("error"));
            }
            @Override
            public void onResponse(String response) {
                EventBus.getDefault().post(new ImsCardCouponStores(response));
            }
        });
    }
    /**现金结账*/
    public void Http_cashier(final Context context, final Wmslbjb_jiezhang mWmlsbjb, final String mPad, final float mYingshou, final float mYishou, final float mZhaoling){
        String sj = mWmlsbjb.getSj().replaceAll("-", "");
        String exec = "exec prc_AADBPRK_android_001 '" + sj + "',1|";
        DownHTTP.postVolley6(Net.url, exec, new VolleyResultListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
            @Override
            public void onResponse(String response) {
                try {
                    JSONArray jsonArray = new JSONArray(response);
                    JSONObject jsonObject = jsonArray.getJSONObject(0);
                    int prk = jsonObject.getInt("prk");
                    String insertXSJBXX = "insert into XSJBXX (XSDH,XH,DDYBH,ZS,JEZJ,ZKJE,ZRJE,YS,SS,ZKFS,DDSJ,JYSJ,BZ,JZFSBM,BMMC,WMBS,ZH,KHBH,QKJE,JCRS,BY7)" +
                            "VALUES('" + mWmlsbjb.getWMDBH() + "','" + Users.YHBH + "','" + Users.YHMC + "','无折扣','" + bigDecimal(Moneys.xfzr) + "','" + bigDecimal(Moneys.zkjr) + "'," + bigDecimal(mYingshou) + ",'" + mWmlsbjb.getYS() + "',0,'" + mWmlsbjb.getZKFS() + "'," +
                            "'" + mWmlsbjb.getJYSJ() + "',GETDATE(),'" + mPad + "','" + mWmlsbjb.getJcfs() + "','','" + prk + "','" + mWmlsbjb.getZH() + "'," + bigDecimal(mYishou) + "," + bigDecimal(mZhaoling) + ",'" + mWmlsbjb.getJCRS() + "','')|";
                    String insertXSMXXX = "insert into XSMXXX(XH,XSDH,XMBH,XMMC,TM,DW,YSJG,XSJG,SL,XSJEXJ,FTJE,SYYXM,SQRXM,SFXS,ZSSJ,TCXMBH,SSLBBM,BZ)" +
                            "select WMDBH+convert(varchar(10),xh),WMDBH,xmbh,xmmc,tm,dw,ysjg,dj,sl,ysjg*sl,dj*sl,syyxm,SQRXM,SFXS,ZSSJ,TCXMBH,by2,BY13 from wmlsb where wmdbh='" + mWmlsbjb.getWMDBH() + "'|";
                    String updateWMLSBJB = "update WMLSBJB set JSJ='" + mPad + "',SFYJZ='1',DJLSH='" + prk + "',YSJE='" + bigDecimal(Moneys.xfzr) + "',JSKSSJ=getdate() where WMDBH='" + mWmlsbjb.getWMDBH() + "'|";
                    String sql = insertXSJBXX + insertXSMXXX + updateWMLSBJB;

                    Post7.getInstance().Http_check(sql, context.getResources().getString(R.string.payStytle_cash));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }
    /**云付款*/
    public void Http_yun(final Context context,final Wmslbjb_jiezhang mWmlsbjb,final String yunLoacalSql){
        final String pad=context.getSharedPreferences("user", Context.MODE_PRIVATE).getString("pad", "");
        String sj = mWmlsbjb.getSj().replaceAll("-", "");
        String exec = "exec prc_AADBPRK_android_001 '" + sj + "',1|";
        DownHTTP.postVolley6(Net.url, exec, new VolleyResultListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
            @Override
            public void onResponse(String response) {
                try {
                    JSONArray jsonArray = new JSONArray(response);
                    JSONObject jsonObject = jsonArray.getJSONObject(0);
                    int prk = jsonObject.getInt("prk");
                    String insertXSJBXX = "insert into XSJBXX (XSDH,XH,DDYBH,ZS,JEZJ,ZKJE,ZRJE,YS,SS,ZKFS," +
                            "DDSJ,JYSJ,BZ,JZFSBM,WMBS,ZH,KHBH,QKJE,JCRS," +
                            "CZKYE,BY7,CXYH,JZFSMC,HYJF,ZL,HYBH,HYKDJ)" +
                            "VALUES('" + mWmlsbjb.getWMDBH() + "','" + Users.YHBH + "','" + Users.YHMC + "','无折扣'," + bigDecimal(Moneys.xfzr) + "," + bigDecimal(Moneys.zkjr) + ",0," + bigDecimal(Moneys.ysjr) + "," + bigDecimal(Moneys.ysjr) + ",'" + mWmlsbjb.getZKFS() + "'," +
                            "'" + mWmlsbjb.getJYSJ() + "',GETDATE(),'" + pad + "','" + mWmlsbjb.getJcfs() + "','" + prk + "','" + mWmlsbjb.getZH() + "',0,0," + Integer.parseInt(mWmlsbjb.getJCRS()) + "," +
                            "" + SqlYun.CZKYE + ",'','','云会员消费'," + (SqlYun.jfbfb_add - SqlYun.jfbfb_sub) + "," + SqlYun.jfbfb_add + ",'" + SqlYun.HYBH + "','" + SqlYun.HYKDJ + "')|";
                    String insertXSMXXX = "insert into XSMXXX(XH,XSDH,XMBH,XMMC,TM,DW,YSJG,XSJG,SL,XSJEXJ,FTJE,SYYXM,SQRXM,SFXS,ZSSJ,TCXMBH,SSLBBM,BZ)" +
                            "select WMDBH+convert(varchar(10),xh),WMDBH,xmbh,xmmc,tm,dw,ysjg,dj,sl,ysjg*sl,dj*sl,syyxm,SQRXM,SFXS,ZSSJ,TCXMBH,by2,BY13 from wmlsb where wmdbh='" + mWmlsbjb.getWMDBH() + "'|";
                    String updateWMLSBJB = "update WMLSBJB set JSJ='" + pad + "',SFYJZ='1',DJLSH='" + prk + "',YSJE=" + bigDecimal(Moneys.xfzr) + ",JSKSSJ=getdate(),BY8='" + SqlYun.from_user + "',JZBZ='" + SqlYun.JZBZ + "' where WMDBH='" + mWmlsbjb.getWMDBH() + "'|";
                    String sql = yunLoacalSql + insertXSJBXX + insertXSMXXX + updateWMLSBJB;

                    Post7.getInstance().Http_check(sql,context.getString(R.string.payStytle_yun));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**扫码支付*/
    public void Http_scan(final Wmslbjb_jiezhang mItem, final String mBm, final String mPad, final String mID, final String ZFBID,final String payStytle){
        String sj = mItem.getSj().replaceAll("-", "");
        String exec = "exec prc_AADBPRK_android_001 '" + sj + "',1|";
        DownHTTP.postVolley6(Net.url, exec, new VolleyResultListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
            }
            @Override
            public void onResponse(String s) {
                try {
                    JSONArray jsonArray = new JSONArray(s);
                    JSONObject jsonObject = jsonArray.getJSONObject(0);
                    int prk = jsonObject.getInt("prk");
                    String insertXSFKFS = "insert into XSFKFS(XSDH,BM,NR,FKJE,DYQZS) values ('" + mItem.getWMDBH() + "','" + mBm + "','" + payStytle + "'," + mItem.getYS() + ",0)|";
                    String insertXSJBXX = "insert into XSJBXX (XSDH,XH,DDYBH,ZS,JEZJ,ZKJE,ZRJE,YS,SS,ZKFS,DDSJ,JYSJ,BZ,JZFSBM,BMMC,WMBS,ZH,KHBH,QKJE,JCRS,CZKYE,BY7,CXYH)" +
                            "VALUES('" + mItem.getWMDBH() + "','" + Users.YHBH + "','" + Users.YHMC + "','无折扣','" + bigDecimal(Moneys.xfzr) + "','" + bigDecimal(Moneys.zkjr) + "',0,'" + mItem.getYS() + "',0,'" + mItem.getZKFS() + "'," +
                            "'" + mItem.getJYSJ() + "',GETDATE(),'"+mPad+"','" + mItem.getJcfs() + "','','" + prk + "','" + mItem.getZH() + "',0,0,'" + mItem.getJCRS() + "',0,'','" + mID + "')|";
                    String insertXSMXXX = "insert into XSMXXX(XH,XSDH,XMBH,XMMC,TM,DW,YSJG,XSJG,SL,XSJEXJ,FTJE,SYYXM,SQRXM,SFXS,ZSSJ,TCXMBH,SSLBBM,BZ)" +
                            "select WMDBH+convert(varchar(10),xh),WMDBH,xmbh,xmmc,tm,dw,ysjg,dj,sl,ysjg*sl,dj*sl,syyxm,SQRXM,SFXS,ZSSJ,TCXMBH,by2,BY13 from wmlsb where wmdbh='" + mItem.getWMDBH() + "'|";
                    String updateWMLSBJB = "update WMLSBJB set JSJ='"+mPad+"',SFYJZ='1',DJLSH='" + prk + "',BY13='" + mID + "',BY16='" + ZFBID + "',YSJE='" + bigDecimal(Moneys.xfzr) + "',JSKSSJ=getdate() where WMDBH='" + mItem.getWMDBH() + "'|";
                    String sql = insertXSFKFS + insertXSJBXX + insertXSMXXX + updateWMLSBJB;

                    Post7.getInstance().Http_scan(sql,payStytle);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }
    /**云会员、现金支付*/
    public void Http_yun_cash(final Context context, final Wmslbjb_jiezhang mWmlsbjb, final String yunLoacalSql, final float mShouXian, final float mZhaoling){
        final String pad=context.getSharedPreferences("user", Context.MODE_PRIVATE).getString("pad", "");
        String sj = mWmlsbjb.getSj().replaceAll("-", "");
        String exec = "exec prc_AADBPRK_android_001 '" + sj + "',1|";
        DownHTTP.postVolley6(Net.url, exec, new VolleyResultListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
            @Override
            public void onResponse(String response) {
                try {
                    JSONArray jsonArray = new JSONArray(response);
                    JSONObject jsonObject = jsonArray.getJSONObject(0);
                    int prk = jsonObject.getInt("prk");
                    String insertXSJBXX = "insert into XSJBXX (XSDH,XH,DDYBH,ZS,JEZJ,ZKJE,ZRJE,YS,SS,ZKFS," +
                            "DDSJ,JYSJ,BZ,JZFSBM,WMBS,ZH,KHBH,QKJE,JCRS," +
                            "CZKYE,BY7,CXYH,JZFSMC,HYJF,ZL,HYBH,HYKDJ)" +
                            "VALUES('" + mWmlsbjb.getWMDBH() + "','" + Users.YHBH + "','" + Users.YHMC + "','无折扣'," + bigDecimal(Moneys.xfzr) + "," + bigDecimal(Moneys.zkjr) + ",0," + bigDecimal(Moneys.ysjr) + "," + bigDecimal(Moneys.ysjr) + ",'" + mWmlsbjb.getZKFS() + "'," +
                            "'" + mWmlsbjb.getJYSJ() + "',GETDATE(),'" + pad + "','" + mWmlsbjb.getJcfs() + "','" + prk + "','" + mWmlsbjb.getZH() + "'," + bigDecimal(mShouXian) + "," + bigDecimal(mZhaoling) + "," + Integer.parseInt(mWmlsbjb.getJCRS()) + "," +
                            "" + SqlYun.CZKYE + ",'','','云会员消费'," + (SqlYun.jfbfb_add - SqlYun.jfbfb_sub) + "," + SqlYun.jfbfb_add + ",'" + SqlYun.HYBH + "','" + SqlYun.HYKDJ + "')|";
                    String insertXSMXXX = "insert into XSMXXX(XH,XSDH,XMBH,XMMC,TM,DW,YSJG,XSJG,SL,XSJEXJ,FTJE,SYYXM,SQRXM,SFXS,ZSSJ,TCXMBH,SSLBBM,BZ)" +
                            "select WMDBH+convert(varchar(10),xh),WMDBH,xmbh,xmmc,tm,dw,ysjg,dj,sl,ysjg*sl,dj*sl,syyxm,SQRXM,SFXS,ZSSJ,TCXMBH,by2,BY13 from wmlsb where wmdbh='" + mWmlsbjb.getWMDBH() + "'|";
                    String updateWMLSBJB = "update WMLSBJB set JSJ='" + pad + "',SFYJZ='1',DJLSH='" + prk + "',YSJE=" + bigDecimal(Moneys.xfzr) + ",JSKSSJ=getdate(),BY8='" + SqlYun.from_user + "',JZBZ='" + SqlYun.JZBZ + "' where WMDBH='" + mWmlsbjb.getWMDBH() + "'|";
                    String sql = yunLoacalSql + insertXSJBXX + insertXSMXXX + updateWMLSBJB;

                    Post7.getInstance().Http_check(sql,context.getString(R.string.payStytle_cash_yun));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }
    /**云会员—扫码支付*/
    public void Http_yun_scan(final Wmslbjb_jiezhang mItem, final String yunLoacalSql,final String mBm,final String mPad, final String mID, final String ZFBID,final String payStytle){
        String sj = mItem.getSj().replaceAll("-", "");
        String exec = "exec prc_AADBPRK_android_001 '" + sj + "',1|";
        DownHTTP.postVolley6(Net.url, exec, new VolleyResultListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
            }
            @Override
            public void onResponse(String s) {
                try {
                    JSONArray jsonArray = new JSONArray(s);
                    JSONObject jsonObject = jsonArray.getJSONObject(0);
                    int prk = jsonObject.getInt("prk");
                    String insertXSFKFS = "insert into XSFKFS(XSDH,BM,NR,FKJE,DYQZS) values ('" + mItem.getWMDBH() + "','" + mBm + "','" + payStytle + "'," + Moneys.wfjr + ",0)|";
                    String insertXSJBXX = "insert into XSJBXX (XSDH,XH,DDYBH,ZS,JEZJ,ZKJE,ZRJE,YS,SS,ZKFS,DDSJ,JYSJ,BZ,JZFSBM,BMMC,WMBS,ZH,KHBH,QKJE,JCRS,CZKYE,BY7,CXYH," +
                            "JZFSMC,HYJF,ZL,HYBH,HYKDJ)" +
                            "VALUES('" + mItem.getWMDBH() + "','" + Users.YHBH + "','" + Users.YHMC + "','无折扣','" + bigDecimal(Moneys.xfzr) + "','" + bigDecimal(Moneys.zkjr) + "',0,'" + mItem.getYS() + "','" + mItem.getYS() + "','" + mItem.getZKFS() + "'," +
                            "'" + mItem.getJYSJ() + "',GETDATE(),'"+mPad+"','" + mItem.getJcfs() + "','','" + prk + "','" + mItem.getZH() + "',0,0,'" + mItem.getJCRS() + "',0,'','" + mID + "'," +
                            "'云会员消费'," + (SqlYun.jfbfb_add - SqlYun.jfbfb_sub) + "," + SqlYun.jfbfb_add + ",'" + SqlYun.HYBH + "','" + SqlYun.HYKDJ + "')|";
                    String insertXSMXXX = "insert into XSMXXX(XH,XSDH,XMBH,XMMC,TM,DW,YSJG,XSJG,SL,XSJEXJ,FTJE,SYYXM,SQRXM,SFXS,ZSSJ,TCXMBH,SSLBBM,BZ)" +
                            "select WMDBH+convert(varchar(10),xh),WMDBH,xmbh,xmmc,tm,dw,ysjg,dj,sl,ysjg*sl,dj*sl,syyxm,SQRXM,SFXS,ZSSJ,TCXMBH,by2,BY13 from wmlsb where wmdbh='" + mItem.getWMDBH() + "'|";
                    String updateWMLSBJB = "update WMLSBJB set JSJ='"+mPad+"',SFYJZ='1',DJLSH='" + prk + "',BY13='" + mID + "',BY16='" + ZFBID + "',YSJE='" + bigDecimal(Moneys.xfzr) + "',JSKSSJ=getdate() where WMDBH='" + mItem.getWMDBH() + "'|";
                    String sql = yunLoacalSql+insertXSFKFS + insertXSJBXX + insertXSMXXX + updateWMLSBJB;

                    Post7.getInstance().Http_scan(sql,payStytle);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }


    public Float bigDecimal(Float f) {
        return BigDecimal.valueOf(f).setScale(1, BigDecimal.ROUND_HALF_UP).floatValue();
    }
}

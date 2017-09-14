package com.duowei.dw_pos.httputils;

import com.android.volley.VolleyError;
import com.duowei.dw_pos.bean.JYXMSZ;
import com.duowei.dw_pos.bean.TBSJ;
import com.duowei.dw_pos.event.CheckJYCXMSZ;
import com.duowei.dw_pos.tools.Net;
import com.google.gson.Gson;

import org.greenrobot.eventbus.EventBus;
import org.litepal.crud.DataSupport;

import java.util.List;

/**
 * Created by Administrator on 2017-05-05.
 */

public class CheckVersion {
    CheckVersion (){}
    private static CheckVersion cv=null;
    public static CheckVersion instance(){
        if(cv==null){
            cv=new CheckVersion();
        }
        return cv;
    }
    public void checkJYXMSZ(){
        List<TBSJ> tbrp = DataSupport.select("tbrq").where("tablename=?","JYXMSZ").find(TBSJ.class);
        if(tbrp.size()<=0){
            return;
        }
        String sql="select tablename,CONVERT(varchar(100), tbrq, 20)as tbrq from tbsj where tbrq>'"+tbrp.get(0).getTbrq()+"'and tablename='jyxmsz'|";
        DownHTTP.postVolley6(Net.url, sql, new VolleyResultListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
            @Override
            public void onResponse(String response) {
                if(!response.equals("]")){
                    DataSupport.deleteAll(TBSJ.class);
                    Gson gson = new Gson();
                    TBSJ[] tbsjs = gson.fromJson(response, TBSJ[].class);
                    for(TBSJ t : tbsjs){
                        t.save();
                    }
                    String sql="select XMBH,XMMC,isnull(PY,'')PY,isnull(TM,'')TM,isnull(DW,'')DW,LBBM,LBMC,XSJG,isnull(SFTC,'0')SFTC,isnull(GQ,'0')GQ," +
                            "isnull(SFQX,'')SFQX,XL,isnull(HLBMMC,'')HLBMMC,isnull(SFYHQ,'')SFYHQ,isnull(BY16,'')BY16,isnull(BY6,0)BY6,isnull(YHJ,0)YHJ," +
                            "isnull(HYJ,0)HYJ,isnull(HYJ2,0)HYJ2,isnull(HYJ3,0)HYJ3,isnull(HYJ4,0)HYJ4,isnull(HYJ5,0)HYJ5,isnull(HYJ6,0)HYJ6," +
                            "isnull(HYJ7,0)HYJ7,isnull(HYJ8,0)HYJ8,isnull(HYJ9,0)HYJ9,isnull(BY3,'') BY3 from JYXMSZ where isnull(SFQX,'0')<>'1' order by XL asc|";
                    DownHTTP.postVolley6(Net.url, sql, new VolleyResultListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                        }
                        @Override
                        public void onResponse(final String response) {
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    DataSupport.deleteAll(JYXMSZ.class);
                                    Gson gson = new Gson();
                                    JYXMSZ[] jyxmsz = gson.fromJson(response, JYXMSZ[].class);
                                    for(JYXMSZ J:jyxmsz){
                                        J.save();
                                    }
                                    EventBus.getDefault().post(new CheckJYCXMSZ());
                                }
                            }).start();
                        }
                    });
                }
            }
        });
    }
}

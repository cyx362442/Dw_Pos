package com.duowei.dw_pos.tools;

import android.app.ProgressDialog;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.duowei.dw_pos.bean.CXDMXXX;
import com.duowei.dw_pos.bean.DMJYXMSSLB;
import com.duowei.dw_pos.bean.DMKWDYDP;
import com.duowei.dw_pos.bean.DMPZSD;
import com.duowei.dw_pos.bean.FXHYKSZ;
import com.duowei.dw_pos.bean.GKLX;
import com.duowei.dw_pos.bean.JYCSSZ;
import com.duowei.dw_pos.bean.JYXMSZ;
import com.duowei.dw_pos.bean.Jgsz;
import com.duowei.dw_pos.bean.MZSZJBXX;
import com.duowei.dw_pos.bean.MZSZMXXX;
import com.duowei.dw_pos.bean.PaySet;
import com.duowei.dw_pos.bean.SZLB;
import com.duowei.dw_pos.bean.TBSJ;
import com.duowei.dw_pos.bean.TCMC;
import com.duowei.dw_pos.bean.TCSD;
import com.duowei.dw_pos.bean.WXFWQDZ;
import com.duowei.dw_pos.bean.YHJBQK;
import com.duowei.dw_pos.httputils.DownHTTP;
import com.duowei.dw_pos.httputils.VolleyResultListener;
import com.google.gson.Gson;

import org.litepal.crud.DataSupport;

/**
 * Created by Administrator on 2017-03-22.
 */

public class DataLoad {
    private Context context;
    private  ProgressDialog mProgressDialog;
    private DataLoad() {
    }
    private static DataLoad singleton = null;
    public static DataLoad getInstance() {
        if (singleton == null) {
            singleton = new DataLoad();
        }
        return singleton;
    }

    public void showDialog(Context context){
        this.context=context;
        mProgressDialog = new ProgressDialog(context);
        mProgressDialog.setTitle("提示");
        mProgressDialog.setMessage("文件下载中……");
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mProgressDialog.setCancelable(true);// 设置是否可以通过点击Back键取消
        mProgressDialog.setCanceledOnTouchOutside(false);// 设置在点击Dialog外是否取消Dialog进度条
        mProgressDialog.show();
    }

    public void startLoad(Context context){
        showDialog(context);
        Http_YHJBQK();
    }
    private void Http_YHJBQK() {
        mProgressDialog.setMessage("用户信息……");
        String sql="SELECT YHBH,YHMC,isnull(YHMM,'')YHMM,isnull(XTJSQX,'')XTJSQX,isnull(ZPQX,'0')ZPQX,isnull(MDBBQX,'0')MDBBQX, TDQX FROM YHJBQK where XTJSQX<>'1'|";
        DownHTTP.postVolley6(Net.url, sql, new VolleyResultListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(context,"网络连接失败",Toast.LENGTH_SHORT).show();
                mProgressDialog.dismiss();
                return;
            }
            @Override
            public void onResponse(final String response) {
                if(response.equals("]")){
                    Http_JYCSSZ();
                }else{
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            Gson gson = new Gson();
                            DataSupport.deleteAll(YHJBQK.class);
                            YHJBQK[] yhjbqks = gson.fromJson(response, YHJBQK[].class);
                            for(YHJBQK Y:yhjbqks){
                                Y.save();
                            }
                        }
                    }).start();
                    //经营场所设置
                    Http_JYCSSZ();
                }
            }
        });
    }

    private void Http_JYCSSZ() {
        mProgressDialog.setMessage("餐桌数据……");
        String sql="SELECT CSBH,isnull(FCSBH,'')FCSBH,CSMC FROM JYCSSZ |";
        DownHTTP.postVolley6(Net.url, sql, new VolleyResultListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
            @Override
            public void onResponse(final String response) {
                if(response.equals("]")){
                    DataSupport.deleteAll(JYCSSZ.class);
                    Http_DMJYXMSSLB();
                }else{
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            Gson gson = new Gson();
                            DataSupport.deleteAll(JYCSSZ.class);
                            JYCSSZ[] jycsszs = gson.fromJson(response, JYCSSZ[].class);
                            for(JYCSSZ J:jycsszs){
                                J.save();
                            }
                        }
                    }).start();
                    //单品类别
                    Http_DMJYXMSSLB();
                }
            }
        });
    }

    private void Http_DMJYXMSSLB() {
        mProgressDialog.setMessage("单品类别……");
        String sql="SELECT LBBM,LBMC,isnull(SFTY,'')SFTY,XL FROM DMJYXMSSLB where isnull(SFXS,'0')<>'1'|";
        DownHTTP.postVolley6(Net.url, sql, new VolleyResultListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
            @Override
            public void onResponse(final String response) {
                if(response.equals("]")){
                    Http_JYXMSZ();
                }else{
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            DataSupport.deleteAll(DMJYXMSSLB.class);
                            Gson gson = new Gson();
                            DMJYXMSSLB[] dmjyxmsslbs = gson.fromJson(response, DMJYXMSSLB[].class);
                            for(DMJYXMSSLB D:dmjyxmsslbs){
                                D.save();
                            }
                        }
                    }).start();
                    //单品信息
                    Http_JYXMSZ();
                }
            }
        });
    }
    //单品信息
    private void Http_JYXMSZ() {
        mProgressDialog.setMessage("单品信息……");
        String sql="select XMBH,XMMC,isnull(PY,'')PY,isnull(TM,'')TM,isnull(DW,'')DW,LBBM,LBMC,XSJG,isnull(SFTC,'0')SFTC,isnull(GQ,'0')GQ," +
                "isnull(SFQX,'')SFQX,XL,isnull(HLBMMC,'')HLBMMC,isnull(SFYHQ,'')SFYHQ,isnull(BY16,'')BY16,isnull(BY6,0)BY6,isnull(BY19,'0')BY19,isnull(YHJ,0)YHJ," +
                "isnull(HYJ,0)HYJ,isnull(HYJ2,0)HYJ2,isnull(HYJ3,0)HYJ3,isnull(HYJ4,0)HYJ4,isnull(HYJ5,0)HYJ5,isnull(HYJ6,0)HYJ6," +
                "isnull(HYJ7,0)HYJ7,isnull(HYJ8,0)HYJ8,isnull(HYJ9,0)HYJ9,isnull(BY3,'') BY3,isnull(SFMYPC,'0')SFMYPC from JYXMSZ where isnull(SFQX,'0')<>'1' order by XL asc|";
        DownHTTP.postVolley6(Net.url, sql, new VolleyResultListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
            @Override
            public void onResponse(final String response) {
                if(response.equals("]")){
                    Http_TCMC();
                }else{
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            DataSupport.deleteAll(JYXMSZ.class);
                            Gson gson = new Gson();
                            JYXMSZ[] jyxmsz = gson.fromJson(response, JYXMSZ[].class);
                            for(JYXMSZ J:jyxmsz){
                                J.save();
                            }
                        }
                    }).start();
                    //单品信息
                    Http_TCMC();
                }
            }
        });
    }
    //套餐名称
    private void Http_TCMC() {
        mProgressDialog.setMessage("套餐名称……");
        String z = DateTimes.getWeek();
        String sql= "SELECT * FROM (select XMBH,XMMC,LBMC,XL,TM,PY,case isnull(by3,'') when '' then '2011-01-01' else by3 end by3, case isnull(by4,'') " +
                "when '' then '2222-01-01' else by4 end by4, (case when isnull(kssj,'')='' then '00:00:00' else CONVERT(VARCHAR(10),kssj,108) end)KSSJ, " +
                "(case when isnull(jssj,'')='' then '23:59:59' else CONVERT(VARCHAR(10),jssj,108) end)JSSJ from tcmc where  ISNULL(" + z + ",'0')= '1' ) A " +
                "WHERE CONVERT(VARCHAR(10),GETDATE(),120) BETWEEN A.BY3 AND A.BY4  AND CONVERT(VARCHAR(10),GETDATE(),108) BETWEEN CONVERT(VARCHAR(10),KSSJ,108) " +
                "AND CONVERT(VARCHAR(10),JSSJ,108)  ORDER BY XL |";
        DownHTTP.postVolley6(Net.url, sql, new VolleyResultListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
            @Override
            public void onResponse(final String response) {
                if(response.equals("]")){
                    DataSupport.deleteAll(TCMC.class);
                    Http_TCSD();
                }else{
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            DataSupport.deleteAll(TCMC.class);
                            Gson gson = new Gson();
                            TCMC[] tcmc = gson.fromJson(response, TCMC[].class);
                            for(TCMC T:tcmc){
                                T.save();
                            }
                        }
                    }).start();
                    //单品信息
                    Http_TCSD();
                }
            }
        });
    }
    //套餐明细
    private void Http_TCSD() {
        mProgressDialog.setMessage("套餐明细……");
        String sql= "SELECT XH,XMBH,XMBH1,XMMC1,isnull(DW1,'')DW1,TM,SL,isnull(DJ,0)DJ,isnull(SFXZ,'0')SFXZ,isnull(GQ,'0')GQ,LBBM FROM TCSD |";
        DownHTTP.postVolley6(Net.url, sql, new VolleyResultListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
            @Override
            public void onResponse(final String response) {
                if(response.equals("]")){
                    DataSupport.deleteAll(TCSD.class);
                    Http_GKLX();
                }else{
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            DataSupport.deleteAll(TCSD.class);
                            Gson gson = new Gson();
                            TCSD[] tcsd = gson.fromJson(response, TCSD[].class);
                            for(TCSD T:tcsd){
                                T.save();
                            }
                        }
                    }).start();
                    //顾客信息
                   Http_GKLX();
                }
            }
        });
    }
    //顾客类型
    private void Http_GKLX() {
        mProgressDialog.setMessage("顾客类型……");
        String sql="select * from GKLX |";
        DownHTTP.postVolley6(Net.url, sql, new VolleyResultListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
            @Override
            public void onResponse(final String response) {
                if(response.equals("]")){
                    Http_SZLB();
                }else{
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            DataSupport.deleteAll(GKLX.class);
                            Gson gson = new Gson();
                            GKLX[] gklx = gson.fromJson(response, GKLX[].class);
                            for(GKLX G:gklx){
                                G.save();
                            }
                        }
                    }).start();
                    //退品原因
                    Http_SZLB();
                }
            }
        });
    }
    //退品原因
    private void Http_SZLB() {
        mProgressDialog.setMessage("退品原因……");
        String sql="select SZBM,SZMC from SZLB|";
        DownHTTP.postVolley6(Net.url, sql, new VolleyResultListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
            @Override
            public void onResponse(final String response) {
                if(response.equals("]")){
                    Http_DMPZSD();
                }else{
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            DataSupport.deleteAll(SZLB.class);
                            Gson gson = new Gson();
                            SZLB[] szlb = gson.fromJson(response, SZLB[].class);
                            for(SZLB S:szlb){
                                S.save();
                            }
                        }
                    }).start();
                    //口味信息
                    Http_DMPZSD();
                }
            }
        });
    }
    //口味信息
    private void Http_DMPZSD() {
        mProgressDialog.setMessage("口味信息……");
        String sql="SELECT PZBM,NR,PXH,isnull(ZDBZ,'0')ZDBZ, DYCP FROM DMPZSD ORDER BY PXH ASC |";
        DownHTTP.postVolley6(Net.url, sql, new VolleyResultListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
            @Override
            public void onResponse(final String response) {
                if(response.equals("]")){
                    Http_DMKWDYDP();
                }else{
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            DataSupport.deleteAll(DMPZSD.class);
                            Gson gson = new Gson();
                            DMPZSD[] dmpzsd = gson.fromJson(response, DMPZSD[].class);
                            for(DMPZSD D:dmpzsd){
                                D.save();
                            }
                        }
                    }).start();
                    //口味对应单品设置
                    Http_DMKWDYDP();
                }
            }
        });
    }

    private void Http_DMKWDYDP() {
        mProgressDialog.setMessage("口味对应单品设置……");
        String sql="SELECT PZBM,XMBH FROM DMKWDYDP |";
        DownHTTP.postVolley6(Net.url, sql, new VolleyResultListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
            @Override
            public void onResponse(final String response) {
                if(response.equals("]")){
                    Http_WXFWQDZ();
                }else{
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            DataSupport.deleteAll(DMKWDYDP.class);
                            Gson gson = new Gson();
                            DMKWDYDP[] dmkwdydp = gson.fromJson(response, DMKWDYDP[].class);
                            for(DMKWDYDP D:dmkwdydp){
                                D.save();
                            }
                        }
                    }).start();
                    Http_WXFWQDZ();
                }
            }
        });
    }

    private void Http_WXFWQDZ() {
        mProgressDialog.setMessage("微信服务器配置……");
        String sql="select SIP,isnull(WXGZH,'')WXGZH,isnull(YSID,'')YSID,isnull(BMBH,'')BMBH,storeid,weid from WXFWQDZ|";
        DownHTTP.postVolley6(Net.url, sql, new VolleyResultListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
            @Override
            public void onResponse(final String response) {
                if(response.equals("]")){
                    Http_FXHYKSZ();
                    DataSupport.deleteAll(WXFWQDZ.class);
                }else{
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            DataSupport.deleteAll(WXFWQDZ.class);
                            Gson gson = new Gson();
                            WXFWQDZ[] wxfwqdz = gson.fromJson(response, WXFWQDZ[].class);
                            for(WXFWQDZ W:wxfwqdz){
                                W.save();
                            }
                        }
                    }).start();
                    Http_FXHYKSZ();
                }
            }
        });
    }

    private void Http_FXHYKSZ() {
        mProgressDialog.setMessage("会员价格信息……");
        String sql="select * from  FXHYKSZ|";
        DownHTTP.postVolley6(Net.url, sql, new VolleyResultListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
            @Override
            public void onResponse(final String response) {
                if(response.equals("]")){
                    Http_PaySet();
                }else{
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            DataSupport.deleteAll(FXHYKSZ.class);
                            Gson gson = new Gson();
                            FXHYKSZ[] fxhykszs = gson.fromJson(response, FXHYKSZ[].class);
                            for(FXHYKSZ F:fxhykszs){
                                F.save();
                            }
                        }
                    }).start();
                    Http_PaySet();
                }
            }
        });
    }
    //微信、支付宝支付信息
    private void Http_PaySet() {
        mProgressDialog.setMessage("扫码支付设置……");
        String sql="SELECT isnull(PID,'')PID,isnull(BY1,'')BY1,isnull(BY2,'')BY2,isnull(BY3,'')BY3,isnull(FWQDZ,'')FWQDZ,isnull(BY6,'')BY6,isnull(BY7,'')BY7 FROM payset|";
        DownHTTP.postVolley6(Net.url, sql, new VolleyResultListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
            @Override
            public void onResponse(final String response) {
                if(response.equals("]")){
                    Http_CXDMXXX();
                    DataSupport.deleteAll(PaySet.class);
                }else{
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            DataSupport.deleteAll(PaySet.class);
                            Gson gson = new Gson();
                            PaySet[] payset = gson.fromJson(response, PaySet[].class);
                            for(PaySet P:payset){
                                P.save();
                            }
                        }
                    }).start();
                    Http_CXDMXXX();
                }
            }
        });
    }

    private void Http_CXDMXXX() {
        mProgressDialog.setMessage("促销单明细信息……");
        String sql="select * from cxdmxxx|";
        DownHTTP.postVolley6(Net.url, sql, new VolleyResultListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
            @Override
            public void onResponse(final String response) {
                if(response.equals("]")){
                    DataSupport.deleteAll(CXDMXXX.class);
                    Http_MZSZJBXX();
                }else{
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            DataSupport.deleteAll(CXDMXXX.class);
                            Gson gson = new Gson();
                            CXDMXXX[] cxdmxxxes = gson.fromJson(response, CXDMXXX[].class);
                            for(CXDMXXX c: cxdmxxxes){
                                c.save();
                            }
                        }
                    }).start();
                    Http_MZSZJBXX();
                }
            }
        });
    }

    private void Http_MZSZJBXX() {
        mProgressDialog.setMessage("买赠/加价促销设置基本信息……");
        String sql="select * from mzszjbxx|";
        DownHTTP.postVolley6(Net.url, sql, new VolleyResultListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
            @Override
            public void onResponse(final String response) {
                if(response.equals("]")){
                    DataSupport.deleteAll(MZSZJBXX.class);
                    Http_MZSZMXXX();
                }else{
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            DataSupport.deleteAll(MZSZJBXX.class);
                            Gson gson = new Gson();
                            MZSZJBXX[] mzszjbxxes = gson.fromJson(response, MZSZJBXX[].class);
                            for(MZSZJBXX m : mzszjbxxes){
                                m.save();
                            }
                        }
                    }).start();
                    Http_MZSZMXXX();
                }
            }
        });
    }

    private void Http_MZSZMXXX() {
        mProgressDialog.setMessage("买赠/加价促销设置明细信息……");
        String sql="select * from mzszmxxx|";
        DownHTTP.postVolley6(Net.url, sql, new VolleyResultListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
            @Override
            public void onResponse(final String response) {
                if(response.equals("]")){
                    DataSupport.deleteAll(MZSZMXXX.class);
                    Http_JGSZ();
                }else{
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            DataSupport.deleteAll(MZSZMXXX.class);
                            Gson gson = new Gson();
                            MZSZMXXX[] mzszjbxxes = gson.fromJson(response, MZSZMXXX[].class);
                            for(MZSZMXXX m : mzszjbxxes){
                                m.save();
                            }
                        }
                    }).start();
                    Http_JGSZ();
                }
            }
        });
    }

    private void Http_JGSZ() {
        mProgressDialog.setMessage("机构设置……");
        String sql="select by52 from jgsz|";
        DownHTTP.postVolley6(Net.url, sql, new VolleyResultListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
            @Override
            public void onResponse(final String response) {
                if(response.equals("]")){
                    DataSupport.deleteAll(Jgsz.class);
                    Http_TBSJ();
                }else{
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            DataSupport.deleteAll(Jgsz.class);
                            Gson gson = new Gson();
                            Jgsz[] jgszs = gson.fromJson(response, Jgsz[].class);
                            for(Jgsz j : jgszs){
                                j.save();
                            }
                        }
                    }).start();
                    Http_TBSJ();
                }
            }
        });
    }

    private void Http_TBSJ() {
        mProgressDialog.setMessage("数据库更新时间表……");
        String sql="select tablename,CONVERT(varchar(100), tbrq, 20)as tbrq  from TBSJ|";
        DownHTTP.postVolley6(Net.url, sql, new VolleyResultListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
            @Override
            public void onResponse(final String response) {
                if(response.equals("]")){
                    mProgressDialog.dismiss();
                }else{
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            DataSupport.deleteAll(TBSJ.class);
                            Gson gson = new Gson();
                            TBSJ[] tbsjs = gson.fromJson(response, TBSJ[].class);
                            for(TBSJ t : tbsjs){
                                t.save();
                            }
                        }
                    }).start();
                    mProgressDialog.dismiss();
                }
            }
        });
    }
}

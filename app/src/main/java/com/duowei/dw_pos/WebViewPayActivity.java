package com.duowei.dw_pos;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.duowei.dw_pos.bean.JYCSSZ;
import com.duowei.dw_pos.bean.Moneys;
import com.duowei.dw_pos.bean.PaySet;
import com.duowei.dw_pos.bean.WMLSBJB;
import com.duowei.dw_pos.httputils.DownHTTP;
import com.duowei.dw_pos.httputils.VolleyResultListener;
import com.duowei.dw_pos.tools.Base64;
import com.duowei.dw_pos.tools.DateTimes;
import com.duowei.dw_pos.tools.Net;
import com.duowei.dw_pos.tools.Users;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.litepal.crud.DataSupport;

import java.io.Serializable;
import java.util.List;
import java.util.Random;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class WebViewPayActivity extends AppCompatActivity {

    @BindView(R.id.img_return)
    ImageView mImgReturn;
    @BindView(R.id.tv_payStytle)
    TextView mTvPayStytle;
    @BindView(R.id.rlTop)
    RelativeLayout mRlTop;
    @BindView(R.id.webview)
    WebView mWebview;
    @BindView(R.id.img_load)
    ImageView mImgLoad;
    @BindView(R.id.tv_return)
    TextView mTvReturn;
    @BindView(R.id.ll_return)
    LinearLayout mLlReturn;

    private String mPid;//支付宝APPID
    private String mBy1;//分店编号
    private String mBy2;//分店名称
    private String mBy3;//微信ID
    private String mFwqdz;//服务器地址
    private String mBy6;
    private String mBy7;//微信支付调用
    private String mBm;
    private String ysturl;
    private String chaUrl;
    private int number;
    private String nr;
    private String ZFBID;
    private String mPayStytle;
    private String mID;
    private WMLSBJB mItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view_pay);
        ButterKnife.bind(this);
        PaySet payset = DataSupport.findFirst(PaySet.class);
        mPid =  payset.PID;
        mBy1 =  payset.BY1;
        mBy2 =  payset.BY2;
        mBy3 =  payset.BY3;
        mFwqdz = payset.FWQDZ;
        mBy6 =  payset.BY6;
        mBy7 =  payset.BY7;
        mPayStytle = getIntent().getStringExtra("from");
        mItem = (WMLSBJB) getIntent().getSerializableExtra("WMLSBJB");
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(mPayStytle.equals("支付宝")){
            if(TextUtils.isEmpty("mPid")){
                Toast.makeText(this,"您还未设置支付宝支付功能！",Toast.LENGTH_SHORT);
            }else{
                //生成10W以内随机数
                number = getNumber();
                mID= DateTimes.getTime() + number;
                chaUrl="http://pay.wxdw.top/aipay/f2fpay/query.php?out_trade_no="+mID+"&appid="+mPid;
                ysturl = "http://%s/aipay/f2fpay/qrpay.php?appid=%s&out_trade_no=%s&subject=%s&store_id=%s&total_amount=%s";
                ysturl = String.format(ysturl,mFwqdz,mPid,mID,mBy1+mBy2,mBy1, Moneys.wfjr);
                mWebview.setVisibility(View.GONE);
                startWebView("支付宝");
            }
        }
    }

    @OnClick(R.id.img_return)
    public void onViewClicked() {
        finish();
    }
    private int getNumber() {
        Random random=new Random();
        return random.nextInt(100000);
    }
    private void startWebView(final String payStytle) {
        WebSettings settings = mWebview.getSettings();
        //设置支持js
        settings.setJavaScriptEnabled(true);
        //防止跳出
        mWebview.setWebViewClient(new WebViewClient(){
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                startTimer(payStytle);
                mWebview.setVisibility(View.VISIBLE);
            }
        });
        mWebview.setWebChromeClient(new WebChromeClient()
        {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);
            }
        });
        mWebview.loadUrl(ysturl);
    }
    private void startTimer(String payStytle){
        mWebview.setVisibility(View.GONE);
        Thread thread = new Thread(new MyThread(payStytle));
        thread.start();
    }
    boolean flag=false;
    class MyThread implements Runnable{
        String payStytle;
        public MyThread(String payStytle) {
            this.payStytle = payStytle;
        }
        @Override
        public void run() {
            while (flag==false){
                getHtmlResult(payStytle);
            }
        }
    }
    private synchronized  void  getHtmlResult(final String payStytle) {
        try {
            Thread.sleep(1000);
            final String result = DownHTTP.getResult(chaUrl);
            Log.e("result==",result);
            if(result.contains("支付成功")||result.contains("SUCCESS")){
                flag=true;
                runOnUiThread(new Runnable() {// 这个方法是将子线程抛向主线程执行
                    @Override
                    public void run() {
                        mWebview.setVisibility(View.GONE);
                    }
                });
                if(payStytle.equals("支付宝")){
                    ZFBID=result.substring(result.indexOf("*")+1,result.length());
                }
                updateData(payStytle);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    private  void  updateData(final String payStytle) {
//        String sj = mItem.sj.replaceAll("-", "");
        String sj="20170324";
        String s = "WMLSBJB" + sj;
        String exec="exec prc_AADBPRK_android_001 '"+s+"',1|";
        DownHTTP.postVolley6(Net.url, exec,new VolleyResultListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
            }
            @Override
            public void onResponse(String s) {
                try {
                    JSONArray jsonArray = new JSONArray(s);
                    JSONObject jsonObject = jsonArray.getJSONObject(0);
                    int prk = jsonObject.getInt("prk");
                    Http_jiezhang(prk,payStytle);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }
    private void Http_jiezhang(int prk, final String payStytle) {
//        float zkje = totalMoney - discountMoney;
        String url="";
            String insertXSFKFS="insert into XSFKFS(XSDH,BM,NR,FKJE,DYQZS) values ('"+mItem.getWMDBH()+"','"+mBm+"','"+nr+"',"+mItem.getYS()+",0)|";
            String insertXSJBXX="insert into XSJBXX (XSDH,XH,DDYBH,ZS,JEZJ,ZKJE,ZRJE,YS,SS,ZKFS,DDSJ,JYSJ,BZ,JZFSBM,BMMC,WMBS,ZH,KHBH,QKJE,JCRS,CZKYE,BY7,CXYH)" +
                    "VALUES('"+mItem.getWMDBH()+"','"+mItem.getYHBH()+"','"+ Users.YHMC+"','无折扣','"+Moneys.xfzr+"','"+Moneys.zkjr+"',0,'"+mItem.getYS()+"',0,'无'," +
                    "'"+mItem.getJYSJ()+"',GETDATE(),'sunmin','"+mItem.getJcfs()+"','','"+prk+"','"+mItem.getZH()+"',0,0,'"+mItem.getJCRS()+"',0,'','"+mID+"')|";
            String insertXSMXXX="insert into XSMXXX(XH,XSDH,XMBH,XMMC,TM,DW,YSJG,XSJG,SL,XSJEXJ,FTJE,SYYXM,SQRXM,SFXS,ZSSJ,TCXMBH,SSLBBM,BZ)" +
                    "select WMDBH+convert(varchar(10),xh),WMDBH,xmbh,xmmc,tm,dw,ysjg,dj,sl,ysjg*sl,dj*sl,syyxm,SQRXM,SFXS,ZSSJ,TCXMBH,by2,BY13 from wmlsb where wmdbh='" + mItem.getWMDBH() + "'|";
            String updateWMLSBJB="update WMLSBJB set JSJ='sunmin',SFYJZ='1',DJLSH='"+prk+"',BY13='"+mID+"',BY16='"+ZFBID+"',YSJE='"+Moneys.xfzr+"',JSKSSJ=getdate() where WMDBH='"+mItem.getWMDBH()+"'|";
            url=insertXSFKFS+insertXSJBXX+insertXSMXXX+updateWMLSBJB;
        Http_local(payStytle, url);
    }
    private void Http_local(final String payStytle, String sql) {
        DownHTTP.postVolley6(Net.url, sql, new VolleyResultListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
            }
            @Override
            public void onResponse(String s) {
                Log.e("jiezhang===",s);
                if(s.contains("richado")){
//                    if(mFrom.equals("yun")){//来自云会员付款
//                        Log.e("yun===",s);
//                        HttpYun();
//                    }else if(mFrom.equals("")){//单扫码支付
//                    }
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
//                                mDrawable2.start();
                                mLlReturn.setVisibility(View.VISIBLE);
                                mTvReturn.setText("恭喜你,"+payStytle+"收款成功!");
                            }
                        });
                }
            }
        });
    }
}

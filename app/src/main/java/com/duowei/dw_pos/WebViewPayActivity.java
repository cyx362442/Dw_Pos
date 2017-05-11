package com.duowei.dw_pos;

import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.duowei.dw_pos.bean.Moneys;
import com.duowei.dw_pos.bean.PaySet;
import com.duowei.dw_pos.bean.Wmslbjb_jiezhang;
import com.duowei.dw_pos.event.CheckSuccess;
import com.duowei.dw_pos.httputils.DownHTTP;
import com.duowei.dw_pos.httputils.VolleyResultListener;
import com.duowei.dw_pos.tools.CloseActivity;
import com.duowei.dw_pos.tools.DateTimes;
import com.duowei.dw_pos.tools.Net;
import com.duowei.dw_pos.sunmiprint.Prints;
import com.duowei.dw_pos.tools.Users;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.litepal.crud.DataSupport;

import java.math.BigDecimal;
import java.util.Random;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import woyou.aidlservice.jiuiv5.IWoyouService;

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
    @BindView(R.id.progressBar)
    ProgressBar mProgressBar;
    private AnimationDrawable mDrawable2;

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
    private Wmslbjb_jiezhang mItem;
    private String mPad;
    private Prints mPrinter;
    private IWoyouService woyouService;
    private ServiceConnection connService = new ServiceConnection() {
        @Override
        public void onServiceDisconnected(ComponentName name) {
            woyouService = null;
        }
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            woyouService = IWoyouService.Stub.asInterface(service);
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view_pay);
        ButterKnife.bind(this);
        CloseActivity.addAcitity(this);
        mDrawable2 = (AnimationDrawable) mImgLoad.getDrawable();
        SharedPreferences user = getSharedPreferences("user", Context.MODE_PRIVATE);
        mPad = user.getString("pad", "");

        PaySet payset = DataSupport.findFirst(PaySet.class);
        mPid = payset.PID;
        mBy1 = payset.BY1;
        mBy2 = payset.BY2;
        mBy3 = payset.BY3;
        mFwqdz = payset.FWQDZ;
        mBy6 = payset.BY6;
        mBy7 = payset.BY7;
        mPayStytle = getIntent().getStringExtra("from");
        mItem = (Wmslbjb_jiezhang) getIntent().getSerializableExtra("WMLSBJB");
        mPrinter = Prints.getPrinter();
        mPrinter.bindPrintService(this,connService);
    }

    @Override
    protected void onStart() {
        super.onStart();
        mTvPayStytle.setText(mPayStytle);
        if (mPayStytle.equals("支付宝")) {
            nr="支付宝支付";
            mBm="PPPPP";
            if (TextUtils.isEmpty("mPid")) {
                Toast.makeText(this, "您还未设置支付宝支付功能！", Toast.LENGTH_SHORT);
            } else {
                //生成10W以内随机数
                number = getNumber();
                mID = DateTimes.getTime() + number;
                chaUrl = "http://pay.wxdw.top/aipay/f2fpay/query.php?out_trade_no=" + mID + "&appid=" + mPid;
                ysturl = "http://%s/aipay/f2fpay/qrpay.php?appid=%s&out_trade_no=%s&subject=%s&store_id=%s&total_amount=%s";
                ysturl = String.format(ysturl, mFwqdz, mPid, mID, mBy1 + mBy2, mBy1, bigDecimal(Moneys.wfjr));
                startWebView("支付宝");
            }
        }else if(mPayStytle.equals("微信")){
            nr="微信支付";
            mBm="WWWWW";
            if(mBy3.equals("")||mBy3==null){
                Toast.makeText(this,"您还未设置支付宝支付功能！",Toast.LENGTH_SHORT).show();
                return;
            }
            //生成10W以内随机数
            number = getNumber();
            mID= DateTimes.getTime() + number;
            chaUrl="http://pay.wxdw.top/%s/%s/order_query.php?orderid=%s&weid=%s";
            //判断使用哪个接口
            if(mBy7.equals("原生态接口"))
            {
                //如果是原生态接口调用这个
                //支付过的wmdbh不会显示图片
                chaUrl=String .format(chaUrl,"yst","pay",mID,mBy3);
                ysturl = "http://%s/yst/pay/native_dynamic_qrcode.php?weid=%s&orderid=%s&money=%s&name=%s";
                ysturl = String.format(ysturl,mFwqdz,mBy3,mID,bigDecimal(Moneys.wfjr),mBy1+mBy2);
            }else
            {
                //如果是服务商接口调用这个
                chaUrl=String .format(chaUrl,"bs","wxzf",mID,mBy3);
                ysturl =  "http://%s/dl/sys/demo/native_dynamic_qrcode.php?weid=%s&orderid=%s&money=%s&name=%s";
                ysturl = String.format(ysturl,mFwqdz,mBy3,mID,bigDecimal(Moneys.wfjr),mBy1+mBy2);
            }
            startWebView("微信");
        }
    }

    @OnClick(R.id.img_return)
    public void onViewClicked() {
        PayExit();
    }

    private int getNumber() {
        Random random = new Random();
        return random.nextInt(100000);
    }

    private void startWebView(final String payStytle) {
        final WebSettings settings = mWebview.getSettings();
        //设置支持js
        settings.setJavaScriptEnabled(true);
        //加载url前，设置图片阻塞
        settings.setBlockNetworkImage(true);
        mWebview.loadUrl(ysturl);
        mWebview.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                //加载完毕后，关闭图片阻塞
                settings.setBlockNetworkImage(false);
                mProgressBar.setVisibility(View.GONE);
                startTimer(payStytle);
            }
        });
        mWebview.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);
            }
        });
    }

    private void startTimer(String payStytle) {
        Thread thread = new Thread(new MyThread(payStytle));
        thread.start();
    }

    boolean flag = false;
    class MyThread implements Runnable {
        String payStytle;
        public MyThread(String payStytle) {
            this.payStytle = payStytle;
        }

        @Override
        public void run() {
            getHtmlResult(payStytle);
        }
    }

    private synchronized void getHtmlResult(final String payStytle) {
        String result = DownHTTP.getResult(chaUrl);
        if(result.contains("支付成功") || result.contains("SUCCESS")){

            if (payStytle.equals("支付宝")) {
                    ZFBID = result.substring(result.indexOf("*") + 1, result.length());
                }
            updateData(payStytle);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mWebview.setVisibility(View.GONE);
                }
            });
        }else{
            if(flag==false){
                try {
                    Thread.sleep(500);
                    startTimer(payStytle);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void updateData(final String payStytle) {
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
                    Http_jiezhang(prk, payStytle);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void Http_jiezhang(int prk, final String payStytle) {
        String url = "";
        String insertXSFKFS = "insert into XSFKFS(XSDH,BM,NR,FKJE,DYQZS) values ('" + mItem.getWMDBH() + "','" + mBm + "','" + nr + "'," + mItem.getYS() + ",0)|";
        String insertXSJBXX = "insert into XSJBXX (XSDH,XH,DDYBH,ZS,JEZJ,ZKJE,ZRJE,YS,SS,ZKFS,DDSJ,JYSJ,BZ,JZFSBM,BMMC,WMBS,ZH,KHBH,QKJE,JCRS,CZKYE,BY7,CXYH)" +
                "VALUES('" + mItem.getWMDBH() + "','" + Users.YHBH + "','" + Users.YHMC + "','无折扣','" + bigDecimal(Moneys.xfzr) + "','" + bigDecimal(Moneys.zkjr) + "',0,'" + mItem.getYS() + "',0,'" + mItem.getZKFS() + "'," +
                "'" + mItem.getJYSJ() + "',GETDATE(),'"+mPad+"','" + mItem.getJcfs() + "','','" + prk + "','" + mItem.getZH() + "',0,0,'" + mItem.getJCRS() + "',0,'','" + mID + "')|";
        String insertXSMXXX = "insert into XSMXXX(XH,XSDH,XMBH,XMMC,TM,DW,YSJG,XSJG,SL,XSJEXJ,FTJE,SYYXM,SQRXM,SFXS,ZSSJ,TCXMBH,SSLBBM,BZ)" +
                "select WMDBH+convert(varchar(10),xh),WMDBH,xmbh,xmmc,tm,dw,ysjg,dj,sl,ysjg*sl,dj*sl,syyxm,SQRXM,SFXS,ZSSJ,TCXMBH,by2,BY13 from wmlsb where wmdbh='" + mItem.getWMDBH() + "'|";
        String updateWMLSBJB = "update WMLSBJB set JSJ='"+mPad+"',SFYJZ='1',DJLSH='" + prk + "',BY13='" + mID + "',BY16='" + ZFBID + "',YSJE='" + bigDecimal(Moneys.xfzr) + "',JSKSSJ=getdate() where WMDBH='" + mItem.getWMDBH() + "'|";
        url = insertXSFKFS + insertXSJBXX + insertXSMXXX + updateWMLSBJB;
        Http_local(payStytle, url);
    }

    private void Http_local(final String payStytle, String sql) {
        DownHTTP.postVolley7(Net.url, sql, new VolleyResultListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
            }
            @Override
            public void onResponse(String s) {
                if (s.contains("richado")) {
                    mWebview.setVisibility(View.GONE);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            flag = true;
                            mDrawable2.start();
                            mLlReturn.setVisibility(View.VISIBLE);
                            mTvReturn.setText("恭喜你," + payStytle + "收款成功!");

                            mPrinter.setWoyouService(woyouService);
                            mPrinter.print_jiezhang(mItem.getYS(),mItem.getYS(),"0.00",payStytle);
                            EventBus.getDefault().post(new CheckSuccess());
                        }
                    });
                }
            }
        });
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0){
            PayExit();
            return true;
        }
        return false;
    }

    private void PayExit() {
        if(flag==false){
            final AlertDialog.Builder builder = new AlertDialog.Builder(this)
                    .setTitle("提示")
                    .setMessage("付款未成功是否退出？")
                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            flag = true;
                            finish();
                        }
                    })
                    .setNegativeButton("取消", null);
            builder.create();
            builder.show();
        }else if(flag==true){
            CloseActivity.finishActivity();
        }
    }
    public  Float bigDecimal(Float f){
        return BigDecimal.valueOf(f).setScale(1, BigDecimal.ROUND_HALF_UP).floatValue();
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(connService);
    }
}

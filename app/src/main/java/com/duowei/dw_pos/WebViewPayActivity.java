package com.duowei.dw_pos;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.drawable.AnimationDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
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

import com.duowei.dw_pos.bean.Moneys;
import com.duowei.dw_pos.bean.PaySet;
import com.duowei.dw_pos.bean.Wmslbjb_jiezhang;
import com.duowei.dw_pos.event.CheckSuccess;
import com.duowei.dw_pos.httputils.DownHTTP;
import com.duowei.dw_pos.httputils.Post6;
import com.duowei.dw_pos.tools.CloseActivity;
import com.duowei.dw_pos.tools.DateTimes;
import com.duowei.dw_pos.tools.Net;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.litepal.crud.DataSupport;

import java.math.BigDecimal;
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
    private String ZFBID;
    private String mPayStytle;
    private String mID;
    private Wmslbjb_jiezhang mItem;
    private String mPad;

    private String mSqlYun;
    private String mSqlLocal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view_pay);
        ButterKnife.bind(this);
        EventBus.getDefault().register(this);
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
        mSqlYun=getIntent().getStringExtra("sqlYun");
        mSqlLocal = getIntent().getStringExtra("sqlLocal");

    }

    @Override
    protected void onStart() {
        super.onStart();
        mTvPayStytle.setText(mPayStytle+"  ￥"+bigDecimal(Moneys.wfjr));
        if (mPayStytle.equals(getString(R.string.payStytle_zhifubao))||mPayStytle.equals(getString(R.string.payStytle_zhifubao_yun))) {
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
                startWebView();
            }
        }else if(mPayStytle.equals(getString(R.string.payStytle_weixin))||mPayStytle.equals(getString(R.string.payStytle_weixin_yun))){
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
            startWebView();
        }
    }

    @Subscribe
    public void paySuccess(final CheckSuccess event) {
        mWebview.setVisibility(View.GONE);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                flag = true;
                mDrawable2.start();
                mLlReturn.setVisibility(View.VISIBLE);
                mTvReturn.setText("恭喜你," + event.payStytle + "收款成功!");
            }
        });
    }

    @OnClick(R.id.img_return)
    public void onViewClicked() {
        PayExit();
    }

    private int getNumber() {
        Random random = new Random();
        return random.nextInt(100000);
    }

    private void startWebView() {
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
                startTimer();
            }
        });
        mWebview.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);
            }
        });
    }

    private void startTimer() {
        Thread thread = new Thread(new MyThread());
        thread.start();
    }

    boolean flag = false;
    class MyThread implements Runnable {
        @Override
        public void run() {
            getHtmlResult(mPayStytle);
        }
    }

    private synchronized void getHtmlResult(final String payStytle) {
        String result = DownHTTP.getResult(chaUrl);
        if(result.contains("支付成功") || result.contains("SUCCESS")){

            if (payStytle.equals(getString(R.string.payStytle_zhifubao_yun))||payStytle.equals(getString(R.string.payStytle_zhifubao))) {
                    ZFBID = result.substring(result.indexOf("*") + 1, result.length());
                }
            if(!TextUtils.isEmpty(mSqlYun)&&!TextUtils.isEmpty(mSqlLocal)){//云会员-扫码
               new MyAsync().execute();
            }else{//纯扫码
                Post6.getInstance().Http_scan(mItem,mBm,mPad,mID,ZFBID,mPayStytle);
            }

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
                    startTimer();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
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


    class MyAsync extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... strings) {
            String result = DownHTTP.postResult(Net.yunUrl, "7", mSqlYun);
            return result;
        }
        @Override
        protected void onPostExecute(String result) {
            if (result.contains("richado")) {
                Post6.getInstance().Http_yun_scan(mItem,mSqlLocal,mBm,mPad,mID,ZFBID,mPayStytle);
            }else{
                Toast.makeText(WebViewPayActivity.this,"云会员提交失败", Toast.LENGTH_SHORT).show();
            }
            super.onPostExecute(result);
        }
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}

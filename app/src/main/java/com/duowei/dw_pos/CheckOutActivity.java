package com.duowei.dw_pos;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.duowei.dw_pos.bean.Moneys;
import com.duowei.dw_pos.bean.WMLSB;
import com.duowei.dw_pos.bean.Wmslbjb_jiezhang;
import com.duowei.dw_pos.dialog.CheckOutDialog;
import com.duowei.dw_pos.httputils.DownHTTP;
import com.duowei.dw_pos.httputils.VolleyResultListener;
import com.duowei.dw_pos.sunmiprint.BytesUtil;
import com.duowei.dw_pos.sunmiprint.ThreadPoolManager;
import com.duowei.dw_pos.tools.Net;
import com.duowei.dw_pos.tools.Users;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import woyou.aidlservice.jiuiv5.ICallback;
import woyou.aidlservice.jiuiv5.IWoyouService;

public class CheckOutActivity extends AppCompatActivity {

    @BindView(R.id.img_return)
    ImageView mImgReturn;
    @BindView(R.id.rlTop)
    RelativeLayout mRlTop;
    @BindView(R.id.tv_user)
    TextView mTvUser;
    @BindView(R.id.btn_dayin)
    Button mBtnDayin;
    @BindView(R.id.btn_dingdan)
    Button mBtnDingdan;
    @BindView(R.id.tv_time)
    TextView mTvTime;
    @BindView(R.id.tv_zonger)
    TextView mTvZonger;
    @BindView(R.id.tv_zekou)
    TextView mTvZekou;
    @BindView(R.id.tv_yishou)
    TextView mTvYishou;
    @BindView(R.id.tv_daishou)
    TextView mTvDaishou;
    @BindView(R.id.tv_zhaoling)
    TextView mTvZhaoling;
    @BindView(R.id.imgzhifubao)
    ImageView mImgzhifubao;
    @BindView(R.id.rl_zhifubao)
    RelativeLayout mRlZhifubao;
    @BindView(R.id.imgweixin)
    ImageView mImgweixin;
    @BindView(R.id.rl_weixin)
    RelativeLayout mRlWeixin;
    @BindView(R.id.rl_jiezhang)
    RelativeLayout mRlJiezhang;
    @BindView(R.id.tv_table)
    TextView mTvTable;
    @BindView(R.id.tvPersons)
    TextView mTvPersons;
    @BindView(R.id.tv_opener)
    TextView mTvOpener;
    @BindView(R.id.ll_cashier)
    LinearLayout mLlCashier;
    private float mTotalMoney = 0;//总额(原始价格总额)
    private float mActualMoney = 0;//实际金额
    private float mYishou = 0.00f;
    private float mYingshou=0.00f;
    private float mZhaoling=0.00f;
    private float mDaishou=0.00f;

    private IWoyouService woyouService;
    private ICallback callback = null;
    private String[] text = new String[3];
    private int[] width = new int[]{20, 6, 8};
    private int[] align = new int[]{0, 0, 0};
    private ServiceConnection connService = new ServiceConnection() {
        @Override
        public void onServiceDisconnected(ComponentName name) {
            woyouService = null;
        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            woyouService = IWoyouService.Stub.asInterface(service);
            mBtnDayin.setEnabled(true);
        }
    };
    private Wmslbjb_jiezhang mWmlsbjb;
    private WMLSB[] mWmlsbs;
    private Intent mIntent;
    private String mWmdbh;
    private String mPad;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_out);
        ButterKnife.bind(this);
        SharedPreferences user = getSharedPreferences("user", Context.MODE_PRIVATE);
        mPad = user.getString("pad", "");

        callback = new ICallback.Stub() {
            @Override
            public void onRunResult(final boolean success) throws RemoteException {
            }

            @Override
            public void onReturnString(final String value) throws RemoteException {
            }

            @Override
            public void onRaiseException(int code, final String msg) throws RemoteException {
            }
        };
        Intent intent = new Intent();
        intent.setPackage("woyou.aidlservice.jiuiv5");
        intent.setAction("woyou.aidlservice.jiuiv5.IWoyouService");
        startService(intent);
        bindService(intent, connService, Context.BIND_AUTO_CREATE);
        mWmdbh = getIntent().getStringExtra("WMDBH");
    }

    @Override
    protected void onStart() {
        super.onStart();
        mTvUser.setText(Users.YHMC);

        String sqlWmlsb = "SELECT convert(varchar(30),getdate(),121) ZSSJ2, isnull(BY3,0)BY3,* FROM WMLSB WHERE WMDBH = '" + mWmdbh + "'|";
        DownHTTP.postVolley6(Net.url, sqlWmlsb, new VolleyResultListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }

            @Override
            public void onResponse(String response) {
                Gson gson = new Gson();
                mWmlsbs = gson.fromJson(response, WMLSB[].class);
                for (WMLSB W : mWmlsbs) {
                    mTotalMoney = mTotalMoney + W.getYSJG() * W.getSL();
                    mActualMoney = mActualMoney + W.getDJ() * W.getSL();
                }
                mYingshou=mActualMoney - mYishou;
                mTvZonger.setText("￥" + mTotalMoney);
                mTvZekou.setText("￥" + (mTotalMoney - mActualMoney));
                mTvYishou.setText("￥" + mYishou);
                mTvDaishou.setText("￥" + mYingshou);
                mDaishou=mYingshou;
                Moneys.wfjr = mActualMoney - mYishou;

                String sqlWmlsbjb = "select convert(varchar(10),getdate(),120) as sj,WMDBH,ZH,JCRS,YS,isnull(BY1,'')BY1,ZKFS,convert(varchar(19), JYSJ,120)JYSJ,jcfs,DJLSH,YHBH,JSJ " +
                        "from WMLSBJB where WMDBH='" + mWmdbh + "'|";
                DownHTTP.postVolley6(Net.url, sqlWmlsbjb, new VolleyResultListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                    }

                    @Override
                    public void onResponse(String response) {
                        Gson gson1 = new Gson();
                        Wmslbjb_jiezhang[] wmslbjb = gson1.fromJson(response, Wmslbjb_jiezhang[].class);
                        mWmlsbjb = wmslbjb[0];
                        mTvTable.setText(mWmlsbjb.getZH());
                        mTvTime.setText(mWmlsbjb.getJYSJ());
                        mTvPersons.setText(mWmlsbjb.getJCRS() + "人");
                        mTvOpener.setText(mWmlsbjb.getYHBH());
                    }
                });
            }
        });
    }

    @OnClick({R.id.img_return, R.id.btn_dayin, R.id.btn_dingdan, R.id.rl_zhifubao, R.id.rl_weixin, R.id.rl_jiezhang,R.id.ll_cashier})
    public void onClick(View view) {
        Float daishou;
        switch (view.getId()) {
            case R.id.img_return:
                finish();
                break;
            case R.id.btn_dayin:
                ThreadPoolManager.getInstance().executeTask(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            woyouService.setAlignment(1, callback);
                            woyouService.printTextWithFont("桌号：" + mWmlsbjb.getZH() + "\n", "", 32, callback);
                            woyouService.setAlignment(0, callback);
                            woyouService.printTextWithFont("账单号：" + mWmlsbjb.getWMDBH() + "\n", "", 28, callback);
                            woyouService.printTextWithFont("日期：" + mWmlsbjb.getJYSJ() + "\n", "", 28, callback);
                            woyouService.printTextWithFont("点单员：" + mWmlsbjb.getYHBH() + "    人数：" + mWmlsbjb.getJCRS() + "\n", "", 28, callback);
                            woyouService.sendRAWData(BytesUtil.initLine1(384, 1), callback);
                            text[0] = "单品名称";
                            text[1] = "数量";
                            text[2] = "金额";
                            woyouService.printColumnsText(text, width, align, callback);
                            for (int i = 0; i < mWmlsbs.length; i++) {
                                text[0] = mWmlsbs[i].getXMMC();
                                text[1] = mWmlsbs[i].getSL() + "";
                                text[2] = mWmlsbs[i].getXJ() + "";
                                woyouService.printColumnsText(text, width, align, callback);
                            }
                            woyouService.sendRAWData(BytesUtil.initLine1(384, 1), callback);
                            woyouService.printTextWithFont("原价合计：" + mTotalMoney + "\n", "", 30, callback);
                            woyouService.printTextWithFont("折扣：" + (mTotalMoney - mActualMoney) + "\n", "", 30, callback);
                            woyouService.printTextWithFont("应付：" + mActualMoney + "\n", "", 30, callback);
                            woyouService.sendRAWData(BytesUtil.initLine1(384, 1), callback);
                            woyouService.setAlignment(1, callback);
                            woyouService.printTextWithFont("此单据不作结账单使用", "", 32, callback);
                            woyouService.lineWrap(4, callback);
                        } catch (RemoteException e) {
                            e.printStackTrace();
                        }
                    }
                });
                break;
            case R.id.btn_dingdan:
                break;
            case R.id.rl_zhifubao:
                mIntent = new Intent(this, WebViewPayActivity.class);
                mIntent.putExtra("WMLSBJB", mWmlsbjb);
                mIntent.putExtra("from", "支付宝");
                startActivity(mIntent);
                finish();
                break;
            case R.id.rl_weixin:
                mIntent = new Intent(this, WebViewPayActivity.class);
                mIntent.putExtra("WMLSBJB", mWmlsbjb);
                mIntent.putExtra("from", "微信");
                startActivity(mIntent);
                finish();
                break;
            case R.id.ll_cashier:
                inputMoney();
                break;
            case R.id.rl_jiezhang:
                if(mDaishou>0){
                    inputMoney();
                }else{
                    String sj = mWmlsbjb.getSj().replaceAll("-", "");
                    String exec = "exec prc_AADBPRK_android_001 '" + sj + "',1|";
                    DownHTTP.postVolley6(Net.url, exec, new VolleyResultListener() {
                        @Override
                        public void onErrorResponse(VolleyError volleyError) {
                            Toast.makeText(CheckOutActivity.this,"网络异常",Toast.LENGTH_SHORT).show();
                        }
                        @Override
                        public void onResponse(String s) {
                            try {
                                JSONArray jsonArray = new JSONArray(s);
                                JSONObject jsonObject = jsonArray.getJSONObject(0);
                                int prk = jsonObject.getInt("prk");
                                String insertXSJBXX = "insert into XSJBXX (XSDH,XH,DDYBH,ZS,JEZJ,ZKJE,ZRJE,YS,SS,ZKFS,DDSJ,JYSJ,BZ,JZFSBM,BMMC,WMBS,ZH,KHBH,QKJE,JCRS,BY7)" +
                                        "VALUES('" + mWmlsbjb.getWMDBH() + "','" + mWmlsbjb.getYHBH() + "','" + Users.YHMC + "','无折扣','" + Moneys.xfzr + "','" + Moneys.zkjr + "',"+mYingshou+",'" + mWmlsbjb.getYS() + "',0,'无'," +
                                        "'" + mWmlsbjb.getJYSJ() + "',GETDATE(),'"+mPad+"','" + mWmlsbjb.getJcfs() + "','','" + prk + "','" + mWmlsbjb.getZH() + "',"+mYishou+","+mZhaoling+",'" + mWmlsbjb.getJCRS() + "','')|";
                                String insertXSMXXX = "insert into XSMXXX(XH,XSDH,XMBH,XMMC,TM,DW,YSJG,XSJG,SL,XSJEXJ,FTJE,SYYXM,SQRXM,SFXS,ZSSJ,TCXMBH,SSLBBM,BZ)" +
                                        "select WMDBH+convert(varchar(10),xh),WMDBH,xmbh,xmmc,tm,dw,ysjg,dj,sl,ysjg*sl,dj*sl,syyxm,SQRXM,SFXS,ZSSJ,TCXMBH,by2,BY13 from wmlsb where wmdbh='" + mWmlsbjb.getWMDBH() + "'|";
                                String updateWMLSBJB = "update WMLSBJB set JSJ='"+mPad+"',SFYJZ='1',DJLSH='" + prk + "',YSJE='" + Moneys.xfzr + "',JSKSSJ=getdate() where WMDBH='" + mWmlsbjb.getWMDBH() + "'|";
                                String sql = insertXSJBXX + insertXSMXXX + updateWMLSBJB;
                                DownHTTP.postVolley7(Net.url, sql, new VolleyResultListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError error) {
                                    }
                                    @Override
                                    public void onResponse(String response) {
                                       if(response.contains("richado")){
                                           finish();
                                       }
                                    }
                                });

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    });



                }
                break;
        }
    }

    private void inputMoney() {
        final CheckOutDialog dialog = new CheckOutDialog(this, "收现");
        dialog.mConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String money = dialog.mEtInput.getText().toString().trim();
                mYishou=Float.parseFloat(money);
                mTvYishou.setText("￥"+String.format(Locale.CANADA,"%.2f",mYishou));
                mZhaoling=(mYishou-mYingshou)>=0?mYishou-mYingshou:mYishou-mYingshou;
                mTvZhaoling.setText("￥"+String.format(Locale.CANADA,"%.2f",mZhaoling));
                mDaishou=mZhaoling>=0?0.00f:-mZhaoling;
                mTvDaishou.setText("￥"+String.format(Locale.CANADA,"%.2f",mDaishou));
                dialog.cancel();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(connService);
    }
}

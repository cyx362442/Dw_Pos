package com.duowei.dw_pos;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.duowei.dw_pos.bean.Moneys;
import com.duowei.dw_pos.bean.WMLSB;
import com.duowei.dw_pos.bean.Wmslbjb_jiezhang;
import com.duowei.dw_pos.bean.YHJBQK;
import com.duowei.dw_pos.constant.ExtraParm;
import com.duowei.dw_pos.dialog.CheckOutDialog;
import com.duowei.dw_pos.dialog.ConfirmDialog;
import com.duowei.dw_pos.event.FinishEvent;
import com.duowei.dw_pos.event.YunSqlFinish;
import com.duowei.dw_pos.httputils.DownHTTP;
import com.duowei.dw_pos.httputils.VolleyResultListener;
import com.duowei.dw_pos.sunmiprint.Prints;
import com.duowei.dw_pos.tools.CartList;
import com.duowei.dw_pos.tools.CloseActivity;
import com.duowei.dw_pos.tools.Net;
import com.duowei.dw_pos.tools.SqlYun;
import com.duowei.dw_pos.tools.Users;
import com.google.gson.Gson;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.EventBus;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import woyou.aidlservice.jiuiv5.IWoyouService;

public class CheckOutActivity extends AppCompatActivity implements ConfirmDialog.OnconfirmClick {
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
    @BindView(R.id.tv_table)
    TextView mTvTable;
    @BindView(R.id.tvPersons)
    TextView mTvPersons;
    @BindView(R.id.tv_opener)
    TextView mTvOpener;
    @BindView(R.id.ll_cashier)
    LinearLayout mLlCashier;
    @BindView(R.id.rl_yun)
    RelativeLayout mRlYun;
    @BindView(R.id.imgxianjin)
    ImageView mImgxianjin;
    @BindView(R.id.rl_xianjin)
    RelativeLayout mRlXianjin;
    @BindView(R.id.imgyun)
    ImageView mImgyun;
    @BindView(R.id.progressBar)
    ProgressBar mProgressBar;
    private ArrayList<WMLSB> list_wmlsb = new ArrayList<>();
    private float mTotalMoney = 0;//总额(原始价格总额)
    private float mActualMoney = 0;//实际金额
    private float mYishou = 0.00f;
    private float mYingshou = 0.00f;
    private float mZhaoling = 0.00f;
    private float mDaishou = 0.00f;

    private final int YUPAYREQUEST = 100;

    private IWoyouService woyouService;
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
    private Prints mPrinter;
    private ConfirmDialog mConfirmDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_out);
        ButterKnife.bind(this);
        EventBus.getDefault().register(this);
        mConfirmDialog = ConfirmDialog.instance();

        CloseActivity.addAcitity(this);
        SharedPreferences user = getSharedPreferences("user", Context.MODE_PRIVATE);
        mPad = user.getString("pad", "");

        mWmdbh = getIntent().getStringExtra("WMDBH");
        mPrinter = Prints.getPrinter();
        mPrinter.bindPrintService(this, connService);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mTvUser.setText(Users.YHMC);
        Http_initData();
    }

    private void Http_initData() {
        mProgressBar.setVisibility(View.VISIBLE);
        list_wmlsb.clear();
        mTotalMoney = 0.00f;
        mActualMoney = 0.00f;
        String sqlWmlsbjb = "select convert(varchar(10),getdate(),120) as sj,WMDBH,ZH,JCRS,YS,isnull(BY1,'')BY1,isnull(ZKFS,'无')ZKFS,convert(varchar(19), JYSJ,120)JYSJ,isnull(jcfs,'')jcfs,DJLSH,YHBH,JSJ " +
                "from WMLSBJB where WMDBH='" + mWmdbh + "'|";
        DownHTTP.postVolley6(Net.url, sqlWmlsbjb, new VolleyResultListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                mProgressBar.setVisibility(View.GONE);
                Toast.makeText(CheckOutActivity.this, "数据加载失败", Toast.LENGTH_SHORT).show();
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

                String sqlWmlsb = "SELECT convert(varchar(30),getdate(),121) ZSSJ2, isnull(BY3,0)BY3,* FROM WMLSB WHERE WMDBH = '" + mWmdbh + "'|";
                DownHTTP.postVolley6(Net.url, sqlWmlsb, new VolleyResultListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        mProgressBar.setVisibility(View.GONE);
                        Toast.makeText(CheckOutActivity.this, "数据加载失败", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onResponse(String response) {
                        if (response.equals("]")) {
                            mTvZonger.setText("￥" + 0.00);
                            mTvZekou.setText("￥" + 0.00);
                            mTvYishou.setText("￥" + 0.00);
                            mTvDaishou.setText("￥" + 0.00);
                            mProgressBar.setVisibility(View.GONE);
                            return;
                        }
                        Gson gson = new Gson();
                        mWmlsbs = gson.fromJson(response, WMLSB[].class);
                        for (WMLSB W : mWmlsbs) {
                            mTotalMoney = mTotalMoney + W.getYSJG() * W.getSL();
                            mActualMoney = mActualMoney + W.getDJ() * W.getSL();
                            list_wmlsb.add(W);
                        }
                        mYingshou = mActualMoney - mYishou;
                        mTvZonger.setText("￥" + String.format(Locale.CHINA, "%.2f", mTotalMoney));
                        mTvZekou.setText("￥" + String.format(Locale.CHINA, "%.2f", mTotalMoney - mActualMoney));
                        mTvYishou.setText("￥" + String.format(Locale.CHINA, "%.2f", mYishou));
                        mTvDaishou.setText("￥" + String.format(Locale.CHINA, "%.2f", mYingshou));

                        mDaishou = mYingshou;

                        Moneys.xfzr = mTotalMoney;
                        Moneys.zkjr = mTotalMoney - mActualMoney;
                        Moneys.ysjr = mYingshou;
                        Moneys.wfjr = mActualMoney - mYishou;
                        mPrinter.setPrintMsg(mWmlsbjb, mWmlsbs);

                        mProgressBar.setVisibility(View.GONE);
                    }
                });
            }
        });
    }

    @OnClick({R.id.btn_dayin, R.id.btn_dingdan, R.id.rl_xianjin, R.id.rl_zhifubao, R.id.rl_weixin, R.id.ll_cashier, R.id.rl_yun})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_dayin:
                mPrinter.setWoyouService(woyouService);
                mPrinter.print_yudayin();
                break;
            case R.id.btn_dingdan:
                CartList.newInstance(this).clear();
                mIntent = new Intent(this, CartDetailActivity.class);
                mIntent.putExtra(ExtraParm.EXTRA_WMDBH, mWmlsbjb.getWMDBH());
                startActivity(mIntent);
                break;
            case R.id.rl_zhifubao:
                if (canCheck()) return;

                mIntent = new Intent(this, WebViewPayActivity.class);
                mIntent.putExtra("WMLSBJB", mWmlsbjb);
                mIntent.putExtra("listWmlsb", list_wmlsb);
                mIntent.putExtra("from", "支付宝");
                startActivity(mIntent);
                break;
            case R.id.rl_weixin:
                if (canCheck()) return;

                mIntent = new Intent(this, WebViewPayActivity.class);
                mIntent.putExtra("WMLSBJB", mWmlsbjb);
                mIntent.putExtra("listWmlsb", list_wmlsb);
                mIntent.putExtra("from", "微信");
                startActivity(mIntent);
                break;
            case R.id.rl_yun:
                if (canCheck()) return;

                mIntent = new Intent(this, YunLandActivity.class);
                mIntent.putExtra("WMLSBJB", mWmlsbjb);
                mIntent.putExtra("listWmlsb", list_wmlsb);
                startActivityForResult(mIntent, YUPAYREQUEST);
                break;
            case R.id.ll_cashier:
//                inputMoney();
                break;
            case R.id.rl_xianjin:
                if (canCheck()) return;

                inputMoney();
                break;
        }
    }

    private boolean canCheck() {
        List<YHJBQK> yhjbqk = DataSupport.select("ZPQX").where("YHBH=?", Users.YHBH).find(YHJBQK.class);
        String zpqx = yhjbqk.get(0).getZPQX();
        if(!zpqx.equals("1")){
            mConfirmDialog.show(this,"当前账号没有结账权限，是否切换有结账权限账号登录？");
            mConfirmDialog.setOnconfirmClick(this);
            return true;
        }
        return false;
    }

    /**
     * 现金结账
     */
    private void Http_cashier() {
        mProgressBar.setVisibility(View.VISIBLE);
        String sj = mWmlsbjb.getSj().replaceAll("-", "");
        String exec = "exec prc_AADBPRK_android_001 '" + sj + "',1|";
        DownHTTP.postVolley6(Net.url, exec, new VolleyResultListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Toast.makeText(CheckOutActivity.this, "网络异常", Toast.LENGTH_SHORT).show();
                mProgressBar.setVisibility(View.GONE);
            }
            @Override
            public void onResponse(String s) {
                try {
                    JSONArray jsonArray = new JSONArray(s);
                    JSONObject jsonObject = jsonArray.getJSONObject(0);
                    int prk = jsonObject.getInt("prk");
                    String insertXSJBXX = "insert into XSJBXX (XSDH,XH,DDYBH,ZS,JEZJ,ZKJE,ZRJE,YS,SS,ZKFS,DDSJ,JYSJ,BZ,JZFSBM,BMMC,WMBS,ZH,KHBH,QKJE,JCRS,BY7)" +
                            "VALUES('" + mWmlsbjb.getWMDBH() + "','" + Users.YHBH + "','" + Users.YHMC + "','无折扣','" + Moneys.xfzr + "','" + Moneys.zkjr + "'," + mYingshou + ",'" + mWmlsbjb.getYS() + "',0,'" + mWmlsbjb.getZKFS() + "'," +
                            "'" + mWmlsbjb.getJYSJ() + "',GETDATE(),'" + mPad + "','" + mWmlsbjb.getJcfs() + "','','" + prk + "','" + mWmlsbjb.getZH() + "'," + mYishou + "," + mZhaoling + ",'" + mWmlsbjb.getJCRS() + "','')|";
                    String insertXSMXXX = "insert into XSMXXX(XH,XSDH,XMBH,XMMC,TM,DW,YSJG,XSJG,SL,XSJEXJ,FTJE,SYYXM,SQRXM,SFXS,ZSSJ,TCXMBH,SSLBBM,BZ)" +
                            "select WMDBH+convert(varchar(10),xh),WMDBH,xmbh,xmmc,tm,dw,ysjg,dj,sl,ysjg*sl,dj*sl,syyxm,SQRXM,SFXS,ZSSJ,TCXMBH,by2,BY13 from wmlsb where wmdbh='" + mWmlsbjb.getWMDBH() + "'|";
                    String updateWMLSBJB = "update WMLSBJB set JSJ='" + mPad + "',SFYJZ='1',DJLSH='" + prk + "',YSJE='" + Moneys.xfzr + "',JSKSSJ=getdate() where WMDBH='" + mWmlsbjb.getWMDBH() + "'|";
                    String sql = insertXSJBXX + insertXSMXXX + updateWMLSBJB;
                    DownHTTP.postVolley7(Net.url, sql, new VolleyResultListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Toast.makeText(CheckOutActivity.this, "网络异常", Toast.LENGTH_SHORT).show();
                            mProgressBar.setVisibility(View.GONE);
                        }

                        @Override
                        public void onResponse(String response) {
                            if (response.contains("richado")) {
                                mPrinter.setWoyouService(woyouService);
                                mPrinter.print_jiezhang(String.format(Locale.CANADA, "%.2f", mYingshou),
                                        String.format(Locale.CANADA, "%.2f", mYishou), String.format(Locale.CANADA, "%.2f", mZhaoling),"收现");
                                mProgressBar.setVisibility(View.GONE);
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

    private void inputMoney() {
        final CheckOutDialog dialog = new CheckOutDialog(this, "现金支付", mYingshou);
        dialog.mConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String money = dialog.mEtInput.getText().toString().trim();
                mYishou = Float.parseFloat(money);
                if (mYingshou > mYishou) {
                    Toast.makeText(CheckOutActivity.this, "输入金额不足", Toast.LENGTH_SHORT).show();
                    return;
                }
                mTvYishou.setText("￥" + String.format(Locale.CANADA, "%.2f", mYishou));
                mZhaoling = (mYishou - mYingshou) >= 0 ? mYishou - mYingshou : mYishou - mYingshou;
                mTvZhaoling.setText("￥" + String.format(Locale.CANADA, "%.2f", mZhaoling));
                mDaishou = mZhaoling >= 0 ? 0.00f : -mZhaoling;
                mTvDaishou.setText("￥" + String.format(Locale.CANADA, "%.2f", mDaishou));
                Http_cashier();
                dialog.cancel();
            }
        });
    }

    /**
     * 云会员支付全额支付
     */
    @Subscribe
    public void getYunPayLocal(final YunSqlFinish event) {
        mProgressBar.setVisibility(View.VISIBLE);
        String sj = mWmlsbjb.getSj().replaceAll("-", "");
        String exec = "exec prc_AADBPRK_android_001 '" + sj + "',1|";
        DownHTTP.postVolley6(Net.url, exec, new VolleyResultListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(CheckOutActivity.this, "网络异常", Toast.LENGTH_SHORT).show();
                mProgressBar.setVisibility(View.GONE);
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
                            "VALUES('" + mWmdbh + "','" + Users.YHBH + "','" + Users.YHMC + "','无折扣'," + Moneys.xfzr + "," + Moneys.zkjr + ",0," + Moneys.ysjr + ",0,'" + mWmlsbjb.getZKFS() + "'," +
                            "'" + mWmlsbjb.getJYSJ() + "',GETDATE(),'" + mPad + "','" + mWmlsbjb.getJcfs() + "','" + prk + "','" + mWmlsbjb.getZH() + "',0,0," + Integer.parseInt(mWmlsbjb.getJCRS()) + "," +
                            "" + SqlYun.CZKYE + ",'','','云会员消费'," + (SqlYun.jfbfb_add-SqlYun.jfbfb_sub) + ","+SqlYun.jfbfb_add+",'" + SqlYun.HYBH + "','" + SqlYun.HYKDJ + "')|";
                    String insertXSMXXX = "insert into XSMXXX(XH,XSDH,XMBH,XMMC,TM,DW,YSJG,XSJG,SL,XSJEXJ,FTJE,SYYXM,SQRXM,SFXS,ZSSJ,TCXMBH,SSLBBM,BZ)" +
                            "select WMDBH+convert(varchar(10),xh),WMDBH,xmbh,xmmc,tm,dw,ysjg,dj,sl,ysjg*sl,dj*sl,syyxm,SQRXM,SFXS,ZSSJ,TCXMBH,by2,BY13 from wmlsb where wmdbh='" + mWmdbh + "'|";
                    String updateWMLSBJB = "update WMLSBJB set JSJ='" + mPad + "',SFYJZ='1',DJLSH='" + prk + "',YSJE=" + Moneys.xfzr + ",JSKSSJ=getdate(),BY8='" + SqlYun.from_user + "',JZBZ='" + SqlYun.JZBZ + "' where WMDBH='" + mWmdbh + "'|";
                    String sql = event.sql + insertXSJBXX + insertXSMXXX + updateWMLSBJB;
                    DownHTTP.postVolley7(Net.url, sql, new VolleyResultListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Toast.makeText(CheckOutActivity.this, "云会员付款失败", Toast.LENGTH_SHORT).show();
                            mProgressBar.setVisibility(View.GONE);
                        }
                        @Override
                        public void onResponse(String response) {
                            if (response.contains("richado")) {
                                //打印结账单
                                mPrinter.setWoyouService(woyouService);
                                mPrinter.print_yun(event.mWmlsbjb, event.mListWmlsb,event.listPay);
                                mProgressBar.setVisibility(View.GONE);
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
    //接口回调，dialog确定键监听
    @Override
    public void confirmListener() {
        EventBus.getDefault().post(new FinishEvent());
        mConfirmDialog.cancel();
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(connService);
        EventBus.getDefault().unregister(this);
    }
}

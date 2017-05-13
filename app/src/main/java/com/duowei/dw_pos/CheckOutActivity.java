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
import com.duowei.dw_pos.bean.OrderNo;
import com.duowei.dw_pos.bean.WMLSB;
import com.duowei.dw_pos.bean.Wmslbjb_jiezhang;
import com.duowei.dw_pos.bean.YHJBQK;
import com.duowei.dw_pos.bean.YunFu;
import com.duowei.dw_pos.constant.ExtraParm;
import com.duowei.dw_pos.dialog.CheckOutDialog;
import com.duowei.dw_pos.dialog.ConfirmDialog;
import com.duowei.dw_pos.event.CheckSuccess;
import com.duowei.dw_pos.event.FinishEvent;
import com.duowei.dw_pos.event.YunSqlFinish;
import com.duowei.dw_pos.event.YunSubmit;
import com.duowei.dw_pos.httputils.DownHTTP;
import com.duowei.dw_pos.httputils.Post6;
import com.duowei.dw_pos.httputils.VolleyResultListener;
import com.duowei.dw_pos.sunmiprint.Prints;
import com.duowei.dw_pos.tools.CartList;
import com.duowei.dw_pos.tools.CloseActivity;
import com.duowei.dw_pos.tools.Net;
import com.duowei.dw_pos.tools.Users;
import com.google.gson.Gson;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.litepal.crud.DataSupport;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

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
    @BindView(R.id.ll_change)
    LinearLayout mLlChange;
    @BindView(R.id.linearLayout)
    LinearLayout mLinearLayout;
    private ArrayList<WMLSB> list_wmlsb = new ArrayList<>();
    private float mTotalMoney = 0;//总额(原始价格总额)
    private float mActualMoney = 0;//实际金额
    private float mYishou = 0.00f;
    private float mYingshou = 0.00f;
    private float mZhaoling = 0.00f;
    private float mDaishou = 0.00f;

    private final int YUPAYREQUEST = 100;
    public final static int RESURTCODE = 1000;

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
    private String mOrderstytle;
    //云会员付款方式
    private List<YunFu> mYunPayStytle;
    //云会员后的数据
    private ArrayList<WMLSB> mListYunWmlsb;
    private float mOtherPay;

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
        mOrderstytle = user.getString("orderstytle", getResources().getString(R.string.order_stytle_zhongxican));

        mWmdbh = getIntent().getStringExtra("WMDBH");
        mPrinter = Prints.getPrinter();
        mPrinter.bindPrintService(this, connService);

        CartList.newInstance(this).setOrderNo(new OrderNo(mWmdbh, true));
    }

    @Override
    protected void onResume() {
        super.onResume();
        mTvUser.setText(Users.YHMC);
        //中西餐
        if (mOrderstytle.equals(getResources().getString(R.string.order_stytle_zhongxican))) {
            mLlChange.setVisibility(View.VISIBLE);
            mLinearLayout.setVisibility(View.VISIBLE);
            //快餐
        } else {
            mLlChange.setVisibility(View.INVISIBLE);
            mLinearLayout.setVisibility(View.INVISIBLE);
        }
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
                        mYingshou = bigDecimal(bigDecimal(mActualMoney) - bigDecimal(mYishou));
                        mTvZonger.setText("￥" + bigDecimal(mTotalMoney));
                        mTvZekou.setText("￥" + bigDecimal(bigDecimal(mTotalMoney) - bigDecimal(mActualMoney)));
                        mTvYishou.setText("￥" + bigDecimal(mYishou));
                        mTvDaishou.setText("￥" + bigDecimal(mYingshou));
                        mDaishou = mYingshou;

                        Moneys.xfzr = bigDecimal(mTotalMoney);
                        Moneys.zkjr = bigDecimal(bigDecimal(mTotalMoney) - bigDecimal(mActualMoney));
                        Moneys.ysjr = bigDecimal(mYingshou);
                        Moneys.wfjr = bigDecimal(bigDecimal(mActualMoney) - bigDecimal(mYishou));
                        mPrinter.setPrintMsg(mWmlsbjb, mWmlsbs);

                        mProgressBar.setVisibility(View.GONE);
                    }
                });
            }
        });
    }

    @OnClick({R.id.btn_dayin, R.id.btn_dingdan, R.id.rl_xianjin, R.id.rl_zhifubao, R.id.rl_weixin, R.id.ll_cashier, R.id.rl_yun, R.id.ll_change})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ll_change:
                Intent intent = new Intent();
                intent.putExtra("wmdbh", mWmdbh);
                setResult(RESURTCODE, intent);
                finish();
                break;
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
                mIntent.putExtra("from", getString(R.string.payStytle_zhifubao));
                startActivity(mIntent);
                break;
            case R.id.rl_weixin:
                if (canCheck()) return;

                mIntent = new Intent(this, WebViewPayActivity.class);
                mIntent.putExtra("WMLSBJB", mWmlsbjb);
                mIntent.putExtra("from", getString(R.string.payStytle_weixin));
                startActivity(mIntent);
                break;
            case R.id.rl_yun:
                if (canCheck()) return;

                mIntent = new Intent(this, YunLandActivity.class);
                mIntent.putExtra("WMLSBJB", mWmlsbjb);
                mIntent.putExtra("listWmlsb", list_wmlsb);
                startActivityForResult(mIntent, YUPAYREQUEST);
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
        if (!zpqx.equals("1")) {
            mConfirmDialog.show(this, "当前账号没有结账权限，是否切换有结账权限账号登录？");
            mConfirmDialog.setOnconfirmClick(this);
            return true;
        }
        return false;
    }
    /**输入金额*/
    private void inputMoney() {
        final CheckOutDialog dialog = new CheckOutDialog(this, "现金支付", bigDecimal(mYingshou));
        dialog.mConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String money = dialog.mEtInput.getText().toString().trim();
                mYishou = Float.parseFloat(money);
                if (bigDecimal(mYingshou) > mYishou) {
                    Toast.makeText(CheckOutActivity.this, "输入金额不足", Toast.LENGTH_SHORT).show();
                    return;
                }
                mTvYishou.setText("￥" + bigDecimal(mYingshou));
                mZhaoling = (mYishou - mYingshou) >= 0 ? mYishou - mYingshou : mYishou - mYingshou;
                mTvZhaoling.setText("￥" + bigDecimal(mZhaoling));
                mDaishou = mZhaoling >= 0 ? 0.00f : -mZhaoling;
                mTvDaishou.setText("￥" + bigDecimal(mDaishou));
                Http_cashier();
                dialog.cancel();
            }
        });
    }
    /**
     * 现金结账
     */
    private void Http_cashier() {
        mProgressBar.setVisibility(View.VISIBLE);
        Post6.getInstance().Http_cashier(this,mWmlsbjb,mPad,mYingshou,mYishou,mZhaoling);
    }

    /**
     * 云会员支付全额支付
     */
    @Subscribe
    public void getYunPayLocal(final YunSqlFinish event) {
        mProgressBar.setVisibility(View.VISIBLE);
        Post6.getInstance().Http_yun(this,mWmlsbjb,event.sql);
    }
    @Subscribe
    public void yunSubmit(YunSubmit event){
        mListYunWmlsb = event.mListWmlsb;
        mYunPayStytle = event.listPay;
        mOtherPay = event.otherPay;
    }
    @Subscribe
    public void checkSuccess(CheckSuccess event){
        mPrinter.setWoyouService(woyouService);
        if(event.payStytle.equals(getResources().getString(R.string.payStytle_cash))){//现金支付
            mPrinter.print_jiezhang(this,bigDecimal(mYingshou) + "",
                    bigDecimal(mYishou) + "", bigDecimal(mZhaoling) + "", event.payStytle);
        }else if(event.payStytle.equals(getString(R.string.payStytle_zhifubao))||event.payStytle.equals(getString(R.string.payStytle_weixin))){//支付宝，微信
            mPrinter.print_jiezhang(this,mYingshou+"", mYingshou+"", "0.00", event.payStytle);
        }else if(event.payStytle.equals(getResources().getString(R.string.payStytle_yun))){//云会员支付
            mPrinter.print_yun(mWmlsbjb, mListYunWmlsb, mYunPayStytle,"",0);
            mProgressBar.setVisibility(View.GONE);
        }else if(event.payStytle.equals(getString(R.string.payStytle_cash_yun))){//云会员、现金支付
            mPrinter.print_yun(mWmlsbjb, mListYunWmlsb, mYunPayStytle,event.payStytle,mOtherPay);
            mProgressBar.setVisibility(View.GONE);
        }else if(event.payStytle.equals(getString(R.string.payStytle_zhifubao_yun))||event.payStytle.equals(getString(R.string.payStytle_weixin_yun))){//云会员、扫码
            mPrinter.print_yun(mWmlsbjb, mListYunWmlsb, mYunPayStytle,event.payStytle,mOtherPay);
            mProgressBar.setVisibility(View.GONE);
        }
        mProgressBar.setVisibility(View.GONE);
        finish();
    }

    public Float bigDecimal(Float f) {
        return BigDecimal.valueOf(f).setScale(1, BigDecimal.ROUND_HALF_UP).floatValue();
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

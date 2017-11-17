package com.duowei.dw_pos;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.app.AppCompatDialogFragment;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.duowei.dw_pos.adapter.CartDetailItemAdapter;
import com.duowei.dw_pos.bean.OpenInfo;
import com.duowei.dw_pos.bean.OrderNo;
import com.duowei.dw_pos.bean.WMLSB;
import com.duowei.dw_pos.bean.WMLSBJB;
import com.duowei.dw_pos.constant.ExtraParm;
import com.duowei.dw_pos.dialog.NumInputDialog;
import com.duowei.dw_pos.event.CartAutoSubmit;
import com.duowei.dw_pos.event.CartMsgDialogEvent;
import com.duowei.dw_pos.event.CartRemoteUpdateEvent;
import com.duowei.dw_pos.event.CartUpdateEvent;
import com.duowei.dw_pos.event.CheckSuccess;
import com.duowei.dw_pos.event.Commit;
import com.duowei.dw_pos.event.FinishEvent;
import com.duowei.dw_pos.fragment.MessageDialogFragment;
import com.duowei.dw_pos.fragment.TasteChoiceDialogFragment;
import com.duowei.dw_pos.httputils.NetUtils;
import com.duowei.dw_pos.sunmiprint.Prints;
import com.duowei.dw_pos.tools.CartList;
import com.duowei.dw_pos.tools.DateTimeUtils;
import com.duowei.dw_pos.tools.Net;
import com.duowei.dw_pos.tools.SqlNetHandler;
import com.duowei.dw_pos.tools.Users;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import woyou.aidlservice.jiuiv5.IWoyouService;

/**
 * 订单详情
 */

public class CartDetailActivity extends AppCompatActivity implements View.OnClickListener, NumInputDialog.OnconfirmClick {

    static {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
    }

    private Handler mHandler = new Handler();

    private TextView mTitleView;
    private ListView mListView;

    /** 加单按钮 */
    private Button mAddButton;

    /** 下单按钮 */
    private Button mSubmit1Button;

    private CartDetailItemAdapter mAdapter;

    private String mWmdbh;
    /** 加载成功 */
    private boolean mLoadSuccess = false;

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
    private String mOrderstytle;
    private LinearLayout mLlCommit;
    private Button mBCheck;
    private NumInputDialog mDialog;
    private ProgressBar mPb;
    private Button mBtnBack;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart_detail);
        EventBus.getDefault().register(this);
        initViews();
        mPrinter = Prints.getPrinter();
        mPrinter.bindPrintService(this, connService);
        SharedPreferences sp = getSharedPreferences("user", Context.MODE_PRIVATE);
        mOrderstytle = sp.getString("orderstytle", getResources().getString(R.string.order_stytle_zhongxican));
    }

    @Override
    protected void onStart() {
        super.onStart();
        loadData();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        unbindService(connService);
    }

    private void initViews() {
        findViewById(R.id.iv_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        findViewById(R.id.btn_all_order_remark).setOnClickListener(this);
        mBtnBack = (Button) findViewById(R.id.btn_back_main);
        mBtnBack.setText(getString(R.string.backorder));
        mBtnBack.setOnClickListener(this);

        mPb = (ProgressBar) findViewById(R.id.pb);
        mLlCommit = (LinearLayout) findViewById(R.id.linearLayout);
        mBCheck = (Button) findViewById(R.id.btn_check);
        mBCheck.setOnClickListener(this);
        mTitleView = (TextView) findViewById(R.id.tv_title);
        mListView = (ListView) findViewById(R.id.list);
        mAddButton = (Button) findViewById(R.id.btn_add);
        mAddButton.setOnClickListener(this);

        mSubmit1Button = (Button) findViewById(R.id.btn_submit_1);
        mSubmit1Button.setOnClickListener(this);
    }

    private void loadData() {
        mWmdbh = getIntent().getStringExtra(ExtraParm.EXTRA_WMDBH);

        mAdapter = new CartDetailItemAdapter(this);
        mListView.setAdapter(mAdapter);

        if (!TextUtils.isEmpty(mWmdbh)) {
            // 从结账界面进来
            mAddButton.setVisibility(View.VISIBLE);

            getWmlsbjb(mWmdbh);

        } else {
            //中西餐版
            if(mOrderstytle.equals(getResources().getString(R.string.order_stytle_zhongxican))){
                mBCheck.setVisibility(View.GONE);
                mLlCommit.setVisibility(View.VISIBLE);
                autoSubmitData(null);
                //快餐版
            }else if(mOrderstytle.equals(getResources().getString(R.string.order_stytle_kuaican))){
                mLlCommit.setVisibility(View.GONE);
                mBCheck.setVisibility(View.VISIBLE);
                mAdapter.addLocalList(CartList.newInstance(this).getList());
                String title = "订餐详情";
                if (mAdapter.getTotalPrice() > 0) {
                    title += " ¥" + mAdapter.getTotalPrice();
                }
                mTitleView.setText(title);
                mPb.setVisibility(View.GONE);
            }
        }
    }

    @Subscribe
    public void updateUiData(CartUpdateEvent event) {
        updateData();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void updateRemoteUiDate(CartRemoteUpdateEvent event) {
        //中西餐
        if(mOrderstytle.equals(getResources().getString(R.string.order_stytle_zhongxican))){
            getWmlsb(CartList.newInstance(this).getOrderNo().getWmdbh());
        }
        //快餐
        else if(mOrderstytle.equals(getResources().getString(R.string.order_stytle_kuaican))){
            if(event.result.equals("success")){
                Intent intent = new Intent(this, CheckOutActivity.class);
                intent.putExtra("WMDBH",CartList.newInstance(this).getOrderNo().getWmdbh());
                startActivity(intent);
                mDialog.cancel();
            }else{
                mDialog.mConfirm.setEnabled(true);
            }
        }
    }

    @Subscribe
    public void commitSuccess(Commit event) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mSubmit1Button.setEnabled(true);
                mPb.setVisibility(View.GONE);
            }
        });

        if(mOrderstytle.equals(getResources().getString(R.string.order_stytle_kuaican))||event.wmlsbjb==null){
            return;
        }
        //打印
        mPrinter.setWoyouService(woyouService);
        mPrinter.print_commit(event.wmlsbjb, event.wmlsbList,event.seconds);
    }
    //结账成功
    @Subscribe
    public void checkSuccess(CheckSuccess event){
        CartList.newInstance(this).getList().clear();
        finish();
    }

    @Subscribe
    public void finishEvent(FinishEvent event){
        finish();
    }

    private void updateData() {
        mAdapter.clear();
        mAdapter.addRemoteList(CartList.sWMLSBList);
        mAdapter.addLocalList(CartList.newInstance(this).getList());

        String title = "订餐详情";
        if (mAdapter.getTotalPrice() > 0) {
            title += " ¥" + mAdapter.getTotalPrice();
        }
        mTitleView.setText(title);

        if (mAdapter.hasUnOrder()) {
            mSubmit1Button.setEnabled(true);
        } else {
            mSubmit1Button.setEnabled(false);
        }
        //中西餐
        if (CartList.sWMLSBJB == null&&mOrderstytle.equals(getResources().getString(R.string.order_stytle_zhongxican))) {
            getWmlsbjb(CartList.newInstance(this).getOrderNo().getWmdbh());
        }
        mPb.setVisibility(View.GONE);
    }

    @Subscribe
    public void showDialog(CartMsgDialogEvent event) {
        AppCompatDialogFragment fragment = MessageDialogFragment.newInstance(event.title, event.message);
        fragment.show(getSupportFragmentManager(), null);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();

        if (id == R.id.btn_back_main) {
            String s = mBtnBack.getText().toString();
            if(s.equals(getString(R.string.backtable))){
                EventBus.getDefault().post(new FinishEvent());
            }else{
                finish();
            }

        } else if (id == R.id.btn_add) {
            Intent intent = new Intent(this, CashierDeskActivity.class);
            intent.putExtras(getIntent().getExtras());
            startActivity(intent);

        } else if (id == R.id.btn_submit_1) {
            // 下单送厨打
            mPb.setVisibility(View.VISIBLE);
            mSubmit1Button.setEnabled(false);

            new SqlNetHandler().handleCommit1(mHandler, CartDetailActivity.this, CartList.newInstance(this).getOrderNo());

        } else if (id == R.id.btn_all_order_remark) {
            TasteChoiceDialogFragment fragment = TasteChoiceDialogFragment.newInstance();
            fragment.show(getSupportFragmentManager(), null);

        }else if(id==R.id.btn_check){
            mDialog=new NumInputDialog(this);

            mDialog.setOnconfirmClick(this);
        }
    }

    /**
     * 自动提交本地点单数据
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void autoSubmitData(CartAutoSubmit event) {
        if (CartList.newInstance(this).getList().size() > 0) {
            OrderNo orderNo = CartList.newInstance(this).getOrderNo();
            new SqlNetHandler().handleCommit(mHandler, CartDetailActivity.this, orderNo);
        } else {
            getWmlsb(CartList.newInstance(this).getOrderNo().getWmdbh());
        }
    }

    private void getWmlsbjb(final String wmdbh) {
        String sql = "select * from wmlsbjb where wmdbh = '" + wmdbh + "'|";
        NetUtils.post6(Net.url, sql, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try {
                    Type type = new TypeToken<ArrayList<WMLSBJB>>() {
                    }.getType();
                    List<WMLSBJB> wmlsbjbList = new Gson().fromJson(response.body().string(), type);
                    CartList.sWMLSBJB = wmlsbjbList.get(0);
                    mLoadSuccess = true;

                    autoSubmitData(null);

                } catch (JsonSyntaxException e) {
                    e.printStackTrace();
                }
            }
        });

    }

    private void getWmlsb(String wmdbh) {
        String sql = "select * from wmlsb where wmdbh = '" + wmdbh + "'|";
        NetUtils.post6(Net.url, sql, new Callback() {
            @Override
            public void onFailure(Call call, final IOException e) {
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(CartDetailActivity.this, e + "",Toast.LENGTH_SHORT).show();
                        mPb.setVisibility(View.GONE);
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try {
                    String result = response.body().string();

                    result = result.replaceAll("&lt;", "<").replaceAll("&gt;", ">");
                    Type type = new TypeToken<ArrayList<WMLSB>>() {
                    }.getType();
                    List<WMLSB> wmlsbList = new Gson().fromJson(result, type);
                    for (WMLSB e : wmlsbList) {
                        e.setRemote(1);
                    }
                    CartList.sWMLSBList = wmlsbList;

                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            updateData();
                            mBtnBack.setText(getString(R.string.backtable));
                        }
                    });

                } catch (JsonSyntaxException e) {
                    e.printStackTrace();

                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            CartList.sWMLSBList.clear();
                            updateData();
                        }
                    });
                }
            }
        });
    }

    private void httpCreateWmlsbjb() {
        OpenInfo openInfo = CartList.newInstance(this).getOpenInfo();
        OrderNo orderNo = CartList.newInstance(this).getOrderNo();

        final WMLSBJB wmlsbjb = new WMLSBJB(
                orderNo.getWmdbh(),
                Users.YHMC,
                openInfo.getDeskNo(),
                "0", // 是否已结账
                Users.pad,
                openInfo.getPeopleNum(),
                0,
                "1",
                openInfo.getPeopleType(),
                openInfo.getRemark()
        );
        String sql=mOrderstytle.equals(getString(R.string.order_stytle_zhongxican))?wmlsbjb.toInsertString():wmlsbjb.toInsertString2(mAdapter.getOriginalMoney(),mAdapter.getTotalPrice());
        NetUtils.post7(Net.url, sql, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                EventBus.getDefault().post(new CartRemoteUpdateEvent("fail"));
                e.printStackTrace();
            }
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String result = response.body().string();
                if (result.equals("]")) {
                    return;
                }
                if (result.contains("richado")) {
                    CartList.sWMLSBJB = wmlsbjb;
                    if(CartList.newInstance(CartDetailActivity.this).getList().size() > 0){
                        OrderNo orderNo = CartList.newInstance(CartDetailActivity.this).getOrderNo();
                        new SqlNetHandler().handleCommit(mHandler, CartDetailActivity.this, orderNo);
                    }
                }
            }
        });
    }
    /**快餐模式，获取餐牌号,生成定单，送厨打*/
    @Override
    public void getDialogInput(String tableNum, String orderStytle) {
        mDialog.mConfirm.setEnabled(false);
        CartList cartList = CartList.newInstance(this);
        cartList.setOpenInfo(new OpenInfo(
                tableNum,
                "",
                "1",
                orderStytle
        ));
        cartList.setOrderNo(new OrderNo(Users.pad + DateTimeUtils.getCurrentDatetime(), false));
        httpCreateWmlsbjb();
    }
}

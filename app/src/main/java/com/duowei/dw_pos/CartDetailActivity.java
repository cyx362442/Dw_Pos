package com.duowei.dw_pos;

import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDialogFragment;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.duowei.dw_pos.adapter.CartDetailItemAdapter;
import com.duowei.dw_pos.constant.ExtraParm;
import com.duowei.dw_pos.event.CartMsgDialogEvent;
import com.duowei.dw_pos.event.CartRemoteUpdateEvent;
import com.duowei.dw_pos.event.CartUpdateEvent;
import com.duowei.dw_pos.event.Commit;
import com.duowei.dw_pos.fragment.LoadingDialogFragment;
import com.duowei.dw_pos.fragment.MessageDialogFragment;
import com.duowei.dw_pos.sunmiprint.Prints;
import com.duowei.dw_pos.tools.CartList;
import com.duowei.dw_pos.tools.DateTimeUtils;
import com.duowei.dw_pos.tools.SqlNetHandler;
import com.duowei.dw_pos.tools.Users;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import woyou.aidlservice.jiuiv5.IWoyouService;

/**
 * 订单详情
 */

public class CartDetailActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView mTitleView;
    private ListView mListView;
    private Button mAddButton;
    private Button mSubmitButton;

    private CartDetailItemAdapter mAdapter;

    private String mWmdbh;
    /** 加载成功 */
    private boolean mLoadSuccess = false;

    private ProgressDialog mCommitDialog;

    public void closeCommitDialog () {
        mSubmitButton.setEnabled(true);

        if (mCommitDialog != null && mCommitDialog.isShowing()) {
            mCommitDialog.dismiss();
        }
    }

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
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart_detail);
        initViews();
        mPrinter = Prints.getPrinter();
        mPrinter.bindPrintService(this, connService);
    }

    @Override
    protected void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
        loadData();
    }

    @Override
    protected void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(connService);
    }

    private void initViews() {
        findViewById(R.id.iv_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        mTitleView = (TextView) findViewById(R.id.tv_title);
        mListView = (ListView) findViewById(R.id.list);
        mAddButton = (Button) findViewById(R.id.btn_add);
        mSubmitButton = (Button) findViewById(R.id.btn_submit);

        mSubmitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.setEnabled(false);
                mCommitDialog = ProgressDialog.show(CartDetailActivity.this, null, "提交中...", true, false);

                if (mWmdbh == null) {
                    // 第一次提交
                    String currentDatetime = DateTimeUtils.getCurrentDatetime();
                    new SqlNetHandler().handleCommit(CartDetailActivity.this, Users.pad + currentDatetime, true);
                } else {
                    // 第二次提交
                    if (mLoadSuccess) {
                        new SqlNetHandler().handleCommit(CartDetailActivity.this, mWmdbh, false);
                    } else {
                        Toast.makeText(CartDetailActivity.this, "从服务器下载数据失败，不能进行提交操作!", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        mAddButton.setOnClickListener(this);
    }

    private void loadData() {
        mWmdbh = getIntent().getStringExtra(ExtraParm.EXTRA_WMDBH);

        mAdapter = new CartDetailItemAdapter(this);
        mListView.setAdapter(mAdapter);

        if (!TextUtils.isEmpty(mWmdbh)) {
            // 从结账界面进来
            mAddButton.setVisibility(View.VISIBLE);

            LoadingDialogFragment fragment = new LoadingDialogFragment();
            fragment.setArguments(getIntent().getExtras());
            fragment.show(getSupportFragmentManager(), null);
            fragment.setListener(new LoadingDialogFragment.OnLoadSuccessListener() {
                @Override
                public void onLoadSuccess() {
                    mLoadSuccess = true;
                    updateData();
                }
            });

        } else {
            updateData();
        }
    }

    @Subscribe
    public void updateUiData(CartUpdateEvent event) {
        updateData();
    }

    @Subscribe
    public void updateRemoteUiDate(CartRemoteUpdateEvent event) {
        loadData();
    }

    @Subscribe
    public void commitSuccessed(Commit event){
        //打印
        mPrinter.setWoyouService(woyouService);
        //第一次下单
        if(event.first){
            mPrinter.print_commit(event.wmlsbjb,mAdapter.getAllList());
        }else{//加单
            mPrinter.print_commit(event.wmlsbjb,event.wmlsbList);
        }
    }

    private void  updateData() {
        mAdapter.clear();
        mAdapter.addRemoteList(CartList.sWMLSBList);
        mAdapter.addLocalList(CartList.newInstance(this).getList());

        String title = "订餐详情";
        if (mAdapter.getTotalPrice() > 0) {
            title += " ¥" + mAdapter.getTotalPrice();
        }
        mTitleView.setText(title);

        if (mAdapter.getLocalNum() > 0) {
            mSubmitButton.setEnabled(true);
        } else {
            mSubmitButton.setEnabled(false);
        }
    }

    @Subscribe
    public void showDialog(CartMsgDialogEvent event) {
        AppCompatDialogFragment fragment = MessageDialogFragment.newInstance(event.title, event.message);
        fragment.show(getSupportFragmentManager(), null);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();

        if (id == R.id.btn_add) {
            Intent intent = new Intent(this, CashierDeskActivity.class);
            intent.putExtras(getIntent().getExtras());
            startActivity(intent);
        }
    }
}

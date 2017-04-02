package com.duowei.dw_pos;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDialogFragment;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.duowei.dw_pos.adapter.CartDetailItemAdapter;
import com.duowei.dw_pos.constant.ExtraParm;
import com.duowei.dw_pos.event.CartMsgDialogEvent;
import com.duowei.dw_pos.event.CartUpdateEvent;
import com.duowei.dw_pos.fragment.LoadingDialogFragment;
import com.duowei.dw_pos.fragment.MessageDialogFragment;
import com.duowei.dw_pos.tools.CartList;
import com.duowei.dw_pos.tools.SqlNetHandler;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

/**
 * 订单详情
 */

public class CartDetailActivity extends AppCompatActivity {

    private TextView mTitleView;
    private ListView mListView;
    private Button mSubmitBtuuon;

    private CartDetailItemAdapter mAdapter;

    private String mWmdbh;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart_detail);
        initViews();
        loadData();
    }

    @Override
    protected void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
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
        mSubmitBtuuon = (Button) findViewById(R.id.btn_submit);

        mSubmitBtuuon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new SqlNetHandler().handleCommit(CartDetailActivity.this, true);
            }
        });
    }

    private void loadData() {
        mWmdbh = getIntent().getStringExtra(ExtraParm.EXTRA_WMDBH);

        mAdapter = new CartDetailItemAdapter(this);
        mListView.setAdapter(mAdapter);

        if (!TextUtils.isEmpty(mWmdbh)) {
            // 从结账界面进来
            LoadingDialogFragment fragment = new LoadingDialogFragment();
            fragment.setArguments(getIntent().getExtras());
            fragment.show(getSupportFragmentManager(), null);
            fragment.setListener(new LoadingDialogFragment.OnLoadSuccessListener() {
                @Override
                public void onLoadSuccess() {
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

    private void updateData() {
        mAdapter.clear();
        mAdapter.addRemoteList(LoadingDialogFragment.sWMLSBList);
        mAdapter.addLocalList(CartList.newInstance(this).getList());

        String title = "订餐详情";
        if (mAdapter.getTotalPrice() > 0) {
            title += " ¥" + mAdapter.getTotalPrice();
        }
        mTitleView.setText(title);

        if (mAdapter.getLocalNum() > 0) {
            mSubmitBtuuon.setEnabled(true);
        } else {
            mSubmitBtuuon.setEnabled(false);
        }
    }

    @Subscribe
    public void showDialog(CartMsgDialogEvent event) {
        AppCompatDialogFragment fragment = MessageDialogFragment.newInstance(event.title, event.message);
        fragment.show(getSupportFragmentManager(), null);
    }
}

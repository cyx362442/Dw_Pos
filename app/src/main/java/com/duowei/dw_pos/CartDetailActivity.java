package com.duowei.dw_pos;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDialogFragment;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import com.duowei.dw_pos.adapter.CartDetailItemAdapter;
import com.duowei.dw_pos.event.CartMsgDialogEvent;
import com.duowei.dw_pos.event.CartUpdateEvent;
import com.duowei.dw_pos.fragment.MessageDialogFragment;
import com.duowei.dw_pos.tools.CartList;
import com.duowei.dw_pos.tools.SqlNetHandler;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

/**
 * 订单详情
 */

public class CartDetailActivity extends AppCompatActivity {

    private ListView mListView;
    private Button mSubmitBtuuon;

    private CartDetailItemAdapter mAdapter;

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

        mListView = (ListView) findViewById(R.id.list);
        mSubmitBtuuon = (Button) findViewById(R.id.btn_submit);

        mSubmitBtuuon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new SqlNetHandler().handleCommit(CartDetailActivity.this);
            }
        });
    }

    private void loadData() {
        mAdapter = new CartDetailItemAdapter(this, CartList.newInstance(this).getList());
        mListView.setAdapter(mAdapter);
    }

    @Subscribe
    public void updateUiData(CartUpdateEvent event) {
        mAdapter.setList(CartList.newInstance(this).getList());

        if (mAdapter.getCount() > 0) {
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

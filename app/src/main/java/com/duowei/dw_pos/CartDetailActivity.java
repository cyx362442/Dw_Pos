package com.duowei.dw_pos;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import com.duowei.dw_pos.adapter.CartDetailItemAdapter;
import com.duowei.dw_pos.tools.CartList;
import com.duowei.dw_pos.tools.SqlNetHandler;

/**
 * 订单详情
 */

public class CartDetailActivity extends AppCompatActivity {

    private ListView mListView;
    private Button mSubmitBtuuon;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart_detail);
        initViews();
        loadData();
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
//                Toast.makeText(CartDetailActivity.this, "Clicked", Toast.LENGTH_SHORT).show();
                new SqlNetHandler().handleCommit(CartDetailActivity.this);
            }
        });
    }

    private void loadData() {
        CartDetailItemAdapter adapter = new CartDetailItemAdapter(this, CartList.newInstance().getList());
        mListView.setAdapter(adapter);
    }
}

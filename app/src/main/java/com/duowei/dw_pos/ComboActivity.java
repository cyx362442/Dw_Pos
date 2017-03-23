package com.duowei.dw_pos;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

/**
 * 套餐子项 界面
 */

public class ComboActivity extends AppCompatActivity {

    private TextView mComboNameView;
    private TextView mComboMoneyView;

    private ListView mListView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_combo);
        initViews();
    }

    private void initViews() {
        findViewById(R.id.iv_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        mComboNameView = (TextView) findViewById(R.id.tv_combo_name);
        mComboMoneyView = (TextView) findViewById(R.id.tv_combo_money);
        mListView = (ListView) findViewById(R.id.list);
    }
}

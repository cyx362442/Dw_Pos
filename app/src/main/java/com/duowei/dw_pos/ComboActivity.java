package com.duowei.dw_pos;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.duowei.dw_pos.adapter.ComboAdapter;
import com.duowei.dw_pos.bean.TCSD;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * 套餐子项 界面
 */

public class ComboActivity extends AppCompatActivity {

    private TextView mComboNameView;
    private TextView mComboMoneyView;
    private Button mOkButton;

    private ListView mListView;

    private String mXmbh;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_combo);
        initViews();
        initData();
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
        mOkButton = (Button) findViewById(R.id.btn_ok);
    }

    private void initData() {
        Intent intent = getIntent();
        if (intent != null) {
            mXmbh = intent.getExtras().getString("xmbh", "");
//            mXmbh = "ZT779"; // 测试
        }

        if (TextUtils.isEmpty(mXmbh)) {
            Toast.makeText(this, "xmbh空", Toast.LENGTH_SHORT).show();
            return;
        }

        List<TCSD> oneTcsdList = DataSupport.where("xmbh = ? and tm = ?", mXmbh, "A").find(TCSD.class);
        if (oneTcsdList.size() == 1) {
            TCSD tcsd = oneTcsdList.get(0);
            mComboNameView.setText(tcsd.getXMMC1());
            mComboMoneyView.setText(tcsd.getDJ());
        } else {
            Toast.makeText(this, "oneTcsdList.size() != 1", Toast.LENGTH_SHORT).show();
        }


        Cursor cursor = DataSupport.findBySQL("select distinct tm from tcsd where xmbh = '" + mXmbh + "' and tm != 'A'");
        List<String> tmList = new ArrayList<>();
        while (cursor.moveToNext()) {
            tmList.add(cursor.getString(cursor.getColumnIndex("tm")));
        }

        LinkedHashMap<String, List<TCSD>> map = new LinkedHashMap<>();
        for (int i = 0; i < tmList.size(); i++) {
            map.put(tmList.get(i), DataSupport.where("xmbh = ? and tm = ?", mXmbh, tmList.get(i)).find(TCSD.class));
        }

        ComboAdapter adapter = new ComboAdapter(this, map, mOkButton);
        mListView.setAdapter(adapter);
    }
}

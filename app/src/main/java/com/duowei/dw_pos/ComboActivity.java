package com.duowei.dw_pos;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.duowei.dw_pos.adapter.ComboAdapter;
import com.duowei.dw_pos.bean.AddTcsdItem;
import com.duowei.dw_pos.bean.TCSD;
import com.duowei.dw_pos.tools.CartList;
import com.duowei.dw_pos.tools.DateTimeUtils;

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
    private LinearLayout mTasteMainLayout;
    private RecyclerView mTasteRecyclerView;
    private Button mOkButton;

    private ListView mListView;

    private String mXmbh;

    private TCSD mMainTcsd;
    private LinkedHashMap<String, List<TCSD>> mSubTcsdMap;

    private float mTotalMainPrice;

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
        mTasteMainLayout = (LinearLayout) findViewById(R.id.ll_taste_main_layout);
        mTasteRecyclerView = (RecyclerView) findViewById(R.id.rv_taste);
        mTasteRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        mListView = (ListView) findViewById(R.id.list);
        mOkButton = (Button) findViewById(R.id.btn_ok);

        mOkButton.setOnClickListener(mOkButtonClickListener);
    }

    private void initData() {
        Intent intent = getIntent();
        if (intent != null) {
            Bundle args = intent.getExtras();
            if (args != null) {
                mXmbh = args.getString("xmbh", "");
            } else {
                mXmbh = "ZT931"; // 测试
            }
        }

        if (TextUtils.isEmpty(mXmbh)) {
            Toast.makeText(this, "xmbh空", Toast.LENGTH_SHORT).show();
            return;
        }

        List<TCSD> oneTcsdList = DataSupport.where("xmbh = ? and tm = ? and gq = ?", mXmbh, "A", "1").find(TCSD.class);
        if (oneTcsdList.size() == 1) {
            mMainTcsd = oneTcsdList.get(0);
            mComboNameView.setText(mMainTcsd.getXMMC1());
            mTotalMainPrice = mMainTcsd.getDJ();
            mComboMoneyView.setText("¥" + mTotalMainPrice);
        } else {
            Toast.makeText(this, "oneTcsdList.size() != 1", Toast.LENGTH_SHORT).show();
        }


        Cursor cursor = DataSupport.findBySQL("select distinct tm from tcsd where xmbh = '" + mXmbh + "' and tm != 'A' order by tm");
        List<String> tmList = new ArrayList<>();
        while (cursor.moveToNext()) {
            tmList.add(cursor.getString(cursor.getColumnIndex("tm")));
        }

        mSubTcsdMap = new LinkedHashMap<>();
        for (int i = 0; i < tmList.size(); i++) {
            mSubTcsdMap.put(tmList.get(i), DataSupport.where("xmbh = ? and tm = ?", mXmbh, tmList.get(i)).find(TCSD.class));
        }

        ComboAdapter adapter = new ComboAdapter(this, mSubTcsdMap, mOkButton);
        mListView.setAdapter(adapter);

        adapter.showTaste(mMainTcsd, mTasteMainLayout, mTasteRecyclerView);
    }

    public void setTotalPrice(float totalSubPrice) {
        mComboMoneyView.setText("¥" + (mTotalMainPrice + totalSubPrice));
    }

    private View.OnClickListener mOkButtonClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            // 添加选择好的套餐子项
            if (mMainTcsd != null) {
                if (mSubTcsdMap != null && mSubTcsdMap.size() > 0) {
                    String datetime = DateTimeUtils.getCurrentDatetime();

                    ArrayList<AddTcsdItem> addTcsdItemArrayList = new ArrayList<>();
                    addTcsdItemArrayList.add(new AddTcsdItem(mMainTcsd, "1", datetime, mMainTcsd.PZ));

                    String[] tmArray = mSubTcsdMap.keySet().toArray(new String[mSubTcsdMap.size()]);
                    for (int i = 0; i < mSubTcsdMap.size(); i++) {
                        List<TCSD> tcsdList = mSubTcsdMap.get(tmArray[i]);
                        for (int j = 0; j < tcsdList.size(); j++) {
                            TCSD tcsd = tcsdList.get(j);

                            if ("1".equals(tcsd.SFXZ)) {
                                addTcsdItemArrayList.add(new AddTcsdItem(tcsd, "0", datetime, tcsd.PZ));
                                break;
                            }
                        }
                    }

                    CartList.newInstance(ComboActivity.this).add(addTcsdItemArrayList);

                    finish();
                } else {
                    Toast.makeText(ComboActivity.this, "没有一个套餐子项", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(ComboActivity.this, "没有套餐主项", Toast.LENGTH_SHORT).show();
            }
        }
    };
}

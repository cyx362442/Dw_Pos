package com.duowei.dw_pos;

import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;

import com.duowei.dw_pos.adapter.LeftAdapter;
import com.duowei.dw_pos.adapter.RightAdapter;
import com.duowei.dw_pos.bean.DMJYXMSSLB;
import com.duowei.dw_pos.bean.JYXMSZ;
import com.duowei.dw_pos.bean.TCMC;
import com.duowei.dw_pos.fragment.CartFragment;
import com.duowei.dw_pos.view.ToggleButton;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.List;

import pl.com.salsoft.sqlitestudioremote.SQLiteStudioService;

/**
 * 点餐界面
 */

public class CashierDeskActivity extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemClickListener {

    private ImageView mBackView;
    private ImageView mSearchView;
    private EditText mEditText;
    private ToggleButton mToggleButton;

    private ListView mLeftListView;
    private ListView mRightListView;

    private LeftAdapter mLeftAdapter;
    private RightAdapter mRightAdapter;

    private CartFragment mCartFragment;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SQLiteStudioService.instance().start(this);
        setContentView(R.layout.activity_cashier_desk);
        initViews();
        initData();
    }

    @Override
    protected void onDestroy() {
        SQLiteStudioService.instance().stop();
        super.onDestroy();
    }

    private void initViews() {
        mBackView = (ImageView) findViewById(R.id.iv_back);
        mSearchView = (ImageView) findViewById(R.id.iv_search);
        mEditText = (EditText) findViewById(R.id.edit_query);
        mLeftListView = (ListView) findViewById(R.id.left_list);
        mRightListView = (ListView) findViewById(R.id.right_list);
        mToggleButton = (ToggleButton) findViewById(R.id.btn_toggle);

        mLeftListView.setEmptyView(findViewById(R.id.left_empty));
        mRightListView.setEmptyView(findViewById(R.id.right_empty));

        mLeftListView.setOnItemClickListener(this);

        mBackView.setOnClickListener(this);
        mSearchView.setOnClickListener(this);

        mToggleButton.setOnClickListener(this);
        mToggleButton.setToggleListener(new ToggleButton.OnToggleListener() {
            @Override
            public void onToggle(ToggleButton.ButtonType type) {
                if (type == ToggleButton.ButtonType.TYPE_1) {
                    // 切换到 单品
                    setupData();
                } else {
                    // 切换到 套餐
                    setupData2();
                }
            }
        });

        mCartFragment = new CartFragment();
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, mCartFragment)
                .commit();
    }

    private void initData() {
        mLeftAdapter = new LeftAdapter(this);
        mLeftListView.setAdapter(mLeftAdapter);

        mRightAdapter = new RightAdapter(this);
        mRightListView.setAdapter(mRightAdapter);

        setupData();
    }

    /**
     * 设置单品数据
     */
    private void setupData() {
        mLeftAdapter.setList(getDmjyxmsslbList());
        mLeftListView.setItemChecked(0, true);
        mLeftListView.smoothScrollToPosition(0);

        int checkedPosition = mLeftListView.getCheckedItemPosition();
        if (checkedPosition != ListView.INVALID_POSITION) {
            mRightAdapter.setList(getJyxmszList(((DMJYXMSSLB) mLeftAdapter.getItem(checkedPosition)).getLBBM()));
        }
    }

    /**
     * 设置套餐数据
     */
    private void setupData2() {
        mLeftAdapter.setList(getTcmc1List());
        mLeftListView.setItemChecked(0, true);
        mLeftListView.smoothScrollToPosition(0);

        int checkedPosition = mLeftListView.getCheckedItemPosition();
        if (checkedPosition != ListView.INVALID_POSITION) {
            mRightAdapter.setList(getTcmc2List(((String) mLeftAdapter.getItem(checkedPosition))));
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();

        if (id == R.id.iv_back) {
//            finish();
            getSupportFragmentManager().beginTransaction().hide(mCartFragment).commit();

        } else if (id == R.id.iv_search) {
            getSupportFragmentManager().beginTransaction().show(mCartFragment).commit();

        } else if (id == R.id.btn_toggle) {
            mToggleButton.toggle();
        }
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Object object = mLeftAdapter.getItem(position);

        if (object instanceof DMJYXMSSLB) {
            // 单品项点击
            DMJYXMSSLB item = (DMJYXMSSLB) object;
            mRightAdapter.setList(getJyxmszList(item.getLBBM()));

        } else if (object instanceof String) {
            // 套餐项 点击
            String item = (String) object;
            mRightAdapter.setList(getTcmc2List(item));
        }
    }

    /**
     * @return 单品分类 列表
     */
    private List<DMJYXMSSLB> getDmjyxmsslbList() {
        return DataSupport.where("sfty != ? and lbbm != ?", "1", "RICH").order("xl").find(DMJYXMSSLB.class);
    }

    /**
     * @param lbbm
     * @return 单品信息 列表
     */
    private List<JYXMSZ> getJyxmszList(String lbbm) {
        return DataSupport.where("lbbm = ?", lbbm).find(JYXMSZ.class);
    }

    /**
     * @return 套餐分类 (lbmc)列表
     */
    private List<String> getTcmc1List() {
        List<String> stringList = new ArrayList<>();

        Cursor cursor = DataSupport.findBySQL("select distinct lbmc from tcmc order by lbmc");
        while (cursor.moveToNext()) {
            String lbmc = cursor.getString(cursor.getColumnIndex("lbmc"));
            stringList.add(lbmc);
        }
        cursor.close();

        return stringList;
    }


    /**
     * @param lbmc 点击的套餐分类名称
     * @return 套餐列表
     */
    private List<TCMC> getTcmc2List(String lbmc) {
        return DataSupport.where("lbmc == ?", lbmc).order("xl").find(TCMC.class);
    }

}

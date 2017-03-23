package com.duowei.dw_pos;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.duowei.dw_pos.adapter.LeftAdapter;
import com.duowei.dw_pos.adapter.RightAdapter;
import com.duowei.dw_pos.bean.DMJYXMSSLB;
import com.duowei.dw_pos.bean.JYXMSZ;

import org.litepal.crud.DataSupport;

import java.util.List;

import pl.com.salsoft.sqlitestudioremote.SQLiteStudioService;

/**
 * 点餐界面
 */

public class CashierDeskActivity extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemClickListener {

    private ImageView mBackView;
    private ImageView mSearchView;
    private EditText mEditText;

    private ListView mLeftListView;
    private ListView mRightListView;

    private LeftAdapter mLeftAdapter;
    private RightAdapter mRightAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SQLiteStudioService.instance().start(this);
        setContentView(R.layout.activity_cashier_desk);
        setupViews();
        setupData();
    }

    @Override
    protected void onDestroy() {
        SQLiteStudioService.instance().stop();
        super.onDestroy();
    }

    private void setupViews() {
        mBackView = (ImageView) findViewById(R.id.iv_back);
        mSearchView = (ImageView) findViewById(R.id.iv_search);
        mEditText = (EditText) findViewById(R.id.edit_query);
        mLeftListView = (ListView) findViewById(R.id.left_list);
        mRightListView = (ListView) findViewById(R.id.right_list);

        mLeftListView.setEmptyView(findViewById(R.id.left_empty));
        mRightListView.setEmptyView(findViewById(R.id.right_empty));

        mLeftListView.setOnItemClickListener(this);

        mBackView.setOnClickListener(this);
        mSearchView.setOnClickListener(this);
    }

    private void setupData() {
        mLeftAdapter = new LeftAdapter(this, getDmjyxmsslbList());
        mLeftListView.setAdapter(mLeftAdapter);
        mLeftListView.setItemChecked(0, true);

        mRightAdapter = new RightAdapter(this);
        mRightListView.setAdapter(mRightAdapter);

        int checkedPosition = mLeftListView.getCheckedItemPosition();
        if (checkedPosition != ListView.INVALID_POSITION) {
            mRightAdapter.setList(getJyxmszList(((DMJYXMSSLB) mLeftAdapter.getItem(checkedPosition)).getLBBM()));
            mRightAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();

        if (id == R.id.iv_back) {
            finish();
        } else if (id == R.id.iv_search) {
            Toast.makeText(this, "clicked", Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Object object = mLeftAdapter.getItem(position);

        if (object instanceof DMJYXMSSLB) {
            // 单品项点击
            DMJYXMSSLB item = (DMJYXMSSLB) object;
            mRightAdapter.setList(getJyxmszList(item.getLBBM()));
            mRightAdapter.notifyDataSetChanged();
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

}

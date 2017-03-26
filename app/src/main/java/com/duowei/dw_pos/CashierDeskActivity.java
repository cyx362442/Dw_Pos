package com.duowei.dw_pos;

import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;

import com.duowei.dw_pos.adapter.LeftAdapter;
import com.duowei.dw_pos.adapter.RightAdapter;
import com.duowei.dw_pos.bean.DMJYXMSSLB;
import com.duowei.dw_pos.bean.JYXMSZ;
import com.duowei.dw_pos.bean.TCMC;
import com.duowei.dw_pos.fragment.CartFragment;
import com.duowei.dw_pos.tools.AnimUtils;
import com.duowei.dw_pos.tools.CartList;
import com.duowei.dw_pos.tools.DateTimeUtils;
import com.duowei.dw_pos.view.ToggleButton;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.List;

import pl.com.salsoft.sqlitestudioremote.SQLiteStudioService;

/**
 * 点餐界面
 */

public class CashierDeskActivity extends AppCompatActivity implements View.OnClickListener,
        AdapterView.OnItemClickListener {
    private static final String TAG = "CashierDeskActivity";

    private ImageView mBackView;
    private ImageView mSearchView;
    private EditText mEditText;
    private ToggleButton mToggleButton;

    private ListView mLeftListView;
    private ListView mRightListView;

    private LeftAdapter mLeftAdapter;
    private RightAdapter mRightAdapter;

    private List<JYXMSZ> mRightJyxmszAllList;
    private List<TCMC> mRightTcmcAllList;

    private CartFragment mCartFragment;

    private TextWatcher mTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            mRightAdapter.getFilter().filter(s);
        }

        @Override
        public void afterTextChanged(Editable s) {
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SQLiteStudioService.instance().start(this);
        setContentView(R.layout.activity_cashier_desk);
        mRightJyxmszAllList = getJyxmszAllList();
        mRightTcmcAllList = getTcmcAllList();

        initViews();
        initData();
        clearEditText();
        // 清空购物车
        CartList.newInstance().clear();



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

        final AnimUtils animUtils = AnimUtils.getInstance(this);
        final ImageView img_cart = (ImageView) findViewById(R.id.img_cart);
        final FrameLayout animLayout = createAnimLayout();
        mRightAdapter.setOnSetHolderClickListener(new RightAdapter.HolderClickListener() {
            @Override
            public void onHolderClick(Drawable drawable, int[] start_location) {
                animUtils.setPX(50);
                animUtils.doAnim(animLayout, img_cart, drawable, start_location);
            }
        });

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
            mRightAdapter.setList(getJyxmszList(((DMJYXMSSLB) mLeftAdapter.getItem(checkedPosition)).getLBBM()), mRightJyxmszAllList);
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
            mRightAdapter.setList(getTcmc2List(((String) mLeftAdapter.getItem(checkedPosition))), mRightTcmcAllList);
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();

        if (id == R.id.iv_back) {
            finish();

        } else if (id == R.id.iv_search) {
            //

        } else if (id == R.id.btn_toggle) {
            mToggleButton.toggle();
            clearEditText();
        }
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        clearEditText();
        Object object = mLeftAdapter.getItem(position);

        if (object instanceof DMJYXMSSLB) {
            // 单品项点击
            DMJYXMSSLB item = (DMJYXMSSLB) object;
            Log.d(TAG, "onItemClick: before " + DateTimeUtils.getCurrentDatetime());
            mRightAdapter.setList(getJyxmszList(item.getLBBM()), mRightJyxmszAllList);
            Log.d(TAG, "onItemClick: after " + DateTimeUtils.getCurrentDatetime());

        } else if (object instanceof String) {
            // 套餐项 点击
            String item = (String) object;
            mRightAdapter.setList(getTcmc2List(item), mRightTcmcAllList);
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
//        ArrayList<JYXMSZ> list = new ArrayList<>();
//        for (int i = 0; i < mRightJyxmszAllList.size(); i++) {
//            JYXMSZ jyxmsz = mRightJyxmszAllList.get(i);
//            if (lbbm.endsWith(jyxmsz.getLBBM())) {
//                list.add(jyxmsz);
//            }
//        }
//        return list;
    }

    /**
     * @return 总的单品信息 列表
     */
    private List<JYXMSZ> getJyxmszAllList() {
        return DataSupport.findAll(JYXMSZ.class);
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

    /**
     * @return 总的套餐列表
     */
    private List<TCMC> getTcmcAllList() {
        return DataSupport.findAll(TCMC.class);
    }

    private void clearEditText() {
        mEditText.removeTextChangedListener(mTextWatcher);
        mEditText.setText(null);
        mEditText.addTextChangedListener(mTextWatcher);
    }

    /**
     * @param
     * @return void
     * @throws
     * @Description: 创建动画层
     */
    private FrameLayout createAnimLayout() {
        ViewGroup rootView = (ViewGroup) this.getWindow().getDecorView();
        FrameLayout animLayout = new FrameLayout(this);
        FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);
        animLayout.setLayoutParams(lp);
        animLayout.setBackgroundResource(android.R.color.transparent);
        rootView.addView(animLayout);
        return animLayout;
    }
}

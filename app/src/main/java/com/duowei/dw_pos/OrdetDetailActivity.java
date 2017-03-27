package com.duowei.dw_pos;

import android.net.http.LoggingEventHandler;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.duowei.dw_pos.adapter.OrderListAdapter;
import com.duowei.dw_pos.bean.WMLSB;
import com.duowei.dw_pos.dialog.SalesReturnDialog;
import com.duowei.dw_pos.event.CartUpdateEvent;
import com.duowei.dw_pos.httputils.DownHTTP;
import com.duowei.dw_pos.httputils.Post7;
import com.duowei.dw_pos.httputils.VolleyResultListener;
import com.duowei.dw_pos.tools.CartList;
import com.duowei.dw_pos.tools.Net;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class OrdetDetailActivity extends AppCompatActivity {

    @BindView(R.id.img_back)
    ImageView mImgBack;
    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.lv_order)
    ListView mLvOrder;
    @BindView(R.id.btn_order_confirm)
    Button mBtnOrderConfirm;
    private ArrayList<WMLSB> mListWmlsb;
    private OrderListAdapter mAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ordet_detail);
        ButterKnife.bind(this);
        mListWmlsb = (ArrayList<WMLSB>) getIntent().getSerializableExtra("listWmlsb");
        CartList.newInstance().setList(mListWmlsb);
    }

    @Override
    protected void onStart() {
        super.onStart();
        mAdapter = new OrderListAdapter(this, mListWmlsb);
        mLvOrder.setAdapter(mAdapter);
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe
    public void updateUiData(CartUpdateEvent event) {
        mAdapter.setList(CartList.newInstance().getList());
        final String sql = CartList.newInstance().getSql();
        if(!TextUtils.isEmpty(sql)){
            final SalesReturnDialog dialog = new SalesReturnDialog(this);
            dialog.mConfirm.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String result = Post7.getInstance().getHttpResult(sql);
                }
            });
        }
    }

    @OnClick({R.id.img_back, R.id.btn_order_confirm})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.img_back:
                finish();
                break;
            case R.id.btn_order_confirm:
                break;
        }
    }
}

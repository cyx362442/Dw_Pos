package com.duowei.dw_pos;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.duowei.dw_pos.adapter.OrderListAdapter;
import com.duowei.dw_pos.bean.WMLSB;
import com.duowei.dw_pos.dialog.SalesReturnDialog;
import com.duowei.dw_pos.event.CartUpdateEvent;
import com.duowei.dw_pos.event.OrderUpdateEvent;
import com.duowei.dw_pos.tools.CartList;

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
    public void updateOrderData(OrderUpdateEvent event){
        Toast.makeText(this,event.msg,Toast.LENGTH_LONG).show();
        if(event.msg.contains("richado")){
            mAdapter.setList(CartList.newInstance().getList());
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

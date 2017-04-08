package com.duowei.dw_pos;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.duowei.dw_pos.bean.OpenInfo;
import com.duowei.dw_pos.dialog.CustomerDialog;
import com.duowei.dw_pos.event.CustomerStytle;
import com.duowei.dw_pos.tools.CartList;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class OpenTableActivity extends AppCompatActivity {

    @BindView(R.id.tv_title)
    TextView mTvTitle;
    @BindView(R.id.rlTop)
    RelativeLayout mRlTop;
    @BindView(R.id.editText2)
    EditText mEditText2;
    @BindView(R.id.editText3)
    EditText mEditText3;
    @BindView(R.id.btn_cancel)
    Button mBtnCancel;
    @BindView(R.id.btn_confirm)
    Button mBtnConfirm;
    String csmc;
    @BindView(R.id.imgCustomer)
    ImageView mImgCustomer;
    @BindView(R.id.tv_stytle)
    TextView mTvStytle;
    private String customerStytle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_open_table);
        ButterKnife.bind(this);
        EventBus.getDefault().register(this);
        CartList.newInstance(this).clear();
    }

    @Subscribe
    public void customerStytle(CustomerStytle event) {
        customerStytle = event.stytle;
        mTvStytle.setText(customerStytle);
    }

    @Override
    protected void onStart() {
        super.onStart();
        csmc = getIntent().getStringExtra("csmc");
        mTvTitle.setText("开台—" + csmc);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @OnClick({R.id.imgCustomer, R.id.btn_cancel, R.id.btn_confirm})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.imgCustomer:
                CustomerDialog dialog = new CustomerDialog(this);
                break;
            case R.id.btn_cancel:
                finish();
                break;
            case R.id.btn_confirm:
                CartList.newInstance(this).setOpenInfo(new OpenInfo(
                        csmc,
                        customerStytle,
                        mEditText2.getText().toString(),
                        mEditText3.getText().toString()
                ));

                Intent intent = new Intent(this, CashierDeskActivity.class);
                startActivity(intent);
                finish();
                break;
        }
    }
}

package com.duowei.dw_pos;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.duowei.dw_pos.bean.OpenInfo;
import com.duowei.dw_pos.bean.OrderNo;
import com.duowei.dw_pos.bean.WMLSBJB;
import com.duowei.dw_pos.dialog.CustomerDialog;
import com.duowei.dw_pos.event.CustomerStytle;
import com.duowei.dw_pos.httputils.NetUtils;
import com.duowei.dw_pos.tools.CartList;
import com.duowei.dw_pos.tools.DateTimeUtils;
import com.duowei.dw_pos.tools.Net;
import com.duowei.dw_pos.tools.Users;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

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
    @BindView(R.id.tv_stytle)
    TextView mTvStytle;
    @BindView(R.id.llcustomer)
    LinearLayout mLlcustomer;
    private String customerStytle="";

    private Handler mHandler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_open_table);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        ButterKnife.bind(this);
        EventBus.getDefault().register(this);
        CartList.newInstance(this).clear();

        mEditText2.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    mEditText2.requestFocus();
                }
            }
        });
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

    @OnClick({R.id.llcustomer, R.id.btn_cancel, R.id.btn_confirm})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.llcustomer:
                CustomerDialog dialog = new CustomerDialog(this);
                break;
            case R.id.btn_cancel:
                finish();
                break;
            case R.id.btn_confirm:
                String persons = mEditText2.getText().toString();
                if (TextUtils.isEmpty(persons)) {
                    Toast.makeText(this, "请输入就餐人数", Toast.LENGTH_SHORT).show();
                    return;
                }

                CartList cartList = CartList.newInstance(this);
                cartList.setOpenInfo(new OpenInfo(
                        csmc,
                        customerStytle,
                        persons,
                        mEditText3.getText().toString()
                ));

                cartList.setOrderNo(new OrderNo(Users.pad + DateTimeUtils.getCurrentDatetime(), false));

                httpCreateWmlsbjb();

                Intent intent = new Intent(this, CashierDeskActivity.class);
                startActivity(intent);
                finish();
                break;
        }
    }

    @OnClick(R.id.llcustomer)
    public void onViewClicked() {
    }

    private void httpCreateWmlsbjb() {
        OpenInfo openInfo = CartList.newInstance(this).getOpenInfo();
        OrderNo orderNo = CartList.newInstance(this).getOrderNo();

        final WMLSBJB wmlsbjb = new WMLSBJB(
                orderNo.getWmdbh(),
                Users.YHMC,
                openInfo.getDeskNo(),
                "0", // 是否已结账
                Users.pad,
                openInfo.getPeopleNum(),
                0,
                "1",
                openInfo.getPeopleType(),
                openInfo.getRemark()
        );
        NetUtils.post7(Net.url, wmlsbjb.toInsertString(), new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String result = response.body().string();
                if (result.equals("]")) {
                    return;
                }

                if (result.contains("richado")) {
                    CartList.sWMLSBJB = wmlsbjb;
                }
            }
        });
    }
}

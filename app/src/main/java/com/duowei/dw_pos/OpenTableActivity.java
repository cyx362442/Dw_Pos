package com.duowei.dw_pos;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class OpenTableActivity extends AppCompatActivity {

    @BindView(R.id.tv_title)
    TextView mTvTitle;
    @BindView(R.id.rlTop)
    RelativeLayout mRlTop;
    @BindView(R.id.editText1)
    EditText mEditText1;
    @BindView(R.id.editText2)
    EditText mEditText2;
    @BindView(R.id.editText3)
    EditText mEditText3;
    @BindView(R.id.btn_cancel)
    Button mBtnCancel;
    @BindView(R.id.btn_confirm)
    Button mBtnConfirm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_open_table);
        ButterKnife.bind(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        String csmc = getIntent().getStringExtra("csmc");
        mTvTitle.setText("开台—"+csmc);
    }

    @OnClick({R.id.btn_cancel, R.id.btn_confirm})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_cancel:
                finish();
                break;
            case R.id.btn_confirm:
                Intent intent = new Intent(this, CashierDeskActivity.class);
                startActivity(intent);
                finish();
                break;
        }
    }
}

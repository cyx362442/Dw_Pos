package com.duowei.dw_pos;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.duowei.dw_pos.summiscan.ScanActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class YunLandActivity extends AppCompatActivity {

    @BindView(R.id.et_phone)
    EditText mEtPhone;
    @BindView(R.id.et_password)
    EditText mEtPassword;
    @BindView(R.id.btn_cancel)
    Button mBtnCancel;
    @BindView(R.id.btn_confirm)
    Button mBtnConfirm;
    @BindView(R.id.btn_shama)
    Button mBtnShama;

    private final int REQUESTCODE=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_yun_land);
        ButterKnife.bind(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==REQUESTCODE){
            String result = data.getStringExtra("result").trim();
            if(result.length()<=0||!result.contains(",")){
                Toast.makeText(YunLandActivity.this,"扫描失败，请重试",Toast.LENGTH_SHORT).show();
            }else{
                mEtPhone.setText(result.substring(0,result.indexOf(",")));
                mEtPassword.setText(result.substring(result.indexOf(",")+1,result.length()).trim());
            }
        }
    }

    @OnClick({R.id.btn_cancel, R.id.btn_confirm, R.id.btn_shama})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_cancel:
                finish();
                break;
            case R.id.btn_confirm:
                break;
            case R.id.btn_shama:
                Intent intent = new Intent(this, ScanActivity.class);
                startActivityForResult(intent,REQUESTCODE);
                break;
        }
    }
}

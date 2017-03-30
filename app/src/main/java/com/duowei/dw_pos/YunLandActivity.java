package com.duowei.dw_pos;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.duowei.dw_pos.bean.WXFWQDZ;
import com.duowei.dw_pos.event.CartUpdateEvent;
import com.duowei.dw_pos.event.ImsCardMembers;
import com.duowei.dw_pos.httputils.Post6;
import com.duowei.dw_pos.summiscan.ScanActivity;
import com.duowei.dw_pos.tools.CartList;
import com.duowei.dw_pos.tools.Net;
import com.google.common.eventbus.EventBus;

import org.greenrobot.eventbus.Subscribe;
import org.litepal.crud.DataSupport;

import java.util.List;

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

    private String mPhone;
    private String mPassword;
    private String mWeid;
    private Post6 mPost6;
    private String mSip;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_yun_land);
        ButterKnife.bind(this);
        mPost6 = Post6.getInstance();
        List<WXFWQDZ> list = DataSupport.select("weid","SIP").find(WXFWQDZ.class);
        mWeid = list.get(0).getWeid();
        mSip = list.get(0).getSIP();
        Net.yunUrl="http://"+mSip+":2233/serverandroid/ServerSvlt?";
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==REQUESTCODE){
            String result = data.getStringExtra("result").trim();
            if(result.length()<=0||!result.contains(",")){
                Toast.makeText(YunLandActivity.this,"扫描失败，请重试",Toast.LENGTH_SHORT).show();
            }else{
                mEtPhone.setText(mPhone=result.substring(0,result.indexOf(",")));
                mEtPassword.setText(mPassword=result.substring(result.indexOf(",")+1,result.length()).trim());
                mPost6.post_ims_card_members(mPhone,mPassword,mWeid);//云会员登录
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        org.greenrobot.eventbus.EventBus.getDefault().register(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
       org.greenrobot.eventbus.EventBus.getDefault().unregister(this);
    }

    @Subscribe
    public void updateUiData(ImsCardMembers event) {

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

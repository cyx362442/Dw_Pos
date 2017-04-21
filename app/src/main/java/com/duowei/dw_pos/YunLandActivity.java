package com.duowei.dw_pos;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.duowei.dw_pos.bean.FXHYKSZ;
import com.duowei.dw_pos.bean.ImsCardMember;
import com.duowei.dw_pos.bean.Moneys;
import com.duowei.dw_pos.bean.WMLSB;
import com.duowei.dw_pos.bean.WXFWQDZ;
import com.duowei.dw_pos.event.ImsCardMembers;
import com.duowei.dw_pos.httputils.Post6;
import com.duowei.dw_pos.summiscan.ScanActivity;
import com.duowei.dw_pos.tools.Net;
import com.google.gson.Gson;

import org.greenrobot.eventbus.Subscribe;
import org.litepal.crud.DataSupport;

import java.io.Serializable;
import java.util.ArrayList;
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
    private int mWeid;
    private Post6 mPost6;
    private String mSip;
    private ArrayList<WMLSB> mListWmlsb;
    private Serializable mWmlsbjb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_yun_land);
        ButterKnife.bind(this);
        mPost6 = Post6.getInstance();
        mListWmlsb = (ArrayList<WMLSB>) getIntent().getSerializableExtra("listWmlsb");
        mWmlsbjb = getIntent().getSerializableExtra("WMLSBJB");
        getYunData();
    }

    private void getYunData() {
        List<WXFWQDZ> list = DataSupport.select("weid","SIP").find(WXFWQDZ.class);
        mWeid = list.get(0).getWeid();
        mSip = list.get(0).getSIP();
        Net.yunUrl="http://"+mSip+":2233/serverandroid/ServerSvlt?";
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==REQUESTCODE&&data!=null){
            String result = data.getStringExtra("result").trim();
            if(result.length()<=0||!result.contains(",")){
                Toast.makeText(YunLandActivity.this,"扫描失败，请重试",Toast.LENGTH_SHORT).show();
            }else{
                mEtPhone.setText(mPhone=result.substring(0,result.indexOf(",")));
                mEtPassword.setText(mPassword=result.substring(result.indexOf(",")+1,result.length()).trim());
                mPost6.post_ims_card_members(mPhone,mPassword,mWeid);//发送post请求云会员登录
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
    /**EventBus提起Post请求返回的云会员等级信息*/
    @Subscribe
    public void getImsCardLand(ImsCardMembers event) {
        String zkfs="";
        if(event.response.equals("]")||event.response.equals("")||event.response.equals("error")){
            Toast.makeText(this,"登录失败",Toast.LENGTH_SHORT).show();
        }else{
            Gson gson = new Gson();
            ImsCardMember[] cards = gson.fromJson(event.response, ImsCardMember[].class);
            String cardgrade = cards[0].getCardgrade();
            List<FXHYKSZ> list = DataSupport.select("ZKFS").where("HYKDJ=?",cardgrade).find(FXHYKSZ.class);
            if(list.size()>0){
                zkfs = list.get(0).getZKFS();
            }
            String hyj=zkfs.equals("会员价1")?"hyj":zkfs.equals("会员价2")?"hyj2":zkfs.equals("会员价3")?"hyj3":zkfs.equals("会员价4")?"hyj4":
                    zkfs.equals("会员价5")?"hyj5":zkfs.equals("会员价6")?"hyj6":zkfs.equals("会员价7")?"hyj7":zkfs.equals("会员价8")?"hyj8":zkfs.equals("会员价9")?"hyj9":"";
            /**重新计算打折后的会员价*/
            float totalMoney=0f;
                //遍历每一项的会员价
            for(WMLSB wmlsb:mListWmlsb){
                if(!TextUtils.isEmpty(hyj)){//有设置了会员价
                    float hyPrice = getHyPrice(hyj, wmlsb.getXMBH());
                    wmlsb.setDJ(hyPrice>0&&wmlsb.getDJ()>hyPrice?hyPrice:wmlsb.getDJ());//未打折，按新的会员价重新计算单价.己打过折扣，还是按原来打折后的单价算;
                    wmlsb.setXJ(wmlsb.getDJ()*wmlsb.getSL());//重算小计金额
                    totalMoney=totalMoney+wmlsb.getXJ();//重算总金额
                }else{
                    totalMoney=totalMoney+wmlsb.getXJ();//重算总金额
                }
            }
            /**改变应收金额、折扣金额*/
            Moneys.ysjr=totalMoney;
            Moneys.zkjr=Moneys.xfzr-Moneys.ysjr;
            Moneys.wfjr=Moneys.ysjr;
            Intent intent = new Intent(this, YunPayActivity.class);
            intent.putExtra("WMLSBJB",mWmlsbjb);
            intent.putExtra("WMLSB",mListWmlsb);
            intent.putExtra("cards",cards[0]);
            startActivity(intent);
            finish();
        }
    }

    private float getHyPrice(String hyj, String xmbh) {
        float hyPrice=0f;
        Cursor cursor = DataSupport.findBySQL("select * from jyxmsz where xmbh=?",xmbh);
        for(cursor.moveToFirst();!cursor.isAfterLast();cursor.moveToNext()) {
            float price = cursor.getFloat(cursor.getColumnIndex(hyj));
            float xsjg = cursor.getFloat(cursor.getColumnIndex("xsjg"));
            hyPrice=price>0?price:xsjg;
        }
        cursor.close();
        return hyPrice;
    }

    @OnClick({R.id.btn_cancel, R.id.btn_confirm, R.id.btn_shama})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_cancel:
                finish();
                break;
            case R.id.btn_confirm:
                mPhone=mEtPhone.getText().toString().trim();
                mPassword=mEtPassword.getText().toString().trim();
                mPost6.post_ims_card_members(mPhone,mPassword,mWeid);//发送post请求云会员登录
                break;
            case R.id.btn_shama:
                Intent intent = new Intent(this, ScanActivity.class);
                startActivityForResult(intent,REQUESTCODE);
                break;
        }
    }
}

package com.duowei.dw_pos;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.duowei.dw_pos.bean.YHJBQK;
import com.duowei.dw_pos.dialog.ClearDialogFragment;
import com.duowei.dw_pos.dialog.MsgInputDialog;
import com.duowei.dw_pos.fragment.UpdateFragment;
import com.duowei.dw_pos.httputils.DownHTTP;
import com.duowei.dw_pos.httputils.VolleyResultListener;
import com.duowei.dw_pos.tools.CartList;
import com.duowei.dw_pos.tools.DataLoad;
import com.duowei.dw_pos.tools.Net;
import com.duowei.dw_pos.tools.Users;

import org.json.JSONException;
import org.json.JSONObject;
import org.litepal.crud.DataSupport;

import java.util.List;

import butterknife.ButterKnife;
import pl.com.salsoft.sqlitestudioremote.SQLiteStudioService;
public class LandActivity extends AppCompatActivity implements View.OnClickListener,
        MsgInputDialog.OnconfirmClick {
    private SharedPreferences mSp;
    private EditText mEtAccount;
    private EditText mEtPassword;
    private Intent mIntent;
    private TextView mVersion;
    private String mOrderstytle;
    private MsgInputDialog mMsgInputDialog;
    private int mVersionCode=0;

    private final String updateUrl="http://ouwtfo4eg.bkt.clouddn.com/dw_pos.txt";
    private boolean mClear;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_land);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

        ButterKnife.bind(this);
        SQLiteStudioService.instance().start(this);
        mSp = getSharedPreferences("user", Context.MODE_PRIVATE);
        initUI();

        checkVersion();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mOrderstytle = mSp.getString("orderstytle", getResources().getString(R.string.order_stytle_zhongxican));
        mClear = mSp.getBoolean("clear", false);
    }

    private void initUI() {
        mEtAccount = (EditText) findViewById(R.id.et_account);
        mEtPassword = (EditText) findViewById(R.id.et_password);
        mVersion = (TextView) findViewById(R.id.tv_version);
        findViewById(R.id.tv_brush).setOnClickListener(this);
        findViewById(R.id.tv_setting).setOnClickListener(this);
        findViewById(R.id.btn_land).setOnClickListener(this);
        findViewById(R.id.btn_exit).setOnClickListener(this);
        mVersion.setText(getVersionName());
    }
    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.tv_brush:
                DataLoad.getInstance().startLoad(this);
                break;
            case R.id.tv_setting:
                mMsgInputDialog = new MsgInputDialog(this, "请输入密码", "密码：");
                mMsgInputDialog.setOnconfirmClick(this);
                break;
            case R.id.btn_land:
                String account = mEtAccount.getText().toString().trim();
                String password = mEtPassword.getText().toString().trim();
                List<YHJBQK> yhjbqk = DataSupport.select("YHMM").where("YHBH=?",account).find(YHJBQK.class);
                if(yhjbqk.size()<=0){
                    Toast.makeText(this,"账号不存在",Toast.LENGTH_SHORT).show();
                }else if(yhjbqk.get(0).getYHMM().equals(password)){
                    Users.YHBH=account;
                    Users.pad=mSp.getString("pad","");
                    String ip = mSp.getString("ip", "");
                    String port = mSp.getString("port", "");
                    Net.url="http://"+ip+":"+port+"/server/ServerSvlt?";
                    List<YHJBQK> yhmc = DataSupport.where("YHBH=?", account).find(YHJBQK.class);
                    Users.YHBH=account;
                    Users.YHMC=yhmc.get(0).YHMC;
                    Users.TDQX=yhmc.get(0).TDQX;
                    if(mOrderstytle.equals(getResources().getString(R.string.order_stytle_zhongxican))){
                        mIntent = new Intent(this, DinningActivity.class);
                    }else if(mOrderstytle.equals(getResources().getString(R.string.order_stytle_kuaican))){
                        mIntent = new Intent(this, CashierDeskActivity.class);
                    }
                    startActivity(mIntent);
                }else{
                    Toast.makeText(this,"密码错误",Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.btn_exit:
                if(mClear&&!TextUtils.isEmpty(Users.pad)){
                    ClearDialogFragment fragment = ClearDialogFragment.newInstance(Users.pad,Users.YHMC);
                    fragment.show(getSupportFragmentManager(),null);
                }else{
                    finish();
                }
                break;
        }
    }

    private void checkVersion() {
        DownHTTP.getVolley(updateUrl, new VolleyResultListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String versionCode = jsonObject.getString("versionCode");
                    if(Integer.parseInt(versionCode)>mVersionCode){
                        final String name = jsonObject.getString("name");
                        final String url = jsonObject.getString("url");
                        String msg = jsonObject.getString("msg");
                        showDialog(name, url, msg);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void showDialog(final String name, final String url, String msg) {
        AlertDialog.Builder builder = new AlertDialog.Builder(LandActivity.this);
        builder.setIcon(R.mipmap.logo);
        builder.setTitle("发现新版本是否升级？");
        builder.setMessage(msg);
        builder.setNegativeButton("取消",null);
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                UpdateFragment fragment = UpdateFragment.newInstance(url, name);
                fragment.show(getFragmentManager(),getString(R.string.update));
            }
        });
        builder.show();
    }

    private String getVersionName() {
        String versionName="版本号：1.0";
        try {
            PackageInfo info = this.getPackageManager().getPackageInfo(this.getPackageName(), 0);
            versionName = "版本号："+info.versionName;
            mVersionCode = info.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return versionName;
    }

    @Override
    protected void onDestroy() {
        SQLiteStudioService.instance().stop();
        CartList.newInstance(this).getList().clear();
        super.onDestroy();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            //TODO something
            if(mClear&&!TextUtils.isEmpty(Users.pad)){
                ClearDialogFragment fragment = ClearDialogFragment.newInstance(Users.pad,Users.YHMC);
                fragment.show(getSupportFragmentManager(),null);
            }else{
                finish();
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void getDialogInput(String contents) {
        if(contents.equals("5651400")){
            mIntent=new Intent(this,SettingsActivity.class);
            startActivity(mIntent);
            mMsgInputDialog.cancel();
        }else{
            Toast.makeText(this,"密码有误",Toast.LENGTH_SHORT).show();
        }
    }
}

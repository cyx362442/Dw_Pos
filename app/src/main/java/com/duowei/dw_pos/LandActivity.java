package com.duowei.dw_pos;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.duowei.dw_pos.bean.YHJBQK;
import com.duowei.dw_pos.tools.Net;
import com.duowei.dw_pos.tools.Users;

import org.litepal.crud.DataSupport;

import java.util.List;

import butterknife.ButterKnife;
import pl.com.salsoft.sqlitestudioremote.SQLiteStudioService;
public class LandActivity extends AppCompatActivity implements View.OnClickListener {
    private SharedPreferences.Editor mEdit;
    private SharedPreferences mSp;
    private EditText mEtAccount;
    private EditText mEtPassword;
    private Intent mIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_land);
        ButterKnife.bind(this);
        SQLiteStudioService.instance().start(this);
        mSp = getSharedPreferences("user", Context.MODE_PRIVATE);
        mEdit = mSp.edit();

        initUI();
    }

    private void initUI() {
        mEtAccount = (EditText) findViewById(R.id.et_account);
        mEtPassword = (EditText) findViewById(R.id.et_password);
        findViewById(R.id.tv_setting).setOnClickListener(this);
        findViewById(R.id.btn_land).setOnClickListener(this);
        findViewById(R.id.btn_exit).setOnClickListener(this);
    }
    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.tv_setting:
                mIntent=new Intent(this,SettingsActivity.class);
                startActivity(mIntent);
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
                    Users.YHMC=yhmc.get(0).YHMC;
                    Users.TDQX=yhmc.get(0).TDQX;
                    mIntent = new Intent(this, DinningActivity.class);
                    startActivity(mIntent);
                }else{
                    Toast.makeText(this,"密码错误",Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.btn_exit:
                finish();
                break;
        }
    }
    @Override
    protected void onDestroy() {
        SQLiteStudioService.instance().stop();
        super.onDestroy();
    }
}

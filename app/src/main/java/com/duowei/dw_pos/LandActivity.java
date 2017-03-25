package com.duowei.dw_pos;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.duowei.dw_pos.bean.YHJBQK;
import com.duowei.dw_pos.httputils.DownHTTP;
import com.duowei.dw_pos.httputils.VolleyResultListener;
import com.duowei.dw_pos.tools.DataLoad;
import com.duowei.dw_pos.tools.Net;
import com.duowei.dw_pos.tools.Users;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;


import org.litepal.crud.DataSupport;

import java.util.List;

import butterknife.ButterKnife;
import pl.com.salsoft.sqlitestudioremote.SQLiteStudioService;

public class LandActivity extends AppCompatActivity implements View.OnClickListener {

    private SharedPreferences.Editor mEdit;
    private SharedPreferences mSp;
    private EditText mIp;
    private EditText mPort;
    private EditText mPad;
    private EditText mEtAccount;
    private EditText mEtPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_land);
        ButterKnife.bind(this);
        SQLiteStudioService.instance().start(this);

        mSp = getSharedPreferences("user", Context.MODE_PRIVATE);
        mEdit = mSp.edit();
        setSimdingMenu();
        initUI();
    }

    private void initUI() {
        mEtAccount = (EditText) findViewById(R.id.et_account);
        mEtPassword = (EditText) findViewById(R.id.et_password);
        mIp = (EditText) findViewById(R.id.et_ip);
        mPort = (EditText) findViewById(R.id.et_port);
        mPad = (EditText) findViewById(R.id.et_pad);
        findViewById(R.id.btn_connect).setOnClickListener(this);
        findViewById(R.id.btn_load).setOnClickListener(this);
        findViewById(R.id.btn_land).setOnClickListener(this);

        mIp.setText(mSp.getString("ip",""));
        mPort.setText(mSp.getString("port",""));
        mPad.setText(mSp.getString("pad",""));
    }

    private void setSimdingMenu() {
        SlidingMenu menu = new SlidingMenu(this);
        menu.setMode(SlidingMenu.LEFT);
        // 设置触摸屏幕的模式
        menu.setTouchModeAbove(SlidingMenu.TOUCHMODE_MARGIN);
        menu.setShadowWidthRes(R.dimen.shadow_width);
        menu.setShadowDrawable(R.color.colorGray);
        // 设置滑动菜单视图的宽度
        menu.setBehindOffsetRes(R.dimen.slidingmenu_offset);
        // 设置渐入渐出效果的值
        menu.setFadeDegree(0.35f);
        /**
         * SLIDING_WINDOW will include the Title/ActionBar in the content
         * section of the SlidingMenu, while SLIDING_CONTENT does not.
         */
        menu.attachToActivity(this, SlidingMenu.SLIDING_CONTENT);
        //为侧滑菜单设置布局
        menu.setMenu(R.layout.layout_left_menu);
    }

    @Override
    public void onClick(View view) {
        String ip=mIp.getText().toString().trim();
        String port=mPort.getText().toString().trim();
        switch (view.getId()){
            case R.id.btn_land:
                String account = mEtAccount.getText().toString().trim();
                String password = mEtPassword.getText().toString().trim();
                List<YHJBQK> yhjbqk = DataSupport.select("YHMM").where("YHBH=?",account).find(YHJBQK.class);
                if(yhjbqk.size()<=0){
                    Toast.makeText(this,"账号不存在",Toast.LENGTH_SHORT).show();
                }else if(yhjbqk.get(0).getYHMM().equals(password)){
                    saveData(ip, port);
                    Users.YHBH=account;
                    List<YHJBQK> yhmc = DataSupport.select("YHMC").where("YHBH=?", account).find(YHJBQK.class);
                    Users.YHMC=yhmc.get(0).YHMC;
                    Intent intent = new Intent(this, DinningActivity.class);
                    startActivity(intent);
                }else{
                    Toast.makeText(this,"密码错误",Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.btn_connect:
                String url="http://"+ip+":"+port+"/server/index.htm";
                Http_connect(ip, port, url);
                break;
            case R.id.btn_load:
                Net.url="http://"+ip+":"+port+"/server/ServerSvlt?";
                DataLoad dataLoad = new DataLoad(this);
                dataLoad.startLoad();
                break;
        }
    }

    private void Http_connect(final String ip, final String port, String url) {
        DownHTTP.getVolley(url, new VolleyResultListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(LandActivity.this,"连接失败",Toast.LENGTH_SHORT).show();
            }
            @Override
            public void onResponse(String response) {
               if(response.contains("Sucess")){
                   Toast.makeText(LandActivity.this,"数据库连接成功",Toast.LENGTH_SHORT).show();
                   Net.url="http://"+ip+":"+port+"/server/ServerSvlt?";
                   saveData(ip, port);
               }
            }
        });
    }

    private void saveData(String ip, String port) {
        Net.url="http://"+ip+":"+port+"/server/ServerSvlt?";
        mEdit.putString("ip",ip);
        mEdit.putString("port",port);
        mEdit.putString("pad",mPad.getText().toString().trim());
        mEdit.putString("url", Net.url);
        mEdit.commit();
    }

    @Override
    protected void onDestroy() {
        SQLiteStudioService.instance().stop();
        super.onDestroy();
    }
}

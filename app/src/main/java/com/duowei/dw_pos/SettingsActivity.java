package com.duowei.dw_pos;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.duowei.dw_pos.tools.DataLoad;
import com.duowei.dw_pos.tools.Net;
import com.duowei.dw_pos.tools.Users;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SettingsActivity extends AppCompatActivity {

    @BindView(R.id.et_ip)
    EditText mEtIp;
    @BindView(R.id.et_port)
    EditText mEtPort;
    @BindView(R.id.et_pad)
    EditText mEtPad;
    @BindView(R.id.tv5)
    TextView mTv5;
    @BindView(R.id.checkbox)
    CheckBox mCheckbox;
    @BindView(R.id.rl_autoStart)
    RelativeLayout mRlAutoStart;
    @BindView(R.id.rl_weight)
    RelativeLayout mRlWeight;
    @BindView(R.id.cb_weight)
    CheckBox mCbWeight;
    @BindView(R.id.btn_load)
    Button mBtnLoad;
    @BindView(R.id.btn_back)
    Button mBtnBack;

    private SharedPreferences.Editor mEdit;
    private SharedPreferences mSp;
    private String mIp;
    private String mPort;
    private String mPad;
    private boolean auto;
    private boolean weight;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        ButterKnife.bind(this);
        mSp = getSharedPreferences("user", Context.MODE_PRIVATE);
        mEdit = mSp.edit();

        initUI();
    }

    private void initUI() {
        mEtIp.setText(mSp.getString("ip","192.168.1.78"));
        mEtPort.setText(mSp.getString("port","2233"));
        mEtPad.setText(mSp.getString("pad",""));
        auto=mSp.getBoolean("auto",true);
        if(auto){
            mCheckbox.setChecked(true);
        }else{
            mCheckbox.setChecked(false);
        }

        weight = mSp.getBoolean("weight", false);
        mCbWeight.setChecked(weight);
    }

    @OnClick({R.id.rl_autoStart, R.id.rl_weight, R.id.btn_load, R.id.btn_back})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.rl_autoStart:
                if(auto){
                    mCheckbox.setChecked(false);
                }else{
                    mCheckbox.setChecked(true);
                }
                auto=!auto;
                mEdit.putBoolean("auto",auto);
                mEdit.commit();
                break;

            case R.id.rl_weight:
                weight = !weight;
                mCbWeight.setChecked(weight);
                mEdit.putBoolean("weight", weight);
                mEdit.commit();
                break;

            case R.id.btn_load:
                mIp = mEtIp.getText().toString().trim();
                mPort = mEtPort.getText().toString().trim();
                mPad = mEtPad.getText().toString().trim();
                if (TextUtils.isEmpty(mIp)) {
                    Toast.makeText(this, "请填写IP地址", Toast.LENGTH_SHORT).show();

                } else if (TextUtils.isEmpty(mPort)) {
                    Toast.makeText(this, "请填写端口", Toast.LENGTH_SHORT).show();

                } else if (TextUtils.isEmpty(mPad)) {
                    Toast.makeText(this, "请填写平板名称", Toast.LENGTH_SHORT).show();

                } else{
                    Net.url="http://"+mIp+":"+mPort+"/server/ServerSvlt?";
                    Users.pad=mPad;
                    mEdit.putString("ip",mIp);
                    mEdit.putString("port",mPort);
                    mEdit.putString("pad",mPad);
                    mEdit.putString("url", Net.url);
                    mEdit.commit();
                    DataLoad dataLoad = new DataLoad(this);
                    dataLoad.startLoad();
                }
                break;
            case R.id.btn_back:
                finish();
                break;
        }
    }
}

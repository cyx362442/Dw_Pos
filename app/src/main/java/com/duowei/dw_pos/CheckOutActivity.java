package com.duowei.dw_pos;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.duowei.dw_pos.bean.WMLSB;
import com.duowei.dw_pos.bean.WMLSBJB;
import com.duowei.dw_pos.httputils.DownHTTP;
import com.duowei.dw_pos.httputils.VolleyResultListener;
import com.duowei.dw_pos.tools.Net;
import com.duowei.dw_pos.tools.Users;
import com.google.gson.Gson;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class CheckOutActivity extends AppCompatActivity {

    @BindView(R.id.img_return)
    ImageView mImgReturn;
    @BindView(R.id.rlTop)
    RelativeLayout mRlTop;
    @BindView(R.id.tv_user)
    TextView mTvUser;
    @BindView(R.id.btn_dayin)
    Button mBtnDayin;
    @BindView(R.id.btn_dingdan)
    Button mBtnDingdan;
    @BindView(R.id.tv_time)
    TextView mTvTime;
    @BindView(R.id.tv_zonger)
    TextView mTvZonger;
    @BindView(R.id.tv_zekou)
    TextView mTvZekou;
    @BindView(R.id.tv_yishou)
    TextView mTvYishou;
    @BindView(R.id.tv_daishou)
    TextView mTvDaishou;
    @BindView(R.id.tv_zhaoling)
    TextView mTvZhaoling;
    @BindView(R.id.imgzhifubao)
    ImageView mImgzhifubao;
    @BindView(R.id.rl_zhifubao)
    RelativeLayout mRlZhifubao;
    @BindView(R.id.imgweixin)
    ImageView mImgweixin;
    @BindView(R.id.rl_weixin)
    RelativeLayout mRlWeixin;
    @BindView(R.id.rl_jiezhang)
    RelativeLayout mRlJiezhang;
    @BindView(R.id.tv_table)
    TextView mTvTable;
    @BindView(R.id.tvPersons)
    TextView mTvPersons;
    @BindView(R.id.tv_opener)
    TextView mTvOpener;
    private WMLSBJB mWmlsbjb;
    private float mTotalMoney=0;//总额(原始价格总额)
    private float mActualMoney=0;//实际金额
    private float mYishou=0.00f;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_out);
        ButterKnife.bind(this);
        String response = getIntent().getStringExtra("response");
        Gson gson = new Gson();
        WMLSBJB[] wmlsbjbs = gson.fromJson(response, WMLSBJB[].class);
        mWmlsbjb = wmlsbjbs[0];
    }

    @Override
    protected void onStart() {
        super.onStart();
        mTvUser.setText(Users.YHMC);
        mTvTable.setText(mWmlsbjb.getZH());
        mTvTime.setText(mWmlsbjb.getJYSJ());
        mTvPersons.setText(mWmlsbjb.getJCRS()+"人");
        mTvOpener.setText(mWmlsbjb.getYHBH());

        String sql="SELECT convert(varchar(30),getdate(),121) ZSSJ2, isnull(BY3,0)BY3,* FROM WMLSB WHERE WMDBH = '" + mWmlsbjb.getWMDBH() + "'|";
        DownHTTP.postVolley6(Net.url, sql, new VolleyResultListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
            @Override
            public void onResponse(String response) {
                Gson gson = new Gson();
                WMLSB[] wmlsbs = gson.fromJson(response, WMLSB[].class);
                for(WMLSB W:wmlsbs){
                    mTotalMoney = mTotalMoney+W.getYSJG() * W.getSL();
                    mActualMoney = mActualMoney+W.getDJ() * W.getSL();
                }
                mTvZonger.setText("￥"+mTotalMoney);
                mTvZekou.setText("￥"+(mTotalMoney-mActualMoney));
                mTvYishou.setText("￥"+mYishou);
                mTvDaishou.setText("￥"+(mActualMoney-mYishou));
            }
        });
    }

    @OnClick({R.id.img_return, R.id.btn_dayin, R.id.btn_dingdan, R.id.rl_zhifubao, R.id.rl_weixin, R.id.rl_jiezhang})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.img_return:
                finish();
                break;
            case R.id.btn_dayin:
                break;
            case R.id.btn_dingdan:
                break;
            case R.id.rl_zhifubao:
                break;
            case R.id.rl_weixin:
                break;
            case R.id.rl_jiezhang:
                break;
        }
    }
}

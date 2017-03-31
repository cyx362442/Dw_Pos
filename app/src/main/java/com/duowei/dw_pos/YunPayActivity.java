package com.duowei.dw_pos;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.duowei.dw_pos.fragment.YunAccountFragment;
import com.duowei.dw_pos.fragment.YunCardFragment;
import com.duowei.dw_pos.fragment.YunPayFragment;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class YunPayActivity extends AppCompatActivity implements YunCardFragment.GvClickListener{

    @BindView(R.id.card)
    TextView mCard;
    @BindView(R.id.name)
    TextView mName;
    @BindView(R.id.frame01)
    FrameLayout mFrame01;
    @BindView(R.id.frame02)
    FrameLayout mFrame02;
    @BindView(R.id.frame03)
    FrameLayout mFrame03;
    @BindView(R.id.btn_confirm)
    Button mBtnConfirm;
    @BindView(R.id.btn_cancel)
    Button mBtnCancel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_yun_pay);
        ButterKnife.bind(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        toYunAccountFragment();
        toYunCardFragment();
        toYunPayFragment("");
    }

    private void toYunAccountFragment() {
        FragmentManager fm = getFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        YunAccountFragment fragment = new YunAccountFragment();
        ft.replace(R.id.frame01,fragment);
        ft.commit();
    }

    private void toYunPayFragment(String value) {
        FragmentManager fm = getFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        YunPayFragment fragment = new YunPayFragment();
        Bundle bundle = new Bundle();
        bundle.putString("msg", value);
        fragment.setArguments(bundle);
        ft.replace(R.id.frame02, fragment);
        ft.commit();
    }

    private void toYunCardFragment() {
        FragmentManager fm = getFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        YunCardFragment fragment0 = new YunCardFragment();
        ft.replace(R.id.frame03,fragment0);
        ft.commit();
    }


    @OnClick({R.id.btn_confirm, R.id.btn_cancel})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_confirm:
                break;
            case R.id.btn_cancel:
                break;
        }
    }

    @Override
    public void sendMsg(int postion) {
        toYunPayFragment(postion+"号");
    }
}

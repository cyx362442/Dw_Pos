package com.duowei.dw_pos.fragment;


import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.duowei.dw_pos.R;
import com.duowei.dw_pos.bean.Moneys;

import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * A simple {@link Fragment} subclass.
 */
public class YunAccountFragment extends Fragment {
    @BindView(R.id.tv_zr)
    TextView mTvZr;
    @BindView(R.id.tv_zk)
    TextView mTvZk;
    @BindView(R.id.tv_ys)
    TextView mTvYs;
    @BindView(R.id.tv_yf)
    TextView mTvYf;
    @BindView(R.id.tv_wf)
    TextView mTvWf;
    Unbinder unbinder;

    public YunAccountFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View inflate = inflater.inflate(R.layout.fragment_yun_account, container, false);
        unbinder = ButterKnife.bind(this, inflate);
        return inflate;
    }

    @Override
    public void onStart() {
        super.onStart();
        mTvZr.setText("￥"+String.format(Locale.CHINA, "%.2f", Moneys.xfzr));
        mTvZk.setText("￥"+String.format(Locale.CHINA, "%.2f", Moneys.zkjr));
        mTvYs.setText("￥"+String.format(Locale.CHINA, "%.2f", Moneys.ysjr));
        mTvYf.setText("￥"+String.format(Locale.CHINA, "%.2f", Moneys.yfjr));
        mTvWf.setText("￥"+String.format(Locale.CHINA, "%.2f", Moneys.wfjr));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}

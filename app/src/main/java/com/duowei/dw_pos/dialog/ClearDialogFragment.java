package com.duowei.dw_pos.dialog;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatDialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.duowei.dw_pos.R;
import com.duowei.dw_pos.httputils.DownHTTP;
import com.duowei.dw_pos.httputils.VolleyResultListener;
import com.duowei.dw_pos.tools.Format;
import com.duowei.dw_pos.tools.Net;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * A simple {@link Fragment} subclass.
 */
public class ClearDialogFragment extends AppCompatDialogFragment {
    @BindView(R.id.jsj)
    TextView mJsj;
    @BindView(R.id.yhmc)
    TextView mYhmc;
    @BindView(R.id.count)
    TextView mCount;
    @BindView(R.id.ysje)
    TextView mYsje;
    @BindView(R.id.zk)
    TextView mZk;
    @BindView(R.id.ys)
    TextView mYs;
    Unbinder unbinder;
    @BindView(R.id.progressBar)
    ProgressBar mProgressBar;
    private String mPad;

    public ClearDialogFragment() {
        // Required empty public constructor
    }

    public static ClearDialogFragment newInstance(String pad, String yhmc) {
        Bundle args = new Bundle();
        args.putString("pad", pad);
        args.putString("yhmc", yhmc);
        ClearDialogFragment fragment = new ClearDialogFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View inflate = inflater.inflate(R.layout.fragment_clear_dialog, container, false);
        unbinder = ButterKnife.bind(this, inflate);
        mPad = getArguments().getString("pad");
        String yhmc = getArguments().getString("yhmc", "");
        mJsj.setText("收银机号：" + mPad);
        mYhmc.setText("收银员：" + yhmc);
        String sql = "select count(wmdbh)as xsbs,sum(ysje)as ysje,sum(ys)as ys from wmlsbjb where sfyjz='1' and jsj='" + mPad + "' and isnull(by7,0)<>'1'|";
        DownHTTP.postVolley6(Net.url, sql, new VolleyResultListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getActivity(), "网络异常", Toast.LENGTH_SHORT).show();
                mProgressBar.setVisibility(View.GONE);
            }

            @Override
            public void onResponse(String response) {
                try {
                    JSONArray jsonArray = new JSONArray(response);
                    JSONObject jsonObject = jsonArray.getJSONObject(0);
                    mCount.setText("销售笔数：" + jsonObject.getString("xsbs"));
                    String ysje = jsonObject.getString("ysje");
                    String ys = jsonObject.getString("ys");
                    String zk = Format.digitFormat((Float.parseFloat(ysje) - Float.parseFloat(ys)) + "");
                    mYsje.setText("原价销售总额：￥" + Format.digitFormat(ysje));
                    mZk.setText("折扣金额：￥" + zk);
                    mYs.setText("应收金额：￥" + Format.digitFormat(ys));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                mProgressBar.setVisibility(View.GONE);
            }
        });

        return inflate;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @OnClick({R.id.btn_exit, R.id.btn_clear})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_exit:
                dismiss();
                getActivity().finish();
                break;
            case R.id.btn_clear:
                mProgressBar.setVisibility(View.VISIBLE);
                String sql = "update wmlsbjb set by7='1' where sfyjz='1' and jsj='" + mPad + "'|";
                DownHTTP.postVolley7(Net.url, sql, new VolleyResultListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getActivity(), "网络异常", Toast.LENGTH_SHORT).show();
                        mProgressBar.setVisibility(View.GONE);
                    }

                    @Override
                    public void onResponse(String response) {
                        if (response.contains("richado")) {
                            Toast.makeText(getActivity(), "清机成功", Toast.LENGTH_SHORT).show();
                            dismiss();
                            getActivity().finish();
                        }
                        mProgressBar.setVisibility(View.GONE);
                    }
                });
                break;
        }
    }
}

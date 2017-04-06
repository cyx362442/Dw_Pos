package com.duowei.dw_pos.fragment;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatDialogFragment;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.duowei.dw_pos.bean.WMLSB;
import com.duowei.dw_pos.bean.WMLSBJB;
import com.duowei.dw_pos.constant.ExtraParm;
import com.duowei.dw_pos.httputils.VolleyUtils;
import com.duowei.dw_pos.tools.Base64;
import com.duowei.dw_pos.tools.CartList;
import com.duowei.dw_pos.tools.Net;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * 加载远程数据窗口
 */

public class LoadingDialogFragment extends AppCompatDialogFragment {

    private Context mContext;
    private String mWmdbh;

    private Gson mGson = new Gson();
    private VolleyUtils mVolleyUtils;

    public static LoadingDialogFragment newInstance(String wmdbh) {

        Bundle args = new Bundle();
        args.putString(ExtraParm.EXTRA_WMDBH, wmdbh);

        LoadingDialogFragment fragment = new LoadingDialogFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getContext();
        setCancelable(false);

        mWmdbh = getArguments().getString(ExtraParm.EXTRA_WMDBH);
        mVolleyUtils = VolleyUtils.getInstance(mContext);

        CartList.sWMLSBJB = null;
        CartList.sWMLSBList.clear();
        loadData1();
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        ProgressDialog dialog = new ProgressDialog(mContext);
        dialog.setMessage("加载中...");
        return dialog;
    }

    public void loadData1() {
        String queryWmlsbjbSql = "select * from wmlsbjb where wmdbh = '" + mWmdbh + "'|";

        mVolleyUtils.postQuerySql6(Net.url, Base64.getBase64(queryWmlsbjbSql).replaceAll("\n", ""),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Type type = new TypeToken<ArrayList<WMLSBJB>>(){}.getType();
                        List<WMLSBJB> wmlsbjbList = mGson.fromJson(response, type);
                        CartList.sWMLSBJB = wmlsbjbList.get(0);
                        loadData2();
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(mContext, "加载失败", Toast.LENGTH_SHORT).show();
                        dismiss();
                    }
                });
    }

    public void loadData2() {
        String queryWmlsbSql = "select * from wmlsb where wmdbh = '" + mWmdbh + "'|";

        mVolleyUtils.postQuerySql6(Net.url, Base64.getBase64(queryWmlsbSql).replaceAll("\n", ""),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Type type = new TypeToken<ArrayList<WMLSB>>(){}.getType();
                        CartList.sWMLSBList = mGson.fromJson(response, type);
                        for (WMLSB w : CartList.sWMLSBList) {
                            w.setRemote(1);
                        }

                        if (mListener != null) {
                            mListener.onLoadSuccess();
                        }

                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                dismiss();
                            }
                        }, 500);
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(mContext, "加载失败", Toast.LENGTH_SHORT).show();
                        dismiss();
                    }
                });
    }

    private OnLoadSuccessListener mListener;

    public interface OnLoadSuccessListener {
        void onLoadSuccess();
    }

    public void setListener(OnLoadSuccessListener listener) {
        mListener = listener;
    }
}

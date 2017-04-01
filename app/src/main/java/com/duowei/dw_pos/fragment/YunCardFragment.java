package com.duowei.dw_pos.fragment;


import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import com.duowei.dw_pos.R;
import com.duowei.dw_pos.adapter.YunGvAdapter;
import com.duowei.dw_pos.bean.ImsCardMember;
import com.duowei.dw_pos.bean.Moneys;
import com.duowei.dw_pos.bean.YunFu;
import com.duowei.dw_pos.dialog.CheckOutDialog;

import java.io.Flushable;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class YunCardFragment extends Fragment implements AdapterView.OnItemClickListener, CheckOutDialog.OnconfirmClick {
    private ArrayList<ImsCardMember> mYunList;
    private List<YunFu> listYunPayFragment = new ArrayList<>();
    private GridView mGv;
    private ImsCardMember mYun;
    private CheckOutDialog mDialog;
    private YunGvAdapter mAdapter;

    public YunCardFragment() {
        // Required empty public constructor
    }
    public GvClickListener listener;

    public interface GvClickListener{
        void yunPayFragment(List<YunFu> listPay);
    }
    @Override
    public void onAttach(Activity activity) {
        listener=(GvClickListener) activity;
        super.onAttach(activity);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View inflate = inflater.inflate(R.layout.fragment_yun_card, container, false);
        Bundle bundle = getArguments();
        mYunList = (ArrayList<ImsCardMember>) bundle.getSerializable("cards");
        mGv = (GridView) inflate.findViewById(R.id.gridView);
        return inflate;
    }

    @Override
    public void onStart() {
        super.onStart();
        mAdapter = new YunGvAdapter(getActivity(),mYunList);
        mGv.setAdapter(mAdapter);
        mGv.setOnItemClickListener(this);
    }
    boolean isCreate2=false;
    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
        mYun = mYunList.get(position);
        switch (mYun.getTicket()){
            /**储值消费*/
            case 0:
                if(isCreate2==false){//未选中，添加
                    if(Moneys.wfjr<=0){
                        Toast.makeText(getActivity(),"您无需再付款了",Toast.LENGTH_SHORT).show();
                    }else{
                        //储值卡余额大于未付金额，取未付金额，否则取储值卡余额
                        float money=Moneys.wfjr< mYun.getCredit2()?Moneys.wfjr: mYun.getCredit2();
                        mDialog = new CheckOutDialog(getActivity(), "储值卡消费", money);
                        mDialog.setOnconfirmClick(this);
                    }
                }else{//己选过，删除
                    for (int j = 0; j < listYunPayFragment.size(); j++) {
                        if (listYunPayFragment.get(j).ticket==0) {
                            listYunPayFragment.remove(j);
                            break;
                        }
                    }
                    isCreate2=!isCreate2;
                    bushYunPayFragmentData();
                    listener.yunPayFragment(listYunPayFragment);
                    mAdapter.setisCreate2(false);
                    mAdapter.notifyDataSetChanged();
                }
                break;
        }

//        listener.yunPayFragment(position);
    }
    /**接口回调获取Dialog输入的金额*/
    @Override
    public void getDialogInput(String money) {
        isCreate2=!isCreate2;
        listYunPayFragment.add(new YunFu(mYun.getCardgrade(), "", mYun.getCredit1(), mYun.getCredit2(), Float.parseFloat(money), 0,mYun.getTicket()));
        listener.yunPayFragment(listYunPayFragment);

        bushYunPayFragmentData();

        mAdapter.setisCreate2(true);
        mAdapter.notifyDataSetChanged();
        mDialog.cancel();
    }

    private void bushYunPayFragmentData() {
        float yf=0f;
        for (int i = 0; i < listYunPayFragment.size(); i++) {
            yf = yf + listYunPayFragment.get(i).money;
        }
        Moneys.yfjr=yf;
        Moneys.wfjr=Moneys.ysjr-Moneys.yfjr;
    }
}

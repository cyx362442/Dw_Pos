package com.duowei.dw_pos.fragment;


import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.net.http.LoggingEventHandler;
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
import com.duowei.dw_pos.bean.WMLSB;
import com.duowei.dw_pos.bean.YunFu;
import com.duowei.dw_pos.dialog.CheckOutDialog;
import com.duowei.dw_pos.dialog.YunFuDialog;

import java.io.Flushable;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class YunCardFragment extends Fragment implements AdapterView.OnItemClickListener, YunFuDialog.OnconfirmClick {
    private ArrayList<ImsCardMember> mYunList;
    private List<YunFu> listYunPayFragment = new ArrayList<>();
    private GridView mGv;
    private ImsCardMember mYun;
    private YunFuDialog mDialog;
    private YunGvAdapter mAdapter;
    private ArrayList<WMLSB> mListWmlsb;
    private ArrayList<WMLSB>mListWmlsb2;

    private final int PETCARD=0;
    private final int CREADITS=1;
    private final int COUPON1=2;
    private final int COUPON2=3;
    private final int COUPON3=4;
    private final int COUPON4=5;

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
        mListWmlsb = (ArrayList<WMLSB>) bundle.getSerializable("WMLSB");

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
    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
        mYun = mYunList.get(position);
        switch (mYun.getTicket()){
            /**储值消费*/
            case PETCARD:
                if(mYun.isSelect()==false){//未选中，添加
                    if(Moneys.wfjr<=0){
                        Toast.makeText(getActivity(),"您无需再付款了",Toast.LENGTH_SHORT).show();
                    }else{
                        //储值卡余额大于未付金额，取未付金额，否则取储值卡余额
                        float money=Moneys.wfjr< mYun.getCredit2()?Moneys.wfjr: mYun.getCredit2();
                        mDialog = new YunFuDialog(getActivity(), "储值卡消费", money);
                        mDialog.setOnconfirmClick(this);
                    }
                }else{//己选过，删除
                    for (int j = 0; j < listYunPayFragment.size(); j++) {
                        if (listYunPayFragment.get(j).ticket==PETCARD) {
                            listYunPayFragment.remove(j);
                            break;
                        }
                    }
                    brushYunPayFragmentData();
                    listener.yunPayFragment(listYunPayFragment);
                    mAdapter.notifyDataSetChanged();
                    mYun.setSelect(false);
                }
                break;
                /**积分消费*/
            case CREADITS:
                float money=0;
                int by3=0;//总需积分
                float minBy3=1000000000;//积分最小的一项
                if(mYun.isSelect()==false){//未选中，添加
                    if(Moneys.wfjr<=0){
                        Toast.makeText(getActivity(),"您无需再付款了",Toast.LENGTH_SHORT).show();
                    }else{
                        for(WMLSB wmlsb:mListWmlsb){
                            if(wmlsb.getBY3()>0){//兑换该单品所需积分
                                by3= (int)(by3+(wmlsb.getBY3())*wmlsb.getSL());//总积分
                                money=money+wmlsb.getXJ();//所有积分抵扣的金额
//                                mListWmlsb2.add(wmlsb);
                                if(minBy3>wmlsb.getBY3()*wmlsb.getSL()){//获取最小积分
                                    minBy3=wmlsb.getBY3()*wmlsb.getSL();
                                }
                            }
                        }
                        if(minBy3>mYun.getCredit1()){//现有积分都不足以抵扣最小的积分商品
                            Toast.makeText(getActivity(),"没有可抵扣积分的商品或现有积分不足",Toast.LENGTH_SHORT).show();
                            return;
                        }
                        if(minBy3<mYun.getCredit1()&&by3>mYun.getCredit1()){//现有积够抵扣其中一项商品，但不足抵扣所有商品
//                            mLl_jinfen.setVisibility(View.VISIBLE);
//                            mTv_jinfenCurrent.setText(mYun.getCredit1()+"");
//                            mGvJinfen =  new GvJinfen(YunFuActivity.this, list_wmlsb);
//                            mGv_jinfen.setAdapter(mGvJinfen);
                        }else if(by3>0&&by3<mYun.getCredit1()){//可用积分大于兑换积分和,直接抵扣
                            money=money>Moneys.wfjr?Moneys.wfjr:money;
                            listYunPayFragment.add(new YunFu(mYun.getCardgrade(), "", mYun.getCredit1(), mYun.getCredit2(), money, 0,mYun.getTicket()));
                            listener.yunPayFragment(listYunPayFragment);
                            brushYunPayFragmentData();
                            mAdapter.notifyDataSetChanged();
                            mYun.setSelect(true);
                        }
                    }
                }else{//己选中，删除
                    for (int j = 0; j < listYunPayFragment.size(); j++) {
                        if (listYunPayFragment.get(j).ticket==CREADITS) {
                            listYunPayFragment.remove(j);
                            break;
                        }
                    }
                    brushYunPayFragmentData();
                    listener.yunPayFragment(listYunPayFragment);
                    mAdapter.notifyDataSetChanged();
                    mYun.setSelect(false);
                }
                break;
            /**抵用券1*/
            case COUPON1:
                if(Moneys.wfjr<=0){//未选中，添加
                    Toast.makeText(getActivity(),"您无需再付款了",Toast.LENGTH_SHORT).show();
                }else{

                }
                break;
        }

//        listener.yunPayFragment(position);
    }
    /**接口回调获取Dialog输入的金额*/
    @Override
    public void getDialogInput(String money) {
        mYun.setSelect(true);
        listYunPayFragment.add(new YunFu(mYun.getCardgrade(), "", mYun.getCredit1(), mYun.getCredit2(), Float.parseFloat(money), 0,mYun.getTicket()));
        listener.yunPayFragment(listYunPayFragment);

        brushYunPayFragmentData();

        mAdapter.notifyDataSetChanged();
        mDialog.cancel();
    }

    private void brushYunPayFragmentData() {
        float yf=0f;
        for (int i = 0; i < listYunPayFragment.size(); i++) {
            yf = yf + listYunPayFragment.get(i).money;
        }
        Moneys.yfjr=yf;
        Moneys.wfjr=Moneys.ysjr-Moneys.yfjr;
    }
}

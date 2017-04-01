package com.duowei.dw_pos.fragment;


import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import com.duowei.dw_pos.R;
import com.duowei.dw_pos.adapter.YunGvAdapter;
import com.duowei.dw_pos.bean.ImsCardMember;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class YunCardFragment extends Fragment implements AdapterView.OnItemClickListener {
    private ArrayList<ImsCardMember> mYunList;
    private GridView mGv;

    public YunCardFragment() {
        // Required empty public constructor
    }
    public GvClickListener listener;

    public interface GvClickListener{
        void sendMsg(int postion);
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
//        ImsCardMember imsCard = (ImsCardMember) bundle.getSerializable("cards");
//        if (imsCard .getCredit2() >= 0) {//储值消费
//            mYunList.add(new ImsCardMember(imsCard .getId(), imsCard .getFrom_user(), imsCard .getCardsn(),imsCard .getCredit1(), imsCard .getCredit2(),
//                    imsCard .getRealname(), imsCard .getMobile(), imsCard .getStatus(), imsCard .getCardgrade(), imsCard .getOccupation(),
//                    imsCard .getCreatetime(), imsCard .getTitle(), imsCard .getCouponmoney(), imsCard .getSL(),0));
//        }
//        if (imsCard .getCredit1() >= 0) {//积分消费
//            mYunList.add(new ImsCardMember(imsCard .getId(), imsCard .getFrom_user(), imsCard .getCardsn(), imsCard .getCredit1(), -1f,
//                    imsCard .getRealname(), imsCard .getMobile(), imsCard .getStatus(), imsCard .getCardgrade(), imsCard .getOccupation(),
//                    imsCard .getCreatetime(), imsCard .getTitle(), imsCard .getCouponmoney(), imsCard .getSL(),1));
//        }

        mGv = (GridView) inflate.findViewById(R.id.gridView);
        return inflate;
    }

    @Override
    public void onStart() {
        super.onStart();
        YunGvAdapter adapter = new YunGvAdapter(getActivity(),mYunList);
        mGv.setAdapter(adapter);
        mGv.setOnItemClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        listener.sendMsg(i);
    }
}

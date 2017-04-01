package com.duowei.dw_pos.fragment;


import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.duowei.dw_pos.R;
import com.duowei.dw_pos.adapter.YunListAdapter;
import com.duowei.dw_pos.bean.YunFu;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class YunPayFragment extends Fragment {
    public YunPayFragment() {
        // Required empty public constructor
    }
    ArrayList<YunFu>listYun=new ArrayList<>();
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View inflate = inflater.inflate(R.layout.fragment_yun_pay, container, false);
        listYun.add(new YunFu("","",0f,0f,0f,0,0));
        ListView lv = (ListView) inflate.findViewById(R.id.listView);
        YunListAdapter adapter = new YunListAdapter(getActivity(), listYun);
        lv.setAdapter(adapter);
        return inflate;
    }
}

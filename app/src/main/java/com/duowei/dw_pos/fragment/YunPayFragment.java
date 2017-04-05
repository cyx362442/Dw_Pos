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

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class YunPayFragment extends Fragment {

    public YunPayFragment() {
        // Required empty public constructor
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View inflate = inflater.inflate(R.layout.fragment_yun_pay, container, false);
        Bundle bundle = getArguments();
        List<YunFu> list = (List<YunFu>) bundle.getSerializable("list");
        ListView lv = (ListView) inflate.findViewById(R.id.listView);
        YunListAdapter adapter = new YunListAdapter(getActivity(), list);
        lv.setAdapter(adapter);
        return inflate;
    }
}

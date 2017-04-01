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

/**
 * A simple {@link Fragment} subclass.
 */
public class YunCardFragment extends Fragment implements AdapterView.OnItemClickListener {
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
        GridView gv = (GridView) inflate.findViewById(R.id.gridView);
        YunGvAdapter adapter = new YunGvAdapter(getActivity());
        gv.setAdapter(adapter);
        gv.setOnItemClickListener(this);
        return inflate;
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        listener.sendMsg(i);
    }
}

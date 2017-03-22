package com.duowei.dw_pos;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.duowei.dw_pos.adapter.ListAdapter;
import com.duowei.dw_pos.bean.JYCSSZ;

import org.litepal.crud.DataSupport;

import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class ListFragment extends Fragment {
    private List<JYCSSZ> mJycssz;
    public ListFragment() {
        // Required empty public constructor
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View inflate = inflater.inflate(R.layout.fragment_list, container, false);
        ListView lv = (ListView) inflate.findViewById(R.id.listView);
        mJycssz = DataSupport.select("CSMC").where("FCSBH=?", "").order("CSBH ASC").find(JYCSSZ.class);
        ListAdapter adapter = new ListAdapter(getActivity(), mJycssz);
        lv.setAdapter(adapter);
        return inflate;
    }
}

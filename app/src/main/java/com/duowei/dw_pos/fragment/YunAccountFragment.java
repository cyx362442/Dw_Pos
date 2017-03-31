package com.duowei.dw_pos.fragment;


import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.duowei.dw_pos.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class YunAccountFragment extends Fragment {
    public YunAccountFragment() {
        // Required empty public constructor
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View inflate = inflater.inflate(R.layout.fragment_yun_account, container, false);
        return inflate;
    }
}

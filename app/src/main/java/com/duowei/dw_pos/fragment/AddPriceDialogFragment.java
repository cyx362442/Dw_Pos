package com.duowei.dw_pos.fragment;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatDialogFragment;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

/**
 * 加价促销 窗口
 */

public class AddPriceDialogFragment extends AppCompatDialogFragment implements AdapterView.OnItemClickListener {

    private Context mContext;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getContext();
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        ListView listView = new ListView(mContext);
        listView.setOnItemClickListener(this);

        List<Integer> list = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            list.add(i);
        }

        listView.setAdapter(new ArrayAdapter<Integer>(mContext, android.R.layout.simple_list_item_1, list));

        return new AlertDialog.Builder(mContext)
                .setTitle("请选择单品")
                .setView(listView)
                .setNegativeButton("取消", null)
                .create();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        getDialog().dismiss();
    }
}

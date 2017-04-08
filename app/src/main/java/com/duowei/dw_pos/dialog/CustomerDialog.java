package com.duowei.dw_pos.dialog;

import android.content.Context;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.LinearLayout;

import com.duowei.dw_pos.R;
import com.duowei.dw_pos.adapter.CustomerAdapter;
import com.duowei.dw_pos.bean.GKLX;
import com.duowei.dw_pos.event.CustomerStytle;
import com.google.common.eventbus.EventBus;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017-04-08.
 */

public class CustomerDialog implements AdapterView.OnItemClickListener {
    Context context;
    private AlertDialog mDialog;
    private final LinearLayout mLayout;
    private final GridView mGv;
    private List<String> gkName = new ArrayList<>();
    public CustomerDialog(Context context) {
        gkName.clear();
        List<GKLX> gklx = DataSupport.findAll(GKLX.class);
        for(GKLX G:gklx){
            gkName.add(G.getGKLX());
        }

        this.context = context;
        mDialog = new AlertDialog.Builder(context).create();
        //必须先setView，否则在dialog\popuwindow中无法自动弹出软健盘
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mLayout = (LinearLayout) inflater.inflate(R.layout.customerstytle, null);
        mDialog.setView(mLayout);
        mDialog.show();
        WindowManager.LayoutParams params = mDialog.getWindow().getAttributes();
        params.height = 400 ;

        mDialog.getWindow().setAttributes(params);
        mGv = (GridView) mLayout.findViewById(R.id.gridView);
        initGridView(context);
        mGv.setOnItemClickListener(this);
    }

    private void initGridView(Context context) {
        CustomerAdapter adapter = new CustomerAdapter(context, gkName);
        mGv.setAdapter(adapter);
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
        String stytle = gkName.get(position);
        org.greenrobot.eventbus.EventBus.getDefault().post(new CustomerStytle(stytle));
        mDialog.dismiss();
    }
}

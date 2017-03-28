package com.duowei.dw_pos.dialog;

import android.content.Context;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;

import com.duowei.dw_pos.R;
import com.duowei.dw_pos.bean.SZLB;
import com.duowei.dw_pos.bean.WMLSB;
import com.duowei.dw_pos.event.OrderUpdateEvent;
import com.duowei.dw_pos.httputils.Post7;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017-03-27.
 */

public class SalesReturnDialog implements View.OnClickListener{
    Context context;
    private AlertDialog mDialog;
    private final LinearLayout mLayout;
    public Button mConfirm;
    private Button mCancel;
    private Spinner mSpinner;
    private String sql;
    private WMLSB wmlsb;
    private ArrayList<String>list=new ArrayList<>();
    public SalesReturnDialog(Context context, String sql,WMLSB wmlsb) {
        this.context = context;
        this.sql=sql;
        this.wmlsb=wmlsb;
        mDialog = new AlertDialog.Builder(context).create();
        //必须先setView，否则在dialog\popuwindow中无法自动弹出软健盘
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mLayout = (LinearLayout) inflater.inflate(R.layout.salereturn_item, null);
        mDialog.setView(mLayout);
        mDialog.show();
        WindowManager.LayoutParams params = mDialog.getWindow().getAttributes();
        params.width = 550;
        params.height = 400 ;
        mDialog.getWindow().setAttributes(params);
        List<SZLB> szlb = DataSupport.findAll(SZLB.class);
        list.clear();
        list.add("无……");
        for(SZLB S:szlb){
            list.add(S.getSZBM());
        }
        initWidget();
    }
    private void initWidget() {
        mSpinner = (Spinner) mLayout.findViewById(R.id.spinner_return);
        mConfirm=(Button)mLayout.findViewById(R.id.btn_confirm);
        mCancel=(Button)mLayout.findViewById(R.id.btn_cancel);
        mConfirm.setOnClickListener(this);
        mCancel.setOnClickListener(this);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_item, list);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpinner.setAdapter(adapter);
    }
    public void cancel(){
        mDialog.dismiss();
    }
    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btn_cancel:
                mDialog.dismiss();
                break;
            case R.id.btn_confirm:
//                org.greenrobot.eventbus.EventBus.getDefault().post(new OrderUpdateEvent("确定"));
                Post7.getInstance().getHttpResult(sql,wmlsb);
                cancel();
                break;
        }
    }
}

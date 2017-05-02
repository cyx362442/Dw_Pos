package com.duowei.dw_pos.dialog;

import android.content.Context;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.duowei.dw_pos.R;

/**
 * Created by Administrator on 2017-05-02.
 */

public class ConfirmDialog implements View.OnClickListener {
    private static ConfirmDialog confirmDialog=null;
    private ConfirmDialog(){}
    public static ConfirmDialog instance(){
        if(confirmDialog==null){
            confirmDialog=new ConfirmDialog();
        }
        return confirmDialog;
    }

    Context context;
    private AlertDialog mDialog;
    private  LinearLayout mLayout;
    public OnconfirmClick listener;
    public interface OnconfirmClick{
        void confirmListener();
    }
    public void setOnconfirmClick(OnconfirmClick listener){
        this.listener=listener;
    }
    public void show(Context context, String msg){
        this.context = context;
        mDialog = new AlertDialog.Builder(context).create();
        //必须先setView，否则在dialog\popuwindow中无法自动弹出软健盘
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mLayout = (LinearLayout) inflater.inflate(R.layout.confirm_item, null);
        mDialog.setView(mLayout);
        mDialog.show();
        WindowManager.LayoutParams params = mDialog.getWindow().getAttributes();
        mDialog.getWindow().setAttributes(params);
        TextView tvMsg = (TextView) mLayout.findViewById(R.id.tv_msg);
        tvMsg.setText(msg);
        mLayout.findViewById(R.id.btn_cancel).setOnClickListener(this);
        mLayout.findViewById(R.id.btn_confirm).setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btn_cancel:
                mDialog.dismiss();
                break;
            case R.id.btn_confirm:
                listener.confirmListener();
                break;
        }
    }
    public void cancel(){
        mDialog.dismiss();
    }
}

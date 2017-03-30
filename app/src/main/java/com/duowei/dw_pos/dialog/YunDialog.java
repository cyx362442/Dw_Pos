package com.duowei.dw_pos.dialog;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.duowei.dw_pos.R;
import com.duowei.dw_pos.summiscan.ScanActivity;

/**
 * Created by Administrator on 2017-03-30.
 */

public class YunDialog implements View.OnClickListener {
    Context context;
    private AlertDialog mDialog;
    private final LinearLayout mLayout;
    private  EditText mPhone;
    private  EditText mPassword;

    public YunDialog(Context context) {
        this.context = context;
        mDialog = new AlertDialog.Builder(context).create();
        //必须先setView，否则在dialog\popuwindow中无法自动弹出软健盘
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mLayout = (LinearLayout) inflater.inflate(R.layout.yundialog_item, null);
        mDialog.setView(mLayout);
        mDialog.show();
        WindowManager.LayoutParams params = mDialog.getWindow().getAttributes();
        params.width = 550;
        params.height = 500;
        mDialog.getWindow().setAttributes(params);
        initUi();
    }

    private void initUi() {
        mPhone = (EditText) mLayout.findViewById(R.id.et_phone);
        mPassword = (EditText) mLayout.findViewById(R.id.et_password);
        mLayout.findViewById(R.id.btn_cancel).setOnClickListener(this);
        mLayout.findViewById(R.id.btn_confirm).setOnClickListener(this);
        mLayout.findViewById(R.id.btn_shama).setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btn_cancel:
                mDialog.dismiss();
                break;
            case R.id.btn_confirm:
                break;
            case R.id.btn_shama:
                Intent intent = new Intent(context, ScanActivity.class);
                context.startActivity(intent);
                cancel();
                break;
        }
    }
    public void cancel(){
        mDialog.dismiss();
    }
}

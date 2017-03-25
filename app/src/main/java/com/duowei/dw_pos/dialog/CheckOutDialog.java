package com.duowei.dw_pos.dialog;

import android.content.Context;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.duowei.dw_pos.R;

/**
 * Created by Administrator on 2017-03-25.
 */

public class CheckOutDialog implements View.OnClickListener {
    Context context;
    String title;
    private AlertDialog mDialog;
    public EditText mEtInput;
    public Button mConfirm;
    private Button mCancel;
    private final LinearLayout mLayout;
    public TextView mTitle;
    public CheckOutDialog(Context context, String title) {
        this.context = context;
        this.title = title;
        mDialog = new AlertDialog.Builder(context).create();
        //必须先setView，否则在dialog\popuwindow中无法自动弹出软健盘
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mLayout = (LinearLayout) inflater.inflate(R.layout.dialog_item, null);
        mDialog.setView(mLayout);
        mDialog.show();
        WindowManager.LayoutParams params = mDialog.getWindow().getAttributes();
        params.width = 500;
        params.height = 400 ;
        mDialog.getWindow().setAttributes(params);
        initWidget();
    }

    private void initWidget() {
        mTitle=(TextView)mLayout.findViewById(R.id.tv_title);
        mEtInput=(EditText)mLayout.findViewById(R.id.et_input);
        mConfirm=(Button)mLayout.findViewById(R.id.btn_cancel);
        mCancel=(Button)mLayout.findViewById(R.id.btn_confirm);
        mCancel.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
       switch (view.getId()){
           case R.id.btn_cancel:
               mDialog.dismiss();
               break;
       }
    }
}

package com.duowei.dw_pos.dialog;

import android.content.Context;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.duowei.dw_pos.R;

/**
 * Created by Administrator on 2017-05-11.
 */

public class NumInputDialog implements View.OnClickListener {
    Context context;
    private AlertDialog mDialog;
    public EditText mEtInput;
    public Button mConfirm;
    private Button mCancel;
    private LinearLayout mLayout;
    public TextView mTitle;

    public CheckOutDialog.OnconfirmClick listener;

    public interface OnconfirmClick{
        void getDialogInput(String money);
    }

    public void setOnconfirmClick(CheckOutDialog.OnconfirmClick listener){
        this.listener=listener;
    }
    public NumInputDialog(Context context,String title,String content,int num){
        this.context=context;
        mDialog = new AlertDialog.Builder(context).create();
        //必须先setView，否则在dialog\popuwindow中无法自动弹出软健盘
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mLayout = (LinearLayout) inflater.inflate(R.layout.dialog_quick_item, null);
        mDialog.setView(mLayout);
        mDialog.show();
        WindowManager.LayoutParams params = mDialog.getWindow().getAttributes();
        mDialog.getWindow().setAttributes(params);
        initWidget(title,content,num);
    }
    public void cancel(){
        mDialog.dismiss();
    }

    private void initWidget(String title,String content,int num) {
        mTitle=(TextView)mLayout.findViewById(R.id.tv_title);
        mTitle.setText(title);
        mEtInput=(EditText)mLayout.findViewById(R.id.et_input);
        mEtInput.setText(num+"");
        TextView tvContent = (TextView) mLayout.findViewById(R.id.tv1);
        tvContent.setText(content);
        mConfirm=(Button)mLayout.findViewById(R.id.btn_confirm);
        mCancel=(Button)mLayout.findViewById(R.id.btn_cancel);
        mConfirm.setOnClickListener(this);
        mCancel.setOnClickListener(this);
        mTitle.setFocusableInTouchMode(true);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btn_cancel:
                mDialog.dismiss();
                break;
            case R.id.btn_confirm:
                listener.getDialogInput(mEtInput.getText().toString().trim());
                break;
        }
    }
}

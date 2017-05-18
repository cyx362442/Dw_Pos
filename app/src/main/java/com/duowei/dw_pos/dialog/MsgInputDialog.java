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

import java.math.BigDecimal;

/**
 * Created by Administrator on 2017-05-18.
 */

public class MsgInputDialog implements View.OnClickListener {
    Context context;
    String title;
    String content;
    private TextView mContents;

    private AlertDialog mDialog;
    public EditText mEtInput;
    public Button mConfirm;
    private Button mCancel;
    private final LinearLayout mLayout;
    public TextView mTitle;

    public OnconfirmClick listener;

    public interface OnconfirmClick{
        void getDialogInput(String contents);
    }

    public void setOnconfirmClick(OnconfirmClick listener){
        this.listener=listener;
    }



    public MsgInputDialog(Context context, String title, String content) {
        this.context = context;
        this.title = title;
        this.content=content;
        mDialog = new AlertDialog.Builder(context).create();
        //必须先setView，否则在dialog\popuwindow中无法自动弹出软健盘
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mLayout = (LinearLayout) inflater.inflate(R.layout.dialog_item, null);
        mDialog.setView(mLayout);
        mDialog.show();
        WindowManager.LayoutParams params = mDialog.getWindow().getAttributes();
        mDialog.getWindow().setAttributes(params);
        initWidget();
    }

    public void cancel(){
        mDialog.dismiss();
    }

    private void initWidget() {
        mTitle=(TextView)mLayout.findViewById(R.id.tv_title);
        mContents = (TextView) mLayout.findViewById(R.id.tv1);
        mEtInput=(EditText)mLayout.findViewById(R.id.et_input);
        mConfirm=(Button)mLayout.findViewById(R.id.btn_confirm);
        mCancel=(Button)mLayout.findViewById(R.id.btn_cancel);
        mConfirm.setOnClickListener(this);
        mCancel.setOnClickListener(this);
        mTitle.setText(title);
        mContents.setText(content);
        mTitle.setFocusableInTouchMode(true);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btn_cancel:
                mDialog.dismiss();
                break;
            case R.id.btn_confirm:
                String trim = mEtInput.getText().toString().trim();
                if(trim.isEmpty()){
                    return;
                }
                listener.getDialogInput(trim);
                break;
        }
    }
    public  Float bigDecimal(Float f){
        return BigDecimal.valueOf(f).setScale(1, BigDecimal.ROUND_HALF_UP).floatValue();
    }
}

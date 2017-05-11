package com.duowei.dw_pos.dialog;

import android.content.Context;
import android.support.annotation.IdRes;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.duowei.dw_pos.R;

/**
 * Created by Administrator on 2017-05-11.
 */

public class NumInputDialog implements View.OnClickListener, RadioGroup.OnCheckedChangeListener {
    Context context;
    private AlertDialog mDialog;
    public EditText mEtInput;
    public Button mConfirm;
    private Button mCancel;
    private LinearLayout mLayout;
    public String orderStytle="";

    public OnconfirmClick listener;
    private RadioButton mRb1;
    private RadioButton mRb2;
    private RadioButton mRb3;


    public interface OnconfirmClick{
        void getDialogInput(String tableNum,String orderStytle);
    }

    public void setOnconfirmClick(OnconfirmClick listener){
        this.listener=listener;
    }
    public NumInputDialog(Context context,int num){
        this.context=context;
        mDialog = new AlertDialog.Builder(context).create();
        //必须先setView，否则在dialog\popuwindow中无法自动弹出软健盘
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mLayout = (LinearLayout) inflater.inflate(R.layout.dialog_quick_item, null);
        mDialog.setView(mLayout);
        mDialog.show();
        WindowManager.LayoutParams params = mDialog.getWindow().getAttributes();
        mDialog.getWindow().setAttributes(params);
        initWidget(num);
    }
    public void cancel(){
        mDialog.dismiss();
    }

    private void initWidget(int num) {
        mEtInput=(EditText)mLayout.findViewById(R.id.et_input);
        mEtInput.setText(num+"");
        mConfirm=(Button)mLayout.findViewById(R.id.btn_confirm);
        mCancel=(Button)mLayout.findViewById(R.id.btn_cancel);
        mConfirm.setOnClickListener(this);
        mCancel.setOnClickListener(this);
        RadioGroup rg = (RadioGroup) mLayout.findViewById(R.id.rg);
        rg.setOnCheckedChangeListener(this);
        mRb1 = (RadioButton) mLayout.findViewById(R.id.rb1);
        mRb2 = (RadioButton) mLayout.findViewById(R.id.rb2);
        mRb3 = (RadioButton) mLayout.findViewById(R.id.rb3);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btn_cancel:
                mDialog.dismiss();
                break;
            case R.id.btn_confirm:
                listener.getDialogInput(mEtInput.getText().toString().trim(),orderStytle);
                break;
        }
    }

    @Override
    public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
        if(checkedId==mRb1.getId()){
            orderStytle="堂食";
        }else if(checkedId==mRb2.getId()){
            orderStytle="打包";
        }else if(checkedId==mRb3.getId()){
            orderStytle="外送";
        }
   }
}

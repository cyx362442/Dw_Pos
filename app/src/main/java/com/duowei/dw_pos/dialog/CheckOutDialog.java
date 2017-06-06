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
import java.util.Locale;

/**
 * Created by Administrator on 2017-03-25.
 */

public class CheckOutDialog implements View.OnClickListener {
    Context context;
    float money;
    private AlertDialog mDialog;
    public EditText mEtInput;
    public Button mConfirm;
    private Button mCancel;
    private final LinearLayout mLayout;

    public OnconfirmClick listener;

    public interface OnconfirmClick{
        void getDialogInput(String money);
    }

    public void setOnconfirmClick(OnconfirmClick listener){
        this.listener=listener;
    }

    public CheckOutDialog(Context context, String title,String contents,float money) {
        this.context = context;
        this.money=money;
        mDialog = new AlertDialog.Builder(context).create();
        //必须先setView，否则在dialog\popuwindow中无法自动弹出软健盘
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mLayout = (LinearLayout) inflater.inflate(R.layout.dialog_item, null);
        mDialog.setView(mLayout);
        mDialog.show();
        WindowManager.LayoutParams params = mDialog.getWindow().getAttributes();
        mDialog.getWindow().setAttributes(params);
        initWidget(title,contents);
    }

    public void cancel(){
        mDialog.dismiss();
    }

    private void initWidget(String title,String contents) {
        TextView tvTitle = (TextView) mLayout.findViewById(R.id.tv_title);
        TextView tvContent = (TextView) mLayout.findViewById(R.id.tv1);
        mEtInput=(EditText)mLayout.findViewById(R.id.et_input);
        mConfirm=(Button)mLayout.findViewById(R.id.btn_confirm);
        mCancel=(Button)mLayout.findViewById(R.id.btn_cancel);
        mConfirm.setOnClickListener(this);
        mCancel.setOnClickListener(this);
        tvTitle.setText(title);
        tvTitle.setFocusableInTouchMode(true);
        tvContent.setText(contents);
        mEtInput.setText(bigDecimal(money)+"");
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
    public  Float bigDecimal(Float f){
        return BigDecimal.valueOf(f).setScale(1, BigDecimal.ROUND_HALF_UP).floatValue();
    }
}

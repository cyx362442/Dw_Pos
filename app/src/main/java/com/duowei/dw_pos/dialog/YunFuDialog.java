package com.duowei.dw_pos.dialog;

import android.content.Context;
import android.support.v7.app.AlertDialog;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.duowei.dw_pos.R;

import java.util.Locale;

/**
 * Created by Administrator on 2017-04-02.
 */

public class YunFuDialog implements View.OnClickListener{
    Context context;
    String title;
    String content;
    float money;
    float couponMoney=1f;//电子券面值
    int couponCount;
    private TextView mContents;

    public void setCouponCount(int couponCount) {
        this.couponCount = couponCount;
    }

    int payStytle;
    private AlertDialog mDialog;
    public EditText mEtInput;
    public Button mConfirm;
    private Button mCancel;
    private final LinearLayout mLayout;
    public TextView mTitle;

    public void setCouponMoney(float couponMoney) {
        this.couponMoney = couponMoney;
    }

    public OnconfirmClick listener;

    public interface OnconfirmClick{
        void getDialogInput(String money,int payStytle);
    }

    public void setOnconfirmClick(OnconfirmClick listener){
        this.listener=listener;
    }



    public YunFuDialog(Context context, String title,String content,float money,int payStytle) {
        this.context = context;
        this.title = title;
        this.content=content;
        this.money=money;
        this.payStytle=payStytle;
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
        if(title.equals("储值卡消费")){
            mEtInput.setText(String.format(Locale.CANADA, "%.2f", money));
        }else if(title.equals("电子券消费")){
            mEtInput.setText((int)money+"");
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btn_cancel:
                mDialog.dismiss();
                break;
            case R.id.btn_confirm:
                String trim = mEtInput.getText().toString().trim();
                /**储值卡*/
                if(payStytle==0){
                    if(Float.parseFloat(trim)>money){
                        Toast.makeText(context,"输入金额过大",Toast.LENGTH_SHORT).show();
                    }else{
                        listener.getDialogInput(trim,payStytle);
                    }
                    /**电子券*/
                }else if(payStytle>=2){
                    if(Float.parseFloat(trim)>couponCount){
                        Toast.makeText(context,"电子券张数不足",Toast.LENGTH_SHORT).show();
                    }else{
                        listener.getDialogInput(Float.parseFloat(trim)+"",payStytle);
                    }
                }
                break;
        }
    }
}

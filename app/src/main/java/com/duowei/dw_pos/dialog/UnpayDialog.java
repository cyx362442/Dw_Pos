package com.duowei.dw_pos.dialog;

import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.duowei.dw_pos.R;
import com.duowei.dw_pos.bean.Moneys;
import com.duowei.dw_pos.httputils.DownHTTP;
import com.duowei.dw_pos.tools.Net;


/**
 * Created by Administrator on 2017-05-12.
 */

public class UnpayDialog implements View.OnClickListener, CheckOutDialog.OnconfirmClick {
    Context context;
    String sqlYun;
    String sqlLocal;
    private AlertDialog mDialog;
    private LinearLayout mLayout;
    private CheckOutDialog mCheckOutDialog;

    public UnpayDialog(Context context,String sqlYun,String sqlLocal) {
        this.context = context;
        this.sqlYun=sqlYun;
        this.sqlLocal=sqlLocal;
        mDialog = new AlertDialog.Builder(context).create();
        //必须先setView，否则在dialog\popuwindow中无法自动弹出软健盘
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mLayout = (LinearLayout) inflater.inflate(R.layout.yun_unpay_item, null);
        mDialog.setView(mLayout);
        mDialog.show();
        WindowManager.LayoutParams params = mDialog.getWindow().getAttributes();
        mDialog.getWindow().setAttributes(params);

        TextView title = (TextView) mLayout.findViewById(R.id.tv_title);
        title.setText("您还有￥"+ Moneys.wfjr+"未付，是否使用其它付款方式？");
        mLayout.findViewById(R.id.btn_close).setOnClickListener(this);
        mLayout.findViewById(R.id.btn_cash).setOnClickListener(this);
        mLayout.findViewById(R.id.btn_zfb).setOnClickListener(this);
        mLayout.findViewById(R.id.btn_wx).setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btn_close:
                mDialog.dismiss();
                break;
            case R.id.btn_cash:
                mCheckOutDialog = new CheckOutDialog(context, "现金支付", Moneys.wfjr);
                mCheckOutDialog.setOnconfirmClick(this);
                mDialog.dismiss();
                break;
            case R.id.btn_zfb:
                break;
            case R.id.btn_wx:
                break;
        }
    }
    public void dialogCancel(){
        mDialog.dismiss();
    }

    @Override
    public void getDialogInput(String money) {
        new MyAsync().execute();
        mCheckOutDialog.cancel();
    }
    class MyAsync extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... strings) {
            String result = DownHTTP.postResult(Net.yunUrl, "7", sqlYun);
            return result;
        }
        @Override
        protected void onPostExecute(String result) {
            if (result.contains("richado")) {
//                EventBus.getDefault().post(new YunSqlFinish(mSqlLocal,mListWmlsb,mWmlsbjb,listYunPayFragment));
//                getActivity().finish();
            }else{
//                mConfirm.setEnabled(true);
//                EventBus.getDefault().post(new YunSubmitFail());
//                Toast.makeText(getActivity(),"数据提交失败",Toast.LENGTH_SHORT).show();
            }
            super.onPostExecute(result);
        }
    }
}

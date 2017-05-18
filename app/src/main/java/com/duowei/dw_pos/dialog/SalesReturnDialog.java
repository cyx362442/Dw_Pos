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
import com.duowei.dw_pos.bean.Wmslbjb_jiezhang;
import com.duowei.dw_pos.event.OrderUpdateEvent;
import com.duowei.dw_pos.httputils.Post7;
import com.duowei.dw_pos.tools.CartList;
import com.duowei.dw_pos.tools.OrderList;
import com.duowei.dw_pos.tools.Users;

import org.greenrobot.eventbus.EventBus;
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
    private WMLSB wmlsb;
    private Wmslbjb_jiezhang wmlsbjb;
    private ArrayList<String>list=new ArrayList<>();
    public SalesReturnDialog(Context context,WMLSB wmlsb,Wmslbjb_jiezhang wmlsbjb) {
        this.context = context;
        this.wmlsb=wmlsb;
        this.wmlsbjb=wmlsbjb;
        mDialog = new AlertDialog.Builder(context).create();
        //必须先setView，否则在dialog\popuwindow中无法自动弹出软健盘
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mLayout = (LinearLayout) inflater.inflate(R.layout.salereturn_item, null);
        mDialog.setView(mLayout);
        mDialog.show();
        WindowManager.LayoutParams params = mDialog.getWindow().getAttributes();
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
                OrderList.newInstance().remove(wmlsb);
                String sql = OrderList.newInstance().getSql();
                //插入平板打印信息
                String str = (String) mSpinner.getSelectedItem();
                String insertPBDYXXB = "insert into pbdyxxb(xh,wmdbh,xmbh,xmmc,dw,sl,dj,xj,pz,ysjg,syyxm,sfxs,tcbh,tcxmbh,tcfz,xtbz,czsj,zh,jsj,thyy) " +
                        "select xh,wmdbh,xmbh,xmmc,dw,'1',dj,'" + wmlsb.getDJ() + "',pz,ysjg,syyxm,sfxs,tcbh,tcxmbh,BY15,'2',getdate(),'" +wmlsbjb.getZH()+ "','" + Users.pad + "','" + str + "'  " +
                        "from wmlsb where wmdbh='" + wmlsb.getWMDBH() + "' and isnull(sfyxd,'0')='1'and  XH='" + wmlsb.getXH() + "'|";
                Post7.getInstance().getHttpResult(sql +insertPBDYXXB,wmlsb);
                cancel();
                break;
        }
    }
}

package com.duowei.dw_pos.fragment;


import android.app.Activity;
import android.app.Fragment;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.duowei.dw_pos.R;
import com.duowei.dw_pos.adapter.YunGvAdapter;
import com.duowei.dw_pos.bean.ImsCardMember;
import com.duowei.dw_pos.bean.JFGZSZ;
import com.duowei.dw_pos.bean.Moneys;
import com.duowei.dw_pos.bean.WMLSB;
import com.duowei.dw_pos.bean.WXFWQDZ;
import com.duowei.dw_pos.bean.Wmslbjb_jiezhang;
import com.duowei.dw_pos.bean.YunFu;
import com.duowei.dw_pos.dialog.YunFuDialog;
import com.duowei.dw_pos.event.YunSqlFinish;
import com.duowei.dw_pos.httputils.DownHTTP;
import com.duowei.dw_pos.httputils.VolleyResultListener;
import com.duowei.dw_pos.tools.Net;
import com.duowei.dw_pos.tools.SqlYun;
import com.google.gson.Gson;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONArray;
import org.json.JSONException;
import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class YunCardFragment extends Fragment implements AdapterView.OnItemClickListener, YunFuDialog.OnconfirmClick, View.OnClickListener {

    /**插入MySql汇总语句*/
    private String sqlCZXF="";//储值消费
    private String sqlJFXF="";//积分消费
    private String sqlJYQ1="";//抵用券
    private String sqlJYQ2="";//抵用券
    private String sqlJYQ3="";//抵用券
    private String sqlJYQ4="";//抵用券
    private String sqlJYQ5="";//抵用券
    private String mSqlYun="";//汇总
    /**插入sql server汇总语句*/
    private String sqlXSFKFS1="";
    private String sqlXSFKFS2="";
    //抵用券
    private String sqlXSFKFS31="";
    private String sqlXSFKFS32="";
    private String sqlXSFKFS33="";
    private String sqlXSFKFS34="";
    private String sqlXSFKFS35="";
    private String sqlCZKJYMXXX="";
    private String mSqlLocal="";
    private String mSql1Deal_record="";

    private ArrayList<ImsCardMember> mYunList;
    private List<YunFu> listYunPayFragment = new ArrayList<>();
    private GridView mGv;
    private ImsCardMember mYun;
    private JFGZSZ mJfgzsz;
    private YunFuDialog mDialog;
    private YunGvAdapter mAdapter;
    private ArrayList<WMLSB> mListWmlsb;
    /**付款方式*/
    private final int PETCARD=0;
    private final int CREADITS=1;
    //抵用券
    private final int COUPON1=2;
    private final int COUPON2=3;
    private final int COUPON3=4;
    private final int COUPON4=5;
    private final int COUPON5=6;
    private int mWeid;
    private String mJysj;
    private Wmslbjb_jiezhang mWmlsbjb;
    private String mBmbh;
    private String mDeal_id;
    private int mJfbfb;//获得积分
    private int by3=0;//总需积分;
    private Button mConfirm;

    public YunCardFragment() {
        // Required empty public constructor
    }
    public GvClickListener listener;


    public interface GvClickListener{
        void yunPayFragment(List<YunFu> listPay);
    }
    @Override
    public void onAttach(Activity activity) {
        listener=(GvClickListener) activity;
        super.onAttach(activity);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View inflate = inflater.inflate(R.layout.fragment_yun_card, container, false);
        Bundle bundle = getArguments();
        mYunList = (ArrayList<ImsCardMember>) bundle.getSerializable("cards");
        mListWmlsb = (ArrayList<WMLSB>) bundle.getSerializable("WMLSB");
        mWmlsbjb = (Wmslbjb_jiezhang) bundle.getSerializable("mWmlsbjb");

        WXFWQDZ wxfwqdz = DataSupport.select("weid", "bmbh").findFirst(WXFWQDZ.class);
        mWeid = wxfwqdz.getWeid();
        mBmbh = wxfwqdz.getBMBH();

        mGv = (GridView) inflate.findViewById(R.id.gridView);
        mConfirm = (Button) inflate.findViewById(R.id.btn_confirm);
        mConfirm.setOnClickListener(this);
        inflate.findViewById(R.id.btn_cancel).setOnClickListener(this);
        return inflate;
    }

    @Override
    public void onStart() {
        super.onStart();
        //积分兑换规则
        Http_JFGZSZ();
        //当前服务器时间
        Http_WMLSB();
        //存储过程获取mDeal_id
        Http_AADBPRK();

        mAdapter = new YunGvAdapter(getActivity(),mYunList);
        mGv.setAdapter(mAdapter);
        mGv.setOnItemClickListener(this);
    }
    /**获取各分兑换规则*/
    private void Http_JFGZSZ() {
        String sql="select id,weid,jfgz,jfbfb,jfly from ims_jfgzsz where weid="+mWeid+"|";
        DownHTTP.postVolley6(Net.yunUrl,sql, new VolleyResultListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
            }
            @Override
            public void onResponse(String s) {
                Gson gson = new Gson();
                JFGZSZ[] jfgzszs =  gson.fromJson(s, JFGZSZ[].class);
                mJfgzsz =  jfgzszs[0];
            }
        });
    }
    /**获取服务器当前时间*/
    private void Http_WMLSB() {
        String sql="SELECT convert(varchar(30),getdate(),121) ZSSJ2|";
       DownHTTP.postVolley6(Net.url, sql, new VolleyResultListener() {
           @Override
           public void onErrorResponse(VolleyError error) {
           }
           @Override
           public void onResponse(String response) {
               try {
                   JSONArray jsonArray = new JSONArray(response);
                   mJysj=jsonArray.getJSONObject(0).getString("ZSSJ2");
               } catch (JSONException e) {
                   e.printStackTrace();
               }
           }
       });
    }

    private void Http_AADBPRK() {
        String sql="call prc_AADBPRK_001('deal_record',1)|";
        DownHTTP.postVolley6(Net.yunUrl,sql,new VolleyResultListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
            }
            @Override
            public void onResponse(String s) {
                try {
                    JSONArray jsonArray = new JSONArray(s);
                    mDeal_id =  jsonArray.getJSONObject(0).getString("table_key");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
        mYun = mYunList.get(position);
        switch (mYun.getTicket()){
            /**储值消费*/
            case PETCARD:
                if(mYun.isSelect()==false){//未选中，添加
                    if(Moneys.wfjr<=0){
                        Toast.makeText(getActivity(),"您无需再付款了",Toast.LENGTH_SHORT).show();
                    }else{
                        //储值卡余额大于未付金额，取未付金额，否则取储值卡余额
                        float money=Moneys.wfjr< mYun.getCredit2()?Moneys.wfjr: mYun.getCredit2();
                        mDialog = new YunFuDialog(getActivity(), "储值卡消费","金额： ", money,PETCARD);
                        mDialog.setOnconfirmClick(this);
                    }
                }else{//己选过，删除
                    for (int j = 0; j < listYunPayFragment.size(); j++) {
                        if (listYunPayFragment.get(j).ticket==PETCARD) {
                            listYunPayFragment.remove(j);
                            break;
                        }
                    }
                    brushYunPayFragmentData();
                    listener.yunPayFragment(listYunPayFragment);
                    mAdapter.notifyDataSetChanged();
                    mYun.setSelect(false);

                    sqlCZXF="";
                    sqlXSFKFS1="";
                    sqlCZKJYMXXX="";
                }
                break;
                /**积分消费*/
            case CREADITS:
                float money=0;
                by3=0;//总需积分
                float minBy3=1000000000;//积分最小的一项
                if(mYun.isSelect()==false){//未选中，添加
                    if(Moneys.wfjr<=0){
                        Toast.makeText(getActivity(),"您无需再付款了",Toast.LENGTH_SHORT).show();
                    }else{
                        for(WMLSB wmlsb:mListWmlsb){
                            if(wmlsb.getBY3()>0){//兑换该单品所需积分
                                by3= (int)(by3+(wmlsb.getBY3())*wmlsb.getSL());//总积分
                                money=money+wmlsb.getXJ();//所有积分抵扣的金额
//                                mListWmlsb2.add(wmlsb);
                                if(minBy3>wmlsb.getBY3()*wmlsb.getSL()){//获取最小积分
                                    minBy3=wmlsb.getBY3()*wmlsb.getSL();
                                }
                            }
                        }
                        if(minBy3>mYun.getCredit1()){//现有积分都不足以抵扣最小的积分商品
                            Toast.makeText(getActivity(),"没有可抵扣积分的商品或现有积分不足",Toast.LENGTH_SHORT).show();
                            return;
                        }
                        if(minBy3<mYun.getCredit1()&&by3>mYun.getCredit1()){//现有积分够抵扣其中一项商品，但不足抵扣所有商品
//                            mLl_jinfen.setVisibility(View.VISIBLE);
//                            mTv_jinfenCurrent.setText(mYun.getCredit1()+"");
//                            mGvJinfen =  new GvJinfen(YunFuActivity.this, list_wmlsb);
//                            mGv_jinfen.setAdapter(mGvJinfen);
                        }else if(by3>0&&by3<mYun.getCredit1()){//可用积分大于兑换积分和,直接抵扣
                            money=money>Moneys.wfjr?Moneys.wfjr:money;
                            listYunPayFragment.add(new YunFu(mYun.getId(),mYun.getFrom_user(),mYun.getCardsn(),mYun.getCardgrade(), "积分消费", mYun.getCredit1(), mYun.getCredit2(), money, 0,mYun.getTicket()));
                            listener.yunPayFragment(listYunPayFragment);
                            brushYunPayFragmentData();
                            mAdapter.notifyDataSetChanged();
                            mYun.setSelect(true);
                        }
                    }
                }else{//己选中，删除
                    for (int j = 0; j < listYunPayFragment.size(); j++) {
                        if (listYunPayFragment.get(j).ticket==CREADITS) {
                            listYunPayFragment.remove(j);
                            break;
                        }
                    }
                    brushYunPayFragmentData();
                    listener.yunPayFragment(listYunPayFragment);
                    mAdapter.notifyDataSetChanged();
                    mYun.setSelect(false);
                }
                break;
            /**抵用券1*/
            case COUPON1:
                click_Coupon(COUPON1);
                break;
            /**抵用券2*/
            case COUPON2:
                click_Coupon(COUPON2);
                break;
            /**抵用券3*/
            case COUPON3:
                click_Coupon(COUPON3);
                break;
            /**抵用券4*/
            case COUPON4:
                click_Coupon(COUPON4);
                break;
            /**抵用券5*/
            case COUPON5:
                click_Coupon(COUPON5);
                break;
        }
//        listener.yunPayFragment(position);
    }
    /**各抵用券点击事件*/
    private void click_Coupon(int couponNum) {
        if (mYun.isSelect() == false) {//未选过，添加
            if(Moneys.wfjr<=0){
                Toast.makeText(getActivity(),"您无需再付款了",Toast.LENGTH_SHORT).show();
                return;
            }
            mDialog = new YunFuDialog(getActivity(), "电子券消费", "张数：", 1, couponNum);
            mDialog.setCouponMoney(mYun.getCouponmoney());//面值
            mDialog.setCouponCount(mYun.getSL());//数量
            mDialog.setOnconfirmClick(this);
        } else {//己选过，删除
            for (int j = 0; j < listYunPayFragment.size(); j++) {
                if (listYunPayFragment.get(j).ticket == couponNum) {
                    listYunPayFragment.remove(j);
                    break;
                }
            }
            brushYunPayFragmentData();
            listener.yunPayFragment(listYunPayFragment);
            mAdapter.notifyDataSetChanged();
            mYun.setSelect(false);
        }
    }

    /**接口回调获取Dialog输入的金额*/
    @Override
    public void getDialogInput(String msg,int payStyte) {
        mYun.setSelect(true);
        float inputMoney = Float.parseFloat(msg);
        int inputNum = (int) inputMoney;
        /**储值卡消费*/
        if(payStyte==0){
            listYunPayFragment.add(new YunFu(mYun.getId(),mYun.getFrom_user(),mYun.getCardsn(),mYun.getCardgrade(), "储值消费", mYun.getCredit1(), mYun.getCredit2(), inputMoney, 0,mYun.getTicket()));
        }
        /**电子券*/
        else if(payStyte>=2){
            //电子券扣掉的金额大于未付金额，取未付金额；否则，取实际扣掉的电子券金额；
            Float money=inputNum*mYun.getCouponmoney()>Moneys.wfjr?Moneys.wfjr:inputNum*mYun.getCouponmoney();
            listYunPayFragment.add(new YunFu(mYun.getId(),mYun.getFrom_user(),mYun.getCardsn(),mYun.getCardgrade(), mYun.getTitle(), mYun.getCredit1(), mYun.getCredit2(), money, inputNum,mYun.getTicket()));
        }

        brushYunPayFragmentData();
        mAdapter.notifyDataSetChanged();
        listener.yunPayFragment(listYunPayFragment);
        mDialog.cancel();
    }

    private void brushYunPayFragmentData() {
        float yf=0f;
        for (int i = 0; i < listYunPayFragment.size(); i++) {
            yf = yf + listYunPayFragment.get(i).money;
        }
        Moneys.yfjr=yf;
        Moneys.wfjr=Moneys.ysjr-Moneys.yfjr;
    }
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_confirm:
                if(listYunPayFragment.size()<=0){
                    Toast.makeText(getActivity(),"请选择付款方式",Toast.LENGTH_SHORT).show();
                    return;
                }else if(Moneys.wfjr>0){
                    Toast.makeText(getActivity(),"金额不足",Toast.LENGTH_SHORT).show();
                    return;
                }
                mConfirm.setEnabled(false);
                for(int i=0;i<listYunPayFragment.size();i++){
                    YunFu yunFu = listYunPayFragment.get(i);
                    saveSqlData(yunFu);
                    /**
                     * 储值卡消费
                     */
                    if(yunFu.ticket==0){
                        //储值卡消费扣减
                        String sql2=SqlYun.updateIms_card_members1(yunFu.money,mWeid,yunFu.fromUser);
                        //储值卡消费记录
                        String sql3 = SqlYun.insertIms_card_deal_record(mWeid, yunFu.fromUser, mJysj,yunFu.credit2, yunFu.money, 0, yunFu.credit2 - yunFu.money, mWmlsbjb.getJSJ(), mWmlsbjb.getYHBH(), mBmbh, mDeal_id, yunFu.id);
                        //0按消费金额获取积分&&(2 储值卡消费.3 现金消费和储值卡消费金额)
                        if(mJfgzsz.jfly==0&&(mJfgzsz.jfgz==2||mJfgzsz.jfgz==3)){
                            mJfbfb =  (int)yunFu.money * mJfgzsz.jfbfb / 100;//获得积分
                            //积分获得
                            String jifen1 = SqlYun.updateIms_card_members2(mJfbfb, mWeid, yunFu.fromUser);
                            //积分获得记录
                            String jifen2 = SqlYun.insertIms_card_jf_record(mWeid, yunFu.fromUser, mJysj, "获取积分",yunFu.credit1, mJfbfb,
                                    yunFu.credit1+mJfbfb, mWmlsbjb.getJSJ(), mWmlsbjb.getYHBH(), mBmbh, mDeal_id, yunFu.id);
                            sqlCZXF=sql2+sql3+jifen1+jifen2;
                        }else{
                            sqlCZXF=sql2+sql3;
                        }
                        //插入sqlserver
                        sqlXSFKFS1="INSERT INTO XSFKFS(XSDH,BM,NR,FKJE,DYQZS) VALUES('"+mWmlsbjb.getWMDBH()+"','999999999','云会员-储值消费',"+yunFu.money+",0)|";
                        sqlCZKJYMXXX="INSERT INTO CZKJYMXXX(HYKH,JYSJ,JYLX,CZQJE,KCZJE,SSJE,KYE,KCZXL,SYJH,YHBH,BY3)" +
                                "VALUES('"+yunFu.cardsn+"',getdate(), '消费',"+yunFu.credit2+", "+yunFu.money+",0, "+(yunFu.credit2 - yunFu.money)+" , '999','"+mWmlsbjb.getJSJ()+"' , '"+mWmlsbjb.getYHBH()+"','1')|";
                    }
                    /**
                     * 积分消费
                     */
                    else if(yunFu.ticket==1){
                        //积分获得
                        String sql4 = SqlYun.updateIms_card_members2(-by3, mWeid, yunFu.fromUser);
                        //积分获得记录
                        String sql5 = SqlYun.insertIms_card_jf_record(mWeid, yunFu.fromUser, mJysj, "积分消费",yunFu.credit1, -by3,
                                yunFu.credit1-by3, mWmlsbjb.getJSJ(), mWmlsbjb.getYHBH(), mBmbh, mDeal_id, yunFu.id);
                        sqlJFXF=sql4+sql5;
                        //插入sqlserver
                        sqlXSFKFS2="INSERT INTO XSFKFS(XSDH,BM,NR,FKJE,DYQZS) VALUES('"+mWmlsbjb.getWMDBH()+"','999999998','云会员-积分消费',"+yunFu.money+",0)|";
                    }
                        /**
                         * 各种电子券消费
                         */
                    else if(yunFu.ticket==2){
                        //电子券使用状态更新
                        String sql7 = SqlYun.updateIms_card_members_coupon(mWmlsbjb.getYHBH(), mJysj, mDeal_id, mWeid, yunFu.id, yunFu.fromUser,yunFu.sl);
                        //电子券使用记录
                        String sql8 = SqlYun.insertCoupon_deal_record(mWeid, yunFu.id, yunFu.fromUser, mJysj, -yunFu.sl,
                                mWmlsbjb.getJSJ(), mWmlsbjb.getYHBH(), mBmbh, mDeal_id, "云会员-" + yunFu.title, -yunFu.money,
                                "云会员-" + yunFu.title + "(" + yunFu.sl + "张)", yunFu.id);
                        sqlJYQ1=sql7+sql8;
                        //插入sqlserver
                        String NR="云会员-"+yunFu.title;
                        sqlXSFKFS31="INSERT INTO XSFKFS(XSDH,BM,NR,FKJE,DYQZS) VALUES('"+mWmlsbjb.getWMDBH()+"',"+yunFu.id+",'"+NR+"',"+yunFu.money+","+yunFu.sl+")|";
                    }
                    else if(yunFu.ticket==3){
                        //电子券使用状态更新
                        String sql7 = SqlYun.updateIms_card_members_coupon(mWmlsbjb.getYHBH(), mJysj, mDeal_id, mWeid, yunFu.id, yunFu.fromUser,yunFu.sl);
                        //电子券使用记录
                        String sql8 = SqlYun.insertCoupon_deal_record(mWeid, yunFu.id, yunFu.fromUser, mJysj, -yunFu.sl,
                                mWmlsbjb.getJSJ(), mWmlsbjb.getYHBH(), mBmbh, mDeal_id, "云会员-" + yunFu.title, -yunFu.money,
                                "云会员-" + yunFu.title + "(" + yunFu.sl + "张)", yunFu.id);
                        sqlJYQ2=sql7+sql8;
                        //插入sqlserver
                        String NR="云会员-"+yunFu.title;
                        sqlXSFKFS32="INSERT INTO XSFKFS(XSDH,BM,NR,FKJE,DYQZS) VALUES('"+mWmlsbjb.getWMDBH()+"',"+yunFu.id+",'"+NR+"',"+yunFu.money+","+yunFu.sl+")|";
                    }
                    else if(yunFu.ticket==4){
                        //电子券使用状态更新
                        String sql7 = SqlYun.updateIms_card_members_coupon(mWmlsbjb.getYHBH(), mJysj, mDeal_id, mWeid, yunFu.id, yunFu.fromUser,yunFu.sl);
                        //电子券使用记录
                        String sql8 = SqlYun.insertCoupon_deal_record(mWeid, yunFu.id, yunFu.fromUser, mJysj, -yunFu.sl,
                                mWmlsbjb.getJSJ(), mWmlsbjb.getYHBH(), mBmbh, mDeal_id, "云会员-" + yunFu.title, -yunFu.money,
                                "云会员-" + yunFu.title + "(" + yunFu.sl + "张)", yunFu.id);
                        sqlJYQ3=sql7+sql8;
                        //插入sqlserver
                        String NR="云会员-"+yunFu.title;
                        sqlXSFKFS33="INSERT INTO XSFKFS(XSDH,BM,NR,FKJE,DYQZS) VALUES('"+mWmlsbjb.getWMDBH()+"',"+yunFu.id+",'"+NR+"',"+yunFu.money+","+yunFu.sl+")|";
                    }
                    else if(yunFu.ticket==5){
                        //电子券使用状态更新
                        String sql7 = SqlYun.updateIms_card_members_coupon(mWmlsbjb.getYHBH(), mJysj, mDeal_id, mWeid, yunFu.id, yunFu.fromUser,yunFu.sl);
                        //电子券使用记录
                        String sql8 = SqlYun.insertCoupon_deal_record(mWeid, yunFu.id, yunFu.fromUser, mJysj, -yunFu.sl,
                                mWmlsbjb.getJSJ(), mWmlsbjb.getYHBH(), mBmbh, mDeal_id, "云会员-" + yunFu.title, -yunFu.money,
                                "云会员-" + yunFu.title + "(" + yunFu.sl + "张)", yunFu.id);
                        sqlJYQ4=sql7+sql8;
                        //插入sqlserver
                        String NR="云会员-"+yunFu.title;
                        sqlXSFKFS34="INSERT INTO XSFKFS(XSDH,BM,NR,FKJE,DYQZS) VALUES('"+mWmlsbjb.getWMDBH()+"',"+yunFu.id+",'"+NR+"',"+yunFu.money+","+yunFu.sl+")|";
                    }
                    else if(yunFu.ticket==6){
                        //电子券使用状态更新
                        String sql7 = SqlYun.updateIms_card_members_coupon(mWmlsbjb.getYHBH(), mJysj, mDeal_id, mWeid, yunFu.id, yunFu.fromUser,yunFu.sl);
                        //电子券使用记录
                        String sql8 = SqlYun.insertCoupon_deal_record(mWeid, yunFu.id, yunFu.fromUser, mJysj, -yunFu.sl,
                                mWmlsbjb.getJSJ(), mWmlsbjb.getYHBH(), mBmbh, mDeal_id, "云会员-" + yunFu.title, -yunFu.money,
                                "云会员-" + yunFu.title + "(" + yunFu.sl + "张)", yunFu.id);
                        sqlJYQ5=sql7+sql8;
                        //插入sqlserver
                        String NR="云会员-"+yunFu.title;
                        sqlXSFKFS35="INSERT INTO XSFKFS(XSDH,BM,NR,FKJE,DYQZS) VALUES('"+mWmlsbjb.getWMDBH()+"',"+yunFu.id+",'"+NR+"',"+yunFu.money+","+yunFu.sl+")|";
                    }
                }
                    //汇总
                    mSqlYun=sqlCZXF+sqlJFXF+sqlJYQ1+sqlJYQ2+sqlJYQ3+sqlJYQ4+sqlJYQ5;
                    //消费明细deal_record
                    mSql1Deal_record =  SqlYun.insertDeal_record(mWeid, mYun.getFrom_user(), mJysj,mWmlsbjb.getJSJ(), mWmlsbjb.getYHBH(), mBmbh, mDeal_id, 0, Moneys.ysjr, Moneys.yfjr,mWmlsbjb.getWMDBH()+"_"+mBmbh,mYun.getId());
                    mSqlYun = mSqlYun +mSql1Deal_record;
                    //sqlServer汇总语句
                    mSqlLocal=sqlXSFKFS1+sqlXSFKFS2+sqlXSFKFS31+sqlXSFKFS32+sqlXSFKFS33+sqlXSFKFS34+sqlXSFKFS35+sqlCZKJYMXXX;
                MyAsync async = new MyAsync();
                async.execute();

                Moneys.yfjr=0;
                Moneys.wfjr=Moneys.ysjr;
                break;
            case R.id.btn_cancel:
                Moneys.yfjr=0;
                Moneys.wfjr=Moneys.ysjr;
                getActivity().finish();
                break;
        }
    }
    class MyAsync extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... strings) {
            String result = DownHTTP.postResult(Net.yunUrl, "7", mSqlYun);
            return result;
        }
        @Override
        protected void onPostExecute(String result) {
            if (result.contains("richado")) {
                EventBus.getDefault().post(new YunSqlFinish(mSqlLocal,mListWmlsb,mWmlsbjb,listYunPayFragment));
                getActivity().finish();
            }else{
                mConfirm.setEnabled(true);
               Toast.makeText(getActivity(),"数据提交失败",Toast.LENGTH_SHORT).show();
            }
            super.onPostExecute(result);
        }
    }

    private void saveSqlData(YunFu yunFu) {
        SqlYun.sqlYun=mSqlYun;
        SqlYun.sqlLocal=mSqlLocal;
        SqlYun.WMBS=mDeal_id;
        SqlYun.CZQJE=yunFu.credit2;
        SqlYun.KCZJE=Moneys.yfjr;
        SqlYun.CZKYE=yunFu.credit2-Moneys.yfjr;
        SqlYun.HYBH=yunFu.cardsn;
        SqlYun.HYKDJ=yunFu.cardgrade;
        SqlYun.jfbfb=mJfbfb;
        SqlYun.from_user=yunFu.fromUser;
        SqlYun.JZBZ=mDeal_id;
    }
}

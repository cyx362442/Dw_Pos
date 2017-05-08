package com.duowei.dw_pos;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.duowei.dw_pos.bean.ImsCardMember;
import com.duowei.dw_pos.bean.Moneys;
import com.duowei.dw_pos.bean.WMLSB;
import com.duowei.dw_pos.bean.WXFWQDZ;
import com.duowei.dw_pos.bean.Wmslbjb_jiezhang;
import com.duowei.dw_pos.bean.YunFu;
import com.duowei.dw_pos.event.ImsCardCouponStores;
import com.duowei.dw_pos.event.YunSubmit;
import com.duowei.dw_pos.event.YunSubmitFail;
import com.duowei.dw_pos.fragment.YunAccountFragment;
import com.duowei.dw_pos.fragment.YunCardFragment;
import com.duowei.dw_pos.fragment.YunPayFragment;
import com.duowei.dw_pos.httputils.Post6;
import com.google.gson.Gson;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.litepal.crud.DataSupport;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class YunPayActivity extends AppCompatActivity implements YunCardFragment.GvClickListener {

    @BindView(R.id.card)
    TextView mCard;
    @BindView(R.id.name)
    TextView mName;
    @BindView(R.id.frame01)
    FrameLayout mFrame01;
    @BindView(R.id.frame02)
    FrameLayout mFrame02;
    @BindView(R.id.frame03)
    FrameLayout mFrame03;
    @BindView(R.id.progressBar)
    ProgressBar mProgressBar;
    private Wmslbjb_jiezhang mWmlsbjb;
    private ImsCardMember mImsCards;
    private List<YunFu>listYunFu=new ArrayList<>();
    private ArrayList<ImsCardMember> mYunList = new ArrayList<>();
    private ArrayList<WMLSB> mWmlsb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_yun_pay);
        EventBus.getDefault().register(this);
        ButterKnife.bind(this);
        mWmlsbjb = (Wmslbjb_jiezhang) getIntent().getSerializableExtra("WMLSBJB");
        mWmlsb = (ArrayList<WMLSB>) getIntent().getSerializableExtra("WMLSB");
        mImsCards = (ImsCardMember) getIntent().getSerializableExtra("cards");
        /**获取会员卡储值余额、积分余额*/
        addImsCardData();
        /**发送post请求获取ims_card_coupon_stores礼券信息*/
        WXFWQDZ wxfwqdz = DataSupport.select("weid", "bmbh").findFirst(WXFWQDZ.class);
        int weid = wxfwqdz.getWeid();
        String bmbh = wxfwqdz.getBMBH();
        mProgressBar.bringToFront();
        Post6.getInstance().post_ims_card_coupon_stores(weid, bmbh, mImsCards.getFrom_user());
    }

    @Override
    protected void onStart() {
        super.onStart();
        mCard.setText(mImsCards.getCardsn());
        mName.setText(mImsCards.getRealname());
        toYunAccountFragment();
        toYunCardFragment();
        toYunPayFragment(listYunFu);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    /**
     * EventBus获取post请求返回的会员卡礼券信息
     */
    @Subscribe
    public void getImsCards(ImsCardCouponStores event) {
        String respone = event.reslut;
        if (!respone.equals("error")) {
            mYunList.clear();
            addImsCardData();
            Gson gson = new Gson();
            ImsCardMember[] yunhuiyuen = gson.fromJson(respone, ImsCardMember[].class);
            for (int i = 0; i < yunhuiyuen.length; i++) {
                yunhuiyuen[i].setTicket(i+2);
                yunhuiyuen[i].setSelect(false);
                yunhuiyuen[i].setFrom_user(mImsCards.getFrom_user());
                mYunList.add(yunhuiyuen[i]);
            }
            toYunCardFragment();
        }
        mProgressBar.setVisibility(View.GONE);
    }
    @Subscribe
    public void yunSubmit(YunSubmit event){
        mProgressBar.setVisibility(View.VISIBLE);
    }
    @Subscribe
    public void yunSubmitFail(YunSubmitFail event){
        mProgressBar.setVisibility(View.GONE);
    }
    private void toYunAccountFragment() {
        FragmentManager fm = getFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        YunAccountFragment fragment = new YunAccountFragment();
        Bundle bundle = new Bundle();
        fragment.setArguments(bundle);
        ft.replace(R.id.frame01, fragment);
        ft.commit();
    }

    private void toYunPayFragment(List<YunFu> listPay) {
        FragmentManager fm = getFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        YunPayFragment fragment = new YunPayFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable("list", (Serializable) listPay);
        fragment.setArguments(bundle);
        ft.replace(R.id.frame02, fragment);
        ft.commit();
    }

    private void toYunCardFragment() {
        FragmentManager fm = getFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        YunCardFragment fragment = new YunCardFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable("cards", mYunList);
        bundle.putSerializable("WMLSB",mWmlsb);
        bundle.putSerializable("mWmlsbjb",mWmlsbjb);
        fragment.setArguments(bundle);
        ft.replace(R.id.frame03, fragment);
        ft.commit();
    }

    /**
     * 获取会员卡储值余额、积分余额
     */
    private void addImsCardData() {
        if (mImsCards.getCredit2() >= 0) {//储值消费
            mYunList.add(new ImsCardMember(mImsCards.getId(), mImsCards.getFrom_user(), mImsCards.getCardsn(), mImsCards.getCredit1(), mImsCards.getCredit2(),
                    mImsCards.getRealname(), mImsCards.getMobile(), mImsCards.getStatus(), mImsCards.getCardgrade(), mImsCards.getOccupation(),
                    mImsCards.getCreatetime(), mImsCards.getTitle(), mImsCards.getCouponmoney(), mImsCards.getSL(), 0,false));
        }
        if (mImsCards.getCredit1() >= 0) {//积分消费
            mYunList.add(new ImsCardMember(mImsCards.getId(), mImsCards.getFrom_user(), mImsCards.getCardsn(), mImsCards.getCredit1(), -1f,
                    mImsCards.getRealname(), mImsCards.getMobile(), mImsCards.getStatus(), mImsCards.getCardgrade(), mImsCards.getOccupation(),
                    mImsCards.getCreatetime(), mImsCards.getTitle(), mImsCards.getCouponmoney(), mImsCards.getSL(), 1,false));
        }
    }

    /***接口回调，获取YunCardFragment中gridview点击事件传值*/
    @Override
    public void yunPayFragment(List<YunFu> listPay) {
        //刷新yunpayfragment
        toYunPayFragment(listPay);
        //刷新yunaccountfragment
        toYunAccountFragment();
    }
    /**重写返回健*/
    @Override
    public void onBackPressed() {
        //TODO something
        Moneys.yfjr=0;
        Moneys.wfjr=Moneys.ysjr;
        super.onBackPressed();
    }
}

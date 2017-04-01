package com.duowei.dw_pos;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.duowei.dw_pos.bean.ImsCardMember;
import com.duowei.dw_pos.bean.WMLSBJB;
import com.duowei.dw_pos.bean.WXFWQDZ;
import com.duowei.dw_pos.bean.Wmslbjb_jiezhang;
import com.duowei.dw_pos.event.ImsCardCouponStores;
import com.duowei.dw_pos.fragment.YunAccountFragment;
import com.duowei.dw_pos.fragment.YunCardFragment;
import com.duowei.dw_pos.fragment.YunPayFragment;
import com.duowei.dw_pos.httputils.Post6;
import com.google.gson.Gson;

import org.greenrobot.eventbus.Subscribe;
import org.litepal.crud.DataSupport;

import java.io.Serializable;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class YunPayActivity extends AppCompatActivity implements YunCardFragment.GvClickListener{

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
    @BindView(R.id.btn_confirm)
    Button mBtnConfirm;
    @BindView(R.id.btn_cancel)
    Button mBtnCancel;
    private Wmslbjb_jiezhang mWmlsbjb;
    private ImsCardMember mImsCards;
    private ArrayList<ImsCardMember> mYunList = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_yun_pay);
        ButterKnife.bind(this);
        mWmlsbjb = (Wmslbjb_jiezhang) getIntent().getSerializableExtra("WMLSBJB");
        mImsCards = (ImsCardMember) getIntent().getSerializableExtra("cards");
        WXFWQDZ wxfwqdz = DataSupport.select("weid", "bmbh").findFirst(WXFWQDZ.class);
        int weid = wxfwqdz.getWeid();
        String bmbh = wxfwqdz.getBMBH();
        addImsCardData();

        Post6.getInstance().post_ims_card_coupon_stores(weid,bmbh,mImsCards.getFrom_user());
    }

    @Override
    protected void onStart() {
        super.onStart();
        org.greenrobot.eventbus.EventBus.getDefault().register(this);
        mCard.setText(mImsCards.getCardsn());
        mName.setText(mImsCards.getRealname());
        toYunAccountFragment();
        toYunCardFragment();
        toYunPayFragment("");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        org.greenrobot.eventbus.EventBus.getDefault().unregister(this);
    }

    @Subscribe
    public void getImsCards(ImsCardCouponStores event){
        String respone = event.reslut;
        if(!respone.equals("error")){
            mYunList.clear();
            addImsCardData();
            Gson gson = new Gson();
            ImsCardMember[] yunhuiyuen = gson.fromJson(respone, ImsCardMember[].class);
            for (int i = 0; i < yunhuiyuen.length; i++) {
                yunhuiyuen[i].setTicket(2);
                mYunList.add(yunhuiyuen[i]);
            }
            toYunCardFragment();
        }
    }

    private void toYunAccountFragment() {
        FragmentManager fm = getFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        YunAccountFragment fragment = new YunAccountFragment();
        ft.replace(R.id.frame01,fragment);
        ft.commit();
    }

    private void toYunPayFragment(String value) {
        FragmentManager fm = getFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        YunPayFragment fragment = new YunPayFragment();
        Bundle bundle = new Bundle();
        bundle.putString("msg", value);
        fragment.setArguments(bundle);
        ft.replace(R.id.frame02, fragment);
        ft.commit();
    }

    private void toYunCardFragment() {
        FragmentManager fm = getFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        YunCardFragment fragment = new YunCardFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable("cards",mYunList);
        fragment.setArguments(bundle);
        ft.replace(R.id.frame03,fragment);
        ft.commit();
    }

    private void addImsCardData() {
        if (mImsCards .getCredit2() >= 0) {//储值消费
            mYunList.add(new ImsCardMember(mImsCards  .getId(), mImsCards  .getFrom_user(), mImsCards  .getCardsn(),mImsCards  .getCredit1(), mImsCards  .getCredit2(),
                    mImsCards  .getRealname(), mImsCards  .getMobile(), mImsCards  .getStatus(), mImsCards  .getCardgrade(), mImsCards  .getOccupation(),
                    mImsCards  .getCreatetime(), mImsCards  .getTitle(), mImsCards  .getCouponmoney(), mImsCards  .getSL(),0));
        }
        if (mImsCards  .getCredit1() >= 0) {//积分消费
            mYunList.add(new ImsCardMember(mImsCards  .getId(), mImsCards  .getFrom_user(), mImsCards  .getCardsn(), mImsCards  .getCredit1(), -1f,
                    mImsCards  .getRealname(), mImsCards  .getMobile(), mImsCards  .getStatus(), mImsCards  .getCardgrade(), mImsCards  .getOccupation(),
                    mImsCards  .getCreatetime(), mImsCards  .getTitle(), mImsCards  .getCouponmoney(), mImsCards  .getSL(),1));
        }
    }

    @OnClick({R.id.btn_confirm, R.id.btn_cancel})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_confirm:
                break;
            case R.id.btn_cancel:
                finish();
                break;
        }
    }

    @Override
    public void sendMsg(int postion) {
        toYunPayFragment(postion+"号");
    }
}

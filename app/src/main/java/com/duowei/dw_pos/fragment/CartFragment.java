package com.duowei.dw_pos.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.duowei.dw_pos.CartDetailActivity;
import com.duowei.dw_pos.R;
import com.duowei.dw_pos.bean.CartInfo;
import com.duowei.dw_pos.event.CartUpdateEvent;
import com.duowei.dw_pos.tools.CartList;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

/**
 * 购物车
 */

public class CartFragment extends Fragment implements View.OnClickListener {
    private static final String TAG = "CartFragment";

    private View mRootView;

    private TextView mCartNumView;
    private TextView mCartPriceView;
    private FrameLayout mCartIconLayout;

    private final String ACTION_NAME = "animStore";

    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (ACTION_NAME.equals(intent.getAction())) {
                Animation animation = AnimationUtils.loadAnimation(getContext(),
                        R.anim.anim_cart);
                mCartIconLayout.startAnimation(animation);
            }
        }
    };

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate: ");
        EventBus.getDefault().register(this);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.fragment_cart, container, false);
        return mRootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mRootView.setVisibility(View.GONE);

        mCartNumView = (TextView) view.findViewById(R.id.tv_cart_num);
        mCartPriceView = (TextView) view.findViewById(R.id.tv_cart_price);
        mCartIconLayout = (FrameLayout) view.findViewById(R.id.fl_cart);

        view.findViewById(R.id.btn_commit).setOnClickListener(this);

        //注册广播
        registerBoradcastReceiver();
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d(TAG, "onStart: ");
        
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d(TAG, "onStop: ");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy: ");
        EventBus.getDefault().unregister(this);
        getActivity().unregisterReceiver(mBroadcastReceiver);
    }

    public void registerBoradcastReceiver() {
        IntentFilter myIntentFilter = new IntentFilter();
        myIntentFilter.addAction(ACTION_NAME);
        //注册广播
        getActivity().registerReceiver(mBroadcastReceiver, myIntentFilter);
    }

    @Subscribe
    public void updateUiDate(CartUpdateEvent event) {
        CartList cartList = CartList.newInstance();

        if (cartList.getSize() > 0) {
            mRootView.setVisibility(View.VISIBLE);

            CartInfo cartInfo = cartList.getCartInfo();
            mCartNumView.setText(String.valueOf(cartInfo.getNum()));
            mCartPriceView.setText(String.valueOf(cartInfo.getPrice()));

        } else {
            mRootView.setVisibility(View.GONE);
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();

        if (id == R.id.btn_commit) {
//            Toast.makeText(getContext(), "Clicked", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(getContext(), CartDetailActivity.class);
            getContext().startActivity(intent);
        }
    }
}

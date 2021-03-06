package com.duowei.dw_pos.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.duowei.dw_pos.CartDetailActivity;
import com.duowei.dw_pos.R;
import com.duowei.dw_pos.bean.CartInfo;
import com.duowei.dw_pos.event.AddAnim;
import com.duowei.dw_pos.event.CartUpdateEvent;
import com.duowei.dw_pos.tools.CartList;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * 购物车
 */

public class CartFragment extends Fragment implements View.OnClickListener {
    private static final String TAG = "CartFragment";

    private View mRootView;

    private TextView mCartNumView;
    private TextView mCartPriceView;
    private FrameLayout mCartIconLayout;
    private Animation mAnimation;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(CartFragment.this);
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
        mCartNumView = (TextView) view.findViewById(R.id.tv_cart_num);
        mCartPriceView = (TextView) view.findViewById(R.id.tv_cart_price);
        mCartIconLayout = (FrameLayout) view.findViewById(R.id.fl_cart);
        mAnimation = AnimationUtils.loadAnimation(getContext(),
                R.anim.anim_cart);

        view.findViewById(R.id.btn_commit).setOnClickListener(this);
        mCartIconLayout.setOnClickListener(this);
    }

    @Override
    public void onStart() {
        super.onStart();
        updateUiDate(null);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(CartFragment.this);
    }

    @Subscribe
    public void startAnim(AddAnim event){
        mCartIconLayout.startAnimation(mAnimation);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void updateUiDate(CartUpdateEvent event) {
        CartInfo cartInfo = CartList.newInstance(getContext()).getCartInfo();

        mCartNumView.setText(String.valueOf(cartInfo.getNum()));
        mCartPriceView.setText(String.valueOf(cartInfo.getPrice()));

//        if (cartList.getSize() > 0) {
//            mRootView.setVisibility(View.VISIBLE);
//
//            CartInfo cartInfo = cartList.getCartInfo();
//            mCartNumView.setText(String.valueOf(cartInfo.getNum()));
//            mCartPriceView.setText(String.valueOf(cartInfo.getPrice()));
//
//        } else {
//            mRootView.setVisibility(View.GONE);
//        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();

        if (id == R.id.btn_commit || id == R.id.fl_cart) {
            if (getArguments() == null) {
                Intent intent = new Intent(getContext(), CartDetailActivity.class);
                getContext().startActivity(intent);
            } else {
                getActivity().finish();
            }
        }
    }
}

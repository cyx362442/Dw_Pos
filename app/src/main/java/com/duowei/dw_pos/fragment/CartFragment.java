package com.duowei.dw_pos.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.duowei.dw_pos.R;
import com.duowei.dw_pos.bean.CartInfo;
import com.duowei.dw_pos.event.CartUpdateEvent;
import com.duowei.dw_pos.tools.CartList;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

/**
 * Created by Administrator on 2017-03-23.
 */

public class CartFragment extends Fragment implements View.OnClickListener {

    private View mRootView;

    private TextView mCartNumView;
    private TextView mCartPriceView;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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

        view.findViewById(R.id.btn_commit).setOnClickListener(this);
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
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
            Toast.makeText(getContext(), "Clicked", Toast.LENGTH_SHORT).show();
        }
    }
}

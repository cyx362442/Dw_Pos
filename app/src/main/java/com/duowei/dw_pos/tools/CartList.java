package com.duowei.dw_pos.tools;

import android.text.TextUtils;

import com.duowei.dw_pos.bean.CartInfo;
import com.duowei.dw_pos.bean.JYXMSZ;
import com.duowei.dw_pos.bean.TCSD;
import com.duowei.dw_pos.event.CartUpdateEvent;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;

/**
 * 购物车
 */

public class CartList {
    private static CartList mInstance;
    
    private ArrayList<Object> mList;

    private CartList() {
        mList = new ArrayList<>();
    }

    public static CartList newInstance() {
       if (mInstance == null) {
           mInstance = new CartList();
       }
       
       return mInstance;
    }

    public void clear() {
        mList.clear();
    }

    public int getSize() {
        return mList.size();
    }

    public CartInfo getCartInfo() {
        int num = 0;
        float price = 0;

        for (int j = 0; j < mList.size(); j++) {
            Object object = mList.get(j);

            if (object instanceof JYXMSZ) {
                // 单品
                JYXMSZ jyxmsz = (JYXMSZ) object;
                num++;
                price += Float.valueOf(jyxmsz.getXSJG());

            } else if (object instanceof TCSD) {
                // 套餐主项
                TCSD tcsd = (TCSD) object;
                if (!TextUtils.isEmpty(tcsd.getGQ())
                        && tcsd.getGQ().equals("1")) {
                    num++;
                    price += Float.valueOf(tcsd.getDJ());
                }
            }
        }

        return new CartInfo(num, price);
    }


    public void add(Object object) {
        mList.add(object);
        EventBus.getDefault().post(new CartUpdateEvent(""));
    }
    
    public void remove(Object object) {
        // // TODO: 2017-03-24  
    }
}

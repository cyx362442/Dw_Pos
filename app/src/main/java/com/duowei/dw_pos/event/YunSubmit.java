package com.duowei.dw_pos.event;

import com.duowei.dw_pos.bean.WMLSB;
import com.duowei.dw_pos.bean.YunFu;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017-05-06.
 */

public class YunSubmit {
    public ArrayList<WMLSB> mListWmlsb;
    public List<YunFu> listPay;
    public float otherPay;

    public YunSubmit(ArrayList<WMLSB> listWmlsb, List<YunFu> listPay,float otherPay) {
        mListWmlsb = listWmlsb;
        this.listPay = listPay;
        this.otherPay=otherPay;
    }
}

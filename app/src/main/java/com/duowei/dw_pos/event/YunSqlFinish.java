package com.duowei.dw_pos.event;

import com.duowei.dw_pos.bean.WMLSB;
import com.duowei.dw_pos.bean.Wmslbjb_jiezhang;
import com.duowei.dw_pos.bean.YunFu;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017-04-05.
 */

public class YunSqlFinish {
    public String sql;
    public ArrayList<WMLSB> mListWmlsb;
    public Wmslbjb_jiezhang mWmlsbjb;
    public List<YunFu> listPay;

    public YunSqlFinish(String sql, ArrayList<WMLSB> listWmlsb, Wmslbjb_jiezhang wmlsbjb,List<YunFu> listPay) {
        this.sql = sql;
        this.mListWmlsb = listWmlsb;
        this.mWmlsbjb = wmlsbjb;
        this.listPay=listPay;
    }
}

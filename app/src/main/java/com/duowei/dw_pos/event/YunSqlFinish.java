package com.duowei.dw_pos.event;

import com.duowei.dw_pos.bean.WMLSB;
import com.duowei.dw_pos.bean.Wmslbjb_jiezhang;

import java.util.ArrayList;

/**
 * Created by Administrator on 2017-04-05.
 */

public class YunSqlFinish {
    public String sql;
    public ArrayList<WMLSB> mListWmlsb;
    public Wmslbjb_jiezhang mWmlsbjb;

    public YunSqlFinish(String sql, ArrayList<WMLSB> listWmlsb, Wmslbjb_jiezhang wmlsbjb) {
        this.sql = sql;
        mListWmlsb = listWmlsb;
        mWmlsbjb = wmlsbjb;
    }
}

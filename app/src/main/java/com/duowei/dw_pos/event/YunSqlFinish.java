package com.duowei.dw_pos.event;


import com.duowei.dw_pos.bean.Wmslbjb_jiezhang;

/**
 * Created by Administrator on 2017-04-05.
 */

public class YunSqlFinish {
    public String sql;
    public Wmslbjb_jiezhang wmlsbjb;
    public YunSqlFinish(Wmslbjb_jiezhang wmlsbjb,String sql) {
        this.wmlsbjb=wmlsbjb;
        this.sql = sql;
    }
}

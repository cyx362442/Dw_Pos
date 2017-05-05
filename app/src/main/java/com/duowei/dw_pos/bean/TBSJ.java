package com.duowei.dw_pos.bean;

import org.litepal.crud.DataSupport;

/**
 * Created by Administrator on 2017-05-03.
 */

public class TBSJ extends DataSupport {

    /**
     * tablename : JYXMSZ
     * tbrq : 20170503T09:14:05
     */

    private String tablename;
    private String tbrq;

    public String getTablename() {
        return tablename;
    }

    public void setTablename(String tablename) {
        this.tablename = tablename;
    }

    public String getTbrq() {
        return tbrq;
    }

    public void setTbrq(String tbrq) {
        this.tbrq = tbrq;
    }
}

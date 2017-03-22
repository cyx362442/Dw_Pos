package com.duowei.dw_pos.bean;

import org.litepal.crud.DataSupport;

/**
 * Created by Administrator on 2017-03-21.
 */

public class YHJBQK extends DataSupport{
    /**
     * YHBH : 0000
     * YHMC : 管理员
     * YHMM : 
     * XTJSQX : 0
     * ZPQX : 1
     * MDBBQX : 1
     */
    public String YHBH;
    public String YHMC;
    public String YHMM;
    public String XTJSQX;
    public String ZPQX;
    public String MDBBQX;

    public String getYHBH() {
        return YHBH;
    }

    public String getYHMC() {
        return YHMC;
    }

    public String getYHMM() {
        return YHMM;
    }

    public String getXTJSQX() {
        return XTJSQX;
    }

    public String getZPQX() {
        return ZPQX;
    }

    public String getMDBBQX() {
        return MDBBQX;
    }

    public void setYHBH(String YHBH) {
        this.YHBH = YHBH;
    }

    public void setYHMC(String YHMC) {
        this.YHMC = YHMC;
    }

    public void setYHMM(String YHMM) {
        this.YHMM = YHMM;
    }

    public void setXTJSQX(String XTJSQX) {
        this.XTJSQX = XTJSQX;
    }

    public void setZPQX(String ZPQX) {
        this.ZPQX = ZPQX;
    }

    public void setMDBBQX(String MDBBQX) {
        this.MDBBQX = MDBBQX;
    }
}

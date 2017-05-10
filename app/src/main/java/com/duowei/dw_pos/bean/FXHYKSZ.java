package com.duowei.dw_pos.bean;

import org.litepal.crud.DataSupport;

/**
 * Created by Administrator on 2017-03-30.
 */

public class FXHYKSZ extends DataSupport{

    /**
     * HYKGS : 111
     * HYKDJ : 银卡会员
     * ZKFS : 会员价2
     * SFQX : 0
     */

    private String HYKGS;
    private String HYKDJ;
    private String ZKFS;
    private String SFQX;

    public String getHYKGS() {
        return HYKGS;
    }

    public void setHYKGS(String HYKGS) {
        this.HYKGS = HYKGS;
    }

    public String getHYKDJ() {
        return HYKDJ;
    }

    public void setHYKDJ(String HYKDJ) {
        this.HYKDJ = HYKDJ;
    }

    public String getZKFS() {
        return ZKFS;
    }

    public void setZKFS(String ZKFS) {
        this.ZKFS = ZKFS;
    }

    public String getSFQX() {
        return SFQX;
    }

    public void setSFQX(String SFQX) {
        this.SFQX = SFQX;
    }
}

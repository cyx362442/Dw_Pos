package com.duowei.dw_pos.bean;

import org.litepal.crud.DataSupport;

/**
 * 买赠/加价促销设置明细信息MZSZMXXX
 */

public class MZSZMXXX extends DataSupport {
    private String BM;
    private String XMBH;
    private String XMMC;
    private String SL;
    private String XSJG;

    public String getBM() {
        return BM;
    }

    public void setBM(String BM) {
        this.BM = BM;
    }

    public String getXMBH() {
        return XMBH;
    }

    public void setXMBH(String XMBH) {
        this.XMBH = XMBH;
    }

    public String getXMMC() {
        return XMMC;
    }

    public void setXMMC(String XMMC) {
        this.XMMC = XMMC;
    }

    public String getSL() {
        return SL;
    }

    public void setSL(String SL) {
        this.SL = SL;
    }

    public String getXSJG() {
        return XSJG;
    }

    public void setXSJG(String XSJG) {
        this.XSJG = XSJG;
    }
}

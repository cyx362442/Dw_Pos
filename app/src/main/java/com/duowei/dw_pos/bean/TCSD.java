package com.duowei.dw_pos.bean;

import org.litepal.crud.DataSupport;

/**
 * Created by Administrator on 2017-03-22.
 */

public class TCSD extends DataSupport {

    /**
     * XH : 1
     * XMBH : ZT779
     * XMBH1 : 00134
     * XMMC1 : 香烤牛仔骨（套餐）
     * DW1 : 份
     * TM : A
     * SL : 1.00
     * DJ : 55.00
     * SFXZ : 1
     * GQ : 1
     * LBBM : 08
     */

    public String XH;
    public String XMBH;
    public String XMBH1;
    public String XMMC1;
    public String DW1;
    public String TM;
    public String SL;
    public String DJ;
    public String SFXZ;
    public String GQ;
    public String LBBM;

    public String getXH() {
        return XH;
    }

    public String getXMBH() {
        return XMBH;
    }

    public String getXMBH1() {
        return XMBH1;
    }

    public String getXMMC1() {
        return XMMC1;
    }

    public String getDW1() {
        return DW1;
    }

    public String getTM() {
        return TM;
    }

    public String getSL() {
        return SL;
    }

    public String getDJ() {
        return DJ;
    }

    public String getSFXZ() {
        return SFXZ;
    }

    public String getGQ() {
        return GQ;
    }

    public String getLBBM() {
        return LBBM;
    }
}

package com.duowei.dw_pos.bean;

import org.litepal.annotation.Column;
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
    public float SL;
    public float DJ;
    public String SFXZ;
    public String GQ;
    public String LBBM;

    @Column(ignore = true)
    public String PZ = "";

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

    public float getSL() {
        return SL;
    }

    public float getDJ() {
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

    public void setXH(String XH) {
        this.XH = XH;
    }

    public void setXMBH(String XMBH) {
        this.XMBH = XMBH;
    }

    public void setXMBH1(String XMBH1) {
        this.XMBH1 = XMBH1;
    }

    public void setXMMC1(String XMMC1) {
        this.XMMC1 = XMMC1;
    }

    public void setDW1(String DW1) {
        this.DW1 = DW1;
    }

    public void setTM(String TM) {
        this.TM = TM;
    }

    public void setSL(float SL) {
        this.SL = SL;
    }

    public void setDJ(float DJ) {
        this.DJ = DJ;
    }

    public void setSFXZ(String SFXZ) {
        this.SFXZ = SFXZ;
    }

    public void setGQ(String GQ) {
        this.GQ = GQ;
    }

    public void setLBBM(String LBBM) {
        this.LBBM = LBBM;
    }
}

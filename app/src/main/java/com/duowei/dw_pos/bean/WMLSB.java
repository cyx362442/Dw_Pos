package com.duowei.dw_pos.bean;

/**
 * Created by Administrator on 2017-03-24.
 */

public class WMLSB {

    /**
     * ZSSJ2 : 2017-03-24 09:59:28.057
     * BY3 : 0.0
     * XH : 754101
     * WMDBH : CYY20170323151848634
     * XMBH : 00093
     * XMMC : 乡村鸡皇（6寸铁盘）
     * TM : 00093
     * DW : 份
     * SL : 1.0000
     * DJ : 22.00
     * XJ : 22.00
     * PZ :
     * TCBH :
     * SFYXD : 1
     * XSZT :
     * YSJG : 22.00
     * SYYXM : 管理员
     * sfxs : 1
     * by2 : 06
     * by3 : 0.0
     * by5 : 20170323T15:18:49
     * BY12 :
     * BY13 :
     */

    private String ZSSJ2;
    private String BY3;
    private String XH;
    private String WMDBH;
    private String XMBH;
    private String XMMC;
    private String TM;
    private String DW;
    private float SL;
    private float DJ;
    private String XJ;
    private String PZ;
    private String TCBH;
    private String SFYXD;
    private String XSZT;
    private float YSJG;
    private String SYYXM;
    private String sfxs;
    private String by2;
    private String by3;
    private String by5;
    private String BY12;
    private String BY13;

    public String getZSSJ2() {
        return ZSSJ2;
    }

    public void setZSSJ2(String ZSSJ2) {
        this.ZSSJ2 = ZSSJ2;
    }

    public String getBY3() {
        return BY3;
    }

    public void setBY3(String BY3) {
        this.BY3 = BY3;
    }

    public String getXH() {
        return XH;
    }

    public void setXH(String XH) {
        this.XH = XH;
    }

    public String getWMDBH() {
        return WMDBH;
    }

    public void setWMDBH(String WMDBH) {
        this.WMDBH = WMDBH;
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

    public String getTM() {
        return TM;
    }

    public void setTM(String TM) {
        this.TM = TM;
    }

    public String getDW() {
        return DW;
    }

    public void setDW(String DW) {
        this.DW = DW;
    }

    public float getSL() {
        return SL;
    }

    public void setSL(float SL) {
        this.SL = SL;
    }

    public float getDJ() {
        return DJ;
    }

    public void setDJ(float DJ) {
        this.DJ = DJ;
    }

    public String getXJ() {
        return XJ;
    }

    public void setXJ(String XJ) {
        this.XJ = XJ;
    }

    public String getPZ() {
        return PZ;
    }

    public void setPZ(String PZ) {
        this.PZ = PZ;
    }

    public String getTCBH() {
        return TCBH;
    }

    public void setTCBH(String TCBH) {
        this.TCBH = TCBH;
    }

    public String getSFYXD() {
        return SFYXD;
    }

    public void setSFYXD(String SFYXD) {
        this.SFYXD = SFYXD;
    }

    public String getXSZT() {
        return XSZT;
    }

    public void setXSZT(String XSZT) {
        this.XSZT = XSZT;
    }

    public float getYSJG() {
        return YSJG;
    }

    public void setYSJG(float YSJG) {
        this.YSJG = YSJG;
    }

    public String getSYYXM() {
        return SYYXM;
    }

    public void setSYYXM(String SYYXM) {
        this.SYYXM = SYYXM;
    }

    public String getSfxs() {
        return sfxs;
    }

    public void setSfxs(String sfxs) {
        this.sfxs = sfxs;
    }

    public String getBy2() {
        return by2;
    }

    public void setBy2(String by2) {
        this.by2 = by2;
    }

    public String getBy3() {
        return by3;
    }

    public void setBy3(String by3) {
        this.by3 = by3;
    }

    public String getBy5() {
        return by5;
    }

    public void setBy5(String by5) {
        this.by5 = by5;
    }

    public String getBY12() {
        return BY12;
    }

    public void setBY12(String BY12) {
        this.BY12 = BY12;
    }

    public String getBY13() {
        return BY13;
    }

    public void setBY13(String BY13) {
        this.BY13 = BY13;
    }

    public WMLSB() {
    }

    /**
     * 添加 单品 到 点单临时表明细信息
     *
     * @param jyxmsz
     */
    public WMLSB(JYXMSZ jyxmsz) {

    }

    /**
     * 添加 套餐 到 点单临时表明细信息
     * @param tcsd 套餐主项（主项）
     */
    public WMLSB(TCSD tcsd) {
    }
}

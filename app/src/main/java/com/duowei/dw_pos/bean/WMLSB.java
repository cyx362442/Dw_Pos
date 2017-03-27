package com.duowei.dw_pos.bean;

import android.text.TextUtils;

import com.duowei.dw_pos.tools.DateTimeUtils;

import java.io.Serializable;

/**
 * Created by Administrator on 2017-03-24.
 */

public class WMLSB implements Serializable{

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
    private float XJ;
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
    private String BY15;
    private String TCXMBH;
    private float DWSL;

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

    public float getXJ() {
        return XJ;
    }

    public void setXJ(float XJ) {
        this.XJ = XJ;
    }

    public String getPZ() {
        if (TextUtils.isEmpty(PZ))
            return "";
        return PZ;
    }

    public void setPZ(String PZ) {
        this.PZ = PZ;
    }

    public String getTCBH() {
        if (TextUtils.isEmpty(TCBH))
            return "";
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

    public String getBY15() {
        if (TextUtils.isEmpty(BY15))
            return "";
        return BY15;
    }

    public void setBY15(String BY15) {
        this.BY15 = BY15;
    }

    public String getTCXMBH() {
        if (TextUtils.isEmpty(TCXMBH))
            return "";
        return TCXMBH;
    }

    public void setTCXMBH(String TCXMBH) {
        this.TCXMBH = TCXMBH;
    }

    public float getDWSL() {
        return DWSL;
    }

    public void setDWSL(float DWSL) {
        this.DWSL = DWSL;
    }



    public WMLSB() {
    }

    /**
     * 添加 单品 到 点单临时表明细信息
     *
     * @param jyxmsz
     */
    public WMLSB(JYXMSZ jyxmsz) {
        this.XMBH = jyxmsz.XMBH;
        this.XMMC = jyxmsz.XMMC;
        this.DW = jyxmsz.DW;
        this.DJ = jyxmsz.XSJG;
        this.YSJG = jyxmsz.XSJG;
        this.sfxs = "1";
        this.by5 = DateTimeUtils.getCurrentDatetime();
        this.TM = jyxmsz.TM;
        this.by2 = jyxmsz.LBBM;
        this.by3 = jyxmsz.YHJ;
        this.SL = 1;

        this.XJ = this.DJ * this.SL;

        this.SFYXD = "1";
    }

    /**
     * 添加 套餐 到 点单临时表明细信息
     *
     * @param tcsd 套餐主项（主项）
     * @param sfxs 主项 1 子项 0
     * @param tcbh 当前时间
     */
    public WMLSB(TCSD tcsd, String sfxs, String tcbh) {
        this.XMBH = tcsd.XMBH1;
        this.TCXMBH = tcsd.XMBH;
        this.DW = tcsd.DW1;
        this.DJ = tcsd.DJ;
        this.YSJG = tcsd.DJ;
        this.sfxs = sfxs;
        if (this.sfxs.equals("1")) {
            this.XMMC = tcsd.XMMC1;
        } else {
            this.XMMC = "  " + tcsd.XMMC1;
        }
        this.BY15 = tcsd.TM;
        this.TCBH = tcbh;
        this.DWSL = tcsd.SL;
        this.by2 = tcsd.LBBM;
//        this.by3 = tcsd.
        this.SL = tcsd.SL;

        this.XJ = this.DJ * this.SL;

        this.SFYXD = "1";
    }



    /**
     *
     */
    public String toInsertString() {
        return "INSERT INTO WMLSB (WMDBH,           XMBH,           XMMC,           TM,           DW,          SL,         DJ,         XJ,          PZ,                TCBH,           SFYXD,      XSZT, FTJE,   YSJG,     SFZS,      SYYXM,      SQRXM, ZSSJ,    DWSL,          sfxs,      by1,       by2,    by3, by4,   by5,      SJC,  BY6,  BY7,  BY8,  BY9,  BY10, BY11,               TCXMBH,  BY12, BY13, PBJSJM, PBXH, BY14, BY15, BY16, BY17, BY18, BY19, BY20, BY21, BY22, BY23, BY24, BY25) " +
                "     VALUES ('" + WMDBH + "', '" + XMBH + "', '" + XMMC + "', '" + TM + "', '" + DW + "', " + SL + ", " + DJ + ", " + XJ + ", '" + getPZ() + "', '" +  getTCBH() + "', '" + SFYXD + "', '', null, " + YSJG + ", null, '" + SYYXM + "', null, null, " + DWSL + ", '" + sfxs + "', null, '" + by2 + "', 0, null, GETDATE(), null, null, null, null, null, null, null, '" + getTCXMBH() + "', '', '', null, null, null, '" + getBY15() + "', null, null, null, null, null, null, null, null, null, null)|";
    }
}

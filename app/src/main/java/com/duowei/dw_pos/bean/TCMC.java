package com.duowei.dw_pos.bean;

import org.litepal.crud.DataSupport;

/**
 * Created by Administrator on 2017-03-22.
 */

public class TCMC extends DataSupport {

    /**
     * XMBH : ZT349
     * XMMC : 美团外送比萨A套
     * LBMC : 外送套餐
     * XL : 348.0
     * TM : 
     * PY : MTWSBSAT
     * by3 : 2011-01-01
     * by4 : 22220101T00:00:00
     * KSSJ : 00:00:00
     * JSSJ : 23:59:59
     */

    public String XMBH;
    public String XMMC;
    public String LBMC;
    public String XL;
    public String TM;
    public String PY;
    public String by3;
    public String by4;
    public String KSSJ;
    public String JSSJ;

    public String getXMBH() {
        return XMBH;
    }

    public String getXMMC() {
        return XMMC;
    }

    public String getLBMC() {
        return LBMC;
    }

    public String getXL() {
        return XL;
    }

    public String getTM() {
        return TM;
    }

    public String getPY() {
        return PY;
    }

    public String getBy3() {
        return by3;
    }

    public String getBy4() {
        return by4;
    }

    public String getKSSJ() {
        return KSSJ;
    }

    public String getJSSJ() {
        return JSSJ;
    }

    public void setXMBH(String XMBH) {
        this.XMBH = XMBH;
    }

    public void setXMMC(String XMMC) {
        this.XMMC = XMMC;
    }

    public void setLBMC(String LBMC) {
        this.LBMC = LBMC;
    }

    public void setXL(String XL) {
        this.XL = XL;
    }

    public void setTM(String TM) {
        this.TM = TM;
    }

    public void setPY(String PY) {
        this.PY = PY;
    }

    public void setBy3(String by3) {
        this.by3 = by3;
    }

    public void setBy4(String by4) {
        this.by4 = by4;
    }

    public void setKSSJ(String KSSJ) {
        this.KSSJ = KSSJ;
    }

    public void setJSSJ(String JSSJ) {
        this.JSSJ = JSSJ;
    }
}

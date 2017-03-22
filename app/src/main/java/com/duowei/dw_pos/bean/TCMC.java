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
}

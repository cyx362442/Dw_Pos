package com.duowei.dw_pos.bean;

import org.litepal.crud.DataSupport;

/**
 * Created by Administrator on 2017-03-22.
 * 经营场所设置
 */

public class JYCSSZ extends DataSupport{

    /**
     * CSBH : 1
     * FCSBH : 921
     * CSMC : 1
     */

    public String CSBH;
    public String FCSBH;
    public String CSMC;

    public String getCSBH() {
        return CSBH;
    }

    public String getFCSBH() {
        return FCSBH;
    }

    public String getCSMC() {
        return CSMC;
    }

    public void setCSBH(String CSBH) {
        this.CSBH = CSBH;
    }

    public void setFCSBH(String FCSBH) {
        this.FCSBH = FCSBH;
    }

    public void setCSMC(String CSMC) {
        this.CSMC = CSMC;
    }
}

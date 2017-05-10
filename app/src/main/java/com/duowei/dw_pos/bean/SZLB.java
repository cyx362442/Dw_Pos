package com.duowei.dw_pos.bean;

import org.litepal.crud.DataSupport;

/**
 * Created by Administrator on 2017-03-22.
 */

public class SZLB extends DataSupport {
    public String SZBM;
    public String SZMC;

    public String getSZBM() {
        return SZBM;
    }

    public String getSZMC() {
        return SZMC;
    }

    public SZLB() {
    }

    public SZLB(String SZBM) {
        this.SZBM = SZBM;
    }
}

package com.duowei.dw_pos.bean;

import org.litepal.crud.DataSupport;

/**
 * Created by Administrator on 2017-03-22.
 */

public class DMPZSD extends DataSupport {
    public String PZBM;
    public String NR;
    public String PXH;
    public String ZDBZ;
    public String DYCP;

    public String getPZBM() {
        return PZBM;
    }

    public String getNR() {
        return NR;
    }

    public String getPXH() {
        return PXH;
    }

    public String getZDBZ() {
        return ZDBZ;
    }
}

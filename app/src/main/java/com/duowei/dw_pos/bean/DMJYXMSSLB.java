package com.duowei.dw_pos.bean;

import org.litepal.crud.DataSupport;

/**
 * Created by Administrator on 2017-03-22.
 * 单品类别
 */

public class DMJYXMSSLB  extends DataSupport{

    /**
     * LBBM : 01
     * LBMC : 沙拉/会员卡
     * SFTY : 0
     * XL : 160000.0
     */

    public String LBBM;
    public String LBMC;
    public String SFTY;
    public String XL;

    public String getLBBM() {
        return LBBM;
    }

    public String getLBMC() {
        return LBMC;
    }

    public String getSFTY() {
        return SFTY;
    }

    public String getXL() {
        return XL;
    }
}

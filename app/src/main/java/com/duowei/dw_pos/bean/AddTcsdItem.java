package com.duowei.dw_pos.bean;

/**
 * Created by Administrator on 2017-03-25.
 */

public class AddTcsdItem {
    public TCSD tcsd;
    public String sfxs;
    public String tcbh;

    public AddTcsdItem(TCSD tcsd, String sfxs, String tcbh) {
        this.tcsd = tcsd;
        this.sfxs = sfxs;
        this.tcbh = tcbh;
    }
}

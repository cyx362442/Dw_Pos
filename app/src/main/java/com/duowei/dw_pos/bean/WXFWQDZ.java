package com.duowei.dw_pos.bean;

import org.litepal.crud.DataSupport;


/**
 * Created by Administrator on 2017-02-09.
 */

public class WXFWQDZ extends DataSupport{

    /**
     * SIP : ai.wxdw.top
     * WXGZH : djniurou
     * YSID : gh_6c67824128ac
     * BMBH : djnr01
     * storeid : 1134
     * weid : 175
     */

    private String SIP;
    private String WXGZH;
    private String YSID;
    private String BMBH;
    private String storeid;
    private String weid;

    public String getSIP() {
        return SIP;
    }

    public void setSIP(String SIP) {
        this.SIP = SIP;
    }

    public String getWXGZH() {
        return WXGZH;
    }

    public void setWXGZH(String WXGZH) {
        this.WXGZH = WXGZH;
    }

    public String getYSID() {
        return YSID;
    }

    public void setYSID(String YSID) {
        this.YSID = YSID;
    }

    public String getBMBH() {
        return BMBH;
    }

    public void setBMBH(String BMBH) {
        this.BMBH = BMBH;
    }

    public String getStoreid() {
        return storeid;
    }

    public void setStoreid(String storeid) {
        this.storeid = storeid;
    }

    public String getWeid() {
        return weid;
    }

    public void setWeid(String weid) {
        this.weid = weid;
    }
}

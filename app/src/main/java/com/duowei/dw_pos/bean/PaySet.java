package com.duowei.dw_pos.bean;

import org.litepal.crud.DataSupport;

/**
 * Created by Administrator on 2017-03-24.
 */

public class PaySet extends DataSupport{

    /**
     * PID : 2015010600023634
     * BY1 : 99999
     * BY2 : 厦门多维软件A
     * BY3 : 75
     * FWQDZ : pay.wxdw.top
     * BY6 :
     * BY7 : 服务商接口
     */

    public String PID;
    public String BY1;
    public String BY2;
    public String BY3;
    public String FWQDZ;
    public String BY6;
    public String BY7;

    public String getPID() {
        return PID;
    }

    public void setPID(String PID) {
        this.PID = PID;
    }

    public String getBY1() {
        return BY1;
    }

    public void setBY1(String BY1) {
        this.BY1 = BY1;
    }

    public String getBY2() {
        return BY2;
    }

    public void setBY2(String BY2) {
        this.BY2 = BY2;
    }

    public String getBY3() {
        return BY3;
    }

    public void setBY3(String BY3) {
        this.BY3 = BY3;
    }

    public String getFWQDZ() {
        return FWQDZ;
    }

    public void setFWQDZ(String FWQDZ) {
        this.FWQDZ = FWQDZ;
    }

    public String getBY6() {
        return BY6;
    }

    public void setBY6(String BY6) {
        this.BY6 = BY6;
    }

    public String getBY7() {
        return BY7;
    }

    public void setBY7(String BY7) {
        this.BY7 = BY7;
    }
}

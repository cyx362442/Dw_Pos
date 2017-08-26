package com.duowei.dw_pos.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Administrator on 2017-03-23.
 */

public class TableUse implements Parcelable{
    /**
     * scjc : 19984
     * csmc : 4
     * WMDBH : pad201703091659218
     * XTBZ : 1
     * YHBH : 罗骏
     * ZH : 24,
     * YS : 22.00
     * JYSJ : 20170309T16:59:17
     * SFYJZ : 0
     * JSJ : pad
     * JCRS : 1
     * jcfs : 堂食
     * by1 :
     * by2 :
     * JSKSSJ : 20170309T16:59:17
     * BY6 :
     * SFFS :
     * BY12 : 1
     */
    private String scjc;
    private String csmc;
    private String WMDBH;
    private String XTBZ;
    private String YHBH;
    public String ZH;
    public float YS;
    public String JYSJ;
    private String SFYJZ;
    private String JSJ;
    public String JCRS;
    private String jcfs;
    private String by1;
    private String by2;
    private String JSKSSJ;
    private String BY6;
    private String SFFS;
    private String BY12;

    protected TableUse(Parcel in) {
        scjc = in.readString();
        csmc = in.readString();
        WMDBH = in.readString();
        XTBZ = in.readString();
        YHBH = in.readString();
        ZH = in.readString();
        YS = in.readFloat();
        JYSJ = in.readString();
        SFYJZ = in.readString();
        JSJ = in.readString();
        JCRS = in.readString();
        jcfs = in.readString();
        by1 = in.readString();
        by2 = in.readString();
        JSKSSJ = in.readString();
        BY6 = in.readString();
        SFFS = in.readString();
        BY12 = in.readString();
    }

    public static final Creator<TableUse> CREATOR = new Creator<TableUse>() {
        @Override
        public TableUse createFromParcel(Parcel in) {
            return new TableUse(in);
        }

        @Override
        public TableUse[] newArray(int size) {
            return new TableUse[size];
        }
    };

    public String getScjc() {
        return scjc;
    }

    public void setScjc(String scjc) {
        this.scjc = scjc;
    }

    public String getCsmc() {
        return csmc;
    }

    public void setCsmc(String csmc) {
        this.csmc = csmc;
    }

    public String getWMDBH() {
        return WMDBH;
    }

    public void setWMDBH(String WMDBH) {
        this.WMDBH = WMDBH;
    }

    public String getXTBZ() {
        return XTBZ;
    }

    public void setXTBZ(String XTBZ) {
        this.XTBZ = XTBZ;
    }

    public String getYHBH() {
        return YHBH;
    }

    public void setYHBH(String YHBH) {
        this.YHBH = YHBH;
    }

    public String getZH() {
        return ZH;
    }

    public void setZH(String ZH) {
        this.ZH = ZH;
    }

    public float getYS() {
        return YS;
    }

    public void setYS(float YS) {
        this.YS = YS;
    }

    public String getJYSJ() {
        return JYSJ;
    }

    public void setJYSJ(String JYSJ) {
        this.JYSJ = JYSJ;
    }

    public String getSFYJZ() {
        return SFYJZ;
    }

    public void setSFYJZ(String SFYJZ) {
        this.SFYJZ = SFYJZ;
    }

    public String getJSJ() {
        return JSJ;
    }

    public void setJSJ(String JSJ) {
        this.JSJ = JSJ;
    }

    public String getJCRS() {
        return JCRS;
    }

    public void setJCRS(String JCRS) {
        this.JCRS = JCRS;
    }

    public String getJcfs() {
        return jcfs;
    }

    public void setJcfs(String jcfs) {
        this.jcfs = jcfs;
    }

    public String getBy1() {
        return by1;
    }

    public void setBy1(String by1) {
        this.by1 = by1;
    }

    public String getBy2() {
        return by2;
    }

    public void setBy2(String by2) {
        this.by2 = by2;
    }

    public String getJSKSSJ() {
        return JSKSSJ;
    }

    public void setJSKSSJ(String JSKSSJ) {
        this.JSKSSJ = JSKSSJ;
    }

    public String getBY6() {
        return BY6;
    }

    public void setBY6(String BY6) {
        this.BY6 = BY6;
    }

    public String getSFFS() {
        return SFFS;
    }

    public void setSFFS(String SFFS) {
        this.SFFS = SFFS;
    }

    public String getBY12() {
        return BY12;
    }

    public void setBY12(String BY12) {
        this.BY12 = BY12;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(scjc);
        parcel.writeString(csmc);
        parcel.writeString(WMDBH);
        parcel.writeString(XTBZ);
        parcel.writeString(YHBH);
        parcel.writeString(ZH);
        parcel.writeFloat(YS);
        parcel.writeString(JYSJ);
        parcel.writeString(SFYJZ);
        parcel.writeString(JSJ);
        parcel.writeString(JCRS);
        parcel.writeString(jcfs);
        parcel.writeString(by1);
        parcel.writeString(by2);
        parcel.writeString(JSKSSJ);
        parcel.writeString(BY6);
        parcel.writeString(SFFS);
        parcel.writeString(BY12);
    }
}

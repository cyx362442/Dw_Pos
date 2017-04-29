package com.duowei.dw_pos.bean;

import android.text.TextUtils;
import android.util.Log;

/**
 * Created by Administrator on 2017-03-24.
 */

public class WMLSBJB{

    /**
     * WMDBH : CYY20170323151848634
     * XTBZ : 1
     * YHBH : 管理员
     * ZH : 103,
     * YS : 42.00
     * JYSJ : 20170323T15:18:49
     * SFYJZ : 0
     * JSJ : CYY
     * JCRS : 3
     * jcfs :
     * by1 :
     * by2 : 117922
     * JSKSSJ : 20170323T15:18:48
     * BY6 :
     * SFFS :
     * BY12 : 2
     */

    private String WMDBH;
    private String XTBZ;
    private String YHBH;
    private String ZH;
    private float YS;
    private String JYSJ;
    private String SFYJZ;
    private String JSJ;
    private String JCRS;
    private String jcfs;
    private String by1;
    private String by2;
    private String JSKSSJ;
    private String BY6;
    private String SFFS;
    private String BY12 = "";

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

    public WMLSBJB() {
    }

    /**
     * @param wmdbh 单据编号
     * @param yhbh  当前用户名称
     * @param zh    桌号
     * @param sfyjz 是否已结账（0未结账 1已结账）
     * @param jsj   计算机名
     * @param jcrs  就餐人数
     * @param ys    总计金额
     * @param by12  1开台 2点餐 默认1
     * @param jcfs  来客类型
     */
    public WMLSBJB(String wmdbh, String yhbh, String zh, String sfyjz,
                   String jsj, String jcrs, float ys, String by12, String jcfs,String by1) {
        this.WMDBH = wmdbh;
        this.XTBZ = "1";
        this.YHBH = yhbh;
        this.ZH = zh;
//        this.jysj = jysj;
        this.SFYJZ = sfyjz;
        this.JSJ = jsj;
        this.JCRS = jcrs;
        this.YS = ys;
        this.BY12 = by12;
        this.jcfs = jcfs;
        this.by1=by1;
    }
    /**
     * 例子:
     * INSERT INTO GSCYDB.dbo.WMLSBJB (WMDBH, XTBZ, YHBH, ZH, YS, JYSJ, SFYJZ, YSJE, HYKH, JSJ, QBDB, JCRS, ZK, ZR, BCWNJS, SS, HYKDJ, ZKFS, jcfs, by1, by2, by3, by4, by5, JZBZ, JSKSSJ, JSJSSJ, DJLSH, BY6, BY7, BY8, BY9, BY10, BY11, SFFS, BY12, BY13, BY14, BY15, BY16, BY17, BY18, BY19, BY20)
     * VALUES ('ZYB20170221140319921', '1', '店长', '101,', 10.00, '2017-02-21 14:03:24.000', '0', null, null, 'ZYB', null, 1, null, null, null, null, null, null, '堂食', '备注', '329', null, null, null, null, '2017-02-21 14:03:19.000', null, null, '', null, null, null, null, null, '', '2', null, null, null, null, null, null, null, null);
     */
    public String toInsertString() {
        return "INSERT INTO WMLSBJB (WMDBH,           XTBZ,           YHBH,           ZH,           YS,     JYSJ,           SFYJZ,      YSJE, HYKH,      JSJ,      QBDB,     JCRS,     ZK,   ZR,   BCWNJS, SS,   HYKDJ, ZKFS,      jcfs,     by1,by2, by3,  by4,  by5,  JZBZ,       JSKSSJ,    JSJSSJ, DJLSH, BY6, BY7,  BY8,  BY9,  BY10, BY11, SFFS,    BY12,      BY13, BY14, BY15, BY16, BY17, BY18, BY19, BY20) " +
                "       VALUES ('" + WMDBH + "', '" + XTBZ + "', '" + YHBH + "', '" + ZH + "', " + YS + ", GETDATE(), '" + SFYJZ + "', null, null, '" + JSJ + "', null, " + JCRS + ", null, null, null,   null, null,  null, '" + jcfs + "', '"+by1+"', '', null, null, null, null, GETDATE(), null,  null,  '',  null, null, null, null, null, '', '" + BY12 + "', null, null, null, null, null, null, null, null)|";
    }
}

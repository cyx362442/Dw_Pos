package com.duowei.dw_pos.bean;

import android.text.TextUtils;

import com.duowei.dw_pos.tools.DateTimeUtils;
import com.duowei.dw_pos.tools.Users;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017-03-24.
 */

public class WMLSB implements Serializable {

    /**
     * ZSSJ2 : 2017-03-24 09:59:28.057
     * BY3 : 0.0
     * XH : 754101
     * WMDBH : CYY20170323151848634
     * XMBH : 00093
     * XMMC : 乡村鸡皇（6寸铁盘）
     * TM : 00093
     * DW : 份
     * SL : 1.0000
     * DJ : 22.00
     * XJ : 22.00
     * PZ :
     * TCBH :
     * SFYXD : 1
     * XSZT :
     * YSJG : 22.00
     * SYYXM : 管理员
     * sfxs : 1
     * by2 : 06
     * by3 : 0.0
     * by5 : 20170323T15:18:49
     * BY12 :
     * BY13 :
     */

    private String ZSSJ;
    private int BY3;
    private String XH;
    private String WMDBH;
    private String XMBH;
    private String XMMC;
    private String TM = "";
    private String DW;
    private float SL;
    private float DJ;
    private float XJ;
    private String PZ;
    private String TCBH;
    private String SFYXD;
    private String XSZT;
    private float YSJG;
    private String SYYXM;
    private String sfxs;
    private String by2;
    private String by5;
    private String BY12 = "";
    private String BY13;
    private String SFZS;
    private String BY15;
    /** 买赠 1 */
    private String BY17 = "";
    private String BY18 = "";
    /** 加价促销 */
    private String BY21 = "";
    private String TCXMBH;
    private float DWSL;

    private int remote = 0;

    /** 买赠 加价 使用字段 */
    private List<WMLSB> mSubWMLSBList = new ArrayList<>();

    //本地使用字段
    // 购物车序号
    public int index = -1;

    public String getZSSJ() {
        return ZSSJ;
    }

    public void setZSSJ(String ZSSJ) {
        this.ZSSJ = ZSSJ;
    }

    public int getBY3() {
        return BY3;
    }

    public void setBY3(int BY3) {
        this.BY3 = BY3;
    }

    public String getXH() {
        return XH;
    }

    public void setXH(String XH) {
        this.XH = XH;
    }

    public String getWMDBH() {
        return WMDBH;
    }

    public void setWMDBH(String WMDBH) {
        this.WMDBH = WMDBH;
    }

    public String getXMBH() {
        return XMBH;
    }

    public void setXMBH(String XMBH) {
        this.XMBH = XMBH;
    }

    public String getXMMC() {
        return XMMC;
    }

    public void setXMMC(String XMMC) {
        this.XMMC = XMMC;
    }

    public String getTM() {
        return TM;
    }

    public void setTM(String TM) {
        this.TM = TM;
    }

    public String getDW() {
        return DW;
    }

    public void setDW(String DW) {
        this.DW = DW;
    }

    public float getSL() {
        return SL;
    }

    public void setSL(float SL) {
        this.SL = SL;
    }

    public float getDJ() {
        return DJ;
    }

    public void setDJ(float DJ) {
        this.DJ = DJ;
    }

    public float getXJ() {
        return XJ;
    }

    public void setXJ(float XJ) {
        this.XJ = XJ;
    }

    public String getPZ() {
        if (TextUtils.isEmpty(PZ))
            return "";
        return PZ;
    }

    public void setPZ(String PZ) {
        this.PZ = PZ;
    }

    public String getTCBH() {
        if (TextUtils.isEmpty(TCBH))
            return "";
        return TCBH;
    }

    public void setTCBH(String TCBH) {
        this.TCBH = TCBH;
    }

    public String getSFYXD() {
        return SFYXD;
    }

    public void setSFYXD(String SFYXD) {
        this.SFYXD = SFYXD;
    }

    public String getXSZT() {
        return XSZT;
    }

    public void setXSZT(String XSZT) {
        this.XSZT = XSZT;
    }

    public float getYSJG() {
        return YSJG;
    }

    public void setYSJG(float YSJG) {
        this.YSJG = YSJG;
    }

    public String getSYYXM() {
        return SYYXM;
    }

    public void setSYYXM(String SYYXM) {
        this.SYYXM = SYYXM;
    }

    public String getSfxs() {
        return sfxs;
    }

    public void setSfxs(String sfxs) {
        this.sfxs = sfxs;
    }

    public String getBy2() {
        return by2;
    }

    public void setBy2(String by2) {
        this.by2 = by2;
    }

    public String getBy5() {
        if (TextUtils.isEmpty(by5)) {
            return "";
        }
        return by5;
    }

    public void setBy5(String by5) {
        this.by5 = by5;
    }

    public String getBY12() {
        return BY12;
    }

    public void setBY12(String BY12) {
        this.BY12 = BY12;
    }

    public String getBY13() {
        if (TextUtils.isEmpty(BY13)) {
            return "";
        }
        return BY13;
    }

    public void setBY13(String BY13) {
        this.BY13 = BY13;
    }

    public String getBY15() {
        if (TextUtils.isEmpty(BY15))
            return "";
        return BY15;
    }

    public String getSFZS() {
        if (TextUtils.isEmpty(SFZS)) {
            return "";
        }
        return SFZS;
    }

    public void setSFZS(String SFZS) {
        this.SFZS = SFZS;
    }

    public void setBY15(String BY15) {
        this.BY15 = BY15;
    }

    public String getTCXMBH() {
        if (TextUtils.isEmpty(TCXMBH))
            return "";
        return TCXMBH;
    }

    public void setTCXMBH(String TCXMBH) {
        this.TCXMBH = TCXMBH;
    }

    public float getDWSL() {
        return DWSL;
    }

    public void setDWSL(float DWSL) {
        this.DWSL = DWSL;
    }

    public List<WMLSB> getSubWMLSBList() {

        return mSubWMLSBList;
    }

    public String getBY17() {
        return BY17;
    }

    public void setBY17(String BY17) {
        this.BY17 = BY17;
    }

    public String getBY18() {
        return BY18;
    }

    public void setBY18(String BY18) {
        this.BY18 = BY18;
    }

    public String getBY21() {
        return BY21;
    }

    public void setBY21(String BY21) {
        this.BY21 = BY21;
    }

    public int getRemote() {
        return remote;
    }

    public void setRemote(int remote) {
        this.remote = remote;
    }

    public WMLSB() {
    }

    /**
     * 添加 单品 到 点单临时表明细信息
     *
     * @param jyxmsz
     */
    public WMLSB(JYXMSZ jyxmsz) {
        this.XMBH = jyxmsz.XMBH;
        this.XMMC = jyxmsz.XMMC;
        this.DW = jyxmsz.DW;
        this.DJ = jyxmsz.XSJG;
        this.YSJG = jyxmsz.XSJG;
        this.sfxs = "1";
        this.TM = jyxmsz.TM;
        this.by2 = jyxmsz.LBBM;
        this.BY3 = Math.round(jyxmsz.YHJ);
        this.SL = 1;
        this.DWSL = this.SL;

        this.SFYXD = "0";

        // 称重
        if (!TextUtils.isEmpty(jyxmsz.getBY3())) {
            setBY12(jyxmsz.getBY3());
        }
    }

    /**
     * 添加 套餐 到 点单临时表明细信息
     *
     * @param tcsd 套餐主项（主项）
     * @param sfxs 主项 1 子项 0
     * @param tcbh 当前时间
     */
    public WMLSB(TCSD tcsd, String sfxs, String tcbh) {
        this.XMBH = tcsd.XMBH1;
        this.TCXMBH = tcsd.XMBH;
        this.DW = tcsd.DW1;
        this.DJ = tcsd.DJ;
        this.YSJG = tcsd.DJ;
        this.sfxs = sfxs;
        if (this.sfxs.equals("1")) {
            this.XMMC = tcsd.XMMC1;
        } else {
            this.XMMC = "  " + tcsd.XMMC1;
        }
        this.BY15 = tcsd.TM;
        this.TCBH = tcbh;
        this.DWSL = tcsd.SL;
        this.by2 = tcsd.LBBM;
//        this.by3 = tcsd.
        this.SL = tcsd.SL;

        this.SFYXD = "0";
    }


    /**
     *
     */
    public String toInsertString() {
        String datetime1 = DateTimeUtils.getCurrentDatetime();
        String datetime2 = datetime1 + "1";

        if (mSubWMLSBList.size() > 0) {
            // 有加价促销项
            if ("加价促销".equals(mSubWMLSBList.get(0).getBY13())) {
                setBY18(datetime1);
                setBY21("1-" + datetime2);
            }
        }

        if ("赠送".equals(getBY13())) {
            setZSSJ("GETDATE()");
        } else {
            setZSSJ(null);
        }

        String mainSql = "INSERT INTO WMLSB (WMDBH,           XMBH,           XMMC,           TM,           DW,          SL,         DJ,                           XJ,          PZ,                TCBH,             SFYXD, XSZT, FTJE,        YSJG,           SFZS,              SYYXM,      SQRXM, ZSSJ,            DWSL,           sfxs,      by1,            by2,            by3,        by4,   by5,      SJC,  BY6,  BY7,  BY8,  BY9,  BY10, BY11,         TCXMBH,             BY12,               BY13, PBJSJM, PBXH, BY14,     BY15,       BY16,        BY17,                 BY18,     BY19, BY20, BY21, BY22, BY23, BY24, BY25) " +
                "               VALUES ('" + WMDBH + "', '" + XMBH + "', '" + XMMC + "', '" + TM + "', '" + DW + "', " + SL + ", " + DJ + ", " + getDJ() * getSL() + ", '" + getPZ() + "', '" + getTCBH() + "', '" + SFYXD + "', '', null, " + YSJG + ", '" + getSFZS() + "', '" + SYYXM + "', null, " + getZSSJ() + ", " + DWSL + ", '" + sfxs + "', null, '" + by2 + "', " + getBY3() + ", null, GETDATE(), null, null, null, null, null, null, null, '" + getTCXMBH() + "', '"+ getBY12() +"', '" + getBY13() + "', null, null, null, '" + getBY15() + "', null, '" + getBY17() + "', '" + getBY18() + "', null, null, '" + getBY21() + "', null, null, null, null)|";

        if (mSubWMLSBList.size() > 0) {
            // 有加价促销项
            if ("加价促销".equals(mSubWMLSBList.get(0).getBY13())) {
                mainSql += update(getWMDBH());
            }
        }

        String subTotalSql = "";
        for (int i = 0; i < mSubWMLSBList.size(); i++) {
            WMLSB subWmlsb = mSubWMLSBList.get(i);
            subWmlsb.setWMDBH(getWMDBH());
            subWmlsb.setSYYXM(Users.YHMC);

            if ("加价促销".equals(subWmlsb.getBY13())) {
                subWmlsb.setBY18(datetime2);
                subWmlsb.setBY21("2-" + datetime1);
            }

            subTotalSql += subWmlsb.toInsertString();

            if ("加价促销".equals(subWmlsb.getBY13())) {
                subTotalSql += update(subWmlsb.getWMDBH());
            }
        }

        return mainSql + subTotalSql;
    }

    /**
     * 加价促销 使用
     *
     * @param wmdbh
     * @return
     */
    private String update(String wmdbh) {
        return "update wmlsb " +
                "set by21 = substring(by21, 1, 2) + convert(varchar(10), a.xh), by18 = '' from (select xh, by18 " +
                "                                                                               from wmlsb " +
                "                                                                               where wmdbh = '" + wmdbh + "') a " +
                "where wmdbh = '" + wmdbh + "' and isnull(by21, '') <> '' and isnull(wmlsb.by18, '') <> '' and " +
                "      replace(replace(by21, '1-', ''), '2-', '') = a.by18|";
    }
}

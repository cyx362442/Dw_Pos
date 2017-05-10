package com.duowei.dw_pos.bean;

import java.io.Serializable;

/**
 * Created by Administrator on 2017-03-30.
 */

public class ImsCardMember implements Serializable{

    /**
     * id : 1069
     * from_user : o1_uLv3YeDSLCLcFfxFW3_-pBp8E
     * cardsn : 86165800025505
     * credit1 : 95.00
     * credit2 : 44932.61
     * realname : 宇轩
     * mobile : 15260202690
     * status : 1
     * cardgrade : 银卡会员
     * occupation : 123456
     * createtime : 20170210T00:00:00
     */

    private int id;
    private String from_user;
    private String cardsn;
    private float credit1;
    private float credit2;
    private String realname;
    private String mobile;
    private String status;
    private String cardgrade;
    private String occupation;
    private String createtime;
    private String title;
    private float couponmoney;
    private int SL;
    private int ticket;
    private boolean isSelect;


    public ImsCardMember(int id, String from_user, String cardsn, float credit1, float credit2, String realname, String mobile, String status, String cardgrade, String occupation, String createtime, String title, float couponmoney, int SL, int ticket, boolean isSelect) {
        this.id = id;
        this.from_user = from_user;
        this.cardsn = cardsn;
        this.credit1 = credit1;
        this.credit2 = credit2;
        this.realname = realname;
        this.mobile = mobile;
        this.status = status;
        this.cardgrade = cardgrade;
        this.occupation = occupation;
        this.createtime = createtime;
        this.title = title;
        this.couponmoney = couponmoney;
        this.SL = SL;
        this.ticket = ticket;
        this.isSelect=isSelect;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFrom_user() {
        return from_user;
    }

    public void setFrom_user(String from_user) {
        this.from_user = from_user;
    }

    public String getCardsn() {
        return cardsn;
    }

    public void setCardsn(String cardsn) {
        this.cardsn = cardsn;
    }

    public float getCredit1() {
        return credit1;
    }

    public void setCredit1(float credit1) {
        this.credit1 = credit1;
    }

    public float getCredit2() {
        return credit2;
    }

    public void setCredit2(float credit2) {
        this.credit2 = credit2;
    }

    public String getRealname() {
        return realname;
    }

    public void setRealname(String realname) {
        this.realname = realname;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCardgrade() {
        return cardgrade;
    }

    public void setCardgrade(String cardgrade) {
        this.cardgrade = cardgrade;
    }

    public String getOccupation() {
        return occupation;
    }

    public void setOccupation(String occupation) {
        this.occupation = occupation;
    }

    public String getCreatetime() {
        return createtime;
    }

    public void setCreatetime(String createtime) {
        this.createtime = createtime;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public float getCouponmoney() {
        return couponmoney;
    }

    public void setCouponmoney(float couponmoney) {
        this.couponmoney = couponmoney;
    }

    public int getSL() {
        return SL;
    }

    public void setSL(int SL) {
        this.SL = SL;
    }

    public int getTicket() {
        return ticket;
    }

    public void setTicket(int ticket) {
        this.ticket = ticket;
    }
    public boolean isSelect() {
        return isSelect;
    }

    public void setSelect(boolean select) {
        isSelect = select;
    }
}

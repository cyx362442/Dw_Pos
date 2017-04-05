package com.duowei.dw_pos.bean;

import java.io.Serializable;

/**
 * Created by Administrator on 2017-02-10.
 */

public class YunFu implements Serializable{
    public int id;
    public String fromUser;
    public String cardsn;
    public String cardgrade;
    public String title;
    public float credit1;
    public float credit2;
    public float money;
    public float sl;
    public int ticket;

    public YunFu(int id,String fromUser,String cardsn,String cardgrade, String title, float credit1, float credit2, float money, float sl, int ticket) {
        this.id=id;
        this.fromUser=fromUser;
        this.cardsn=cardsn;
        this.cardgrade = cardgrade;
        this.title = title;
        this.credit1 = credit1;
        this.credit2 = credit2;
        this.money = money;
        this.sl = sl;
        this.ticket = ticket;
    }
}

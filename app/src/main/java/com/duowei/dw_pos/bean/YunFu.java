package com.duowei.dw_pos.bean;

import java.io.Serializable;

/**
 * Created by Administrator on 2017-02-10.
 */

public class YunFu implements Serializable{
    public String cardgrade;
    public String title;
    public float credit1;
    public float credit2;
    public float money;
    public int sl;
    public int ticket;

    public YunFu(String cardgrade, String title, float credit1, float credit2, float money, int sl, int ticket) {
        this.cardgrade = cardgrade;
        this.title = title;
        this.credit1 = credit1;
        this.credit2 = credit2;
        this.money = money;
        this.sl = sl;
        this.ticket = ticket;
    }
}

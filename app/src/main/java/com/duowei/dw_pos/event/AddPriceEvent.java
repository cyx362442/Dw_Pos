package com.duowei.dw_pos.event;

/**
 * Created by Administrator on 2017-03-31.
 */

public class AddPriceEvent {
    public final int who;

    public AddPriceEvent(int who) {
        this.who = who;
    }
}

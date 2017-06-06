package com.duowei.dw_pos.event;

/**
 * 赠送、加价
 */

public class AddEvent {
    private int type;
    public float num;
    public AddEvent(int type,float num) {
        this.type = type;
        this.num=num;
    }

    public int getType() {
        return type;
    }
}

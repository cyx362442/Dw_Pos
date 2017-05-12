package com.duowei.dw_pos.event;

/**
 * 赠送、加价
 */

public class AddEvent {
    private int type;

    public AddEvent(int type) {
        this.type = type;
    }

    public int getType() {
        return type;
    }
}

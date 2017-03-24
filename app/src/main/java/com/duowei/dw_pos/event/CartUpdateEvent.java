package com.duowei.dw_pos.event;

/**
 * Created by Administrator on 2017-03-24.
 */

public class CartUpdateEvent {
    public final String message;

    public CartUpdateEvent(String message) {
        this.message = message;
    }
}

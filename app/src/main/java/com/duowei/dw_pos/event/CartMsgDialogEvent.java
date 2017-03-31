package com.duowei.dw_pos.event;

/**
 * Created by Administrator on 2017-03-31.
 */

public class CartMsgDialogEvent {
    public final String title;
    public final String message;

    public CartMsgDialogEvent(String title, String message) {
        this.title = title;
        this.message = message;
    }
}

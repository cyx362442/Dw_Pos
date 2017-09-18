package com.duowei.dw_pos.event;

/**
 * Created by Administrator on 2017-09-18.
 */

public class HideLoad {
    private boolean isShow;

    public HideLoad(boolean isShow) {
        this.isShow = isShow;
    }

    public boolean isShow() {
        return isShow;
    }
}

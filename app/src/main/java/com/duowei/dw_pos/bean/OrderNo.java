package com.duowei.dw_pos.bean;

/**
 * 单号
 *
 * Created by Administrator on 2017-05-08.
 */

public class OrderNo {

    private String wmdbh;
    private boolean created = false;

    public OrderNo(String wmdbh, boolean created) {
        this.wmdbh = wmdbh;
        this.created = created;
    }

    public String getWmdbh() {
        return wmdbh;
    }

    /**
     * @return 单号在服务器已创建
     */
    public boolean isCreated() {
        return created;
    }

    /**
     * @param created true，单号创建成功
     */
    public void setCreated(boolean created) {
        this.created = created;
    }
}

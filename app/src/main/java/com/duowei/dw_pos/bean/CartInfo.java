package com.duowei.dw_pos.bean;

/**
 * 购物车信息
 */

public class CartInfo {
    private int num;
    private float price;

    public CartInfo(int num, float price) {
        this.num = num;
        this.price = price;
    }

    /**
     * @return 购物车数量
     */
    public int getNum() {
        return num;
    }

    /**
     * @return 购物车总的价钱
     */
    public float getPrice() {
        return price;
    }
}

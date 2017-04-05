package com.duowei.dw_pos.bean;

/**
 * Created by Administrator on 2017-02-17.
 */

public class JFGZSZ {
    public int id;
    public int weid;
    /**积分规则
     1 现金消费
     2 储值卡消费
     3 现金消费和储值卡消费金额*/
    public int jfgz;
    /**按金额的百分比设置充值积分*/
    public int jfbfb;
    /**积分来源 0按消费金额获取积分 1按单品设置积分获取积分*/
    public int jfly;
}

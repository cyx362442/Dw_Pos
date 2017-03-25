package com.duowei.dw_pos.bean;

/**
 * 开台信息
 */

public class OpenInfo {
    private String deskNo;
    /**来客类型*/
    private String peopleType;
    /** 就餐人数 */
    private String peopleNum;
    /** 备注 */
    private String remark;

    public OpenInfo(String deskNo, String peopleType, String peopleNum, String remark) {
        this.deskNo = deskNo;
        this.peopleType = peopleType;
        this.peopleNum = peopleNum;
        this.remark = remark;
    }

    public String getDeskNo() {
        return deskNo + ",";
    }

    public String getPeopleType() {
        return peopleType;
    }

    public String getPeopleNum() {
        return peopleNum;
    }

    public String getRemark() {
        return remark;
    }
}

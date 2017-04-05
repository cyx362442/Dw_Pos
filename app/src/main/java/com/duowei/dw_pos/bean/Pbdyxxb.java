package com.duowei.dw_pos.bean;

/**
 * 平板打印信息表
 */

public class Pbdyxxb {

    /**
     * @param wmdbh
     * @param zh    桌号 例:701,
     * @param jsj   设备名
     * @param jcrs  就餐人数
     * @return 插入平板打印信息表
     * <p>
     * 例子：
     * String insertPBDYXXB = "insert into pbdyxxb(xh,wmdbh,xmbh,xmmc,dw,sl,dj,xj,pz,ysjg,syyxm,sfxs,tcbh,tcxmbh,tcfz,xtbz,czsj,zh,jsj,jcrs)" +
     * "select xh,wmdbh,xmbh,xmmc,dw,sl,dj,xj,pz,ysjg,syyxm,sfxs,tcbh,tcxmbh,BY15,'1',GETDATE(),'" + mZh + "','" + mPinban + "','" + persons + "'from wmlsb where wmdbh='" + mDnum2 + "'and isnull(sfyxd,'0')<>'1'|";
     */
    public static String toInsertString(String wmdbh, String zh, String jsj, String jcrs) {
        return "insert into pbdyxxb(xh,wmdbh,xmbh,xmmc,dw,sl,dj,xj,pz,ysjg,syyxm,sfxs,tcbh,tcxmbh,tcfz,xtbz,czsj,      zh,jsj,jcrs) " +
                "            select xh,wmdbh,xmbh,xmmc,dw,sl,dj,xj,pz,ysjg,syyxm,sfxs,tcbh,tcxmbh,BY15,'1', GETDATE(), '" + zh + "','" + jsj + "'," + jcrs + " " +
                "            from wmlsb where wmdbh='" + wmdbh + "' and sfyxd != '1'|" + updateSfyxd(wmdbh);
    }

    /**
     * 设置已下单
     */
    private static String updateSfyxd(String wmdbh) {
        return "update wmlsb " +
                "set sfyxd = '1' where wmdbh = '" + wmdbh + "'|";
    }
}

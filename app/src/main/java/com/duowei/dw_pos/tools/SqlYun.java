package com.duowei.dw_pos.tools;


import android.util.Log;

/**
 * Created by Administrator on 2017-02-15.
 */

public class SqlYun {
    /**消费记录*/
   public static String insertDeal_record(int weid,String from_user,String JYSJ,String SYJH,String YHMC,String bmbh,String deal_id,float cash,float jezj,float payment1,String xsdh,int uid){
       String sql="INSERT INTO deal_record(weid,from_user,JYSJ,JYLX,SYJH,YHMC,bmbh,deal_id,cash,jezj,payment1,xsdh,uid) \n" +
               "VALUES('"+weid+"','"+from_user+"','"+JYSJ+"','消费' ,'"+SYJH+"' , '"+YHMC+"','"+bmbh+"','"+deal_id+"'," +
               "'"+cash+"','"+jezj+"','"+payment1+"','"+xsdh+"','"+uid+"')|";
       return Base64.getBase64(sql).replaceAll("\n","");
   }
    /**储值卡消费扣减*/
    public static String updateIms_card_members1(float money,int weid,String from_user){
        String sql="update ims_card_members set credit2=Round(credit2-"+money+",2) where weid='"+weid+"' and from_user='"+from_user+"'|";
        return Base64.getBase64(sql).replaceAll("\n","");
    }
    /**储值卡消费记录*/
    public static String insertIms_card_deal_record(int weid,String from_user,String JYSJ,float CZQJE,float KCZJE,float SSJE,float KYE,String SYJH,String YHMC,String bmbh,String deal_id,int uid){
        String sql="INSERT INTO ims_card_deal_record(weid,from_user,JYSJ,JYLX,CZQJE,KCZJE,SSJE,KYE,SYJH,YHMC,bmbh,deal_id,uid) \n" +
                "VALUES('"+weid+"','"+from_user+"' , '"+JYSJ+"', '储值卡消费' ,'"+CZQJE+"' , '"+KCZJE+"' , '"+SSJE+"' , '"+KYE+"'" +
                " , '"+SYJH+"' , '"+YHMC+"','"+bmbh+"','"+deal_id+"','"+uid+"')|";
        return Base64.getBase64(sql).replaceAll("\n","");
    }
    /**电子券使用状态更新*/
    public static String updateIms_card_members_coupon(String receiver,String consumetime,String deal_id,int weid,int couponid,String from_user,int num){
        String sql="Update ims_card_members_coupon set  status = 2 , receiver ='"+receiver+"', consumetime = UNIX_TIMESTAMP('"+consumetime+"') , deal_id ='"+deal_id+"' \n" +
                "where weid ='"+weid+"' and couponid ='"+couponid+"' and from_user ='"+from_user+"' and status=1  and (CURRENT_DATE() between CAST(FROM_UNIXTIME(starttime,'%Y-%m-%d')as datetime) " +
                "and CAST(FROM_UNIXTIME(endtime,'%Y-%m-%d')as datetime) or IFNULL(starttime,0)= 0 or IFNULL(endtime,0)=0)  order by " +
                "CAST(FROM_UNIXTIME((CASE IFNULL(endtime,0) WHEN 0 THEN 1893427200 ELSE endtime END),'%Y-%m-%d')as datetime)  limit "+num+" |";
        return Base64.getBase64(sql).replaceAll("\n","");
    }
    /**电子券使用记录*/
    public static String insertCoupon_deal_record(int weid,int couponid,String from_user,String jysj,int sl,
                                                  String syjh,String yhmc,String bmbh,String deal_id,String title,
                                                  float jyje,String bz,int uid){
        String sql="insert into coupon_deal_record(weid,couponid,from_user,jysj,jylx,sl,syjh,yhmc,bmbh,deal_id,title,jyje,bz,uid)" +
                "values('"+weid+"','"+couponid+"','"+from_user+"','"+jysj+"','券消费',"+sl+",'"+syjh+"' , '"+yhmc+"' , '"+bmbh+"','"+deal_id+"','"+title+"','"+jyje+"','"+bz+"','"+uid+"')|";
        return Base64.getBase64(sql).replaceAll("\n","");
    }
    /**积分获得*/
    public static String updateIms_card_members2(int jfbfb,int weid,String from_user){
        String sql="update ims_card_members set credit1=Round(credit1+"+jfbfb+",2) where weid="+weid+" and from_user='"+from_user+"'|";
        return Base64.getBase64(sql).replaceAll("\n","");
    }
    /**积分获得记录*/
    public static String insertIms_card_jf_record(int weid,String from_user,String jysj,String jylx,float czqjf,float kczjf,
                                                  float syjf,String syjh,String yhmc,String bmbh,String deal_id,int uid){
        String sql="INSERT INTO ims_card_jf_record(weid,from_user,jysj,jylx,czqjf,kczjf,syjf,syjh,yhmc,bmbh,deal_id,uid)" +
                "VALUES("+weid+",'"+from_user+"','"+jysj+"', '"+jylx+"',"+czqjf+","+kczjf+" , "+syjf+" ,'"+syjh+"','"+yhmc+"','"+bmbh+"','"+deal_id+"',"+uid+")|";
        return Base64.getBase64(sql).replaceAll("\n","");
    }
    public static String sqlYun;//mySql语句集合
    public static String sqlLocal;//sql server语句集合
    public static String WMBS;//交易记录id
    public static float CZQJE;//卡余额
    public static float KCZJE;//己付
    public static float CZKYE;//未付
    public static String HYBH;//卡号
    public static String HYKDJ;//卡等级
    public static int jfbfb_add=0;//获得积分
    public static int jfbfb_sub=0;//积分消费
    public static String from_user;
    public static String JZBZ;
}

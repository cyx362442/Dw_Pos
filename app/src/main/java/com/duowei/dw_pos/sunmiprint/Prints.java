package com.duowei.dw_pos.sunmiprint;

import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.RemoteException;

import com.duowei.dw_pos.bean.Moneys;
import com.duowei.dw_pos.bean.WMLSB;
import com.duowei.dw_pos.bean.WMLSBJB;
import com.duowei.dw_pos.bean.Wmslbjb_jiezhang;
import com.duowei.dw_pos.tools.DateTimes;
import com.duowei.dw_pos.tools.Users;

import java.util.List;
import java.util.Locale;

import woyou.aidlservice.jiuiv5.ICallback;
import woyou.aidlservice.jiuiv5.IWoyouService;

/**
 * Created by Administrator on 2017-03-27.
 */

public class Prints {
    private Prints(){}
    private static Prints p;
    public static Prints getPrinter(){
        if(p==null){
            p=new Prints();
        }
        return p;
    }

    private Wmslbjb_jiezhang mWmlsbjb;
    private WMLSB[] mWmlsbs;

    private IWoyouService woyouService;
    public static ICallback callback = null;
    /**打印列数、每列宽度、每列的对齐方式*/
    private String[] text = new String[3];
    private int[] width = new int[]{20, 6, 8};
    private int[] align = new int[]{0, 0, 0};
    public void bindPrintService(Context context,ServiceConnection connService){
        callback = new ICallback.Stub() {
            @Override
            public void onRunResult(final boolean success) throws RemoteException {
            }
            @Override
            public void onReturnString(final String value) throws RemoteException {
            }
            @Override
            public void onRaiseException(int code, final String msg) throws RemoteException {
            }
        };
        Intent intent = new Intent();
        intent.setPackage("woyou.aidlservice.jiuiv5");
        intent.setAction("woyou.aidlservice.jiuiv5.IWoyouService");
        context.startService(intent);
        context.bindService(intent, connService, Context.BIND_AUTO_CREATE);
    }

    public void setWoyouService(IWoyouService woyouService){
        this.woyouService=woyouService;
    }

    public void setPrintMsg(Wmslbjb_jiezhang wmlsbjb,WMLSB[] wmlsb){
        this.mWmlsbjb=wmlsbjb;
        this.mWmlsbs=wmlsb;
    }

    public void print_yudayin(){
        ThreadPoolManager.getInstance().executeTask(new Runnable() {
            @Override
            public void run() {
                if(woyouService==null){
                    return;
                }
                try {
                    woyouService.setAlignment(1, callback);
                    woyouService.printTextWithFont("桌号：" + mWmlsbjb.getZH() + "\n", "", 32, callback);
                    woyouService.setAlignment(0, callback);
                    woyouService.printTextWithFont("账单号：" + mWmlsbjb.getWMDBH() + "\n", "", 28, callback);
                    woyouService.printTextWithFont("日期：" + mWmlsbjb.getJYSJ() + "\n", "", 28, callback);
                    woyouService.printTextWithFont("点单员：" + Users.YHMC + "    人数：" + mWmlsbjb.getJCRS() + "\n", "", 28, callback);
                    woyouService.sendRAWData(BytesUtil.initLine1(384, 1), callback);
                    //_______________________________________________________________________________________________________________________________
                    text[0] = "单品名称";
                    text[1] = "数量";
                    text[2] = "金额";
                    woyouService.printColumnsText(text, width, align, callback);
                    for (int i = 0; i < mWmlsbs.length; i++) {
                        text[0] = mWmlsbs[i].getXMMC();
                        text[1] = mWmlsbs[i].getSL() + "";
                        text[2] = mWmlsbs[i].getXJ() + "";
                        woyouService.printColumnsText(text, width, align, callback);
                    }
                    woyouService.sendRAWData(BytesUtil.initLine1(384, 1), callback);
                    //______________________________________________________________________________
                    woyouService.printTextWithFont("原价合计：" + String.format(Locale.CANADA, "%.2f", Moneys.xfzr) + "\n", "", 30, callback);
                    woyouService.printTextWithFont("折扣：" + String.format(Locale.CANADA, "%.2f", Moneys.zkjr) + "\n", "", 30, callback);
                    woyouService.printTextWithFont("应付：" + String.format(Locale.CANADA, "%.2f", Moneys.ysjr) + "\n", "", 30, callback);
                    woyouService.sendRAWData(BytesUtil.initLine1(384, 1), callback);
                    woyouService.setAlignment(1, callback);
                    //______________________________________________________________________________
                    woyouService.printTextWithFont("此单据不作结账单使用", "", 32, callback);
                    woyouService.lineWrap(4, callback);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        });
    }
    //现金\支付宝、微信结账
    public void print_jiezhang(final String ys, final String sx, final String zl, final String payStyle){
        ThreadPoolManager.getInstance().executeTask(new Runnable() {
            @Override
            public void run() {
                if(woyouService==null){
                    return;
                }
                try {
                    woyouService.setAlignment(1, callback);// 对齐方式
                    woyouService.printTextWithFont("桌号：" + mWmlsbjb.getZH() + "\n", "", 32, callback);
                    woyouService.setAlignment(0, callback);
                    woyouService.printTextWithFont("账单号：" + mWmlsbjb.getWMDBH() + "\n", "", 28, callback);
                    woyouService.printTextWithFont("日期：" + mWmlsbjb.getJYSJ() + "\n", "", 28, callback);
                    woyouService.printTextWithFont("收银员：" + Users.YHMC + "    人数：" + mWmlsbjb.getJCRS() + "\n", "", 28, callback);
                    woyouService.sendRAWData(BytesUtil.initLine1(384, 1), callback);
                    //_______________________________________________________________________________________________________________________________
                    text[0] = "单品名称";
                    text[1] = "数量";
                    text[2] = "金额";
                    woyouService.printColumnsText(text, width, align, callback);
                    for (int i = 0; i < mWmlsbs.length; i++) {
                        text[0] = mWmlsbs[i].getXMMC();
                        text[1] = mWmlsbs[i].getSL() + "";
                        text[2] = mWmlsbs[i].getXJ() + "";
                        woyouService.printColumnsText(text, width, align, callback);
                    }
                    woyouService.sendRAWData(BytesUtil.initLine1(384, 1), callback);
                    //_________________________________________________________________________________
                    woyouService.printTextWithFont("原价合计：" + String.format(Locale.CANADA, "%.2f", Moneys.xfzr) + "\n", "", 30, callback);
                    woyouService.printTextWithFont("折扣：" + String.format(Locale.CANADA, "%.2f", Moneys.zkjr) + "\n", "", 30, callback);
                    woyouService.printTextWithFont("应付：" + String.format(Locale.CANADA, "%.2f", Moneys.ysjr) + "\n", "", 30, callback);
                    woyouService.sendRAWData(BytesUtil.initLine1(384, 1), callback);
                    //_________________________________________________________________________________
                    woyouService.printTextWithFont("应收现金:￥" + ys + "\n", "", 30, callback);
                    if(payStyle.equals("收现")){
                        woyouService.printTextWithFont(payStyle+":￥"+sx+"  找零:"+zl+"\n","",30,callback);
                    }else{
                        woyouService.printTextWithFont(payStyle+"支付:￥"+sx+"\n","",30,callback);
                    }
                    woyouService.setAlignment(1, callback);// 对齐方式
                    woyouService.lineWrap(1, callback);
                    woyouService.printTextWithFont("谢谢光临！", "", 30, callback);
                    woyouService.lineWrap(4, callback);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    //云会员结账
    public void print_yun(final Wmslbjb_jiezhang wmlsbjb, final List<WMLSB>wmlsbList){
        ThreadPoolManager.getInstance().executeTask(new Runnable() {
            @Override
            public void run() {
                if(woyouService==null){
                    return;
                }
                try {
                    woyouService.setAlignment(1, callback);
                    woyouService.printTextWithFont("桌号：" +wmlsbjb.getZH() + "\n", "", 32, callback);
                    woyouService.setAlignment(0, callback);
                    woyouService.printTextWithFont("账单号：" + wmlsbjb.getWMDBH() + "\n", "", 28, callback);
                    woyouService.printTextWithFont("日期：" + DateTimes.getTime2() + "\n", "", 28, callback);
                    woyouService.printTextWithFont("点单员：" + Users.YHMC + "    人数：" + wmlsbjb.getJCRS() + "\n", "", 28, callback);
                    woyouService.sendRAWData(BytesUtil.initLine1(384, 1), callback);
                    //_______________________________________________________________________________________________________________________________
                    text[0] = "单品名称";
                    text[1] = "数量";
                    text[2] = "金额";
                    woyouService.printColumnsText(text, width, align, callback);
                    for (int i = 0; i < wmlsbList.size(); i++) {
                        text[0] = wmlsbList.get(i).getXMMC();
                        text[1] = wmlsbList.get(i).getSL() + "";
                        text[2] = wmlsbList.get(i).getDJ()*wmlsbList.get(i).getSL() + "";
                        woyouService.printColumnsText(text, width, align, callback);
                    }
                    woyouService.sendRAWData(BytesUtil.initLine1(384, 1), callback);
                    //_________________________________________________________________________________
                    woyouService.printTextWithFont("原价合计：" + String.format(Locale.CANADA, "%.2f", Moneys.xfzr) + "\n", "", 30, callback);
                    woyouService.printTextWithFont("折扣：" + String.format(Locale.CANADA, "%.2f", Moneys.zkjr) + "\n", "", 30, callback);
                    woyouService.printTextWithFont("应付：" + String.format(Locale.CANADA, "%.2f", Moneys.ysjr) + "\n", "", 30, callback);
                    woyouService.sendRAWData(BytesUtil.initLine1(384, 1), callback);
                    //_________________________________________________________________________________
                    woyouService.printTextWithFont("应收现金:￥" + getZj(wmlsbList) + "\n", "", 30, callback);
                    woyouService.printTextWithFont("云会员支付:￥"+getZj(wmlsbList)+"\n","",30,callback);
                    woyouService.setAlignment(1, callback);// 对齐方式
                    woyouService.lineWrap(1, callback);
                    woyouService.printTextWithFont("谢谢光临！", "", 30, callback);
                    woyouService.lineWrap(4, callback);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        });
    }
    //提交订单打印
    public void print_commit(final WMLSBJB wmlsbjb, final List<WMLSB>wmlsbList){
        ThreadPoolManager.getInstance().executeTask(new Runnable() {
            @Override
            public void run() {
                if(woyouService==null){
                    return;
                }
                try {
                     woyouService.setAlignment(1, callback);
                    woyouService.printTextWithFont("桌号：" +wmlsbjb.getZH() + "\n", "", 32, callback);
                    woyouService.setAlignment(0, callback);
                    woyouService.printTextWithFont("账单号：" + wmlsbjb.getWMDBH() + "\n", "", 28, callback);
                    woyouService.printTextWithFont("日期：" + DateTimes.getTime2() + "\n", "", 28, callback);
                    woyouService.printTextWithFont("点单员：" + Users.YHMC + "    人数：" + wmlsbjb.getJCRS() + "\n", "", 28, callback);
                    woyouService.sendRAWData(BytesUtil.initLine1(384, 1), callback);
                    //_______________________________________________________________________________________________________________________________
                    text[0] = "单品名称";
                    text[1] = "数量";
                    text[2] = "金额";
                    woyouService.printColumnsText(text, width, align, callback);
                    for (int i = 0; i < wmlsbList.size(); i++) {
                        text[0] = wmlsbList.get(i).getXMMC();
                        text[1] = wmlsbList.get(i).getSL() + "";
                        text[2] = wmlsbList.get(i).getDJ()*wmlsbList.get(i).getSL() + "";
                        woyouService.printColumnsText(text, width, align, callback);
                    }
                    woyouService.sendRAWData(BytesUtil.initLine1(384, 1), callback);
                    //______________________________________________________________________________
                    woyouService.printTextWithFont("消费合计：" + getZj(wmlsbList) + "\n", "", 30, callback);
                    woyouService.sendRAWData(BytesUtil.initLine1(384, 1), callback);
                    woyouService.setAlignment(1, callback);
                    woyouService.printTextWithFont("压桌单", "", 30, callback);
                    woyouService.lineWrap(4, callback);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        });
    }
    //总计金额
    private float getZj(List<WMLSB>wmlsbList){
        float zj=0f;
        for(WMLSB w:wmlsbList){
            zj=zj+w.getDJ()*w.getSL();
        }
        return zj;
    }
}

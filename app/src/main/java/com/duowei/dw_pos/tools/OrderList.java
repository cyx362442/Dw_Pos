package com.duowei.dw_pos.tools;

import android.text.TextUtils;
import android.util.Log;

import com.duowei.dw_pos.bean.AddTcsdItem;
import com.duowei.dw_pos.bean.CartInfo;
import com.duowei.dw_pos.bean.JYXMSZ;
import com.duowei.dw_pos.bean.OpenInfo;
import com.duowei.dw_pos.bean.WMLSB;
import com.duowei.dw_pos.event.CartUpdateEvent;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Created by Administrator on 2017-03-29.
 */

public class OrderList {
    public String sql="";

    public String getSql() {
        return sql;
    }

    private static OrderList mInstance;

    private ArrayList<WMLSB> mList;

    private OpenInfo mOpenInfo;

    private OrderList() {
        mList = new ArrayList<>();
    }

    public static OrderList newInstance() {
        if (mInstance == null) {
            mInstance = new OrderList();
        }

        return mInstance;
    }

    public ArrayList<WMLSB> getList() {
        return mList;
    }

    public void setList(ArrayList<WMLSB>list){
        this.mList=list;
    }

    public void clear() {
        mList.clear();
    }

    public int getSize() {
        return mList.size();
    }

    public CartInfo getCartInfo() {
        int num = 0;
        float price = 0;

        for (int i = 0; i < mList.size(); i++) {
            WMLSB wmlsb = mList.get(i);
            if ("1".equals(wmlsb.getSfxs())) {
                num += wmlsb.getSL();
                price += wmlsb.getDJ() * wmlsb.getSL();
            }
        }

        return new CartInfo(num, price);
    }


    /**
     * 添加单品
     *
     * @param jyxmsz
     */
    public void add(JYXMSZ jyxmsz) {
        // 添加单品
        if (mList.size() == 0) {
            // 购物车第一次添加处理
            mList.add(new WMLSB(jyxmsz));
            EventBus.getDefault().post(new CartUpdateEvent());
            return;
        }

        String xmbh = jyxmsz.getXMBH();
        for (int i = 0; i < mList.size(); i++) {
            WMLSB wmlsb = mList.get(i);
            if ("1".equals(wmlsb.getSfxs()) && TextUtils.isEmpty(wmlsb.getBY15()) && xmbh.endsWith(wmlsb.getXMBH())) {
                // 购物车已存在当前单品
                // 数量+1
                wmlsb.setSL(wmlsb.getSL() + 1);
                EventBus.getDefault().post(new CartUpdateEvent());
                return;
            }
        }
        // 购物车没有当前要添加的单品
        // 直接添加
        mList.add(new WMLSB(jyxmsz));
        EventBus.getDefault().post(new CartUpdateEvent());
    }

    /**
     * 添加套餐
     *
     * @param addTcsdItems
     */
    public void add(ArrayList<AddTcsdItem> addTcsdItems) {
        if (mList.size() == 0) {
            for (int i = 0; i < addTcsdItems.size(); i++) {
                AddTcsdItem addTcsdItem = addTcsdItems.get(i);
                mList.add(new WMLSB(addTcsdItem.tcsd, addTcsdItem.sfxs, addTcsdItem.tcbh));
            }
            EventBus.getDefault().post(new CartUpdateEvent());
            return;
        }

        boolean find = false;
        for (int i = 0; i < mList.size(); i++) {
            WMLSB wmlsb = mList.get(i);
            for (int j = 0; j < addTcsdItems.size(); j++) {
                AddTcsdItem addTcsdItem = addTcsdItems.get(j);
                if (addTcsdItem.tcbh.equals(wmlsb.getTCBH())) {
                    // 购物车已存在添加的套餐
                    find = true;

                    if ("A".equals(wmlsb.getBY15())) {
                        // 主项 +1
                        wmlsb.setSL(wmlsb.getSL() + 1);
                    } else {
                        // 子项 +dwsl
                        wmlsb.setSL(wmlsb.getSL() + wmlsb.getDWSL());
                    }
                }
            }

        }
        EventBus.getDefault().post(new CartUpdateEvent());

        if (!find) {
            for (int i = 0; i < addTcsdItems.size(); i++) {
                AddTcsdItem addTcsdItem = addTcsdItems.get(i);
                mList.add(new WMLSB(addTcsdItem.tcsd, addTcsdItem.sfxs, addTcsdItem.tcbh));
            }
            EventBus.getDefault().post(new CartUpdateEvent());
        }
    }

    /**
     * 购物车详情 添加
     *
     * @param wmlsb
     */
    public void add(WMLSB wmlsb) {
        String by15 = wmlsb.getBY15();

        if (!TextUtils.isEmpty(by15)) {
            // 套餐
            if (by15.equals("A")) {
                // 主
                wmlsb.setSL(wmlsb.getSL() + 1);

                // 子
                for (int i = 0; i < mList.size(); i++) {
                    WMLSB w = mList.get(i);
                    if (w.getTCBH().equals(wmlsb.getTCBH()) && !w.getBY15().equals("A")) {
                        w.setSL(w.getSL() + w.getDWSL());
                    }
                }
            }
        } else {
            // 单品
            wmlsb.setSL(wmlsb.getSL() + 1);
        }

        EventBus.getDefault().post(new CartUpdateEvent());
    }

    public void remove(WMLSB wmlsb) {
        sql="";

        String by15 = wmlsb.getBY15();
        if (!TextUtils.isEmpty(by15)) {
            // 套餐
            if (wmlsb.getSL() == 1) {
                mList.remove(wmlsb);

                Iterator<WMLSB> it = mList.iterator();
                while (it.hasNext()) {
                    WMLSB w = it.next();
                    if (w.getTCBH().equals(wmlsb.getTCBH()) && !w.getBY15().equals("A")) {
                        it.remove();

                        sql=sql+"delete from  wmlsb where TCBH='" + wmlsb.getTCBH() + "'|";
                    }
                }
                sql=sql+"update  wmlsbjb set YS='" + getTotoalMoney()  + "',BY12='2' where wmdbh='" + wmlsb.getWMDBH() + "'|";
            } else {
                // -1
                wmlsb.setSL(wmlsb.getSL() - 1);

                float xj = wmlsb.getDJ() * (wmlsb.getSL());
                sql =sql+ "update  WMLSB set SL='" +(wmlsb.getSL()) + "',XJ='" + xj + "' where tcbh='" + wmlsb.getTCBH() + "' and xh='" + wmlsb.getXH() + "'|";
                for (int i = 0; i < mList.size(); i++) {
                    WMLSB w = mList.get(i);
                    if (w.getTCBH().equals(wmlsb.getTCBH()) && !w.getBY15().equals("A")) {
                        w.setSL(w.getSL() - w.getDWSL());

                        float xj2 = w.getDJ() * (w.getSL());
                        sql =sql+ "update  WMLSB set SL='" +(w.getSL()) + "',XJ='" + xj2 + "' where tcbh='" + w.getTCBH() + "' and xh='" + w.getXH() + "'|";
                    }
                }
                sql=sql+"update  wmlsbjb set YS='" + getTotoalMoney() + "',BY12='2' where wmdbh='" + wmlsb.getWMDBH() + "'|";
            }

        } else {
            // 单品
            if (wmlsb.getSL() == 1) {
                mList.remove(wmlsb);

                sql="update  wmlsbjb set YS=" + getTotoalMoney() + " where wmdbh='" + wmlsb.getWMDBH() + "'|"+
                        "delete from  wmlsb where XH='" + wmlsb.getXH() + "'|";
            } else {
                wmlsb.setSL(wmlsb.getSL() - 1);

                float xj = wmlsb.getSL() * wmlsb.getDJ();
                sql="update  WMLSB set SL='" + wmlsb.getSL() + "',XJ=" + xj + " where XH='" + wmlsb.getXH() + "'|"+
                        "update  wmlsbjb set YS="+getTotoalMoney()+" where wmdbh='" + wmlsb.getWMDBH() + "'|";
            }
        }
    }

    private float getTotoalMoney() {
        float totalMoney=0.00f;
        for(int i=0;i<mList.size();i++){
            float xj = mList.get(i).getSL() * mList.get(i).getDJ();
            totalMoney=totalMoney+xj;
        }
        return totalMoney;
    }

    public OpenInfo getOpenInfo() {
        return mOpenInfo;
    }

    public void setOpenInfo(OpenInfo openInfo) {
        mOpenInfo = openInfo;
    }
}

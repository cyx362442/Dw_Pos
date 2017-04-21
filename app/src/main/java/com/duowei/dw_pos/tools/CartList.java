package com.duowei.dw_pos.tools;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.duowei.dw_pos.bean.AddTcsdItem;
import com.duowei.dw_pos.bean.CXDMXXX;
import com.duowei.dw_pos.bean.CartInfo;
import com.duowei.dw_pos.bean.JYXMSZ;
import com.duowei.dw_pos.bean.MZSZJBXX;
import com.duowei.dw_pos.bean.MZSZMXXX;
import com.duowei.dw_pos.bean.OpenInfo;
import com.duowei.dw_pos.bean.WMLSB;
import com.duowei.dw_pos.bean.WMLSBJB;
import com.duowei.dw_pos.event.AddPriceEvent;
import com.duowei.dw_pos.event.CartMsgDialogEvent;
import com.duowei.dw_pos.event.CartUpdateEvent;
import com.duowei.dw_pos.fragment.AddPriceDialogFragment;

import org.greenrobot.eventbus.EventBus;
import org.joda.time.DateTime;
import org.litepal.crud.DataSupport;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

/**
 * 购物车
 */

public class CartList {
    private static final String TAG = "CartList";

    public static WMLSBJB sWMLSBJB;
    public static List<WMLSB> sWMLSBList = new ArrayList<>();
    private static List<WMLSB> mRemoveRemoteList = new ArrayList<>();

    public String sql = "";

    public String getSql() {
        return sql;
    }

    private static CartList mInstance;

    private ArrayList<WMLSB> mList;

    private OpenInfo mOpenInfo;

    private Context mContext;

    private CartList(Context context) {
        mList = new ArrayList<>();
        mContext = context;
    }

    public static CartList newInstance(Context context) {
        if (mInstance == null) {
            mInstance = new CartList(context.getApplicationContext());
        }

        return mInstance;
    }

    public ArrayList<WMLSB> getList() {
        return mList;
    }

    public void setList(ArrayList<WMLSB> list) {
        this.mList = list;
    }

    public void clear() {
        mList.clear();
        sWMLSBJB = null;
        sWMLSBList.clear();
    }

    public int getSize() {
        return mList.size();
    }

    public CartInfo getCartInfo() {
        int num = 0;
        float price = 0;

        for (int i = 0; i < mList.size(); i++) {
            WMLSB wmlsb = mList.get(i);
            num += wmlsb.getSL();
            price += wmlsb.getDJ() * wmlsb.getSL();

            for (int j = 0; j < wmlsb.getSubWMLSBList().size(); j++) {
                WMLSB subWmlsb1 = wmlsb.getSubWMLSBList().get(j);
                num += subWmlsb1.getSL();
                price += subWmlsb1.getDJ() * subWmlsb1.getSL();
            }
        }

        return new CartInfo(num, Float.valueOf(String.format(Locale.CHINA, "%.2f", price)));
    }


    /**
     * 添加单品
     *
     * @param jyxmsz
     */
    public void add(JYXMSZ jyxmsz) {
        processCxdmxxx(jyxmsz);

        boolean find = false;

        String xmbh = jyxmsz.getXMBH();
        for (int i = 0; i < mList.size(); i++) {
            WMLSB wmlsb = mList.get(i);
            if ("1".equals(wmlsb.getSfxs()) && TextUtils.isEmpty(wmlsb.getBY15()) && xmbh.endsWith(wmlsb.getXMBH())) {
                // 购物车已存在当前单品
                // 数量+1
                wmlsb.setSL(wmlsb.getSL() + 1);

                if (!hasCXDMXXX) {

                    for (int j = 0; j < wmlsb.getSubWMLSBList().size(); j++) {
                        WMLSB subWmlsb = wmlsb.getSubWMLSBList().get(j);
                        subWmlsb.setSL(subWmlsb.getSL() + 1);
                    }
                }

                EventBus.getDefault().post(new CartUpdateEvent());
                find = true;
            }
        }

        if (!find) {
            // 购物车没有当前要添加的单品
            // 直接添加
            WMLSB wmlsb = new WMLSB(jyxmsz);
            mList.add(wmlsb);

            if (!hasCXDMXXX) {
                processMzszjb(wmlsb, jyxmsz);
            }

            EventBus.getDefault().post(new CartUpdateEvent());
        }
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
            if ("赠送".equals(wmlsb.getBY13())) {
                // 买赠项
                EventBus.getDefault().post(new CartMsgDialogEvent("信息提示", "该单品是赠送品,因此您无法修改数量"));

            } else if ("加价促销".equals(wmlsb.getBY13())) {
                // 加价项
                EventBus.getDefault().post(new CartMsgDialogEvent("信息提示", "该单品是加价促销品,因此您无法修改数量"));

            } else {
                wmlsb.setSL(wmlsb.getSL() + 1);

                for (int i = 0; i < wmlsb.getSubWMLSBList().size(); i++) {
                    WMLSB subWmlsb1 = wmlsb.getSubWMLSBList().get(i);
                    subWmlsb1.setSL(subWmlsb1.getSL() + 1);
                }
            }
        }

        EventBus.getDefault().post(new CartUpdateEvent());
    }

    public void remove(WMLSB wmlsb) {
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
                    }
                }

            } else {
                // -1
                wmlsb.setSL(wmlsb.getSL() - 1);
                for (int i = 0; i < mList.size(); i++) {
                    WMLSB w = mList.get(i);
                    if (w.getTCBH().equals(wmlsb.getTCBH()) && !w.getBY15().equals("A")) {
                        w.setSL(w.getSL() - w.getDWSL());
                    }
                }
            }

        } else {
            // 单品
            if ("加价促销".equals(wmlsb.getBY13())) {
                // 加价不允许修改
                EventBus.getDefault().post(new CartMsgDialogEvent("信息提示", "该单品是加价促销品,因此您无法修改数量"));
                return;
            }

            if (wmlsb.getSL() == 1) {
                mList.remove(wmlsb);
                for (int i = 0; i < mList.size(); i++) {
                    List<WMLSB> subWmlsbList = mList.get(i).getSubWMLSBList();
                    for (int j = 0; j < subWmlsbList.size(); j++) {
                        subWmlsbList.remove(wmlsb);
                    }
                }
            } else {
                wmlsb.setSL(wmlsb.getSL() - 1);

                //处理 买赠、加价
                for (int i = 0; i < wmlsb.getSubWMLSBList().size(); i++) {
                    WMLSB subWmlsb1 = wmlsb.getSubWMLSBList().get(i);
                    subWmlsb1.setSL(subWmlsb1.getSL() - 1);
                    if (subWmlsb1.getSL() < 1) {
                        wmlsb.getSubWMLSBList().remove(subWmlsb1);
                    }
                }
            }
        }
        EventBus.getDefault().post(new CartUpdateEvent());
    }

    public OpenInfo getOpenInfo() {
        return mOpenInfo;
    }

    public void setOpenInfo(OpenInfo openInfo) {
        mOpenInfo = openInfo;
    }

    /**
     * 处理 单品 促销单明细信息CXDMXXX
     */
    private void processCxdmxxx(JYXMSZ jyxmsz) {
        hasCXDMXXX = false;

        CXDMXXX cxdmxxx = DataSupport.where("xmbh = ?", jyxmsz.getXMBH()).findFirst(CXDMXXX.class);
        if (cxdmxxx != null) {
            SimpleDateFormat dateSdf = new SimpleDateFormat("yyyyMMdd'T'HH:mm:ss", Locale.CHINA);
            SimpleDateFormat timeSdf = new SimpleDateFormat("HH:mm:ss", Locale.CHINA);

            Calendar calendar = Calendar.getInstance();
            try {
                // 判断 日期 时间
                if (calendar.getTime().after(dateSdf.parse(cxdmxxx.getKSRQ()))
                        && calendar.getTime().before(dateSdf.parse(cxdmxxx.getJSRQ()))
                        && timeSdf.parse(timeSdf.format(calendar.getTime())).after(timeSdf.parse(cxdmxxx.getKSSJ().substring(9)))
                        && timeSdf.parse(timeSdf.format(calendar.getTime())).before(timeSdf.parse(cxdmxxx.getJSSJ().substring(9)))) {
                    // 判断周几
                    int dayOfWeek = new DateTime().getDayOfWeek();
                    if (1 <= dayOfWeek && dayOfWeek <= 7) {
                        String z = (String) CXDMXXX.class.getMethod("getZ" + dayOfWeek).invoke(cxdmxxx);
                        if ("1".equals(z)) {
                            String bz = cxdmxxx.getBZ();
                            if (!TextUtils.isEmpty(bz) && "1".equals(bz)) {
                                // 赠送
                                jyxmsz.setXSJG(0);
                                Log.d(TAG, "processCxdmxxx: " + jyxmsz.getXMMC() + "赠送");
                                hasCXDMXXX = true;
                            } else {
                                // 使用折后价
                                float xsjgzhj = Float.parseFloat(cxdmxxx.getXSJGZHJ());
                                jyxmsz.setXSJG(xsjgzhj);
                                Log.d(TAG, "processCxdmxxx: " + jyxmsz.getXMMC() + " 设置价格 " + xsjgzhj);
                                hasCXDMXXX = true;
                            }
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        } else {
            Log.d(TAG, "processCxdmxxx: " + jyxmsz.getXMMC() + " 没有促销单明细信息");
        }
    }

    private static boolean hasCXDMXXX = false;

    /**
     * 第一次 添加 单品 买赠/加价促销
     *
     * @param jyxmsz
     */
    private void processMzszjb(WMLSB wmlsb, JYXMSZ jyxmsz) {
        MZSZJBXX mzszjbxx = DataSupport.where("xmbh = ?", jyxmsz.getXMBH()).findFirst(MZSZJBXX.class);
        if (mzszjbxx != null) {
            SimpleDateFormat dateSdf = new SimpleDateFormat("yyyyMMdd'T'HH:mm:ss", Locale.CHINA);
            SimpleDateFormat timeSdf = new SimpleDateFormat("HH:mm:ss", Locale.CHINA);

            Calendar calendar = Calendar.getInstance();
            try {
                // 判断 日期 时间
                if (calendar.getTime().after(dateSdf.parse(mzszjbxx.getKSRQ()))
                        && calendar.getTime().before(dateSdf.parse(mzszjbxx.getJSRQ()))
                        && timeSdf.parse(timeSdf.format(calendar.getTime())).after(timeSdf.parse(mzszjbxx.getKSSJ().substring(9)))
                        && timeSdf.parse(timeSdf.format(calendar.getTime())).before(timeSdf.parse(mzszjbxx.getJSSJ().substring(9)))) {
                    // 判断周几
                    int dayOfWeek = new DateTime().getDayOfWeek();
                    if (1 <= dayOfWeek && dayOfWeek <= 7) {
                        String z = (String) MZSZJBXX.class.getMethod("getZ" + dayOfWeek).invoke(mzszjbxx);
                        if ("1".equals(z)) {
                            String jbby1 = mzszjbxx.getJBBY1();
                            String jbby3 = mzszjbxx.getJBBY3();
                            if ("1".equals(jbby1) && Float.valueOf(jbby3) >= 1) {
                                // 买赠
                                processMzJj("1", wmlsb, mzszjbxx.getBM());
                            } else if ("2".equals(jbby1)) {
                                // 加价促销
                                processMzJj("2", wmlsb, mzszjbxx.getBM());
                            }
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            Log.d(TAG, "processMzszjb: " + jyxmsz.getXMMC() + " 没有买赠、加价信息");
        }
    }

    /**
     * 处理 买赠 加价
     */
    private void processMzJj(String jbby1, WMLSB wmlsb, String bm) {
        List<MZSZMXXX> mzszmxxxList = DataSupport.where("bm = ?", bm).find(MZSZMXXX.class);
        if (mzszmxxxList != null && mzszmxxxList.size() > 0) {

            for (int i = 0; i < mzszmxxxList.size(); i++) {
                MZSZMXXX mzszmxxx = mzszmxxxList.get(i);
                JYXMSZ subJyxmsz = DataSupport.where("xmbh = ?", mzszmxxx.getXMBH()).findFirst(JYXMSZ.class);

                if ("1".equals(jbby1)) {
                    // 添加买赠
                    WMLSB subWmlsb = new WMLSB(subJyxmsz);
                    subWmlsb.setSL(Float.valueOf(mzszmxxx.getSL()));
                    subWmlsb.setDJ(0);
                    subWmlsb.setBY13("赠送");
                    subWmlsb.setSFZS("1");
                    subWmlsb.setBY17("7");
                    wmlsb.getSubWMLSBList().clear();
                    wmlsb.getSubWMLSBList().add(subWmlsb);
                    EventBus.getDefault().post(new CartUpdateEvent());
                }
            }

            if ("2".equals(jbby1) && mzszmxxxList.size() > 0) {
                // 加价
                AddPriceDialogFragment.sWMLSB = wmlsb;
                AddPriceDialogFragment.sMZSZMXXXList = mzszmxxxList;
                EventBus.getDefault().post(new AddPriceEvent());
            }
        }
    }

    // ---------------------------------------------------------------------------------------------

    public void removeRemote(WMLSB wmlsb) {
        mRemoveRemoteSql = "";
        mRemoveRemoteList.clear();

        if (TextUtils.isEmpty(wmlsb.getBY15())) {
            // 单品

            for (int i = 0; i < sWMLSBList.size(); i++) {
                if ("赠送".equals(wmlsb.getBY13()) || "加价促销".equals(wmlsb.getBY13())) {
                    mRemoveRemoteSql += "update wmlsb " +
                            "set sl = sl - 1 " +
                            "where xh = " + wmlsb.getXH() + "|";

                    mRemoveRemoteList.add(wmlsb);
                    break;
                }

                WMLSB remote = sWMLSBList.get(i);
                if (wmlsb.getBy5().equals(remote.getBy5())) {
                    mRemoveRemoteSql += "update wmlsb " +
                            "set sl = sl - 1 " +
                            "where wmdbh = '" + remote.getWMDBH() + "' and xh = " + remote.getXH() + "|";

                    mRemoveRemoteList.add(remote);
                }
            }

            if (!TextUtils.isEmpty(wmlsb.getBY21())) {
                String by21 = wmlsb.getBY21().substring(2);
                mRemoveRemoteSql += "update wmlsb " +
                        "set sl = sl - 1 " +
                        "where xh = " + by21 + "|";

                mRemoveRemoteList.add(wmlsb);
            }

        } else {
            // 套餐
            for (int i = 0; i < sWMLSBList.size(); i++) {
                WMLSB remote = sWMLSBList.get(i);
                if (wmlsb.getTCBH().equals(remote.getTCBH())) {
                    mRemoveRemoteSql += "update wmlsb " +
                            "set sl = sl - dwsl " +
                            "where wmdbh = '" + remote.getWMDBH() + "' and tcbh = '" + remote.getTCBH() + "'|";

                    mRemoveRemoteList.add(remote);
                }
            }
        }

        mRemoveRemoteSql += "delete wmlsb " +
                "where sl < 1 and wmdbh = '" + wmlsb.getWMDBH() + "'|";

        mRemoveRemoteSql += "update WMLSB " +
                "set xj = DJ * SL " +
                "where WMDBH = '" + wmlsb.getWMDBH() + "'|";

        mRemoveRemoteSql += "update WMLSBJB " +
                "set YS = (select sum(XJ) from WMLSB where WMDBH = '" + wmlsb.getWMDBH() + "') " +
                "where wmdbh = '" + wmlsb.getWMDBH() + "'|";
    }

    private String mRemoveRemoteSql = "";

    public String getRemoveRemoteSql(String text) {
        String pbdyxxbSql = "";
        for (int i = 0; i < mRemoveRemoteList.size(); i++) {
            WMLSB remote = mRemoveRemoteList.get(i);

            pbdyxxbSql += "insert into pbdyxxb(xh,wmdbh,xmbh,xmmc,dw,sl,dj,xj,pz,ysjg,syyxm,sfxs,tcbh,tcxmbh,tcfz,xtbz,czsj,zh,jsj,thyy) " +
                    "select xh,wmdbh,xmbh,xmmc,dw,'1',dj,'" + remote.getDJ() + "',pz,ysjg,syyxm,sfxs,tcbh,tcxmbh,BY15,'2',getdate(),'" + sWMLSBJB.getZH() + "','" + Users.pad + "','" + text + "'  " +
                    "from wmlsb where wmdbh='" + remote.getWMDBH() + "' and  XH='" + remote.getXH() + "'|";
        }

        return pbdyxxbSql + mRemoveRemoteSql;
    }
}

package com.duowei.dw_pos.tools;

import android.content.Context;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.duowei.dw_pos.bean.OrderNo;
import com.duowei.dw_pos.bean.Pbdyxxb;
import com.duowei.dw_pos.bean.WMLSB;
import com.duowei.dw_pos.bean.WMLSBJB;
import com.duowei.dw_pos.event.CartRemoteUpdateEvent;
import com.duowei.dw_pos.httputils.DownHTTP;
import com.duowei.dw_pos.httputils.VolleyResultListener;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

/**
 * 提交
 */

public class SqlNetHandler {

    private WMLSBJB mWmlsbjb;
    private List<WMLSB> mWmlsbList;

    /**
     * 提交订单
     *
     * @param context
     * @param orderNo 单号
     */
    public void handleCommit(final Context context, final OrderNo orderNo) {
        final CartList cartList = CartList.newInstance(context);
        String localSql = "";

        if (!orderNo.isCreated()) {
            // 第一次开单
            // 点单临时表基本信息WMLSBJB
            // 是否已结账
            mWmlsbjb = new WMLSBJB(
                    orderNo.getWmdbh(),
                    Users.YHMC,
                    cartList.getOpenInfo().getDeskNo(),
                    "0", // 是否已结账
                    Users.pad,
                    cartList.getOpenInfo().getPeopleNum(),
                    cartList.getCartInfo().getPrice(),
                    "1",
                    cartList.getOpenInfo().getPeopleType(),
                    cartList.getOpenInfo().getRemark()
            );
            localSql += mWmlsbjb.toInsertString();
        } else {
            //从服务器上load
            mWmlsbjb = CartList.sWMLSBJB;
        }

        // 点单临时表明细信息WMLSB
        String insertWmlsbSqlSet = "";
        mWmlsbList = cartList.getList();
        for (int i = 0; i < mWmlsbList.size(); i++) {
            WMLSB wmlsb = mWmlsbList.get(i);
            wmlsb.setWMDBH(orderNo.getWmdbh());
            wmlsb.setSYYXM(Users.YHMC);
            wmlsb.setSFYXD("0");  // 是否已下单
            insertWmlsbSqlSet += wmlsb.toInsertString();
        }
        localSql += insertWmlsbSqlSet;

        // 总的价格设置
        localSql += "update WMLSBJB " +
                "set YS = (select sum(XJ) from WMLSB where WMDBH = '" + orderNo.getWmdbh() + "') " +
                "where wmdbh = '" + orderNo.getWmdbh() + "'|";
//        localSql += "update WMLSBJB" +
//                " set YS = " + totalMoney +
//                " where wmdbh = '" + wmdbh + "'|";

//        if (!orderNo.isCreated()) {
        // 平板打印信息表
//        String insertPbdyxxb = Pbdyxxb.toInsertString(
//                orderNo.getWmdbh(),
//                cartList.getOpenInfo().getDeskNo(),
//                Users.pad,
//                cartList.getOpenInfo().getPeopleNum(),
//                sfyxd
//        );

//        localSql += insertPbdyxxb;
//        } else {
//            // 平板打印信息表
//            String insertPbdyxxb = Pbdyxxb.toInsertString(
//                    orderNo.getWmdbh(),
//                    CartList.sWMLSBJB.getZH(),
//                    Users.pad,
//                    CartList.sWMLSBJB.getJCRS(),
//                    sfyxd
//            );
//            localSql += insertPbdyxxb;
//        }

        DownHTTP.postVolley7(Net.url, localSql, new VolleyResultListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(context, "网络错误", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onResponse(String response) {
                if (response.contains("richado")) {
                    Toast.makeText(context, "提交成功！", Toast.LENGTH_SHORT).show();

                    orderNo.setCreated(true);
                    cartList.getList().clear(); // 清空本地没提交数据
                    EventBus.getDefault().post(new CartRemoteUpdateEvent());

//                    if (first) {
//                        EventBus.getDefault().post(new Commit(first, mWmlsbjb));
//                    } else {
//                        EventBus.getDefault().post(new Commit(first, mWmlsbjb, mWmlsbList));
//                    }
                } else {
                    Toast.makeText(context, "提交失败！", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    /**
     * 下单送厨打
     *
     * @param context
     * @param orderNo
     */
    public void handleCommit1(final Context context, OrderNo orderNo) {
        String sql = "";

        String insertPbdyxxb = Pbdyxxb.toInsertString(
                orderNo.getWmdbh(),
                CartList.sWMLSBJB.getZH(),
                Users.pad,
                CartList.sWMLSBJB.getJCRS(),
                "1"
        );
        sql += insertPbdyxxb;

        DownHTTP.postVolley7(Net.url, sql, new VolleyResultListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(context, "网络错误", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onResponse(String response) {
                if (response.contains("richado")) {
                    Toast.makeText(context, "提交成功！", Toast.LENGTH_SHORT).show();

                    EventBus.getDefault().post(new CartRemoteUpdateEvent());

                } else {
                    Toast.makeText(context, "提交失败！", Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}

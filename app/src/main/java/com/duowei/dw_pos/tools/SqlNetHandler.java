package com.duowei.dw_pos.tools;

import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.duowei.dw_pos.DinningActivity;
import com.duowei.dw_pos.bean.Pbdyxxb;
import com.duowei.dw_pos.bean.WMLSB;
import com.duowei.dw_pos.bean.WMLSBJB;
import com.duowei.dw_pos.httputils.DownHTTP;
import com.duowei.dw_pos.httputils.VolleyResultListener;

import java.util.List;

/**
 * 提交
 */

public class SqlNetHandler {

    /**
     * 提交订单
     *
     * @param context
     * @param wmdbh
     * @param first   true, 第一次提交；false，添加新的点单
     */
    public void handleCommit(final Context context, String wmdbh, boolean first) {
        CartList cartList = CartList.newInstance(context);
        String localSql = "";

        if (first) {
            // 点单临时表基本信息WMLSBJB
            WMLSBJB wmlsbjb = new WMLSBJB(
                    wmdbh,
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
            localSql += wmlsbjb.toInsertString();
        }

        // 点单临时表明细信息WMLSB
        String insertWmlsbSqlSet = "";
        List<WMLSB> wmlsbList = cartList.getList();
        for (int i = 0; i < wmlsbList.size(); i++) {
            WMLSB wmlsb = wmlsbList.get(i);
            wmlsb.setWMDBH(wmdbh);
            wmlsb.setSYYXM(Users.YHMC);
            insertWmlsbSqlSet += wmlsb.toInsertString();
        }
        localSql += insertWmlsbSqlSet;

        localSql += "update WMLSBJB " +
                "set YS = (select sum(XJ) from WMLSB where WMDBH = '" + wmdbh + "')|";

        if (first) {
            // 平板打印信息表
            String insertPbdyxxb = Pbdyxxb.toInsertString(
                    wmdbh,
                    cartList.getOpenInfo().getDeskNo(),
                    Users.pad,
                    cartList.getOpenInfo().getPeopleNum()
            );
            localSql += insertPbdyxxb;
        } else {
            // 平板打印信息表
            String insertPbdyxxb = Pbdyxxb.toInsertString(
                    wmdbh,
                    CartList.sWMLSBJB.getZH(),
                    Users.pad,
                    CartList.sWMLSBJB.getJCRS()
            );
            localSql += insertPbdyxxb;
        }

        DownHTTP.postVolley7(Net.url, localSql, new VolleyResultListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(context, "网络错误", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onResponse(String response) {
                if (response.contains("richado")) {
                    Toast.makeText(context, "提交成功！", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(context, DinningActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    context.startActivity(intent);
                } else {
                    Toast.makeText(context, "提交失败！", Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}

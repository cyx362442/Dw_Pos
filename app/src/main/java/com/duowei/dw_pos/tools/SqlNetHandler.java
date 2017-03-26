package com.duowei.dw_pos.tools;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
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
     */
    public void handleCommit(final Context context) {
        CartList cartList = CartList.newInstance();
        String localSql = "";

        SharedPreferences sharedPref = context.getSharedPreferences("user", Context.MODE_PRIVATE);
        String deviceName = sharedPref.getString("pad", "null");

        String currentDatetime = DateTimeUtils.getCurrentDatetime();

        // 点单临时表基本信息WMLSBJB
        WMLSBJB wmlsbjb = new WMLSBJB(
                deviceName + currentDatetime,
                Users.YHMC,
                cartList.getOpenInfo().getDeskNo(),
                "0", // 是否已结账
                deviceName,
                cartList.getOpenInfo().getPeopleNum(),
                cartList.getCartInfo().getPrice(),
                "1",
                cartList.getOpenInfo().getPeopleType(),
                cartList.getOpenInfo().getRemark()
        );
        localSql += wmlsbjb.toInsertString();

        // 点单临时表明细信息WMLSB

        String insertWmlsbSqlSet = "";
        List<WMLSB> wmlsbList = cartList.getList();
        for (int i = 0; i < wmlsbList.size(); i++) {
            WMLSB wmlsb = wmlsbList.get(i);
            wmlsb.setWMDBH(deviceName + currentDatetime);
            wmlsb.setSYYXM(Users.YHMC);
            insertWmlsbSqlSet += wmlsb.toInsertString();
        }
        localSql += insertWmlsbSqlSet;

        // 平板打印信息表
        String insertPbdyxxb = Pbdyxxb.toInsertString(
                deviceName + currentDatetime,
                cartList.getOpenInfo().getDeskNo(),
                deviceName,
                cartList.getOpenInfo().getPeopleNum()
        );
        localSql += insertPbdyxxb;

        DownHTTP.postVolley7(Net.url, localSql, new VolleyResultListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(context, "提交网络错误", Toast.LENGTH_SHORT).show();
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

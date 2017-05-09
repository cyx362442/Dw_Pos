package com.duowei.dw_pos.tools;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.widget.Toast;

import com.duowei.dw_pos.DinningActivity;
import com.duowei.dw_pos.bean.OrderNo;
import com.duowei.dw_pos.bean.Pbdyxxb;
import com.duowei.dw_pos.bean.WMLSB;
import com.duowei.dw_pos.bean.WMLSBJB;
import com.duowei.dw_pos.event.CartUpdateEvent;
import com.duowei.dw_pos.event.Commit;
import com.duowei.dw_pos.httputils.NetUtils;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;

import org.greenrobot.eventbus.EventBus;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

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
    public void handleCommit(final Handler handler, final Context context, final OrderNo orderNo) {
        final CartList cartList = CartList.newInstance(context);
        String localSql = "";

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

        NetUtils.post7(Net.url, localSql, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String result = response.body().string();
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (result.contains("richado")) {
                            Toast.makeText(context, "提交成功！", Toast.LENGTH_SHORT).show();

                            orderNo.setCreated(true);
                            cartList.getList().clear(); // 清空本地没提交数据
                            getWmlsb(handler, orderNo.getWmdbh());

                        } else {
                            Toast.makeText(context, "提交失败！", Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }
        });
    }

    /**
     * 下单送厨打
     *
     * @param context
     * @param orderNo
     */
    public void handleCommit1(final Handler handler, final Context context, OrderNo orderNo) {
        List<WMLSB> allWmlsbList = CartList.sWMLSBList;
        final List<WMLSB> wmlsbList = new ArrayList<>();
        for (int i = 0; i < allWmlsbList.size(); i++) {
            WMLSB item = allWmlsbList.get(i);
            if (!"1".equals(item.getSFYXD())) {
                wmlsbList.add(item);
            }
        }

        String sql = "";

        String insertPbdyxxb = Pbdyxxb.toInsertString(
                orderNo.getWmdbh(),
                CartList.sWMLSBJB.getZH(),
                Users.pad,
                CartList.sWMLSBJB.getJCRS(),
                "1"
        );
        sql += insertPbdyxxb;

        NetUtils.post7(Net.url, sql, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String result = response.body().string();

                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (result.contains("richado")) {
                            Toast.makeText(context, "提交成功！", Toast.LENGTH_SHORT).show();

                            EventBus.getDefault().post(new Commit(false, CartList.sWMLSBJB, wmlsbList));

                            Intent intent = new Intent(context, DinningActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            context.startActivity(intent);

                        } else {
                            Toast.makeText(context, "提交失败！", Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }
        });

    }

    private void getWmlsb(Handler handler, String wmdbh) {
        String sql = "select * from wmlsb where wmdbh = '" + wmdbh + "'|";
        NetUtils.post6(Net.url, sql, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try {
                    String result = response.body().string();

                    result = result.replaceAll("&lt;", "<").replaceAll("&gt;", ">");
                    Type type = new TypeToken<ArrayList<WMLSB>>() {
                    }.getType();
                    List<WMLSB> wmlsbList = new Gson().fromJson(result, type);
                    for (WMLSB e : wmlsbList) {
                        e.setRemote(1);
                    }

                    CartList.sWMLSBList = wmlsbList;
                    EventBus.getDefault().post(new CartUpdateEvent());

                } catch (JsonSyntaxException e) {
                    e.printStackTrace();

                    CartList.sWMLSBList.clear();
                    EventBus.getDefault().post(new CartUpdateEvent());
                }
            }
        });
    }
}

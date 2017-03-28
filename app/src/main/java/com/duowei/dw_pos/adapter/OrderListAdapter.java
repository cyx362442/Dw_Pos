package com.duowei.dw_pos.adapter;

import android.content.Context;
import android.graphics.Color;
import android.net.http.LoggingEventHandler;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.duowei.dw_pos.R;
import com.duowei.dw_pos.bean.Moneys;
import com.duowei.dw_pos.bean.WMLSB;
import com.duowei.dw_pos.dialog.SalesReturnDialog;
import com.duowei.dw_pos.event.CartUpdateEvent;
import com.duowei.dw_pos.event.OrderUpdateEvent;
import com.duowei.dw_pos.httputils.DownHTTP;
import com.duowei.dw_pos.httputils.Post7;
import com.duowei.dw_pos.httputils.VolleyResultListener;
import com.duowei.dw_pos.tools.CartList;
import com.duowei.dw_pos.tools.Net;
import com.google.common.eventbus.EventBus;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Created by Administrator on 2017-03-27.
 */

public class OrderListAdapter extends BaseAdapter {
    Context context;
    ArrayList<WMLSB>list;

    public OrderListAdapter(Context context, ArrayList<WMLSB> list) {
        this.context = context;
        this.list = list;
    }

    public void setList(ArrayList<WMLSB> list) {
        this.list = list;
        notifyDataSetChanged();
    }
    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup) {
        ViewHolder holder = null;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.list_item_cart_detail, viewGroup, false);
            holder = new ViewHolder();
            holder.tv_no = (TextView) convertView.findViewById(R.id.tv_no);
            holder.tv_name = (TextView) convertView.findViewById(R.id.tv_name);
            holder.tv_price = (TextView) convertView.findViewById(R.id.tv_price);
            holder.ll_right = (LinearLayout) convertView.findViewById(R.id.ll_right);
            holder.iv_remove = (ImageView) convertView.findViewById(R.id.iv_remove);
            holder.tv_num = (TextView) convertView.findViewById(R.id.tv_num);
            holder.iv_add = (ImageView) convertView.findViewById(R.id.iv_add);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        final WMLSB item = list.get(position);

        holder.tv_no.setText(String.valueOf(position + 1));

        holder.tv_price.setText(String.valueOf("¥" + item.getDJ() * item.getSL()));

        holder.tv_num.setText(String.valueOf(Math.round(item.getSL())));
        if (!TextUtils.isEmpty(item.getTCBH())) {
            // 套餐主项 子项
            if ("A".equals(item.getBY15())) {
                // 主项
                holder.tv_name.setText(item.getXMMC());
//                holder.ll_right.setVisibility(View.VISIBLE);
                holder.iv_remove.setVisibility(View.VISIBLE);
                holder.iv_add.setVisibility(View.VISIBLE);
            } else {
                // 子项
                holder.tv_name.setText("  " + item.getXMMC());
//                holder.ll_right.setVisibility(View.INVISIBLE);
                holder.iv_remove.setVisibility(View.INVISIBLE);
                holder.iv_add.setVisibility(View.INVISIBLE);
            }
        } else {
            // 单品
            holder.tv_name.setText(item.getXMMC());
            holder.ll_right.setVisibility(View.VISIBLE);
        }
        if(item.getSFYXD().equals("1")){//己下单打印
            holder.tv_no.setTextColor(context.getResources().getColor(R.color.colorAccent));
            holder.tv_name.setTextColor(context.getResources().getColor(R.color.colorAccent));
            holder.tv_price.setTextColor(context.getResources().getColor(R.color.colorAccent));
            holder.iv_add.setVisibility(View.INVISIBLE);
        }else{
            holder.tv_no.setTextColor(Color.parseColor("#4c4c4c"));
            holder.tv_name.setTextColor(Color.parseColor("#4c4c4c"));
            holder.tv_price.setTextColor(Color.parseColor("#4c4c4c"));
            holder.iv_add.setVisibility(View.VISIBLE);
        }

        holder.iv_remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                CartList.newInstance().remove(item);
                new SalesReturnDialog(context,removeSql(item),item);
            }
        });

        holder.iv_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                CartList.newInstance().add(item);
            }
        });

        return convertView;
    }
    private static class ViewHolder {
        TextView tv_no;
        TextView tv_name;
        TextView tv_price;

        LinearLayout ll_right;
        ImageView iv_remove;
        TextView tv_num;
        ImageView iv_add;
    }
    public String removeSql(WMLSB wmlsb) {
        String by15 = wmlsb.getBY15();
        String updateSql="";
        if (!TextUtils.isEmpty(by15)) {
            // 套餐
            if (wmlsb.getSL() == 1) {
                Moneys.yfjr=Moneys.yfjr-wmlsb.getDJ();
                Iterator<WMLSB> it = list.iterator();
                while (it.hasNext()) {
                    WMLSB w = it.next();
                    if (w.getTCBH().equals(wmlsb.getTCBH()) && !w.getBY15().equals("A")) {
                        Moneys.yfjr=Moneys.yfjr-w.getDJ();
                        updateSql=updateSql+"delete from  wmlsb where TCBH='" + wmlsb.getTCBH() + "'|";
                    }
                }
                updateSql=updateSql+"update  wmlsbjb set YS='" + Moneys.yfjr  + "',BY12='2' where wmdbh='" + wmlsb.getWMDBH() + "'|";
            } else {
                // -1
                Moneys.yfjr=Moneys.yfjr-wmlsb.getDJ();
                float xj = wmlsb.getDJ() * (wmlsb.getSL()-1);
                updateSql =updateSql+ "update  WMLSB set SL='" +(wmlsb.getSL()-1) + "',XJ='" + xj + "' where tcbh='" + wmlsb.getTCBH() + "' and xh='" + wmlsb.getXH() + "'|";
                for (int i = 0; i < list.size(); i++) {
                    WMLSB w = list.get(i);
                    if (w.getTCBH().equals(wmlsb.getTCBH()) && !w.getBY15().equals("A")) {

                        float xj2 = w.getDJ() * (w.getSL()-1);
                        Moneys.yfjr=Moneys.yfjr-w.getDJ();
                        updateSql =updateSql+ "update  WMLSB set SL='" +(w.getSL()-1) + "',XJ='" + xj2 + "' where tcbh='" + w.getTCBH() + "' and xh='" + w.getXH() + "'|";
                    }
                }
                updateSql=updateSql+"update  wmlsbjb set YS='" + Moneys.yfjr + "',BY12='2' where wmdbh='" + wmlsb.getWMDBH() + "'|";
            }
        } else {
            // 单品
            Moneys.yfjr=Moneys.yfjr-wmlsb.getDJ();
            if (wmlsb.getSL() == 1) {
                /**己下单打印提交服务器更新*/
                if(wmlsb.getSFYXD().equals("1")){
                    updateSql="update  wmlsbjb set YS=" + Moneys.yfjr + " where wmdbh='" + wmlsb.getWMDBH() + "'|"+
                            "delete from  wmlsb where XH='" + wmlsb.getXH() + "'|";
                }
            } else {
                /**己下单打印提交服务器更新*/
                if(wmlsb.getSFYXD().equals("1")){
                    float xj = (wmlsb.getSL()-1) * wmlsb.getDJ();
                    updateSql="update  WMLSB set SL='" + (wmlsb.getSL()-1) + "',XJ=" + xj + " where XH='" + wmlsb.getXH() + "'|"+
                            "update  wmlsbjb set YS="+Moneys.yfjr+" where wmdbh='" + wmlsb.getWMDBH() + "'|";
                }
            }
        }
        return updateSql;
    }
}

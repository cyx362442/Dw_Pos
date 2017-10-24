package com.duowei.dw_pos.adapter;

import android.content.Context;
import android.graphics.Color;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.duowei.dw_pos.R;
import com.duowei.dw_pos.bean.WMLSB;
import com.duowei.dw_pos.bean.Wmslbjb_jiezhang;
import com.duowei.dw_pos.dialog.SalesReturnDialog;

import java.util.ArrayList;

/**
 * Created by Administrator on 2017-03-27.
 */

public class OrderListAdapter extends BaseAdapter {
    Context context;
    ArrayList<WMLSB> list;
    Wmslbjb_jiezhang wmlsbjb;

    public OrderListAdapter(Context context, ArrayList<WMLSB> list, Wmslbjb_jiezhang wmlsbjb) {
        this.context = context;
        this.list = list;
        this.wmlsbjb = wmlsbjb;
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
            holder.ll_taste = (LinearLayout) convertView.findViewById(R.id.taste_layout);

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
        if ("1".equals(item.getSFYXD())) {//己下单打印
            holder.tv_no.setTextColor(context.getResources().getColor(R.color.colorAccent));
            holder.tv_name.setTextColor(context.getResources().getColor(R.color.colorAccent));
            holder.tv_price.setTextColor(context.getResources().getColor(R.color.colorAccent));
            holder.iv_add.setVisibility(View.GONE);
            holder.ll_taste.setVisibility(View.GONE);
        } else {
            holder.tv_no.setTextColor(Color.parseColor("#4c4c4c"));
            holder.tv_name.setTextColor(Color.parseColor("#4c4c4c"));
            holder.tv_price.setTextColor(Color.parseColor("#4c4c4c"));
            holder.iv_add.setVisibility(View.VISIBLE);
            holder.ll_taste.setVisibility(View.VISIBLE);
        }

        holder.iv_remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                CartList.newInstance().remove(item);

                new SalesReturnDialog(context, item, wmlsbjb);
            }
        });

//        holder.iv_add.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                CartList.newInstance().add(item);
//            }
//        });

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
        LinearLayout ll_taste;
    }
}

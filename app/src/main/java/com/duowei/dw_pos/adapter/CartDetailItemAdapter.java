package com.duowei.dw_pos.adapter;

import android.content.Context;
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
import com.duowei.dw_pos.tools.CartList;

import java.util.List;

/**
 * Created by Administrator on 2017-03-25.
 */

public class CartDetailItemAdapter extends BaseAdapter {

    private Context mContext;
    private List<WMLSB> mList;

    public CartDetailItemAdapter(Context context, List<WMLSB> list) {
        mContext = context;
        mList = list;
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public WMLSB getItem(int position) {
        return mList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.list_item_cart_detail, parent, false);
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

        final WMLSB item = getItem(position);

        holder.tv_no.setText(String.valueOf(position + 1));

        holder.tv_price.setText(String.valueOf("¥" + item.getDJ() * item.getSL()));

        holder.tv_num.setText(String.valueOf(Math.round(item.getSL())));
        if (!TextUtils.isEmpty(item.getTCBH())) {
            // 套餐主项 子项
            if ("A".equals(item.getBY15())) {
                // 主项
                holder.tv_name.setText(item.getXMMC());
                holder.ll_right.setVisibility(View.VISIBLE);
            } else {
                // 子项
                holder.tv_name.setText("  " + item.getXMMC());
                holder.ll_right.setVisibility(View.INVISIBLE);
            }
        } else {
            // 单品
            holder.tv_name.setText(item.getXMMC());
            holder.ll_right.setVisibility(View.VISIBLE);
        }

        holder.iv_remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CartList.newInstance().remove(item);
            }
        });

        holder.iv_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CartList.newInstance().add(item);
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
}

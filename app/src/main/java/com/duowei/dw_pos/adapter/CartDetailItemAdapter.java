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

import com.duowei.dw_pos.CartDetailActivity;
import com.duowei.dw_pos.R;
import com.duowei.dw_pos.bean.WMLSB;
import com.duowei.dw_pos.fragment.TasteChoiceDialogFragment;
import com.duowei.dw_pos.tools.CartList;

import java.util.List;

/**
 * Created by Administrator on 2017-03-25.
 */

public class CartDetailItemAdapter extends BaseAdapter {

    private CartDetailActivity mActivity;
    private List<WMLSB> mList;

    public CartDetailItemAdapter(CartDetailActivity activity, List<WMLSB> list) {
        mActivity = activity;
        mList = list;
    }

    public void setList(List<WMLSB> list) {
        mList = list;
        notifyDataSetChanged();
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
            convertView = LayoutInflater.from(mActivity).inflate(R.layout.list_item_cart_detail, parent, false);
            holder = new ViewHolder();
            holder.tv_no = (TextView) convertView.findViewById(R.id.tv_no);
            holder.tv_name = (TextView) convertView.findViewById(R.id.tv_name);
            holder.tv_price = (TextView) convertView.findViewById(R.id.tv_price);
            holder.ll_right = (LinearLayout) convertView.findViewById(R.id.ll_right);
            holder.iv_remove = (ImageView) convertView.findViewById(R.id.iv_remove);
            holder.tv_num = (TextView) convertView.findViewById(R.id.tv_num);
            holder.iv_add = (ImageView) convertView.findViewById(R.id.iv_add);
            holder.taste_layout = (LinearLayout) convertView.findViewById(R.id.taste_layout);
            holder.btn_taste = (Button) convertView.findViewById(R.id.btn_taste);
            holder.recycler_view_taste = (RecyclerView) convertView.findViewById(R.id.recycler_view_taste);

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
//                holder.ll_right.setVisibility(View.VISIBLE);
                holder.iv_remove.setVisibility(View.VISIBLE);
                holder.iv_add.setVisibility(View.VISIBLE);
                holder.taste_layout.setVisibility(View.GONE);
            } else {
                // 子项
                holder.tv_name.setText("  " + item.getXMMC());
//                holder.ll_right.setVisibility(View.INVISIBLE);
                holder.iv_remove.setVisibility(View.INVISIBLE);
                holder.iv_add.setVisibility(View.INVISIBLE);
                holder.taste_layout.setVisibility(View.VISIBLE);
                holder.btn_taste.setTag(item);
                holder.btn_taste.setOnClickListener(mTasteClickListener);
                setTasteShow(holder.recycler_view_taste, item.getPZ());
            }
        } else {
            // 单品
            holder.tv_name.setText(item.getXMMC());
            holder.ll_right.setVisibility(View.VISIBLE);
            holder.taste_layout.setVisibility(View.VISIBLE);
            holder.btn_taste.setTag(item);
            holder.btn_taste.setOnClickListener(mTasteClickListener);
            setTasteShow(holder.recycler_view_taste, item.getPZ());
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

    private void setTasteShow(RecyclerView recyclerView, String pz) {
        if (TextUtils.isEmpty(pz))
            return;

        // pz = (番茄鸡肉)(加冰)(不加冰)(餐前)<备注>
        String[] array = pz.split("[()<>]");
        List<String> list = new ArrayList<String>();
        for (String s: array) {
            if (s.length() != 0)
                list.add(s);
        }

        TasteShowAdapter tasteAdapter = new TasteShowAdapter(list);
        recyclerView.setAdapter(tasteAdapter);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(mActivity, LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(layoutManager);
    }

    private View.OnClickListener mTasteClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            TasteChoiceDialogFragment fragment = new TasteChoiceDialogFragment();
            Bundle args = new Bundle();
            args.putSerializable("wmlsb", (WMLSB) v.getTag());
            fragment.setArguments(args);
            fragment.show(mActivity.getSupportFragmentManager(), null);
        }
    };

    private static class ViewHolder {
        TextView tv_no;
        TextView tv_name;
        TextView tv_price;

        LinearLayout ll_right;
        ImageView iv_remove;
        TextView tv_num;
        ImageView iv_add;

        LinearLayout taste_layout;
        Button btn_taste;
        RecyclerView recycler_view_taste;
    }
}

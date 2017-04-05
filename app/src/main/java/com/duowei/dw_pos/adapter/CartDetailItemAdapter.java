package com.duowei.dw_pos.adapter;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.duowei.dw_pos.CartDetailActivity;
import com.duowei.dw_pos.R;
import com.duowei.dw_pos.bean.WMLSB;
import com.duowei.dw_pos.fragment.ModifyDialogFragment;
import com.duowei.dw_pos.fragment.TasteChoiceDialogFragment;
import com.duowei.dw_pos.tools.CartList;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017-03-25.
 */

public class CartDetailItemAdapter extends BaseAdapter {

    private CartDetailActivity mActivity;
    private List<WMLSB> mAllList = new ArrayList<>();

    public CartDetailItemAdapter(CartDetailActivity activity) {
        mActivity = activity;
    }

    /**
     * 设置本地未提交的数据
     *
     * @param list
     */
    public void addLocalList(List<WMLSB> list) {
        for (int i = 0; i < list.size(); i++) {
            mAllList.add(list.get(i));
            for (int j = 0; j < list.get(i).getSubWMLSBList().size(); j++) {
                mAllList.add(list.get(i).getSubWMLSBList().get(j));
            }
        }
        notifyDataSetChanged();
    }

    /**
     * 服务器已下单数据
     *
     * @param list
     */
    public void addRemoteList(List<WMLSB> list) {
        mAllList.addAll(list);
        notifyDataSetChanged();
    }

    public void clear() {
        mAllList.clear();
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return mAllList.size();
    }

    @Override
    public WMLSB getItem(int position) {
        return mAllList.get(position);
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

        // 初始化
        holder.tv_name.setTextColor(Color.DKGRAY);
        holder.iv_remove.setEnabled(true);
        holder.iv_remove.setVisibility(View.VISIBLE);
        holder.iv_add.setEnabled(true);
        holder.iv_add.setVisibility(View.VISIBLE);

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

            // 附加信息显示
            String localMsg = item.getBY13();
            if (!TextUtils.isEmpty(localMsg)) {
                Spannable spannable = new SpannableString(localMsg);
                spannable.setSpan(new ForegroundColorSpan(Color.RED), 0, localMsg.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                spannable.setSpan(new RelativeSizeSpan(0.75f), 0, localMsg.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                holder.tv_name.append(spannable);
            }

            holder.ll_right.setVisibility(View.VISIBLE);
            holder.taste_layout.setVisibility(View.VISIBLE);
            holder.btn_taste.setTag(item);
            holder.btn_taste.setOnClickListener(mTasteClickListener);
            setTasteShow(holder.recycler_view_taste, item.getPZ());
        }

        holder.iv_remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CartList.newInstance(v.getContext()).remove(item);
            }
        });

        holder.iv_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CartList.newInstance(v.getContext()).add(item);
            }
        });

        if (item.getRemote() == 1) {
            holder.tv_name.setTextColor(Color.RED);
            holder.taste_layout.setVisibility(View.GONE);
            holder.iv_add.setEnabled(false);
            holder.iv_remove.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ModifyDialogFragment fragment = new ModifyDialogFragment();
                    fragment.show(mActivity.getSupportFragmentManager(), null);
                }
            });
        }

        return convertView;
    }

    /**
     * @return 本地未下单数量
     */
    public float getLocalNum() {
        float num = 0;

        for (int i = 0; i < mAllList.size(); i++) {
            WMLSB w = mAllList.get(i);
            if (w.getRemote() == 0) {
                num += w.getSL();
            }
        }

        return num;
    }

    /**
     * @return 总的金额
     */
    public float getTotalPrice() {
        float total = 0;

        for (int i = 0; i < mAllList.size(); i++) {
            WMLSB w = mAllList.get(i);
            total += w.getSL() * w.getDJ();
        }

        return total;
    }

    private void setTasteShow(RecyclerView recyclerView, String pz) {
        if (TextUtils.isEmpty(pz))
            return;

        // pz = (番茄鸡肉)(加冰)(不加冰)(餐前)<备注>
        String[] array = pz.split("[()<>]");
        List<String> list = new ArrayList<String>();
        for (String s : array) {
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

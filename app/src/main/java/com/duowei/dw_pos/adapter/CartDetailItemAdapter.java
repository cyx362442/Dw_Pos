package com.duowei.dw_pos.adapter;

import android.graphics.Color;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.duowei.dw_pos.CartDetailActivity;
import com.duowei.dw_pos.R;
import com.duowei.dw_pos.bean.WMLSB;
import com.duowei.dw_pos.fragment.InputNumDialogFragment;
import com.duowei.dw_pos.fragment.ModifyDialogFragment;
import com.duowei.dw_pos.fragment.ModifyLoginDialogFragment;
import com.duowei.dw_pos.fragment.TasteChoiceDialogFragment;
import com.duowei.dw_pos.impl.OnSuccessListener;
import com.duowei.dw_pos.tools.CartList;
import com.duowei.dw_pos.tools.Users;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * 订单详情
 */

public class CartDetailItemAdapter extends BaseAdapter {

    private CartDetailActivity mActivity;
    private List<WMLSB> mAllList = new ArrayList<>();

    private int mIndex = 1;

    public CartDetailItemAdapter(CartDetailActivity activity) {
        mActivity = activity;
    }

    public List<WMLSB> getAllList() {
        return mAllList;
    }

    /**
     * 设置本地未提交的数据
     *
     * @param list
     */
    public void addLocalList(List<WMLSB> list) {
        for (int i = 0; i < list.size(); i++) {
//            list.get(i).setRemote(1);
            mAllList.add(list.get(i));
            for (int j = 0; j < list.get(i).getSubWMLSBList().size(); j++) {
                mAllList.add(list.get(i).getSubWMLSBList().get(j));
            }
        }
        clearIndex(mAllList);
        notifyDataSetChanged();
    }

    /**
     * 服务器已下单数据
     *
     * @param list
     */
    public void addRemoteList(List<WMLSB> list) {
        mAllList.addAll(list);
        clearIndex(mAllList);
        notifyDataSetChanged();
    }

    public void clear() {
        mAllList.clear();
        notifyDataSetChanged();
    }

    private void clearIndex(List<WMLSB> list) {
        for (WMLSB w : list)
            w.index = -1;
        mIndex = 1;
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
            holder.btn_taste = (TextView) convertView.findViewById(R.id.btn_taste);
            holder.recycler_view_taste = (RecyclerView) convertView.findViewById(R.id.recycler_view_taste);
            holder.btn_edit = (ImageView) convertView.findViewById(R.id.btn_edit);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        // 初始化
        holder.tv_name.setTextColor(Color.DKGRAY);
        holder.iv_remove.setVisibility(View.VISIBLE);
        holder.iv_remove.setEnabled(true);
        holder.iv_add.setVisibility(View.VISIBLE);
        holder.iv_add.setEnabled(true);
        holder.btn_taste.setVisibility(View.VISIBLE);
        holder.recycler_view_taste.setAdapter(null);
        holder.btn_edit.setVisibility(View.GONE);

        final WMLSB item = getItem(position);

        holder.tv_no.setText(null);

        // 金额
        BigDecimal total = new BigDecimal(item.getDJ())
                .multiply(new BigDecimal(item.getSL()))
                .setScale(2, BigDecimal.ROUND_HALF_UP);
        holder.tv_price.setText(String.valueOf("¥" + total));

        holder.tv_num.setText(String.valueOf(item.getSL()));

        if (!TextUtils.isEmpty(item.getTCBH())) {
            // 套餐主项 子项
            if ("A".equals(item.getBY15())) {
                // 主项
//                holder.tv_no.setText(String.valueOf(mIndex++));
                if (item.index == -1) {
                    item.index = mIndex++;
                }
                holder.tv_no.setText(String.valueOf(item.index));

                holder.tv_name.setText(item.getXMMC());
                holder.iv_remove.setVisibility(View.VISIBLE);
                holder.iv_add.setVisibility(View.VISIBLE);
//                holder.btn_taste.setVisibility(View.INVISIBLE);
                holder.taste_layout.setVisibility(View.VISIBLE);
                holder.btn_taste.setTag(item);
                holder.btn_taste.setOnClickListener(mTasteClickListener);
                setTasteShow(holder.recycler_view_taste, item.getPZ());

                // 数量修改
                holder.btn_edit.setVisibility(View.VISIBLE);
                holder.btn_edit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        InputNumDialogFragment fragment = new InputNumDialogFragment();
                        fragment.show(mActivity.getSupportFragmentManager(), null);
                        fragment.setOnOkBtnClickListener(new InputNumDialogFragment.OnOkBtnClickListener() {
                            @Override
                            public void onOkBtnClick(float inputValue) {
                                if (item.getRemote() == 1) {
                                    item.setSL2(inputValue);

                                } else {
                                    CartList.newInstance(mActivity).modifyNum(item, inputValue);
                                }
                            }
                        });
                    }
                });
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
//            holder.tv_no.setText(String.valueOf(mIndex++));
            if (item.index == -1) {
                item.index = mIndex++;
            }
            holder.tv_no.setText(String.valueOf(item.index));

            holder.tv_name.setText(item.getXMMC());

            // 附加信息显示
            String localMsg = item.getBY13();
            if (!TextUtils.isEmpty(localMsg)) {
                Spannable spannable = new SpannableString(localMsg);
                spannable.setSpan(new ForegroundColorSpan(Color.RED), 0, localMsg.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                spannable.setSpan(new RelativeSizeSpan(0.75f), 0, localMsg.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                holder.tv_name.append(spannable);

                holder.btn_edit.setVisibility(View.GONE);
            } else {
                // 数量修改
                holder.btn_edit.setVisibility(View.VISIBLE);
                holder.btn_edit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        InputNumDialogFragment fragment = new InputNumDialogFragment();
                        fragment.show(mActivity.getSupportFragmentManager(), null);

                        fragment.setOnOkBtnClickListener(new InputNumDialogFragment.OnOkBtnClickListener() {
                            @Override
                            public void onOkBtnClick(float inputValue) {
                                if (item.getRemote() == 1) {
                                    item.setSL2(inputValue);

                                } else {
                                    CartList.newInstance(mActivity).modifyNum(item, inputValue);
                                }
                            }
                        });
                    }
                });
            }

            holder.ll_right.setVisibility(View.VISIBLE);
            holder.taste_layout.setVisibility(View.VISIBLE);
            holder.btn_taste.setTag(item);
            holder.btn_taste.setOnClickListener(mTasteClickListener);
            setTasteShow(holder.recycler_view_taste, item.getPZ());
        }

        // - 按钮
        holder.iv_remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (item.getRemote() == 1) {
                    // 偶数份处理
                    if (!TextUtils.isEmpty(item.getBY16()) && "1".equals(item.getBY16())) {
                        List<WMLSB> remoteList = CartList.sWMLSBList;
                        int num1 = 0;
                        int num2 = 0;
                        for (WMLSB w : remoteList) {
                            if (w.getXMBH().equals(item.getXMBH())) {
                                if ("1".equals(w.getBY16())) {
                                    num1++;

                                } else if ("2".equals(w.getBY16())) {
                                    num2++;
                                }
                            }
                        }
                        if (num1 <= num2) {
                            Toast.makeText(mActivity, "存在偶数份半价的单品，请先退偶数份半价的单品", Toast.LENGTH_SHORT).show();
                            return;
                        }
                    }
                    // ------------------
                    if (item.getDWSL() > 0) {
                        item.setSL(item.getSL() - item.getDWSL());
                    } else {
                        item.setSL(item.getSL() - 1);
                    }
                } else {
                    CartList.newInstance(v.getContext()).remove(item);
                }
            }
        });

        // + 按钮
        holder.iv_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (item.getRemote() == 1) {
                    if (item.getDWSL() > 0) {
                        item.setSL(item.getSL() + item.getDWSL());
                    } else {
                        item.setSL(item.getSL() + 1);
                    }

                } else {
                    CartList.newInstance(v.getContext()).add(item);
                }
            }
        });

        // 远程已下单处理
        if ("1".equals(item.getSFYXD())) {
            holder.tv_name.setTextColor(Color.RED);
            holder.btn_taste.setVisibility(View.GONE);
            holder.iv_add.setEnabled(false);
            holder.iv_add.setImageResource(R.mipmap.ic_add_circle_gray_36dp);
            holder.btn_edit.setVisibility(View.GONE);

            holder.iv_remove.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if ("1".equals(Users.TDQX)) {
                        CartList.newInstance(v.getContext()).removeRemote(item);

                        ModifyDialogFragment fragment = new ModifyDialogFragment();
                        fragment.show(mActivity.getSupportFragmentManager(), null);

                    } else {
                        ModifyLoginDialogFragment fragment = new ModifyLoginDialogFragment();
                        fragment.show(mActivity.getSupportFragmentManager(), null);
                        fragment.setOnSuccessListener(new OnSuccessListener() {

                            @Override
                            public void onSuccess() {
                                CartList.newInstance(mActivity).removeRemote(item);

                                ModifyDialogFragment fragment = new ModifyDialogFragment();
                                fragment.show(mActivity.getSupportFragmentManager(), null);
                            }
                        });
                    }
                }
            });

        } else {
            // 未下单
        }

        if ("加价促销".equals(item.getBY13())) {
            holder.iv_remove.setEnabled(false);
            holder.iv_add.setEnabled(false);

        } else if ("赠送".equals(item.getBY13())) {
            holder.iv_add.setEnabled(false);

        } else if ("偶数份半价".equals(item.getBY13())) {
            holder.iv_add.setEnabled(false);
        }

        if ("1".equals(item.getBY16()) || "2".equals(item.getBY16())) {
            holder.iv_add.setEnabled(false);
            holder.btn_edit.setVisibility(View.GONE);
        }

        return convertView;
    }

    /**
     * @return 本地未下单数量
     */
    public float getTotalNum() {
        BigDecimal num = BigDecimal.valueOf(0);

        for (int i = 0; i < mAllList.size(); i++) {
            WMLSB w = mAllList.get(i);
            num = num.add(new BigDecimal(w.getSL()));
        }

        return num.setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
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

        return BigDecimal.valueOf(total).setScale(1, BigDecimal.ROUND_HALF_UP).floatValue();
    }

    /**
     * @return 原始总的金额
     */
    public float getOriginalMoney() {
        float total = 0;

        for (int i = 0; i < mAllList.size(); i++) {
            WMLSB w = mAllList.get(i);
            total += w.getSL() * w.getYSJG();
        }

        return BigDecimal.valueOf(total).setScale(1, BigDecimal.ROUND_HALF_UP).floatValue();
    }

    /**
     * @return true, 有未下单数据
     */
    public boolean hasUnOrder() {
        boolean result = false;

        for (int i = 0; i < mAllList.size(); i++) {
            WMLSB w = mAllList.get(i);
            if (!"1".equals(w.getSFYXD())) {
                result = true;
                break;
            }
        }

        return result;
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
            TasteChoiceDialogFragment fragment = TasteChoiceDialogFragment.newInstance((WMLSB) v.getTag(),1,false);
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
        TextView btn_taste;
        RecyclerView recycler_view_taste;
        ImageView btn_edit;
    }
}

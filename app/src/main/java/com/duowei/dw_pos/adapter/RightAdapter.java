package com.duowei.dw_pos.adapter;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.duowei.dw_pos.CashierDeskActivity;
import com.duowei.dw_pos.ComboActivity;
import com.duowei.dw_pos.R;
import com.duowei.dw_pos.bean.DMKWDYDP;
import com.duowei.dw_pos.bean.JYXMSZ;
import com.duowei.dw_pos.bean.Jgsz;
import com.duowei.dw_pos.bean.TCMC;
import com.duowei.dw_pos.bean.TCSD;
import com.duowei.dw_pos.bean.WMLSB;
import com.duowei.dw_pos.event.ClearSearchEvent;
import com.duowei.dw_pos.fragment.InputNumDialogFragment;
import com.duowei.dw_pos.fragment.TasteChoiceDialogFragment;
import com.duowei.dw_pos.tools.CartList;

import org.greenrobot.eventbus.EventBus;
import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017-03-23.
 */

public class RightAdapter extends BaseAdapter implements Filterable {
    private static HolderClickListener mHolderClickListener;

    private CashierDeskActivity mContext;
    private List mList = new ArrayList();

    private CartList mCartList;

    private final Object mLock = new Object();
    private List mOriginalValues = new ArrayList();
    private ArrayFilter mFilter;

    private List mAllList = new ArrayList();
    private List mAllOriginalValues = new ArrayList();

    private boolean isAll = false;

    public RightAdapter(CashierDeskActivity context) {
        mContext = context;
        mCartList = CartList.newInstance(mContext);
    }

    @Override
    public int getCount() {
        if (isAll) {
            return mAllList.size();
        }

        return mList.size();
    }

    @Override
    public Object getItem(int position) {
        if (isAll) {
            return mAllList.get(position);
        }

        return mList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.list_item_righ, parent, false);
            holder = new ViewHolder();
            holder.tv_name = (TextView) convertView.findViewById(R.id.tv_name);
            holder.tv_money = (TextView) convertView.findViewById(R.id.tv_money);
            holder.btn_add = (ImageButton) convertView.findViewById(R.id.btn_add);
            holder.ll_view = (LinearLayout) convertView.findViewById(R.id.temp);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Object object = getItem(position);
        if (object instanceof JYXMSZ) {
            // 单品
            final JYXMSZ item = (JYXMSZ) object;
            holder.tv_name.setText(item.getXMMC());
            if (item.getGQ().equals("1")) {
                holder.tv_money.setText("停售");
            } else {
                holder.tv_money.setText(String.valueOf("¥" + item.getXSJG()));
            }

            holder.btn_add.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (item.getGQ().equals("1")) {
                        Toast.makeText(mContext, "该单品己停售", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    EventBus.getDefault().post(new ClearSearchEvent());
                    final WMLSB wmlsb = mCartList.add(item);

                    if (mHolderClickListener != null) {
                        int[] start_location = new int[2];
                        holder.btn_add.getLocationInWindow(start_location);//获取点击商品图片的位置
                        Animation animation = AnimationUtils.loadAnimation(mContext, R.anim.anim_goods);
                        holder.ll_view.startAnimation(animation);
                        Drawable drawable = holder.btn_add.getDrawable();//复制一个新的商品图标
                        mHolderClickListener.onHolderClick(drawable, start_location);
                    }


                    JYXMSZ jyxmsz = DataSupport.where("xmbh = ?", wmlsb.getXMBH()).findFirst(JYXMSZ.class);

                    // 称重处理
                    boolean hasWeight = false;
                    Jgsz jgsz = DataSupport.findFirst(Jgsz.class);
                    if (jgsz != null && "1".equals(jgsz.by52) && "1".equals(jyxmsz.getBY3())) {
                        hasWeight = true;

                        InputNumDialogFragment fragment = new InputNumDialogFragment();
                        fragment.show(mContext.getSupportFragmentManager(), null);
                        fragment.setOnOkBtnClickListener(new InputNumDialogFragment.OnOkBtnClickListener() {
                            @Override
                            public void onOkBtnClick(float inputValue) {
                                CartList.newInstance(mContext).modifyNum(wmlsb, inputValue);
                            }
                        });
                    }

                    // 必选口味处理(有称重是，就不处理必选口味了)
                    if (!hasWeight && "1".equals(jyxmsz.getSFYHQ())) {

                        List<DMKWDYDP> tasteList = DataSupport.where("xmbh = ?", wmlsb.getXMBH()).find(DMKWDYDP.class);

                        if (tasteList != null) {
                            // 有选中必须口味框，都弹出口味选择
                            TasteChoiceDialogFragment fragment = TasteChoiceDialogFragment.newInstance(wmlsb);
                            fragment.show(mContext.getSupportFragmentManager(), null);
                        }
                    }
                }
            });

        } else if (object instanceof TCMC) {
            // 套餐
            final TCMC item = (TCMC) object;
            holder.tv_name.setText(item.getXMMC());

            List<TCSD> tcsdList = DataSupport.where("xmbh = ? and gq = ?", item.getXMBH(), "1").find(TCSD.class);
            if (tcsdList.size() > 0) {
                TCSD tcsd = tcsdList.get(0);
                holder.tv_money.setText("¥" + tcsd.getDJ());
            }
            holder.btn_add.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    EventBus.getDefault().post(new ClearSearchEvent());

                    Intent intent = new Intent(mContext, ComboActivity.class);
                    intent.putExtra("xmbh", item.getXMBH());
                    mContext.startActivity(intent);
                }
            });
        }

        return convertView;
    }

    public void setList(List list) {
        isAll = false;
        mList = list;

        notifyDataSetChanged();

        mOriginalValues.clear();
        mOriginalValues.addAll(mList);
    }

    public void setAllList(List allList) {
        mAllList = allList;

        if (allList != null) {
            mAllOriginalValues.clear();
            mAllOriginalValues.addAll(allList);
        }
    }

    @Override
    public Filter getFilter() {
        if (mFilter == null) {
            mFilter = new ArrayFilter();
        }
        return mFilter;
    }

    private class ViewHolder {
        TextView tv_name;
        TextView tv_money;
        ImageButton btn_add;
        LinearLayout ll_view;
    }

    private class ArrayFilter extends Filter {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults results = new FilterResults();

            if (mAllList == null) {
                return results;
            }

            if (TextUtils.isEmpty(constraint)) {
                isAll = false;

                ArrayList list;
                synchronized (mLock) {
                    list = new ArrayList(mOriginalValues);
                }
                results.values = list;
                results.count = list.size();

            } else {
                isAll = true;

                String prefixString = constraint.toString().toUpperCase();

                ArrayList values;
                synchronized (mLock) {
                    values = new ArrayList(mAllOriginalValues);
                }

                final int count = values.size();
                final ArrayList newValues = new ArrayList();

                for (int i = 0; i < count; i++) {
                    Object object = values.get(i);

                    if (object instanceof JYXMSZ) {
                        // 单品搜索
                        JYXMSZ item = (JYXMSZ) object;
                        if (item.getXMMC().contains(prefixString) || item.getPY().contains(prefixString)) {
                            newValues.add(item);
                        }
                    } else if (object instanceof TCMC) {
                        TCMC item = (TCMC) object;
                        if (item.getXMMC().contains(prefixString) || item.getPY().contains(prefixString)) {
                            newValues.add(item);
                        }
                    }
                }

                results.values = newValues;
                results.count = newValues.size();
            }


            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            if (results.values == null) {
                return;
            }

            if (isAll) {
                mAllList = (List) results.values;
            } else {
                mList = (List) results.values;
            }

            if (results.count > 0) {
                notifyDataSetChanged();

            } else {
                notifyDataSetInvalidated();
            }
        }
    }

    public static void setOnSetHolderClickListener(HolderClickListener holderClickListener) {
        mHolderClickListener = holderClickListener;
    }

    public interface HolderClickListener {
        void onHolderClick(Drawable drawable, int[] start_location);
    }
}

package com.duowei.dw_pos.adapter;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageButton;
import android.widget.TextView;

import com.duowei.dw_pos.ComboActivity;
import com.duowei.dw_pos.R;
import com.duowei.dw_pos.bean.JYXMSZ;
import com.duowei.dw_pos.bean.TCMC;
import com.duowei.dw_pos.bean.TCSD;
import com.duowei.dw_pos.tools.CartList;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017-03-23.
 */

public class RightAdapter extends BaseAdapter implements Filterable {
    private Context mContext;
    private List mList;

    private CartList mCartList;

    private final Object mLock = new Object();
    private List mOriginalValues;
    private ArrayFilter mFilter;

    private List mAllList;
    private List mAllOriginalValues;

    private boolean isAll = false;

    public RightAdapter(Context context) {
        mContext = context;

        mAllList = new ArrayList();
        mList = new ArrayList();

        mCartList = CartList.newInstance();
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
        ViewHolder holder = null;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.list_item_righ, parent, false);
            holder = new ViewHolder();
            holder.tv_name = (TextView) convertView.findViewById(R.id.tv_name);
            holder.tv_money = (TextView) convertView.findViewById(R.id.tv_money);
            holder.btn_add = (ImageButton) convertView.findViewById(R.id.btn_add);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Object object = getItem(position);
        if (object instanceof JYXMSZ) {
            // 单品信息
            final JYXMSZ item = (JYXMSZ) object;
            holder.tv_name.setText(item.getXMMC());
            holder.tv_money.setText(String.valueOf("¥" + item.getXSJG()));

            holder.btn_add.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mCartList.add(item);
                }
            });

        } else if (object instanceof TCMC) {
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
//                    Toast.makeText(mContext, "进入套餐子项", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(mContext, ComboActivity.class);
                    intent.putExtra("xmbh", item.getXMBH());
                    mContext.startActivity(intent);
                }
            });
        }

        return convertView;
    }

    public void setList(List list, List allList) {
        isAll = false;

        mList = list;
        mAllList = allList;

        notifyDataSetChanged();

        if (mOriginalValues == null) {
            mOriginalValues = new ArrayList();
        }
        mOriginalValues.clear();
        mOriginalValues.addAll(mList);

        if (mAllOriginalValues == null) {
            mAllOriginalValues = new ArrayList();
        }
        mAllOriginalValues.clear();
        mAllOriginalValues.addAll(allList);
    }

    @Override
    public Filter getFilter() {
        if (mFilter == null) {
            mFilter = new ArrayFilter();
        }
        return mFilter;
    }

    private static class ViewHolder {
        TextView tv_name;
        TextView tv_money;
        ImageButton btn_add;
    }

    private class ArrayFilter extends Filter {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults results = new FilterResults();

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
                        if (item.getXMMC().startsWith(prefixString) || item.getPY().contains(prefixString)) {
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

}

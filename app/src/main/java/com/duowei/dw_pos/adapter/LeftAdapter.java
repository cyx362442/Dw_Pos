package com.duowei.dw_pos.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.duowei.dw_pos.R;
import com.duowei.dw_pos.bean.DMJYXMSSLB;

import java.util.List;

/**
 * Created by Administrator on 2017-03-23.
 */

public class LeftAdapter extends BaseAdapter {
    private Context mContext;
    private List mList;

    public LeftAdapter(Context context, List list) {
        mContext = context;
        mList = list;
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public Object getItem(int position) {
        return mList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        TextView textView = null;
        if (convertView == null) {
            textView = (TextView) LayoutInflater.from(mContext).inflate(R.layout.list_item_left, parent, false);
        } else {
            textView = (TextView) convertView;
        }

        Object object = getItem(position);
        if (object instanceof DMJYXMSSLB) {
            // 单品
            DMJYXMSSLB item = (DMJYXMSSLB) object;
            textView.setText(item.getLBMC());
        }

        return textView;
    }
}

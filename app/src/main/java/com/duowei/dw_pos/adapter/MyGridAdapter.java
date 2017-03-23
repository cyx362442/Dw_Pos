package com.duowei.dw_pos.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.duowei.dw_pos.R;
import com.duowei.dw_pos.bean.JYCSSZ;

import java.util.List;
import java.util.zip.Inflater;

/**
 * Created by Administrator on 2017-03-23.
 */

public class MyGridAdapter extends BaseAdapter{
    Context context;
    List<JYCSSZ>list;

    public MyGridAdapter(Context context, List<JYCSSZ> list) {
        this.context = context;
        this.list = list;
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
        ViewHolder viewHolder;
        if(convertView==null){
            convertView = LayoutInflater.from(context).inflate(R.layout.table_item, null);
            viewHolder=new ViewHolder();
            viewHolder.ll_table=(LinearLayout) convertView.findViewById(R.id.ll_table);
            viewHolder.ll_tv= (LinearLayout) convertView.findViewById(R.id.linearLayout01);
            viewHolder.tv1 = (TextView) convertView.findViewById(R.id.tv_tableNum);
            viewHolder.tv2 = (TextView) convertView.findViewById(R.id.tv_totalMoney);
            viewHolder.tv3 = (TextView) convertView.findViewById(R.id.tv_persionNumber);
            viewHolder.tv4 = (TextView) convertView.findViewById(R.id.tv_signTime);
            viewHolder.tv5 = (TextView) convertView.findViewById(R.id.tv_passTime);
            convertView.setTag(viewHolder);
        }else{
            viewHolder=(ViewHolder)convertView.getTag();
        }
        viewHolder.tv1.setText(list.get(position).CSMC);
        return convertView;
    }
    class ViewHolder{
        public LinearLayout ll_table;
        public LinearLayout ll_tv;
        public TextView tv1;
        public TextView tv2;
        public TextView tv3;
        public TextView tv4;
        public TextView tv5;
    }
}

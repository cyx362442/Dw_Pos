package com.duowei.dw_pos.adapter;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.duowei.dw_pos.R;
import com.duowei.dw_pos.bean.JYCSSZ;
import com.duowei.dw_pos.bean.TableUse;
import com.duowei.dw_pos.tools.Users;

import java.util.List;
import java.util.zip.Inflater;

/**
 * Created by Administrator on 2017-03-23.
 */

public class MyGridAdapter extends BaseAdapter{
    Context context;
    List<JYCSSZ>list;
    TableUse[]used;
    public MyGridAdapter(Context context, List<JYCSSZ> list,TableUse[]used) {
        this.context = context;
        this.list = list;
        this.used=used;
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
        for(int i=0;i<used.length;i++){//已开台
            if((list.get(position).CSMC+",").equals(used[i].getZH())){
                Log.e("===",list.get(position).CSMC+":"+used[i].getCsmc());
                viewHolder.ll_table.setBackgroundResource(R.drawable.table_used);
                viewHolder.ll_tv.setVisibility(View.VISIBLE);
                viewHolder.tv1.setTextColor(Color.parseColor("#ffffff"));
                viewHolder.tv2.setText(used[i].getYS());
                viewHolder.tv3.setText("  "+used[i].getJCRS());
                //截取点餐时间时、分
                String jysj = used[i].getJYSJ();
                String sDateTime = jysj.substring(9, 14);
                viewHolder.tv4.setText(sDateTime);
                viewHolder.tv5.setText(used[i].getScjc()+"分");
                break;
            }else{
                viewHolder.ll_table.setBackgroundResource(R.drawable.table_normal);
                viewHolder.ll_tv.setVisibility(View.GONE);
            }
        }
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

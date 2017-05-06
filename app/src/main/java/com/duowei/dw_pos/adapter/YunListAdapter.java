package com.duowei.dw_pos.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.duowei.dw_pos.R;
import com.duowei.dw_pos.bean.YunFu;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017-02-10.
 */

public class YunListAdapter extends BaseAdapter{
    Context context;
    List<YunFu> listyf;

    public YunListAdapter(Context context, List<YunFu> listyf) {
        this.context = context;
        this.listyf = listyf;
    }

    @Override
    public int getCount() {
        return listyf.size();
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
    public View getView(int i, View view, ViewGroup viewGroup) {
        View inflate = LayoutInflater.from(context).inflate(R.layout.yunlistview_item, null);
        TextView fangshi = (TextView) inflate.findViewById(R.id.tv_fangshi);
        TextView jr = (TextView) inflate.findViewById(R.id.tv_jr);
        TextView quan = (TextView) inflate.findViewById(R.id.tv_quan);

        fangshi.setText(listyf.get(i).title);
        jr.setText(bigDecimal(listyf.get(i).money)+"");
        quan.setText(listyf.get(i).sl+"");
        return inflate;
    }
    public  Float bigDecimal(Float f){
        return BigDecimal.valueOf(f).setScale(1, BigDecimal.ROUND_HALF_UP).floatValue();
    }
}

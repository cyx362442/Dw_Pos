package com.duowei.dw_pos.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.duowei.dw_pos.R;

/**
 * Created by Administrator on 2017-03-31.
 */

public class YunGvAdapter extends BaseAdapter {
    Context context;

    public YunGvAdapter(Context context) {
        this.context = context;
    }

    @Override
    public int getCount() {
        return 6;
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
    public View getView(int positon, View contentView, ViewGroup viewGroup) {
        ViewHold hold;
        if(contentView==null){
            hold=new ViewHold();
            contentView= LayoutInflater.from(context).inflate(R.layout.gridview_yun,null);
            hold.content= (TextView) contentView.findViewById(R.id.tv_content);
            hold.money= (TextView) contentView.findViewById(R.id.tv_money);
            contentView.setTag(hold);
        }else{
            hold= (ViewHold) contentView.getTag();
        }
        return contentView;
    }
    class ViewHold{
        public TextView content;
        public TextView money;
    }
}

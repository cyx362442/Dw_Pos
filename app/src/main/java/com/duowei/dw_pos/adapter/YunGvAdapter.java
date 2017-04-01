package com.duowei.dw_pos.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.duowei.dw_pos.R;
import com.duowei.dw_pos.bean.ImsCardMember;

import java.util.ArrayList;

/**
 * Created by Administrator on 2017-03-31.
 */

public class YunGvAdapter extends BaseAdapter {
    Context context;
    ArrayList<ImsCardMember>mList;

    public YunGvAdapter(Context context, ArrayList<ImsCardMember> list) {
        this.context = context;
        this.mList = list;
    }

    @Override
    public int getCount() {
        return mList.size();
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
        if(mList.get(positon).getTicket()==0){
            hold.content.setText("储值消费");
            hold.money.setText("￥"+mList.get(positon).getCredit2());
        }
        else if(mList.get(positon).getTicket()==1){
            hold.content.setText("积分消费");
            hold.money.setText(mList.get(positon).getCredit1()+"");
        }
        else if(mList.get(positon).getTicket()==2){
            hold.content.setText(mList.get(positon).getTitle());
            hold.money.setText(mList.get(positon).getSL()+"张");
        }
        return contentView;
    }
    class ViewHold{
        public TextView content;
        public TextView money;
    }
}

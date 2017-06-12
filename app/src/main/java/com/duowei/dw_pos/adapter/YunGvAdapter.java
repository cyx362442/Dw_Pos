package com.duowei.dw_pos.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.duowei.dw_pos.R;
import com.duowei.dw_pos.bean.ImsCardMember;
import com.duowei.dw_pos.bean.Moneys;

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
            hold.ll= (LinearLayout) contentView.findViewById(R.id.linearLayout);
            hold.content= (TextView) contentView.findViewById(R.id.tv_content);
            hold.money= (TextView) contentView.findViewById(R.id.tv_money);
            contentView.setTag(hold);
        }else{
            hold= (ViewHold) contentView.getTag();
        }
        /**储值卡消费*/
        ImsCardMember cards = mList.get(positon);
        if(cards.getTicket()==0&& cards.isSelect()==true){
            hold.ll.setBackgroundResource(R.drawable.item_click);
        }else if(cards.getTicket()==0&& cards.isSelect()==false){
            hold.ll.setBackgroundResource(R.drawable.item_normal);
        }
        /**积分消费*/
        if(cards.getTicket()==1&& cards.isSelect()==true){
            hold.ll.setBackgroundResource(R.drawable.item_click);
        }else if(cards.getTicket()==1&& cards.isSelect()==false){
            hold.ll.setBackgroundResource(R.drawable.item_normal);
        }
        /**电子券1消费*/
        if(cards.getTicket()==2&& cards.isSelect()==true){
            hold.ll.setBackgroundResource(R.drawable.item_click);
        }else if(cards.getTicket()==2&& cards.isSelect()==false){
            hold.ll.setBackgroundResource(R.drawable.item_normal);
        }
        /**电子券2*/
        if(cards.getTicket()==3&& cards.isSelect()==true){
            hold.ll.setBackgroundResource(R.drawable.item_click);
        }else if(cards.getTicket()==3&& cards.isSelect()==false){
            hold.ll.setBackgroundResource(R.drawable.item_normal);
        }
        /**电子券3*/
        if(cards.getTicket()==4&& cards.isSelect()==true){
            hold.ll.setBackgroundResource(R.drawable.item_click);
        }else if(cards.getTicket()==4&& cards.isSelect()==false){
            hold.ll.setBackgroundResource(R.drawable.item_normal);
        }
        /**电子券4*/
        if(cards.getTicket()==5&& cards.isSelect()==true){
            hold.ll.setBackgroundResource(R.drawable.item_click);
        }else if(cards.getTicket()==5&& cards.isSelect()==false){
            hold.ll.setBackgroundResource(R.drawable.item_normal);
        }
        /**电子券5*/
        if(cards.getTicket()==6&& cards.isSelect()==true){
            hold.ll.setBackgroundResource(R.drawable.item_click);
        }else if(cards.getTicket()==6&& cards.isSelect()==false){
            hold.ll.setBackgroundResource(R.drawable.item_normal);
        }

        if(cards.getTicket()==0){
            hold.content.setText("储值消费");
            hold.money.setText("￥"+ cards.getCredit2());
        }
        else if(cards.getTicket()==1){
            hold.content.setText("积分消费");
            hold.money.setText(cards.getCredit1()+"");
        }
        /**各电子券*/
        else if(cards.getTicket()==2){
            hold.content.setText(cards.getTitle());
            if(cards.getLeast_cost()>0&& Moneys.ysjr<cards.getLeast_cost()){//消费满多少才可使用
                hold.money.setText("消费满"+cards.getLeast_cost()+"元");
            }else{
                hold.money.setText(cards.getSL()+"张");
            }
        }else if(cards.getTicket()==3){
            hold.content.setText(cards.getTitle());
            if(cards.getLeast_cost()>0&& Moneys.ysjr<cards.getLeast_cost()){
                hold.money.setText("消费满"+cards.getLeast_cost()+"元");
            }else{
                hold.money.setText(cards.getSL()+"张");
            }
        }else if(cards.getTicket()==4){
            hold.content.setText(cards.getTitle());
            if(cards.getLeast_cost()>0&& Moneys.ysjr<cards.getLeast_cost()){
                hold.money.setText("消费满"+cards.getLeast_cost()+"元");
            }else{
                hold.money.setText(cards.getSL()+"张");
            }
        }else if(cards.getTicket()==5){
            hold.content.setText(cards.getTitle());
            if(cards.getLeast_cost()>0&& Moneys.ysjr<cards.getLeast_cost()){
                hold.money.setText("消费满"+cards.getLeast_cost()+"元");
            }else{
                hold.money.setText(cards.getSL()+"张");
            }
        }else if(cards.getTicket()==6&& Moneys.ysjr<cards.getLeast_cost()){
            hold.content.setText(cards.getTitle());
            if(cards.getLeast_cost()>0){
                hold.money.setText("消费满"+cards.getLeast_cost()+"元");
            }else{
                hold.money.setText(cards.getSL()+"张");
            }
        }
        return contentView;
    }
    class ViewHold{
        public TextView content;
        public TextView money;
        public LinearLayout ll;
    }
}

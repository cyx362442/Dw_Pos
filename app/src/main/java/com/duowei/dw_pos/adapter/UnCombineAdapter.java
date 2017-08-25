package com.duowei.dw_pos.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.duowei.dw_pos.R;
import com.duowei.dw_pos.bean.WMLSBJB;
import com.duowei.dw_pos.event.Combine;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

/**
 * Created by Administrator on 2017-08-24.
 */

public class UnCombineAdapter extends RecyclerView.Adapter<UnCombineAdapter.ViewHold>{
    private List<WMLSBJB> mWMLSBList;
    private final LayoutInflater mLayoutInflater;

    public UnCombineAdapter(List<WMLSBJB> mWMLSBList, Context context) {
        mLayoutInflater = LayoutInflater.from(context);
        this.mWMLSBList = mWMLSBList;
    }

    public void setWMLSBJBs(List<WMLSBJB> mWMLSBList){
        this.mWMLSBList=mWMLSBList;
        notifyDataSetChanged();
    }

    @Override
    public ViewHold onCreateViewHolder(ViewGroup parent, int viewType) {
        View inflate = mLayoutInflater.inflate(R.layout.combine_item, parent,false);
        ViewHold viewHold = new ViewHold(inflate);
        viewHold.tvTime= (TextView) inflate.findViewById(R.id.tv_time);
        viewHold.tvZh= (TextView) inflate.findViewById(R.id.tv_zh);
        viewHold.tvMoney= (TextView) inflate.findViewById(R.id.tv_money);
        viewHold.tvUser= (TextView) inflate.findViewById(R.id.tv_user);
        return viewHold;
    }

    @Override
    public void onBindViewHolder(ViewHold holder, final int position) {
        WMLSBJB wmlsbjb = mWMLSBList.get(position);
        String jysj = wmlsbjb.getJYSJ().substring(9, 14);
        holder.tvTime.setText(jysj);
        holder.tvZh.setText(wmlsbjb.getZH());
        holder.tvMoney.setText("￥"+wmlsbjb.getYS());
        holder.tvUser.setText(wmlsbjb.getYHBH());
        holder.tvUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EventBus.getDefault().post(new Combine(position));
            }
        });
    }

    @Override
    public int getItemCount() {
        return mWMLSBList.size();
    }

    class ViewHold extends RecyclerView.ViewHolder {
        public ViewHold(View itemView) {
            super(itemView);
        }
        TextView tvTime;
        TextView tvZh;
        TextView tvMoney;
        TextView tvUser;
    }
}

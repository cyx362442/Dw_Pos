package com.duowei.dw_pos.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.duowei.dw_pos.R;

import java.util.List;

/**
 * 口味显示Adapter
 */

public class TasteShowAdapter extends RecyclerView.Adapter<TasteShowAdapter.ViewHolder>  {

    private List<String> mStringList;

    public TasteShowAdapter(List<String> stringList) {
        mStringList = stringList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LinearLayout layout = (LinearLayout) LayoutInflater.from(parent.getContext()).inflate(R.layout.taste_tag_item, parent, false);
        return new ViewHolder(layout);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.mTextView.setText(mStringList.get(position));
    }

    @Override
    public int getItemCount() {
        return mStringList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView mTextView;

        ViewHolder(View itemView) {
            super(itemView);
            mTextView = (TextView) itemView.findViewById(R.id.tv_tag);;
        }
    }
}

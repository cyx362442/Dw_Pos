package com.duowei.dw_pos.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.duowei.dw_pos.R;
import com.duowei.dw_pos.bean.JYXMSZ;
import com.duowei.dw_pos.bean.TCMC;
import com.duowei.dw_pos.bean.TCSD;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017-03-23.
 */

public class RightAdapter extends BaseAdapter {
    private Context mContext;
    private List mList;

    public RightAdapter(Context context) {
        mContext = context;
        mList = new ArrayList();
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
        ViewHolder holder = null;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.list_item_righ, parent, false);
            holder = new ViewHolder();
            holder.tv_name = (TextView) convertView.findViewById(R.id.tv_name);
            holder.tv_money = (TextView) convertView.findViewById(R.id.tv_money);
            holder.btn_add = (Button) convertView.findViewById(R.id.btn_add);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Object object = getItem(position);
        if (object instanceof JYXMSZ) {
            // 单品信息
            JYXMSZ item = (JYXMSZ) object;
            holder.tv_name.setText(item.getXMMC());
            holder.tv_money.setText(String.valueOf("¥" + item.getXSJG()));

            holder.btn_add.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(mContext, "添加到购物车", Toast.LENGTH_SHORT).show();
                }
            });

        } else if (object instanceof TCMC) {
            TCMC item = (TCMC) object;
            holder.tv_name.setText(item.getXMMC());

            List<TCSD> tcsdList = DataSupport.where("xmbh = ? and gq = ?", item.getXMBH(), "1").find(TCSD.class);
            if (tcsdList.size() > 0) {
                TCSD tcsd = tcsdList.get(0);
                holder.tv_money.setText("¥" + tcsd.getDJ());
            }
            holder.btn_add.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(mContext, "进入套餐子项", Toast.LENGTH_SHORT).show();
                }
            });
        }

        return convertView;
    }

    public void setList(List list) {
        mList = list;
        notifyDataSetChanged();
    }

    static class ViewHolder {
        TextView tv_name;
        TextView tv_money;
        Button btn_add;
    }
}

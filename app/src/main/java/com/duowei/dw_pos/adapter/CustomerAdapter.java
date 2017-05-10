package com.duowei.dw_pos.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;

import com.duowei.dw_pos.R;

import java.util.List;

/**
 * Created by Administrator on 2017-04-08.
 */

public class CustomerAdapter extends BaseAdapter {
    Context context;
    List<String> gkName;

    public CustomerAdapter(Context context, List<String> gkName) {
        this.context = context;
        this.gkName = gkName;
    }

    @Override
    public int getCount() {
        return gkName.size();
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
        ViewHold hold;
        if(convertView==null){
            convertView = LayoutInflater.from(context).inflate(R.layout.customer_item, null);
            hold=new ViewHold();
            hold.button= (Button) convertView.findViewById(R.id.btn_customer);
            convertView.setTag(hold);
        }else{
            hold= (ViewHold) convertView.getTag();
        }
        hold.button.setText(gkName.get(position));
        return convertView;
    }
    class ViewHold{
        Button button;
    }
}

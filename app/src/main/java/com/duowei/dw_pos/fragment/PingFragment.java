package com.duowei.dw_pos.fragment;

import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.duowei.dw_pos.R;
import com.duowei.dw_pos.bean.TableUse;
import com.duowei.dw_pos.event.SelectPing;

import org.greenrobot.eventbus.EventBus;

/**
 * A simple {@link } subclass.
 */
public class PingFragment extends DialogFragment implements AdapterView.OnItemClickListener {

    private TableUse[] mTableuses;

    public static PingFragment newInstance(TableUse[] tableUses) {
        Bundle args = new Bundle();
        args.putParcelableArray("tableuse",tableUses);
        PingFragment fragment = new PingFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
        View inflate = layoutInflater.inflate(R.layout.fragment_ping, null);
        mTableuses = (TableUse[]) getArguments().getParcelableArray("tableuse");
        ListView lv = (ListView) inflate.findViewById(R.id.listView);
        PingAdapter adapter = new PingAdapter(mTableuses);
        lv.setAdapter(adapter);
        lv.setOnItemClickListener(this);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        return builder.setView(inflate).show();
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        String wmdbh = mTableuses[i].getWMDBH();
        EventBus.getDefault().post(new SelectPing(wmdbh));
        dismiss();
    }

    class PingAdapter extends BaseAdapter{
        private TableUse[] tableUses;

        public PingAdapter(TableUse[] tableUses) {
            this.tableUses = tableUses;
        }

        @Override
        public int getCount() {
            return tableUses.length;
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
            Hold hold=null;
            if(convertView==null){
                LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
                convertView=layoutInflater.inflate(R.layout.ping_item, null);
                hold=new Hold();
                hold.tvTime= (TextView) convertView.findViewById(R.id.tv_time);
                hold.tvTable= (TextView) convertView.findViewById(R.id.tv_table);
                hold.tvMoney= (TextView) convertView.findViewById(R.id.tv_money);
                convertView.setTag(hold);
            }else{
                hold= (Hold) convertView.getTag();
            }
            TableUse tableUse = tableUses[position];
            hold.tvTime.setText(tableUse.getJYSJ().substring(9,14));
            hold.tvTable.setText(tableUse.getZH());
            hold.tvMoney.setText("￥"+tableUse.getYS());
            return convertView;
        }

        class Hold{
            TextView tvTime;
            TextView tvTable;
            TextView tvMoney;
        }
    }
}

package com.duowei.dw_pos.adapter;

import android.content.Context;
import android.support.v7.widget.GridLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckedTextView;
import android.widget.LinearLayout;

import com.duowei.dw_pos.ComboActivity;
import com.duowei.dw_pos.R;
import com.duowei.dw_pos.bean.DMKWDYDP;
import com.duowei.dw_pos.bean.DMPZSD;
import com.duowei.dw_pos.bean.TCSD;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 套餐子项
 */

public class ComboAdapter extends BaseAdapter {
    private ComboActivity mActivity;
    private Map<String, List<TCSD>> mMap;
    private String[] mKeys;

    private Button mOkButton;

    public ComboAdapter(ComboActivity activity, LinkedHashMap<String, List<TCSD>> map, Button okButton) {
        mActivity = activity;
        mMap = map;
        mKeys = mMap.keySet().toArray(new String[mMap.size()]);

        mOkButton = okButton;
        checkOkButtonStatus();
    }

    @Override
    public int getCount() {
        return mMap.size();
    }

    @Override
    public List<TCSD> getItem(int position) {
        return mMap.get(mKeys[position]);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(mActivity).inflate(R.layout.list_item_combo, parent, false);
        }

        final List<TCSD> list = getItem(position);

//        FlexboxLayout flexboxLayout = (FlexboxLayout) convertView.findViewById(R.id.flexbox_layout);
//        flexboxLayout.removeAllViews();
        GridLayout gridLayout = (GridLayout) convertView.findViewById(R.id.grid_layout);
        gridLayout.removeAllViews();

        TCSD checkedTcsd = null;
        for (int i = 0; i < list.size(); i++) {
            TCSD tcsd = list.get(i);
            final CheckedTextView textView = (CheckedTextView) LayoutInflater.from(mActivity).inflate(R.layout.flexbox_item, null);

            if (tcsd.getSFXZ().equals("1")) {
                textView.setChecked(true);
                checkedTcsd = tcsd;
            } else {
                textView.setChecked(false);
            }
            textView.setTag(i);

            textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    int i2 = (int) v.getTag();
                    for (int j = 0; j < list.size(); j++) {
                        list.get(j).setSFXZ("");
                    }
                    list.get(i2).setSFXZ("1");
                    notifyDataSetChanged();
                    checkOkButtonStatus();
                }
            });

//            FlexboxLayout.LayoutParams layoutParams = new FlexboxLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
//            layoutParams.setMargins(20, 0, 20, 0);
//            textView.setText(tcsd.getXMMC1() + " ¥" + tcsd.getDJ());
//            flexboxLayout.addView(textView);

            GridLayout.LayoutParams layoutParams = new GridLayout.LayoutParams(GridLayout.spec(GridLayout.UNDEFINED, 1f), GridLayout.spec(GridLayout.UNDEFINED, 1f));
            layoutParams.width = 0;
            textView.setLayoutParams(layoutParams);

            String text = tcsd.getXMMC1();
            if (tcsd.getDJ() != 0) {
                text += " ¥" + tcsd.getDJ();
            }
            textView.setText(text);
            gridLayout.addView(textView);

            if (list.size() == 1) {
                GridLayout.LayoutParams layoutParams1 = new GridLayout.LayoutParams(GridLayout.spec(GridLayout.UNDEFINED, 1f), GridLayout.spec(GridLayout.UNDEFINED, 1f));
                layoutParams1.width = 0;
                View view = new View(mActivity);
                view.setLayoutParams(layoutParams1);
                gridLayout.addView(view);
            }
        }

        LinearLayout mainTasteLayout = (LinearLayout) convertView.findViewById(R.id.ll_taste_main_layout);
        RecyclerView recyclerView = (RecyclerView) convertView.findViewById(R.id.rv_taste);
        recyclerView.setLayoutManager(new LinearLayoutManager(mActivity, LinearLayoutManager.HORIZONTAL, false));
        showTaste(checkedTcsd, mainTasteLayout, recyclerView);

        return convertView;
    }

    private void checkOkButtonStatus() {
        float totalSubMoney = 0;

        ArrayList<TCSD> enableList = new ArrayList<>();

        for (int i = 0; i < mMap.size(); i++) {
            List<TCSD> tcsdList = mMap.get(mKeys[i]);
            for (int j = 0; j < tcsdList.size(); j++) {
                TCSD tcsd = tcsdList.get(j);
                if (tcsd.getSFXZ() != null && tcsd.getSFXZ().equals("1")) {
                    enableList.add(tcsd);
                    totalSubMoney += tcsd.SL * tcsd.DJ;
                }
            }
        }

        if (enableList.size() == mMap.size()) {
            mOkButton.setEnabled(true);
        } else {
            mOkButton.setEnabled(false);
        }

        mActivity.setTotalPrice(totalSubMoney);
    }

    private void showTaste(TCSD tcsd, LinearLayout mainLayout,  RecyclerView tastelayout) {
        if (tcsd == null) {
            // 没有选中项
            mainLayout.setVisibility(View.GONE);
            return;
        }

        List<DMKWDYDP> dmkwdydpList = DataSupport.where("xmbh = ?", tcsd.getXMBH1()).find(DMKWDYDP.class);
        if (dmkwdydpList.size() > 0) {
            mainLayout.setVisibility(View.VISIBLE);
            List<DMPZSD> dmpzsdList = new ArrayList<>();
            for (int i = 0; i < dmkwdydpList.size(); i++) {
                dmpzsdList.add(DataSupport.where("pzbm = ?", dmkwdydpList.get(i).getPZBM()).findFirst(DMPZSD.class));
            }

            tastelayout.setAdapter(new TasteAdapter(mActivity, tcsd, dmpzsdList));
        } else {
            mainLayout.setVisibility(View.GONE);
            tastelayout.setAdapter(null);
        }
    }

    private static class TasteAdapter extends RecyclerView.Adapter<TasteAdapter.ViewHolder> {
        Context mContext;
        TCSD mTCSD;
        List<DMPZSD> mList;

        TasteAdapter(Context context, TCSD tcsd, List<DMPZSD> list) {
            mContext = context;
            mTCSD = tcsd;
            mList = list;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            CheckedTextView checkedTextView = (CheckedTextView) LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_item_taste, parent, false);
            return new ViewHolder(checkedTextView);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, int position) {
            final String nr = mList.get(position).getNR();

            holder.mTextView.setText(nr);
            if (mTCSD.PZ.contains("(" + nr + ")")) {
                holder.mTextView.setChecked(true);
            } else {
                holder.mTextView.setChecked(false);
            }

            holder.mTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    CheckedTextView ctv = (CheckedTextView) v;

                    if (ctv.isChecked()) {
                        ctv.setChecked(false);
                        mTCSD.PZ = mTCSD.PZ.replace("(" + nr + ")", "");

                    } else {
                        ctv.setChecked(true);
                        mTCSD.PZ = mTCSD.PZ + "(" + nr + ")";
                    }
                }
            });
        }

        @Override
        public int getItemCount() {
            return mList.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            CheckedTextView mTextView;

            ViewHolder(View itemView) {
                super(itemView);
                mTextView = (CheckedTextView) itemView;
            }
        }
    }
}

package com.duowei.dw_pos.fragment;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatDialogFragment;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.duowei.dw_pos.R;
import com.duowei.dw_pos.bean.JYXMSZ;
import com.duowei.dw_pos.bean.MZSZMXXX;
import com.duowei.dw_pos.bean.WMLSB;
import com.duowei.dw_pos.event.CartUpdateEvent;

import org.greenrobot.eventbus.EventBus;
import org.litepal.crud.DataSupport;

import java.util.List;

/**
 * 加价促销 窗口
 */

public class AddPriceDialogFragment extends AppCompatDialogFragment {

    public static WMLSB sWMLSB;
    public static List<MZSZMXXX> sMZSZMXXXList;

    private Context mContext;
    private ArrayAdapter<MZSZMXXX> mArrayAdapter;

    private ListView mListView;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getContext();
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        mListView = new ListView(mContext);
        mListView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        mListView.setItemsCanFocus(false);

        mArrayAdapter = new ArrayAdapter<MZSZMXXX>(mContext, R.layout.list_item_add_price, R.id.left, sMZSZMXXXList) {
            @NonNull
            @Override
            public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                TextView leftView = (TextView) view.findViewById(R.id.left);
                TextView rightView = (TextView) view.findViewById(R.id.right);

                MZSZMXXX item = getItem(position);
                leftView.setText("" + item.getXMMC());
                rightView.setText("¥" + item.getXSJG());

                view.setTag(item);
                return view;
            }
        };

        mListView.setAdapter(mArrayAdapter);

        return new AlertDialog.Builder(mContext)
                .setTitle("请选择单品(加价促销)")
                .setView(mListView)
                .setNegativeButton("取消", null)
                .setPositiveButton("确定加价", mOkListener)
                .create();
    }


    /**
     * 确定加价 按钮
     */
    private DialogInterface.OnClickListener mOkListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            int position = mListView.getCheckedItemPosition();
            if (position == ListView.INVALID_POSITION) {
                return;
            }

            MZSZMXXX item = mArrayAdapter.getItem(position);

            JYXMSZ subJyxmsz = DataSupport.where("xmbh = ?", item.getXMBH()).findFirst(JYXMSZ.class);
            if (subJyxmsz != null) {
                WMLSB wmlsb = new WMLSB(subJyxmsz);
                wmlsb.setSL(Float.valueOf(item.getSL()));
                wmlsb.setDJ(Float.valueOf(item.getXSJG()));
                wmlsb.setBY13("加价促销");
                sWMLSB.getSubWMLSBList().add(wmlsb);
                EventBus.getDefault().post(new CartUpdateEvent());

//                CartList.newInstance(mContext).getList().add(wmlsb);
//                new SqlNetHandler().handleCommit(new Handler(Looper.getMainLooper()),
//                        MyApplication.getContext(),
//                        CartList.newInstance(mContext).getOrderNo());
            }
        }
    };
}

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
 * 赠送、加价促销 窗口
 */

public class AddDialogFragment extends AppCompatDialogFragment {

    public static WMLSB sWMLSB;
    public static List<MZSZMXXX> sMZSZMXXXList;

    private Context mContext;
    private ArrayAdapter<MZSZMXXX> mArrayAdapter;

    private ListView mListView;

    private int mType;

    /**
     * @param type 1, 赠送; 2, 加价促销
     */
    public static AddDialogFragment newInstance(int type) {

        Bundle args = new Bundle();
        args.putInt("type", type);

        AddDialogFragment fragment = new AddDialogFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getContext();
        mType = getArguments().getInt("type", 1);
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

                if (mType == 1) {
                    rightView.setText(null);
                } else if (mType == 2) {
                    rightView.setText("¥" + item.getXSJG());
                }

                view.setTag(item);
                return view;
            }
        };

        mListView.setAdapter(mArrayAdapter);

        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setView(mListView);
        builder.setNegativeButton("取消", null);
        if (mType == 1) {
            builder.setTitle("请选择单品(赠送)");
            builder.setPositiveButton("确定", mOkListener);

        } else if (mType == 2) {
            builder.setTitle("请选择单品(加价促销)");
            builder.setPositiveButton("确定加价", mOkListener);
        }

        return builder.create();
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
                if (mType == 1) {
                    // 添加买赠
                    WMLSB subWmlsb = new WMLSB(subJyxmsz);
                    subWmlsb.setSL(Float.valueOf(item.getSL()));
                    subWmlsb.setDJ(0);
                    subWmlsb.setBY13("赠送");
                    subWmlsb.setSFZS("1");
                    subWmlsb.setBY17("7");
                    subWmlsb.setBy5(sWMLSB.getBy5());
                    sWMLSB.getSubWMLSBList().add(subWmlsb);
                    EventBus.getDefault().post(new CartUpdateEvent());

                } else if (mType == 2) {
                    WMLSB wmlsb = new WMLSB(subJyxmsz);
                    wmlsb.setSL(Float.valueOf(item.getSL()));
                    wmlsb.setDJ(Float.valueOf(item.getXSJG()));
                    wmlsb.setBY13("加价促销");
                    sWMLSB.getSubWMLSBList().add(wmlsb);
                    EventBus.getDefault().post(new CartUpdateEvent());
                }
            }
        }
    };
}

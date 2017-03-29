package com.duowei.dw_pos.fragment;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatDialogFragment;
import android.text.TextUtils;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.duowei.dw_pos.R;
import com.duowei.dw_pos.bean.DMKWDYDP;
import com.duowei.dw_pos.bean.DMPZSD;
import com.duowei.dw_pos.bean.WMLSB;
import com.duowei.dw_pos.event.CartUpdateEvent;

import org.greenrobot.eventbus.EventBus;
import org.litepal.crud.DataSupport;

import java.util.List;

/**
 * 口味选择
 */

public class TasteChoiceDialogFragment extends AppCompatDialogFragment {
    private static final String TAG = "TasteChoiceDialogFragme";

    private Context mContext;

    private ListView mListView;
    private TasteAdapter mTasteAdapter;

    private EditText mEditText;

    private WMLSB mWMLSB;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getContext();

        mWMLSB = (WMLSB) getArguments().getSerializable("wmlsb");
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.fragment_taste_choice_dialog, null);
        mListView = (ListView) view.findViewById(R.id.list);
        mEditText = (EditText) view.findViewById(R.id.edit);

        mTasteAdapter = new TasteAdapter(mContext);
        mListView.setAdapter(mTasteAdapter);
        setData();

        AlertDialog.Builder builder = new AlertDialog.Builder(mContext)
                .setTitle("选择口味")
                .setView(view)
                .setNegativeButton("取消", null)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        saveTasteToCart();
                    }
                });

        return builder.create();
    }

    @Override
    public void onStart() {
        super.onStart();
        loadTasteToList();
    }

    private void setData() {
        if (mWMLSB != null) {
            DMKWDYDP dmkwdydp = DataSupport.where("xmbh = ?", mWMLSB.getXMBH()).findFirst(DMKWDYDP.class);

            if (dmkwdydp != null) {
                // 套餐 子项
                // 单品
                List<DMPZSD> dmpzsdList = DataSupport.where("pzbm = ?", dmkwdydp.getPZBM()).find(DMPZSD.class);
                mTasteAdapter.addAll(dmpzsdList);
            } else {
                List<DMPZSD> dmpzsdList = DataSupport.findAll(DMPZSD.class);
                mTasteAdapter.addAll(dmpzsdList);
            }

        } else {
            Log.d(TAG, "setData: mWmlsb = null");
        }
    }

    /**
     * 从购物车数据库载入已经保存的口味
     */
    private void loadTasteToList() {
        String pzString = mWMLSB.getPZ();

        if (pzString == null) {
            // 本地没有保存的口味
            return;
        }

        // 备注提取
        if (pzString.contains("<")) {
            String comment = "";
            comment = pzString.substring(pzString.indexOf("<"), pzString.indexOf(">") + 1);
            pzString = pzString.replace(comment, "");
            comment = comment.replace("<", "").replace(">", "");
            mEditText.setText(comment);
        }

        // 设置口味选中项
        for (int i = 0; i < mTasteAdapter.getCount(); i++) {
            if (pzString.contains(mTasteAdapter.getItem(i).getNR())) {
                mListView.setItemChecked(i, true);
            }
        }
    }

    private void saveTasteToCart() {
        SparseBooleanArray sba = mListView.getCheckedItemPositions();
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < sba.size(); i++) {
            if (sba.valueAt(i)) {
                sb.append("(");
                sb.append(mTasteAdapter.getItem(sba.keyAt(i)).getNR());
                sb.append(")");
            }
        }

        String comment = mEditText.getText().toString();
        if (!TextUtils.isEmpty(comment)) {
            sb.append("<");
            sb.append(comment);
            sb.append(">");
        }

        mWMLSB.setPZ(sb.toString());
        EventBus.getDefault().post(new CartUpdateEvent());
    }

    private static class TasteAdapter extends ArrayAdapter<DMPZSD> {

        TasteAdapter(@NonNull Context context) {
            super(context, android.R.layout.simple_list_item_multiple_choice);
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            View view = super.getView(position, convertView, parent);
            ((TextView) view).setText(getItem(position).getNR());
            return view;
        }
    }
}

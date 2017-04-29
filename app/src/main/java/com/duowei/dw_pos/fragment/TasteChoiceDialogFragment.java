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
import com.duowei.dw_pos.tools.CartList;

import org.greenrobot.eventbus.EventBus;
import org.litepal.crud.DataSupport;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 口味选择、整单备注
 */

public class TasteChoiceDialogFragment extends AppCompatDialogFragment {
    private static final String TAG = "TasteChoiceDialog";

    private Context mContext;

    private ListView mListView;
    private TasteAdapter mTasteAdapter;

    private EditText mEditText;

    private WMLSB mWMLSB;

    /**
     * 1:整单备注   2:单个口味选择
     */
    private int mMode = 0;

    /**
     * 整单备注
     */
    public static TasteChoiceDialogFragment newInstance() {

        Bundle args = new Bundle();
        args.putInt("mode", 1);

        TasteChoiceDialogFragment fragment = new TasteChoiceDialogFragment();
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * 单品口味选择
     *
     * @param wmlsb 当前购物车项
     */
    public static TasteChoiceDialogFragment newInstance(WMLSB wmlsb) {

        Bundle args = new Bundle();
        args.putInt("mode", 2);
        args.putSerializable("wmlsb", wmlsb);

        TasteChoiceDialogFragment fragment = new TasteChoiceDialogFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getContext();
        mMode = getArguments().getInt("mode");
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
                .setView(view)
                .setNegativeButton("取消", null)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        saveTasteToCart();
                    }
                });

        if (mMode == 1) {
            builder.setTitle("整单备注");
        } else if (mMode == 2) {
            builder.setTitle("选择口味");
        }

        return builder.create();
    }

    @Override
    public void onStart() {
        super.onStart();
        loadTasteToList();
    }

    private void setData() {
        if (mMode == 1) {
            List<DMPZSD> dmpzsdList = DataSupport.where("zdbz = ?", "1").find(DMPZSD.class);
            mTasteAdapter.addAll(dmpzsdList);

        } else if (mMode == 2) {
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

            }
        }
    }

    /**
     * 载入已经保存的口味
     */
    private void loadTasteToList() {
        if (mMode == 1) {
            // 不用处理

        } else if (mMode == 2) {
            String pzString = mWMLSB.getPZ();

            if (pzString == null) {
                // 本地没有保存的口味
                return;
            }

            // 备注提取
            if (pzString.contains("<")) {
//                String comment = "";
//                comment = pzString.substring(pzString.indexOf("<"), pzString.indexOf(">") + 1);
//                pzString = pzString.replace(comment, "");
//                comment = comment.replace("<", "").replace(">", "");
//                mEditText.setText(comment);
                String comment = "";

                Pattern pattern = Pattern.compile("<(.*?)>");
                Matcher matcher = pattern.matcher(pzString);
                while (matcher.find()) {
                    String item = matcher.group();
                    pzString = pzString.replace(item, "");
                    comment += item.replace("<", "").replace(">", "") + ",";
                }

                if (comment.lastIndexOf(",") == comment.length()) {
                    comment = comment.substring(0, comment.length() - 1);
                }

                mEditText.setText(comment);
            }

            // 设置口味选中项
            Pattern pattern = Pattern.compile("\\((.*?)\\)");
            for (int i = 0; i < mTasteAdapter.getCount(); i++) {
                String nr = mTasteAdapter.getItem(i).getNR();

                Matcher matcher = pattern.matcher(pzString);
                while (matcher.find()) {
                    if (matcher.group().replace("(", "").replace(")", "").equals(nr)) {
                        mListView.setItemChecked(i, true);
                    }
                }

//                if (pzString.contains(mTasteAdapter.getItem(i).getNR())) {
//                    mListView.setItemChecked(i, true);
//                }
            }
        }

    }

    private void saveTasteToCart() {
        SparseBooleanArray sba = mListView.getCheckedItemPositions();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < sba.size(); i++) {
            if (sba.valueAt(i)) {
                if (mMode == 1) {
                    sb.append("<");
                } else if (mMode == 2) {
                    sb.append("(");
                }

                sb.append(mTasteAdapter.getItem(sba.keyAt(i)).getNR());

                if (mMode == 1) {
                    sb.append(">");
                } else if (mMode == 2) {
                    sb.append(")");
                }

            }
        }

        String comment = mEditText.getText().toString();
        if (!TextUtils.isEmpty(comment)) {
            comment = comment.replaceAll("，", ",");
            if (comment.contains(",")) {
                String[] commentArray = comment.split(",");
                for (int i = 0; i < commentArray.length; i++) {
                    sb.append("<");
                    sb.append(commentArray[i]);
                    sb.append(">");
                }

            } else {
                sb.append("<");
                sb.append(comment);
                sb.append(">");
            }
        }

        if (mMode == 1) {
            List<WMLSB> wmlsbList = CartList.newInstance(mContext).getList();
            for (int i = 0; i < wmlsbList.size(); i++) {
                WMLSB wmlsb = wmlsbList.get(i);
                wmlsb.setPZ("" + wmlsb.getPZ() + sb.toString());

                for (int j = 0; j < wmlsb.getSubWMLSBList().size(); j++) {
                    WMLSB subWmlsb1 = wmlsb.getSubWMLSBList().get(j);
                    subWmlsb1.setPZ("" + subWmlsb1.getPZ() + sb.toString());
                }
            }
            EventBus.getDefault().post(new CartUpdateEvent());

        } else if (mMode == 2) {
            mWMLSB.setPZ(sb.toString());
            EventBus.getDefault().post(new CartUpdateEvent());
        }
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

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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckedTextView;
import android.widget.EditText;
import android.widget.ListView;

import com.duowei.dw_pos.R;
import com.duowei.dw_pos.bean.DMKWDYDP;
import com.duowei.dw_pos.bean.DMPZSD;
import com.duowei.dw_pos.bean.JYXMSZ;
import com.duowei.dw_pos.bean.WMLSB;
import com.duowei.dw_pos.event.CartAutoSubmit;
import com.duowei.dw_pos.event.CartUpdateEvent;
import com.duowei.dw_pos.tools.CartList;

import org.greenrobot.eventbus.EventBus;
import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 口味选择、整单备注
 */

public class TasteChoiceDialogFragment extends AppCompatDialogFragment implements AdapterView.OnItemClickListener {
    private static final String TAG = "TasteChoiceDialog";

    private Context mContext;

    private ListView mListView;
    private TasteAdapter mTasteAdapter;

    private EditText mEditText;

    private WMLSB mWMLSB;

    private float mSl=1;

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
    public static TasteChoiceDialogFragment newInstance(WMLSB wmlsb,float sl) {

        Bundle args = new Bundle();
        args.putInt("mode", 2);
        args.putFloat("sl",sl);
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
        mSl=getArguments().getFloat("sl");
        mWMLSB = (WMLSB) getArguments().getSerializable("wmlsb");
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.fragment_taste_choice_dialog, null);
        mListView = (ListView) view.findViewById(R.id.list);
        mListView.setOnItemClickListener(this);
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
            builder.setTitle("整单备注(只对未下单有效)");

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
                List<DMKWDYDP> dmkwdydpList = DataSupport.where("xmbh = ?", mWMLSB.getXMBH()).find(DMKWDYDP.class);

                List<DMPZSD> dmpzsdList = new ArrayList<>();
                for (int i = 0; i < dmkwdydpList.size(); i++) {
                    DMKWDYDP dmkwdydp = dmkwdydpList.get(i);
                    DMPZSD item = DataSupport.where("pzbm = ?", dmkwdydp.getPZBM()).findFirst(DMPZSD.class);
                    if (item != null) {
                        dmpzsdList.add(item);
                    }
                }

                if (dmpzsdList.size() > 0) {
                    mTasteAdapter.addAll(dmpzsdList);
                } else {
                    dmpzsdList.addAll(DataSupport.findAll(DMPZSD.class));
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
                DMPZSD item = mTasteAdapter.getItem(i);
                if (item == null) {
                    break;
                }
                String nr = item.getNR();

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
            List<WMLSB> wmlsbList = CartList.sWMLSBList;
            for (int i = 0; i < wmlsbList.size(); i++) {
                WMLSB wmlsb = wmlsbList.get(i);
                if (!"1".equals(wmlsb.getSFYXD())) {
                    wmlsb.setPZ("" + wmlsb.getPZ() + sb.toString());
                }
            }

        } else if (mMode == 2) {
            mWMLSB.setPZ(sb.toString());

            // 添加特殊口味
            if (mIntegerDMPZSDMap.size() > 0) {
                Collection<DMPZSD> dmpzsds = mIntegerDMPZSDMap.values();
                Iterator<DMPZSD> dmpzsdIterator = dmpzsds.iterator();
                while (dmpzsdIterator.hasNext()) {
                    String dycp = dmpzsdIterator.next().DYCP;
                    String xmbh = dycp.substring(dycp.indexOf('@') + 1, dycp.indexOf('#'));
                    JYXMSZ jyxmsz = DataSupport.where("xmbh = ?", xmbh).findFirst(JYXMSZ.class);
                    if (jyxmsz != null) {
                        WMLSB wmlsb = CartList.newInstance(mContext).add(jyxmsz);
                        wmlsb.setSL(mSl);
                    }
                }

                EventBus.getDefault().post(new CartAutoSubmit());
            }
        }

        EventBus.getDefault().post(new CartUpdateEvent());
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        CheckedTextView ctv = (CheckedTextView) view;
        DMPZSD dmpzsd = mTasteAdapter.getItem(position);
        if (!TextUtils.isEmpty(dmpzsd.DYCP)) {
            // 有口味特殊要求
            if (ctv.isChecked()) {
                mIntegerDMPZSDMap.put(position, dmpzsd);
            } else {
                mIntegerDMPZSDMap.remove(position);
            }
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
            CheckedTextView ctv = (CheckedTextView) view.findViewById(android.R.id.text1);
            ctv.setText("" + getItem(position).getNR());

            return view;
        }
    }

    private Map<Integer, DMPZSD> mIntegerDMPZSDMap = new HashMap<>();
}

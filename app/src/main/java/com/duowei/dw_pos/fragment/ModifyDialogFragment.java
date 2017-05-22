package com.duowei.dw_pos.fragment;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.duowei.dw_pos.R;
import com.duowei.dw_pos.bean.SZLB;
import com.duowei.dw_pos.event.CartRemoteUpdateEvent;
import com.duowei.dw_pos.httputils.VolleyUtils;
import com.duowei.dw_pos.tools.Base64;
import com.duowei.dw_pos.tools.CartList;
import com.duowei.dw_pos.tools.Net;

import org.greenrobot.eventbus.EventBus;
import org.litepal.crud.DataSupport;

import java.util.List;

/**
 * 已下单修改数量窗口
 */

public class ModifyDialogFragment extends AppCompatDialogFragment implements View.OnClickListener {

    private Context mContext;

    private CustomAdapter mCustomAdapter;
    private Spinner mSpinner;
    private EditText mEditText;
    private Button mOkButton;
    private Button mCancelButton;

    public static ModifyDialogFragment newInstance() {

        Bundle args = new Bundle();

        ModifyDialogFragment fragment = new ModifyDialogFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getContext();
        setCancelable(false);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return new AlertDialog.Builder(mContext)
                .setView(createView())
                .create();
    }

    private View createView() {
        View view = LayoutInflater.from(mContext).inflate(R.layout.fragment_modify_dialog, null);
        mSpinner = (Spinner) view.findViewById(R.id.spinner);
        mEditText = (EditText) view.findViewById(R.id.editText);
        mCancelButton = (Button) view.findViewById(R.id.btn_cancel);
        mOkButton = (Button) view.findViewById(R.id.btn_ok);

        mCustomAdapter = new CustomAdapter(mContext, DataSupport.findAll(SZLB.class));
        mCustomAdapter.add(new SZLB("手写原因"));
        mSpinner.setAdapter(mCustomAdapter);
        mSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String szbm = mCustomAdapter.getItem(position).getSZBM();
                if ("手写原因".equals(szbm)) {
                    mEditText.setVisibility(View.VISIBLE);
                } else {
                    mEditText.setVisibility(View.GONE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        mCancelButton.setOnClickListener(this);
        mOkButton.setOnClickListener(this);

        return view;
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();

        if (id == R.id.btn_cancel) {
            dismiss();
        } else if (id == R.id.btn_ok) {
            String selectedText = ((SZLB) mSpinner.getSelectedItem()).getSZBM();
            if ("手写原因".equals(selectedText)) {
                String text = mEditText.getText().toString();
                update(text);
            } else {
                update(selectedText);
            }
        }
    }

    private void update(String text) {
        VolleyUtils.getInstance(mContext).postQuerySql7(Net.url,
                Base64.getBase64(CartList.newInstance(mContext).getRemoveRemoteSql(text)).replaceAll("\n", ""),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if (response.contains("richado")) {
                            Toast.makeText(mContext, "修改成功", Toast.LENGTH_SHORT).show();

                            EventBus.getDefault().post(new CartRemoteUpdateEvent(""));
                        } else {
                            Toast.makeText(mContext, "" + response, Toast.LENGTH_SHORT).show();
                        }

                        dismiss();
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(mContext, "修改失败", Toast.LENGTH_SHORT).show();
                        dismiss();
                    }
                });
    }




    private class CustomAdapter extends ArrayAdapter<SZLB> {

        public CustomAdapter(@NonNull Context context, @NonNull List<SZLB> objects) {
            super(context, android.R.layout.simple_spinner_dropdown_item, objects);
        }

        @Override
        public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            View view = super.getDropDownView(position, convertView, parent);
            TextView textView = (TextView) view;
            textView.setText(getItem(position).getSZBM());
            return view;
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            View view = super.getView(position, convertView, parent);
            TextView textView = (TextView) view;
            textView.setText(getItem(position).getSZBM());
            return view;
        }
    }
}

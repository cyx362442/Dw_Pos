package com.duowei.dw_pos.fragment;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatDialogFragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;

import com.duowei.dw_pos.R;

/**
 * 购物车修改数量
 *
 * Created by Administrator on 2017-05-02.
 */

public class InputNumDialogFragment extends AppCompatDialogFragment implements View.OnClickListener {

    private Context mContext;
    private EditText mEditText;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getActivity();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return new AlertDialog.Builder(mContext)
                .setView(createView())
                .create();
    }

    private View createView() {
        View view = LayoutInflater.from(mContext).inflate(R.layout.fragment_input_num_dialog, null);
        mEditText = (EditText) view.findViewById(R.id.editText);
        view.findViewById(R.id.btn_cancel).setOnClickListener(this);
        view.findViewById(R.id.btn_ok).setOnClickListener(this);
        return view;
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();

        if (id == R.id.btn_cancel) {
            dismiss();

        } else if (id == R.id.btn_ok) {
            String inputText = mEditText.getText().toString();
            if (!TextUtils.isEmpty(inputText) && mOnOkBtnClickListener != null) {
                mOnOkBtnClickListener.onOkBtnClick(Float.valueOf(inputText));
            }

            dismiss();
        }
    }

    private OnOkBtnClickListener mOnOkBtnClickListener;

    public void setOnOkBtnClickListener(OnOkBtnClickListener onOkBtnClickListener) {
        mOnOkBtnClickListener = onOkBtnClickListener;
    }

    public interface OnOkBtnClickListener {
        void onOkBtnClick(float inputValue);
    }
}

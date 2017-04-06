package com.duowei.dw_pos.fragment;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatDialogFragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.duowei.dw_pos.R;
import com.duowei.dw_pos.bean.YHJBQK;
import com.duowei.dw_pos.impl.OnSuccessListener;

import org.litepal.crud.DataSupport;

import java.util.List;

/**
 * 退单权限用户登录窗口
 */

public class ModifyLoginDialogFragment extends AppCompatDialogFragment implements View.OnClickListener {

    private Context mContext;
    private EditText mUsername;
    private EditText mPassword;
    private Button mOkButton;

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
        View view = LayoutInflater.from(mContext).inflate(R.layout.fragment_modify_login_dialog, null);
        mUsername = (EditText) view.findViewById(R.id.et_username);
        mPassword = (EditText) view.findViewById(R.id.et_password);
        view.findViewById(R.id.btn_cancel).setOnClickListener(this);
        mOkButton = (Button) view.findViewById(R.id.btn_ok);
        mOkButton.setOnClickListener(this);

        mOkButton.setEnabled(false);
        mUsername.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 0) {
                    mOkButton.setEnabled(true);
                } else {
                    mOkButton.setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        return view;
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();

        if (id == R.id.btn_cancel) {
            dismiss();

        } else if (id == R.id.btn_ok) {
            String username = mUsername.getText().toString();
            String password = mPassword.getText().toString();



            List<YHJBQK> list = DataSupport.where("yhbh = ? and TDQX = 1", username).find(YHJBQK.class);
            if (list.size() > 0 && list.get(0).getYHMM().equals(password)) {
                Toast.makeText(mContext, "Ok", Toast.LENGTH_SHORT).show();

                if (mOnSuccessListener != null) {
                    mOnSuccessListener.onSuccess();
                }

                dismiss();
            } else {
                Toast.makeText(mContext, "您输入的用户编号和用户密码错误\n或仍然不具备退单权限", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private OnSuccessListener mOnSuccessListener;

    public void setOnSuccessListener(OnSuccessListener listener) {
        mOnSuccessListener = listener;
    }
}

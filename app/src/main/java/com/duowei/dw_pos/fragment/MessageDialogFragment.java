package com.duowei.dw_pos.fragment;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatDialogFragment;

/**
 * 消息提示 窗口
 */

public class MessageDialogFragment extends AppCompatDialogFragment {

    private String mTitle;
    private String mMessage;

    public static MessageDialogFragment newInstance(String title, String message) {

        Bundle args = new Bundle();
        args.putString("title", title);
        args.putString("message", message);

        MessageDialogFragment fragment = new MessageDialogFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        mTitle = args.getString("title");
        mMessage = args.getString("message");
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return new AlertDialog.Builder(getContext())
                .setTitle(mTitle)
                .setMessage(mMessage)
                .setPositiveButton("确定", null)
                .create();
    }
}

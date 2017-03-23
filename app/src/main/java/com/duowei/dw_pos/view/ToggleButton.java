package com.duowei.dw_pos.view;

import android.content.Context;
import android.support.v7.widget.AppCompatButton;
import android.util.AttributeSet;

/**
 * Created by Administrator on 2017-03-23.
 */

public class ToggleButton extends AppCompatButton {

    private ButtonType mButtonType = ButtonType.TYPE_1;

    public ToggleButton(Context context) {
        super(context);
        initUi();
    }

    public ToggleButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        initUi();
    }

    public ToggleButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initUi();
    }

    private void initUi() {
        if (mButtonType == ButtonType.TYPE_1) {
            setText("单品");
        } else {
            setText("套餐");
        }
    }

    public void toggle() {
        if (mButtonType == ButtonType.TYPE_1) {
            mButtonType = ButtonType.TYPE_2;
        } else {
            mButtonType = ButtonType.TYPE_1;
        }

        initUi();

        if (mToggleListener != null) {
            mToggleListener.onToggle(mButtonType);
        }
    }

    public enum ButtonType {
        /**
         * 单品
         */
        TYPE_1,
        /**
         * 套餐
         */
        TYPE_2
    }

    public void setToggleListener(OnToggleListener listener) {
        mToggleListener = listener;
    }

    private OnToggleListener mToggleListener;

    public interface OnToggleListener {
        /**
         * @param type 切换后的类型
         */
        void onToggle(ButtonType type);
    }
}

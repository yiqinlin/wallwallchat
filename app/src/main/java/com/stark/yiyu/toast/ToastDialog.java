package com.stark.yiyu.toast;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.KeyEvent;
import android.widget.TextView;

import com.stark.yiyu.R;

/**
 * Created by Stark on 2017/2/10.
 */
public class ToastDialog extends Dialog {
    private TextView tvLoadInfo;
    private boolean isAllowClose = true;

    public ToastDialog(Context context) {
        this(context, R.style.NoFrameNoDim_Dialog);
    }
    public ToastDialog(Context context, int theme) {
        super(context, theme);
        setContentView(R.layout.dialog_toast);
        tvLoadInfo = (TextView) findViewById(R.id.dialogToast_tvLoadInfo);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setCanceledOnTouchOutside(true);
    }


    public ToastDialog setText(String msg) {
        tvLoadInfo.setText(msg);
        return this;
    }

    public ToastDialog setOnKeyDownClose(boolean isAllowClose) {
        this.isAllowClose = isAllowClose;
        return this;
    }
    public ToastDialog setOnTouchClose(boolean temp)
    {
        setCanceledOnTouchOutside(temp);
        return this;
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (!isAllowClose) {
            return true;
        } else {
            return super.onKeyDown(keyCode, event);
        }
    }
}

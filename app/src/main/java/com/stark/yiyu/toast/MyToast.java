package com.stark.yiyu.toast;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.stark.yiyu.R;

/**
 * Created by Stark on 2017/2/10.
 */
public class MyToast {
    private Toast mToast;
    static private MyToast myToast;
    private MyToast(Context context, CharSequence text, int duration) {
        View v = LayoutInflater.from(context).inflate(R.layout.mytoast, null);
        TextView textView = (TextView) v.findViewById(R.id.text_toast);
        textView.setText(text);
        mToast = new Toast(context);
        mToast.setDuration(duration);
        mToast.setView(v);
    }

    public static MyToast makeText(Context context, CharSequence text, int duration) {
        myToast=new MyToast(context, text, duration);
        return myToast;
    }
    public void show() {
        if (mToast != null) {
            mToast.show();
        }
    }
    public MyToast setGravity(int gravity, int xOffset, int yOffset) {
        if (mToast != null) {
            mToast.setGravity(gravity, xOffset, yOffset);
        }
        return this.myToast;
    }
}

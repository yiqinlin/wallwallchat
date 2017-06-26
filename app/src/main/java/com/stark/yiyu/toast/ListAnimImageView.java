package com.stark.yiyu.toast;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.util.AttributeSet;
import android.widget.ImageView;

/**
 * Created by Stark on 2017/2/10.
 */
public class ListAnimImageView extends ImageView {
    private AnimationDrawable animationDrawable;

    public ListAnimImageView(Context context) {
        super(context);
        inti();

    }

    public ListAnimImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        inti();
    }

    public ListAnimImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        inti();
    }

    public void inti() {
        animationDrawable = (AnimationDrawable) getDrawable();
        animationDrawable.start();
    }

    public void startAnimation() {
        animationDrawable.start();
    }

    public void stopAnimation() {
        animationDrawable.setVisible(true, true);
        animationDrawable.stop();
    }

    public void pauseAnimation() {
        animationDrawable.stop();
    }
}

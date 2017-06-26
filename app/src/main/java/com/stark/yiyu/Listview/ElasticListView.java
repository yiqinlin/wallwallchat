package com.stark.yiyu.Listview;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ListView;

import com.stark.yiyu.Util.Elastic;

/**
 * Created by Stark on 2017/3/26.
 */
public class ElasticListView extends ListView{
    private Context context=null;
    private int RADIO=2;
    private int startX;
    private int startY;
    private int endX;
    private int endY;
    private OnBackListener mBackListener;
    public ElasticListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context=context;
    }
    public interface OnBackListener{
        void onBack();
    }
    private void onBack(){
        if(mBackListener!=null){
            mBackListener.onBack();
        }
    }
    public void setonBackListener(OnBackListener onBackListener) {
        this.mBackListener = onBackListener;
    }
    @Override
    public boolean onTouchEvent(MotionEvent ev) {
            switch (ev.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    startX=(int)ev.getX();
                    startY=(int)ev.getY();
                    break;
                case MotionEvent.ACTION_UP:
                    endX = (int)ev.getX();
                    endY = (int) ev.getY();
                    Elastic.set((endY - startY) / RADIO, ElasticListView.this);
                    if(endY-startY<endX-startX){
                        onBack();
                    }
                    break;
                case MotionEvent.ACTION_MOVE:
                    endY = (int) ev.getY();
                    this.setPadding(0,(endY - startY)/RADIO,0,0);
                    break;
            }
        return super.onTouchEvent(ev);
    }
}

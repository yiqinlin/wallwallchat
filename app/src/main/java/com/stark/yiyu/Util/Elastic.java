package com.stark.yiyu.Util;

import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.ListView;

/**
 * Created by Stark on 2017/3/26.
 */
public class Elastic {
    private static LinearLayout HeadView;
    private static ListView listView;
    private static int offset;
    private static int delay;
    private static double step=0.0;
    private static int padding;
    private static int HeadHeight;
    public static void set(int Offset,LinearLayout headView,int headHeight){
        HeadView=headView;
        HeadHeight=headHeight;
        set(Offset);
        handle.postDelayed(ScrollElasticTop,0);
    }
    public static void set(int Offset){
        Reset();
        offset = Offset;
    }
    public static void set(int Offset,ListView listview){
        listView=listview;
        delay=400;
        set(Offset);
        if(Offset>=0) {
            handle.postDelayed(ScrollElasticUp, 0);
        }else{
            handle.postDelayed(ScrollElasticDown,0);
        }
    }
    public static void set(int Offset,int Delay,ListView listview){
        listView=listview;
        delay=Delay;
        set(Offset);
        handle.postDelayed(ScrollElasticUp, 0);
    }
    public static void setRefreshUp(int Offset,LinearLayout headView){
        HeadView=headView;
        set(Offset);
        handle.postDelayed(RefreshingElastic,0);
    }
    public static void setRefreshUp(int Offset,LinearLayout headView,int headHeight){
        HeadView=headView;
        HeadHeight=headHeight;
        set(Offset);
        handle.postDelayed(DoneElastic,0);
    }
    private static void Reset(){
        padding=0;
        step=0.0;
    }
    static Handler handle=new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            handle.removeCallbacks(ScrollElasticTop);
            return false;
        }
    });
    static Runnable ScrollElasticTop =new Runnable() {
        @Override
        public void run() {
            padding=Gravity.Gtoh(offset,step);
            Log.i("padding",padding+"");
            step+=618.0;
            if(padding<offset&&padding>=0) {
                HeadView.setPadding(0, padding - HeadHeight, 0, 0);
                handle.postDelayed(ScrollElasticTop,0);
            }else{
                HeadView.setPadding(0, -HeadHeight, 0, 0);
            }
        }
    };
    static Runnable ScrollElasticUp =new Runnable() {
        @Override
        public void run() {
            padding=Gravity.getH(offset, step, delay);
            step+=70.0;
            if(padding>=0) {
                listView.setPadding(0, padding, 0, 0);
                handle.postDelayed(ScrollElasticUp,0);
            }else{
                listView.setPadding(0, 0, 0, 0);
            }
        }
    };
    static Runnable ScrollElasticDown =new Runnable() {
        @Override
        public void run() {
            padding=Gravity.getH(-offset, step, delay);
            step+=70.0;
            if(padding>=0) {
                listView.setPadding(0, -padding, 0, 0);
                handle.postDelayed(ScrollElasticDown,0);
            }else{
                listView.setPadding(0, 0, 0, 0);
            }
        }
    };
    static Runnable RefreshingElastic =new Runnable() {
        @Override
        public void run() {
            padding=Gravity.getH(offset,step,400);
            step+=50.0;
            if(padding>=0) {
                HeadView.setPadding(0, padding, 0, 0);
                handle.postDelayed(RefreshingElastic,0);
            }else{
                HeadView.setPadding(0, 0, 0, 0);
            }
        }
    };
    static Runnable DoneElastic =new Runnable() {
        @Override
        public void run() {
            padding=Gravity.getH(offset,step,400);
            step+=50.0;
            if(padding>=0) {
                HeadView.setPadding(0, padding-HeadHeight, 0, 0);
                handle.postDelayed(DoneElastic,0);
            }else{
                HeadView.setPadding(0, -HeadHeight, 0, 0);
            }
        }
    };
}

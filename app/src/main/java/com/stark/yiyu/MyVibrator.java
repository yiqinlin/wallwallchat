package com.stark.yiyu;

import android.app.Service;
import android.content.Context;
import android.os.Vibrator;

/**
 * Created by Stark on 2017/1/21.
 */
public class MyVibrator {
    public static void getVibrator(Context context,int n){
        long[] patten;
        switch (n){
            case 0:
                patten=new long[]{0,55,170,165,180,55};
                break;
            default:
                patten=new long[]{0,55,170,165,180,55};
        }
        Vibrator vib = (Vibrator) context.getSystemService(Service.VIBRATOR_SERVICE);
        vib.vibrate(patten, -1);
    }
}

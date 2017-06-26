package com.stark.yiyu.Util;

/**
 * Created by Stark on 2017/3/26.
 */
public class Gravity {
    public static int Gtoh(int height,double time){
        return (int)((Math.sqrt(20*height)-5.0*time/1000.0)*time/1000.0);
    }
    public static int getH(int height,double time,double delay){
        return (int)(height-((double)height/(delay/1000.0))*time/1000.0);
    }
    public static int delaytime(int height){
        return (int)(Math.sqrt((double)height/5.0)*1000);
    }
}

package com.stark.yiyu.Util;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Stark on 2017/3/13.
 */
public class DateUtil {
    public static String getMsgCode(Context context){
        SharedPreferences sp=context.getSharedPreferences("action",Context.MODE_PRIVATE);
        String msgcode=sp.getString("msgcode", "900000000000000000");
        msgcode=(Long.parseLong(msgcode)+1L)+"";
        sp.edit().putString("msgcode", msgcode).apply();
        return msgcode;
    }
    public static String Mtod(String msgcode){
        if(msgcode!=null&&!msgcode.equals("")) {
            return  msgcode.substring(0,4)+"-"+msgcode.substring(4,6)+"-"+msgcode.substring(6,8);
        }else {
            return msgcode;
        }
    }
    public static String Mtot(String msgcode){
        if(msgcode!=null&&!msgcode.equals("")) {
            return msgcode.substring(8,10)+":"+msgcode.substring(10,12);
        }else {
            return msgcode;
        }
    }
}

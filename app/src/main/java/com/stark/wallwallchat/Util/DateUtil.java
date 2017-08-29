package com.stark.wallwallchat.Util;

import android.content.Context;
import android.content.SharedPreferences;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

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
    public static String MtoST(String msgcode){
        if(msgcode!=null&&!msgcode.equals("")) {
            Date date = new Date();
            DateFormat y = new SimpleDateFormat("yyyy", Locale.CHINA);
            DateFormat d = new SimpleDateFormat("MM-dd", Locale.CHINA);
            return y.format(date).equals(msgcode.substring(0, 4)) ? (d.format(date).equals(Mtod(msgcode)) ? Mtot(msgcode) : Mtod(msgcode)) : msgcode.substring(0, 4) + "-" + msgcode.substring(4, 6);
        }else {
            return "";
        }
    }
    public static String MtoNT(String msgcode){
        Date date=new Date();
        DateFormat y=new SimpleDateFormat("yyyy", Locale.CHINA);
        DateFormat d=new SimpleDateFormat("MM-dd", Locale.CHINA);
        String result=y.format(date).equals(msgcode.substring(0,4))?"":msgcode.substring(0,4)+"-";
        result+=(result.equals("")&&d.format(date).equals(Mtod(msgcode)))? Mtot(msgcode):(Mtod(msgcode)+" "+ Mtot(msgcode));
        return result;
    }
    public static String Mtod(String msgcode){
        if(msgcode!=null&&!msgcode.equals("")) {
            return  msgcode.substring(4,6)+"-"+msgcode.substring(6,8);
        }else {
            return msgcode;
        }
    }

    public static String Mtoy(String msgcode){
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

package com.stark.wallwallchat.NetWork;

import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.math.BigInteger;
import java.security.MessageDigest;

/**
 * Created by Stark on 2017/2/11.
 */
public class MD5 {
    public static String get(String str) {
        try {
            // 生成一个MD5加密计算摘要
            MessageDigest md = MessageDigest.getInstance("MD5");
            // 计算md5函数
            md.update(str.getBytes());
            // digest()最后确定返回md5 hash值，返回值为8为字符串。因为md5 hash值是16位的hex值，实际上就是8位的字符
            // BigInteger函数则将8位的字符串转换成16位hex值，用字符串来表示；得到字符串形式的hash值
            return new BigInteger(1, md.digest()).toString(16).substring(8,24);
        } catch (Exception e) {
            Log.i("MD5",e.toString());
        }
        return null;
    }
    public static String get(byte[] temp){
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(temp);
            byte[] b=md.digest();
            String result="";
            for (int i=0; i < b.length; i+=2) {
                result += Integer.toString( ( b[i] & 0xff ) + 0x100, 16).substring(1);
            }
            return result;
        }catch (Exception e){
            Log.i("MD5",e.toString());
        }
        return null;
    }
    public static String get(File file){
        try {
            byte[] sendMsg=new byte[(int)file.length()];
            FileInputStream fis = new FileInputStream(file);
            fis.read(sendMsg);
            return get(sendMsg);
        }catch (Exception e){
            Log.i("MD5",e.toString());
        }
        return null;
    }
}

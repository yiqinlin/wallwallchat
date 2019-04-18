package com.stark.wallwallchat.SQLite;

import android.content.ContentValues;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by Stark on 2017/3/2.
 */
public class Data {
    public static ContentValues MapToContentValues(HashMap map){
        ContentValues cv=new ContentValues();
        if(map!=null) {
            Iterator it = map.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry entry = (Map.Entry) it.next();
                cv.put(entry.getKey().toString(), entry.getValue().toString());
            }
        }
        return cv;
    }
}

//        Data_new_path=Environment.getExternalStorageDirectory().getAbsolutePath()+"/360/";
//        File music = Environment.getExternalStorageDirectory();
//        File []pp =music.listFiles();
//        for (File file : pp) {
//            Log.i("hh", " 文件名：" + file.getName() + "文件路径 ：" + file.getAbsolutePath());//获得file的绝对路径
//        }
//        if(ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
//            //做申请权限处理
//            ActivityCompat.requestPermissions(this, new String[]
//                    {Manifest.permission.WRITE_EXTERNAL_STORAGE}, 2);
//        }else {
//        }
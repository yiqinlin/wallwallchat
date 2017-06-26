package com.stark.yiyu.SQLite;

import android.content.ContentValues;

/**
 * Created by Stark on 2017/3/2.
 */
public class Data {
    public static ContentValues getSChatContentValues(String id,int type,int bubble,String msg,String msgcode,String date,String time,int ack){
        ContentValues cv=new ContentValues();
        if(id!=null){
            cv.put("id",id);
        }if(type!=-1){
            cv.put("type",type);
        }if(bubble!=-1){
            cv.put("bubble",bubble);
        }if(msg!=null){
            cv.put("msg",msg);
        }if(msgcode!=null){
            cv.put("msgcode",msgcode);
        }if(date!=null){
            cv.put("date",date);
        }if(time!=null){
            cv.put("time",time);
        }if(ack!=-1){
            cv.put("ack",ack);
        }
        return cv;
    }
    public static ContentValues getMidContentValues(String id,String head,String remarks,String message,String date,String count){
        ContentValues cv=new ContentValues();
        if(id!=null){
            cv.put("id", id);
        }if(head!=null){
            cv.put("head",head);
        }if(remarks!=null) {
            cv.put("remarks", remarks);
        }if(message!=null) {
            cv.put("message", message);
        }if(date!=null) {
            cv.put("date", date);
        }if(count!=null) {
            cv.put("count", count);
        }
        return cv;
    }
    public static ContentValues getUserContentValues(String id,String nick,String auto,int sex,String birth,String pnumber,String startdate,int catdate,int typeface,int theme,int bubble,int iknow,int knowme){
        ContentValues cv=new ContentValues();
        if(id!=null){
            cv.put("id", id);
        }if(nick!=null) {
            cv.put("nick", nick);
        }if(auto!=null) {
            cv.put("auto", auto);
        }if(sex!=-1){
            cv.put("sex",sex);
        }if(birth!=null) {
            cv.put("birth", birth);
        }if(pnumber!=null) {
            cv.put("pnumber", pnumber);
        }if(startdate!=null) {
            cv.put("startdate", startdate);
        }if(catdate!=-1) {
            cv.put("catdate", catdate);
        }if(typeface!=-1) {
            cv.put("typeface", typeface);
        }if(theme!=-1){
            cv.put("theme",theme);
        }if(bubble!=-1){
            cv.put("bubble",bubble);
        }if(iknow!=-1){
            cv.put("iknow",iknow);
        }if(knowme!=-1){
            cv.put("knowme",knowme);
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
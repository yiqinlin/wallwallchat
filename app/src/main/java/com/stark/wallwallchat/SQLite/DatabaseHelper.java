package com.stark.wallwallchat.SQLite;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Stark on 2017/2/28.
 */
public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String name = "data"; //数据库名称
    private static final int version = 1; //数据库版本
    public DatabaseHelper(Context context) {
        super(context, name, null, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS userdata(id varchar(20),head varchar(32),nick varchar(16),auto varchar(100),sex varchar(2),birth varchar(10),college varchar(30),edu varchar(10),mail varchar(30),pnumber varchar(11),startdate varchar(10),catdate varchar(6),typeface varchar(6),bubble varchar(6),theme varchar(6),iknow varchar(20),knowme varchar(20))");
        db.execSQL("CREATE TABLE IF NOT EXISTS wall(msgcode varchar(20),sponsor varchar(20),remarks varchar(20),edu varchar(20),msg varchar(1024),cnum varchar(10),img varchar(2) DEFAULT '0',anum varchar(10),mode varchar(2),type varchar(2),isagree varchar(5))");
        db.execSQL("CREATE TABLE IF NOT EXISTS msg(sponsor varchar(20),receiver varchar(20),sendtype varchar(6),bubble varchar(6),msg varchar(1024),msgcode varchar(20),msgcode2 varchar(20),ack integer)");
        db.execSQL("CREATE TABLE If Not Exists file(hashcode varchar(32),path varchar(100),time varchar(20),name varchar(50))");
        db.execSQL("CREATE TABLE IF NOT EXISTS mid(sponsor varchar(20),remarks varchar(20),sendtype varchar(6),msg varchar(100),msgcode varchar(20),num varchar(5))");
        db.execSQL("CREATE TABLE IF NOT EXISTS config(key varchar(20),value integer)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }
}
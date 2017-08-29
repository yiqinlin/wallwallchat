package com.stark.wallwallchat.SQLite;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Stark on 2017/2/28.
 */
public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String name = "yiyu"; //数据库名称
    private static final int version = 1; //数据库版本
    public DatabaseHelper(Context context) {
        super(context, name, null, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS userdata(id varchar(20),nick varchar(16),auto varchar(50),sex integer,birth varchar(10),college varchar(30),edu char(10),mail char(30),pnumber varchar(11),startdate varchar(10),catdate integer,typeface integer,theme integer,bubble integer,iknow integer,knowme integer)");
        db.execSQL("CREATE TABLE IF NOT EXISTS mid(id varchar(20),head varchar(20),remarks varchar(20),message varchar(100),msgcode varchar(20),count varchar(3))");
        db.execSQL("CREATE TABLE IF NOT EXISTS config(key varchar(20),value integer)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }
}
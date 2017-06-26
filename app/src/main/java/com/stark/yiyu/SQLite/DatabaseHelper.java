package com.stark.yiyu.SQLite;

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
        db.execSQL("CREATE TABLE IF NOT EXISTS mid(id varchar(20),head varchar(20),remarks varchar(20),message varchar(100),date varchar(20),count varchar(3))");
        db.execSQL("CREATE TABLE IF NOT EXISTS config(key varchar(20),value integer)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }
}
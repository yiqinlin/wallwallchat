package com.stark.wallwallchat.Database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.stark.wallwallchat.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

/**
 * Created by asus on 2017/7/14.
 */
public class DatabaseSchoolHelper extends SQLiteOpenHelper {

    private static final String name = "school.db";
    private static final int version = 1;
    Context mContext;

    public DatabaseSchoolHelper(Context context) {
        super(context, name, null, version);
        mContext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS data(province varchar(10),name varchar(30),website varchar(30),code varchar(20))");
        InitDatabase(mContext);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public static String getDatabasePath(Context context) {
        String DB_PATH = String.format("/data/data/%1$s/databases/", context.getPackageName());
        File f = new File(DB_PATH);
        if (!f.exists() && !f.mkdir()) {
            return null;
        }
        return DB_PATH;
    }

    public static boolean InitDatabase(Context context) {
        String DATABASE_PATH = getDatabasePath(context);
        try {
            FileOutputStream fs = new FileOutputStream(DATABASE_PATH + name);
            InputStream is = context.getResources().openRawResource(R.raw.schools);
            byte[] buffer = new byte[512];
            int count;
            while ((count = is.read(buffer)) != -1) {
                fs.write(buffer, 0, count);
                fs.flush();
            }
            fs.close();
            is.close();
        } catch (Exception e) {
            return false;
        }
        return true;
    }
}

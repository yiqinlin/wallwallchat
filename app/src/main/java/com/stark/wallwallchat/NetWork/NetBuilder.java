package com.stark.wallwallchat.NetWork;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.stark.wallwallchat.SQLite.DatabaseHelper;
import com.stark.wallwallchat.bean.BaseItem;
import com.stark.wallwallchat.bean.ItemMargin;
import com.stark.wallwallchat.bean.ItemSMsg;
import com.stark.wallwallchat.bean.ItemWallInfo;
import com.stark.wallwallchat.json.JsonConvert;

import org.json.JSONException;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Created by Stark on 2017/8/31.
 */
public class NetBuilder {
    public Map<String,Object> Config;
    public Map<String,Object> Package;
    public List<Object> Data;
    public NetBuilder(){
        Config=new HashMap<String, Object>() ;
        Package=new HashMap<String,Object>();
        Data=new ArrayList<Object>();
    }
    public NetBuilder(String json)throws IllegalAccessException,JSONException{
        NetBuilder temp=(NetBuilder)JsonConvert.DeserializeObject(json,new NetBuilder());
        Config=temp.Config;
        Package=temp.Package;
        Data=temp.Data;
    }
    public String get(String key){
        return get(key, null);
    }
    public String get(String key,String Default){
        if(Config.containsKey(key)){
            return Config.get(key).toString();
        }else if(Package.containsKey(key)){
            return Package.get(key).toString();
        }
        return Default;
    }
    public int getInt(String key,int Default){
        String temp=get(key);
        if(temp==null){
            return Default;
        }else{
            return Integer.parseInt(temp);
        }
    }
    public Long getLong(String key,Long Default){
        String temp=get(key);
        if(temp==null){
            return Default;
        }else{
            return Long.parseLong(temp);
        }
    }
    public boolean getBool(String key,boolean Default){
        String temp=get(key);
        if(temp==null){
            return Default;
        }else{
            return Boolean.parseBoolean(temp);
        }
    }
    public Object getList(int i){
        if(ListSize()==0){
            return null;
        }
        return Data.get(i);
    }
    public int ListSize(){
        return Data.size();
    }
    public NetBuilder add(String key,Object value){
        Config.put(key,value);
        return this;
    }
    public NetBuilder put(String key,Object value){
        Package.put(key, value);
        return this;
    }
    public NetBuilder in(Map pkg){
        Data.add(pkg);
        return this;
    }
    public String build(Context context) throws IllegalAccessException,JSONException{
        SharedPreferences sp=context.getSharedPreferences("action",Context.MODE_PRIVATE);
        String id=sp.getString("id", null);
        String edu=sp.getString("edu", null);
        if(id==null||id.equals("")||edu==null||edu.equals("")){
            throw new NullPointerException("login information expired,please log in again !");
        }
        String uuid=sp.getString("uuid",null);
        if(uuid==null||uuid.equals("")){
            uuid=new SimpleDateFormat("yyyyMMddHHmmssSSS", Locale.CHINA).format(new Date());
            sp.edit().putString("uuid",uuid).apply();
        }
        add("sponsor",id);
        add("edu",edu);
        add("uuid", uuid);
        return build();
    }
    public String build() throws IllegalAccessException,JSONException{
        return JsonConvert.SerializeObject(this);
    }
    public void UpdateDB(SQLiteDatabase db,SharedPreferences sp){
        int length=Data.size();
        HashMap<String,Object> User;
        for (int i=0;i<length;i++) {
            try {
                User = (HashMap)Data.get(i);
                Cursor cr=db.query("userdata",new String[]{"id"},"id=?",new String[]{(String)User.get("id")},null,null,null);
                if(User.get("id")==sp.getString("id",null)) {
                    sp.edit().putString("nick", (String) User.get("nick")).putString("auto", (String) User.get("auto")).putString("edu", (String) User.get("edu")).apply();
                }
                if(cr!=null&&cr.getCount()>0&&cr.moveToNext()) {
                    db.update("userdata", com.stark.wallwallchat.SQLite.Data.MapToContentValues((HashMap) Data.get(i)), "id=?", new String[]{(String)User.get("id")});
                }else{
                    db.insert("userdata", null, com.stark.wallwallchat.SQLite.Data.MapToContentValues((HashMap) Data.get(i)));
                }
            } catch (Exception e) {
                Log.i("UpdateDB", e.toString());
            }
        }
        db.close();
    }
    public void UpdateWallDB(Context context){
        SQLiteDatabase db=new DatabaseHelper(context).getWritableDatabase();
        int length=Data.size();
        HashMap<String,Object> User;
        for (int i=0;i<length;i++) {
            try {
                User = (HashMap)Data.get(i);
                Cursor cr=db.query("wall",new String[]{"msgcode"},"msgcode=?",new String[]{(String)User.get("msgcode")},null,null,null);
                if(cr!=null&&cr.getCount()>0&&cr.moveToNext()) {
                    db.update("wall", com.stark.wallwallchat.SQLite.Data.MapToContentValues((HashMap) Data.get(i)), "msgcode=?", new String[]{(String) User.get("msgcode")});
                }else{
                    db.insert("wall", null, com.stark.wallwallchat.SQLite.Data.MapToContentValues((HashMap) Data.get(i)));
                }
            } catch (Exception e) {
                Log.i("UpdateDB", e.toString());
            }
        }
        db.close();
    }
    public void UpdateMidDB(Context context){
        SQLiteDatabase db=new DatabaseHelper(context).getWritableDatabase();
        int length=Data.size();
        HashMap<String,Object> User;
        for (int i=0;i<length;i++) {
            try {
                User = (HashMap)Data.get(i);
                Cursor cr=db.query("mid",new String[]{"sponsor"},"sponsor=?",new String[]{(String)User.get("sponsor")},null,null,null);
                if(cr!=null&&cr.getCount()>0&&cr.moveToNext()) {
                    db.update("mid", com.stark.wallwallchat.SQLite.Data.MapToContentValues((HashMap) Data.get(i)), "sponsor=?", new String[]{(String) User.get("sponsor")});
                }else{
                    db.insert("mid", null, com.stark.wallwallchat.SQLite.Data.MapToContentValues((HashMap) Data.get(i)));
                }
            } catch (Exception e) {
                Log.i("UpdateDB", e.toString());
            }
        }
        db.close();
    }
    public void UpdateWall(Context context,  ArrayList<BaseItem> mArrays){
        int length=Data.size();
        HashMap<String,Object> Msg;
        int item_type=11;
        for (int i=0;i<length;i++) {
            try {
                Log.e("temp", Data.get(i).toString());
                Msg =(HashMap)Data.get(i);
                switch (Integer.parseInt((String) Msg.get("mode"))){
                    case 0:
                        item_type = 11;
                        break;
                    case 1:
                        item_type = 12;
                        break;
                }
                mArrays.add( new ItemWallInfo(item_type, Msg));
                if(i!=length-1)
                    mArrays.add(new ItemMargin(8));
            } catch (Exception e) {
                Log.e("UpdateWall", e.toString());
            }
        }
    }
    public void UpdateComment(Context context, ArrayList<BaseItem> mArrays) {
        int length = Data.size();
        HashMap<String, Object> Msg;
        int item_type;
        for (int i = 0; i < length; i++) {
            try {
                Msg = (HashMap) Data.get(i);
                switch (Integer.parseInt((String) Msg.get("mode"))) {
                    case 0:
                        item_type = 13;
                        break;
                    case 1:
                        item_type = 14;
                        break;
                    default:
                        item_type = 13;
                        break;
                }
                mArrays.add(new ItemWallInfo(item_type, Msg));
            } catch (Exception e) {
                Log.i("UpdateComment", e.toString());
            }
        }
    }

    public void UpdateData(Context context, SQLiteDatabase db, String DesId, ArrayList<BaseItem> mArray){
        int length=Data.size();
        HashMap<String,Object> Msg;
        Cursor cursor;
        int item_type;
        for (int i=0;i<length;i++) {
            try {
                Msg = (HashMap)Data.get(i);
                if (Msg.get("sponsor").equals(DesId)) {
                    item_type = 1;
                } else if (Msg.get("sponsor").equals("10000")) {
                    item_type = 2;
                } else {
                    item_type = 0;
                }
                Msg.remove("sendack");
                Msg.put("ack", 1);
                mArray.add(0, new ItemSMsg(item_type, Msg));
                cursor= db.query("msg",null, "msgcode2=?", new String[]{(String)Msg.get("msgcode2")}, null, null, null);
                if(cursor!=null&&cursor.getCount()>0&&cursor.moveToNext()){
                    db.update("msg", com.stark.wallwallchat.SQLite.Data.MapToContentValues(Msg),"msgcode2=?", new String[]{(String)Msg.get("msgcode2")});
                    cursor.close();
                }else{
                    db.insert("msg", null, com.stark.wallwallchat.SQLite.Data.MapToContentValues(Msg));
                }
            } catch (Exception e) {
                Log.i("UpdateDB", e.toString());
            }
        }
    }
}
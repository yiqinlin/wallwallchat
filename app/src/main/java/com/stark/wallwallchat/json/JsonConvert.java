package com.stark.wallwallchat.json;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.stark.wallwallchat.Format.Get;
import com.stark.wallwallchat.Format.Refresh;
import com.stark.wallwallchat.Format.UserInfo;
import com.stark.wallwallchat.SQLite.Data;
import com.stark.wallwallchat.bean.BaseItem;
import com.stark.wallwallchat.bean.ItemSMsg;
import com.stark.wallwallchat.bean.ItemWallInfo;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by Stark on 2017/2/8.
 */
public class JsonConvert {
    public static String SerializeObject(Object obj)throws IllegalAccessException,JSONException         /**序列化：封装成json格式*/
    {
        JSONObject json=new JSONObject();
        Field[] fields = obj.getClass().getDeclaredFields();//用反射获取内部类的属性
        for (Field field : fields) {
            switch(getType(field.getType()))//field.getType:获取属性声明时类型对象（返回class对象）
            {
                case 0://字符串类型,//field.getName():获取属性声明时名字
                    json.put(field.getName(),(field.get(obj)==null?"":field.get(obj)));//get(Object obj):取得obj对象这个Field上的值
                    break;
                case 1://整型
                    json.put(field.getName(),(int)(field.get(obj)==null?0:field.get(obj)));
                    break;
                case 2:
                    json.put(field.getName(),(long)(field.get(obj)==null?0:field.get(obj)));
                    break;
                case 3:
                    json.put(field.getName(),(float)(field.get(obj)==null?0:field.get(obj)));
                    break;
                case 4:
                    json.put(field.getName(),(double)(field.get(obj)==null?0:field.get(obj)));
                    break;
                case 5:
                    json.put(field.getName(),(boolean)(field.get(obj)==null?false:field.get(obj)));
                    break;
                case 6:
                case 7:
                case 8://JsonArray型
                    json.put(field.getName(),(field.get(obj)==null?null:field.get(obj)));
                    break;
                case 9:
                    json.put(field.getName(),  new JSONArray((List)field.get(obj)));
                    break;
                case 10:
                    json.put(field.getName(),new  JSONObject((HashMap)field.get(obj)));
                    break;
            }
        }
        return json.toString();
    }
    public static Object DeserializeObject(String JsonStr,Object obj)throws JSONException,NullPointerException,IllegalAccessException{
        if(JsonStr ==null||JsonStr.equals("")||obj==null) {
            throw new NullPointerException("JsonString can't is null");
        }
        if(obj==null) {
            throw new NullPointerException("object can't is null");
        }
        Field[] fields= obj.getClass().getDeclaredFields();
        JSONObject jsonObject=(JSONObject)new JSONTokener(JsonStr).nextValue();
        for (Field field : fields) {
            field.setAccessible(true);
            field.set(obj,JsonObjectToObject(jsonObject, field));
        }
        return obj;
    }
    private static Object JsonObjectToObject(JSONObject obj,Field field) throws JSONException{
        switch (getType(field.getType()))//field.getType:获取属性声明时类型对象（返回class对象）
        {
            case 0:
                return obj.opt(field.getName());
            case 1:
                return obj.optInt(field.getName());
            case 2:
                return obj.optLong(field.getName());
            case 3:
            case 4:
                return obj.optDouble(field.getName());
            case 5:
                return obj.optBoolean(field.getName());
            case 6:
            case 7:
            case 8://JsonArray型
                return obj.optJSONArray(field.getName());
            case 9:
                return JsonArrayToList(obj.optJSONArray(field.getName()));
            case 10:
                return JsonObjectToMap(obj.optJSONObject(field.getName()));
        }
        return null;
    }
    private static Map<String, Object> JsonObjectToMap(JSONObject jsonResult) throws JSONException {
        Map<String, Object> result = new HashMap<String, Object>();
        if(jsonResult!=null) {
            Iterator<String> keyIt = jsonResult.keys();
            while (keyIt.hasNext()) {
                String key = keyIt.next();
                Object val = jsonResult.get(key);
                if (val.equals(null)) {
                    continue;
                }
                if (!(val instanceof JSONObject) && !(val instanceof JSONArray)) {
                    result.put(key, val);
                    continue;
                }
                if (val instanceof JSONObject) {
                    Map<String, Object> valMap = JsonObjectToMap((JSONObject) val);
                    result.put(key, valMap);
                    continue;
                }
                if (val instanceof JSONArray) {
                    JSONArray ja = (JSONArray) val;
                    result.put(key, JsonArrayToList(ja));
                }
            }
        }
        return result;
    }
    private static List<Object> JsonArrayToList(JSONArray jsonArray) throws JSONException {
        List<Object> list = new ArrayList<Object>();
        for (int i = 0; i < jsonArray.length(); i++) {
            Object val = jsonArray.get(i);
            if(val==null) {
                continue;
            }
            if (!(val instanceof JSONObject) && !(val instanceof JSONArray)) {
                list.add(val);
                continue;
            }
            if (val instanceof JSONObject) {
                Map<String, Object> map = JsonObjectToMap((JSONObject) val);
                list.add(map);
                continue;
            }
            if (val instanceof JSONArray) {
                list.add(JsonArrayToList((JSONArray) val));
                continue;
            }

        }
        return list;
    }
    private static int getType(Class<?> type)//Class<?>它是个通配泛型，?可以代表任何类型
    {
        if(type!=null&&(String.class.isAssignableFrom(type)||Character.class.isAssignableFrom(type)||Character.TYPE.isAssignableFrom(type)||char.class.isAssignableFrom(type)))
            return 0;
        if(type!=null&&(Byte.TYPE.isAssignableFrom(type)||Short.TYPE.isAssignableFrom(type)||Integer.TYPE.isAssignableFrom(type)||Integer.class.isAssignableFrom(type)||Number.class.isAssignableFrom(type)||int.class.isAssignableFrom(type)||byte.class.isAssignableFrom(type)||short.class.isAssignableFrom(type)))
            return 1;
        if(type!=null&&(Long.TYPE.isAssignableFrom(type)||long.class.isAssignableFrom(type)))
            return 2;
        if(type!=null&&(Float.TYPE.isAssignableFrom(type)||float.class.isAssignableFrom(type)))
            return 3;
        if(type!=null&&(Double.TYPE.isAssignableFrom(type)||double.class.isAssignableFrom(type)))
            return 4;
        if(type!=null&&(Boolean.TYPE.isAssignableFrom(type)||Boolean.class.isAssignableFrom(type)||boolean.class.isAssignableFrom(type)))
            return 5;
        if(type!=null&&type.isArray())
            return 6;
        if(type!=null&&Connection.class.isAssignableFrom(type))
            return 7;
        if(type!=null&&JSONArray.class.isAssignableFrom(type))
            return 8;
        if(type!=null&&List.class.isAssignableFrom(type))
            return 9;
        if(type!=null&&Map.class.isAssignableFrom(type))
            return 10;
        return 11;
    }

    public static void UpdateData(Context context, SQLiteDatabase db, String DesId, Refresh refresh, ArrayList<BaseItem> mArray){
        int length=refresh.ChatData.size();
        HashMap<String,Object> Msg;
        Cursor cursor;
        int item_type;
        for (int i=0;i<length;i++) {
            try {
                Msg = (HashMap)refresh.ChatData.get(i);
                if (Msg.get("Guestid").equals(DesId)) {
                    item_type = 1;
                } else if (Msg.get("Guestid").equals("10000")) {
                    item_type = 2;
                } else {
                    item_type = 0;
                }
                mArray.add(0,new ItemSMsg(item_type, Msg));
                cursor= db.query("u" + DesId,null, "msgcode=?", new String[]{(String)Msg.get("MsgCode")}, null, null, null);
                if(cursor!=null&&cursor.getCount()>0&&cursor.moveToNext()){
                    db.update("u" + DesId,Data.MapToContentValues((HashMap)refresh.ChatData.get(i)),"msgcode=?", new String[]{(String)Msg.get("MsgCode")});
                    cursor.close();
                }else{
                    db.insert("u" + DesId, null, Data.MapToContentValues((HashMap) refresh.ChatData.get(i)));
                }
            } catch (Exception e) {
                Log.i("UpdateDB", e.toString());
            }
        }
    }
    public static void UpdateWall(Context context, Refresh refresh, ArrayList<BaseItem> mArrays){
        int length=refresh.WallData.size();
        HashMap<String,Object> Msg;
        int item_type;
        for (int i=0;i<length;i++) {
            try {
               Log.e("temp", refresh.WallData.get(i).toString());
                Msg =(HashMap)refresh.WallData.get(i);
                switch ((int)Msg.get("Mode")){
                    case 0:
                        item_type = 11;
                        break;
                    case 1:
                        item_type = 12;
                        break;
                    default:
                        item_type=11;
                        break;
                }
                mArrays.add(0, new ItemWallInfo(item_type,  Msg));
            } catch (Exception e) {
                Log.i("UpdateWall", e.toString());
            }
        }
    }
    public static void UpdateComment(Context context, Refresh refresh, ArrayList<BaseItem> mArrays){
        int length=refresh.WallData.size();
        HashMap<String,Object> Msg;
        int item_type;
        for (int i=0;i<length;i++) {
            try {
                Msg =(HashMap)refresh.WallData.get(i);
                switch ((int)Msg.get("Mode")){
                    case 0:
                        item_type = 13;
                        break;
                    case 1:
                        item_type = 14;
                        break;
                    default:
                        item_type=13;
                        break;
                }
                mArrays.add(new ItemWallInfo(item_type,  Msg));
            } catch (Exception e) {
                Log.i("UpdateComment", e.toString());
            }
        }
    }
    public static UserInfo GetInfo(Get get,int i){
        try {
            return (UserInfo)get.Data.get(i);
        }catch (Exception e){
            Log.i("GetInfo",e.toString());
            return null;
        }
    }
    public static void UpdateDB(SQLiteDatabase db,SharedPreferences sp,Get get){
        int length=get.Data.size();
        HashMap<String,Object> User;
        for (int i=0;i<length;i++) {
            try {
                User = (HashMap)get.Data.get(i);
                Cursor cr=db.query("userdata",new String[]{"id"},"id=?",new String[]{(String)User.get("Id")},null,null,null);
                sp.edit().putString("nick", (String) User.get("Nick")).putString("auto", (String) User.get("Auto")).putString("edu", (String)User.get("Edu")).apply();
                if(cr!=null&&cr.getCount()>0&&cr.moveToNext()) {
                    db.update("userdata", Data.MapToContentValues((HashMap) get.Data.get(i)), "id=?", new String[]{(String)User.get("Id")});
                }else{
                    db.insert("userdata", null, Data.MapToContentValues((HashMap) get.Data.get(i)));
                }
            } catch (Exception e) {
                Log.i("UpdateDB", e.toString());
            }
        }
    }
    public static ArrayList<BaseItem> AddList(Context context,String SrcID,ArrayList<BaseItem> mArrays,Refresh refresh){
        int length=refresh.ChatData.size();
        for (int i=0;i<length;i++) {
            JSONObject jsonobj;
            HashMap<String,Object> Msg;
            Msg=(HashMap) refresh.ChatData.get(i);
            int typeTemp;
            if(Msg.get("Guestid").equals(SrcID)){
                typeTemp=0;
            }else if(Msg.get("Guestid").equals("10000")){
                typeTemp=2;
            }else {
                typeTemp = 1;
            }
            mArrays.add(0, new ItemSMsg(typeTemp, Msg));
        }
        return mArrays;
    }
}

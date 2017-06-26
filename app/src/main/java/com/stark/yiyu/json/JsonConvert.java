package com.stark.yiyu.json;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.stark.yiyu.Format.Get;
import com.stark.yiyu.Format.Refresh;
import com.stark.yiyu.Format.SimpleMsg;
import com.stark.yiyu.Format.UserInfo;
import com.stark.yiyu.R;
import com.stark.yiyu.SQLite.Data;
import com.stark.yiyu.bean.BaseItem;
import com.stark.yiyu.bean.ItemSMsg;

import org.json.JSONObject;
import org.json.JSONTokener;

import java.lang.reflect.Field;
import java.util.ArrayList;

/**
 * Created by Stark on 2017/2/8.
 */
public class JsonConvert {
    public static String SerializeObject(Object obj)/**序列化：封装成json格式*/
    {
        JSONObject json=new JSONObject();
        Field[] fields = obj.getClass().getDeclaredFields();//用反射获取内部类的属性
        for (Field field : fields) {
            try {
                switch(TypeInt.getType(field.getType()))//field.getType:获取属性声明时类型对象（返回class对象）
                {
                    case 0://字符串类型,//field.getName():获取属性声明时名字
                        json.put(field.getName(),(field.get(obj)==null?"":field.get(obj)));//get(Object obj):取得obj对象这个Field上的值
                        break;
                    case 1://整型
                        json.put(field.getName(),(int)(field.get(obj)==null?0:field.get(obj)));
                        break;
                    case 2:
                        json.put(field.getName(),(boolean)(field.get(obj)==null?false:field.get(obj)));
                        break;
                    case 3:
                    case 4:
                    case 5:
                    case 6:
                    case 7://JsonArray型
                        json.put(field.getName(),(field.get(obj)==null?null:field.get(obj)));
                        break;
                }
            } catch (Exception e) {
                Log.i("JsonSer",e.toString());
            }
        }
        return json.toString();
    }
    public static Object DeserializeObject(String JsonStr,Object obj)/**反序列化*/
    {
        JSONObject jsonobj=new JSONObject();
        try{
            JSONTokener jsonTokener = new JSONTokener(JsonStr);
            jsonobj=(JSONObject)jsonTokener.nextValue();
        }catch (Exception e)
        {
            Log.i("JsonTokener",e.toString());
        }
        return getObject(obj, jsonobj);
    }
    public static void UpdateDB(SQLiteDatabase db,String DesId,Refresh refresh){
        int length=refresh.Data.length();
        SimpleMsg Msg;
        for (int i=0;i<length;i++) {
            try {
                Msg = (SimpleMsg) getObject(new SimpleMsg(), refresh.Data.getJSONObject(i));
                db.insert("u" + DesId, null, Data.getSChatContentValues(Msg.Guestid, Msg.SendType, Msg.Bubble, Msg.Msg, Msg.MsgCode, Msg.Date, Msg.Time, 1));
            } catch (Exception e) {
                Log.i("UpdateDB", e.toString());
            }
        }
    }
    public static UserInfo GetInfo(Get get,int i){
        try {
            return (UserInfo) getObject(new UserInfo(),get.Data.getJSONObject(i));
        }catch (Exception e){
            Log.i("GetInfo",e.toString());
            return null;
        }
    }
    public static void UpdateDB(SQLiteDatabase db,Get get){
        int length=get.Data.length();
        UserInfo User;
        for (int i=0;i<length;i++) {
            try {
                User = (UserInfo) getObject(new UserInfo(),get.Data.getJSONObject(i));
                Cursor cr=db.query("userdata",new String[]{"id"},"id=?",new String[]{User.Id},null,null,null);
                if(cr!=null&&cr.getCount()>0&&cr.moveToNext()) {
                    db.update("userdata", Data.getUserContentValues(User.Id, User.Nick, User.Auto, User.Sex, User.Birth, User.Pnumber, User.Startdate, User.Catdate, User.Typeface, User.Theme, User.Bubble,User.Iknow,User.Knowme), "id=?", new String[]{User.Id});
                }else{
                    db.insert("userdata", null,Data.getUserContentValues(User.Id, User.Nick, User.Auto, User.Sex, User.Birth, User.Pnumber, User.Startdate, User.Catdate, User.Typeface, User.Theme, User.Bubble,User.Iknow,User.Knowme));
                }
            } catch (Exception e) {
                Log.i("UpdateDB", e.toString());
            }

        }
    }
    public static ArrayList<BaseItem> AddList(Context context,String id,ArrayList<BaseItem> mArrays,Refresh refresh){
        int length=refresh.Data.length();
        for (int i=0;i<length;i++) {
            JSONObject jsonobj;
            SimpleMsg Msg=new SimpleMsg();
            try {
                jsonobj = refresh.Data.getJSONObject(i);
                Msg=(SimpleMsg)getObject(new SimpleMsg(),jsonobj);
            }catch (Exception e){
                Log.i("Array",e.toString());
            }
            int typeTemp;
            if(Msg.Guestid.equals(id)){
                typeTemp=0;
            }else if(Msg.Guestid.equals("10000")){
                typeTemp=2;
            }else {
                typeTemp=1;
            }
            mArrays.add(0,new ItemSMsg(typeTemp,Msg.Guestid,context.getResources().getDrawable(R.drawable.tianqing),Msg.SendType,Msg.Bubble,Msg.Msg,Msg.MsgCode,Msg.Date,Msg.Time,1));
        }
        return mArrays;
    }
    private static Object getObject(Object obj,JSONObject jsonobj){
        Field[] fields=obj.getClass().getDeclaredFields();
        for (Field field : fields) {
            try {
                field.setAccessible(true);
                field.set(obj, jsonobj.get(field.getName()));//set(Object obj, Object value):向obj对象的这个Field设置新值value
            } catch (Exception e) {
                Log.i("Jsonobj", e.toString());
            }
        }
        return obj;
    }
}

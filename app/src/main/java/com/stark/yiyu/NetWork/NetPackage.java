package com.stark.yiyu.NetWork;

import android.util.Log;

import com.stark.yiyu.Format.Ack;
import com.stark.yiyu.Format.CmdType;
import com.stark.yiyu.Format.Format;
import com.stark.yiyu.Format.Get;
import com.stark.yiyu.Format.Msg;
import com.stark.yiyu.Format.Refresh;
import com.stark.yiyu.Format.Registion;
import com.stark.yiyu.Format.TransFile;
import com.stark.yiyu.json.JsonConvert;

import org.json.JSONArray;

/**
 * Created by Stark on 2017/2/8.
 */
public class NetPackage {

    public static String Login(String id,String password,String uuid,int type)
    {
        Registion login=new Registion();
        Format format=new Format();
        login.Id=id;
        login.PassWord=password;
        login.Nick=uuid;
        login.Type=type;
        String JsonStr=null;
        try {
            JsonStr= JsonConvert.SerializeObject(login);
            format.Type = "Registion";
            format.Cmd = "login";
            format.JsonMsg = JsonStr;
            JsonStr = JsonConvert.SerializeObject(format);
        }catch (Exception e)
        {
            Log.i("Login",e.toString());
        }
        Log.i("Login",JsonStr);
        return JsonStr;
    }
    public static String Register(String nick,String password)
    {
        Registion register = new Registion();
        Format format=new Format();
        register.Nick =nick;
        register.PassWord =password;
        String JsonStr=null;
        try {
            JsonStr= JsonConvert.SerializeObject(register);/**序列化：封装成json格式*/
            format.Type = "Registion";
            format.Cmd = "register";
            format.JsonMsg = JsonStr;
            JsonStr = JsonConvert.SerializeObject(format);
        }catch (Exception e)
        {
            Log.i("Register",e.toString());
        }
        Log.i("Register",JsonStr);
        return JsonStr;
    }
    public static String SendMsg(String SrcId,String DesId,String msg,String msgCode){
        Msg message=new Msg();
        Format format=new Format();
        message.SrcId=SrcId;
        message.DesId=DesId;
        message.Msg=msg;
        message.SendType=1;
        message.MsgCode=msgCode;
        String JsonStr=null;
        try {
            JsonStr= JsonConvert.SerializeObject(message);
            format.Type = "Message";
            format.Cmd = "csend";
            format.JsonMsg = JsonStr;
            JsonStr = JsonConvert.SerializeObject(format);
        }catch (Exception e)
        {
            Log.i("Send",e.toString());
        }
        Log.i("Send",JsonStr);
        return JsonStr;
    }
    public static String SendGMsg(String SrcId,String DesId,String msg,String msgCode){
        Msg message=new Msg();
        Format format=new Format();
        message.SrcId=SrcId;
        message.DesId=DesId;
        message.Msg=msg;
        message.SendType=1;
        message.MsgCode=msgCode;
        String JsonStr=null;
        try {
            JsonStr= JsonConvert.SerializeObject(message);
            format.Type = "Message";
            format.Cmd = "gsend";
            format.JsonMsg = JsonStr;
            JsonStr = JsonConvert.SerializeObject(format);
        }catch (Exception e)
        {
            Log.i("Send",e.toString());
        }
        Log.i("Send",JsonStr);
        return JsonStr;
    }
    public static String Heart(String str){
        try {
            Format format = new Format();
            format.Type = "Command";
            format.Cmd = "heart";
            format.JsonMsg = str;
            return JsonConvert.SerializeObject(format);
        }catch (Exception e){
            Log.i("Heart",e.toString());
        }
        return null;
    }
    public static String Refresh(String SrcId,String DesId,int start,int type,JSONArray data){
        Refresh refresh=new Refresh();
        refresh.SrcId=SrcId;
        refresh.DesId=DesId;
        refresh.Mode=type;
        refresh.Start=start;
        refresh.MsgCode=data;
        String JsonStr;
        try {
            JsonStr=JsonConvert.SerializeObject(refresh);
            Format format = new Format();
            format.Type = "Refresh";
            format.Cmd = "sRefresh";
            format.JsonMsg = JsonStr;
            Log.i("Refresh",JsonStr);
            return JsonConvert.SerializeObject(format);
        }catch (Exception e){
            Log.i("Refresh",e.toString());
        }
        return null;
    }
    public static String Friend(String SrcId,String DesId,String Remarks,int dynamic){
        Msg msg=new Msg();
        msg.SrcId=SrcId;
        msg.DesId=DesId;
        msg.Remarks=Remarks;
        msg.SendType=dynamic;
        String JsonStr;
        try{
            JsonStr=JsonConvert.SerializeObject(msg);
            Format format=new Format();
            format.Type="Message";
            format.Cmd="friend";
            format.JsonMsg=JsonStr;
            Log.i("Friend",JsonStr);
            return JsonConvert.SerializeObject(format);
        }catch(Exception e){
            Log.i("Friend",e.toString());
        }
        return null;
    }
    public static String SendFile(String id,String src, long size, String name, String hashcode) {
        TransFile transFile = new TransFile();
        transFile.SrcId=id;
        transFile.Src = src;
        transFile.Size = size;
        transFile.Name = name;
        transFile.HashCode = hashcode;
        transFile.Mode = "check";
        return JsonConvert.SerializeObject(transFile);
    }
    public static String Get(String SrcId,int Type,JSONArray data){
        Get get=new Get();
        get.Guestid=SrcId;
        get.Type=Type;
        get.Data=data;
        String JsonStr;
        try{
            JsonStr=JsonConvert.SerializeObject(get);
            Format format=new Format();
            format.Type="Get";
            format.Cmd="userinfo";
            format.JsonMsg=JsonStr;
            Log.i("Get",JsonStr);
            return JsonConvert.SerializeObject(format);
        }catch (Exception e){
            Log.i("Get",e.toString());
        }
        return null;
    }
    public static String CmdModify(String JsonStr,String cmd){
        TransFile temp=(TransFile)JsonConvert.DeserializeObject(JsonStr,new TransFile());
        temp.Mode=cmd;
        return JsonConvert.SerializeObject(temp);
    }
    public static Object getBag(String str){
        return getBag(getFormatBag(str));
    }
    public static Format getFormatBag(String str){
        Log.i("getFirstBag",str);
        if(str!=null&&!str.equals("")) {
            Format format = (Format) JsonConvert.DeserializeObject(str, new Format());
            return format;
        }else {
            return null;
        }
    }
    public static Object getBag(Format format){
        if(format!=null) {
            return getBag(format.JsonMsg, format.Type);
        }else {
            return null;
        }
    }
    public static Object getBag(String str,String cmd){
        Log.i("jsonStr",str);
        if(str!=null&&cmd!=null&&!str.equals("")&&!cmd.equals("")){
            switch (CmdType.valueOf(cmd)) {
                case Ack:
                    return JsonConvert.DeserializeObject(str, new Ack());
                case Message:
                    return JsonConvert.DeserializeObject(str, new Msg());
                case Refresh:
                    return JsonConvert.DeserializeObject(str, new Refresh());
                case Get:
                    return JsonConvert.DeserializeObject(str,new Get());
                default:
                    Log.i("getbag","cmd is null");
                    return null;
            }
        }else{
            return null;
        }
    }
}

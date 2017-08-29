package com.stark.wallwallchat.NetWork;

import android.util.Log;

import com.stark.wallwallchat.Format.Ack;
import com.stark.wallwallchat.Format.CmdType;
import com.stark.wallwallchat.Format.Format;
import com.stark.wallwallchat.Format.Get;
import com.stark.wallwallchat.Format.Interact;
import com.stark.wallwallchat.Format.Msg;
import com.stark.wallwallchat.Format.Refresh;
import com.stark.wallwallchat.Format.Registion;
import com.stark.wallwallchat.Format.TransFile;
import com.stark.wallwallchat.Format.UserInfo;
import com.stark.wallwallchat.Format.WallMsgSend;
import com.stark.wallwallchat.json.JsonConvert;

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
            format.Type = "Registion";
            format.Cmd = "login";
            format.JsonMsg = JsonConvert.SerializeObject(login);
            JsonStr = JsonConvert.SerializeObject(format);
        }catch (Exception e)
        {
            Log.i("Login",e.toString());
        }
        Log.i("Login",JsonStr);
        return JsonStr;
    }
    public static String Register(String pNumber,String nick,String password)
    {
        Registion register = new Registion();
        Format format=new Format();
        register.Id=pNumber;
        register.Nick =nick;
        register.PassWord =password;
        String JsonStr=null;
        try {
            format.Type = "Registion";
            format.Cmd = "register";
            format.JsonMsg = JsonConvert.SerializeObject(register);/**序列化：封装成json格式*/
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
            format.Type = "Message";
            format.Cmd = "csend";
            format.JsonMsg = JsonConvert.SerializeObject(message);
            JsonStr = JsonConvert.SerializeObject(format);
        }catch (Exception e)
        {
            Log.i("Send",e.toString());
        }
        Log.i("Send", JsonStr);
        return JsonStr;
    }
    public static String Change(UserInfo userInfo){
        Format format=new Format();
        String JsonStr=null;
        try {
            format.Type="Change";
            format.Cmd="changeUser";
            format.JsonMsg=JsonConvert.SerializeObject(userInfo);
            JsonStr=JsonConvert.SerializeObject(format);
        }catch (Exception e){
            Log.i("Change",e.toString());
        }
        Log.i("Change",JsonStr);
        return JsonStr;
    }
    public static String Comment(String sponsor,String receiver,String msgcode,String msgcode2,String msg,String edu,int mode,int type){
        Interact interact=new Interact();
        Format format=new Format();
        interact.Sponsor=sponsor;
        interact.Receiver=receiver;
        interact.MsgCode=msgcode;
        interact.MsgCode2=msgcode2;
        interact.Msg=msg;
        interact.Edu=edu;
        interact.Mode=mode;
        interact.Type=type;
        String JsonStr=null;
        try {
            format.Type = "Interact";
            format.Cmd = "comment";
            format.JsonMsg = JsonConvert.SerializeObject(interact);
            JsonStr = JsonConvert.SerializeObject(format);
        }catch (Exception e)
        {
            Log.i("Interact",e.toString());
        }
        Log.i("Interact",JsonStr);
        return JsonStr;
    }
    public static String Agree(String sponsor,String receiver,String msgcode,String edu,int mode,int type) {
        Interact interact=new Interact();
        Format format=new Format();
        interact.Sponsor=sponsor;
        interact.Receiver=receiver;
        interact.MsgCode=msgcode;
        interact.Edu=edu;
        interact.Mode=mode;
        interact.Type=type;
        String JsonStr=null;
        try {
            format.Type = "Interact";
            format.Cmd = "agree";
            format.JsonMsg = JsonConvert.SerializeObject(interact);
            JsonStr = JsonConvert.SerializeObject(format);
        }catch (Exception e)
        {
            Log.i("Interact",e.toString());
        }
        Log.i("Interact",JsonStr);
        return JsonStr;
    }
    public static String SendWMsg(String SrcId,String Edu,String msg,String msgCode,int mode,int type){
        WallMsgSend wallmsg=new WallMsgSend();
        Format format=new Format();
        wallmsg.Sponsor=SrcId;
        wallmsg.Edu=Edu;
        wallmsg.Msg=msg;
        wallmsg.MsgCode=msgCode;
        wallmsg.Mode=mode;
        wallmsg.Type=type;
        String JsonStr=null;
        try {
            format.Type = "WallMsg";
            format.Cmd = "wsend";
            format.JsonMsg = JsonConvert.SerializeObject(wallmsg);
            JsonStr = JsonConvert.SerializeObject(format);
        }catch (Exception e)
        {
            Log.i("WSend",e.toString());
        }
        Log.i("WSend",JsonStr);
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
            format.Type = "Message";
            format.Cmd = "gsend";
            format.JsonMsg = JsonConvert.SerializeObject(message);
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
    public static String Refresh(String SrcId,String DesId,int start,int refreshMode,int cmd,String msg,int sortMode){
        Refresh refresh=new Refresh();
        refresh.SrcId=SrcId;
        refresh.DesId=DesId;
        refresh.Mode=refreshMode;
        switch (sortMode){
            case 0:
                refresh.Sort="msgcode ASC";
            break;
            case 1:
                refresh.Sort="msgcode DESC";
                break;
            case 2:
                refresh.Sort="anum ASC";
                break;
            case 3:
                refresh.Sort="anum DESC";
                break;
            case 4:
                refresh.Sort="cnum ASC";
                break;
            case 5:
                refresh.Sort="cnum DESC";
                break;
        }
        refresh.Start=start;
        refresh.Msg=msg;
        String JsonStr=null;
        try {
            Format format = new Format();
            format.Type = "Refresh";
            format.Cmd = (cmd==0?"sRefresh":"wRefresh");
            format.JsonMsg = JsonConvert.SerializeObject(refresh);
            JsonStr=JsonConvert.SerializeObject(format);
        }catch (Exception e){
            Log.i("Refresh",e.toString());
        }
        Log.i("Refresh",JsonStr);
        return JsonStr;
    }
    public static String Friend(String SrcId,String DesId,String Remarks,int dynamic){
        Msg msg=new Msg();
        msg.SrcId=SrcId;
        msg.DesId=DesId;
        msg.Remarks=Remarks;
        msg.SendType=dynamic;
        String JsonStr=null;
        try{
            Format format=new Format();
            format.Type="Message";
            format.Cmd="friend";
            format.JsonMsg=JsonConvert.SerializeObject(msg);
            JsonStr= JsonConvert.SerializeObject(format);
        }catch(Exception e){
            Log.i("Friend", e.toString());
        }
        Log.i("Friend",JsonStr);
        return JsonStr;
    }
    public static String SendFile(String hashcode,String name,long size,boolean longtime ) {
        TransFile transFile = new TransFile();
        transFile.Size = size;
        transFile.HashCode = hashcode;
        transFile.Name=name;
        transFile.IsLong=longtime;
        String JsonStr=null;
        try{
            Format format=new Format();
            format.Type="File";
            format.Cmd="up";
            format.JsonMsg=JsonConvert.SerializeObject(transFile);
            JsonStr= JsonConvert.SerializeObject(format);
        }catch (Exception e) {
            Log.e("SendFile", "" + e);
        }
        Log.e("GetFile", JsonStr);
        return JsonStr;
    }
    public static String getFile(String hashcode){
        TransFile transFile = new TransFile();
        transFile.HashCode = hashcode;
        String JsonStr=null;
        try{
            Format format=new Format();
            format.Type="File";
            format.Cmd="down";
            format.JsonMsg=JsonConvert.SerializeObject(transFile);
            JsonStr= JsonConvert.SerializeObject(format);
        }catch (Exception e) {
            Log.e("SendFile", "" + e);
        }
        Log.e("SendFile", JsonStr);
        return JsonStr;
    }
    public static String Get(String SrcId,int Type,JSONArray data){
        Get get=new Get();
        get.Guestid=SrcId;
        get.Type=Type;
        get.Data=data;
        String JsonStr=null;
        try{
            Format format=new Format();
            format.Type="Get";
            format.Cmd="userinfo";
            format.JsonMsg=JsonConvert.SerializeObject(get);
            JsonStr= JsonConvert.SerializeObject(format);
        }catch (Exception e){
            Log.i("Get",e.toString());
        }
        Log.i("Get",JsonStr);
        return JsonStr;
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

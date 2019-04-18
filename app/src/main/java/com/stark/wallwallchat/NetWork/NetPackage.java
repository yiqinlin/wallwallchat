package com.stark.wallwallchat.NetWork;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.stark.wallwallchat.Format.Ack;
import com.stark.wallwallchat.Format.CmdType;
import com.stark.wallwallchat.Format.Format;
import com.stark.wallwallchat.Format.Get;
import com.stark.wallwallchat.Format.Msg;
import com.stark.wallwallchat.Format.Refresh;
import com.stark.wallwallchat.json.JsonConvert;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

/**
 * Created by Stark on 2017/9/4.
 */
public class NetPackage {
    private SharedPreferences sp;
    private HashMap<String,Object> SqlPkg;

    public NetPackage(Context context,HashMap<String,Object> SqlPkg){
        this.SqlPkg=SqlPkg;
        sp=context.getSharedPreferences("action",Context.MODE_PRIVATE);
    }
    public NetPackage(Context context){
        this.SqlPkg=new HashMap<String, Object>();
        sp=context.getSharedPreferences("action",Context.MODE_PRIVATE);
    }
    public NetPackage(){
        this.SqlPkg=new HashMap<String, Object>();
    }
    public NetPackage(HashMap<String,Object> SqlPkg){
        this.SqlPkg=SqlPkg;
    }



//        SqlPkg.put("nick","");
//        SqlPkg.put("password","");
//        SqlPkg.put("sex","");
//        SqlPkg.put("college","");
//        SqlPkg.put("edu","");
//        SqlPkg.put("pnumber","");

    public NetBuilder Register()throws NullPointerException{
        check();
        NetBuilder data=new NetBuilder();
        data.Config.put("Type", "ControlMsg");
        data.Config.put("Cmd", "register");
        data.Package=SqlPkg;
        return data;
    }

    public NetBuilder Heart()throws NullPointerException{
        NetBuilder data=new NetBuilder();
        data.Config.put("id",sp.getString("id", null));
        data.Config.put("Type", "ControlMsg");
        data.Config.put("Cmd", "heart");
        return data;
    }
    //    SqlPkg.put("user","");
    //    SqlPkg.put("password","");
    public NetBuilder Login(boolean IsAuto)throws NullPointerException{
        NetBuilder data=new NetBuilder();
        if(IsAuto) {
            String id=sp.getString("id", null);
            String password=sp.getString("password",null);
            if(id==null||id.equals("")||password==null||password.equals("")){
                throw new NullPointerException("login information expired,please log in again !");
            }
            data.Config.put("user",id);
            data.Config.put("password",password);
            data.Config.put("type",0);//0自动登录 1是手动登录
        } else {
            check();
            data.Config.put("type",1);
        }
        data.Config.put("Type", "ControlMsg");
        data.Config.put("Cmd", "login");
        String uuid=sp.getString("uuid",null);
        if(uuid==null||uuid.equals("")){
            uuid=new SimpleDateFormat("yyyyMMddHHmmssSSS", Locale.CHINA).format(new Date());
            sp.edit().putString("uuid",uuid).apply();
        }
        data.Config.put("uuid",uuid);
        data.Package=SqlPkg;
        return data;
    }

    //    SqlPkg.put("user","");
    //    SqlPkg.put("nick","");
    //    SqlPkg.put("password","");
    //    SqlPkg.put("auto","");
    //    SqlPkg.put("sex","");
    //    SqlPkg.put("birth","");
    //    SqlPkg.put("college","");
    //    SqlPkg.put("edu","");
    //    SqlPkg.put("mail","");
    //    SqlPkg.put("pnumber","");
    //    SqlPkg.put("catdate","");
    //    SqlPkg.put("typeface","");
    //    SqlPkg.put("bubble","");
    //    SqlPkg.put("theme","");

    public NetBuilder ChangeUser()throws NullPointerException{
        String id=sp.getString("id", null);
        if(id==null||id.equals("")){
            throw new NullPointerException("login information expired,please log in again !");
        }
        check();
        NetBuilder data=new NetBuilder();
        data.Config.put("Type", "ControlMsg");
        data.Config.put("Cmd", "changeUser");
        data.Config.put("id",id);
        data.Package=SqlPkg;
        return data;
    }
    public NetBuilder ChangeRead()throws NullPointerException{
        String id=sp.getString("id", null);
        if(id==null||id.equals("")){
            throw new NullPointerException("login information expired,please log in again !");
        }
        check();
        NetBuilder data=new NetBuilder();
        data.Config.put("Type", "ControlMsg");
        data.Config.put("Cmd", "changeMsgRead");
        data.Config.put("sponsor",id);
        data.Package=SqlPkg;
        return data;
    }


    //    SqlPkg.put("msg","");
    //    SqlPkg.put("mode",);代表墙消息的种类
    //    SqlPkg.put("type",);0代表不匿名，1代表匿名
    public NetBuilder WSend()throws NullPointerException{
        String id=sp.getString("id", "10001");
        String edu=sp.getString("edu", "nsu");
        if(id==null||id.equals("")||edu==null||edu.equals("")){
            throw new NullPointerException("login information expired,please log in again !");
        }
        check();
        NetBuilder data=new NetBuilder();
        data.Config.put("Type", "ControlMsg");
        data.Config.put("Cmd", "wsend");
        SqlPkg.put("sponsor", id);
        SqlPkg.put("edu", edu);
        SqlPkg.put("msgcode2",new SimpleDateFormat("yyyyMMddHHmmssSSS", Locale.CHINA).format(new Date()));
        data.Package=SqlPkg;
        return data;
    }

//      SqlPkg.put("msg","");
//      SqlPkg.put("msgcode3","");
//      SqlPkg.put("receiver","");
//      SqlPkg.put("type",0);       //0是墙，1是评论，2是回复
//      SqlPkg.put("mode",0);     //0非匿名，1匿名
    public NetBuilder Comment()throws NullPointerException{
        String id=sp.getString("id", "10001");
        String edu=sp.getString("edu", "nsu");
        if(id==null||id.equals("")||edu==null||edu.equals("")){
            throw new NullPointerException("login information expired,please log in again !");
        }
        check();
        NetBuilder data=new NetBuilder();
        data.Config.put("Type", "ControlMsg");
        data.Config.put("Cmd", "comment");
        data.Config.put("edu", edu);
        SqlPkg.put("sponsor", id);
        SqlPkg.put("msgcode2",new SimpleDateFormat("yyyyMMddHHmmssSSS", Locale.CHINA).format(new Date()));
        data.Package=SqlPkg;
        return data;
    }

    //    SqlPkg.put("msgcode","");
//    SqlPkg.put("receiver","");
    public NetBuilder Agree(int mode)throws NullPointerException{
        String id=sp.getString("id", "10001");
        String edu=sp.getString("edu", "nsu");
        if(id==null||id.equals("")||edu==null||edu.equals("")){
            throw new NullPointerException("login information expired,please log in again !");
        }
        check();
        NetBuilder data=new NetBuilder();
        data.Config.put("Type", "ControlMsg");
        data.Config.put("Cmd", "agree");
        data.Config.put("edu", edu);
        data.Config.put("mode",mode);          //0点赞,1取消
        SqlPkg.put("sponsor", id);
        data.Package=SqlPkg;
        return data;
    }

    //  SqlPkg.put("receiver","");
    //  SqlPkg.put("msg","");
    public NetBuilder Csend(int type)throws NullPointerException{
        String id=sp.getString("id", "10007");
        if(id==null||id.equals("")){
            throw new NullPointerException("login information expired,please log in again !");
        }
        check();
        NetBuilder data=new NetBuilder();
        data.Config.put("Type", "ControlMsg");
        data.Config.put("Cmd", "csend");
        SqlPkg.put("msgcode2", new SimpleDateFormat("yyyyMMddHHmmssSSS", Locale.CHINA).format(new Date()));
        SqlPkg.put("sponsor", id);
        SqlPkg.put("sendtype", type);
        data.Package=SqlPkg;
        return data;
    }


    //    SqlPkg.put("receiver","");
//    SqlPkg.put("remarks","");
    public NetBuilder Friend(int mode)throws NullPointerException{
        String id=sp.getString("id", "10007");
        if(id==null||id.equals("")){
            throw new NullPointerException("login information expired,please log in again !");
        }
        check();
        NetBuilder data=new NetBuilder();
        data.Config.put("Type", "ControlMsg");
        data.Config.put("Cmd", "friend");
        data.Config.put("mode",mode);          //0关注,1取消关注
        data.Config.put("msgcode",new SimpleDateFormat("yyyyMMddHHmmssSSS", Locale.CHINA).format(new Date()));
        SqlPkg.put("sponsor", id);
        data.Package=SqlPkg;
        return data;
    }


    //    SqlPkg.put("receiver","10006");
    public NetBuilder UserInfo(int mode)throws NullPointerException{
        check();
        NetBuilder data=new NetBuilder();
        data.Config.put("Type", "ControlMsg");
        data.Config.put("Cmd", "userinfo");
        data.Config.put("mode",mode);      //0是获取个人基本信息，1是获取自己关注的人，2是获取关注自己的人
        data.Package=SqlPkg;
        return data;
    }

//    SqlPkg.put("receiver","");
//    SqlPkg.put("start",);

    public NetBuilder SRefresh()throws NullPointerException{
        String id=sp.getString("id", "10006");
        if(id==null||id.equals("")){
            throw new NullPointerException("login information expired,please log in again !");
        }
        check();
        NetBuilder data=new NetBuilder();
        data.Config.put("Type", "ControlMsg");
        data.Config.put("Cmd", "sRefresh");
        data.Config.put("sort","msgcode DESC");
        SqlPkg.put("sponsor", id);
        data.Package=SqlPkg;
        return data;
    }

    //    SqlPkg.put("mode",);
//    SqlPkg.put("start",);
//    SqlPkg.put("msgcode",);//指定消息
    public NetBuilder WRefresh(int sort)throws NullPointerException{
        String id=sp.getString("id", "10006");
        String edu=sp.getString("edu", "nsu");
        if(id==null||id.equals("")||edu==null||edu.equals("")){
            throw new NullPointerException("login information expired,please log in again !");
        }
        check();
        NetBuilder data=new NetBuilder();
        data.Config.put("Type", "ControlMsg");
        data.Config.put("Cmd", "wRefresh");
        data.Config.put("edu", edu);
        String Sort=null;
        switch (sort){
            case 0:
                Sort="msgcode DESC";
                break;
            case 1:
                Sort="msgcode ASC";
                break;
            case 2:
                Sort="anum ASC";
                break;
            case 3:
                Sort="anum DESC";
                break;
            case 4:
                Sort="cnum ASC";
                break;
            case 5:
                Sort="cnum DESC";
                break;
            case 6:
                Sort="(anum*5+cnum) desc,msgcode desc";
                break;
        }
        data.Config.put("sort",Sort);
        SqlPkg.put("sponsor", id);
        data.Package=SqlPkg;
        return data;
    }
    public NetBuilder MRefresh()throws NullPointerException{
        String id=sp.getString("id", "10006");
        if(id==null||id.equals("")){
            throw new NullPointerException("login information expired,please log in again !");
        }
        check();
        NetBuilder data=new NetBuilder();
        data.Config.put("Type", "ControlMsg");
        data.Config.put("Cmd", "mRefresh");
        SqlPkg.put("sponsor", id);
        data.Package=SqlPkg;
        return data;
    }
    public NetBuilder UpFile()throws NullPointerException{
        check();
        NetBuilder data=new NetBuilder();
        data.Config.put("Type", "ControlMsg");
        data.Config.put("Cmd", "upFile");
        data.Package=SqlPkg;
        return data;
    }
    public NetBuilder DownFile()throws NullPointerException{
        check();
        NetBuilder data=new NetBuilder();
        data.Config.put("Type", "ControlMsg");
        data.Config.put("Cmd", "downFile");
        data.Package=SqlPkg;
        return data;

    }public NetBuilder Ack(String cmd,boolean ack)throws NullPointerException{
        check();
        NetBuilder data=new NetBuilder();
        data.Config.put("Type", "ControlMsg");
        data.Config.put("Cmd", cmd);
        data.Package=SqlPkg;
        data.Package.put("flag",ack);
        return data;
    }
    private void check()throws NullPointerException{
        if(SqlPkg==null){
            throw new NullPointerException("SqlPkg must be init !");
        }
    }



    public static Object getBag(String str)throws Exception{
        return getBag(getFormatBag(str));
    }
    public static Format getFormatBag(String str)throws Exception {
        Log.i("getFirstBag",str);
        if(str!=null&&!str.equals("")) {
            Format format = (Format) JsonConvert.DeserializeObject(str, new Format());
            return format;
        }else {
            return null;
        }
    }
    public static Object getBag(Format format)throws Exception{
        if(format!=null) {
            return getBag(format.JsonMsg, format.Type);
        }else {
            return null;
        }
    }
    public static Object getBag(String str,String cmd)throws Exception{
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

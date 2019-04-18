package com.stark.wallwallchat.bean;

import android.database.Cursor;

import com.stark.wallwallchat.Util.DateUtil;

import java.util.HashMap;

/**
 * Created by Stark on 2017/7/11.
 */
public class ItemWallInfo extends BaseItem{
    private String id;
    private String id2;
    private boolean isAgree=false;
    private String msgCode;
    private String msgCode2;
    private int type;
    private String nick;
    private String nick2;
    private String time;
    private String content;
    private String cnum;
    private String anum;
    public ItemWallInfo(int itemType,HashMap msg){
        super(itemType);
        this.type=Integer.parseInt((String)msg.get("type"));
        this.id=(String)msg.get("sponsor");
        this.nick=(String)msg.get("remarks");
        this.msgCode=(String)msg.get("msgcode");
        this.time= DateUtil.MtoNT(msgCode);
        this.content=(String)msg.get("msg");
        this.cnum=(String)msg.get("cnum");
        this.anum =(String)msg.get("anum");
        this.isAgree=Boolean.parseBoolean((String)msg.get("isagree"));
    }
    public ItemWallInfo(int itemType,Cursor msg){
        super(itemType);
        this.type= msg.getInt(msg.getColumnIndex("type"));
        this.id=msg.getString(msg.getColumnIndex("sponsor"));
        this.nick=msg.getString(msg.getColumnIndex("remarks"));
        this.msgCode=msg.getString(msg.getColumnIndex("msgcode"));
        this.time= DateUtil.MtoNT(msgCode);
        this.content=msg.getString(msg.getColumnIndex("msg"));
        this.cnum=msg.getString(msg.getColumnIndex("cnum"));
        this.anum =msg.getString(msg.getColumnIndex("anum"));
        this.isAgree = Boolean.parseBoolean(msg.getString(msg.getColumnIndex("isagree")));
    }
    public String getId(){
        return this.id;
    }
    public String getId2(){
        return this.id2;
    }
    public void setAgree(boolean isAgree){
        this.isAgree=isAgree;
    }
    public void setAnum(int anum){
        this.anum=anum+"";
    }
    public boolean IsAgree(){return this.isAgree;}
    public String getMsgcode(){return this.msgCode;}
    public String getMsgcode2(){return this.msgCode2;}
    public int getType(){
        return this.type;
    }
    public String getNick(){
        return this.nick;
    }
    public String getNick2(){return this.nick2;}
    public String getTime(){
        return this.time;
    }
    public String getContent(){
        return this.content;
    }
    public String getCnum(){
        return this.cnum;
    }
    public String getAnum(){
        return this.anum;
    }
}

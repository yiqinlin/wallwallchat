package com.stark.wallwallchat.bean;

import android.database.Cursor;

import com.stark.wallwallchat.Util.DateUtil;

import java.util.HashMap;

/**
 * Created by Stark on 2017/3/7.
 */
public class ItemSMsg extends BaseItem{
    public String Guestid;
    public int SendType = 0;
    public int Bubble=0;
    public String Msg;
    public String MsgCode;
    public String MsgCode2;
    public String Date;
    public String Time;
    public int State=1;
    public ItemSMsg(int itemType,HashMap msg){
        super(itemType);

        this.Guestid=msg.get("sponsor").toString();
        this.SendType=Integer.parseInt(msg.get("sendtype").toString());
        this.Msg=msg.get("msg").toString();
        this.MsgCode2=msg.get("msgcode2").toString();
        this.Date= DateUtil.Mtoy(MsgCode2);
        this.Time=DateUtil.Mtodt(MsgCode2);
        if(msg.get("ack")!=null) {
            this.State = Integer.parseInt(msg.get("ack").toString());
        }
    }
    public ItemSMsg(int itemType,Cursor msg,int state){
        super(itemType);
        this.Guestid=msg.getString(msg.getColumnIndex("sponsor"));
        this.SendType=msg.getInt(msg.getColumnIndex("sendtype"));
        this.Bubble=msg.getInt(msg.getColumnIndex("bubble"));
        this.Msg=msg.getString(msg.getColumnIndex("msg"));
        this.MsgCode=msg.getString(msg.getColumnIndex("msgcode"));
        this.MsgCode2=msg.getString(msg.getColumnIndex("msgcode2"));
        this.Date= DateUtil.Mtoy(MsgCode);
        this.Time=DateUtil.Mtodt(MsgCode);
        this.State=state;
    }
    public String getId(){
        return this.Guestid;
    }

    public int getSendType(){
        return this.SendType;
    }

    public int getBubble() {
        return this.Bubble;
    }

    public String getMsg() {
        return this.Msg;
    }

    public String getMsgCode() {
        return this.MsgCode;
    }

    public String getMsgCode2() {
        return this.MsgCode2;
    }

    public String getDate(){
        return this.Date;
    }

    public String getTime(){
        return this.Time;
    }

    public int getState(){
        return this.State;
    }
}

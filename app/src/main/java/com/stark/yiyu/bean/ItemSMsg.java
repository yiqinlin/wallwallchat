package com.stark.yiyu.bean;

import android.graphics.drawable.Drawable;

/**
 * Created by Stark on 2017/3/7.
 */
public class ItemSMsg extends BaseItem{
    public String Guestid;
    public Drawable Head;
    public int SendType = 0;
    public int Bubble=0;
    public String Msg;
    public String MsgCode;
    public String Date;
    public String Time;
    public int State=1;
    public ItemSMsg(int itemType,String guestid,Drawable head,int sendtype,int bubble,String msg,String msgCode,String date,String time,int state){
        super(itemType);
        this.Head=head;
        this.Guestid=guestid;
        this.SendType=sendtype;
        this.Bubble=bubble;
        this.Msg=msg;
        this.MsgCode=msgCode;
        this.Date=date;
        this.Time=time;
        this.State=state;
    }
    public Drawable getHead(){
        return this.Head;
    }

    public String getid(){
        return this.Guestid;
    }

    public int getSendType(){
        return this.SendType;
    }

    public int getBubble() {
        return this.Bubble;
    }

    public String getMsg() {
        return this.Msg+"";
    }

    public String getMsgCode() {
        return this.MsgCode+"";
    }

    public String getDate(){
        return this.Date+"";
    }

    public String getTime(){
        return this.Time+"";
    }

    public int getState(){
        return this.State;
    }
}

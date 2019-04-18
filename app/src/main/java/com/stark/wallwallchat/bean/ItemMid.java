package com.stark.wallwallchat.bean;

import android.database.Cursor;

import com.stark.wallwallchat.Util.DateUtil;

import java.util.HashMap;


/**
 * Created by Stark on 2017/3/2.
 */
public class ItemMid extends BaseItem{
    private String Id;
    private String Nick;
    private String Message;
    private String Date;
    private String Count;

    public ItemMid(int itemType,Cursor msg) {
        super(itemType);
        this.Id=msg.getString(msg.getColumnIndex("sponsor"));
        this.Nick = msg.getString(msg.getColumnIndex("remarks"));
        this.Message = msg.getString(msg.getColumnIndex("msg"));
        this.Date = DateUtil.MtoST(msg.getString(msg.getColumnIndex("msgcode")));
        this.Count=msg.getString(msg.getColumnIndex("num"));
    }
    public ItemMid(int itemType,HashMap msg) {
        super(itemType);
        this.Id=(String)msg.get("id");
        this.Nick = (String)msg.get("nick");
        this.Message = (String)msg.get("auto");
    }
    public String getId(){
        return this.Id;
    }

    public String getNick() {
        return this.Nick;
    }

    public String getMsg() {
        return this.Message;
    }

    public String getDate() {
        return this.Date;
    }

    public String getCount(){
        return this.Count==null?"0":this.Count;
    }

}

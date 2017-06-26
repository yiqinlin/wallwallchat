package com.stark.yiyu.bean;

import android.graphics.drawable.Drawable;


/**
 * Created by Stark on 2017/3/2.
 */
public class ItemMid extends BaseItem{
    private String Id;
    private Drawable Head;
    private String Nick;
    private String Message;
    private String Date;
    private String Count;

    public ItemMid(int itemType,String id,Drawable head,String nick, String msg, String date,String count) {
        super(itemType);
        this.Id=id;
        this.Head=head;
        this.Nick = nick;
        this.Message = msg;
        this.Date = date;
        this.Count=count;
    }
    public String getId(){
        return this.Id;
    }

    public Drawable getHead(){
        return this.Head;
    }

    public String getNick() {
        return this.Nick+"";
    }

    public String getMsg() {
        return this.Message+"";
    }

    public String getDate() {
        return this.Date+"";
    }

    public String getCount(){
        return this.Count+"";
    }

}

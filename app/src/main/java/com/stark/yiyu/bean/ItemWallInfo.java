package com.stark.yiyu.bean;

import android.graphics.drawable.Drawable;

import com.stark.yiyu.R;

/**
 * Created by Stark on 2017/7/11.
 */
public class ItemWallInfo extends BaseItem{
    private String id;
    private String id2;
    private boolean isAgree=false;
    private String msgCode;
    private Drawable head;
    private int type;
    private String nick;
    private String nick2;
    private String time;
    private String content;
    private String cnum;
    private String anum;
    public ItemWallInfo(int itemType, int type, String id,String id2,String msgCode, Drawable head, String nick, String nick2,String time, String content, String cnum, String anum,boolean isAgree){
        super(itemType);
        this.type=type;
        this.id=id;
        this.id2=id2;
        this.msgCode=msgCode;
        this.head=head;
        this.nick=nick;
        this.nick2=nick2;
        this.time=time;
        this.content=content;
        this.cnum=cnum;
        this.anum =anum;
        this.isAgree=isAgree;
    }
    public String getId(){
        return this.id+"";
    }
    public String getId2(){
        return this.id2;
    }
    public void setAgree(boolean isAgree){
        this.isAgree=isAgree;
    }public void setAnum(int anum){
        this.anum=anum+"";
    }
    public boolean IsAgree(){return this.isAgree;}
    public String getMsgcode(){return this.msgCode+"";}
    public Drawable getHead(){
        return this.head;
    }
    public int getType(){
        switch (this.type){
            case 0:
                return R.drawable.info_ordinary_bkg;
            case 1:
                return R.drawable.info_azure_bkg;
            case 2:
                return R.drawable.info_pink_bkg;
        }
        return R.drawable.info_ordinary_bkg;
    }
    public String getNick(){
        return this.nick+"";
    }
    public String getNick2(){return this.nick2;}
    public String getTime(){
        return this.time+"";
    }
    public String getContent(){
        return this.content+"";
    }
    public String getCnum(){
        return this.cnum+"";
    }
    public String getAnum(){
        return this.anum +"";
    }
}

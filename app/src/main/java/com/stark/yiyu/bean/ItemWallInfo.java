package com.stark.yiyu.bean;

import android.graphics.drawable.Drawable;

import com.stark.yiyu.R;

/**
 * Created by Stark on 2017/7/11.
 */
public class ItemWallInfo extends BaseItem{
    private String id;
    private String msgCode;
    private Drawable head;
    private int type;
    private String nick;
    private String time;
    private String content;
    private String cnum;
    private String anum;
    public ItemWallInfo(int itemType, int type, String id,String msgCode, Drawable head, String nick, String time, String content, String cnum, String anum){
        super(itemType);
        this.type=type;
        this.id=id;
        this.msgCode=msgCode;
        this.head=head;
        this.nick=nick;
        this.time=time;
        this.content=content;
        this.cnum=cnum;
        this.anum =anum;
    }
    public String getId(){
        return this.id+"";
    }
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

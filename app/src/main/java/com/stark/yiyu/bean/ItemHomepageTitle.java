package com.stark.yiyu.bean;

import android.graphics.drawable.Drawable;

/**
 * Created by Stark on 2017/4/15.
 */
public class ItemHomepageTitle extends BaseItem{
    private String id;
    private Drawable head;
    private String nick;
    private String auto;
    public ItemHomepageTitle(int itemType,String id,Drawable head,String nick,String auto){
        super(itemType);
        this.id=id;
        this.head=head;
        this.nick=nick;
        this.auto=auto;
    }
    public String getId(){
        return this.id+"";
    }
    public Drawable getHead(){
        return this.head;
    }
    public String getNick(){
        return this.nick;
    }
    public String getAuto(){
        return this.auto;
    }
}

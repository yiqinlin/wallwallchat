package com.stark.wallwallchat.bean;

/**
 * Created by Stark on 2017/4/15.
 */
public class ItemHomepageTitle extends BaseItem{
    private String id;
    private String nick;
    private String auto;
    public ItemHomepageTitle(int itemType,String id,String nick,String auto){
        super(itemType);
        this.id=id;
        this.nick=nick;
        this.auto=auto;
    }
    public String getId(){
        return this.id;
    }
    public String getNick(){
        return this.nick;
    }
    public String getAuto(){
        return this.auto;
    }
}

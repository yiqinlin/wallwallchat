package com.stark.wallwallchat.bean;

/**
 * Created by Stark on 2017/7/15.
 */
public class ItemTextSeparate extends BaseItem {
    private String text;
    public ItemTextSeparate(int itemType,String text){
        super(itemType);
        this.text=text;
    }
    public String getText(){
        return this.text;
    }
}

package com.stark.yiyu.bean;

/**
 * Created by Stark on 2017/4/9.
 */
public class ItemKnow extends BaseItem{
    private String I;
    private String Me;
    public ItemKnow(int itemType,String i,String me){
        super(itemType);
        this.I= i;
        this.Me= me;
    }
    public String geti(){
        return this.I;
    }
    public String getme(){
        return this.Me;
    }
}

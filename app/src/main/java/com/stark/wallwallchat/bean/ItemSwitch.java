package com.stark.wallwallchat.bean;

/**
 * Created by Stark on 2017/10/10.
 */
public class ItemSwitch extends BaseItem {
    private int mode;

    private String Text;

    public ItemSwitch(int itemType,String text,int mode) {
        super(itemType);
        this.Text=text;
        this.mode=mode;
    }

    public String getText(){
        return this.Text;
    }

    public int getMode() {
        return this.mode;
    }
}

package com.stark.wallwallchat.bean;

/**
 * Created by asus on 2017/7/10.
 */
public class ItemEditMail extends BaseItem {
    private String strLeft;
    private String strRight;

    public ItemEditMail(int itemType, String strLeft, String strRight) {
        super(itemType);
        this.strLeft = strLeft;
        this.strRight = strRight;
    }

    public String getStrLeft() {
        return strLeft;
    }

    public String getStrRight() {
        return strRight;
    }
}

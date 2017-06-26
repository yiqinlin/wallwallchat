package com.stark.yiyu.bean;

import android.graphics.drawable.Drawable;

/**
 * Created by Stark on 2017/4/22.
 */
public class ItemSimpleList extends BaseItem {
    private String Text;
    private Drawable Image;

    public ItemSimpleList(int itemType,String text,Drawable image) {
        super(itemType);
        this.Text=text;
        this.Image=image;
    }

    public String getText(){
        return this.Text;
    }

    public Drawable getImage(){
        return this.Image;
    }
}

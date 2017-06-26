
package com.stark.yiyu.bean;

import android.graphics.drawable.Drawable;

/**
 * Created by Stark on 2017/4/3.
 */
public class ItemRightHead extends BaseItem{
    private String Id;
    private Drawable Head;
    private String Nick;
    private String Auto;

    public ItemRightHead(int itemType,String id,Drawable head,String nick,String auto) {
        super(itemType);
        this.Id=id;
        this.Head=head;
        this.Nick = nick;
        this.Auto=auto;
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

    public String getAuto() {
        return this.Auto+"";
    }
}

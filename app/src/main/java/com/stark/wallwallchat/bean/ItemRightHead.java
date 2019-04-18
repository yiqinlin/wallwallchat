
package com.stark.wallwallchat.bean;

/**
 * Created by Stark on 2017/4/3.
 */
public class ItemRightHead extends BaseItem{
    private String Id;
    private String Nick;
    private String Auto;

    public ItemRightHead(int itemType,String id,String nick,String auto) {
        super(itemType);
        this.Id=id;
        this.Nick = nick;
        this.Auto=auto;
    }

    public String getId(){
        return this.Id;
    }


    public String getNick() {
        return this.Nick;
    }

    public String getAuto() {
        return this.Auto;
    }
}

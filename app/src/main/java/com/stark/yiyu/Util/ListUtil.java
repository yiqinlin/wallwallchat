package com.stark.yiyu.Util;

import com.stark.yiyu.bean.BaseItem;
import com.stark.yiyu.bean.ItemSMsg;

import java.util.ArrayList;

/**
 * Created by Stark on 2017/4/21.
 */
public class ListUtil {
    public static ArrayList<BaseItem> UpadteState(ArrayList<BaseItem> mArrays,String msgCode,String newCode){
        for(int i=mArrays.size()-1;i>=0;i--){
            ItemSMsg temp=(ItemSMsg)mArrays.get(i);
            if(temp.getMsgCode()!=null&&temp.getMsgCode().equals(msgCode)){
                temp.State = 1;
                temp.MsgCode=newCode;
                temp.Date=DateUtil.Mtod(newCode);
                temp.Time=DateUtil.Mtot(newCode);
                mArrays.set(i,temp);
            }
        }
        return mArrays;
    }
}

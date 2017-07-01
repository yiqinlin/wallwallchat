package com.stark.yiyu.json;

import org.json.JSONArray;

import java.sql.Connection;
import java.util.List;
import java.util.Map;

/**
 * Created by Stark on 2017/2/8.
 */
public class TypeInt {
    public static int getType(Class<?> type)//Class<?>它是个通配泛型，?可以代表任何类型
    {
        if(type!=null&&(String.class.isAssignableFrom(type)||Character.class.isAssignableFrom(type)||Character.TYPE.isAssignableFrom(type)))
            return 0;
        if(type!=null&&(Byte.TYPE.isAssignableFrom(type)||Short.TYPE.isAssignableFrom(type)||Integer.TYPE.isAssignableFrom(type)||Number.class.isAssignableFrom(type)))
            return 1;
        if(type!=null&&Long.TYPE.isAssignableFrom(type))
            return 2;
        if(type!=null&&Float.TYPE.isAssignableFrom(type))
            return 3;
        if(type!=null&&Double.TYPE.isAssignableFrom(type))
            return 4;
        if(type!=null&&(Boolean.TYPE.isAssignableFrom(type)||Boolean.class.isAssignableFrom(type)))
            return 5;
        if(type!=null&&type.isArray())
            return 6;
        if(type!=null&&Connection.class.isAssignableFrom(type))
            return 7;
        if(type!=null&&Map.class.isAssignableFrom(type))
            return 8;
        if(type!=null&&List.class.isAssignableFrom(type))
            return 9;
        if(type!=null&&JSONArray.class.isAssignableFrom(type))
            return 10;
        return 11;
    }
}

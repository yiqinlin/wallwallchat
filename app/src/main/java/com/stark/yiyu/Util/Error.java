package com.stark.yiyu.Util;

/**
 * Created by Stark on 2017/2/13.
 */
public class Error {
    public static String error(int i){
        switch (i){
            case -1:
                return "网络异常，请检查网络";
            case 0:
                return "账号不存在！";
            case 1:
                return "密码错误！";
            case 2:
                return "验证码有误！";
            case 3:
                return "已在其他设备登录";
            case 5:
                return "服务器异常";
            case 6:
                return "数据异常,请稍后重试";
            case 101:
                return "请输入壹语账号";
            case 102:
                return "密码不能为空";
            case 103:
                return "账号不存在";
            case 104:
                return "密码格式有误";
            case 105:
                return "昵称不能为空";
            case 106:
                return "请在确认栏中再次输入密码";
            case 107:
                return "密码不能少于六位";
            case 108:
                return "两次输入的密码不一致，请注意格式重新输入";
            case 109:
                return "请勿重复添加";
            default:
                return "未知错误！";
        }
    }
}

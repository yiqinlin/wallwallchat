package com.stark.yiyu.Format;

/**
 * Created by Stark on 2017/2/11.
 */
public class Ack {
    public String SrcId;//源地址（发送者id）
    public String DesId;//目的地id
    public String BackMsg;//返回数据
    public String MsgCode;//唯一标示符
    public boolean Flag;//标志命令成功与否
    public int Error;//错误类型
}

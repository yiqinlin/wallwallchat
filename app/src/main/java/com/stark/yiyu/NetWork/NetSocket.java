package com.stark.yiyu.NetWork;

import android.util.Log;

import com.stark.yiyu.File.FileMode;
import com.stark.yiyu.File.FileUtil;
import com.stark.yiyu.Format.Ack;
import com.stark.yiyu.Format.Format;
import com.stark.yiyu.Util.Try;
import com.stark.yiyu.json.JsonConvert;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;

/**
 * Created by Stark on 2017/2/11.
 */
public class NetSocket {
    private static String IP="60.205.191.131";
    private static int PORT=12345;
    private static int FILEPORT=12344;
    public static String request(String Package)
    {
        Socket socket= Try.getSocket(IP, PORT);////第一个服务器
        if(!SocketConnect(socket)) {//没连接起
            Try.CloseSocket(socket);
            socket=Try.getSocket(IP,PORT+1);//第二个服务器
        }
        DataOutputStream bw= Try.getBW(socket);
        InputStream is=Try.getIS(socket);
        BufferedReader br= Try.getBR(is);
        send(bw, Package);
        String result=get(br);
        NetDestroy(socket, bw, br);
        return result;
    }
    public static String request(String Package,String FileSrc,int fileMode) {
        Socket socket=Try.getSocket(IP, FILEPORT);
        DataOutputStream bw = Try.getBW(socket);
        InputStream is=Try.getIS(socket);
        BufferedReader br = Try.getBR(is);
        send(bw, Package);
        String result=get(br);
        Ack ack=(Ack) NetPackage.getBag(result);
        switch (fileMode){
            case FileMode.UPLOAD:
                if(ack.Flag){
                    send(bw,new File(FileSrc));
                    result=get(br);
                }
                break;
            case FileMode.DOWNLOAD:
                if(ack.Flag){
                    FileOutputStream fos;
                    try {
                        File file=new File(FileUtil.getUsefulPath(FileSrc,ack.BackMsg,ack.MsgCode));
                        fos = new FileOutputStream(file);
                        fos.write(get(is, Integer.parseInt(ack.BackMsg)));
                        fos.close();
                        ack.BackMsg=FileSrc;
                        Format format=new Format();
                        format.Cmd="down";
                        format.Type="Ack";
                        format.JsonMsg= JsonConvert.SerializeObject(ack);
                        result=JsonConvert.SerializeObject(format);
                    }catch (Exception e){
                        Log.e("request",e.toString());
                    }
                }
                break;
        }
        NetDestroy(socket, bw, br);
        return result;
    }
    public static boolean send(DataOutputStream bw,File file){
        try {
            byte[] sendMsg=new byte[(int)file.length()];
            FileInputStream fis = new FileInputStream(file);
            fis.read(sendMsg);
            fis.close();
            bw.write(sendMsg);
            bw.flush();
        }catch (Exception e){
            Log.e("Send", "File:" + e);
        }
        return true;
    }
    public static boolean send(DataOutputStream bw,String Package)
    {
        try{
            bw.write(Package.getBytes("UTF-8"));
            bw.flush();
            return true;
        }catch (Exception e){
            Log.i("NetSocket", "W" + e.toString());
        }
        return false;
    }
    public static String get(BufferedReader br) {
        try {
            return br.readLine();
        }catch (Exception e){
            return null;
        }
    }
    public static byte[] get(InputStream is,int count){
        ByteArrayOutputStream bout = new ByteArrayOutputStream();
        byte[] buf=new byte[512];
        int a;
        try {
            while(count>0&&(a= is.read(buf))!=-1) {
                bout.write(buf, 0, a);
                count-=a;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bout.toByteArray();
    }
    public static void NetDestroy(Socket socket,DataOutputStream bw,BufferedReader br){
        Try.CloseSocket(socket);
        Try.CloseBW(bw);
        Try.CloseBR(br);
    }
    public static boolean SocketConnect(Socket socket){
        if(socket==null||socket.isClosed()||!socket.isConnected()) {
            return false;
        }
        return true;
    }
}

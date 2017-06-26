package com.stark.yiyu.NetWork;

import android.util.Log;

import com.stark.yiyu.Util.Try;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.net.Socket;

/**
 * Created by Stark on 2017/2/11.
 */
public class NetSocket {
    private static String IP="60.205.191.131";
    private static int PORT=12345;
    public static String request(String Package)
    {
        Socket socket= Try.getSocket(IP, PORT);////第一个服务器
        if(!SocketConnect(socket)) {//没连接起
            Try.CloseSocket(socket);
            socket=Try.getSocket(IP,PORT+1);//第二个服务器
        }
        DataOutputStream bw= Try.getBW(socket);
        BufferedReader br= Try.getBR(socket);
        send(bw, Package);
        String result=get(br);
        NetDestroy(socket,bw,br);
        return result;
    }
    public static boolean send(DataOutputStream bw,String Package)
    {
        try{
            bw.write(Package.getBytes("UTF-8"));
            bw.flush();
            return true;
        }catch (Exception e){
            Log.i("NetSocket","W"+e.toString());
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

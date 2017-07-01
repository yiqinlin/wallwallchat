package com.stark.yiyu.NetWork;

import android.util.Log;

import com.stark.yiyu.Format.Ack;
import com.stark.yiyu.Util.Try;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
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
        BufferedReader br= Try.getBR(socket);
        send(bw, Package);
        String result=get(br);
        NetDestroy(socket, bw, br);
        return result;
    }
    public static String request(String Package,String FileSrc) {
        Socket socket=Try.getSocket(IP, FILEPORT);
        DataOutputStream bw = Try.getBW(socket);
        BufferedReader br = Try.getBR(socket);
        send(bw, Package, null);
        String result=get(br);
        Ack ack=(Ack)NetPackage.getBag(result);
        if(ack.Flag){
            send(bw, NetPackage.CmdModify(Package, "up"), FileSrc);
            result=get(br);
            Log.e("132132",result);
        }
        return result;
    }
    public static boolean send(DataOutputStream bw,String Package,String FileSrc){
        try{
            int len=0;
            File file=null;
            if(FileSrc!=null){
                file=new File(FileSrc);
                len=new Long(file.length()).intValue();
            }
            byte[] temp=Package.getBytes("UTF-8");
            byte[] sendMsg=new byte[temp.length+2+len];
            System.arraycopy(temp, 0, sendMsg,2,temp.length);
            sendMsg[0]=(byte)((temp.length+2)/255);
            sendMsg[1]=(byte)((temp.length+2)%255);
            if(FileSrc!=null){
                try {
                    FileInputStream fis = new FileInputStream(file);
                    fis.read(sendMsg, temp.length+2,len);
                    fis.close();
                }catch (Exception e){
                    Log.e("Send","File:"+e);
                }
            }
            bw.write(sendMsg);
            bw.flush();
            return true;
        }catch (Exception e){
            Log.e("Send","File:"+e);
        }
        return false;
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

package com.stark.wallwallchat.Util;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Log;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.Socket;

/**
 * Created by Stark on 2017/2/12.
 */
public class Try {
    public static Socket getSocket(String ip,int port){
        try {
            return new Socket(ip,port);
        }catch (Exception e)
        {
            Log.i("getSocket Exception", e.toString());
            return null;
        }
    }
    public static MulticastSocket getMulticastSocket(String IP,int port){
        try {
            MulticastSocket s=new MulticastSocket(port);
            s.joinGroup(InetAddress.getByName(IP));
            return s;
        }catch (Exception e)
        {
            Log.i("Multicast Exception", e.toString());
            return null;
        }
    }
    public static void CloseSocket(Socket temp){
        try {
            temp.close();
        }catch(Exception e)
        {
            Log.i("CloseSocket Exception", e.toString());
        }
    }
    public static DataOutputStream getBW(Socket socket)
    {
        try {
            return new DataOutputStream(socket.getOutputStream());
        }catch (Exception e)
        {
            Log.i("getBR Exception", e.toString());
            return null;
        }
    }
    public static InputStream getIS(Socket socket){
        try{
            return socket.getInputStream();
        }catch (Exception e){
            Log.i("getBR Exception", e.toString());
            return null;
        }
    }
    public static Bitmap UriToBm(Context context,Uri uri){
        try{
            return BitmapFactory.decodeStream(context.getContentResolver().openInputStream(uri));
        }catch (Exception e){
            Log.e("UriToBm",e.toString());
        }
        return null;
    }
    public static BufferedReader getBR(InputStream is)
    {
        try {
            return new BufferedReader(new InputStreamReader(is,"UTF-8"));
        }catch (Exception e)
        {
            Log.i("getBR Exception", e.toString());
            return null;
        }
    }
    public static void Sleep(int time){
        try {
            Thread.sleep(time);
        }catch (Exception e)
        {
            Log.i("Sleep Exception",e.toString());
        }
    }
    public static void CloseBW(DataOutputStream bw) {
        try {
            bw.flush();
            bw.close();
        }catch (Exception e)
        {
            Log.i("CloseBW Exception",e.toString());
        }
    }
    public static void CloseBR(BufferedReader br){
        try {
            //br.close();
        }catch (Exception e)
        {
            Log.i("CloseBR Exception",e.toString());
        }
    }
    public static String getStringExtra(Intent intent,String temp){
        try{
            return intent.getStringExtra(temp);
        }
        catch (Exception e)
        {
            return null;
        }
    }
}

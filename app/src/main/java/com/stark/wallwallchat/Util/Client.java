package com.stark.wallwallchat.Util;

import android.util.Log;

import com.stark.wallwallchat.Listener.DownFileListener;
import com.stark.wallwallchat.Listener.FileListener;
import com.stark.wallwallchat.NetWork.NetPackage;
import com.stark.wallwallchat.json.JsonConvert;

import org.json.JSONException;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.RandomAccessFile;
import java.net.InetSocketAddress;
import java.net.Socket;

/**
 * Created by Stark on 2017/2/12.
 */
public class Client extends Socket{
    private DataOutputStream bw;
    private InputStream is;
    private BufferedReader br;
    public Client(){
        super();
    }
    public Client(String ip,int port)throws IOException{
        super(ip, port);
        Init();
    }
    public Client(String ip,int port,int CTimeout)throws IOException{
        super();
        this.connect(new InetSocketAddress(ip, port), CTimeout);
        Init();
    }
    public Client(String ip,int port,int CTimeout,int Timeout)throws IOException{
        super();
        this.connect(new InetSocketAddress(ip, port), CTimeout);
        this.setSoTimeout(Timeout);
        Init();
    }
    public void Init()throws IOException{
        this.bw=new DataOutputStream(this.getOutputStream());
        this.is=this.getInputStream();
        this.br=new BufferedReader(new InputStreamReader(this.is,"UTF-8"));
    }
    public void Close()throws IOException{
        super.close();
        this.close();
        this.bw.flush();
        this.bw.close();
        this.br.close();
    }
    public void send(String Package)throws IOException{
        this.bw.write(Package.getBytes("UTF-8"));
        this.bw.flush();
    }
    public String receive()throws IOException{
        return br.readLine();
    }
    public void send(RandomAccessFile file,FileListener listener)throws IOException{
        int a;
        long count,sum;
        sum=Integer.parseInt(String.valueOf(file.length()));
        count=sum-Integer.parseInt(String.valueOf(file.getFilePointer()));
        byte[] buffer=new byte[512];
        while(count>0&&(a = file.read(buffer)) >0){
            this.bw.write(buffer, 0, a);
            count-=a;
            listener.update(Integer.parseInt(String.valueOf((sum - count) * 100 / sum)));
        }
        file.close();
        this.bw.flush();
    }
    public void receive(RandomAccessFile file,long count,DownFileListener listener)throws IOException,JSONException,IllegalAccessException{
        int a;
        long sum=count;
        count-=Integer.parseInt(String.valueOf(file.getFilePointer()));
        byte[] buffer=new byte[1024];
        this.send(JsonConvert.SerializeObject(new NetPackage().Ack("downFile", true)));
        while (count > 0 && (a = is.read(buffer)) > 0) {
            file.write(buffer, 0, a);
            count -= a;
            listener.update(Integer.parseInt(String.valueOf((sum - count) * 100 / sum)));
            Log.e("sum=" + sum, count + ":" + (sum - count) * 100 / sum + "%");
        }
        file.close();
    }
}

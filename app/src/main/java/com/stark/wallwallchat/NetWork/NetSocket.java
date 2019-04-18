package com.stark.wallwallchat.NetWork;


import android.util.Log;

import com.stark.wallwallchat.Listener.DownFileListener;
import com.stark.wallwallchat.Listener.UpFileListener;
import com.stark.wallwallchat.Util.Client;
import com.stark.wallwallchat.json.JsonConvert;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.InetAddress;

/**
 * Created by Stark on 2017/2/11.
 */
public class NetSocket {
    private static int PORT=12346;
    private static int FILEPORT=12347;
    public static String request(String Package)throws  NullPointerException,IOException
    {
        Client client=getClient(PORT);
        client.send(Package);
        String result=client.receive();
        client.Close();
        return result;
    }
    public static String request(String Url,String JsonStr) throws Exception{
        HttpClient httpclient = new DefaultHttpClient();
        HttpPost httpPost = new HttpPost(Url);
        httpPost.addHeader(HTTP.CONTENT_TYPE,"application/x-www-form-urlencoded;application/json;charset=utf-8");
        StringEntity se = new StringEntity(JsonStr,"UTF-8");
        se.setContentEncoding("UTF-8");
        httpPost.setEntity(se);
        HttpResponse response = httpclient.execute(httpPost);
        if(response.getStatusLine().getStatusCode()== HttpStatus.SC_OK) {
            HttpEntity entity = response.getEntity();
            if (entity != null) {
                return EntityUtils.toString(entity, "UTF-8");
            }
        }
        return null;
    }
    public static void request(String Package,String FilePath, UpFileListener listener)throws NullPointerException,IllegalAccessException,JSONException,IOException{
        Client client=getClient(FILEPORT);
        client.send(Package);
        String result=client.receive();
        NetBuilder out=(NetBuilder) JsonConvert.DeserializeObject(result, new NetBuilder());
        if(out.getBool("flag",false)){
            RandomAccessFile tempFile=new RandomAccessFile(FilePath,"r");
            tempFile.seek(out.getLong("range", 0l));
            client.send(tempFile, listener);
            result=client.receive();
            NetBuilder temp=(NetBuilder) JsonConvert.DeserializeObject(result, new NetBuilder());
            if(temp.getBool("flag",false)||temp.getInt("error",-2)==8){
                listener.finish(temp.get("hashcode"));
            }else{
                listener.error(temp.getInt("error",-2));
            }
        }else{
            listener.error(out.getInt("error",-2));
        }
        client.Close();
    }
    public static void request(String Package,String FilePath, DownFileListener listener)throws NullPointerException,IllegalAccessException,JSONException,IOException{
        Client client=getClient(FILEPORT);
        client.send(Package);
        String result=client.receive();
        NetBuilder out=(NetBuilder)JsonConvert.DeserializeObject(result,new NetBuilder());
        if(out.getBool("flag",false)){
            RandomAccessFile  file=new RandomAccessFile(FilePath,"rw");
            file.seek(out.getInt("range", 0));
            client.receive(file, out.getLong("size", 0l), listener);
        }else{
            listener.error(out.getInt("error", -2));
        }
        client.Close();
    }
    public static Client getClient(int port)throws NullPointerException,IOException{
        Client client=null;
        InetAddress[] name=InetAddress.getAllByName("www.paikeji.cn");
        for(InetAddress a:name){
            try{
                client = new Client(a.getHostAddress(), port, 20000, 400000);
                break;
            } catch (Exception e) {
                Log.e("request", e.toString());
            }
        }
        if(client==null){
            throw new NullPointerException("Server is shutdown !");
        }
        return client;
    }
}

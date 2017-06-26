package com.stark.yiyu;


import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;

import com.stark.yiyu.Format.Ack;
import com.stark.yiyu.Format.CmdType;
import com.stark.yiyu.Format.Format;
import com.stark.yiyu.Format.Msg;
import com.stark.yiyu.NetWork.NetPackage;
import com.stark.yiyu.SQLite.Data;
import com.stark.yiyu.SQLite.DatabaseHelper;
import com.stark.yiyu.Util.Try;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.Queue;

/**
 * Created by Stark on 2017/1/20.
 */
public class MyService extends Service {
    public Socket MsgSocket=null;
    private String JsonMsg;
    private String ID=null;
    private String PassWord=null;
    private String UUID=null;
    private String IP="60.205.191.131";
    private int PORT=12345;
    private String UIP="";
    private int UPORT=3000;
    private boolean outRec=true;
    private int NetState=1;
    private int LogState=0;
    private Queue<String>Msg=null;
    private SQLiteDatabase db=null;
    private BroadcastReceiver mReceiver;
    @Override
    public IBinder onBind(Intent intent){
        return null;
    }
    @Override
    public void onCreate(){
        super.onCreate();
        db=new DatabaseHelper(MyService.this).getWritableDatabase();
        AlarmManager am = (AlarmManager)getSystemService(ALARM_SERVICE);
        Intent intent=new Intent(this,MyService.class);
        intent.putExtra("CMD", "Heart");
        am.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), 60000, PendingIntent.getService(this, 0, intent, 0));
        mReceiver=new BroadcastReceiver(){
            public void onReceive(Context context, Intent intent) {
                    ConnectivityManager mConnectivityManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
                    NetworkInfo netInfo = mConnectivityManager.getActiveNetworkInfo();
                    if(netInfo != null && netInfo.isAvailable()) {
                        if(netInfo.getType()==ConnectivityManager.TYPE_WIFI){
                            NetState=2;
                        }else if(netInfo.getType()==ConnectivityManager.TYPE_ETHERNET){
                            NetState=1;
                        }else if(netInfo.getType()==ConnectivityManager.TYPE_MOBILE){
                            NetState=1;
                        }
                        handle.postDelayed(Auto,3000);
                    } else {
                        LogState=0;
                        NetState=0;
                        handle.removeCallbacks(Auto);
                        Try.CloseSocket(MsgSocket);
                    }
            }
        };
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(mReceiver, intentFilter);
        Log.i("MyService","in onCreate");
    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i("MyService", "in onStartCommand");
        String cmd= Try.getStringExtra(intent, "CMD");
        if(cmd!=null&&!cmd.equals("")) {
            if (cmd.equals("Welcome")) {
                if(NetState!=0&&!Connected()) {
                    new Thread(MsgTask).start();
                }
            } else if (cmd.equals("Manual")) {
                ID= Try.getStringExtra(intent, "id");
                PassWord= Try.getStringExtra(intent, "password");
                UUID=this.getSharedPreferences("action", MODE_PRIVATE).getString("MyUUID",null);
                manualLogin();
            } else if (cmd.equals("Heart")){
                if (Connected()) {
                    sendHeart();
                }
            }
        }else {
            Log.i("MyService","null CMD");
        }
        return START_NOT_STICKY;
    }
    @Override
    public void onDestroy() {
        Log.i("message", "in onDestroy");
        unregisterReceiver(mReceiver);
        super.onDestroy();
    }
    public boolean Connected(){
        if(MsgSocket==null||MsgSocket.isClosed()){
            return false;
        }else{
            return MsgSocket.isConnected();
        }
    }
    public boolean AutoLogin()
    {
        SharedPreferences sp = this.getSharedPreferences("action", MODE_PRIVATE);
        if(!sp.getBoolean("state",false)) {
            return false;
        }
        String UUID=sp.getString("MyUUID",null);
        String id=sp.getString("id",null);
        String password=sp.getString("password",null);
        if(id!=null&& password!=null) {
            JsonMsg=NetPackage.Login(id, password,UUID,0)+'\n';
            new Thread(send).start();
            return true;
        }
        else
            return false;
    }
    public void manualLogin(){
        JsonMsg=NetPackage.Login(ID,PassWord,UUID,1)+'\n';
        new Thread(send).start();
    }
    public void sendHeart(){
        SharedPreferences sp = this.getSharedPreferences("action", MODE_PRIVATE);
        if(!sp.getBoolean("state",false)) {
            return ;
        }
        String id=sp.getString("id",null);
        String password=sp.getString("password",null);
        if(id!=null&& password!=null) {
            JsonMsg=NetPackage.Heart(id) + '\n';
            new Thread(send).start();
        }
    }
    Handler handle=new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            handle.removeCallbacks(Auto);
            return false;
        }
    });
    Runnable Auto =new Runnable() {
        @Override
        public void run() {
            if(outRec&&Connected()){
                new Thread(RecTask).start();
            }
            if (NetState!=0&&!Connected()) {
                new Thread(MsgTask).start();
            }
            handle.postDelayed(Auto,5000);
        }
    };
    Runnable send = new Runnable() {
        @Override
        public void run() {
            // TODO
            if(outRec&&Connected()){
                new Thread(RecTask).start();
            }else if(NetState!=0&&!Connected()){
                new Thread(MsgTask).start();
            }
            if(Connected()) {
                try {
                    DataOutputStream bw = new DataOutputStream(MsgSocket.getOutputStream());
                    bw.write(JsonMsg.getBytes("UTF-8"));
                    bw.flush();
                } catch (Exception e) {
                    Log.i("test","test");
                    Try.CloseSocket(MsgSocket);
                }
            }else{
                //存入消息队列
            }
        }
    };
    Runnable MsgTask=new Runnable() {
        @Override
        public void run() {
            Log.i("MsgService", "线程启动成功");
            MsgSocket = Try.getSocket(IP, PORT);
            if(Connected()) {
                if(outRec){
                    new Thread(RecTask).start();
                }
                NetState=NetState==0?1:NetState;
                AutoLogin();
            }else{
                MsgSocket=Try.getSocket(IP,PORT+1);
                //发送广播，刷新UI
            }
        }
    };
    Runnable RecTask = new Runnable() {
        @Override
        public void run() {
            // TODO
            while (true) {
                outRec=false;
                try {
                    BufferedReader br = new BufferedReader(new InputStreamReader(MsgSocket.getInputStream(),"UTF-8"));
                    String out = br.readLine();
                    Log.i("rec",out);
                    Format format=NetPackage.getFormatBag(out);
                    switch (CmdType.valueOf(format.Type)) {
                        case Ack:
                            Ack ack=(Ack)NetPackage.getBag(format.JsonMsg,format.Type);
                            if(format.Cmd.equals("login")){
                                int temp=0;
                                if(ack!=null){
                                    if(ack.Flag){
                                        LogState=1;
                                        temp=10;
                                    }else{
                                        temp=ack.Error;
                                        LogState=0;
                                    }
                                }
                                Intent intent = new Intent();
                                intent.setAction("com.stark.yiyu.login");
                                intent.putExtra("key", temp);
                                sendBroadcast(intent);
                            }
                            break;
                        case Message:
                            if(format.Cmd.equals("csend")) {
                                Msg msg = (Msg) NetPackage.getBag(format.JsonMsg, format.Type);
                                if(msg==null)
                                    break;
                                db.execSQL("CREATE TABLE IF NOT EXISTS u" + msg.SrcId + "(id varchar(20),type integer,bubble integer,msg varchar(1024),msgcode varchar(20),date varchar(10),time varchar(12),ack integer)");
                                db.insert("u" + msg.SrcId, null, Data.getSChatContentValues(msg.SrcId, msg.SendType, msg.Bubble, msg.Msg, msg.MsgCode, msg.Date, msg.Time, 1));
                                Intent intent = new Intent();
                                intent.setAction("com.stark.yiyu.msg");
                                intent.putExtra("Msg", format.JsonMsg);
                                intent.putExtra("BagType", format.Type);
                                sendBroadcast(intent);
                            }
                            break;
                        default:
                            break;
                    }
                } catch (Exception e) {
                    Log.i("RecTask",e.toString());
                    Try.CloseSocket(MsgSocket);
                    break;
                }
                if(!Connected()){
                    break;
                }
            }
            outRec=true;
        }
    };
}

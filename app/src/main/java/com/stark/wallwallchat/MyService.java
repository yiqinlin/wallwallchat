package com.stark.wallwallchat;


import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;

import com.stark.wallwallchat.Format.CmdType;
import com.stark.wallwallchat.NetWork.NetBuilder;
import com.stark.wallwallchat.NetWork.NetPackage;
import com.stark.wallwallchat.NetWork.NetSocket;
import com.stark.wallwallchat.SQLite.Data;
import com.stark.wallwallchat.SQLite.DatabaseHelper;
import com.stark.wallwallchat.Util.Try;
import com.stark.wallwallchat.json.JsonConvert;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Locale;
import java.util.Queue;

/**
 * Created by Stark on 2017/1/20.
 */
public class MyService extends Service {
    public Socket MsgSocket=null;
    private String ID=null;
    private String PassWord=null;
    private String IP="60.205.191.131";
    private int PORT=23333;
    private boolean outRec=true;
    private int NetState=1;
    private Queue<SendData>Msg=new LinkedList<SendData>() ;
    private Thread ThreadSend=null;
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
        Intent intent=new Intent(this,MyService.class);
        intent.putExtra("CMD", "Heart");
        AlarmManager am = (AlarmManager)getSystemService(ALARM_SERVICE);
        am.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), 180000, PendingIntent.getService(this, 0, intent, 0));
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
                        NetState=0;
                        handle.removeCallbacks(Auto);
                        Try.CloseSocket(MsgSocket);
                    }
            }
        };
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(mReceiver, intentFilter);
        Log.i("MyService", "in onCreate");
    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String cmd= Try.getStringExtra(intent, "CMD");
        Log.i("MyService", "in onStartCommand:" + cmd);
        if(cmd!=null&&!cmd.equals("")) {
            if (cmd.equals("Welcome")) {
                if(NetState!=0&&!Connected()) {
                    new Thread(MsgTask).start();
                }
            } else if (cmd.equals("Manual")) {
                ID= Try.getStringExtra(intent, "id");
                PassWord= Try.getStringExtra(intent, "password");
                manualLogin();
            } else if (cmd.equals("Heart")){
                if (Connected()) {
                    sendHeart();
                }
            } else if (cmd.equals("Image")) {
                /**
                 */
            }else if(cmd.equals("WallMsg")){
                WSend(intent);
            }else if(cmd.equals("Agree")){
                Agree(intent);
            }else if(cmd.equals("Comment")){
                Comment(intent);
            }else if(cmd.equals("Change")){
                Change(intent);
            }else if(cmd.equals("ChangeRead")){
                ChangeRead(intent);
            }
        }else {
            Log.i("MyService","null CMD");
        }
        return START_STICKY;
    }
    @Override
    public void onDestroy() {
        Log.i("message", "in onDestroy");
        unregisterReceiver(mReceiver);
        super.onDestroy();
    }
    private class SendData{
        public String url;
        public String str;
        SendData(String url,String str){
            this.url=url;
            this.str=str;
        }
    }
    private void Send(){
        if(ThreadSend==null||!ThreadSend.isAlive()){
            ThreadSend =new Thread(send);
            ThreadSend.start();
        }
    }
    private boolean Connected(){
        if(MsgSocket==null||MsgSocket.isClosed()){
            return false;
        }else{
            return MsgSocket.isConnected();
        }
    }
    public boolean Change(Intent intent){
        try {
            Msg.add(new SendData("http://kwall.cn/changeUser.php",intent.getStringExtra("data")));
        }catch (Exception e){
            return false;
        }
        Send();
        return true;
    }
    public boolean ChangeRead(Intent intent){
        try {
            NetBuilder N=new NetBuilder();
            N.put("receiver", intent.getStringExtra("receiver"));
            Msg.add(new SendData("http://kwall.cn/msgRead.php",N.build(MyService.this)));
        }catch (Exception e){
            return false;
        }
        Send();
        return true;
    }
    public boolean Comment(Intent intent) {
        try {
            NetBuilder N=new NetBuilder();
            N.put("msg", intent.getStringExtra("msg"))
                    .put("type", intent.getIntExtra("type", 0))
                    .put("mode",intent.getIntExtra("mode", 0))
                    .put("receiver",intent.getStringExtra("receiver"))
                    .put("msgcode3", intent.getStringExtra("msgcode3"))
                    .put("msgcode2", new SimpleDateFormat("yyyyMMddHHmmssSSS", Locale.CHINA).format(new Date()));
            Msg.add(new SendData("http://kwall.cn/comment.php",N.build(MyService.this)));
        }catch (Exception e){
            Log.e("service",e.toString());
            return false;
        }
        Send();
        return true;
    }
    public boolean Agree(Intent intent){
        try {
            NetBuilder N=new NetBuilder();
            N.add("mode", intent.getIntExtra("mode", 0))
                    .put("msgcode", intent.getStringExtra("msgcode"))
                    .put("receiver", intent.getStringExtra("receiver"));
            Msg.add(new SendData("http://kwall.cn/agree.php",N.build(MyService.this)));
        }catch (Exception e){
            Log.e("service",e.toString());
            return false;
        }
        Send();
        return true;
    }
    public boolean WSend(Intent intent){
        try {
            NetBuilder N=new NetBuilder();
            N.put("msg", intent.getStringExtra("msg"))
                    .put("mode", intent.getIntExtra("mode", 0))
                    .put("type", intent.getIntExtra("type", 0))
                    .put("msgcode2", new SimpleDateFormat("yyyyMMddHHmmssSSS", Locale.CHINA).format(new Date()));
            Msg.add(new SendData("http://kwall.cn/wallSend.php",N.build(MyService.this)));
        }catch (Exception e){
            Log.e("service",e.toString());
            return false;
        }
        Send();
        return true;
    }
    public boolean AutoLogin()
    {
        SharedPreferences sp = this.getSharedPreferences("action", MODE_PRIVATE);
        if(!sp.getBoolean("state",false)) {
            return false;
        }
        try {
            NetPackage netPkg = new NetPackage(MyService.this);
            Msg.add(new SendData(null,JsonConvert.SerializeObject(netPkg.Login(true))));
        }catch (Exception e){
            Log.e("service",e.toString());
            return false;
        }
        Send();
        return true;
    }
    public void manualLogin(){
        try {
            HashMap<String, Object> SqlPkg = new HashMap<String, Object>();
            SqlPkg.put("user", ID);
            SqlPkg.put("password", PassWord);
            NetPackage netPkg = new NetPackage(MyService.this, SqlPkg);
            Msg.add(new SendData(null,JsonConvert.SerializeObject(netPkg.Login(false))));
        }catch (Exception e){
            Log.e("service",e.toString());
            return ;
        }
        Send();
    }
    public void sendHeart(){
        SharedPreferences sp = this.getSharedPreferences("action", MODE_PRIVATE);
        if(!sp.getBoolean("state",false)) {
            return ;
        }
        String id=sp.getString("id",null);
        String password=sp.getString("password",null);
        if(id!=null&& password!=null) {
            try {
                NetPackage netPkg = new NetPackage(MyService.this);
                Msg.add(new SendData(null,JsonConvert.SerializeObject(netPkg.Heart())));
            }catch (Exception e){
                Log.e("service",e.toString());
                return ;
            }
            Send();
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
            try {
                DataOutputStream bw = new DataOutputStream(MsgSocket.getOutputStream());
                while(Connected()&&Msg.peek()!=null){
                    SendData data=Msg.poll();
                    if(data.url==null) {
                        bw.write(data.str.getBytes("UTF-8"));
                        bw.flush();
                    }else{
                        String result =NetSocket.request(data.url,data.str);
                        Intent intent=new Intent();
                        intent.setAction("com.stark.wallwallchat.result");
                        intent.putExtra("data",result);
                        sendBroadcast(intent);
                    }
                }
            }catch (Exception e){
                Log.i("test",e.toString());
                Try.CloseSocket(MsgSocket);
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
                    Log.e("rec", out);
                    NetBuilder format = new NetBuilder(out);
                    switch (CmdType.valueOf(format.get("Type"))) {
                        case Ack:
                            if(format.get("Cmd").equals("login")){
                                if(format.getBool("flag", false)){
                                    Cursor cursor = db.query("userdata",null, "id=?", new String[]{format.get("id")}, null, null, null);
                                    if(cursor!=null&&cursor.getCount()>0&&cursor.moveToNext()) {
                                        ContentValues temp= Data.MapToContentValues((HashMap) format.getList(0));
                                        db.update("userdata", temp, "id=?", new String[]{format.get("id")});
                                        cursor.close();
                                    }else {
                                        ContentValues temp=Data.MapToContentValues((HashMap) format.getList(0));
                                        db.insert("userdata", null, temp);
                                    }
                                }
                                Intent intent = new Intent();
                                intent.setAction("com.stark.wallwallchat.login");
                                intent.putExtra("id", format.get("id"));
                                intent.putExtra("key", format.getInt("error", -2));
                                sendBroadcast(intent);
                            }else if(format.get("Cmd").equals("userinfo")){
                                Intent intent=new Intent();
                                intent.setAction("com.stark.wallwallchat.DBUpdate");
                                sendBroadcast(intent);
                            }else if(format.get("Cmd").equals("changeUser")){
                                ContentValues temp= Data.MapToContentValues((HashMap) format.Data.get(0));
                                db.update("userdata", temp, "id=?", new String[]{format.get("id")});
                                MyService.this.getSharedPreferences("action", MODE_PRIVATE).edit().putString("nick",temp.getAsString("nick")).putString("auto",temp.getAsString("auto")).apply();
                                Intent intent=new Intent();
                                intent.setAction("com.stark.wallwallchat.DBUpdate");
                                sendBroadcast(intent);
                            }
                            Intent intent=new Intent();
                            intent.setAction("com.stark.wallwallchat.Ack");
                            intent.putExtra("Cmd",format.get("Cmd"));
                            sendBroadcast(intent);
                            break;
                        case Message:
                            if(format.get("Cmd").equals("csend")) {
                                HashMap Msg=(HashMap)format.Data.get(0);
                                Msg.remove("sendack");
                                Msg.put("ack", 1);
                                db.insert("msg", null, Data.MapToContentValues(Msg));
                                Msg.remove("receiver");
                                Msg.remove("msgcode2");
                                Msg.remove("ack");
                                Msg.put("remarks", format.get("remarks"));
                                //Msg.put("count", format.get("count"));
                                Cursor cursor = db.query("mid",null, "sponsor=?", new String[]{format.get("sponsor")}, null, null, null);
                                if(cursor!=null&&cursor.getCount()>0&&cursor.moveToNext()) {
                                    db.update("mid", Data.MapToContentValues(Msg) , "sponsor=?", new String[]{format.get("sponsor")});
                                    cursor.close();
                                }else {
                                    db.insert("mid", null, Data.MapToContentValues(Msg));
                                }
                                SharedPreferences sp=getSharedPreferences("action",MODE_PRIVATE);
                                if(sp.getBoolean("vibrate",true)) {
                                    MyVibrator.getVibrator(MyService.this, 0);
                                }
                                if(sp.getBoolean("notification", true)) {
                                    MyNotification.Send(MyService.this,(String)Msg.get("remarks"),(String)Msg.get("msg"),10000);
                                }
                                Intent intent1 = new Intent();
                                intent1.setAction("com.stark.wallwallchat.msg");
                                intent1.putExtra("sponsor", format.get("sponsor"));
                                intent1.putExtra("msgcode",format.get("msgcode"));
                                sendBroadcast(intent1);
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

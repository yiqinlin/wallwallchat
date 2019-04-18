package com.stark.wallwallchat;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.support.v4.app.NotificationCompat;

import com.stark.wallwallchat.UIactivity.TransferActivity;

/**
 * Created by Stark on 2017/1/21.
 */
public class MyNotification {
    public static void Send(Context context,String title,String text,int id){
        Intent intent = new Intent(context,TransferActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
        NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(context.NOTIFICATION_SERVICE);
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context);
        mBuilder.setContentTitle(title)//设置通知栏标题
                .setContentText(text)
                .setWhen(System.currentTimeMillis())//通知产生的时间，会在通知信息里显示，一般是系统获取到的时间
                .setPriority(Notification.PRIORITY_MAX) //设置该通知优先级
                .setAutoCancel(true)//设置这个标志当用户单击面板就可以让通知将自动取消
                .setOngoing(false)//ture，设置他为一个正在进行的通知。他们通常是用来表示一个后台任务,用户积极参与(如播放音乐)或以某种方式正在等待,因此占用设备(如一个文件下载,同步操作,主动网络连接)
                //.setSmallIcon(R.drawable.tianqing)//设置通知小ICON
                .setLargeIcon(BitmapFactory.decodeResource(context.getResources(),R.drawable.tianqing))//设置通知小ICON
                .setContentIntent(pendingIntent);

        Notification notification=mBuilder.build();
        mNotificationManager.notify(id,notification);
    }
}

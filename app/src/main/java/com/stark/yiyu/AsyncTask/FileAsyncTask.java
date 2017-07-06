package com.stark.yiyu.AsyncTask;

import android.os.AsyncTask;

import com.stark.yiyu.File.FileMode;
import com.stark.yiyu.Format.Ack;
import com.stark.yiyu.NetWork.MD5;
import com.stark.yiyu.NetWork.NetPackage;
import com.stark.yiyu.NetWork.NetSocket;

import java.io.File;

/**
 * Created by Stark on 2017/7/6.
 */
public class FileAsyncTask extends AsyncTask<String, Integer, Void> {
    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }
    @Override
    protected java.lang.Void doInBackground(String... values) {
        if(values[0].equals("up")) {
           String path = values[1];
            File file = new File(path);
            String answer = NetSocket.request(NetPackage.SendFile(MD5.get(file), file.getName(), file.length(), true), path, FileMode.UPLOAD);
            Ack ack = (Ack) NetPackage.getBag(answer);
            if (ack.Flag) {
                publishProgress(1);
            } else {
                publishProgress(0);
            }
        }else if(values[0].equals("down")){
            String path = values[1];
            String answer = NetSocket.request(NetPackage.getFile(values[2]), path, FileMode.DOWNLOAD);
            Ack ack = (Ack) NetPackage.getBag(answer);
            if (ack.Flag) {
                publishProgress(1);
            } else {
                publishProgress(0);
            }
        }
        return null;
    }
    @Override
    protected void onProgressUpdate(java.lang.Integer... values) {
        super.onProgressUpdate(values);
        switch (values[0]) {
            case 0:
                break;
            case 1:
                break;
        }
    }
}


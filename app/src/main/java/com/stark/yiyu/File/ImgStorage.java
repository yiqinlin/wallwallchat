package com.stark.yiyu.File;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by asus on 2017/7/1.
 */
public class ImgStorage {

    public static String getPhotoPath(Context context) {
        String photoPath = String.format("data/data/%1$s/imgbases/", context.getApplicationContext().getPackageName());
        return photoPath;
    }

    public static Uri saveBitmap(Bitmap bm, String pathName, String imgName) {
        File filePath = new File(Environment.getExternalStorageDirectory(), pathName);
        if (!filePath.exists()) {
            filePath.mkdir();
        }
        File imgPath = new File(filePath, imgName);
        try {
            FileOutputStream fos = new FileOutputStream(imgPath);
            bm.compress(Bitmap.CompressFormat.PNG, 85, fos);
            fos.flush();
            fos.close();
            return Uri.fromFile(imgPath);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
    //存储图片
    public static Uri saveBitmap(Bitmap bm, String imgName) {
        File filePath = new File(Environment.getExternalStorageDirectory() + "/com.stark.yiyu.File");
        if (!filePath.exists()) {
            filePath.mkdir();
        }
        File imgPath = new File(filePath, imgName);
        try {
            FileOutputStream fos = new FileOutputStream(imgPath);
            bm.compress(Bitmap.CompressFormat.PNG, 85, fos);
            fos.flush();
            fos.close();
            return Uri.fromFile(imgPath);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
    //存储图片
    public static void saveCirBitmap(String cirHeadPath, String imgName, Bitmap bm) {
        File filePath = new File(cirHeadPath);
        if (!filePath.exists()) {
            filePath.mkdir();
        }
        File imgPath = new File(filePath, imgName);
        try {
            FileOutputStream fos = new FileOutputStream(imgPath);
            bm.compress(Bitmap.CompressFormat.PNG, 85, fos);
            fos.flush();
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

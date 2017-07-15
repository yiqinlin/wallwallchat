package com.stark.yiyu.File;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;

import com.stark.yiyu.Format.FileType;
import com.stark.yiyu.MyService;
import com.stark.yiyu.R;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by asus on 2017/7/1.
 */
public class ImgStorage {

    public static Drawable getHead(Context context) {
        String photoPath = FileUtil.getPath(FileType.mHead);
        photoPath = photoPath + "/cir.png";
        File imgPath = new File(photoPath);
        if (imgPath.exists()) {
            Bitmap bitmap = BitmapFactory.decodeFile(photoPath);
            Drawable drawable = new BitmapDrawable(bitmap);
            return drawable;
        }
        Intent intent = new Intent(context, MyService.class);
        intent.putExtra("CMD", "Image");
        context.startService(intent);
        SharedPreferences sp =context.getSharedPreferences("action", Context.MODE_PRIVATE);
        if (sp.getBoolean("sex",true)) {
            Log.e("圆形图片路径", "不存在,男");
            return context.getResources().getDrawable(R.drawable.tianqing);
        }
        Log.e("圆形图片路径", "不存在,女");
        return context.getResources().getDrawable(R.drawable.tianqing);
    }

    public static String getPhotoPath(Context context) {
        String photoPath = String.format("data/data/%1$s/imgbases/", context.getApplicationContext().getPackageName());
        return photoPath;
    }

    public static void saveBitmap(Bitmap bm, String path) {
        File file = new File(path);
        try {
            FileOutputStream fos = new FileOutputStream(file);
            bm.compress(Bitmap.CompressFormat.PNG, 90, fos);
            fos.flush();
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Intent getCropIntent(Uri in,Uri out){
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(in, "image/*");//数据和类型
        intent.putExtra("crop", "true");//开启的Intent 显示View是可裁减的
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        intent.putExtra("outputX", 150);
        intent.putExtra("outputY", 150);//裁剪的图片的宽高。最终得到的输出图片的宽高252
        intent.putExtra("circleCrop", true);
        intent.putExtra(MediaStore.EXTRA_OUTPUT,out);
        intent.putExtra("outputFormat", Bitmap.CompressFormat.PNG.toString());
        intent.putExtra("return-data", false);//裁剪后的数据通过intent返回回来
        return intent;
    }

    //存储图片
    public static void saveCirBitmap(String cirHeadPath, String imgName, Bitmap bm) {
        File filePath = new File(cirHeadPath);
        if (!filePath.exists()) {
            filePath.mkdirs();
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

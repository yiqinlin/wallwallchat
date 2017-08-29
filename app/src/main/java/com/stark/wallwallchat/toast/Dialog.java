package com.stark.wallwallchat.toast;

import android.app.AlertDialog;
import android.content.Context;

/**
 * Created by asus on 2017/6/27.
 */
public class Dialog {
    private static Context context;
    private static int CAMERA_REQUEST_CODE = 1;
    private static int GALLERY_REQUEST_CODE = 2;
    private static int CROP_REQUEST_CODE = 3;
    private static int MY_PERMISSIONS_REQUEST_CALL_PHONE = 4;


    public static void showChoosePicDialog(Context ctx) {
        context = ctx;
        AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
        builder.setTitle("设置头像");
        String[] items = {"拍照", "选择本地照片"};
        builder.setNegativeButton("取消", null);
        builder.create().show();
    }
}

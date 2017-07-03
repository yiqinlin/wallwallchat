package com.stark.yiyu.UIactivity;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.util.Base64;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.stark.yiyu.File.ImgStorage;
import com.stark.yiyu.Format.Ack;
import com.stark.yiyu.Format.Msg;
import com.stark.yiyu.Format.TransFile;
import com.stark.yiyu.Listview.ElasticListView;
import com.stark.yiyu.NetWork.NetPackage;
import com.stark.yiyu.NetWork.NetSocket;
import com.stark.yiyu.R;
import com.stark.yiyu.SQLite.Data;
import com.stark.yiyu.Util.DateUtil;
import com.stark.yiyu.Util.Error;
import com.stark.yiyu.Util.ImageRound;
import com.stark.yiyu.Util.ListUtil;
import com.stark.yiyu.Util.Status;
import com.stark.yiyu.adapter.MyAdapter;
import com.stark.yiyu.bean.BaseItem;
import com.stark.yiyu.bean.ItemHomepageTitle;
import com.stark.yiyu.bean.ItemSMsg;
import com.stark.yiyu.json.JsonConvert;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class HomepageActivity extends Activity implements MyAdapter.Callback{

    private static int CAMERA_REQUEST_CODE = 1;
    private static int GALLERY_REQUEST_CODE = 2;
    private static int CROP_REQUEST_CODE = 3;
    private static int MY_PERMISSIONS_REQUEST_CAMERA_GALLERY = 4;
    private static int GOTO_APPSETTING = 5;

    private String SrcID = null;
    private String DesId = null;
    private String Nick = null;
    private String Auto = null;
    private ArrayList<BaseItem> mArrays = null;
    private MyAdapter adapter = null;
    private AlertDialog dialog = null;
    private BroadcastReceiver mReceiver = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Status.setTranslucentStatus(getWindow());
        setContentView(R.layout.activity_homepage);

        Button get = (Button) findViewById(R.id.button_homepage_left);//左边按钮
        Button send = (Button) findViewById(R.id.button_homepage_right);//右边按钮
        Intent intent = getIntent();
        SrcID = HomepageActivity.this.getSharedPreferences("action", MODE_PRIVATE).getString("id", null);
        DesId = intent.getStringExtra("id");
        Nick = intent.getStringExtra("nick");//昵称
        Auto = intent.getStringExtra("auto");//签名
        mArrays = new ArrayList<BaseItem>();
        adapter = new MyAdapter(HomepageActivity.this, mArrays);
        ElasticListView listView = (ElasticListView) findViewById(R.id.listView_homePage);
        listView.setAdapter(adapter);
        mArrays.add(new ItemHomepageTitle(5, DesId, ImgStorage.getHead(this, true), Nick, Auto));
        if (!DesId.equals(SrcID)) {
            get.setOnClickListener(Click);
            send.setOnClickListener(Click);
        } else {
            get.setText("待开发");
            send.setText("编辑资料");
        }

        mReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().equals("com.stark.yiyu.changeHead")&&DesId.equals(SrcID)) {
                    mArrays.remove(0);
                    mArrays.add(0, new ItemHomepageTitle(5, DesId, new BitmapDrawable(BitmapFactory.decodeFile(ImgStorage.getPhotoPath(HomepageActivity.this) + "image_cir_head.png")), Nick, Auto));
                    adapter.notifyDataSetChanged();
                }
            }
        };
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("com.stark.yiyu.changeHead");
        registerReceiver(mReceiver, intentFilter);
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(event.getKeyCode()==KeyEvent.KEYCODE_BACK) {
            unregisterReceiver(mReceiver);
            finish();
        }
        return false;
    }
    View.OnClickListener Click = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.button_homepage_left:
                    //环境检测
                    MyAsyncTask myAsyncTask = new MyAsyncTask();
                    myAsyncTask.execute();
                    break;
                case R.id.button_homepage_right:
                    Date date = new Date();
                    DateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA);
                    DateFormat Time = new SimpleDateFormat("HH:mm:ss", Locale.CHINA);
                    Msg msg = new Msg();
                    msg.SrcId = DesId;
                    msg.Remarks = Nick;
                    msg.Msg = Auto;
                    msg.Date = format.format(date);
                    msg.Time = Time.format(date);
                    Intent broad = new Intent();
                    broad.setAction("com.stark.yiyu.msg");
                    broad.putExtra("Msg", JsonConvert.SerializeObject(msg));
                    broad.putExtra("BagType", "Message");
                    sendBroadcast(broad);
                    if (AddActivity.mThis != null) {
                        AddActivity.mThis.finish();
                    }
                    Intent intent = new Intent(HomepageActivity.this, ChatActivity.class);
                    intent.putExtra("nick", Nick);
                    intent.putExtra("id", DesId);
                    startActivityForResult(intent, 0);
                    ChatActivity.This.finish();
                    finish();
                    break;
            }
        }
    };

    public void showChoosePicDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("设置头像");
        String[] items = {"拍照", "选择本地照片"};
        builder.setNegativeButton("取消", null);
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case 0:
                        Log.e("拍照", "选择本地照片");
                        if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.M&&ContextCompat.checkSelfPermission(HomepageActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                            ActivityCompat.requestPermissions(HomepageActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_CAMERA_GALLERY);
                        } else {
                            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                            startActivityForResult(intent, CAMERA_REQUEST_CODE);
                        }
                        break;
                    case 1:
                        if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.M&&ContextCompat.checkSelfPermission(HomepageActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                            ActivityCompat.requestPermissions(HomepageActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_CAMERA_GALLERY);
                        } else {
                            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                            intent.setType("image/*");
                            startActivityForResult(intent, GALLERY_REQUEST_CODE);
                        }
                        break;
                    default:
                        break;
                }
            }
        });
        builder.create().show();
    }


    private void startImageZoom(Uri uri) {//裁剪
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");//数据和类型
        intent.putExtra("crop", "true");//开启的Intent 显示View是可裁减的
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        intent.putExtra("outputX", 150);
        intent.putExtra("outputY", 150);//裁剪的图片的宽高。最终得到的输出图片的宽高252
        intent.putExtra("circleCrop", true);
        intent.putExtra("return-data", true);//裁剪后的数据通过intent返回回来
        startActivityForResult(intent, CROP_REQUEST_CODE);
    }

    private Uri converUri(Uri uri) {
        InputStream is = null;
        try {
            is = getContentResolver().openInputStream(uri);
            Bitmap bitmap = BitmapFactory.decodeStream(is);
            is.close();
            return ImgStorage.saveBitmap(bitmap, "photo_head.png");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

//    private void sendImage(Bitmap bm) {
//        ByteArrayOutputStream stream = new ByteArrayOutputStream();
//        bm.compress(Bitmap.CompressFormat.PNG, 60, stream);
//        byte[] bytes = stream.toByteArray();
//        String img = new String(Base64.encodeToString(bytes, Base64.DEFAULT));
//        /**
//         * 发送到服务器
//         */
//        String picPath = getphotoPath();
//        picPath = picPath + "image_cir_head.png";
//    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CAMERA_REQUEST_CODE) {//从摄像头中获取图像
            if (data == null) {
                return;
            } else {//判断出是拍照
                Bundle extras = data.getExtras();//从data中取出数据
                if (extras != null) {
                    Bitmap bm = extras.getParcelable("data");//保存用户拍摄的数据

                    Uri uri = ImgStorage.saveBitmap(bm, "photo_head.png");//将bitmap转化为uri
                    startImageZoom(uri);//uri必须是File类型
                }
            }
        } else if (requestCode == GALLERY_REQUEST_CODE) {
            if (data == null) {
                return;
            }
            Uri uri;
            uri = data.getData();
            Uri fileUri = converUri(uri);//转化为File类型的uri
            startImageZoom(fileUri);
        } else if (requestCode == CROP_REQUEST_CODE) {
            if (data == null) {
                return;
            }
            Bundle extras = data.getExtras();
            Bitmap bm = extras.getParcelable("data");

            Bitmap roundBitmap = ImageRound.toRoundBitmap(bm);


            ImgStorage.saveCirBitmap(ImgStorage.getPhotoPath(this), "image_cir_head.png", roundBitmap);//保存圆形图片到本地
            FileAsyncTask fileAsyncTask = new FileAsyncTask();

            fileAsyncTask.execute();

        } else if (requestCode == GOTO_APPSETTING) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                int i = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
                if (i != PackageManager.PERMISSION_GRANTED) {
                    showDialogTipUserGoToAppSetting();
                } else {
                    if (dialog != null && dialog.isShowing()) {
                        dialog.dismiss();
                    }
                    Toast.makeText(this, "权限获取成功", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    class FileAsyncTask extends AsyncTask<Void, Integer, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }
        @Override
        protected Void doInBackground(Void...values) {
            String path = ImgStorage.getPhotoPath(HomepageActivity.this) + "image_cir_head.png";
            File file = new File(path);
            String answer = NetSocket.request(NetPackage.SendFile(SrcID, path, file.length(), file.getName(), "12", true), path);
            Ack ack = (Ack) NetPackage.getBag(answer);
            if (ack.Flag) {
                publishProgress(1);
            } else {
                publishProgress(0);
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            switch (values[0]){
                case 0:
                    Toast.makeText(HomepageActivity.this,"发送失败，请稍后重试",Toast.LENGTH_SHORT);
                    break;
                case 1:
                    Intent intent = new Intent();
                    intent.setAction("com.stark.yiyu.changeHead");
                    sendBroadcast(intent);

//                    mArrays.remove(0);
//                    mArrays.add(0, new ItemHomepageTitle(5, DesId, new BitmapDrawable(BitmapFactory.decodeFile(ImgStorage.getPhotoPath(HomepageActivity.this) + "image_cir_head.png")), Nick, Auto));
//                    adapter.notifyDataSetChanged();
                    break;
            }
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == MY_PERMISSIONS_REQUEST_CAMERA_GALLERY) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    boolean b = shouldShowRequestPermissionRationale(permissions[0]);
                    if (!b) {
                        showDialogTipUserGoToAppSetting();
                    } else {
                        finish();
                    }
                } else {
                    Toast.makeText(this, "权限获取成功", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    private void showDialogTipUserGoToAppSetting() {
        dialog = new AlertDialog.Builder(this)
                .setTitle("权限不可用")
                .setMessage("请在-应用设置-权限中，获取权限。")
                .setPositiveButton("接受", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        gotoAppSetting();
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                }).setCancelable(false).show();
    }

    private void gotoAppSetting() {
        Intent intent = new Intent();
        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", getPackageName(), null);
        intent.setData(uri);
        startActivityForResult(intent, GOTO_APPSETTING);
    }

    //头像的点击事件
    @Override
    public void click(View v) {
        if (!DesId.equals(SrcID)) {
            /***
             * 待开发ing
             */
        } else {
            switch (v.getId()) {
                case R.id.list_homepage_head:
                    showChoosePicDialog();
                    break;
                case R.id.list_homepage_nick:
                    /**
                     *
                     */
                    break;
                case R.id.list_homepage_auto:
                    final EditText edtAuto = new EditText(this);
                    new AlertDialog.Builder(this)
                            .setTitle("个性签名")
                            .setView(edtAuto)
                            .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    String auto = edtAuto.getText().toString();

                                }
                            })
                            .setNegativeButton("取消", null)
                            .setCancelable(false).show();
                    break;
            }
        }
    }

    class MyAsyncTask extends AsyncTask<Void, Integer, Void> {
        Ack ack = new Ack();
        String msgcode = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            msgcode = DateUtil.getMsgCode(HomepageActivity.this);
        }

        @Override
        protected Void doInBackground(Void... values) {
            ack = (Ack) NetPackage.getBag(NetSocket.request(NetPackage.Friend(SrcID, DesId, Nick, 0)));
            //db.update("u" + ack.DesId, Data.getSChatContentValues(null, -1, -1, null, ack.BackMsg, DateUtil.Mtod(ack.BackMsg), DateUtil.Mtot(ack.BackMsg), ack.Flag ? 1 : 2), "msgcode=?", new String[]{ack.MsgCode});
            if (!ack.Flag) {
                publishProgress(-1);
            } else {
                publishProgress(1);
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            if (values[0] == -1) {
                Toast.makeText(HomepageActivity.this, com.stark.yiyu.Util.Error.error(ack.Error), Toast.LENGTH_SHORT).show();
            } else if (values[0] == 1) {
                Toast.makeText(HomepageActivity.this, "留存成功", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mReceiver);
    }
}

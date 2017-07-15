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
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.stark.yiyu.File.FileMode;
import com.stark.yiyu.File.FileUtil;
import com.stark.yiyu.File.ImgStorage;
import com.stark.yiyu.Format.Ack;
import com.stark.yiyu.Format.FileType;
import com.stark.yiyu.Format.Msg;
import com.stark.yiyu.Listview.ElasticListView;
import com.stark.yiyu.NetWork.MD5;
import com.stark.yiyu.NetWork.NetPackage;
import com.stark.yiyu.NetWork.NetSocket;
import com.stark.yiyu.R;
import com.stark.yiyu.Util.DateUtil;
import com.stark.yiyu.Util.Error;
import com.stark.yiyu.Util.ImageRound;
import com.stark.yiyu.Util.Status;
import com.stark.yiyu.Util.Try;
import com.stark.yiyu.adapter.MyAdapter;
import com.stark.yiyu.bean.BaseItem;
import com.stark.yiyu.bean.ItemHomepageTitle;
import com.stark.yiyu.json.JsonConvert;

import java.io.File;
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
    private BroadcastReceiver mReceiver = null;
    private String outPath;
    private String creamPath;
    private String cirPath;

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
        mArrays.add(new ItemHomepageTitle(5, DesId, ImgStorage.getHead(this), Nick, Auto));
        if (!DesId.equals(SrcID)) {
            get.setOnClickListener(Click);
            send.setOnClickListener(Click);
        } else {
            get.setText("待开发");
            send.setText("编辑资料");
        }
        outPath=FileUtil.getPath(FileType.ImgTemp) + "/poly.png";
        creamPath=FileUtil.getPath(FileType.ImgTemp) + "/crm.png";
        cirPath=FileUtil.getPath(FileType.ImgTemp) + "/cir.png";

        mReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().equals("com.stark.yiyu.changeHead")&&DesId.equals(SrcID)) {
                    mArrays.remove(0);
                    mArrays.add(0, new ItemHomepageTitle(5, DesId,ImgStorage.getHead(HomepageActivity.this), Nick, Auto));
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
                            intent.putExtra(MediaStore.EXTRA_OUTPUT,FileUtil.PathToUri(creamPath));
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == CAMERA_REQUEST_CODE) {
                Uri in = Uri.fromFile(new File(creamPath));
                Uri out = Uri.fromFile(new File(outPath));
                startActivityForResult(ImgStorage.getCropIntent(in, out), CROP_REQUEST_CODE);
            } else if (requestCode == GALLERY_REQUEST_CODE) {
                startActivityForResult(ImgStorage.getCropIntent(Uri.fromFile(new File(FileUtil.getPhotoPathFromContentUri(this, data.getData()))),Uri.fromFile(new File(outPath))), CROP_REQUEST_CODE);
            } else if (requestCode == CROP_REQUEST_CODE) {

                Bitmap rbm = ImageRound.toRoundBitmap(Try.UriToBm(this, Uri.fromFile(new File(outPath))));
                ImgStorage.saveBitmap(rbm, cirPath);

                FileAsyncTask fileAsyncTask = new FileAsyncTask();
                fileAsyncTask.execute(cirPath);

            } else if (requestCode == GOTO_APPSETTING) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    int i = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
                    if (i != PackageManager.PERMISSION_GRANTED) {
                        PermissionRequest();
                    } else {
                        Toast.makeText(this, "权限获取成功", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }
    }
    private void PermissionRequest() {
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("权限不可用")
                .setMessage("请在-应用设置-权限中，获取权限。")
                .setPositiveButton("接受", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent();
                        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        Uri uri = Uri.fromParts("package", getPackageName(), null);
                        intent.setData(uri);
                        startActivityForResult(intent, GOTO_APPSETTING);
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                }).setCancelable(false).show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, java.lang.String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == MY_PERMISSIONS_REQUEST_CAMERA_GALLERY) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    boolean b = shouldShowRequestPermissionRationale(permissions[0]);
                    if (!b) {
                        PermissionRequest();
                    } else {
                        finish();
                    }
                } else {
                    Toast.makeText(this, "权限获取成功", Toast.LENGTH_SHORT).show();
                }
            }
        }
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
                    final EditText edtNick = new EditText(this);
                    new AlertDialog.Builder(this)
                            .setTitle("修改昵称")
                            .setView(edtNick)
                            .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    String nick = edtNick.getText().toString();
                                    if (nick != null && !nick.equals("")) {

                                    } else {
                                        nick = "";
                                    }
                                }
                            })
                            .setNegativeButton("取消", null).setCancelable(false).show();
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
                                    if (auto != null && !auto.equals("")) {

                                    } else {
                                        auto = "";
                                    }
                                }
                            })
                            .setNegativeButton("取消", null)
                            .setCancelable(false).show();
                    break;
            }
        }
    }
    class FileAsyncTask extends AsyncTask<String, Integer, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }
        @Override
        protected java.lang.Void doInBackground(String... values) {
            String path = values[0];
            Log.e("path",path);
            File file = new File(path);
            String answer = NetSocket.request(NetPackage.SendFile(MD5.get(file), file.getName(), file.length(), true), path, FileMode.UPLOAD);
            Ack ack = (Ack) NetPackage.getBag(answer);
            Log.e("backmsg",answer);
            if(ack.Error==8) {
                answer = NetSocket.request(NetPackage.getFile(ack.BackMsg), FileUtil.getPath(FileType.mHead) + "/cir.png", FileMode.DOWNLOAD);
                ack = (Ack) NetPackage.getBag(answer);
            }else {
                publishProgress(ack.Error);
            }
            if (ack.Flag) {
                if(FileUtil.Move(path,FileUtil.getPath(FileType.mHead),true)&&FileUtil.Move(outPath, FileUtil.getPath(FileType.mHead), true)) {
                    Intent intent = new Intent();
                    intent.setAction("com.stark.yiyu.changeHead");
                    intent.putExtra("hashcode", ack.BackMsg);
                    sendBroadcast(intent);
                    publishProgress(100);
                }else{
                    publishProgress(110);
                }
            }else {
                publishProgress(ack.Error);
            }
            return null;
        }
        @Override
        protected void onProgressUpdate(java.lang.Integer... values) {
            super.onProgressUpdate(values);
            Toast.makeText(HomepageActivity.this, Error.error(values[0]),Toast.LENGTH_SHORT).show();
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
}

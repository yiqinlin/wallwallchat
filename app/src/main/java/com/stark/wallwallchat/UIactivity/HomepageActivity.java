package com.stark.wallwallchat.UIactivity;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.stark.wallwallchat.File.FileMode;
import com.stark.wallwallchat.File.FileUtil;
import com.stark.wallwallchat.File.ImgStorage;
import com.stark.wallwallchat.Format.FileType;
import com.stark.wallwallchat.Listener.DownFileListener;
import com.stark.wallwallchat.Listener.UpFileListener;
import com.stark.wallwallchat.Listview.ElasticListView;
import com.stark.wallwallchat.NetWork.MD5;
import com.stark.wallwallchat.NetWork.NetBuilder;
import com.stark.wallwallchat.NetWork.NetPackage;
import com.stark.wallwallchat.NetWork.NetSocket;
import com.stark.wallwallchat.R;
import com.stark.wallwallchat.SQLite.DatabaseHelper;
import com.stark.wallwallchat.Util.Error;
import com.stark.wallwallchat.Util.Status;
import com.stark.wallwallchat.adapter.MyAdapter;
import com.stark.wallwallchat.bean.BaseItem;
import com.stark.wallwallchat.bean.ItemHomepageTitle;
import com.stark.wallwallchat.json.JsonConvert;
import com.stark.wallwallchat.toast.ToastDialog;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
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
    private ToastDialog mToastDialog=null;
    private String outPath;
    private String creamPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Status.setTranslucentStatus(getWindow());
        setContentView(R.layout.activity_homepage);
        Button get = (Button) findViewById(R.id.button_homepage_right);//右边按钮
        Button back = (Button) findViewById(R.id.button_homepage_left);//左边按钮
        Button send = (Button) findViewById(R.id.button_send);//下边按钮
        Intent intent = getIntent();
        SrcID = HomepageActivity.this.getSharedPreferences("action", MODE_PRIVATE).getString("id", null);
        DesId = intent.getStringExtra("id");
        Nick = intent.getStringExtra("nick");//昵称
        Auto = intent.getStringExtra("auto");//签名
        mArrays = new ArrayList<BaseItem>();
        adapter = new MyAdapter(HomepageActivity.this, mArrays);
        ElasticListView listView = (ElasticListView) findViewById(R.id.listView_homePage);
        listView.setAdapter(adapter);
        mArrays.add(new ItemHomepageTitle(5, DesId, Nick, Auto));
        send.setOnClickListener(Click);
        back.setOnClickListener(Click);
        if (!DesId.equals(SrcID)) {
            send.setVisibility(View.VISIBLE);
            get.setText("关 注");
            get.setOnClickListener(Click);
        }else{
            get.setText(null);
        }
        outPath=FileUtil.getPath(FileType.ImgTemp) + "/poly.png";
        creamPath=FileUtil.getPath(FileType.ImgTemp) + "/crm.png";

        mReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if(DesId.equals(SrcID)){
                    mArrays.clear();
                    SharedPreferences sp=getSharedPreferences("action",MODE_PRIVATE);
                    Nick= sp.getString("nick",null);
                    Auto=sp.getString("auto",null);
                    mArrays.add(new ItemHomepageTitle(5, DesId , Nick, Auto));
                    adapter.notifyDataSetChanged();
                }
            }
        };
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("com.stark.wallwallchat.changeHead");
        intentFilter.addAction("com.stark.wallwallchat.DBUpdate");
        registerReceiver(mReceiver, intentFilter);
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mReceiver);
    }
    View.OnClickListener Click = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.button_homepage_right:
                    //环境检测
                    MyAsyncTask myAsyncTask = new MyAsyncTask();
                    myAsyncTask.execute();
                    break;
                case R.id.button_send:
                    if (!DesId.equals(SrcID)) {
                        Intent intent = new Intent(HomepageActivity.this, ChatActivity.class);
                        intent.putExtra("nick", Nick);
                        intent.putExtra("id", DesId);
                        startActivityForResult(intent, 0);
                        if(ChatActivity.This!=null)
                            ChatActivity.This.finish();
                        finish();
                    }
                    break;

                case R.id.button_homepage_left:
                    //环境检测
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

                FileAsyncTask fileAsyncTask = new FileAsyncTask();
                fileAsyncTask.execute(outPath,FileUtil.getPath(FileType.Head));
//                Bitmap rbm = ImageRound.toRoundBitmap(Try.UriToBm(this, Uri.fromFile(new File(outPath))));
//                ImgStorage.saveBitmap(rbm, cirPath);

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
                                    if (!nick.equals("") && nick.length() != 0) {
                                        /**
                                         */
                                    } else {
                                        Toast.makeText(HomepageActivity.this, "请填写昵称", Toast.LENGTH_SHORT).show();
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
                                    if (!auto.equals("") && auto.length() != 0) {
                                        /**
                                         */
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
            if (mToastDialog == null)
                mToastDialog = new ToastDialog(HomepageActivity.this);
            mToastDialog.setOnTouchClose(false).show();
        }
        @Override
        protected java.lang.Void doInBackground(final String... values) {
            String path = values[0];
            final String path2=values[1];
            final HashMap<String,Object> SqlPkg=new HashMap<>();
            Log.e("path", path);
            try{
                File file=new File(path);
                SqlPkg.put("hashcode", MD5.get(file));
                SqlPkg.put("islong", true);
                SqlPkg.put("size", file.length());
                SqlPkg.put("name", file.getName());
                String temp1=JsonConvert.SerializeObject(new NetPackage(HomepageActivity.this,SqlPkg).UpFile());
                Log.e("request", temp1);
                SqlPkg.remove("islong");
                SqlPkg.remove("size");
                SqlPkg.remove("name");
                SqlPkg.put("thumb", true);
                SqlPkg.put("range",0);
                SqlPkg.put("width", 200);
                SqlPkg.put("height",200);
                SqlPkg.put("mode", "HW");
                final String temp2=JsonConvert.SerializeObject(new NetPackage(HomepageActivity.this, SqlPkg).DownFile());
                NetSocket.request(temp1, path, new UpFileListener() {
                    @Override
                    public void update(int progress) {
                        onProgressUpdate(FileMode.UPLOAD,-3,progress);
                    }
                    @Override
                    public void finish(final String hash) {
                        try {
                            Log.e("request", temp2 + ":" + path2);
                            NetSocket.request(temp2, path2, new DownFileListener() {
                                @Override
                                public void update(int progress) {
                                    onProgressUpdate(FileMode.DOWNLOAD,-3,progress);
                                }
                                @Override
                                public void finish() {
                                    HashMap<String,Object> SqlPkg=new HashMap<String, Object>();
                                    SqlPkg.put("head", hash);
                                    NetPackage netPkg=new NetPackage(HomepageActivity.this,SqlPkg);
                                    try {
                                        String temp = JsonConvert.SerializeObject(netPkg.ChangeUser());
                                        Log.e("temp", temp);
                                        String re=NetSocket.request(temp);
                                        Log.e("re",re);
                                        NetBuilder out=(NetBuilder)JsonConvert.DeserializeObject(re, new NetBuilder());
                                        if(out.getBool("flag",false)) {
                                            out.UpdateDB(new DatabaseHelper(HomepageActivity.this).getWritableDatabase(), HomepageActivity.this.getSharedPreferences("action", MODE_PRIVATE));
                                            String path=FileUtil.getPath(FileType.Head)+"/"+hash+".png";
                                            File file=new File(path);
                                            if(file.exists()){
                                                ContentValues cv=new ContentValues();
                                                cv.put("hashcode",hash);
                                                cv.put("name",hash+".png");
                                                cv.put("path",path);
                                                cv.put("time", new SimpleDateFormat("yyyyMMddHHmmssSSS", Locale.CHINA).format(new Date()));
                                                new DatabaseHelper(HomepageActivity.this).getWritableDatabase().insert("file", null, cv);
                                            }
                                            onProgressUpdate(FileMode.DOWNLOAD,out.getInt("error", -2));
                                        }else{
                                            onProgressUpdate(FileMode.DOWNLOAD,out.getInt("error", -2));
                                        }
                                    }catch (Exception e){
                                        onProgressUpdate(FileMode.DOWNLOAD,10);
                                    }
                                    onProgressUpdate(FileMode.DOWNLOAD, 0);
                                }
                                @Override
                                public void error(int error) {
                                    onProgressUpdate(FileMode.DOWNLOAD, error);
                                }
                            });
                        }catch (Exception e){
                            onProgressUpdate(FileMode.DOWNLOAD,10);
                        }
                    }
                    @Override
                    public void error(int error) {
                        onProgressUpdate(FileMode.UPLOAD,error);
                    }
                });
            }catch (Exception e){
                Log.e("file",e.toString());
            }
            return null;
        }
        @Override
        protected void onProgressUpdate(java.lang.Integer... values) {
            super.onProgressUpdate(values);
            if(values[1]==-3) {
                switch (values[0]) {
                    case FileMode.UPLOAD:
                        break;
                    case FileMode.DOWNLOAD:
                        Log.e("hhhh",values[2]+"");
                        break;
                }
            }else if (values[1] == 0) {
                mToastDialog.cancel();
                Intent intent=new Intent();
                intent.setAction("com.stark.wallwallchat.changeHead");
                sendBroadcast(intent);
            } else {
                Toast.makeText(HomepageActivity.this, Error.error(values[1]), Toast.LENGTH_SHORT).show();
            }
        }
    }
    class MyAsyncTask extends AsyncTask<Void, Integer, Void> {
        NetBuilder ack = new NetBuilder();
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }
        @Override
        protected Void doInBackground(Void... values) {
            HashMap<String,Object> SqlPkg=new HashMap<>();
            SqlPkg.put("receiver",DesId);
            SqlPkg.put("remarks",Nick);
            NetPackage netPkg=new NetPackage(HomepageActivity.this,SqlPkg);
            try {
                String temp = JsonConvert.SerializeObject(netPkg.Friend(0));
                String result;
                try {
                    result = NetSocket.request(temp);
                    ack = (NetBuilder) JsonConvert.DeserializeObject(result, new NetBuilder());
                    Log.e("temp",temp);
                    Log.e("result",result);
                    publishProgress(ack.getInt("error", -2));
                } catch (Exception e) {
                    publishProgress(-1);
                }
            }catch (Exception e){
                publishProgress(7);
                Log.e("NetWork", e.toString());
                e.printStackTrace();
            }
            return null;
            //db.update("u" + ack.DesId, Data.getSChatContentValues(null, -1, -1, null, ack.BackMsg, DateUtil.Mtod(ack.BackMsg), DateUtil.Mtot(ack.BackMsg), ack.Flag ? 1 : 2), "msgcode=?", new String[]{ack.MsgCode});
        }
        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            if(values[0]==0){
                Toast.makeText(HomepageActivity.this, "留存成功", Toast.LENGTH_SHORT).show();
            }else{
                Toast.makeText(HomepageActivity.this, com.stark.wallwallchat.Util.Error.error(values[0]), Toast.LENGTH_SHORT).show();
            }
        }
    }
}

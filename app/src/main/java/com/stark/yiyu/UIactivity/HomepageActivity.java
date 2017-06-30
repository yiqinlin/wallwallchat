package com.stark.yiyu.UIactivity;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;
import com.stark.yiyu.Format.Ack;
import com.stark.yiyu.Format.Msg;
import com.stark.yiyu.Listview.ElasticListView;
import com.stark.yiyu.NetWork.NetPackage;
import com.stark.yiyu.NetWork.NetSocket;
import com.stark.yiyu.R;
import com.stark.yiyu.Util.DateUtil;
import com.stark.yiyu.Util.Status;
import com.stark.yiyu.adapter.MyAdapter;
import com.stark.yiyu.bean.BaseItem;
import com.stark.yiyu.bean.ItemHomepageTitle;
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

public class HomepageActivity extends Activity {

    private static int CAMERA_REQUEST_CODE = 1;
    private static int GALLERY_REQUEST_CODE = 2;
    private static int CROP_REQUEST_CODE = 3;
    private static int MY_PERMISSIONS_REQUEST_CALL_PHONE = 4;

    private String SrcID = null;
    private String DesId = null;
    private String Nick = null;
    private String Auto = null;
    private ArrayList<BaseItem> mArrays = null;
    private MyAdapter adapter = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Status.setTranslucentStatus(getWindow());
        setContentView(R.layout.activity_homepage);

        ImageButton imgHead = (ImageButton) findViewById(R.id.list_homepage_head);

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
        mArrays.add(new ItemHomepageTitle(5, DesId, getResources().getDrawable(R.drawable.tianqing), Nick, Auto));
        if (!DesId.equals(SrcID)) {
            get.setOnClickListener(Click);
            send.setOnClickListener(Click);
        } else {
            imgHead.setOnClickListener(Click);

            get.setText("待开发");
            send.setText("待开发");
        }
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
                case R.id.list_homepage_head:
                    showChoosePicDialog();
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
                        if (ContextCompat.checkSelfPermission(HomepageActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                            ActivityCompat.requestPermissions(HomepageActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_CALL_PHONE);
                        } else {
                            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                            startActivityForResult(intent, CAMERA_REQUEST_CODE);
                        }
                        break;
                    case 1:
                        if (ContextCompat.checkSelfPermission(HomepageActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                            ActivityCompat.requestPermissions(HomepageActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_CALL_PHONE);
                        } else {
                            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                            intent.setType("imgage/*");
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

    private Uri saveBitmap(Bitmap bm) {
        File tmDir = new File(Environment.getExternalStorageDirectory() + "/com.stark.yiyu.UIactivity");
        if (!tmDir.exists()) {
            tmDir.mkdir();
        }
        File img = new File(tmDir.getAbsolutePath() + "photo_upload.png");
        try {
            FileOutputStream fos = new FileOutputStream(img);
            bm.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.flush();
            fos.close();
            return Uri.fromFile(img);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private void startImageZoom(Uri uri) {//裁剪
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");//数据和类型
        intent.putExtra("crop", "true");//开启的Intent 显示View是可裁减的
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        intent.putExtra("outputX", 150);
        intent.putExtra("outputY", 150);//裁剪的图片的宽高。最终得到的输出图片的宽高
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
            return saveBitmap(bitmap);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public Bitmap toRoundBitmap(Bitmap bitmap) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        float roundPx = 0.0f;
        float left, top, right, bottom, dst_left, dst_top, dst_right, dst_bottom;
        if (width <= height) {
            roundPx = width / 2;
            left = 0;
            top = 0;
            right = width;
            bottom = width;
            height = width;
            dst_left = 0;
            dst_top = 0;
            dst_right = width;
            dst_bottom = width;
        } else {
            roundPx = height / 2;
            float clip = (width - height) / 2;
            left = clip;
            right = width - clip;
            top = 0;
            bottom = height;
            width = height;
            dst_left = 0;
            dst_top = 0;
            dst_right = height;
            dst_bottom = height;
        }

        Bitmap output = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect src = new Rect((int) left, (int) top, (int) right, (int) bottom);
        final Rect dst = new Rect((int) dst_left, (int) dst_top, (int) dst_right, (int) dst_bottom);
        //final RectF rectF = new RectF(dst);

        paint.setAntiAlias(true);//设置画笔无锯齿

        canvas.drawARGB(0, 0, 0, 0);//填充整个Canvas
        paint.setColor(color);

        //两种方法画圆,drawRounRect和drawCircle
        // canvas.drawRoundRect(rectF, roundPx, roundPx, paint);// 画圆角矩形，第一个参数为图形显示区域，第二个参数和第三个参数分别是水平圆角半径和垂直圆角半径。
        canvas.drawCircle(roundPx, roundPx, roundPx, paint);

        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));// 设置两张图片相交时的模式,参考http://trylovecatch.iteye.com/blog/1189452
        canvas.drawBitmap(bitmap, src, dst, paint);//以Mode.SRC_IN模式合并bitmap和已经draw了的Circle
        return output;
    }

    private void sendImage(Bitmap bm) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.PNG, 60, stream);
        byte[] bytes = stream.toByteArray();
        String img = new String(Base64.encodeToString(bytes, Base64.DEFAULT));
        /**
         * 发送到服务器
         */
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CAMERA_REQUEST_CODE) {
            if (data == null) {
                return;
            } else {
                Bundle extras = data.getExtras();
                if (extras != null) {
                    Bitmap bm = extras.getParcelable("data");
                    Uri uri = saveBitmap(bm);
                    startImageZoom(uri);
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
            Bitmap roundbm = toRoundBitmap(bm);//将图片裁减为圆形
            sendImage(roundbm);
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

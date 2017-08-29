package com.stark.wallwallchat.UIactivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.TextView;

import com.stark.wallwallchat.CustomDialog.MyDateDialog;
import com.stark.wallwallchat.Format.UserInfo;
import com.stark.wallwallchat.Listview.ElasticListView;
import com.stark.wallwallchat.MyService;
import com.stark.wallwallchat.R;
import com.stark.wallwallchat.SQLite.DatabaseHelper;
import com.stark.wallwallchat.adapter.MyAdapter;
import com.stark.wallwallchat.bean.BaseItem;
import com.stark.wallwallchat.bean.ItemEditInfo;
import com.stark.wallwallchat.bean.ItemEditInfo2;
import com.stark.wallwallchat.bean.ItemEditMail;
import com.stark.wallwallchat.bean.ItemMargin;

import java.util.ArrayList;

/**
 * Created by asus on 2017/7/7.
 */
public class EditInfoActivity extends Activity {

    ElasticListView listView;
    ArrayList<BaseItem> mArrays;
    MyAdapter adapter;

    private static int RES_AUTO_CODE = 0;
    private static int RES_NOTE_CODE = 1;
    UserInfo User;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edtinfo);
        ImageButton left = (ImageButton) findViewById(R.id.button_transfer_title_left);
        TextView title = (TextView) findViewById(R.id.text_transfer_title);
        left.setBackgroundResource(R.drawable.title_back);
        Button right=(Button)findViewById(R.id.button_transfer_title_right);
        left.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        title.setText("编辑资料");
        right.setText("完成");

        listView = (ElasticListView) findViewById(R.id.listView_edtinfo);
        mArrays = new ArrayList<>();
        adapter = new MyAdapter(EditInfoActivity.this, mArrays);
        listView.setAdapter(adapter);

        User=new UserInfo();
        SQLiteDatabase db=new DatabaseHelper(EditInfoActivity.this).getWritableDatabase();
        Cursor cr=db.query("userdata", null, "id=?", new String[]{getSharedPreferences("action",MODE_PRIVATE).getString("id",null)}, null, null, null);
        if(cr!=null&&cr.getCount()>0&&cr.moveToNext()){
            User.Id=cr.getString(0);
            User.Nick=cr.getString(1);
            User.Auto=cr.getString(2);
            User.Sex=cr.getInt(3);
            User.Birth=cr.getString(4);
            User.College=cr.getString(5);
            User.Edu=cr.getString(6);
            User.Mail=cr.getString(7);
            User.Pnumber=cr.getString(8);
            User.Catdate=cr.getInt(10);
            User.Typeface=cr.getInt(11);
            User.Theme=cr.getInt(12);
            User.Bubble=cr.getInt(13);
        }
        getSharedPreferences("action",MODE_PRIVATE).edit().putString("nick",User.Nick).putString("mail",User.Mail).apply();
        refreshAdapter();
        adapter.notifyDataSetChanged();
        listView.setOnItemClickListener(new MyOnItemClickListener());
        right.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(EditInfoActivity.this, MyService.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("user", User);
                intent.putExtra("CMD","Change");
                intent.putExtras(bundle);
                startService(intent);
                finish();
            }
        });
    }

    private class MyOnItemClickListener implements AdapterView.OnItemClickListener {
        Intent it = null;
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            switch (position) {
                case 1:
                    it = new Intent(EditInfoActivity.this, AutoActivity.class);
                    it.putExtra("auto", User.Auto);
                    startActivityForResult(it,RES_AUTO_CODE);
                    break;
                case 3:
                    new AlertDialog.Builder(EditInfoActivity.this).setItems(new String[]{"男", "女"}, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            User.Sex=which;
                            refreshAdapter();
                            dialog.dismiss();
                        }
                    }).show();
                    break;
                case 4://birthday = "1997-2-22";
                    //Calendar now = Calendar.getInstance();
                    if(User.Birth==null||User.Birth.equals("")){
                        User.Birth="1980-01-01";
                    }
                    int year =Integer.parseInt(User.Birth.substring(0, 4));
                    int monthOfYear =Integer.parseInt(User.Birth.substring(5,7))-1;
                    int dayOfMonth =Integer.parseInt(User.Birth.substring(8,10));

                    MyDateDialog myDateDialog = new MyDateDialog(EditInfoActivity.this, android.R.style.Theme_Holo_Dialog_NoActionBar, new MyDateDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                            int month = monthOfYear + 1;
                            User.Birth = String.format("%04d",year) + "-" + String.format("%02d",month) + "-" + String.format("%02d",dayOfMonth);
                            refreshAdapter();
                        }
                    }, year, monthOfYear, dayOfMonth);
                    myDateDialog.myShow();

                    WindowManager windowManager = getWindowManager();
                    Display display = windowManager.getDefaultDisplay();
                    WindowManager.LayoutParams lp = myDateDialog.getWindow().getAttributes();
                    lp.width = (int) (display.getWidth() * 0.8);
                    myDateDialog.getWindow().setAttributes(lp);
                    break;
                case 6:
                    it = new Intent(EditInfoActivity.this, SortSchool.class);
                    startActivityForResult(it, 123);
                    break;
            }
        }
    }

    private void refreshAdapter() {
        mArrays.clear();
        mArrays.add(new ItemEditInfo2(9, "昵称", User.Nick));
        mArrays.add(new ItemEditInfo(7, "签名", User.Auto));
        mArrays.add(new ItemMargin(8));//设置间隔空格.
        mArrays.add(new ItemEditInfo(7, "性别", User.Sex==0?"男":"女"));
        mArrays.add(new ItemEditInfo(7, "生日", User.Birth));
        mArrays.add(new ItemMargin(8));//设置间隔空格.
        mArrays.add(new ItemEditInfo(7, "学校", User.College));
        mArrays.add(new ItemEditMail(10, "邮箱", User.Mail));
        adapter.notifyDataSetChanged();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 123) {
            if (resultCode == 666) {
                User.College = data.getStringExtra("college");
                User.Edu=data.getStringExtra("edu");
                refreshAdapter();
            }
        } else if (requestCode == RES_AUTO_CODE) {
            if (resultCode == 4) {
                User.Auto = data.getStringExtra("auto");
                Log.e("EditInfoActivity", User.Auto);
                refreshAdapter();
            }
        }
    }
//    @Override
//    public void onBackPressed() {
//        /**
//         * 保存个人信息
//         */
//
//
//
//        super.onBackPressed();
//    }
}

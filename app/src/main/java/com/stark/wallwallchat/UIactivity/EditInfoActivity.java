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
import com.stark.wallwallchat.Listview.ElasticListView;
import com.stark.wallwallchat.MyService;
import com.stark.wallwallchat.NetWork.NetPackage;
import com.stark.wallwallchat.R;
import com.stark.wallwallchat.SQLite.DatabaseHelper;
import com.stark.wallwallchat.adapter.MyAdapter;
import com.stark.wallwallchat.bean.BaseItem;
import com.stark.wallwallchat.bean.ItemEditInfo;
import com.stark.wallwallchat.bean.ItemEditInfo2;
import com.stark.wallwallchat.bean.ItemEditMail;
import com.stark.wallwallchat.bean.ItemMargin;
import com.stark.wallwallchat.json.JsonConvert;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by asus on 2017/7/7.
 */
public class EditInfoActivity extends Activity {

    ElasticListView listView;
    ArrayList<BaseItem> mArrays;
    MyAdapter adapter;

    final HashMap<String,Object> SqlPkg=new HashMap<String,Object>();
    private static int RES_AUTO_CODE = 0;
    private static int RES_NOTE_CODE = 1;

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

        SQLiteDatabase db=new DatabaseHelper(EditInfoActivity.this).getWritableDatabase();
        Cursor cr=db.query("userdata", null, "id=?", new String[]{getSharedPreferences("action",MODE_PRIVATE).getString("id",null)}, null, null, null);
        if(cr!=null&&cr.getCount()>0&&cr.moveToNext()){
            SqlPkg.put("id", cr.getString(cr.getColumnIndex("id")));
            SqlPkg.put("nick",cr.getString(cr.getColumnIndex("nick")));
            SqlPkg.put("auto",cr.getString(cr.getColumnIndex("auto")));
            SqlPkg.put("sex",cr.getString(cr.getColumnIndex("sex")));
            SqlPkg.put("birth", cr.getString(cr.getColumnIndex("birth")));
            SqlPkg.put("college",cr.getString(cr.getColumnIndex("college")));
            SqlPkg.put("edu",cr.getString(cr.getColumnIndex("edu")));
            SqlPkg.put("mail",cr.getString(cr.getColumnIndex("mail")));
            SqlPkg.put("pnumber",cr.getString(cr.getColumnIndex("pnumber")));
            SqlPkg.put("catdate",cr.getString(cr.getColumnIndex("catdate")));
            SqlPkg.put("typeface",cr.getString(cr.getColumnIndex("typeface")));
            SqlPkg.put("bubble",cr.getString(cr.getColumnIndex("bubble")));
            SqlPkg.put("theme",cr.getString(cr.getColumnIndex("theme")));
        }
        getSharedPreferences("action",MODE_PRIVATE).edit().putString("nick", (String) SqlPkg.get("nick")).putString("mail", (String) SqlPkg.get("mail")).apply();
        refreshAdapter();
        adapter.notifyDataSetChanged();
        listView.setOnItemClickListener(new MyOnItemClickListener());
        right.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(EditInfoActivity.this, MyService.class);
                SqlPkg.put("nick", getSharedPreferences("action",MODE_PRIVATE).getString("nick",(String)SqlPkg.get("nick")));
                SqlPkg.put("mail", getSharedPreferences("action",MODE_PRIVATE).getString("mail",(String)SqlPkg.get("mail")));
                    try {
                    NetPackage data = new NetPackage(EditInfoActivity.this, SqlPkg);
                    intent.putExtra("CMD", "Change");
                    intent.putExtra("data", JsonConvert.SerializeObject(data.ChangeUser()));
                    startService(intent);
                    finish();
                }catch (Exception e){
                        Log.e("change",e.toString());
                }
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
                    it.putExtra("auto",  (String) SqlPkg.get("auto"));
                    startActivityForResult(it,RES_AUTO_CODE);
                    break;
                case 3:
                    new AlertDialog.Builder(EditInfoActivity.this).setItems(new String[]{ "女","男"}, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            SqlPkg.put("sex",which+"");
                            refreshAdapter();
                            dialog.dismiss();
                        }
                    }).show();
                    break;
                case 4://birthday = "1997-2-22";
                    //Calendar now = Calendar.getInstance();
                    String temp=(String)SqlPkg.get("birth");
                    if(temp==null||temp.equals("")){
                        temp="1990-01-01";
                    }
                    int year =Integer.parseInt(temp.substring(0, 4));
                    int monthOfYear =Integer.parseInt(temp.substring(5,7))-1;
                    int dayOfMonth =Integer.parseInt(temp.substring(8,10));

                    MyDateDialog myDateDialog = new MyDateDialog(EditInfoActivity.this, android.R.style.Theme_Holo_Dialog_NoActionBar, new MyDateDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                            int month = monthOfYear + 1;
                            SqlPkg.put("birth", String.format("%04d",year) + "-" + String.format("%02d",month) + "-" + String.format("%02d",dayOfMonth));
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
        mArrays.add(new ItemEditInfo2(9, "昵称", (String) SqlPkg.get("nick")));
        mArrays.add(new ItemEditInfo(7, "签名", (String) SqlPkg.get("auto")));
        mArrays.add(new ItemMargin(8));//设置间隔空格.
        mArrays.add(new ItemEditInfo(7, "性别", SqlPkg.get("sex")==null?null:SqlPkg.get("sex").equals("0")?"女":"男"));
        mArrays.add(new ItemEditInfo(7, "生日",  (String)SqlPkg.get("birth")));
        mArrays.add(new ItemMargin(8));//设置间隔空格.
        mArrays.add(new ItemEditInfo(7, "学校",  (String)SqlPkg.get("college")));
        mArrays.add(new ItemEditMail(10, "邮箱",  (String)SqlPkg.get("mail")));
        adapter.notifyDataSetChanged();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 123) {
            if (resultCode == 666) {
                SqlPkg.put("college",data.getStringExtra("college"));
                SqlPkg.put("edu",data.getStringExtra("edu"));
                refreshAdapter();
            }
        } else if (requestCode == RES_AUTO_CODE) {
            if (resultCode == 4) {
                SqlPkg.put("auto", data.getStringExtra("auto"));
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

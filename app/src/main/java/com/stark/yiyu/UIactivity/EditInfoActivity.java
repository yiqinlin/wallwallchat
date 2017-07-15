package com.stark.yiyu.UIactivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.stark.yiyu.CustomDialog.MyDateDialog;
import com.stark.yiyu.Listview.ElasticListView;
import com.stark.yiyu.R;
import com.stark.yiyu.Util.Status;
import com.stark.yiyu.adapter.MyAdapter;
import com.stark.yiyu.bean.BaseItem;
import com.stark.yiyu.bean.ItemEditInfo;
import com.stark.yiyu.bean.ItemEditInfo2;
import com.stark.yiyu.bean.ItemEditMail;
import com.stark.yiyu.bean.ItemMargin;
import com.stark.yiyu.bean.ItemSimpleList;

import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by asus on 2017/7/7.
 */
public class EditInfoActivity extends Activity {

    ElasticListView listView;
    ArrayList<BaseItem> mArrays;
    MyAdapter adapter;

    private static int RES_AUTO_CODE = 0;

    private Drawable imgHead = null;
    private String nick = null;
    private String auto = null;
    private String sex = null;
    private String birthday = null;
    private String school = null;
    private String address = null;
    private String hometown = null;
    private String mail = null;
    private String selfInfo = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edtinfo);
        Status.setTranslucentStatus(getWindow(), this, (LinearLayout) findViewById(R.id.transfer_title_status));
        ImageButton left = (ImageButton) findViewById(R.id.button_transfer_title_left);
        TextView title = (TextView) findViewById(R.id.text_transfer_title);
        left.setBackgroundResource(R.drawable.title_back);
        left.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        title.setText("编辑资料");

        listView = (ElasticListView) findViewById(R.id.listView_edtinfo);
        mArrays = new ArrayList<>();
        adapter = new MyAdapter(EditInfoActivity.this, mArrays);
        listView.setAdapter(adapter);

        imgHead = getResources().getDrawable(R.drawable.tianqing);
        nick = "哈库纳玛塔塔";
        auto = "踏破虚空无一事，涅槃生死绝安排";
        sex = "男";
        birthday = "1997-2-22";
        school = "成都东软学院";
        address = "中国";
        hometown = "成都";
        mail = "919664295@qq.com";
        selfInfo = "人必自侮,而后人侮之。";

        refreshAdapter();
        adapter.notifyDataSetChanged();
        listView.setOnItemClickListener(new MyOnItemClickListener());

    }

    private class MyOnItemClickListener implements AdapterView.OnItemClickListener {
        Intent it = null;
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            switch (position) {
                case 0:
                    Toast.makeText(EditInfoActivity.this, "" + position, Toast.LENGTH_SHORT).show();
                    break;
                case 3:
                    it = new Intent(EditInfoActivity.this, AutoActivity.class);
                    startActivityForResult(it,RES_AUTO_CODE);
                    break;
                case 5:
                    new AlertDialog.Builder(EditInfoActivity.this).setItems(new String[]{"男", "女"}, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if (which == 0) {
                                sex = "男";
                            } else {
                                sex = "女";
                            }
                            refreshAdapter();
                            dialog.dismiss();
                        }
                    }).show();
                    break;
                case 6:
                    Calendar now = Calendar.getInstance();
                    int year = now.get(Calendar.YEAR);
                    int monthOfYear = now.get(Calendar.MONTH);
                    int dayOfMonth = now.get(Calendar.DAY_OF_MONTH);

                    MyDateDialog myDateDialog = new MyDateDialog(EditInfoActivity.this, android.R.style.Theme_Holo_Dialog_NoActionBar
                            , new MyDateDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                            int month = monthOfYear + 1;
                            birthday = year + "-" + month + "-" + dayOfMonth;
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
                case 8:
                    it = new Intent(EditInfoActivity.this, SortSchool.class);
                    startActivityForResult(it, 123);
                    break;
            }
        }
    }

    private void refreshAdapter() {
        mArrays.clear();
        mArrays.add(new ItemSimpleList(6, "头像", imgHead));
        mArrays.add(new ItemMargin(8));//设置间隔空格.
        mArrays.add(new ItemEditInfo2(9, "昵称", nick));
        mArrays.add(new ItemEditInfo(7, "签名", auto));
        mArrays.add(new ItemMargin(8));//设置间隔空格.
        mArrays.add(new ItemEditInfo(7, "性别", sex));
        mArrays.add(new ItemEditInfo(7, "生日", birthday));
        mArrays.add(new ItemMargin(8));//设置间隔空格.
        mArrays.add(new ItemEditInfo(7, "学校", school));
        mArrays.add(new ItemEditInfo(7, "所在地", address));
        mArrays.add(new ItemEditInfo(7, "家乡", hometown));
        mArrays.add(new ItemEditMail(10, "邮箱", mail));
        mArrays.add(new ItemMargin(8));//设置间隔空格.
        mArrays.add(new ItemEditInfo(7, "个人说明", selfInfo));
        adapter.notifyDataSetChanged();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 123) {
            if (resultCode == 666) {
                school = data.getStringExtra("college");
                Log.i("college", "college = " + school);
                refreshAdapter();
            } else if (resultCode == RES_AUTO_CODE) {
                auto = data.getStringExtra("auto");
                refreshAdapter();
            }
        }
    }
}

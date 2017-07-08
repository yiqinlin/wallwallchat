package com.stark.yiyu.UIactivity;

import android.app.Activity;
import android.content.Intent;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edtinfo);
        Status.setTranslucentStatus(getWindow(), this, (LinearLayout) findViewById(R.id.transfer_title_status));
        ImageButton left = (ImageButton) findViewById(R.id.button_transfer_title_left);
        TextView title = (TextView) findViewById(R.id.text_transfer_title);
        Button right = (Button) findViewById(R.id.button_transfer_title_right);
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

        mArrays.add(new ItemSimpleList(6, "头像", getResources().getDrawable(R.drawable.tianqing)));
        mArrays.add(new ItemMargin(8));//设置间隔空格.
        mArrays.add(new ItemEditInfo(7, "昵称", "哈库纳玛塔塔"));
        mArrays.add(new ItemEditInfo(7, "签名", "踏破虚空无一事,涅槃生死绝安排"));
        mArrays.add(new ItemMargin(8));//设置间隔空格.
        mArrays.add(new ItemEditInfo(7, "性别", "男"));
        mArrays.add(new ItemEditInfo(7, "生日", "1997-2-22"));
        mArrays.add(new ItemMargin(8));//设置间隔空格.
        mArrays.add(new ItemEditInfo(7, "学校", "成都东软"));
        mArrays.add(new ItemEditInfo(7, "所在地", "中国"));
        mArrays.add(new ItemEditInfo(7, "家乡", ""));
        mArrays.add(new ItemEditInfo(7, "邮箱", ""));
        mArrays.add(new ItemMargin(8));//设置间隔空格.
        mArrays.add(new ItemEditInfo(7, "个人说明", "人必自侮，而后人侮之。"));
        adapter.notifyDataSetChanged();
        listView.setOnItemClickListener(new MyOnItemClickListener());

    }

    private class MyOnItemClickListener implements AdapterView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            switch (position) {
                case 0:
                    Toast.makeText(EditInfoActivity.this, "" + position, Toast.LENGTH_SHORT).show();
                    break;
                case 1:
                    break;
                case 6:
                    Calendar now = Calendar.getInstance();
                    int year = now.get(Calendar.YEAR);
                    int monthOfYear = now.get(Calendar.MONTH);
                    int dayOfMonth = now.get(Calendar.DAY_OF_MONTH);

                    Log.d("Shanks", "1");
                    MyDateDialog myDateDialog = new MyDateDialog(EditInfoActivity.this, android.R.style.Theme_Holo_Dialog_NoActionBar
                            , new MyDateDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {

                            int month = monthOfYear + 1;
                            Toast.makeText(EditInfoActivity.this, "Shanks", Toast.LENGTH_SHORT).show();

                            Log.d("生日:", year + "-" + month + "-" + dayOfMonth);
                            mArrays.clear();
                            mArrays.add(new ItemSimpleList(6, "头像", getResources().getDrawable(R.drawable.tianqing)));
                            mArrays.add(new ItemMargin(8));//设置间隔空格.
                            mArrays.add(new ItemEditInfo(7, "昵称", "哈库纳玛塔塔"));
                            mArrays.add(new ItemEditInfo(7, "签名", "踏破虚空无一事,涅槃生死绝安排"));
                            mArrays.add(new ItemMargin(8));//设置间隔空格.
                            mArrays.add(new ItemEditInfo(7, "性别", "男"));
                            mArrays.add(new ItemEditInfo(7, "生日", year + "-" + month + "-" + dayOfMonth));
                            mArrays.add(new ItemMargin(8));//设置间隔空格.
                            mArrays.add(new ItemEditInfo(7, "学校", "成都东软"));
                            mArrays.add(new ItemEditInfo(7, "所在地", "中国"));
                            mArrays.add(new ItemEditInfo(7, "家乡", ""));
                            mArrays.add(new ItemEditInfo(7, "邮箱", ""));
                            mArrays.add(new ItemMargin(8));//设置间隔空格.
                            mArrays.add(new ItemEditInfo(7, "个人说明", "人必自侮，而后人侮之。"));
                            adapter.notifyDataSetChanged();
                        }
                    }, year, monthOfYear, dayOfMonth);
                    myDateDialog.myShow();

                    WindowManager windowManager = getWindowManager();
                    Display display = windowManager.getDefaultDisplay();
                    WindowManager.LayoutParams lp = myDateDialog.getWindow().getAttributes();
                    lp.width = (int) (display.getWidth() * 0.8);
                    myDateDialog.getWindow().setAttributes(lp);
                    break;
            }
        }
    }

}

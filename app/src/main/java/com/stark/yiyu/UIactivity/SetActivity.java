package com.stark.yiyu.UIactivity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.stark.yiyu.Listview.ElasticListView;
import com.stark.yiyu.R;
import com.stark.yiyu.Util.Status;
import com.stark.yiyu.adapter.MyAdapter;
import com.stark.yiyu.bean.BaseItem;
import com.stark.yiyu.bean.ItemSimpleList;

import java.util.ArrayList;

public class SetActivity extends Activity {
    private SharedPreferences sp=null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set);
        Status.setTranslucentStatus(getWindow(), this, (LinearLayout) findViewById(R.id.transfer_title_status));
        ImageButton left=(ImageButton)findViewById(R.id.button_transfer_title_left);
        TextView title=(TextView)findViewById(R.id.text_transfer_title);
        Button right=(Button)findViewById(R.id.button_transfer_title_right);
        left.setBackgroundResource(R.drawable.title_back);
        title.setText("设 置");
        right.setText("完成");
        sp = SetActivity.this.getSharedPreferences("action", MODE_PRIVATE);
        ElasticListView listView=(ElasticListView)findViewById(R.id.listView_set);
        ArrayList<BaseItem> mArrays=new ArrayList<BaseItem>();
        MyAdapter adapter=new MyAdapter(SetActivity.this,mArrays);
        listView.setAdapter(adapter);
        mArrays.add(new ItemSimpleList(6, "退出当前账号", getResources().getDrawable(R.drawable.tianqing)));
        adapter.notifyDataSetChanged();
        listView.setOnItemClickListener(new MyOnItemClickListener());
        right.setOnClickListener(Click);
    }
    private class MyOnItemClickListener implements AdapterView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            switch(position){
                case 0:
                    Intent intent=new Intent(SetActivity.this,Login.class);
                    startActivity(intent);
                    sp.edit().putBoolean("state",false).apply();
                    TransferActivity.This.finish();
                    finish();
                    overridePendingTransition(R.anim.anim_null,R.anim.anim_null);
                    break;
            }
        }
    }
    View.OnClickListener Click=new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.button_transfer_title_left:
                    finish();
                    break;
                case R.id.button_transfer_title_right:
                    Intent intent=new Intent();
                    setResult(2, intent);
                    finish();
                    break;
                default:
                    break;
            }

        }
    };
}

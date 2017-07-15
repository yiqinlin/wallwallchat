package com.stark.yiyu.UIactivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.stark.yiyu.File.ImgStorage;
import com.stark.yiyu.Listview.MyListView;
import com.stark.yiyu.R;
import com.stark.yiyu.adapter.MyAdapter;
import com.stark.yiyu.bean.BaseItem;
import com.stark.yiyu.bean.ItemTextSeparate;
import com.stark.yiyu.bean.ItemWallInfo;

import java.util.ArrayList;

public class WallMsgActivity extends Activity {
    ArrayList<BaseItem> mArrays=null;
    MyAdapter adapter=null;
    String SrcID;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wall_msg);
        Intent intent=getIntent();
        ImageButton left=(ImageButton)findViewById(R.id.button_transfer_title_left);
        TextView mid=(TextView)findViewById(R.id.text_transfer_title);
        Button right=(Button)findViewById(R.id.button_transfer_title_right);
        final MyListView listView=(MyListView)findViewById(R.id.listView_wall_detail);
        final SharedPreferences sp = getSharedPreferences("action", Context.MODE_PRIVATE);
        mid.setText("详 情");
        right.setText("更多");
        left.setBackgroundResource(R.drawable.title_back);
        left.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        SrcID=sp.getString("id",null);
        mArrays=new ArrayList<BaseItem>();
        mArrays.add(new ItemWallInfo(intent.getStringExtra("nick")==null?12:11,intent.getIntExtra("type",0),intent.getStringExtra("id"),intent.getStringExtra("msgcode"), ImgStorage.getHead(this),intent.getStringExtra("nick"),intent.getStringExtra("time"),intent.getStringExtra("content"),intent.getStringExtra("cnum"),intent.getStringExtra("anum")));
        mArrays.add(new ItemTextSeparate(13,"Comment"));
        adapter=new MyAdapter(this,mArrays);
        listView.setAdapter(adapter);
        //listView.setOnItemClickListener(new MyOnItemClickListener());
        listView.setonRefreshListener(new MyListView.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new AsyncTask<Void, Void, Void>() {
                    @Override
                    protected Void doInBackground(Void... params) {

                        return null;
                    }

                    protected void onPostExecute(Void result) {
                        adapter.notifyDataSetChanged();
                        listView.onRefreshComplete();
                    }
                }.execute();
            }
        });

    }
}

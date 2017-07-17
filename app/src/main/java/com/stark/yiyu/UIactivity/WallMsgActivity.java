package com.stark.yiyu.UIactivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.stark.yiyu.File.ImgStorage;
import com.stark.yiyu.Format.Refresh;
import com.stark.yiyu.Listview.MyListView;
import com.stark.yiyu.MyService;
import com.stark.yiyu.NetWork.NetPackage;
import com.stark.yiyu.NetWork.NetSocket;
import com.stark.yiyu.R;
import com.stark.yiyu.Util.DateUtil;
import com.stark.yiyu.adapter.MyAdapter;
import com.stark.yiyu.bean.BaseItem;
import com.stark.yiyu.bean.ItemTextSeparate;
import com.stark.yiyu.bean.ItemWallInfo;
import com.stark.yiyu.json.JsonConvert;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class WallMsgActivity extends Activity {
    ArrayList<BaseItem> mArrays=null;
    MyAdapter adapter=null;
    String SrcID;
    String DesID;
    MyListView listView;
    String MsgCode;
    EditText input;
    boolean isComment;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wall_msg);
        final Intent intent=getIntent();
        ImageButton left=(ImageButton)findViewById(R.id.button_transfer_title_left);
        TextView mid=(TextView)findViewById(R.id.text_transfer_title);
        final Button right=(Button)findViewById(R.id.button_transfer_title_right);
        final Button send=(Button)findViewById(R.id.button_send);
        input=(EditText)findViewById(R.id.edit_send);
        final SharedPreferences sp = getSharedPreferences("action", Context.MODE_PRIVATE);
        listView=(MyListView)findViewById(R.id.listView_wall_detail);
        mid.setText("详 情");
        right.setText("筛选");
        left.setBackgroundResource(R.drawable.title_back);
        left.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        SrcID=sp.getString("id", null);
        DesID=intent.getStringExtra("sponsor");
        MsgCode=intent.getStringExtra("msgcode");
        isComment=intent.getBooleanExtra("isComment",false);
        mArrays=new ArrayList<BaseItem>();
        adapter=new MyAdapter(this,mArrays);
        listView.setAdapter(adapter);
        MyAsyncTask asyncTask=new MyAsyncTask();
        asyncTask.execute();
        listView.setOnItemClickListener(new MyOnItemClickListener());
        listView.setonRefreshListener(new MyListView.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new MyAsyncTask().execute();
            }
        });
        send.setClickable(false);
        send.setBackgroundResource(R.drawable.gray_button);
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(WallMsgActivity.this, MyService.class);
                intent.putExtra("CMD","Comment");
                intent.putExtra("msg",input.getText().toString());
                intent.putExtra("msgcode",MsgCode);
                intent.putExtra("receiver",DesID);
                intent.putExtra("mode",0);
                intent.putExtra("type", isComment ? 1 : 0);
                startService(intent);
                mArrays.add(new ItemWallInfo(13, 0, SrcID, DesID, DateUtil.getMsgCode(WallMsgActivity.this), ImgStorage.getHead(WallMsgActivity.this), sp.getString("nick",""), null, new SimpleDateFormat("mm:ss", Locale.CHINA).format(new Date()),input.getText().toString(),"0","0"));
                input.setText(null);

            }
        });
        input.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence text, int start, int before, int count) {
                //text  输入框中改变后的字符串信息 start 输入框中改变后的字符串的起始位置 before 输入框中改变前的字符串的位置 默认为0 count 输入框中改变后的一共输入字符串的数量
                if(count==0){
                    send.setClickable(false);
                    send.setBackgroundResource(R.drawable.gray_button);
                }else{
                    send.setClickable(true);
                    send.setBackgroundResource(R.drawable.big_button);
                }
            }
            @Override
            public void beforeTextChanged(CharSequence text, int start, int count, int after) {
                //text  输入框中改变前的字符串信息 start 输入框中改变前的字符串的起始位置 count 输入框中改变前后的字符串改变数量一般为0 after 输入框中改变后的字符串与起始位置的偏移量
            }
            @Override
            public void afterTextChanged(Editable edit) {
                //edit  输入结束呈现在输入框中的信息
            }
        });
    }
    class MyAsyncTask extends AsyncTask<Void, Integer, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }
        @Override
        protected Void doInBackground(Void...values) {
            Refresh refresh=(Refresh)NetPackage.getBag(NetSocket.request(NetPackage.Refresh(SrcID, "nsu", 0, isComment?4:3, 1,MsgCode,2)));
            mArrays.clear();
            JsonConvert.UpdateWall(WallMsgActivity.this, refresh, mArrays);
            mArrays.add(new ItemTextSeparate(15, "Comments"));
            publishProgress(1);
            refresh=(Refresh)NetPackage.getBag(NetSocket.request(NetPackage.Refresh(SrcID, "nsu", 0, isComment?6:5, 1,MsgCode,2)));
            JsonConvert.UpdateComment(WallMsgActivity.this, refresh, mArrays);
            publishProgress(2);
            return null;
        }
        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            adapter.notifyDataSetChanged();
            if(values[0]==2){
                listView.onRefreshComplete();
            }
        }
    }
    private class MyOnItemClickListener implements AdapterView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            if (!isComment){
                ItemWallInfo msg = (ItemWallInfo) mArrays.get(position - 1);
                Intent intent = new Intent(WallMsgActivity.this, WallMsgActivity.class);
                intent.putExtra("isComment", true);
                 intent.putExtra("sponsor", msg.getId());
                intent.putExtra("msgcode", msg.getMsgcode());
                startActivity(intent);
             }else{

            }
        }
    }
}

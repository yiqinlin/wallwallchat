package com.stark.wallwallchat.UIactivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.stark.wallwallchat.Listview.MyListView;
import com.stark.wallwallchat.MyService;
import com.stark.wallwallchat.NetWork.NetBuilder;
import com.stark.wallwallchat.NetWork.NetPackage;
import com.stark.wallwallchat.NetWork.NetSocket;
import com.stark.wallwallchat.R;
import com.stark.wallwallchat.adapter.MyAdapter;
import com.stark.wallwallchat.bean.BaseItem;
import com.stark.wallwallchat.bean.ItemTextSeparate;
import com.stark.wallwallchat.bean.ItemWallInfo;
import com.stark.wallwallchat.json.JsonConvert;

import java.util.ArrayList;
import java.util.HashMap;

public class WallMsgActivity extends Activity {
    ArrayList<BaseItem> mArrays=null;
    MyAdapter adapter=null;
    MyListView listView;
    String MsgCode;
    EditText input;
    boolean isComment;
    String DesId;
    int type=1;
    int mode=0;
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
        left.setBackgroundResource(R.drawable.title_back);
        left.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        isComment=intent.getBooleanExtra("isComment",false);
        DesId=intent.getStringExtra("sponsor");
        MsgCode=intent.getStringExtra("msgcode");
        if(isComment){
            type=2;
        }
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
                intent.putExtra("msgcode3",MsgCode);
                intent.putExtra("receiver",DesId);
                intent.putExtra("mode", mode);
                intent.putExtra("type", type);
                startService(intent);
                //mArrays.add(new ItemWallInfo(13, 0, SrcID, DesID, DateUtil.getMsgCode(WallMsgActivity.this), ImgStorage.getHead(WallMsgActivity.this), sp.getString("nick", ""), null, new SimpleDateFormat("mm:ss", Locale.CHINA).format(new Date()), input.getText().toString(), "0", "0",false));
                input.setText(null);
            }
        });
        if(intent.getBooleanExtra("input",false)){
            input.requestFocus();
        }
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
            try {
                    HashMap<String,Object> SqlPkg=new HashMap<String,Object>();
                    SqlPkg.put("mode",isComment?4:3); //0非匿名，1匿名，2所有，3墙指定消息，4评论指定消息，5带回复，6不带回复
                    SqlPkg.put("start",0);
                    SqlPkg.put("msgcode",MsgCode);
                    NetPackage netPkg=new NetPackage(WallMsgActivity.this,SqlPkg);
                    String temp = JsonConvert.SerializeObject(netPkg.WRefresh(0));
                    String result;
                    try{
                        result=NetSocket.request(temp);
                    }catch (Exception e){
                        publishProgress(-1);
                        return null;
                    }
                    Log.e("request", temp);
                    Log.e("result", result);
                    NetBuilder out = (NetBuilder) JsonConvert.DeserializeObject(result, new NetBuilder());
                    if(out.getBool("flag", false)) {
                        mArrays.clear();
                        out.UpdateWall(WallMsgActivity.this, mArrays);
                        mArrays.add(new ItemTextSeparate(15, "Comments"));
                        publishProgress(1);
                        SqlPkg.put("mode", isComment ? 6 : 5); //0非匿名，1匿名，2所有，3墙指定消息，4评论指定消息，5不带回复，6带回复
                        temp = JsonConvert.SerializeObject(netPkg.WRefresh(6));
                        try{
                            result=NetSocket.request(temp);
                        }catch (Exception e){
                            publishProgress(-1);
                            return null;
                        }
                        Log.e("request", temp);
                        Log.e("result", result);
                        out = new NetBuilder(result);
                        out.UpdateComment(WallMsgActivity.this,mArrays);
                        publishProgress(2);
                    }
                    else {
                        publishProgress(out.getInt("error", -2));/**告诉UI线程 更新*/
                    }
                    //没网或超时
                    //SelectionTemp=AddList();
            } catch (Exception e) {
                publishProgress(7);
                e.printStackTrace();
            }
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
            if(position!=1&&position!=2) {
                if (!isComment) {
                    ItemWallInfo msg = (ItemWallInfo) mArrays.get(position - 1);
                    Intent intent = new Intent(WallMsgActivity.this, WallMsgActivity.class);
                    intent.putExtra("isComment", true);
                    intent.putExtra("sponsor", msg.getId());
                    intent.putExtra("msgcode", msg.getMsgcode());
                    startActivity(intent);
                } else {
                    ItemWallInfo msg = (ItemWallInfo) mArrays.get(position - 1);
                    input.setHint("回复:"+msg.getNick());
                    mode=2;
                    type=3;
                }
            }else if(position==1){
                input.setHint(null);
                mode=0;
                type=isComment?2:1;
            }
        }
    }
}

package com.stark.yiyu.UIactivity;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
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

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class HomepageActivity extends Activity {

    private String SrcID=null;
    private String DesId=null;
    private String Nick=null;
    private String Auto=null;
    private ArrayList<BaseItem> mArrays=null;
    private MyAdapter adapter=null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Status.setTranslucentStatus(getWindow());
        setContentView(R.layout.activity_homepage);
        Button get=(Button)findViewById(R.id.button_homepage_left);
        Button send=(Button)findViewById(R.id.button_homepage_right);
        Intent intent=getIntent();
        SrcID=HomepageActivity.this.getSharedPreferences("action",MODE_PRIVATE).getString("id", null);
        DesId=intent.getStringExtra("id");
        Nick=intent.getStringExtra("nick");
        Auto=intent.getStringExtra("auto");
        mArrays=new ArrayList<BaseItem>();
        adapter=new MyAdapter(HomepageActivity.this,mArrays);
        ElasticListView listView=(ElasticListView)findViewById(R.id.listView_homePage);
        listView.setAdapter(adapter);
        mArrays.add(new ItemHomepageTitle(5,DesId,getResources().getDrawable(R.drawable.tianqing),Nick,Auto));
        if(!DesId.equals(SrcID)) {
            get.setOnClickListener(Click);
            send.setOnClickListener(Click);
        }
        else {
            get.setText("待开发");
            send.setText("待开发");
        }
    }
    View.OnClickListener Click=new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.button_homepage_left:
                    //环境检测
                    MyAsyncTask myAsyncTask=new MyAsyncTask();
                    myAsyncTask.execute();
                    break;
                case R.id.button_homepage_right:
                    Date date=new Date();
                    DateFormat format=new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA);
                    DateFormat Time=new SimpleDateFormat("HH:mm:ss", Locale.CHINA);
                    Msg msg=new Msg();
                    msg.SrcId=DesId;
                    msg.Remarks=Nick;
                    msg.Msg=Auto;
                    msg.Date= format.format(date);
                    msg.Time= Time.format(date);
                    Intent broad=new Intent();
                    broad.setAction("com.stark.yiyu.msg");
                    broad.putExtra("Msg", JsonConvert.SerializeObject(msg));
                    broad.putExtra("BagType", "Message");
                    sendBroadcast(broad);
                    if(AddActivity.mThis!=null) {
                        AddActivity.mThis.finish();
                    }
                    Intent intent=new Intent(HomepageActivity.this, ChatActivity.class);
                    intent.putExtra("nick", Nick);
                    intent.putExtra("id", DesId);
                    startActivityForResult(intent, 0);
                    finish();
                    break;
            }
        }
    };
    class MyAsyncTask extends AsyncTask<Void, Integer, Void> {
        Ack ack=new Ack();
        String msgcode=null;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            msgcode= DateUtil.getMsgCode(HomepageActivity.this);
        }
        @Override
        protected Void doInBackground(Void...values) {
            ack=(Ack)NetPackage.getBag(NetSocket.request(NetPackage.Friend(SrcID, DesId, Nick, 0)));
            //db.update("u" + ack.DesId, Data.getSChatContentValues(null, -1, -1, null, ack.BackMsg, DateUtil.Mtod(ack.BackMsg), DateUtil.Mtot(ack.BackMsg), ack.Flag ? 1 : 2), "msgcode=?", new String[]{ack.MsgCode});
            if(!ack.Flag){
                publishProgress(-1);
            }else{
                publishProgress(1);
            }
            return null;
        }
        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            if(values[0]==-1){
                Toast.makeText(HomepageActivity.this, com.stark.yiyu.Util.Error.error(ack.Error), Toast.LENGTH_SHORT).show();
            }else if(values[0]==1){
                Toast.makeText(HomepageActivity.this, "留存成功", Toast.LENGTH_SHORT).show();
            }
        }
    }
}

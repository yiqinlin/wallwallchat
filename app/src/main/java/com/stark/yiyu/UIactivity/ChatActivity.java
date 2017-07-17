package com.stark.yiyu.UIactivity;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.stark.yiyu.File.ImgStorage;
import com.stark.yiyu.Format.Ack;
import com.stark.yiyu.Format.Msg;
import com.stark.yiyu.Format.Refresh;
import com.stark.yiyu.Listview.MyListView;
import com.stark.yiyu.NetWork.NetPackage;
import com.stark.yiyu.NetWork.NetSocket;
import com.stark.yiyu.R;
import com.stark.yiyu.SQLite.Data;
import com.stark.yiyu.SQLite.DatabaseHelper;
import com.stark.yiyu.Util.DateUtil;
import com.stark.yiyu.Util.Error;
import com.stark.yiyu.Util.ListUtil;
import com.stark.yiyu.adapter.MyAdapter;
import com.stark.yiyu.bean.BaseItem;
import com.stark.yiyu.bean.ItemSMsg;
import com.stark.yiyu.json.JsonConvert;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.Locale;

public class ChatActivity extends Activity {
    private String DesId=null;
    private String SrcId=null;
    private SharedPreferences sp;
    private ArrayList<BaseItem> mArrays;
    public MyListView listView=null;
    private TextView msgNumber=null;
    private int start=0;
    private String MsgTemp=null;
    private EditText input=null;
    private MyAdapter adapter;
    private SQLiteDatabase db=null;
    private BroadcastReceiver mReceiver;
    public static Activity This=null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
//        Status.setTranslucentStatus(getWindow(), this, (LinearLayout) findViewById(R.id.transfer_title_status));
        ImageButton left=(ImageButton)findViewById(R.id.button_transfer_title_left);
        TextView mid=(TextView)findViewById(R.id.text_transfer_title);
        Button right=(Button)findViewById(R.id.button_transfer_title_right);
        final Button send=(Button)findViewById(R.id.button_send);
        msgNumber=(TextView)findViewById(R.id.text_transfer_title_left);
        input=(EditText)findViewById(R.id.edit_send);
        listView=(MyListView)findViewById(R.id.list_chat);
        sp = ChatActivity.this.getSharedPreferences("action", MODE_PRIVATE);
        SrcId = sp.getString("id", null);
        This=ChatActivity.this;
        final Intent intent = getIntent();
        mid.setText(intent.getStringExtra("nick"));
        right.setText("更多");
        left.setBackgroundResource(R.drawable.title_back);
        DesId=intent.getStringExtra("id");
        mArrays=new ArrayList<BaseItem>();
        db=new DatabaseHelper(ChatActivity.this).getWritableDatabase();
        db.execSQL("CREATE TABLE IF NOT EXISTS u"+DesId+"(id varchar(20),type integer,bubble integer,msg varchar(1024),msgcode varchar(20),date varchar(10),time varchar(12),ack integer)");
        start+=AddList();
        adapter=new MyAdapter(ChatActivity.this,mArrays);
        listView.setAdapter(adapter);
        listView.setonRefreshListener(new MyListView.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new AsyncTask<Void, Void, Void>() {
                    int SelectionTemp = 0;
                    @Override
                    protected Void doInBackground(Void... params) {
                        try {
                            if (!sp.getBoolean("state", false)) {
                                return null;
                            }
                            if (DesId != null && SrcId != null) {
//                                JSONArray jsonArray = new JSONArray();
//                                Cursor cr = db.query("u" + DesId, new String[]{"msgcode"}, null, null, null, null, "msgcode desc", start + ",15");
//                                if (cr != null && cr.getCount() > 0) {
//                                    while (cr.moveToNext()) {
//                                        jsonArray.put(cr.getString(0));
//                                    }
//                                    cr.close();
//                                }
                                //有网
                                Refresh refresh=(Refresh) NetPackage.getBag(NetSocket.request(NetPackage.Refresh(SrcId, DesId, start, 1, 0, "",1) + '\n'));
                                JsonConvert.UpdateData(ChatActivity.this, db, DesId, refresh, mArrays);
                                SelectionTemp = refresh.ChatData.length();
                                //没网或超时
                                //SelectionTemp=AddList();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        return null;
                    }

                    protected void onPostExecute(Void result) {
                        start += SelectionTemp;
                        adapter.notifyDataSetChanged();
                        listView.onRefreshComplete();
                        listView.setSelection(SelectionTemp);
                    }
                }.execute();
            }
        });
        listView.setonBackListener(new MyListView.OnBackListener() {
            @Override
            public void onBack() {
                unregisterReceiver(mReceiver);
                finish();
            }
        });
        right.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1 = new Intent(ChatActivity.this, HomepageActivity.class);
                intent1.putExtra("nick", intent.getStringExtra("nick"));
                intent1.putExtra("id", DesId);
                startActivity(intent1);
            }
        });
        listView.setSelection(listView.getCount() - 1);
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MyAsyncTask asyncTask = new MyAsyncTask();
                asyncTask.execute();
            }
        });
        mReceiver=new BroadcastReceiver(){
            public void onReceive(Context context, Intent intent) {
                String msgStr=intent.getStringExtra("Msg");
                String cmdStr=intent.getStringExtra("BagType");
                Msg msg= (Msg)NetPackage.getBag(msgStr,cmdStr);
                if(msg.SrcId.equals(DesId)) {
                    start++;
                    mArrays.add(new ItemSMsg(1, msg.SrcId, ChatActivity.this.getResources().getDrawable(R.drawable.tianqing), msg.SendType, msg.Bubble, msg.Msg, msg.MsgCode, msg.Date, msg.Time, 1));
                    adapter.notifyDataSetChanged();
                }else{
                    msgNumber.setText(Integer.parseInt(msgNumber.getText().toString())+1);
                }
            }
        };
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("com.stark.yiyu.msg");
        ChatActivity.this.registerReceiver(mReceiver, intentFilter);
        left.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                unregisterReceiver(mReceiver);
                Intent intent = new Intent();
                intent.putExtra("id", DesId);
                setResult(2, intent);
                finish();
            }
        });
        send.setClickable(false);
        send.setBackgroundResource(R.drawable.gray_button);
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
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(event.getKeyCode()==KeyEvent.KEYCODE_BACK) {
            unregisterReceiver(mReceiver);
            Intent intent=new Intent();
            intent.putExtra("id",DesId);
            setResult(2, intent);
            finish();
        }
        return false;
    }
    private int AddList(){
        int item_type;
        int count=0;
        Cursor cr=db.query("u"+DesId,null,null,null,null,null,"msgcode desc",start+",15");
        if(cr!=null&&cr.getCount()>0){
            while (cr.moveToNext()){
                if(cr.getString(0)!=null) {
                    if (cr.getString(0).equals(SrcId)) {
                        item_type = 0;
                    } else if (cr.getString(0).equals("10000")) {
                        item_type = 2;
                    } else {
                        item_type = 1;
                    }
                    count++;
                    mArrays.add(0,new ItemSMsg(item_type, cr.getString(0), ImgStorage.getHead(ChatActivity.this), cr.getInt(1), cr.getInt(2), cr.getString(3), cr.getString(4), cr.getString(5), cr.getString(6), cr.getInt(7)));
                }
            }
            cr.close();
        }
        return count;
    }
    class MsgCmp implements Comparator<ItemSMsg>{
        @Override
        public int compare(ItemSMsg M1, ItemSMsg M2) {
            return M1.getMsgCode().compareTo(M2.getMsgCode());
        }
    }
    class MyAsyncTask extends AsyncTask<Void, Integer, Void> {
        Ack ack=new Ack();
        String msgCode=null;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            MsgTemp=input.getText().toString();
            input.setText(null);
            Date date=new Date();
            DateFormat DateTemp=new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA);
            DateFormat TimeTemp=new SimpleDateFormat("HH:mm:ss", Locale.CHINA);
            msgCode= DateUtil.getMsgCode(ChatActivity.this);
            db.insert("u" + DesId, null, Data.getSChatContentValues(SrcId, 1, 0, MsgTemp,msgCode, DateTemp.format(date), TimeTemp.format(date), 0));
            mArrays.add(new ItemSMsg(0, SrcId, ChatActivity.this.getResources().getDrawable(R.drawable.tianqing), 1, 0, MsgTemp, msgCode, DateTemp.format(date), TimeTemp.format(date), 0));
            adapter.notifyDataSetChanged();
            listView.setSelection(listView.getBottom());
        }
        @Override
        protected Void doInBackground(Void...values) {
            if(MsgTemp!=null) {
                ack = (Ack) NetPackage.getBag(NetSocket.request(NetPackage.SendMsg(sp.getString("id", null), DesId, MsgTemp, msgCode) + '\n'));
                db.update("u" + ack.DesId, Data.getSChatContentValues(null, -1, -1, null, ack.BackMsg, DateUtil.Mtoy(ack.BackMsg), DateUtil.Mtot(ack.BackMsg), ack.Flag ? 1 : 0), "msgcode=?", new String[]{ack.MsgCode});
                if(!ack.Flag){
                    publishProgress(-1);
                }else{
                    publishProgress(1);
                }
            }else{
                publishProgress(-2);
            }
            return null;
        }
        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            switch (values[0]){
                case -1:
                    Toast.makeText(ChatActivity.this, Error.error(ack.Error),Toast.LENGTH_SHORT).show();
                    break;
                case 1:
                    start++;
                    mArrays=ListUtil.UpadteState(mArrays,ack.MsgCode,ack.BackMsg);
                    adapter.notifyDataSetChanged();
                    break;
            }
        }
    }
}

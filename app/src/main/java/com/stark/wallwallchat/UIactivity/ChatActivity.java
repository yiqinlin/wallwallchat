package com.stark.wallwallchat.UIactivity;

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
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.stark.wallwallchat.Listview.MyListView;
import com.stark.wallwallchat.MyService;
import com.stark.wallwallchat.NetWork.NetBuilder;
import com.stark.wallwallchat.NetWork.NetPackage;
import com.stark.wallwallchat.NetWork.NetSocket;
import com.stark.wallwallchat.R;
import com.stark.wallwallchat.SQLite.Data;
import com.stark.wallwallchat.SQLite.DatabaseHelper;
import com.stark.wallwallchat.Util.Error;
import com.stark.wallwallchat.Util.ListUtil;
import com.stark.wallwallchat.adapter.MyAdapter;
import com.stark.wallwallchat.bean.BaseItem;
import com.stark.wallwallchat.bean.ItemSMsg;
import com.stark.wallwallchat.json.JsonConvert;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

public class  ChatActivity extends Activity {
    private String DesId = null;
    private String SrcId = null;
    private SharedPreferences sp;
    private ArrayList<BaseItem> mArrays;
    public MyListView listView = null;
    private TextView msgNumber = null;
    private int start = 0;
    private String MsgTemp = null;
    private EditText input = null;
    private MyAdapter adapter;
    private SQLiteDatabase db = null;
    private BroadcastReceiver mReceiver;
    public static Activity This = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
//        Status.setTranslucentStatus(getWindow(), this, (LinearLayout) findViewById(R.id.transfer_title_status));
        ImageButton left = (ImageButton) findViewById(R.id.button_transfer_title_left);
        TextView mid = (TextView) findViewById(R.id.text_transfer_title);
        Button right = (Button) findViewById(R.id.button_transfer_title_right);
        final Button send = (Button) findViewById(R.id.button_send);
        msgNumber = (TextView) findViewById(R.id.text_transfer_title_left);
        input = (EditText) findViewById(R.id.edit_send);
        listView = (MyListView) findViewById(R.id.list_chat);
        sp = ChatActivity.this.getSharedPreferences("action", MODE_PRIVATE);
        SrcId = sp.getString("id", null);
        This = ChatActivity.this;
        final Intent intent = getIntent();
        mid.setText(intent.getStringExtra("nick"));
        right.setText("更多");
        left.setBackgroundResource(R.drawable.title_back);
        DesId = intent.getStringExtra("id");
        mArrays = new ArrayList<BaseItem>();
        db = new DatabaseHelper(ChatActivity.this).getWritableDatabase();
        if(intent.getBooleanExtra("unread",false)) {
            Intent service=new Intent(ChatActivity.this, MyService.class);
            service.putExtra("receiver",DesId);
            service.putExtra("CMD", "ChangeRead");
            startService(service);
            new MsgAsyncTask().execute();
        }else{
            start += AddList();
        }
        adapter = new MyAdapter(ChatActivity.this, mArrays);
        listView.setAdapter(adapter);
        listView.setonRefreshListener(new MyListView.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new MsgAsyncTask().execute();
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
        mReceiver = new BroadcastReceiver() {
            public void onReceive(Context context, Intent intent) {
                if (intent.getStringExtra("sponsor").equals(DesId)) {
                    start++;
                    Cursor cr = db.query("msg", null, "msgcode=?", new String[]{intent.getStringExtra("msgcode")}, null, null, null, null);
                    if (cr != null && cr.getCount() > 0 && cr.moveToNext()) {
                        mArrays.add(new ItemSMsg(1, cr, 1));
                        adapter.notifyDataSetChanged();
                        cr.close();
                    }
                } else {
                    msgNumber.setText(Integer.parseInt(msgNumber.getText().toString()) + 1);
                }
            }
        };
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("com.stark.wallwallchat.msg");
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
                if (count == 0) {
                    send.setClickable(false);
                    send.setBackgroundResource(R.drawable.gray_button);
                } else {
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
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mReceiver);
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
            Intent intent = new Intent();
            intent.putExtra("id", DesId);
            setResult(2, intent);
            finish();
        }
        return false;
    }

    private int AddList() {
        int item_type;
        int count = 0;
        Cursor cr = db.query("msg", null, "(sponsor=? and receiver=?) or(sponsor=? and receiver=?)", new String[]{SrcId, DesId, DesId, SrcId}, null, null, "msgcode desc", start + ",15");
        if (cr != null && cr.getCount() > 0) {
            while (cr.moveToNext()) {
                if (cr.getString(0) != null) {
                    if (cr.getString(0).equals(SrcId)) {
                        item_type = 0;
                    } else if (cr.getString(0).equals("10000")) {
                        item_type = 2;
                    } else {
                        item_type = 1;
                    }
                    count++;
                    mArrays.add(0, new ItemSMsg(item_type, cr, 1));
                }
            }
            cr.close();
        }
        return count;
    }

    class MsgCmp implements Comparator<ItemSMsg> {
        @Override
        public int compare(ItemSMsg M1, ItemSMsg M2) {
            return M1.getMsgCode().compareTo(M2.getMsgCode());
        }
    }

    class MsgAsyncTask extends AsyncTask<Void, Integer, Void> {
        int SelectionTemp = 0;
        @Override
        protected Void doInBackground(Void... params) {
            try {
                if (!sp.getBoolean("state", false)) {
                    return null;
                }
                if (DesId != null && SrcId != null) {
                    //有网
                    HashMap<String, Object> SqlPkg = new HashMap<String, Object>();
                    SqlPkg.put("receiver", DesId);
                    SqlPkg.put("start", start);
                    NetPackage netPkg = new NetPackage(ChatActivity.this, SqlPkg);
                    String temp = JsonConvert.SerializeObject(netPkg.SRefresh());
                    String result;
                    try {
                        result = NetSocket.request(temp);
                    } catch (Exception e) {
                        //没网或超时
                        SelectionTemp = AddList();
                        publishProgress(-1);
                        return null;
                    }
                    Log.e("request", temp);
                    Log.e("result", result);
                    NetBuilder out = (NetBuilder) JsonConvert.DeserializeObject(result, new NetBuilder());
                    if (out.getBool("flag", false)) {
                        out.UpdateData(ChatActivity.this, db, DesId, mArrays);
                        SelectionTemp = out.ListSize();
                        publishProgress(0);/**告诉UI线程 更新*/
                    } else {
                        publishProgress(out.getInt("error", -2));/**告诉UI线程 更新*/
                    }
                }
            } catch (Exception e) {
                publishProgress(7);
                e.printStackTrace();
            }
            return null;
        }
        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            start += SelectionTemp;
            adapter.notifyDataSetChanged();
            listView.onRefreshComplete();
            listView.setSelection(SelectionTemp);
        }
    }
    class MyAsyncTask extends AsyncTask<Void, Integer, Void> {
        HashMap<String,Object>  data=new HashMap<String,Object>();
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            data.put("sponsor",sp.getString("id", null));
            data.put("receiver", DesId);
            data.put("msg", input.getText().toString());
            input.setText(null);
            Date date=new Date();
            data.put("msgcode2",new SimpleDateFormat("yyyyMMddHHmmssSSS", Locale.CHINA).format(new Date()));
            data.put("bubble",0);
            data.put("sendtype", 1);
            data.put("ack", 0);
            db.insert("msg", null, Data.MapToContentValues(data));
            mArrays.add(new ItemSMsg(0 ,data));
            adapter.notifyDataSetChanged();
            listView.setSelection(listView.getBottom());
        }
        @Override
        protected Void doInBackground(Void...values) {
            if (data.get("msg") != null) {
                HashMap<String, Object> SqlPkg = new HashMap<String, Object>();
                SqlPkg.put("receiver", DesId);
                SqlPkg.put("msg", data.get("msg"));
                SqlPkg.put("msgcode2", data.get("msgcode2"));
                try {
                    NetPackage netPkg = new NetPackage(ChatActivity.this, SqlPkg);
                    String temp = JsonConvert.SerializeObject(netPkg.Csend(1));
                    String result;
                    try {
                        result = NetSocket.request(temp);
                    } catch (Exception e) {
                        publishProgress(-1);
                        return null;
                    }
                    Log.e("request", temp);
                    Log.e("result", result);
                    NetBuilder out = (NetBuilder) JsonConvert.DeserializeObject(result, new NetBuilder());
                    if (out.getBool("flag", false)) {
                        HashMap<String, Object> bag = new HashMap<String, Object>();
                        bag.put("msgcode", out.get("msgcode"));
                        bag.put("ack", 1);
                        db.update("msg", Data.MapToContentValues(bag), "msgcode2=?", new String[]{out.get("msgcode2")});
                        mArrays=ListUtil.UpadteState(mArrays,data.get("msgcode2").toString(),out.get("msgcode"));
                        publishProgress(0);/**告诉UI线程 更新*/
                    }
                    else {
                        publishProgress(out.getInt("error", -2));/**告诉UI线程 更新*/
                    }
                } catch (Exception e) {
                    publishProgress(7);
                    Log.e("NetWork", e.toString());
                }
            }
            return null;
        }
        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            if(values[0]==0){
                start++;
                adapter.notifyDataSetChanged();
            }else{
                Toast.makeText(ChatActivity.this, Error.error(values[0]),Toast.LENGTH_SHORT).show();
            }
        }
    }
}

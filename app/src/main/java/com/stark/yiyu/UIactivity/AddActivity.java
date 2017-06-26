package com.stark.yiyu.UIactivity;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.stark.yiyu.Format.Get;
import com.stark.yiyu.Format.UserInfo;
import com.stark.yiyu.Listview.ElasticListView;
import com.stark.yiyu.NetWork.NetPackage;
import com.stark.yiyu.NetWork.NetSocket;
import com.stark.yiyu.R;
import com.stark.yiyu.Util.Status;
import com.stark.yiyu.adapter.MyAdapter;
import com.stark.yiyu.adapter.holder.ViewHolderMid;
import com.stark.yiyu.bean.BaseItem;
import com.stark.yiyu.bean.ItemMid;
import com.stark.yiyu.json.JsonConvert;
import com.stark.yiyu.toast.ToastDialog;

import org.json.JSONArray;

import java.util.ArrayList;

public class AddActivity extends Activity {
    private ToastDialog mToastDialog=null;
    private String info=null;
    private ArrayList<BaseItem> mArrays=null;
    private MyAdapter adapter=null;
    public static Activity mThis=null;
    public static LinearLayout refreshView;
    private int Mode=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mThis=this;
        setContentView(R.layout.activity_add);
        Status.setTranslucentStatus(getWindow(), this, (LinearLayout) findViewById(R.id.transfer_title_status));
        ImageButton left=(ImageButton)findViewById(R.id.button_transfer_title_left);
        TextView title=(TextView)findViewById(R.id.text_transfer_title);
        refreshView = (LinearLayout)findViewById(R.id.layout_refresh);
        final Button search=(Button)findViewById(R.id.button_chat_send);
        final EditText input=(EditText)findViewById(R.id.edit_chat_input);
        title.setText(getIntent().getStringExtra("title"));
        Mode=getIntent().getIntExtra("Mode",1);
        search.setText("搜索");
        left.setBackgroundResource(R.drawable.title_back);
        getWindow().setSoftInputMode(getIntent().getIntExtra("TouchMode", WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN));
        input.setSingleLine();
        input.setHint("一语账号");
        input.setHintTextColor(getResources().getColor(android.R.color.darker_gray));
        mArrays=new ArrayList<BaseItem>();
        adapter=new MyAdapter(AddActivity.this,mArrays);
        ElasticListView listView = (ElasticListView)findViewById(R.id.listView_add);
        listView.setAdapter(adapter);
        listView.setonBackListener(new ElasticListView.OnBackListener() {
            @Override
            public void onBack() {
                finish();
            }
        });
        left.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                info = input.getText().toString();
                if (!info.equals("")) {
                    MyAsyncTask asyncTask = new MyAsyncTask();
                    asyncTask.execute();
                    if (mToastDialog == null)
                        mToastDialog = new ToastDialog(AddActivity.this);
                    mToastDialog.setText("搜索中...").show();
                }
            }
        });
        listView.setOnItemClickListener(new MyOnItemClickListener());
        switch (Mode){
            case 2:
            case 3:
                info=AddActivity.this.getSharedPreferences("action",MODE_PRIVATE).getString("id",null);
                refreshView.setVisibility(View.VISIBLE);
                MyAsyncTask asyncTask = new MyAsyncTask();
                asyncTask.execute();
                break;
        }
    }
    class MyAsyncTask extends AsyncTask<Void, Integer, Void> {
        Get get;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mArrays.clear();
        }
        @Override
        protected Void doInBackground(Void...values) {
            get=(Get) NetPackage.getBag(NetSocket.request(NetPackage.Get(info, Mode, new JSONArray())));
            publishProgress(0);
            return null;
        }
        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            if(mToastDialog!=null) {
                mToastDialog.cancel();
            }
            if(refreshView.getVisibility()==View.VISIBLE){
                refreshView.setVisibility(View.GONE);
                info=null;
            }
            if(values[0]==0&&get!=null){
                UserInfo user;
                for(int i=0;i<get.Data.length();i++) {
                    user= JsonConvert.GetInfo(get,i);
                    mArrays.add(new ItemMid(2, user.Id, getResources().getDrawable(R.drawable.tianqing), user.Nick, user.Auto, "", ""));
                }
                adapter.notifyDataSetChanged();
            }
        }
    }
    private class MyOnItemClickListener implements AdapterView.OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            ViewHolderMid viewHolder=(ViewHolderMid)view.getTag();
            Intent intent=new Intent(AddActivity.this,HomepageActivity.class);
            intent.putExtra("nick", viewHolder.nick.getText().toString());
            intent.putExtra("id", viewHolder.id);
            intent.putExtra("auto",viewHolder.message.getText().toString());
            startActivity(intent);
        }
    }
}

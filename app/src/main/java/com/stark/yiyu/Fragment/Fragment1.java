package com.stark.yiyu.Fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.stark.yiyu.Format.Ack;
import com.stark.yiyu.Listview.MyListView;
import com.stark.yiyu.NetWork.NetPackage;
import com.stark.yiyu.NetWork.NetSocket;
import com.stark.yiyu.R;
import com.stark.yiyu.SQLite.Data;
import com.stark.yiyu.SQLite.DatabaseHelper;
import com.stark.yiyu.Util.DateUtil;
import com.stark.yiyu.Util.ListUtil;
import com.stark.yiyu.adapter.MyAdapter;
import com.stark.yiyu.bean.BaseItem;
import com.stark.yiyu.bean.ItemSMsg;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Stark on 2017/2/14.
 */
public class Fragment1 extends Fragment {

    EditText input=null;
    MyListView listView=null;
    ArrayList<BaseItem> mArrays=null;
    MyAdapter adapter=null;
    @Override
    public View onCreateView(LayoutInflater inflater,ViewGroup container,Bundle savesInstanceState){
        View view=inflater.inflate(R.layout.transfer_left, container, false);
        Button send=(Button)getActivity().findViewById(R.id.button_chat_send);
        input=(EditText)getActivity().findViewById(R.id.edit_chat_input);
        listView=(MyListView)view.findViewById(R.id.listView_left);
        final SQLiteDatabase db=new DatabaseHelper(getActivity()).getWritableDatabase();
        final SharedPreferences sp = getActivity().getSharedPreferences("action", Context.MODE_PRIVATE);
        db.execSQL("CREATE TABLE IF NOT EXISTS g10001(id varchar(20),type integer,bubble integer,msg varchar(1024),msgcode varchar(20),date varchar(10),time varchar(12),ack integer)");
        mArrays=new ArrayList<BaseItem>();
        adapter=new MyAdapter(getActivity(),mArrays);
        listView.setAdapter(adapter);
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String MsgTemp=input.getText().toString();
                input.setText(null);
                Date date=new Date();
                DateFormat DateTemp=new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA);
                DateFormat TimeTemp=new SimpleDateFormat("HH:mm:ss", Locale.CHINA);
                String msgCode= DateUtil.getMsgCode(getActivity());
                db.insert("g10001", null, Data.getSChatContentValues(sp.getString("id",null), 1, 0, MsgTemp, msgCode, DateTemp.format(date), TimeTemp.format(date), 0));
                mArrays.add(new ItemSMsg(0, sp.getString("id",null), getActivity().getResources().getDrawable(R.drawable.tianqing), 1, 0, MsgTemp, msgCode, DateTemp.format(date), TimeTemp.format(date), 0));
                adapter.notifyDataSetChanged();
                listView.setSelection(listView.getBottom());
                MyAsyncTask asyncTask = new MyAsyncTask();
                asyncTask.execute(MsgTemp);
            }
        });
        return view;
    }
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }
    class MyAsyncTask extends AsyncTask<String, Integer, Void> {
        Ack ack=new Ack();
        String msgCode=null;
        SQLiteDatabase db=new DatabaseHelper(getActivity()).getWritableDatabase();
        SharedPreferences sp = getActivity().getSharedPreferences("action", Context.MODE_PRIVATE);
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }
        @Override
        protected Void doInBackground(String...values) {
            if(values[0]!=null) {
                ack = (Ack) NetPackage.getBag(NetSocket.request(NetPackage.SendGMsg(sp.getString("id", null), "10001", values[0], msgCode) + '\n'));
                db.update("g" + ack.DesId, Data.getSChatContentValues(null, -1, -1, null, ack.BackMsg, DateUtil.Mtod(ack.BackMsg), DateUtil.Mtot(ack.BackMsg), ack.Flag ? 1 : 0), "msgcode=?", new String[]{ack.MsgCode});
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
                    Toast.makeText(getActivity(), com.stark.yiyu.Util.Error.error(ack.Error), Toast.LENGTH_SHORT).show();
                    break;
                case 1:
                    mArrays=ListUtil.UpadteState(mArrays,ack.MsgCode,ack.BackMsg);
                    adapter.notifyDataSetChanged();
                    break;
            }
        }
    }
}

package com.stark.wallwallchat.Fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import com.stark.wallwallchat.Format.Refresh;
import com.stark.wallwallchat.Listview.MyListView;
import com.stark.wallwallchat.NetWork.NetPackage;
import com.stark.wallwallchat.NetWork.NetSocket;
import com.stark.wallwallchat.R;
import com.stark.wallwallchat.SQLite.DatabaseHelper;
import com.stark.wallwallchat.UIactivity.WallMsgActivity;
import com.stark.wallwallchat.adapter.MyAdapter;
import com.stark.wallwallchat.bean.BaseItem;
import com.stark.wallwallchat.bean.ItemWallInfo;
import com.stark.wallwallchat.json.JsonConvert;

import java.util.ArrayList;

/**
 * Created by Stark on 2017/2/14.
 */
public class Fragment1 extends Fragment{
    ArrayList<BaseItem> mArrays=null;
    MyAdapter adapter=null;
    MyListView listView;
    String SrcID;
    private BroadcastReceiver mReceiver = null;
    @Override
    public View onCreateView(LayoutInflater inflater,ViewGroup container,Bundle savesInstanceState){
        View view=inflater.inflate(R.layout.transfer_left, container, false);
        final SQLiteDatabase db=new DatabaseHelper(getActivity()).getWritableDatabase();
        //db.execSQL("CREATE TABLE IF NOT EXISTS wall(item_type int,type int,sponsor char(20),receiver char(20),msgcode char(20),nick  varchar(16),nick2  varchar(16),msg varchar(1024),cnum char(10),anum char(10),boolean isAgree)");
        listView =(MyListView)view.findViewById(R.id.listView_left);
        final SharedPreferences sp = getActivity().getSharedPreferences("action", Context.MODE_PRIVATE);
        SrcID=sp.getString("id",null);
        mArrays=new ArrayList<BaseItem>();
        adapter=new MyAdapter(getActivity(),mArrays);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new MyOnItemClickListener());
        listView.setonRefreshListener(new MyListView.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new MyAsyncTask().execute();
            }
        });
        mReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().equals("com.stark.wallwallchat.updateWall")) {
                    new MyAsyncTask().execute();
                }
            }
        };
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("com.stark.wallwallchat.updateWall");
        getActivity().registerReceiver(mReceiver, intentFilter);
        return view;
    }
    private class MyOnItemClickListener implements AdapterView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            ItemWallInfo msg=(ItemWallInfo)mArrays.get(position-1);
            Intent intent = new Intent(getActivity(), WallMsgActivity.class);
            intent.putExtra("sponsor",msg.getId());
            intent.putExtra("msgcode",msg.getMsgcode());
            startActivity(intent);
        }
    }
    class MyAsyncTask extends AsyncTask<Void, Integer, Void> {
        SharedPreferences sp = getActivity().getSharedPreferences("action", Context.MODE_PRIVATE);
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... values) {
            Refresh refresh = (Refresh) NetPackage.getBag(NetSocket.request(NetPackage.Refresh(SrcID, sp.getString("edu","nsu"), 0, 2, 1, "",0)));
            mArrays.clear();
            JsonConvert.UpdateWall(getActivity(), refresh, mArrays);
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            adapter.notifyDataSetChanged();
            listView.onRefreshComplete();
        }
    }
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }
    @Override
    public void onDestroyView() {
        Log.e("onDestroyView", "Fragment");
        getActivity().unregisterReceiver(mReceiver);
        super.onDestroyView();
    }
}

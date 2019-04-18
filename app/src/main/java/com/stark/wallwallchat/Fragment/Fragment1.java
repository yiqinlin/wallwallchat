package com.stark.wallwallchat.Fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Toast;

import com.stark.wallwallchat.Listview.MyListView;
import com.stark.wallwallchat.NetWork.NetBuilder;
import com.stark.wallwallchat.NetWork.NetPackage;
import com.stark.wallwallchat.NetWork.NetSocket;
import com.stark.wallwallchat.R;
import com.stark.wallwallchat.SQLite.DatabaseHelper;
import com.stark.wallwallchat.UIactivity.WallMsgActivity;
import com.stark.wallwallchat.adapter.MyAdapter;
import com.stark.wallwallchat.bean.BaseItem;
import com.stark.wallwallchat.bean.ItemMargin;
import com.stark.wallwallchat.bean.ItemWallInfo;
import com.stark.wallwallchat.json.JsonConvert;

import java.util.ArrayList;
import java.util.HashMap;

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
                    msgRefresh();
                }
            }
        };
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("com.stark.wallwallchat.updateWall");
        getActivity().registerReceiver(mReceiver, intentFilter);
        if(!msgRefresh()){
            new MyAsyncTask().execute();
        }
        return view;
    }

    private boolean msgRefresh(){
        SQLiteDatabase db=new DatabaseHelper(getActivity()).getWritableDatabase();
        Cursor cr=db.query("wall",null,null,null,null,null,"msgcode desc","0,15");
        if(cr!=null &&cr.getCount()>0){
            mArrays.clear();
            while(cr.moveToNext()){
                int item_type=11;
                switch (cr.getInt(cr.getColumnIndex("mode"))){
                    case 0:
                        item_type = 11;
                        break;
                    case 1:
                        item_type = 12;
                        break;
                }
                mArrays.add(new ItemWallInfo(item_type, cr));
                if(!cr.isLast()) {
                    mArrays.add(new ItemMargin(8));
                }
            }
            cr.close();
            adapter.notifyDataSetChanged();
            db.close();
            return true;
        }
        return false;
    }
    private class MyOnItemClickListener implements AdapterView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            if((position&1)==1){
                ItemWallInfo msg=(ItemWallInfo)mArrays.get(position-1);
                Intent intent = new Intent(getActivity(), WallMsgActivity.class);
                intent.putExtra("msgcode",msg.getMsgcode());
                intent.putExtra("sponsor",msg.getId());
                startActivity(intent);
            }
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
            HashMap<String,Object> SqlPkg=new HashMap<String,Object>();
            SqlPkg.put("mode", 0);
            SqlPkg.put("start", 0);
            try {
                NetPackage netPkg=new NetPackage(getActivity(),SqlPkg);
                String temp = JsonConvert.SerializeObject(netPkg.WRefresh(0));
                String result;
                try{
                    result=NetSocket.request("http://kwall.cn/wallRefresh.php",temp);
                }catch (Exception e){
                    publishProgress(-1);
                    return null;
                }
                Log.e("request", temp);
                Log.e("result",result);
                NetBuilder out = (NetBuilder) JsonConvert.DeserializeObject(result, new NetBuilder());
                if(out.getBool("flag", false)) {
                    mArrays.clear();
                    out.UpdateWall(getActivity(), mArrays);
                    out.UpdateWallDB(getActivity());
                    publishProgress(0);/**告诉UI线程 更新*/
                }
                else {
                    publishProgress(out.getInt("error", -2));/**告诉UI线程 更新*/
                }
            }catch (Exception e){
                publishProgress(7);
                Log.e("NetWork",e.toString());
            }
            return null;
        }
        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            listView.onRefreshComplete();
            if(values[0]==0){
                adapter.notifyDataSetChanged();
            }else{
                Toast.makeText(getActivity(), com.stark.wallwallchat.Util.Error.error(values[0]),Toast.LENGTH_SHORT).show();
            }
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

package com.stark.wallwallchat.Fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
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
import android.widget.TextView;

import com.stark.wallwallchat.Listview.MyListView;
import com.stark.wallwallchat.MyService;
import com.stark.wallwallchat.NetWork.NetBuilder;
import com.stark.wallwallchat.NetWork.NetPackage;
import com.stark.wallwallchat.NetWork.NetSocket;
import com.stark.wallwallchat.R;
import com.stark.wallwallchat.SQLite.Data;
import com.stark.wallwallchat.SQLite.DatabaseHelper;
import com.stark.wallwallchat.UIactivity.ChatActivity;
import com.stark.wallwallchat.Util.Try;
import com.stark.wallwallchat.adapter.MyAdapter;
import com.stark.wallwallchat.adapter.holder.ViewHolderMid;
import com.stark.wallwallchat.bean.BaseItem;
import com.stark.wallwallchat.bean.ItemMid;
import com.stark.wallwallchat.json.JsonConvert;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Stark on 2017/2/14.
 */
public class Fragment2 extends Fragment {
    private BroadcastReceiver mReceiver;
    private MyAdapter adapter;
    private ArrayList<BaseItem> mArrays;
    public static MyListView listView=null;
    @Override
    public View onCreateView(final LayoutInflater inflater,ViewGroup container,Bundle savesInstanceState){
        View view=inflater.inflate(R.layout.transfer_mid,container,false);
        mArrays=new ArrayList<BaseItem>();
        adapter=new MyAdapter(getActivity(),mArrays);
        listView= (MyListView) view.findViewById(R.id.listView_mid);
        listView.setAdapter(adapter);
        listView.setonRefreshListener(new MyListView.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new MyAsyncTask().execute();
            }
        });
        listView.setOnItemClickListener(new MyOnItemClickListener());
        mReceiver=new BroadcastReceiver(){
            public void onReceive(Context context, Intent intent) {
                msgRefresh();
            }
        };
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("com.stark.wallwallchat.msg");
        getActivity().registerReceiver(mReceiver, intentFilter);
        if(!msgRefresh()){
            new MyAsyncTask().execute();
        }
        return view;
    }
    @Override
    public void onDestroy(){
        Log.i("message", "in onDestroy");
        getActivity().unregisterReceiver(mReceiver);
        super.onDestroy();
    }
    public static void reset(){
        listView.Reset();
    }

    private boolean msgRefresh(){
        SQLiteDatabase db=new DatabaseHelper(getActivity()).getWritableDatabase();
        Cursor cr=db.query("mid",null,null,null,null,null,"msgcode desc");
        if(cr!=null&&cr.getCount()>0){
            mArrays.clear();
            while(cr.moveToNext()){
                mArrays.add(new ItemMid(2, cr));
            }
            cr.close();
            adapter.notifyDataSetChanged();
            db.close();
            return true;
        }
        return false;
    }
    private  class MyAsyncTask extends AsyncTask<Void, Void, Void>{
        @Override
        protected Void doInBackground(Void... params) {
            try {
                HashMap<String,Object> SqlPkg=new HashMap<String,Object>();
                NetPackage netPkg=new NetPackage(getActivity(),SqlPkg);
                String temp = JsonConvert.SerializeObject(netPkg.MRefresh());
                String result;
                try{
                    result= NetSocket.request(temp);
                }catch (Exception e){
                    return null;
                }
                Log.e("request", temp);
                Log.e("result",result);
                NetBuilder out = (NetBuilder) JsonConvert.DeserializeObject(result, new NetBuilder());
                if(out.getBool("flag", false)) {
                    out.UpdateMidDB(getActivity());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
        protected void onPostExecute(Void result) {
            msgRefresh();
            listView.onRefreshComplete();
        }
    }
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        // TODO Auto-generated method stub
        super.onActivityResult(requestCode, resultCode, intent);
        Log.i("code", requestCode + " " + resultCode);
        if (requestCode==0&&resultCode == 2) {
            String temp= Try.getStringExtra(intent, "id");
            if(temp!=null&&!temp.equals("")){
                SQLiteDatabase db=new DatabaseHelper(getActivity()).getWritableDatabase();
                HashMap<String,Object> temp1=new HashMap<String,Object>();
                temp1.put("num",0);
                db.update("mid", Data.MapToContentValues(temp1), "sponsor=?", new String[]{temp});
                db.close();
            }
            Intent service=new Intent(getActivity(), MyService.class);
            service.putExtra("receiver",intent.getStringExtra("id"));
            service.putExtra("CMD", "ChangeRead");
            getActivity().startService(service);
        }
    }
    private class MyOnItemClickListener implements AdapterView.OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            ViewHolderMid viewHolder=(ViewHolderMid)view.getTag();
            Intent intent=new Intent(getActivity(), ChatActivity.class);
            SQLiteDatabase db=new DatabaseHelper(getActivity()).getWritableDatabase();
            Cursor cr=db.query("mid", new String[]{"remarks","num"}, "sponsor=?", new String[]{viewHolder.id}, null, null, null);
            if(cr!=null&&cr.getCount()>0&&cr.moveToNext()){
                intent.putExtra("unread", cr.getInt(cr.getColumnIndex("num"))>0);
                intent.putExtra("nick", cr.getString(cr.getColumnIndex("remarks")));
                cr.close();
            }else {
                intent.putExtra("nick",viewHolder.id);
            }
            intent.putExtra("id", viewHolder.id);
            startActivityForResult(intent, 0);
            getActivity().overridePendingTransition(R.anim.anim_rtom, R.anim.anim_mtol);
            HashMap<String,Object> temp1=new HashMap<String,Object>();
            temp1.put("num", 0);
            db.update("mid", Data.MapToContentValues(temp1), "sponsor=?", new String[]{viewHolder.id});
            db.close();
            TextView temp=(TextView)view.findViewById(R.id.mid_list_count);
            temp.setActivated(false);
            temp.setText(null);
        }
    }

}

package com.stark.wallwallchat.Fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import com.stark.wallwallchat.File.ImgStorage;
import com.stark.wallwallchat.Listview.ElasticListView;
import com.stark.wallwallchat.R;
import com.stark.wallwallchat.SQLite.DatabaseHelper;
import com.stark.wallwallchat.UIactivity.HomepageActivity;
import com.stark.wallwallchat.UIactivity.ZoneActivity;
import com.stark.wallwallchat.adapter.MyAdapter;
import com.stark.wallwallchat.bean.BaseItem;
import com.stark.wallwallchat.bean.ItemKnow;
import com.stark.wallwallchat.bean.ItemRightHead;

import java.util.ArrayList;

/**
 * Created by Stark on 2017/2/14.
 */
public class Fragment3 extends Fragment {
    private ArrayList<BaseItem> mArrays=null;
    private MyAdapter adapter=null;
    private String Nick=null;
    private String Auto = null;
    private BroadcastReceiver mReceiver = null;
    @Override
    public View onCreateView(LayoutInflater inflater,ViewGroup container,Bundle savedInstanceState){
        View view=inflater.inflate(R.layout.transfer_right, container, false);
        mArrays=new ArrayList<BaseItem>();
        adapter=new MyAdapter(getActivity(),mArrays);
        ElasticListView listView = (ElasticListView) view.findViewById(R.id.listView_right);
        final SQLiteDatabase db = new DatabaseHelper(getActivity()).getWritableDatabase();
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new MyOnItemClickListener());
        init(db);
        mReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().equals("com.stark.wallwallchat.changeHead")) {
                    SharedPreferences sp=getActivity().getSharedPreferences("action", Context.MODE_PRIVATE);
                    db.execSQL("CREATE TABLE IF NOT EXISTS userdata(id varchar(20),nick varchar(16),auto varchar(50),sex integer,birth varchar(10),pnumber varchar(11),startdate varchar(10),catdate integer,typeface integer,theme integer,bubble integer,iknow integer,knowme integer)");
                    Cursor cr=db.query("userdata", new String[]{"nick", "auto"}, "id=?", new String[]{sp.getString("id", null)}, null, null, null);
                    if (cr != null && cr.getCount() > 0 && cr.moveToNext()) {
                        mArrays.remove(0);
                        mArrays.add(0,new ItemRightHead(3, sp.getString("id", null), ImgStorage.getHead(getActivity()), cr.getString(0), cr.getString(1)));
                        adapter.notifyDataSetChanged();
                    }
                }else if(intent.getAction().equals("com.stark.wallwallchat.userInfo")){
                    mArrays.clear();
                    init(db);
                }
            }
        };
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("com.stark.wallwallchat.changeHead");
        intentFilter.addAction("com.stark.wallwallchat.userInfo");
        getActivity().registerReceiver(mReceiver, intentFilter);
        return view;
    }
    public void init(SQLiteDatabase db){
        SharedPreferences sp=getActivity().getSharedPreferences("action", Context.MODE_PRIVATE);
        db.execSQL("CREATE TABLE IF NOT EXISTS userdata(id varchar(20),nick varchar(16),auto varchar(50),sex integer,birth varchar(10),pnumber varchar(11),startdate varchar(10),catdate integer,typeface integer,theme integer,bubble integer,iknow integer,knowme integer)");
        Cursor cr=db.query("userdata", new String[]{"nick", "auto"}, "id=?", new String[]{sp.getString("id", null)}, null, null, null);
        if (cr != null && cr.getCount() > 0 && cr.moveToNext()) {
            mArrays.add(new ItemRightHead(3, sp.getString("id", null), ImgStorage.getHead(getActivity()), cr.getString(0), cr.getString(1)));
            Nick=cr.getString(0);
            Auto=cr.getString(1);
            cr.close();
        }
        cr=db.query("userdata", new String[]{"iknow", "knowme"}, "id=?", new String[]{sp.getString("id", null)}, null, null, null);
        if (cr != null && cr.getCount() > 0 && cr.moveToNext()) {
            mArrays.add(new ItemKnow(4, cr.getString(0), cr.getString(1)));
            cr.close();
        }
        adapter.notifyDataSetChanged();
    }
    private class MyOnItemClickListener implements AdapterView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            SharedPreferences sp=getActivity().getSharedPreferences("action", Context.MODE_PRIVATE);
            switch(position){
                case 0:
                    Intent intent=new Intent(getActivity(), HomepageActivity.class);
                    intent.putExtra("nick",Nick);
                    intent.putExtra("id", sp.getString("id", null));
                    intent.putExtra("auto",Auto);
                    startActivity(intent);
                    break;
                case 2:
                    Intent intent1=new Intent(getActivity(), ZoneActivity.class);
                    startActivity(intent1);
                    break;
            }
        }
    }

    @Override
    public void onDestroyView() {
        Log.e("onDestroyView", "Fragment");
        getActivity().unregisterReceiver(mReceiver);
        super.onDestroyView();
    }
}

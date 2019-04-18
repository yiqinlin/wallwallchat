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
import android.widget.Toast;

import com.stark.wallwallchat.Listview.ElasticListView;
import com.stark.wallwallchat.R;
import com.stark.wallwallchat.SQLite.DatabaseHelper;
import com.stark.wallwallchat.UIactivity.EditInfoActivity;
import com.stark.wallwallchat.UIactivity.HomepageActivity;
import com.stark.wallwallchat.UIactivity.Login;
import com.stark.wallwallchat.UIactivity.TransferActivity;
import com.stark.wallwallchat.adapter.MyAdapter;
import com.stark.wallwallchat.bean.BaseItem;
import com.stark.wallwallchat.bean.ItemKnow;
import com.stark.wallwallchat.bean.ItemMargin;
import com.stark.wallwallchat.bean.ItemRightHead;
import com.stark.wallwallchat.bean.ItemSimpleList;

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
                    mArrays.clear();
                    init(db);
                }else if(intent.getAction().equals("com.stark.wallwallchat.DBUpdate")){
                    mArrays.clear();
                    init(db);
                }
            }
        };
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("com.stark.wallwallchat.changeHead");
        intentFilter.addAction("com.stark.wallwallchat.DBUpdate");
        getActivity().registerReceiver(mReceiver, intentFilter);
        return view;
}

    public void init(SQLiteDatabase db){
        SharedPreferences sp=getActivity().getSharedPreferences("action", Context.MODE_PRIVATE);
        Cursor cr=db.query("userdata", new String[]{"nick", "auto","iknow", "knowme"}, "id=?", new String[]{sp.getString("id", null)}, null, null, null);
        if (cr != null && cr.getCount() > 0 && cr.moveToNext()) {
            mArrays.add(new ItemMargin(8));
            mArrays.add(new ItemRightHead(3, sp.getString("id", null), cr.getString(0), cr.getString(1)));
            Nick=cr.getString(0);
            Auto=cr.getString(1);
            mArrays.add(new ItemKnow(4, cr.getString(2), cr.getString(3)));
            mArrays.add(new ItemMargin(8));
            mArrays.add(new ItemSimpleList(6, "我的点赞", getResources().getDrawable(R.drawable.into_detial)));
            mArrays.add(new ItemSimpleList(6, "我的评论", getResources().getDrawable(R.drawable.into_detial)));
            mArrays.add(new ItemMargin(8));
            mArrays.add(new ItemSimpleList(6, "好友回复", getResources().getDrawable(R.drawable.into_detial)));
            mArrays.add(new ItemMargin(8));
            mArrays.add(new ItemSimpleList(6, "资料设置", getResources().getDrawable(R.drawable.into_detial)));
            mArrays.add(new ItemMargin(8));
            mArrays.add(new ItemSimpleList(6, "退出账号", getResources().getDrawable(R.drawable.into_detial)));
            cr.close();
        }
        adapter.notifyDataSetChanged();
    }
    private class MyOnItemClickListener implements AdapterView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            SharedPreferences sp=getActivity().getSharedPreferences("action", Context.MODE_PRIVATE);
            switch(position){
                case 1:
                    Intent intent=new Intent(getActivity(), HomepageActivity.class);
                    intent.putExtra("nick",Nick);
                    intent.putExtra("id", sp.getString("id", null));
                    intent.putExtra("auto",Auto);
                    startActivity(intent);
                    break;
                case 4:
                    Toast.makeText(getActivity(),"待开发",Toast.LENGTH_SHORT).show();
                    break;
                case 5:
                    Toast.makeText(getActivity(),"待开发",Toast.LENGTH_SHORT).show();
                    break;
                case 7:
                    Toast.makeText(getActivity(),"待开发",Toast.LENGTH_SHORT).show();
                    break;
                case 9:
                    Intent intent2= new Intent(getActivity(), EditInfoActivity.class);
                    startActivity(intent2);
                    break;
                case 11:
                    Intent intent3=new Intent(getActivity(),Login.class);
                    startActivity(intent3);
                    sp.edit().putBoolean("state",false).apply();
                    TransferActivity.This.finish();
                    getActivity().finish();
                    getActivity().overridePendingTransition(R.anim.anim_null,R.anim.anim_null);
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

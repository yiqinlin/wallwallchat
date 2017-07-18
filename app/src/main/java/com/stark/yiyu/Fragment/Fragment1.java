package com.stark.yiyu.Fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import com.stark.yiyu.Format.Refresh;
import com.stark.yiyu.Listview.MyListView;
import com.stark.yiyu.NetWork.NetPackage;
import com.stark.yiyu.NetWork.NetSocket;
import com.stark.yiyu.R;
import com.stark.yiyu.SQLite.DatabaseHelper;
import com.stark.yiyu.UIactivity.WallMsgActivity;
import com.stark.yiyu.adapter.MyAdapter;
import com.stark.yiyu.bean.BaseItem;
import com.stark.yiyu.bean.ItemWallInfo;
import com.stark.yiyu.json.JsonConvert;

import java.util.ArrayList;

/**
 * Created by Stark on 2017/2/14.
 */
public class Fragment1 extends Fragment{
    ArrayList<BaseItem> mArrays=null;
    MyAdapter adapter=null;
    MyListView listView;
    String SrcID;
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
                new AsyncTask<Void, Void, Void>() {
                    @Override
                    protected Void doInBackground(Void... params) {
                        Refresh refresh = (Refresh) NetPackage.getBag(NetSocket.request(NetPackage.Refresh(SrcID, "nsu", 0, 2, 1, "",0)));
                        mArrays.clear();
                        JsonConvert.UpdateWall(getActivity(), refresh, mArrays);
                        return null;
                    }

                    protected void onPostExecute(Void result) {
                        adapter.notifyDataSetChanged();
                        listView.onRefreshComplete();
                    }
                }.execute();
            }
        });
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

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }
}

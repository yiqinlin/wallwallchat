package com.stark.yiyu.Fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import com.stark.yiyu.Format.Get;
import com.stark.yiyu.Listview.ElasticListView;
import com.stark.yiyu.NetWork.NetPackage;
import com.stark.yiyu.NetWork.NetSocket;
import com.stark.yiyu.R;
import com.stark.yiyu.SQLite.DatabaseHelper;
import com.stark.yiyu.UIactivity.HomepageActivity;
import com.stark.yiyu.UIactivity.ZoneActivity;
import com.stark.yiyu.adapter.MyAdapter;
import com.stark.yiyu.bean.BaseItem;
import com.stark.yiyu.bean.ItemKnow;
import com.stark.yiyu.bean.ItemRightHead;
import com.stark.yiyu.bean.ItemSimpleList;
import com.stark.yiyu.json.JsonConvert;

import org.json.JSONArray;

import java.util.ArrayList;

/**
 * Created by Stark on 2017/2/14.
 */
public class Fragment3 extends Fragment {
    private ArrayList<BaseItem> mArrays=null;
    private MyAdapter adapter=null;
    private String Nick=null;
    private String Auto=null;
    @Override
    public View onCreateView(LayoutInflater inflater,ViewGroup container,Bundle savedInstanceState){
        View view=inflater.inflate(R.layout.transfer_right, container, false);
        mArrays=new ArrayList<BaseItem>();
        adapter=new MyAdapter(getActivity(),mArrays);
        MyAsyncTask asyncTask = new MyAsyncTask();
        asyncTask.execute();
        ElasticListView listView = (ElasticListView) view.findViewById(R.id.listView_right);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new MyOnItemClickListener());
        return view;
    }
    public void init(SQLiteDatabase db){
        SharedPreferences sp=getActivity().getSharedPreferences("action", Context.MODE_PRIVATE);
        db.execSQL("CREATE TABLE IF NOT EXISTS userdata(id varchar(20),nick varchar(16),auto varchar(50),sex integer,birth varchar(10),pnumber varchar(11),startdate varchar(10),catdate integer,typeface integer,theme integer,bubble integer,iknow integer,knowme integer)");
        Cursor cr=db.query("userdata", new String[]{"nick", "auto"}, "id=?", new String[]{sp.getString("id", null)}, null, null, null);
        if (cr != null && cr.getCount() > 0 && cr.moveToNext()) {
            mArrays.add(new ItemRightHead(3, sp.getString("id", null), getResources().getDrawable(R.drawable.tianqing), cr.getString(0), cr.getString(1)));
            Nick=cr.getString(0);
            Auto=cr.getString(1);
            cr.close();
        }
        cr=db.query("userdata", new String[]{"iknow", "knowme"}, "id=?", new String[]{sp.getString("id", null)}, null, null, null);
        if (cr != null && cr.getCount() > 0 && cr.moveToNext()) {
            mArrays.add(new ItemKnow(4, cr.getString(0), cr.getString(1)));
            cr.close();
        }
        mArrays.add(new ItemSimpleList(6,"世界",getResources().getDrawable(R.drawable.tianqing)));
        adapter.notifyDataSetChanged();
    }
    class MyAsyncTask extends AsyncTask<Void, Integer, Void> {
        SharedPreferences sp=getActivity().getSharedPreferences("action", Context.MODE_PRIVATE);
        SQLiteDatabase db=new DatabaseHelper(getActivity()).getWritableDatabase();
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            init(db);
        }
        @Override
        protected Void doInBackground(Void...values) {
            JsonConvert.UpdateDB(db, (Get) NetPackage.getBag(NetSocket.request(NetPackage.Get(sp.getString("id", null), 0, new JSONArray()))));
            publishProgress(0);
            return null;
        }
        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            if(values[0]==0){
                mArrays.clear();
                init(db);
            }
        }
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
}

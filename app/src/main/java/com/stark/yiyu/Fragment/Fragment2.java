package com.stark.yiyu.Fragment;

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

import com.stark.yiyu.Format.Msg;
import com.stark.yiyu.Listview.MyListView;
import com.stark.yiyu.NetWork.NetPackage;
import com.stark.yiyu.R;
import com.stark.yiyu.SQLite.Data;
import com.stark.yiyu.SQLite.DatabaseHelper;
import com.stark.yiyu.UIactivity.ChatActivity;
import com.stark.yiyu.Util.Try;
import com.stark.yiyu.adapter.MyAdapter;
import com.stark.yiyu.adapter.holder.ViewHolderMid;
import com.stark.yiyu.bean.BaseItem;
import com.stark.yiyu.bean.ItemMid;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Stark on 2017/2/14.
 */
public class Fragment2 extends Fragment {
    private BroadcastReceiver mReceiver;
    public static MyListView listView=null;
    @Override
    public View onCreateView(final LayoutInflater inflater,ViewGroup container,Bundle savesInstanceState){
        View view=inflater.inflate(R.layout.transfer_mid,container,false);
        final ArrayList<BaseItem> mArrays=new ArrayList<BaseItem>();
        final MyAdapter adapter=new MyAdapter(getActivity(),mArrays);
        listView= (MyListView) view.findViewById(R.id.listView_mid);
        listView.setAdapter(adapter);
        listView.setonRefreshListener(new MyListView.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new AsyncTask<Void, Void, Void>() {
                    @Override
                    protected Void doInBackground(Void... params) {
                        try {
                            Thread.sleep(1000);

                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        return null;
                    }

                    protected void onPostExecute(Void result) {
                        msgRefresh(adapter, mArrays);
                        listView.onRefreshComplete();
                    }
                }.execute();
            }
        });
        listView.setOnItemClickListener(new MyOnItemClickListener());
        mReceiver=new BroadcastReceiver(){
            public void onReceive(Context context, Intent intent) {
                SQLiteDatabase db=new DatabaseHelper(getActivity()).getWritableDatabase();
                Msg msg= (Msg)NetPackage.getBag(intent.getStringExtra("Msg"),intent.getStringExtra("BagType"));
                Cursor cursor = db.query("mid",null, "id=?", new String[]{msg.SrcId}, null, null, null);
                if(cursor!=null&&cursor.getCount()>0&&cursor.moveToNext()) {
                    db.update("mid", Data.getMidContentValues(null, null, msg.Remarks, msg.Msg + "", msg.Date + " " + msg.Time, "" + (Integer.parseInt(cursor.getString(5)) < 100 ? Integer.parseInt(cursor.getString(5)) + 1 : 100)), "id=?", new String[]{msg.SrcId});
                    cursor.close();
                }else {
                    db.insert("mid", null, Data.getMidContentValues(msg.SrcId, null, msg.Remarks, msg.Msg + "", msg.Date + " " + msg.Time, "1"));
                }
                db.close();
                msgRefresh(adapter, mArrays);
            }
        };
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("com.stark.yiyu.msg");
        getActivity().registerReceiver(mReceiver, intentFilter);
        msgRefresh(adapter,mArrays);
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

    private void msgRefresh(MyAdapter adapter,ArrayList<BaseItem> mArrays){
        SQLiteDatabase db=new DatabaseHelper(getActivity()).getWritableDatabase();
        Cursor cr=db.query("mid",null,null,null,null,null,"date desc");
        if(cr!=null&&cr.getCount()>0){
            mArrays.clear();
            Date date=new Date();
            DateFormat format=new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA);
            while(cr.moveToNext()){
                mArrays.add(new ItemMid(2, cr.getString(0), getResources().getDrawable(R.drawable.tianqing), cr.getString(2), cr.getString(3), format.format(date).equals(cr.getString(4).substring(0, 10))?cr.getString(4).substring(11, 16):cr.getString(4).substring(5,10), cr.getString(5)));
            }
            cr.close();
            adapter.notifyDataSetChanged();
        }
        db.close();
    }
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        // TODO Auto-generated method stub
        super.onActivityResult(requestCode, resultCode, intent);
        Log.i("code", requestCode + " " + resultCode);
        if (requestCode==0&&resultCode == 2) {
            String temp= Try.getStringExtra(intent, "id");
            if(temp!=null&&!temp.equals("")){
                SQLiteDatabase db=new DatabaseHelper(getActivity()).getWritableDatabase();
                db.update("mid", Data.getMidContentValues(null, null, null, null, null, "0"), "id=?", new String[]{temp});
                db.close();
            }
        }
    }
    private class MyOnItemClickListener implements AdapterView.OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            ViewHolderMid viewHolder=(ViewHolderMid)view.getTag();
            Intent intent=new Intent(getActivity(), ChatActivity.class);
            SQLiteDatabase db=new DatabaseHelper(getActivity()).getWritableDatabase();
            Cursor cr=db.query("mid", new String[]{"remarks"}, "id=?", new String[]{viewHolder.id}, null, null, null);
            if(cr!=null&&cr.getCount()>0&&cr.moveToNext()){
                intent.putExtra("nick", cr.getString(0));
                cr.close();
            }else {
                intent.putExtra("nick",viewHolder.id);
            }
            intent.putExtra("id", viewHolder.id);
            startActivityForResult(intent, 0);
            getActivity().overridePendingTransition(R.anim.anim_rtom, R.anim.anim_mtol);
            db.update("mid", Data.getMidContentValues(null, null, null, null, null, "0"), "id=?", new String[]{viewHolder.id});
            db.close();
            TextView temp=(TextView)view.findViewById(R.id.mid_list_count);
            temp.setText(null);
        }
    }

}

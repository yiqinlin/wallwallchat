package com.stark.yiyu.UIactivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.stark.yiyu.Database.DatabaseSchoolHelper;
import com.stark.yiyu.R;
import com.stark.yiyu.SortListView.CharacterParser;
import com.stark.yiyu.SortListView.PinyinComparator;
import com.stark.yiyu.SortListView.SideBar;
import com.stark.yiyu.Util.Status;
import com.stark.yiyu.adapter.SortAdapter;
import com.stark.yiyu.bean.SortModel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SortSchool extends Activity {

    private ListView sortListView;
    private SideBar sideBar;

    private TextView dialog;
    private SortAdapter adapter;
    private EditText mEditText;

    private CharacterParser characterParser;
    private List<SortModel> SourceDateList;

    private PinyinComparator pinyinComparator;

    boolean isProvince;//true 省份界面 false学校界面

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sort_school);
        Status.setTranslucentStatus(getWindow(), this, (LinearLayout) findViewById(R.id.transfer_title_status));
        ImageButton left=(ImageButton)findViewById(R.id.button_transfer_title_left);
        TextView mid=(TextView)findViewById(R.id.text_transfer_title);
        mid.setText("选 择");
        left.setBackgroundResource(R.drawable.title_back);
        left.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        Log.i("SortSchool", "start");
        isProvince = true;//省份界面
        SQLiteDatabase db = new DatabaseSchoolHelper(SortSchool.this).getWritableDatabase();
        Cursor cr = db.rawQuery("select * from data", null);

        if (!cr.moveToNext()) {
            DatabaseSchoolHelper.InitDatabase(SortSchool.this);
        }

        cr.close();
        db.close();
        initViews();
    }

    private void initViews() {
        characterParser = CharacterParser.getInstance();
        pinyinComparator = new PinyinComparator();

        sideBar = (SideBar) findViewById(R.id.sidrbar);
        dialog = (TextView) findViewById(R.id.dialog);
        sideBar.setTextView(dialog);

        sideBar.setOnTouchingLetterChangedListener(new SideBar.OnTouchingLetterChangedListener() {
            @Override
            public void onTouchingLetterChanged(String s) {
                int position = adapter.getPositionForSection(s.charAt(0));
                if (position != -1) {
                    sortListView.setSelection(position);
                }
            }
        });

        sortListView = (ListView) findViewById(R.id.country_lvcountry);
        sortListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (isProvince) {//省份界面进入学校界面
                    SQLiteDatabase db = new DatabaseSchoolHelper(SortSchool.this).getWritableDatabase();
                    Cursor cr = db.query("data", null, "province=?", new String[]{((SortModel) adapter.getItem(position)).getName()}, null, null, null);
                    if (cr != null && cr.getCount() > 0) {
                        isProvince = false;//学校界面，选择学校
                        SourceDateList.clear();
                        while (cr.moveToNext()) {
                            String name = cr.getString(cr.getColumnIndex("name"));
                            SortModel sortModel = new SortModel();
                            sortModel.setName(name);
                            String pinyin = characterParser.getSelling(name);
                            String sortString = pinyin.substring(0, 1).toUpperCase();
                            if (sortString.matches("[A-Z]")) {
                                sortModel.setSortLetters(sortString.toUpperCase());
                            } else {
                                sortModel.setSortLetters("#");
                            }
                            SourceDateList.add(sortModel);
                        }
                        Collections.sort(SourceDateList, pinyinComparator);
                        adapter.notifyDataSetChanged();
                    }
                    cr.close();
                    db.close();
                } else {//学校界面，选择学校
                    SQLiteDatabase db = new DatabaseSchoolHelper(SortSchool.this).getWritableDatabase();
                    Cursor cr = db.query("data", null, "name=?", new String[]{((SortModel) adapter.getItem(position)).getName()}, null, null, null);
                    String code = null;
                    if (cr != null && cr.getCount() > 0) {
                        cr.moveToNext();
                        code = cr.getString(cr.getColumnIndex("code"));

//                        Toast.makeText(SortSchool.this, code, Toast.LENGTH_SHORT).show();
                    }
                    cr.close();
                    db.close();
                    isProvince = true;
                    Intent it = new Intent();
                    it.putExtra("college", ((SortModel) adapter.getItem(position)).getName());
                    getSharedPreferences("action", Context.MODE_PRIVATE).edit().putString("edu",code).apply();
                    setResult(666, it);
                    finish();
                }
            }
        });
        SourceDateList = filledData();//省份界面

        //根据a-z进行排序源数据
        Collections.sort(SourceDateList, pinyinComparator);
        adapter = new SortAdapter(this, SourceDateList);
        sortListView.setAdapter(adapter);

        mEditText = (EditText) findViewById(R.id.filter_edit);
        mEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //当输入框里面的值为空，更新为原来的列表，否则为过滤数据列表
                filterData(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private List<SortModel> filledData() {//省份界面
        List<SortModel> mSortList = new ArrayList<SortModel>();
        SQLiteDatabase db = new DatabaseSchoolHelper(SortSchool.this).getWritableDatabase();
        Cursor cr = db.rawQuery("select distinct province from data", null);
        if (!cr.moveToNext()) {
            DatabaseSchoolHelper.InitDatabase(SortSchool.this);
        }
        cr.moveToFirst();
        do {
            SortModel sortModel = new SortModel();
            sortModel.setName(cr.getString(cr.getColumnIndex("province")));
            String pinyin = characterParser.getSelling(cr.getString(cr.getColumnIndex("province")));
            String sortString = pinyin.substring(0, 1).toUpperCase();
            if (sortString.matches("[A-Z]")) {
                sortModel.setSortLetters(sortString.toUpperCase());
            } else {
                sortModel.setSortLetters("#");
            }
            mSortList.add(sortModel);
        } while (cr.moveToNext());
        cr.close();
        db.close();
        return mSortList;
    }

    private void filterData(String filterStr) {
        List<SortModel> filterDateList = new ArrayList<SortModel>();

        if (TextUtils.isEmpty(filterStr)) {
            filterDateList = SourceDateList;
        } else {
            filterDateList.clear();
            for (SortModel sortModel : SourceDateList) {
                String name = sortModel.getName();
                if (name.toUpperCase().indexOf(filterStr.toString().toUpperCase())
                        != -1 || characterParser.getSelling(name).toUpperCase()
                        .startsWith(filterStr.toString().toUpperCase())) {
                    filterDateList.add(sortModel);
                }
            }
        }
        //根据a-z进行排序
        Collections.sort(filterDateList, pinyinComparator);
        adapter.updateListView(filterDateList);
    }

    @Override
    public void onBackPressed() {
        if (isProvince) {//显示省份, 按返回即返回
            super.onBackPressed();
        } else {//进入学校界面后 ， 按返回为省份界面
            isProvince = true;
            SourceDateList.clear();
            SQLiteDatabase db = new DatabaseSchoolHelper(SortSchool.this).getWritableDatabase();
            Cursor cr = db.rawQuery("select distinct province from data", null);
            if (!cr.moveToNext()) {
                DatabaseSchoolHelper.InitDatabase(SortSchool.this);
            }
            cr.moveToFirst();
            do {
                SortModel sortModel = new SortModel();
                sortModel.setName(cr.getString(cr.getColumnIndex("province")));
                String pinyin = characterParser.getSelling(cr.getString(cr.getColumnIndex("province")));
                String sortString = pinyin.substring(0, 1).toUpperCase();
                if (sortString.matches("[A-Z]")) {
                    sortModel.setSortLetters(sortString.toUpperCase());
                } else {
                    sortModel.setSortLetters("#");
                }
                SourceDateList.add(sortModel);
            } while (cr.moveToNext());
            Collections.sort(SourceDateList, pinyinComparator);
            adapter.notifyDataSetChanged();
        }
    }
}

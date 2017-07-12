package com.stark.yiyu.UIactivity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.stark.yiyu.R;
import com.stark.yiyu.SortListView.CharacterParser;
import com.stark.yiyu.SortListView.PinyinComparator;
import com.stark.yiyu.SortListView.SideBar;
import com.stark.yiyu.adapter.SortAdapter;
import com.stark.yiyu.bean.SortModel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SortSchool extends AppCompatActivity {

    private ListView sortListView;
    private SideBar sideBar;

    private TextView dialog;
    private SortAdapter adapter;
    private EditText mEditText;

    private CharacterParser characterParser;
    private List<SortModel> SourceDateList;

    private PinyinComparator pinyinComparator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sort_school);
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
//                Toast.makeText(SortSchool.this, ((SortModel) adapter.getItem(position)).getName(), Toast.LENGTH_SHORT).show();
                Intent it = new Intent();
                it.putExtra("school", ((SortModel) adapter.getItem(position)).getName());
                setResult(666, it);
                finish();
            }
        });

        SourceDateList = filledData(getResources().getStringArray(R.array.data));

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

    private List<SortModel> filledData(String[] date) {
        List<SortModel> mSortList = new ArrayList<SortModel>();

        for(int i = 0;i<date.length;i++) {
            SortModel sortModel = new SortModel();
            sortModel.setName(date[i]);
            String pinyin = characterParser.getSelling(date[i]);
            String sortString = pinyin.substring(0, 1).toUpperCase();
            if (sortString.matches("[A-Z]")) {
                sortModel.setSortLetters(sortString.toUpperCase());
            } else {
                sortModel.setSortLetters("#");
            }
            mSortList.add(sortModel);
        }
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
}

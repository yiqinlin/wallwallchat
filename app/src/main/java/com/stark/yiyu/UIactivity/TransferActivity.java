package com.stark.yiyu.UIactivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.stark.yiyu.Fragment.FragAdapter;
import com.stark.yiyu.Fragment.Fragment1;
import com.stark.yiyu.Fragment.Fragment2;
import com.stark.yiyu.Fragment.Fragment3;
import com.stark.yiyu.R;
import com.stark.yiyu.SQLite.DatabaseHelper;
import com.stark.yiyu.Util.Status;

import java.util.ArrayList;
import java.util.List;

public class TransferActivity extends FragmentActivity{
    static Button left=null;
    static Button mid=null;
    static Button right=null;
    static EditText input=null;
    private ImageButton titleLeft=null;
    private TextView title=null;
    private Button titleRight=null;
    public static Activity This;
    private LinearLayout mInputLine=null;
    private LinearLayout mTabLine=null;
    public static ViewPager vp=null;
    private String Nick=null;
    private String Auto=null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Status.setTranslucentStatus(getWindow());
        setContentView(R.layout.activity_transfer);
        Status.setTranslucentStatus(getWindow(), this, (LinearLayout) findViewById(R.id.transfer_title_status));
        This=TransferActivity.this;
        titleLeft=(ImageButton)findViewById(R.id.button_transfer_title_left);
        title=(TextView)findViewById(R.id.text_transfer_title);
        titleRight=(Button)findViewById(R.id.button_transfer_title_right);

        left=(Button)findViewById(R.id.button_tab_left);
        mid=(Button)findViewById(R.id.button_tab_mid);
        right=(Button)findViewById(R.id.button_tab_right);
        mInputLine=(LinearLayout)findViewById(R.id.layout_tab_input);
        mTabLine=(LinearLayout)findViewById(R.id.layout_tab);
        input=(EditText)findViewById(R.id.edit_chat_input);

        SQLiteDatabase db=new DatabaseHelper(TransferActivity.this).getWritableDatabase();
        SharedPreferences sp=TransferActivity.this.getSharedPreferences("action", Context.MODE_PRIVATE);
        final String SrcId=sp.getString("id", null);
        db.execSQL("CREATE TABLE IF NOT EXISTS userdata(id varchar(20),nick varchar(16),auto varchar(50),sex integer,birth varchar(10),pnumber varchar(11),startdate varchar(10),catdate integer,typeface integer,theme integer,bubble integer,iknow integer,knowme integer)");
        Cursor cr=db.query("userdata", new String[]{"nick", "auto"}, "id=?", new String[]{SrcId}, null, null, null);
        if (cr != null && cr.getCount() > 0 && cr.moveToNext()) {
            Nick=cr.getString(0);
            Auto=cr.getString(1);
            cr.close();
        }

        title.setText("消 息");
        titleRight.setText("添加");
        titleRight.setOnClickListener(Click);
        titleLeft.setBackgroundResource(R.drawable.tianqing);
        titleLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent =new Intent(TransferActivity.this,HomepageActivity.class);
                intent.putExtra("id",SrcId);
                intent.putExtra("Nick",Nick);
                intent.putExtra("Auto",Auto);
                startActivity(intent);
            }
        });
//        DisplayMetrics outMetrics=new DisplayMetrics();
//        getWindow().getWindowManager().getDefaultDisplay().getMetrics(outMetrics);
//        screenWidth=outMetrics.widthPixels;
        //构造适配器
        List<Fragment> fragments = new ArrayList<Fragment>();
        fragments.add(new Fragment1());
        fragments.add(new Fragment2());
        fragments.add(new Fragment3());
        FragAdapter adapter = new FragAdapter(getSupportFragmentManager(), fragments);
        //设定适配器
        vp = (ViewPager) findViewById(R.id.container);
        vp.setAdapter(adapter);
        vp.setCurrentItem(1);
        vp.setOnPageChangeListener(new TabOnPageChangeListener());
        left.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setViewPager(0);
            }
        });
        mid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setViewPager(1);
            }
        });
        right.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setViewPager(2);
            }
        });
    }
    public static void setViewPager(int i){
        vp.setCurrentItem(i);
    }
    public class TabOnPageChangeListener implements ViewPager.OnPageChangeListener {

        //当滑动状态改变时调用
        public void onPageScrollStateChanged(int state) {
            InputMethodManager imm=(InputMethodManager)TransferActivity.this.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(input.getWindowToken(), 0);
            Fragment2.reset();
        }
        //当前页面被滑动时调用
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels){
        }
        //当新的页面被选中时调用
        public void onPageSelected(int position) {
            switch (position){
                case 0:
                    left.setBackgroundResource(R.drawable.theme_transfer_tab_group_focused);
                    mid.setBackgroundResource(R.drawable.theme_transfer_tab_message_nor);
                    right.setBackgroundResource(R.drawable.theme_transfer_tab_me_nor);
                    title.setText("世 界");
                    titleRight.setText("消息");
                    break;
                case 1:
                    left.setBackgroundResource(R.drawable.theme_transfer_tab_group_nor);
                    mid.setBackgroundResource(R.drawable.theme_transfer_tab_message_focused);
                    right.setBackgroundResource(R.drawable.theme_transfer_tab_me_nor);
                    title.setText("消 息");
                    titleRight.setText("添加");
                    break;
                case 2:
                    left.setBackgroundResource(R.drawable.theme_transfer_tab_group_nor);
                    mid.setBackgroundResource(R.drawable.theme_transfer_tab_message_nor);
                    right.setBackgroundResource(R.drawable.theme_transfer_tab_me_focused);
                    title.setText("我");
                    titleRight.setText("设置");
                    break;
            }
            if(position==0&&mInputLine.getVisibility()==View.GONE){
                Animation btom= AnimationUtils.loadAnimation(TransferActivity.this,R.anim.anim_btom);
                Animation mtob= AnimationUtils.loadAnimation(TransferActivity.this,R.anim.anim_mtob);
                mTabLine.setAnimation(mtob);
                mInputLine.setAnimation(btom);
                mTabLine.setVisibility(View.GONE);
                mInputLine.setVisibility(View.VISIBLE);
            }
            else if(mInputLine.getVisibility()==View.VISIBLE){
                Animation mtob= AnimationUtils.loadAnimation(TransferActivity.this,R.anim.anim_mtob);
                Animation btom= AnimationUtils.loadAnimation(TransferActivity.this,R.anim.anim_btom);
                mTabLine.setAnimation(btom);
                mInputLine.setAnimation(mtob);
                mTabLine.setVisibility(View.VISIBLE);
                mInputLine.setVisibility(View.GONE);
            }
        }
    }
    View.OnClickListener Click=new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (vp.getCurrentItem()){
                case 0:
                    setViewPager(1);
                    break;
                case 1:
                    Intent intent=new Intent(TransferActivity.this, AddActivity.class);
                    intent.putExtra("title","添 加");
                    intent.putExtra("Mode",1);
                    intent.putExtra("TouchMode", WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
                    startActivity(intent);
                    break;
                case 2:
                    Intent intent1 = new Intent(TransferActivity.this, SetActivity.class);
                    startActivityForResult(intent1, 2);
                    break;
            }
        }
    };
}

package com.stark.yiyu.UIactivity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.stark.yiyu.MyService;
import com.stark.yiyu.R;
import com.stark.yiyu.Util.Status;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class WelcomeActivity extends Activity {

    private final List<View> viewList = new ArrayList<View>();// 将要分页显示的View装入数组中
    private TextView point1=null;
    private TextView point2=null;
    private TextView point3=null;
    private TextView pointTemp=null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Status.setTranslucentStatus(getWindow());
        Intent service=new Intent(WelcomeActivity.this, MyService.class);
        service.putExtra("CMD", "Welcome");
        startService(service);
        SharedPreferences sp = WelcomeActivity.this.getSharedPreferences("action", MODE_PRIVATE);
        if(sp.getBoolean("first",true)) {
            setContentView(R.layout.activity_welcome);
            sp.edit().putBoolean("first",false).apply();
            ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);
            LayoutInflater inflater = getLayoutInflater();
            View view1=inflater.inflate(R.layout.test, null);
            View view2=inflater.inflate(R.layout.test2, null);
            View view3=inflater.inflate(R.layout.test3, null);
            viewList.add(view1);
            viewList.add(view2);
            viewList.add(view3);
            viewPager.setAdapter(new MyPagerAdapter(viewList));
            viewPager.setOnPageChangeListener(new MyPageChangeListener());
            point1=(TextView)findViewById(R.id.textView_point1);
            point2=(TextView)findViewById(R.id.textView_point2);
            point3=(TextView)findViewById(R.id.textView_point3);
            Button start=(Button)view3.findViewById(R.id.button_start);
            point1.setBackgroundResource(R.drawable.indication_point_light);
            pointTemp=point1;
            Date date=new Date();
            DateFormat format=new SimpleDateFormat("yyyyMMddHHmmssSSS", Locale.CHINA);
            String MyUUID= format.format(date)+(int)(Math.random()*1000);
            sp.edit().putString("MyUUID", MyUUID).apply();
            start.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent=new Intent(WelcomeActivity.this,Login.class);
                    startActivity(intent);
                    finish();
                    overridePendingTransition(R.anim.anim_null, R.anim.anim_null);
                }
            });
        }
        else {
            setContentView(R.layout.activity_init);
            String id=sp.getString("id",null);
            String password=sp.getString("password",null);
            Class clazz=TransferActivity.class;
            if(!sp.getBoolean("state",false)||id==null||password==null) {
                clazz=Login.class;
            }
            final Class finalTemp=clazz;
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    Intent intent = new Intent(WelcomeActivity.this, finalTemp);
                    startActivity(intent);
                    finish();
                    overridePendingTransition(R.anim.anim_null, R.anim.anim_null);
                }
            }, 1000);
        }
    }
    //页面改变监听器
    public class MyPageChangeListener implements ViewPager.OnPageChangeListener {

        @Override
        public void onPageSelected(int arg0) {
            pointTemp.setBackgroundResource(R.drawable.indication_point_dark);
            switch (arg0) {
                case 0:
                    if(pointTemp!=point1) {
                        point1.setBackgroundResource(R.drawable.indication_point_light);
                        pointTemp = point1;
                    }
                    break;
                case 1:
                    if(pointTemp!=point2) {
                        point2.setBackgroundResource(R.drawable.indication_point_light);
                        pointTemp = point2;
                    }
                    break;
                case 2:
                    if(pointTemp!=point3) {
                        point3.setBackgroundResource(R.drawable.indication_point_light);
                        pointTemp = point3;
                    }
                    break;
            }
//            currIndex = arg0;
//            animation.setFillAfter(true);// True:图片停在动画结束位置
//            animation.setDuration(300);
//            cursor.startAnimation(animation);
        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {
        }

        @Override
        public void onPageScrollStateChanged(int arg0) {
        }
    }

   /**
     * ViewPager适配器
     */
    public class MyPagerAdapter extends PagerAdapter {
        public List<View> mListViews;

        public MyPagerAdapter(List<View> mListViews) {
            this.mListViews = mListViews;
        }

        @Override
        public boolean isViewFromObject(View arg0, Object arg1) {
            // TODO Auto-generated method stub
            return arg0 == arg1;
        }

        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return mListViews.size();
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            // TODO Auto-generated method stub
            container.removeView(mListViews.get(position));
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            // TODO Auto-generated method stub
            container.addView(mListViews.get(position));

            return mListViews.get(position);
        }
    }

}

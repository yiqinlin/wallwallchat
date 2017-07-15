package com.stark.yiyu.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.stark.yiyu.MyService;
import com.stark.yiyu.R;
import com.stark.yiyu.UIactivity.AddActivity;
import com.stark.yiyu.UIactivity.DetailActivity;
import com.stark.yiyu.UIactivity.WallMsgActivity;
import com.stark.yiyu.adapter.holder.ItemType;
import com.stark.yiyu.adapter.holder.ViewHolderEditInfo;
import com.stark.yiyu.adapter.holder.ViewHolderEditInfo2;
import com.stark.yiyu.adapter.holder.ViewHolderEditMail;
import com.stark.yiyu.adapter.holder.ViewHolderHomepageTitle;
import com.stark.yiyu.adapter.holder.ViewHolderKnow;
import com.stark.yiyu.adapter.holder.ViewHolderMargin;
import com.stark.yiyu.adapter.holder.ViewHolderMid;
import com.stark.yiyu.adapter.holder.ViewHolderRightHead;
import com.stark.yiyu.adapter.holder.ViewHolderSChat;
import com.stark.yiyu.adapter.holder.ViewHolderSimpleList;
import com.stark.yiyu.adapter.holder.ViewHolderTextSeparate;
import com.stark.yiyu.adapter.holder.ViewHolderWallInfo;
import com.stark.yiyu.bean.BaseItem;
import com.stark.yiyu.bean.ItemEditInfo;
import com.stark.yiyu.bean.ItemEditInfo2;
import com.stark.yiyu.bean.ItemEditMail;
import com.stark.yiyu.bean.ItemHomepageTitle;
import com.stark.yiyu.bean.ItemKnow;
import com.stark.yiyu.bean.ItemMargin;
import com.stark.yiyu.bean.ItemMid;
import com.stark.yiyu.bean.ItemRightHead;
import com.stark.yiyu.bean.ItemSMsg;
import com.stark.yiyu.bean.ItemSimpleList;
import com.stark.yiyu.bean.ItemTextSeparate;
import com.stark.yiyu.bean.ItemWallInfo;
import com.stark.yiyu.toast.ListAnimImageView;

import java.util.ArrayList;

public class MyAdapter extends BaseAdapter {

    private Context mContext = null;//上下文
    private LayoutInflater mInflater = null;
    private ArrayList<BaseItem> mData = null;//要显示的数据
    private Callback mCallback;

    public MyAdapter(Context context, ArrayList<BaseItem> data){
        this.mContext=context;
        this.mInflater = LayoutInflater.from(context);
        this.mData = data;
    }

    //添加一个新的Item，并通知listview进行显示刷新
    public void addItem(BaseItem newItem){
        this.mData.add(newItem);
        this.notifyDataSetChanged();
    }

public interface Callback{
    public void click(View v);
}



    @Override
    public int getItemViewType(int position) {

        return mData.get(position).getitemType();
    }

    @Override
    public int getViewTypeCount() {
        return ItemType.ITEM_TYPE_MAX_COUNT;
    }

    @Override
    public int getCount() {
        if(mData == null){
            return 0;
        }
        return this.mData.size();
    }

    @Override
    public Object getItem(int i) {

        return mData.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup) {
        int itemType = this.getItemViewType(position);
        switch (itemType){
            case 0:
            case 1:
                convertView=getSChatConvertView(itemType, position, convertView);
                break;
            case 2:
                convertView=getMidConvertView(position, convertView);
                break;
            case 3:
                convertView=getRightHeadConvertView(position, convertView);
                break;
            case 4:
                convertView=getKnowConvertView(position, convertView);
                break;
            case 5:
                convertView=getHomepageTitleConvertView(position, convertView);
                break;
            case 6:
                convertView=getSimpleListConvertView(position, convertView);
                break;
            case 7://俩个TextView
                convertView = getEditInfoConvertView(position, convertView);
                break;
            case 8://空白间隔:
                convertView = getMarginConvertView(position, convertView);
                break;
            case 9://一个TextView，一个EditText
                convertView = getEditInfo2ConvertView(position, convertView);
                break;
            case 10://一个TextView，一个邮箱格式的EditText:
                convertView = getEditMailConvertView(position, convertView);
                break;
            case 11:
            case 12:
                convertView= getWallInfoConvertView(itemType,position, convertView);
                break;
            case 13:
                convertView=getTextSeparateConVertView(position,convertView);
            case 7://俩个TextView
                convertView = getEditInfoConvertView(position, convertView);
                break;
            case 8://空白间隔:
                convertView = getMarginConvertView(position, convertView);
                break;
            case 9://一个TextView，一个EditText
                convertView = getEditInfo2ConvertView(position, convertView);
                break;
            case 10://一个TextView，一个邮箱格式的EditText:
                convertView = getEditMailConvertView(position, convertView);
                break;
        }
        return convertView;
    }
    private View getTextSeparateConVertView(int position,View convertView){
        ViewHolderTextSeparate viewHolder;
        ItemTextSeparate msg=(ItemTextSeparate)mData.get(position);
        if(convertView==null){
            viewHolder=new ViewHolderTextSeparate();
            convertView = mInflater.inflate(R.layout.list_comment_title, null);
            viewHolder.Text=(TextView)convertView.findViewById(R.id.list_comment_title);
            convertView.setTag(viewHolder);
        }else{
            viewHolder=(ViewHolderTextSeparate)convertView.getTag();
        }
        viewHolder.Text.setText(msg.getText());
        return convertView;
    }
    private View getMidConvertView(int position,View convertView){
        ViewHolderMid viewHolder;
        ItemMid msg =(ItemMid)mData.get(position);
        if(convertView==null) {
            viewHolder=new ViewHolderMid();
            convertView = mInflater.inflate(R.layout.list_msg, null);
            viewHolder.head = (ImageView) convertView.findViewById(R.id.mid_list_head);
            viewHolder.nick = (TextView) convertView.findViewById(R.id.mid_list_nick);
            viewHolder.message = (TextView) convertView.findViewById(R.id.mid_list_msg);
            viewHolder.date = (TextView) convertView.findViewById(R.id.mid_list_date);
            viewHolder.count = (TextView) convertView.findViewById(R.id.mid_list_count);
            convertView.setTag(viewHolder);
        }else{
            viewHolder=(ViewHolderMid)convertView.getTag();
        }
        viewHolder.id=msg.getId();
        viewHolder.head.setImageDrawable(msg.getHead());
        viewHolder.nick.setText(msg.getNick());
        viewHolder.message.setText(msg.getMsg());
        viewHolder.date.setText(msg.getDate());
        String tempStr = msg.getCount();
        viewHolder.count.setText("0".equals(tempStr) ? null : "100".equals(tempStr) ? "99+" : tempStr);
        return convertView;
    }
    private View getSChatConvertView(int type,int position,View convertView){
        ViewHolderSChat viewHolder;
        final ItemSMsg msg =(ItemSMsg)mData.get(position);
        if(convertView==null) {
            viewHolder=new ViewHolderSChat();
            if(type==0) {
                convertView = mInflater.inflate(R.layout.list_item_chat_su, null);
                viewHolder.head = (ImageButton) convertView.findViewById(R.id.list_chat_su_head);
                viewHolder.message = (TextView) convertView.findViewById(R.id.list_chat_su_msg);
                viewHolder.state=(ListAnimImageView)convertView.findViewById(R.id.list_chat_su_ListAnim);
            }else if(type==1){
                convertView = mInflater.inflate(R.layout.list_item_chat_sf, null);
                viewHolder.head = (ImageButton) convertView.findViewById(R.id.list_chat_sf_head);
                viewHolder.message = (TextView) convertView.findViewById(R.id.list_chat_sf_msg);
            }
            convertView.setTag(viewHolder);
        }else{
            viewHolder=(ViewHolderSChat) convertView.getTag();
        }
        viewHolder.head.setBackgroundDrawable(msg.getHead());
        viewHolder.message.setText(msg.getMsg());
        viewHolder.message.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (msg.getSendType() == 1) {
                    Intent intent = new Intent(mContext, DetailActivity.class);
                    intent.putExtra("id", msg.getid());
                    intent.putExtra("bubble", msg.getBubble());
                    intent.putExtra("msg", msg.getMsg());
                    intent.putExtra("date", msg.getDate());
                    intent.putExtra("time", msg.getTime());
                    mContext.startActivity(intent);
                }
            }
        });
        if(type==0) {
            if (msg.State == 0) {
                viewHolder.state.setVisibility(View.VISIBLE);
            } else if (msg.State == 1) {
                viewHolder.state.setVisibility(View.GONE);
            }
        }
        return convertView;
    }
    private View getRightHeadConvertView(int position,View convertView){
        ViewHolderRightHead viewHolder;
        ItemRightHead msg =(ItemRightHead)mData.get(position);
        if(convertView==null) {
            viewHolder=new ViewHolderRightHead();
            convertView = mInflater.inflate(R.layout.list_right_head, null);
            viewHolder.head = (ImageView) convertView.findViewById(R.id.right_head_img);
            viewHolder.nick = (TextView) convertView.findViewById(R.id.right_head_nick);
            viewHolder.auto = (TextView) convertView.findViewById(R.id.right_head_auto);
            convertView.setTag(viewHolder);
        }else{
            viewHolder=(ViewHolderRightHead)convertView.getTag();
        }
        viewHolder.id=msg.getId();
        viewHolder.head.setImageDrawable(msg.getHead());
        viewHolder.nick.setText(msg.getNick());
        viewHolder.auto.setText(msg.getAuto());
        return convertView;
    }
    private View getKnowConvertView(int position,View convertView){
        ViewHolderKnow viewHolder;
        ItemKnow msg =(ItemKnow)mData.get(position);
        if(convertView==null) {
            viewHolder=new ViewHolderKnow();
            convertView = mInflater.inflate(R.layout.list_know, null);
            viewHolder.I = (TextView) convertView.findViewById(R.id.list_know_i);
            viewHolder.Me = (TextView) convertView.findViewById(R.id.list_know_me);
            viewHolder.ToI=(Button)convertView.findViewById(R.id.list_button_know_i);
            viewHolder.Tome=(Button)convertView.findViewById(R.id.list_button_know_me);
            convertView.setTag(viewHolder);
        }else{
            viewHolder= (ViewHolderKnow)convertView.getTag();
        }
        viewHolder.I.setText(msg.geti());
        viewHolder.Me.setText(msg.getme());
        viewHolder.ToI.setOnClickListener(Click);
        viewHolder.Tome.setOnClickListener(Click);
        return convertView;
    }
    private View getHomepageTitleConvertView(int position,View convertView){
        ViewHolderHomepageTitle viewHolder;
        ItemHomepageTitle msg=(ItemHomepageTitle)mData.get(position);
        if(convertView==null){
            viewHolder=new ViewHolderHomepageTitle();
            convertView = mInflater.inflate(R.layout.list_homepage_title,null);
            viewHolder.head=(ImageButton)convertView.findViewById(R.id.list_homepage_head);
            viewHolder.nick=(Button)convertView.findViewById(R.id.list_homepage_nick);
            viewHolder.auto=(Button)convertView.findViewById(R.id.list_homepage_auto);
            convertView.setTag(viewHolder);
        }else{
            viewHolder=(ViewHolderHomepageTitle)convertView.getTag();
        }
        viewHolder.id=msg.getId();
        viewHolder.head.setBackgroundDrawable(msg.getHead());
        mCallback = (Callback) mContext;
        viewHolder.nick.setText(msg.getNick());
        viewHolder.auto.setText(msg.getAuto());
        viewHolder.head.setOnClickListener(Click);
        viewHolder.nick.setOnClickListener(Click);
        viewHolder.auto.setOnClickListener(Click);
        return convertView;
    }
    private View getWallInfoConvertView(int type,int position, View convertView){
        ViewHolderWallInfo viewHolder;
        final ItemWallInfo msg=(ItemWallInfo)mData.get(position);
        if(convertView==null){
            viewHolder=new ViewHolderWallInfo();
            viewHolder.id=msg.getId();
            if(type==11) {
                convertView=mInflater.inflate(R.layout.list_info_ordinary, null);
                viewHolder.head = (ImageButton) convertView.findViewById(R.id.list_info_ordinary_head);
                viewHolder.nick = (TextView) convertView.findViewById(R.id.list_info_ordinary_nick);
                viewHolder.linear = (LinearLayout) convertView.findViewById(R.id.list_info_ordinary_linear);
                viewHolder.time = (TextView) convertView.findViewById(R.id.list_info_ordinary_time);
                viewHolder.more = (ImageButton) convertView.findViewById(R.id.list_info_ordinary_more);
                viewHolder.content = (TextView) convertView.findViewById(R.id.list_info_ordinary_content);
            }else if(type==12){
                convertView=mInflater.inflate(R.layout.list_info_anonymous, null);
                viewHolder.linear=(LinearLayout)convertView.findViewById(R.id.list_info_anonymous_linear);
                viewHolder.time=(TextView)convertView.findViewById(R.id.list_info_anonymous_time);
                viewHolder.more=(ImageButton)convertView.findViewById(R.id.list_info_anonymous_more);
                viewHolder.content=(TextView)convertView.findViewById(R.id.list_info_anonymous_content);
            }
            viewHolder.comment=(ImageButton)convertView.findViewById(R.id.list_info_comment);
            viewHolder.cnum=(TextView)convertView.findViewById(R.id.list_info_cnum);
            viewHolder.agree =(ImageButton)convertView.findViewById(R.id.list_info_agree);
            viewHolder.anum =(TextView)convertView.findViewById(R.id.list_info_anum);
            convertView.setTag(viewHolder);
        }else{
            viewHolder=(ViewHolderWallInfo)convertView.getTag();
        }
        if(type==11) {
            viewHolder.head.setBackgroundDrawable(msg.getHead());
            viewHolder.nick.setText(msg.getNick());
        }
        viewHolder.linear.setBackgroundDrawable(mContext.getResources().getDrawable(msg.getType()));
        viewHolder.time.setText(msg.getTime());
        viewHolder.more.setOnClickListener(Click);
        viewHolder.content.setText(msg.getContent());
        viewHolder.comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(mContext, WallMsgActivity.class);
                intent.putExtra("type",msg.getType());
                intent.putExtra("id",msg.getId());
                intent.putExtra("msgcode",msg.getMsgcode());
                intent.putExtra("nick",msg.getNick());
                intent.putExtra("time",msg.getTime());
                intent.putExtra("content",msg.getContent());
                intent.putExtra("anum",msg.getAnum());
                intent.putExtra("cnum",msg.getCnum());
                mContext.startActivity(intent);
//                Intent intent=new Intent(mContext, MyService.class);
//                intent.putExtra("CMD","Comment");
//                intent.putExtra("msgcode",msg.getMsgcode());
//                intent.putExtra("receiver",msg.getId());
//                intent.putExtra("mode",0);
//                intent.putExtra("type",0);
//                mContext.startService(intent);
            }
        });
        viewHolder.cnum.setText(msg.getCnum());
        viewHolder.agree.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(mContext, MyService.class);
                intent.putExtra("CMD","Agree");
                intent.putExtra("msgcode",msg.getMsgcode());
                intent.putExtra("receiver",msg.getId());
                intent.putExtra("mode",1);
                intent.putExtra("type",0);
                mContext.startService(intent);
            }
        });
        viewHolder.anum.setText(msg.getAnum());
        return convertView;
    }
    private View getSimpleListConvertView(int position,View convertView){
        ViewHolderSimpleList viewHolder;
        ItemSimpleList msg=(ItemSimpleList)mData.get(position);
        if(convertView==null){
            viewHolder=new ViewHolderSimpleList();
            convertView=mInflater.inflate(R.layout.list_simple,null);
            viewHolder.Text=(TextView)convertView.findViewById(R.id.list_simple_text);
            viewHolder.Image=(ImageView)convertView.findViewById(R.id.list_simple_image);
            convertView.setTag(viewHolder);
        }else{
            viewHolder=(ViewHolderSimpleList)convertView.getTag();
        }
        viewHolder.Text.setText(msg.getText());
        viewHolder.Image.setImageDrawable(msg.getImage());
        return convertView;
    }

    private View getEditInfoConvertView(int position, View convertView) {
        ViewHolderEditInfo viewHolder;
        ItemEditInfo msg = (ItemEditInfo) mData.get(position);
        if (convertView == null) {
            viewHolder = new ViewHolderEditInfo();
            convertView = mInflater.inflate(R.layout.list_editinfo, null);
            viewHolder.txvLeft = (TextView) convertView.findViewById(R.id.list_txv_left);
            viewHolder.txvRight = (TextView) convertView.findViewById(R.id.list_txv_right);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolderEditInfo) convertView.getTag();
        }
        viewHolder.txvLeft.setText(msg.getStrLeft());
        viewHolder.txvRight.setText(msg.getStrRight());
        return convertView;
    }

    private View getMarginConvertView(int position, View convertView) {
        ViewHolderMargin viewHolder;
        ItemMargin msg = (ItemMargin) mData.get(position);
        if (convertView == null) {
            viewHolder = new ViewHolderMargin();
            convertView = mInflater.inflate(R.layout.list_margin, null);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolderMargin) convertView.getTag();
        }
        return convertView;
    }

    private View getEditInfo2ConvertView(int position, View convertView) {
        ViewHolderEditInfo2 viewHolder;
        ItemEditInfo2 msg = (ItemEditInfo2) mData.get(position);
        if (convertView == null) {
            viewHolder = new ViewHolderEditInfo2();
            convertView = mInflater.inflate(R.layout.list_editinfo2, null);
            viewHolder.txvLeft = (TextView) convertView.findViewById(R.id.list_txv_left);
            viewHolder.edtRight = (EditText) convertView.findViewById(R.id.list_edt_right);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolderEditInfo2) convertView.getTag();
        }
        viewHolder.txvLeft.setText(msg.getStrLeft());
        viewHolder.edtRight.setText(msg.getStrRight());
        return convertView;
    }

    private View getEditMailConvertView(int position, View converView) {
        ViewHolderEditMail viewHolder;
        ItemEditMail msg = (ItemEditMail) mData.get(position);
        if (converView == null) {
            viewHolder = new ViewHolderEditMail();
            converView = mInflater.inflate(R.layout.list_editmail, null);
            viewHolder.txvLeft = (TextView) converView.findViewById(R.id.list_txv_left);
            viewHolder.edtRight = (EditText) converView.findViewById(R.id.list_edt_right);
            converView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolderEditMail) converView.getTag();
        }
        viewHolder.txvLeft.setText(msg.getStrLeft());
        viewHolder.edtRight.setText(msg.getStrRight());
        return converView;
    }

    View.OnClickListener Click=new View.OnClickListener(){
        @Override
        public void onClick(View v) {
            Intent intent=new Intent(mContext, AddActivity.class);
            switch(v.getId()){
                case R.id.list_button_know_i:
                    intent.putExtra("title","我认识的");
                    intent.putExtra("Mode",2);
                    intent.putExtra("TouchMode", WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
                    mContext.startActivity(intent);
                break;
                case R.id.list_button_know_me:
                    intent.putExtra("title","认识我的");
                    intent.putExtra("Mode",3);
                    intent.putExtra("TouchMode",WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
                    mContext.startActivity(intent);
                break;
                case R.id.list_homepage_head:
                    mCallback.click(v);
                    break;
                case R.id.list_homepage_nick:
                    mCallback.click(v);
                    break;
                case R.id.list_homepage_auto:
                    mCallback.click(v);
            }
        }
    };
}

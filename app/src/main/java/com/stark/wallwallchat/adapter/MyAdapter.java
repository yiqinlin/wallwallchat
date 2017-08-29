package com.stark.wallwallchat.adapter;

import android.content.Context;
import android.content.Intent;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
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

import com.stark.wallwallchat.MyService;
import com.stark.wallwallchat.R;
import com.stark.wallwallchat.UIactivity.AddActivity;
import com.stark.wallwallchat.UIactivity.DetailActivity;
import com.stark.wallwallchat.UIactivity.WallMsgActivity;
import com.stark.wallwallchat.adapter.holder.ItemType;
import com.stark.wallwallchat.adapter.holder.ViewHolderEditInfo;
import com.stark.wallwallchat.adapter.holder.ViewHolderEditInfo2;
import com.stark.wallwallchat.adapter.holder.ViewHolderEditMail;
import com.stark.wallwallchat.adapter.holder.ViewHolderHomepageTitle;
import com.stark.wallwallchat.adapter.holder.ViewHolderKnow;
import com.stark.wallwallchat.adapter.holder.ViewHolderMargin;
import com.stark.wallwallchat.adapter.holder.ViewHolderMid;
import com.stark.wallwallchat.adapter.holder.ViewHolderRightHead;
import com.stark.wallwallchat.adapter.holder.ViewHolderSChat;
import com.stark.wallwallchat.adapter.holder.ViewHolderSimpleList;
import com.stark.wallwallchat.adapter.holder.ViewHolderTextSeparate;
import com.stark.wallwallchat.adapter.holder.ViewHolderWallInfo;
import com.stark.wallwallchat.bean.BaseItem;
import com.stark.wallwallchat.bean.ItemEditInfo;
import com.stark.wallwallchat.bean.ItemEditInfo2;
import com.stark.wallwallchat.bean.ItemEditMail;
import com.stark.wallwallchat.bean.ItemHomepageTitle;
import com.stark.wallwallchat.bean.ItemKnow;
import com.stark.wallwallchat.bean.ItemMargin;
import com.stark.wallwallchat.bean.ItemMid;
import com.stark.wallwallchat.bean.ItemRightHead;
import com.stark.wallwallchat.bean.ItemSMsg;
import com.stark.wallwallchat.bean.ItemSimpleList;
import com.stark.wallwallchat.bean.ItemTextSeparate;
import com.stark.wallwallchat.bean.ItemWallInfo;
import com.stark.wallwallchat.toast.ListAnimImageView;

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
            case 13:
            case 14:
                convertView= getWallInfoConvertView(itemType,position, convertView);
                break;
            case 15:
                convertView=getTextSeparateConVertView(position,convertView);
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
            viewHolder.nick=(TextView)convertView.findViewById(R.id.list_homepage_nick);
            viewHolder.auto=(TextView)convertView.findViewById(R.id.list_homepage_auto);
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
    private View getWallInfoConvertView(int type,final int position, View convertView){
        ViewHolderWallInfo viewHolder;
        ItemWallInfo msg=(ItemWallInfo)mData.get(position);
        if(convertView==null){
            viewHolder=new ViewHolderWallInfo();
            viewHolder.id=msg.getId();
            switch (type){
                case 11:
                    convertView=mInflater.inflate(R.layout.list_info_ordinary, null);
                    viewHolder.head = (ImageButton) convertView.findViewById(R.id.list_info_ordinary_head);
                    viewHolder.nick = (TextView) convertView.findViewById(R.id.list_info_ordinary_nick);
                    viewHolder.linear = (LinearLayout) convertView.findViewById(R.id.list_info_ordinary_linear);
                    viewHolder.more = (ImageButton) convertView.findViewById(R.id.list_info_ordinary_more);
                    break;
                case 12:
                    convertView=mInflater.inflate(R.layout.list_info_anonymous, null);
                    viewHolder.linear=(LinearLayout)convertView.findViewById(R.id.list_info_anonymous_linear);
                    viewHolder.more=(ImageButton)convertView.findViewById(R.id.list_info_anonymous_more);
                    break;
                case 13:
                    convertView=mInflater.inflate(R.layout.list_wall_comment, null);
                    viewHolder.head = (ImageButton) convertView.findViewById(R.id.list_info_comment_head);
                    viewHolder.nick = (TextView) convertView.findViewById(R.id.list_info_comment_nick);
                    viewHolder.reply=(TextView)convertView.findViewById(R.id.list_comment_reply);
                    break;
                case 14:
                    convertView=mInflater.inflate(R.layout.list_wall_comment_anonymous, null);
                    viewHolder.reply=(TextView)convertView.findViewById(R.id.list_comment_reply);
                    break;
            }
            viewHolder.content = (TextView) convertView.findViewById(R.id.list_info_content);
            viewHolder.time = (TextView) convertView.findViewById(R.id.list_info_time);
            viewHolder.comment=(ImageButton)convertView.findViewById(R.id.list_info_comment);
            viewHolder.cnum=(TextView)convertView.findViewById(R.id.list_info_cnum);
            viewHolder.agree =(ImageButton)convertView.findViewById(R.id.list_info_agree);
            viewHolder.anum =(TextView)convertView.findViewById(R.id.list_info_anum);
            convertView.setTag(viewHolder);
        }else{
            viewHolder=(ViewHolderWallInfo)convertView.getTag();
        }
        if(type==11||type==13){
            viewHolder.head.setBackgroundDrawable(msg.getHead());
            viewHolder.nick.setText(msg.getNick());
        }
       switch (type) {
           case 11:
           case 12:
               viewHolder.linear.setBackgroundDrawable(mContext.getResources().getDrawable(msg.getType()));
               viewHolder.more.setOnClickListener(Click);
               break;
           case 13:
           case 14:
               if(msg.getNick2()!=null) {
                   viewHolder.reply.setVisibility(View.VISIBLE);
                   viewHolder.reply.setText("回复: "+msg.getNick2());
               }else{
                   viewHolder.reply.setVisibility(View.GONE);
               }
               break;
        }
        viewHolder.comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, WallMsgActivity.class);
                intent.putExtra("sponsor", ((ItemWallInfo) mData.get(position)).getId());
                intent.putExtra("msgcode", ((ItemWallInfo)mData.get(position)).getMsgcode());
                mContext.startActivity(intent);
            }
        });
        viewHolder.time.setText(msg.getTime());
        viewHolder.content.setText(msg.getContent());
        viewHolder.cnum.setText(msg.getCnum());
        viewHolder.anum.setText(msg.getAnum());
        viewHolder.agree.setActivated(msg.IsAgree());
        viewHolder.agree.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int mode;
                ItemWallInfo msg=(ItemWallInfo)mData.get(position);
                if(msg.IsAgree()) {
                    ((ItemWallInfo) mData.get(position)).setAgree(false);
                    ((ItemWallInfo) mData.get(position)).setAnum(Integer.parseInt(msg.getAnum()) - 1);
                    mode=1;
                }else{
                    ((ItemWallInfo) mData.get(position)).setAgree(true);
                    ((ItemWallInfo) mData.get(position)).setAnum(Integer.parseInt(msg.getAnum()) + 1);
                    mode=0;
                }
                Intent intent=new Intent(mContext, MyService.class);
                intent.putExtra("CMD","Agree");
                intent.putExtra("msgcode",msg.getMsgcode());
                intent.putExtra("receiver",msg.getId());
                intent.putExtra("mode",mode);
                intent.putExtra("type",0);
                mContext.startService(intent);
                notifyDataSetChanged();
            }
        });
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
        viewHolder.edtRight.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                Log.e("e", s.toString());
                mContext.getSharedPreferences("action", Context.MODE_PRIVATE).edit().putString("nick", s.toString()).apply();
            }
        });
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
        viewHolder.edtRight.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                mContext.getSharedPreferences("action",Context.MODE_PRIVATE).edit().putString("mail",s.toString()).apply();

            }
        });
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
            }
        }
    };
}

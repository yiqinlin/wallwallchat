package com.stark.wallwallchat.UIactivity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.stark.wallwallchat.NetWork.MD5;
import com.stark.wallwallchat.NetWork.NetBuilder;
import com.stark.wallwallchat.NetWork.NetSocket;
import com.stark.wallwallchat.R;
import com.stark.wallwallchat.Util.Error;
import com.stark.wallwallchat.Util.Status;
import com.stark.wallwallchat.toast.ToastDialog;

public class Register extends Activity implements View.OnClickListener {
    private String Nick="";
    private String ID=null;
    private String PassWord="";
    private String RPassWord="";
    private String College="";
    private String Edu="";
    private String Number="";
    private ToastDialog mToastDialog=null;
    private SharedPreferences sp=null;
    private SharedPreferences.Editor editor = null;
    private TextView txvCollege = null;
    private boolean ReID=false;

    private static int i=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sp=this.getSharedPreferences("action",MODE_PRIVATE);
        if(sp.getBoolean("register",false)) {
            setContentView(R.layout.register_success);
            ReID=true;
            ID=sp.getString("registerid",null);
            TextView title=(TextView)findViewById(R.id.textview_register_title);
            TextView registerid=(TextView)findViewById(R.id.textview_register_id);
            TextView hint=(TextView)findViewById(R.id.textview_register_hint);
            Button button=(Button)findViewById(R.id.button_register_success);
            title.setText("已完成注册");
            registerid.setText(ID);
            hint.setText("每个手机号只能注册一个墙墙说账号，以上是该手机号注册的墙墙说账号，请勿重复注册~");
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent();
                    intent.putExtra("id", ID);
                    setResult(1, intent);
                    finish();
                }
            });
        }else{
            setContentView(R.layout.activity_register);
            ReID=false;
            final EditText[] Edit = new EditText[3];
            Edit[0] = (EditText) findViewById(R.id.edit_regi_nick);
            Edit[1] = (EditText) findViewById(R.id.edit_regi_pass);
            Edit[2] = (EditText) findViewById(R.id.edit_right_pass);
            final Button register = (Button) findViewById(R.id.button_register);

            txvCollege = (TextView) findViewById(R.id.txvCollege);
            final Button btnCollege = (Button) findViewById(R.id.btnCollege);

            btnCollege.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent it = new Intent(Register.this, SortSchool.class);
                    startActivityForResult(it,321);
                }
            });

            register.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Nick = Edit[0].getText().toString();
                    PassWord = Edit[1].getText().toString();
                    RPassWord = Edit[2].getText().toString();
                    String Error = null;
                    if (Nick.isEmpty()) {//wtf
                        Error = com.stark.wallwallchat.Util.Error.error(105);
                    } else if (PassWord.isEmpty()) {
                        Error = com.stark.wallwallchat.Util.Error.error(102);
                    } else if (RPassWord.isEmpty()) {
                        Error = com.stark.wallwallchat.Util.Error.error(106);
                    } else if (PassWord.length() < 6) {
                        Error = com.stark.wallwallchat.Util.Error.error(107);
                    } else if (!PassWord.equals(RPassWord)) {
                        Error = com.stark.wallwallchat.Util.Error.error(108);
                        Edit[1].setText(null);
                        Edit[2].setText(null);
                    } else if (txvCollege.getText().toString().equals("") || txvCollege.getText().toString().length() == 0) {
                        Error = com.stark.wallwallchat.Util.Error.error(111);
                    } else {
                        PassWord = MD5.get(PassWord);
                        MyAsyncTask asyncTask = new MyAsyncTask();/**异步线程*/
                        asyncTask.execute();/**开启异步线程*/
                    }
                    if (Error != null) {
                        Toast toast = Toast.makeText(Register.this, Error, Toast.LENGTH_SHORT);
                        toast.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.TOP, 0, 20);
                        toast.show();
                    }
                }
            });
            register.setClickable(false);
            for (i = 0; i < 3; i++) {
                Edit[i].addTextChangedListener(new TextWatcher() {
                    final int x = i;

                    @Override
                    public void onTextChanged(CharSequence text, int start, int before, int count) {
                        //text  输入框中改变后的字符串信息 start 输入框中改变后的字符串的起始位置 before 输入框中改变前的字符串的位置 默认为0 count 输入框中改变后的一共输入字符串的数量
                    }

                    @Override
                    public void beforeTextChanged(CharSequence text, int start, int count, int after) {
                        //text  输入框中改变前的字符串信息 start 输入框中改变前的字符串的起始位置 count 输入框中改变前后的字符串改变数量一般为0 after 输入框中改变后的字符串与起始位置的偏移量
                    }

                    @Override
                    public void afterTextChanged(Editable edit) {
                        //edit  输入结束呈现在输入框中的信息
                        switch (x) {
                            case 0:
                                Nick = edit.toString();
                                break;
                            case 1:
                                PassWord = edit.toString();
                                break;
                            case 2:
                                RPassWord = edit.toString();
                                break;
                            default:
                                break;
                        }
                        if (Nick.isEmpty() && PassWord.isEmpty() && RPassWord.isEmpty()) {
                            register.setClickable(false);
                            register.setBackgroundResource(R.drawable.gray_button);
                        } else {
                            register.setClickable(true);
                            register.setBackgroundResource(R.drawable.big_button);
                        }
                    }
                });
            }
        }
        Status.setTranslucentStatus(getWindow(), this, (LinearLayout) findViewById(R.id.transfer_title_status));
        ImageButton left=(ImageButton)findViewById(R.id.button_transfer_title_left);
        TextView mid=(TextView)findViewById(R.id.text_transfer_title);
        left.setBackgroundResource(R.drawable.title_back);
        mid.setText("注 册");
        left.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(ReID) {
                        Intent intent = new Intent();
                        intent.putExtra("id", ID);
                        setResult(1, intent);
                    }
                    finish();
                }
            }
        );
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 321) {
            if (resultCode == 666) {

                College = data.getStringExtra("college");
                Edu = data.getStringExtra("edu");
                txvCollege.setText(College);
            }
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(event.getKeyCode()==KeyEvent.KEYCODE_BACK) {
            if(ReID) {
                Intent intent = new Intent();
                intent.putExtra("id", ID);
                setResult(1, intent);
            }
            finish();
        }
        return false;
    }
    class MyAsyncTask extends AsyncTask<Void, Integer, Void>{
        @Override
        protected void onPreExecute() {/** 线程开启之前在UI线程运行he **/
            super.onPreExecute();
            if (mToastDialog == null)
                mToastDialog = new ToastDialog(Register.this);
            mToastDialog.setText("注册中...").show();
        }
        @Override
        protected Void doInBackground(Void...values){ /**后台执行，不影响UI线程**/
            NetBuilder N=new NetBuilder();
            N.put("nick",Nick)
                    .put("password",PassWord)
                    .put("college",College)
                    .put("edu",Edu)
                    .put("pnumber", sp.getString("PNumber", null));
            try {
                String temp = N.build();
                String result;
                try{
                    result=NetSocket.request("http://kwall.cn/register.php",temp);
                }catch (Exception e){
                    publishProgress(-1);
                    return null;
                }
                Log.e("request", temp);
                Log.e("result",result);
                NetBuilder out = new NetBuilder(result);
                if(out.getBool("flag", false)) {
                    ID = out.get("id");
                    ReID = true;
                    publishProgress(0);/**告诉UI线程 更新*/
                }
                else {//如果注册不成功
                    publishProgress(out.getInt("error", -2));/**告诉UI线程 更新*/
                }
            }catch (Exception e){
                publishProgress(7);
                Log.e("NetWork",e.toString());
            }
            return null;
        }
        @Override
        protected void onProgressUpdate(Integer... values) {/**后台更新界面**/
            super.onProgressUpdate(values);
            mToastDialog.cancel();
            if(values[0]==0)
            {
                setContentView(R.layout.register_success);
                com.stark.wallwallchat.Util.Status.setTranslucentStatus(getWindow(), Register.this, (LinearLayout) findViewById(R.id.transfer_title_status));
                ImageButton left=(ImageButton)findViewById(R.id.button_transfer_title_left);
                TextView mid=(TextView)findViewById(R.id.text_transfer_title);
                left.setBackgroundResource(R.drawable.title_back);
                mid.setText("注 册");
                left.setOnClickListener(Register.this);
                TextView setid=(TextView)findViewById(R.id.textview_register_id);
                setid.setText(ID);
                Button button=(Button)findViewById(R.id.button_register_success);
                button.setOnClickListener(Register.this);
                return;
            }
            Toast.makeText(Register.this, Error.error(values[0]), Toast.LENGTH_SHORT).show();
        }
    }
    @Override
    public void onClick(View v){
        switch (v.getId()){
            case R.id.button_register_success:
            case R.id.button_transfer_title_left:
                Intent intent = new Intent();
                intent.putExtra("id", ID);
                setResult(1, intent);
                editor=sp.edit();
                editor.putBoolean("register", true);
                editor.putString("registerid", ID);
                editor.apply();
                finish();
                break;
        }
    }
}

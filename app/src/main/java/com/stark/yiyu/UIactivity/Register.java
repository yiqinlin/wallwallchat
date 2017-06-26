package com.stark.yiyu.UIactivity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.stark.yiyu.Format.Ack;
import com.stark.yiyu.NetWork.MD5;
import com.stark.yiyu.NetWork.NetPackage;
import com.stark.yiyu.NetWork.NetSocket;
import com.stark.yiyu.R;
import com.stark.yiyu.Util.Error;
import com.stark.yiyu.Util.Status;
import com.stark.yiyu.toast.ToastDialog;

public class Register extends Activity {
    private String Nick="";
    private String ID=null;
    private String PassWord="";
    private String RPassWord="";
    private ToastDialog mToastDialog=null;
    private SharedPreferences sp=null;
    private SharedPreferences.Editor editor=null;
    private static int i=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Status.setTranslucentStatus(getWindow());
        sp=this.getSharedPreferences("action",MODE_PRIVATE);
        if(sp.getBoolean("register",false)) {
            setContentView(R.layout.register_success);
            TextView title=(TextView)findViewById(R.id.textview_register_title);
            TextView registerid=(TextView)findViewById(R.id.textview_register_id);
            TextView hint=(TextView)findViewById(R.id.textview_register_hint);
            Button button=(Button)findViewById(R.id.button_register_success);
            title.setText("已完成注册");
            registerid.setText(sp.getString("registerid",null));
            hint.setText("以上是你注册过的壹语账号，请勿重复注册，若忘记密码，暂时可联系壹语账号10001，将为你在后台重置密码。");
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });
        }else{
            setContentView(R.layout.activity_register);
            final EditText[] Edit = new EditText[3];
            Edit[0] = (EditText) findViewById(R.id.edit_regi_nick);
            Edit[1] = (EditText) findViewById(R.id.edit_regi_pass);
            Edit[2] = (EditText) findViewById(R.id.edit_right_pass);
            final Button register = (Button) findViewById(R.id.button_register);
            register.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Nick = Edit[0].getText().toString();
                    PassWord = Edit[1].getText().toString();
                    RPassWord = Edit[2].getText().toString();
                    String Error = null;
                    if (Nick.isEmpty()) {//wtf
                        Error = com.stark.yiyu.Util.Error.error(105);
                    } else if (PassWord.isEmpty()) {
                        Error = com.stark.yiyu.Util.Error.error(102);
                    } else if (RPassWord.isEmpty()) {
                        Error = com.stark.yiyu.Util.Error.error(106);
                    } else if (PassWord.length() < 6) {
                        Error = com.stark.yiyu.Util.Error.error(107);
                    } else if (!PassWord.equals(RPassWord)) {
                        Error = com.stark.yiyu.Util.Error.error(108);
                        Edit[1].setText(null);
                        Edit[2].setText(null);
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
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(event.getKeyCode()==KeyEvent.KEYCODE_BACK) {
            Intent intent=new Intent();
            intent.putExtra("id",ID);
            setResult(1, intent);
            finish();
        }
        return false;
    }
    class MyAsyncTask extends AsyncTask<Void, Integer, Void> {
        @Override
        protected void onPreExecute() {/** 线程开启之前在UI线程运行he **/
            super.onPreExecute();
            if (mToastDialog == null)
                mToastDialog = new ToastDialog(Register.this);//旋转图标（缓冲）
            mToastDialog.setText("注册中...").show();
        }
        @Override
        protected Void doInBackground(Void...values) { /**后台执行，不影响UI线程**/
            String JsonMsg=NetSocket.request(NetPackage.Register(Nick, PassWord));
            if(JsonMsg!=null){
                Ack result=(Ack)NetPackage.getBag(JsonMsg);/**解析服务器所传来的数据报*/
                if(!result.Flag)//如果注册不成功
                    publishProgress(result.Error);/**告诉UI线程 更新*/
                else {
                    ID=result.BackMsg;
                    publishProgress(10);/**告诉UI线程 更新*/
                }
            }else {
                publishProgress(-1);/**告诉UI线程 更新*/
            }
            return null;
        }
        @Override
        protected void onProgressUpdate(Integer... values) {/**后台更新界面**/
            super.onProgressUpdate(values);
            mToastDialog.cancel();
            if(values[0]==10)
            {
                setContentView(R.layout.register_success);
                TextView setid=(TextView)findViewById(R.id.textview_register_id);
                setid.setText(ID);
                Button button=(Button)findViewById(R.id.button_register_success);
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent();
                        intent.putExtra("id", ID);
                        setResult(1, intent);
                        editor=sp.edit();
                        editor.putBoolean("register", true);
                        editor.putString("registerid",ID);
                        editor.apply();
                        finish();
                    }
                });
                return;
            }
            Toast.makeText(Register.this, Error.error(values[0]), Toast.LENGTH_SHORT).show();
        }
    }
}

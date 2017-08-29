package com.stark.wallwallchat.UIactivity;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.stark.wallwallchat.File.ImgStorage;
import com.stark.wallwallchat.MyService;
import com.stark.wallwallchat.NetWork.MD5;
import com.stark.wallwallchat.R;
import com.stark.wallwallchat.Util.Error;
import com.stark.wallwallchat.Util.Status;
import com.stark.wallwallchat.Util.Try;
import com.stark.wallwallchat.toast.ToastDialog;

/**
 * Created by Stark on 2017/2/9.
 */
public class Login extends Activity {
    public String ID="";
    public String PassWord="";
    private ToastDialog mToastDialog=null;
    private SharedPreferences sp=null;
    private EditText IdEdit=null;
    private EditText PsEdit=null;
    private BroadcastReceiver mReceiver=null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Status.setTranslucentStatus(getWindow());
        setContentView(R.layout.activity_login);
        IdEdit=(EditText)findViewById(R.id.edit_id);
        PsEdit=(EditText)findViewById(R.id.edit_password);
        final Button signin=(Button)findViewById(R.id.button_sign_in);
        final ImageButton clear_id=(ImageButton)findViewById(R.id.button_clear_login_id);
        final ImageButton clear_ps=(ImageButton)findViewById(R.id.button_clear_login_ps);
        Button register=(Button)findViewById(R.id.button_to_register);
        Button forget=(Button)findViewById(R.id.button_to_forget);
        sp= this.getSharedPreferences("action", MODE_PRIVATE);
        ((ImageView)findViewById(R.id.Login_head)).setImageDrawable(ImgStorage.getHead(Login.this));
        ID=sp.getString("id", null);//用户帐号
        PassWord=sp.getString("password",null);
        if(ID!=null) {
            IdEdit.setText(ID);
            IdEdit.setSelection(ID.length());
            clear_id.setActivated(true);
        }else{
            clear_id.setClickable(false);
            clear_id.setActivated(false);
        }
        if(PassWord!=null) {
            PsEdit.setText(PassWord);
            PsEdit.setSelection(PassWord.length());
            clear_ps.setActivated(true);
        }else{
            clear_ps.setClickable(false);
            clear_ps.setActivated(false);
        }
        clear_id.setOnClickListener(Click);
        clear_ps.setOnClickListener(Click);
        register.setOnClickListener(Click);
        forget.setOnClickListener(Click);
        signin.setOnClickListener(Click);
        IdEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!s.toString().equals("")) {
                    clear_id.setClickable(true);
                    clear_id.setActivated(true);
                } else {
                    clear_id.setClickable(false);
                    clear_id.setActivated(false);
                }
            }
        });
        PsEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
            @Override
            public void afterTextChanged(Editable s) {
                if(!s.toString().equals("")){
                    clear_ps.setClickable(true);
                    clear_ps.setActivated(true);
                }else{
                    clear_ps.setClickable(false);
                    clear_ps.setActivated(false);
                }
                PassWord = s.toString();
                sp.edit().putString("password", null).apply();
            }
        });
        mReceiver=new BroadcastReceiver(){
            public void onReceive(Context context, Intent intent) {
                int temp=intent.getIntExtra("key",-1);
                if (temp == 10) {
                    sp.edit().putBoolean("state", true).putString("id", ID).putString("password", PassWord).apply();
                    Intent ToTransfer=new Intent(Login.this,TransferActivity.class);
                    ToTransfer.putExtra("CMD", "MAuto");
                    startActivity(ToTransfer);
                    overridePendingTransition(R.anim.anim_null, R.anim.anim_null);//Activity切换效果
                    finish();
                    return;
                }
                mToastDialog.cancel();
                Toast.makeText(Login.this, Error.error(temp), Toast.LENGTH_SHORT).show();
            }
        };
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("com.stark.wallwallchat.login");
        registerReceiver(mReceiver, intentFilter);
    }
    View.OnClickListener Click=new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.button_clear_login_id:
                    IdEdit.setText("");
                    PsEdit.setText("");
                    sp.edit().putString("id", null).putString("password", null).apply();
                    break;
                case R.id.button_clear_login_ps:
                    PsEdit.setText("");
                    sp.edit().putString("password", null).apply();
                    break;
                case R.id.button_to_register:
                    Intent intent=new Intent(Login.this,SMSActivity.class);
                    startActivityForResult(intent, 0);
                    break;
                case R.id.button_to_forget:
                    Intent intent2=new Intent(Login.this,SMSActivity.class);
                    startActivityForResult(intent2,1);
                    break;
                case R.id.button_sign_in:
                    ID = IdEdit.getText().toString();
                    PassWord = PsEdit.getText().toString();
                    String Error=null;
                    if (ID.isEmpty()) {
                        Error= com.stark.wallwallchat.Util.Error.error(101);
                    } else if (PassWord.isEmpty()) {
                        Error= com.stark.wallwallchat.Util.Error.error(102);
                    } else if (Long.parseLong(ID) < 10001) {
                        Error= com.stark.wallwallchat.Util.Error.error(103);
                    } else if (PassWord.length() < 6) {
                        Error= com.stark.wallwallchat.Util.Error.error(104);
                    } else {
                        if (sp.getString("password",null)==null) {
                            PassWord = MD5.get(PassWord);
                        }
                        if (mToastDialog == null)
                            mToastDialog = new ToastDialog(Login.this);
                        mToastDialog.setOnTouchClose(false);
                        mToastDialog.setText("登录中...").show();
                        Intent intent1 = new Intent(Login.this, MyService.class);
                        intent1.putExtra("CMD", "Manual");
                        intent1.putExtra("id", ID);
                        intent1.putExtra("password", PassWord);
                        startService(intent1);
                    }
                    if (Error != null) {
                        Toast.makeText(Login.this, Error, Toast.LENGTH_SHORT).show();
                    }

                    break;
            }
        }
    };
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        // TODO Auto-generated method stub
        super.onActivityResult(requestCode, resultCode, intent);
        Log.i("code",requestCode+" "+resultCode);
        if (requestCode==0&&resultCode == 1) {
            String temp= Try.getStringExtra(intent, "id");
            if(temp!=null&&!temp.equals("")){
                IdEdit.setText(temp);
                IdEdit.setSelection(temp.length());
                PsEdit.setText("");
            }
        }
    }
    @Override
    protected void onDestroy(){
        super.onDestroy();
        unregisterReceiver(mReceiver);
    }
}

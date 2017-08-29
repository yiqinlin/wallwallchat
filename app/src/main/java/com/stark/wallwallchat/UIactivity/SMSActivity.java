package com.stark.wallwallchat.UIactivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.stark.wallwallchat.R;
import com.stark.wallwallchat.Util.Status;

import cn.bmob.newsmssdk.BmobSMS;
import cn.bmob.newsmssdk.exception.BmobException;
import cn.bmob.newsmssdk.listener.RequestSMSCodeListener;
import cn.bmob.newsmssdk.listener.VerifySMSCodeListener;

public class SMSActivity extends Activity implements View.OnClickListener {
    private EditText EPN, EPin;
    private Button Switch;
    private boolean isPin =false;
    private boolean isNumber=false;
    private String PNCache =null;
    private String PinCache =null;
    private long time=0;
    private String Pin;
    private String PN;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sms);
        Status.setTranslucentStatus(getWindow(), this, (LinearLayout) findViewById(R.id.transfer_title_status));
        ImageButton left=(ImageButton)findViewById(R.id.button_transfer_title_left);
        TextView mid=(TextView)findViewById(R.id.text_transfer_title);
        left.setBackgroundResource(R.drawable.title_back);
        mid.setText("手机验证");
        left.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        BmobSMS.initialize(SMSActivity.this, "13d43843a7ed60f21043e2993bcad831");
        EPN = (EditText) findViewById(R.id.userName_et);
        EPin = (EditText) findViewById(R.id.passWord_et);
        final ImageButton CPN=(ImageButton)findViewById(R.id.button_clear_pn);
        final ImageButton CPin=(ImageButton)findViewById(R.id.button_clear_pin);
        Switch = (Button) findViewById(R.id.button_Switch);
        Switch.setOnClickListener(this);
        ButtonChange();
        EPN.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if(s.toString().equals("")){
                    CPN.setActivated(false);
                    CPN.setClickable(false);
                }else{
                    CPN.setActivated(true);
                    CPN.setClickable(true);
                }
                if (s.length() == 11) {
                    isNumber = true;
                    ButtonChange();
                } else if (s.length() != 11) {
                    isNumber = false;
                    ButtonChange();
                }
            }
        });
        EPin.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if(s.toString().equals("")){
                    CPin.setActivated(false);
                    CPin.setClickable(false);
                }else{
                    CPin.setActivated(true);
                    CPin.setClickable(true);
                }
                if (s.length() == 6) {
                    isPin = true;
                    ButtonChange();
                } else {
                    isPin = false;
                    ButtonChange();
                }
            }
        });
    }
    @Override
    public void onClick(View v){
        PN = EPN.getText().toString();
        Pin = EPin.getText().toString();
        switch (v.getId()) {
            case R.id.button_Switch:
                if(!isPin&&isNumber) {
                    BmobSMS.requestSMSCode(this, PN, "register", new RequestSMSCodeListener() {
                        @Override
                        public void done(Integer integer, BmobException e) {
                            if (e == null) {
                                ButtonChange();
                                Toast.makeText(SMSActivity.this, "验证码发送中，请注意查收", Toast.LENGTH_SHORT).show();
                                new CountDownTimer(60000, 1000) {
                                    @Override
                                    public void onTick(long millisUntilFinished) {
                                        time=millisUntilFinished;
                                        ButtonChange();
                                    }
                                    @Override
                                    public void onFinish() {
                                        time=0;
                                        ButtonChange();
                                    }
                                }.start();
                            } else {
                                Toast.makeText(SMSActivity.this, "验证码发送失败，请检查网络连接", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }else if(isPin&&isNumber) {
                    if (PN.equals(PNCache)&& Pin.equals(PinCache)) {
                        Intent intent = new Intent(SMSActivity.this, Register.class);
                        startActivityForResult(intent, 0);
                    } else {
                        BmobSMS.verifySmsCode(this, PN, Pin, new VerifySMSCodeListener() {
                            @Override
                            public void done(BmobException e) {
                                if (e == null) {
                                    PNCache = PN;
                                    PinCache = Pin;
                                    getSharedPreferences("action",MODE_PRIVATE).edit().putString("PNumber",PN).apply();
                                    Intent intent = new Intent(SMSActivity.this, Register.class);
                                    startActivityForResult(intent, 0);
                                } else {
                                    EPin.setText("");
                                    Toast.makeText(SMSActivity.this, "验证码错误", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                }
                break;
        }
    }
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        // TODO Auto-generated method stub
        super.onActivityResult(requestCode, resultCode, intent);
        Log.i("code", requestCode + " " + resultCode);
        if (requestCode==0&&resultCode == 1) {
            setResult(1, intent);
            finish();
        }
    }
    private void ButtonChange(){
        if(isPin &&isNumber){
            Switch.setActivated(true);
            Switch.setClickable(true);
            Switch.setText("下一步");
        }else if(!isPin&&isNumber){
            if(time!=0){
                Switch.setActivated(false);
                Switch.setClickable(false);
                Switch.setText(time/1000+"s后重新获取");
            }else{
                Switch.setActivated(true);
                Switch.setClickable(true);
                Switch.setText("获取验证码");
            }
        }else{
            Switch.setActivated(false);
            Switch.setClickable(false);
            Switch.setText("请输入手机号");
        }
    }
}

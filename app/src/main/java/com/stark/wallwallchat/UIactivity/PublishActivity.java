package com.stark.wallwallchat.UIactivity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;

import com.stark.wallwallchat.MyService;
import com.stark.wallwallchat.R;
import com.stark.wallwallchat.Util.Status;

public class PublishActivity extends Activity {
    EditText editText;
    Switch anonymous;
    SharedPreferences sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_publish);
        Status.setTranslucentStatus(getWindow(), this, (LinearLayout) findViewById(R.id.transfer_title_status));
        ImageButton left=(ImageButton)findViewById(R.id.button_transfer_title_left);
        TextView title=(TextView)findViewById(R.id.text_transfer_title);
        Button right=(Button)findViewById(R.id.button_transfer_title_right);
        editText=(EditText)findViewById(R.id.publish_editText);
        anonymous=(Switch)findViewById(R.id.publish_switch);
        sp=getSharedPreferences("action",MODE_PRIVATE);
        left.setBackgroundResource(R.drawable.title_back);
        left.setOnClickListener(Click);
        title.setText("发 表");
        right.setText("完成");
        right.setOnClickListener(Click);
    }
    View.OnClickListener Click=new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.button_transfer_title_left:
                    finish();
                    break;
                case R.id.button_transfer_title_right:
                    sp.edit().putString("edu","nsu").apply();
                    Intent intent=new Intent(PublishActivity.this, MyService.class);
                    intent.putExtra("CMD","WallMsg");
                    intent.putExtra("msg",editText.getText().toString());
                    intent.putExtra("mode",anonymous.isChecked()?1:0);
                    intent.putExtra("type",0);
                    startService(intent);
                    finish();
                    break;
            }
        }
    };
}

package com.stark.wallwallchat.UIactivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.stark.wallwallchat.R;
import com.stark.wallwallchat.Util.Status;

public class SelfInfoActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_self_info);

        Status.setTranslucentStatus(getWindow());
        Status.setTranslucentStatus(getWindow(), this, (LinearLayout) findViewById(R.id.transfer_title_status));

        ImageButton btnLeft = (ImageButton) findViewById(R.id.button_transfer_title_left);
        TextView title = (TextView) findViewById(R.id.text_transfer_title);
        Button btnRight = (Button) findViewById(R.id.button_transfer_title_right);
        final EditText edtInfo = (EditText) findViewById(R.id.edtInfo);

        title.setText("个人说明");
        btnLeft.setBackgroundResource(R.drawable.title_back);
        btnRight.setText("保存");


        Intent it = getIntent();
        String selfInfo = it.getStringExtra("selfInfo");
        if (selfInfo.equals("") || selfInfo == null) {
            selfInfo = "  ";
        }
        edtInfo.setText(selfInfo);
        edtInfo.setSelection(selfInfo.length());

        btnLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        btnRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String selfInfo = edtInfo.getText().toString();
                Intent it = new Intent();
                it.putExtra("selfInfo", selfInfo);
                setResult(5, it);
                finish();
            }
        });
    }
}

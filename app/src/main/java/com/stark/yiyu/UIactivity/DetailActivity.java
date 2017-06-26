package com.stark.yiyu.UIactivity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.stark.yiyu.R;
import com.stark.yiyu.Util.Status;

public class DetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Status.setTranslucentStatus(getWindow());
        setContentView(R.layout.activity_detail);
        Intent intent=getIntent();
        Button MsgButton=(Button)findViewById(R.id.button_detail_msg);
        TextView Msg=(TextView)findViewById(R.id.text_detail_msg);
        Msg.setText(intent.getStringExtra("msg"));
        MsgButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}

package com.stark.yiyu.UIactivity;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.stark.yiyu.R;
import com.stark.yiyu.Util.Status;

public class AutoActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auto);
        Status.setTranslucentStatus(getWindow());
        Status.setTranslucentStatus(getWindow(), this, (LinearLayout) findViewById(R.id.transfer_title_status));

        Button right = (Button) findViewById(R.id.button_transfer_title_right);
        ImageButton left = (ImageButton) findViewById(R.id.button_transfer_title_left);
        TextView title = (TextView) findViewById(R.id.text_transfer_title);
        final EditText edtAuto = (EditText) findViewById(R.id.edtAuto);

        left.setBackgroundResource(R.drawable.title_back);
        right.setText("保存");

        left.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        title.setText("个性签名");

        right.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String auto = edtAuto.getText().toString();
                Intent it = new Intent();
                it.putExtra("auto", auto);
                setResult(123, it);
                finish();
            }
        });
    }
}

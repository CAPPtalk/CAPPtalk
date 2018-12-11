package com.example.jangheejun.capptalk;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

public class Reported extends Activity {
    public void onCreate(Bundle saveInstanceState) {
        super.onCreate(saveInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.reported_text);
        Intent intent = new Intent(this.getIntent());
        String text = intent.getStringExtra("text");
        TextView tv1 = (TextView) findViewById(R.id.textView1);
        TextView tv2 = (TextView) findViewById(R.id.textView2);
        Button btn = (Button) findViewById(R.id.button);
        tv1.setText(text);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}


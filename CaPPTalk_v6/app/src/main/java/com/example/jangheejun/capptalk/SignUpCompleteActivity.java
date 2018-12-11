package com.example.jangheejun.capptalk;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

public class SignUpCompleteActivity extends Activity {

    SignUpActivity Previous;

    public void onCreate(Bundle saveInstanceState) {
        super.onCreate(saveInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_signupcomplete);

        Intent intent = getIntent();
        final String host = intent.getStringExtra("host");

        Previous = (SignUpActivity)SignUpActivity.SignUp;

        Button btn = (Button) findViewById(R.id.button);
        Button btn2 = (Button) findViewById(R.id.button2);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(host.equals("SKKU"))
                {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://mail.skku.edu/"));
                    startActivity(intent);
                }

                else if(host. equals("GOOGLE"))
                {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://gmail.com/"));
                    startActivity(intent);
                }

            }
        });

        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SignUpCompleteActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
                Previous.finish();
            }
        });
    }

    public void onBackPressed() {
        finish();
        Previous.finish();
    }
}
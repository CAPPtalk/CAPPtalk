package com.example.jangheejun.capptalk;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class AddSubjectActivity extends Activity {

    private TextView classInfo;

    private String uid;
    private String info;
    private String rid;

    public void onCreate(Bundle saveInstanceState) {
        super.onCreate(saveInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_addsubject);

        Intent intent = getIntent();
        uid = intent.getStringExtra("myUid");
        info = intent.getStringExtra("info");
        rid = getRoomId(info);

        classInfo = findViewById(R.id.tv_info);
        classInfo.setText(info);

        Button btn_yes = (Button) findViewById(R.id.btn_yes);
        Button btn_no = (Button) findViewById(R.id.btn_no);

        btn_yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
                mDatabase.child("rooms").child(rid).child("users").child(uid).setValue(true);
                mDatabase.child("Users").child(uid).child("UserRooms").child(rid).setValue(true);
                finish();
            }
        });

        btn_no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    public String getRoomId(String info){

        String result = info.substring(info.length()-10, info.length());

        return result;

    }

}

package com.example.jangheejun.capptalk;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;

import com.example.jangheejun.capptalk.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Modification extends Activity {
    EditText edt;
    String qid,aid;
    public void onCreate(Bundle saveInstanceState) {
        super.onCreate(saveInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.modification);

        final Intent intent = getIntent();
        qid = intent.getExtras().getString("qid");
        aid = intent.getExtras().getString("aid");

        Log.d("M qid", qid);
        Log.d("M aid", aid);

        edt = (EditText)findViewById(R.id.editText);
        Button btn = (Button) findViewById(R.id.button);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
                mDatabase.child("question").child(qid).child("answer").child(aid).child("content").setValue(edt.getText().toString());
                //Intent temp = new Intent(getApplicationContext());
                Intent temp = new Intent();
                temp.putExtra("aid",aid);
                temp.putExtra("content", edt.getText().toString());
                setResult(Activity.RESULT_OK, temp);
                finish();
//                mDatabase.addValueEventListener(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(DataSnapshot dataSnapshot) {
//                    }
//                    @Override
//                    public void onCancelled(DatabaseError databaseError) {
//                    }
//                });
            }
        });
    }

}

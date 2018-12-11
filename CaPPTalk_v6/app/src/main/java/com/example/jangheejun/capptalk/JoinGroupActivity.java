package com.example.jangheejun.capptalk;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.security.acl.Group;
import java.util.ArrayList;
import java.util.List;

public class JoinGroupActivity extends Activity {

    public String uid;
    public String roomID;
    public String groupID;
    Button button_yes;
    Button button_no;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_join_group);

        uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        groupID = getIntent().getStringExtra("groupID");
        roomID = getIntent().getStringExtra("roomID");
        final List<String> users = new ArrayList<>();

        button_yes = (Button) findViewById(R.id.joinGroupActivity_button_yes);
        button_no = (Button) findViewById(R.id.joinGroupActivity_button_no);

        button_no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        button_yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                FirebaseDatabase.getInstance().getReference().child("groups").child(groupID).child("users").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        users.clear();
                        for(DataSnapshot item: dataSnapshot.getChildren()) {
                            users.add(item.getKey());
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

                FirebaseDatabase.getInstance().getReference().child("Users").child(uid).child("GroupRooms").child(groupID).setValue(true);
                FirebaseDatabase.getInstance().getReference().child("groups").child(groupID).child("users").child(uid).setValue(true);
                FirebaseDatabase.getInstance().getReference().child("rooms").child(roomID).child("groups").child(groupID).child("users").child(uid).setValue(true);

                Intent intent = new Intent(JoinGroupActivity.this,sample_activity_1.class);
                FirebaseDatabase.getInstance().getReference().child("Users").child(uid).child("gotolist").setValue(1);
                startActivity(intent);

                finish();

            }
        });
    }
}

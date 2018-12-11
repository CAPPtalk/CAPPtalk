package com.example.jangheejun.capptalk;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.jangheejun.capptalk.models.ChatModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.regex.Pattern;

public class AddGroupActivity extends AppCompatActivity {

    int condition = 0;
    long usercount;
    String Group;

    ArrayList grouplist = new ArrayList();

    private EditText groupName;
    private EditText maxCount;

    Button btn;

    String RoomID;
    String UID = FirebaseAuth.getInstance().getCurrentUser().getUid();
    String nickname;

    DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();

    private static final Pattern GROUPNAME_PATTERN = Pattern.compile("^[ㄱ-ㅎ가-힣a-zA-Z0-9!@.#$%^*?_~ ]{1,20}$");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_group);

        RoomID = getIntent().getStringExtra("RoomID");

        btn = (Button)findViewById(R.id.btn_make);
        groupName = findViewById(R.id.g_name);
        maxCount = findViewById(R.id.max_count);

        mDatabase.child("Users").child(UID).child("userName").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                nickname = dataSnapshot.getValue().toString();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        mDatabase.child("rooms").child(RoomID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    String num = snapshot.getKey();
                    switch(num){
                        case "users":
                            usercount = snapshot.getChildrenCount();
                            break;
                        default:
                            break;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        mDatabase.child("rooms").child(RoomID).child("groups").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Group = snapshot.getKey();
                    grouplist.add(Group);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isValidName(groupName.getText().toString()) && isValidCount(Long.parseLong(maxCount.getText().toString())) && isExistName(RoomID + "_" + groupName.getText().toString())){
                    CreateGroup(groupName.getText().toString(),Long.parseLong(maxCount.getText().toString()));
                    Intent intent = new Intent(AddGroupActivity.this,sample_activity_1.class);
                    mDatabase.child("Users").child(UID).child("gotolist").setValue(1);
                    startActivity(intent);
                }
            }
        });


    }

    private boolean isValidName(String groupname){

        if (groupname.isEmpty() || !GROUPNAME_PATTERN.matcher(groupname).matches() ){
            Toast.makeText(AddGroupActivity.this, "잘못된 그룹명 형식입니다.", Toast.LENGTH_SHORT).show();
            return false;
        }
        else return true;
    }

    private boolean isValidCount(long count){

        if (String.valueOf(count).isEmpty() || (count<2) || (count>usercount) ){
            Toast.makeText(AddGroupActivity.this, "잘못된 인원수 입니다.", Toast.LENGTH_SHORT).show();
            return false;
        }
        else return true;
    }

    private boolean isExistName(final String groupname){

        if(grouplist.contains(groupname)){
            Toast.makeText(AddGroupActivity.this, "이미 존재하는 이름입니다.", Toast.LENGTH_SHORT).show();
            condition = 0;
            return false;
        }
        else return true;
    }

    private void CreateGroup(final String groupname, long maxcount){
        mDatabase.child("groups").child(RoomID + "_" + groupname).child("info").child("name").setValue(groupname);
        mDatabase.child("groups").child(RoomID + "_" + groupname).child("info").child("maxcount").setValue(maxcount);
        mDatabase.child("groups").child(RoomID + "_" + groupname).child("info").child("creator").setValue(nickname);
        mDatabase.child("groups").child(RoomID + "_" + groupname).child("info").child("lastMessage").child("message").setValue("");
        mDatabase.child("groups").child(RoomID + "_" + groupname).child("info").child("lastMessage").child("timestamp").setValue(ServerValue.TIMESTAMP);
        mDatabase.child("groups").child(RoomID + "_" + groupname).child("users").child(UID).setValue(true);
        mDatabase.child("Users").child(UID).child("GroupRooms").child(RoomID + "_" + groupname).setValue(true);
        mDatabase.child("rooms").child(RoomID).child("groups").child(RoomID + "_" + groupname).child("users").child(UID).setValue(true);

        final ChatModel.Comment comment = new ChatModel.Comment();
        comment.uid = UID;
        comment.message = "'" + groupname + "' 스터디가 생성되었습니다. 원하시는 분은 사이드 탭을 누르거나 해당 메세지를 클릭하여 채팅방에 참여하세요!";
        comment.timestamp = ServerValue.TIMESTAMP;
        comment.gid = RoomID + "_" + groupname;

        if (!(comment.message.equals(""))) {
            mDatabase.child("rooms").child(RoomID).child("comments")
                    .child(mDatabase.push().getKey()).setValue(comment).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    //FirebaseDatabase.getInstance().getReference().child("rooms").child(RoomID).child("comments").child(mDatabase.push().getKey()).child("gid").setValue(RoomID + "_" + groupname);
                    FirebaseDatabase.getInstance().getReference().child("rooms").child(RoomID).child("info").child("lastMessage").setValue(comment);
                }
            });
        }
    }
}

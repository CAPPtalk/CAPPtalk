package com.example.jangheejun.capptalk;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.example.jangheejun.capptalk.models.QuestionModel;
import com.example.jangheejun.capptalk.models.ChatModel;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;

public class AddQuestionActivity extends Activity {

    private FirebaseAuth mAuth;

    private EditText editTextTitle;
    private EditText editTextContent;

    private String uid;
    private String RoomID;
    private String qid;

    private int points;

    public void onCreate(Bundle saveInstanceState){
        super.onCreate(saveInstanceState);
        setContentView(R.layout.activity_add_question);

        uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        RoomID = getIntent().getStringExtra("RoomID");

        mAuth = FirebaseAuth.getInstance();

        editTextTitle = findViewById(R.id.et_write_title);
        editTextContent = findViewById(R.id.et_write_content);

        Button bt_cancel = (Button) findViewById(R.id.bt_cancel);
        Button bt_ok = (Button) findViewById(R.id.bt_ok);

        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                points = Integer.parseInt(dataSnapshot.child("Users").child(uid).child("points").getValue().toString());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }

        });

        bt_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        bt_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(points < 3)
                    Toast.makeText(AddQuestionActivity.this, "포인트가 부족합니다.", Toast.LENGTH_SHORT).show();

                else {

                    DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();

                    QuestionModel NewQuestion = new QuestionModel(uid, editTextTitle.getText().toString(), editTextContent.getText().toString(), RoomID);
                    qid = mDatabase.child("question").push().getKey().toString();
                    mDatabase.child("question").child(qid).setValue(NewQuestion);

                    mDatabase.child("rooms").child(RoomID).child("question").child(qid).setValue(true);

                    final ChatModel.Comment comment = new ChatModel.Comment();
                    comment.uid = uid;
                    comment.message = "'" + editTextTitle.getText().toString() + "' 질문이 등록되었습니다. 답변을 달아주세요!";
                    comment.timestamp = ServerValue.TIMESTAMP;
                    comment.qid = qid;

                    if (!(comment.message.equals(""))) {
                        mDatabase.child("rooms").child(RoomID).child("comments")
                                .child(qid).setValue(comment).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                FirebaseDatabase.getInstance().getReference().child("rooms").child(RoomID).child("comments").child(qid).child("qid").setValue(qid);
                                FirebaseDatabase.getInstance().getReference().child("rooms").child(RoomID).child("info").child("lastMessage").setValue(comment);

                            }
                        });
                    }

                    mDatabase.child("Users").child(uid).child("points").setValue(points - 3);


                    Intent temp = new Intent();
                    temp.putExtra("qid", qid);
                    setResult(Activity.RESULT_OK, temp);
                    finish();
                }
            }
        });

    }

}

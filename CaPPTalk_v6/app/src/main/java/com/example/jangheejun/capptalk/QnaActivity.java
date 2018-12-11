package com.example.jangheejun.capptalk;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class QnaActivity extends AppCompatActivity {
    ListView QnaList;
    QnaAdapter Qna_adapter;
    String tempContent;
    QnaList temp = new QnaList();
    String uid;
    String qid;
    String selected;
    private Button btnClick;
    private DatabaseReference qdatabase;
    private DatabaseReference adatabase;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qna);

        uid =FirebaseAuth.getInstance().getCurrentUser().getUid();
        qid = getIntent().getStringExtra("qid");
        Qna_adapter= new QnaAdapter();
        QnaList=(ListView)findViewById(R.id.fragment_qna);
        Qna_adapter.getQid(qid);
        QnaList.setAdapter(Qna_adapter);
        qdatabase = FirebaseDatabase.getInstance().getReference().child("question").child(qid);
        adatabase=FirebaseDatabase.getInstance().getReference().child("question").child(qid).child("answer");
        //데이타베이스 참조로 Q까지 진입 후 어뎁터에 Q가 될 아이템을 리스트에 추가해준다.
        final String[] tempUid = new String[1];
        FirebaseDatabase.getInstance().getReference().addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot item : dataSnapshot.child("question").child(qid).getChildren()){
                    switch(item.getKey()){
                        case "uid":
                            tempUid[0] =item.getValue().toString();
                            temp.setUid(tempUid[0]);
                            break;
                        case "content":
                            temp.setContent(item.getValue().toString());break;
                        case "Title":
                            temp.setTitle(item.getValue().toString());break;
                        case "selected":
                            selected= item.getValue().toString();break;
                        default:
                            break;
                    }
                }
                temp.setIsComment(0);
                for(DataSnapshot item2 : dataSnapshot.child("Users").child(tempUid[0]).getChildren()){
                    String tempItem = item2.getKey();
                    Log.d("tempItem",tempItem);
                    switch(tempItem){
                        case "userName":
                            temp.setName(item2.getValue().toString());
                            break;
                        case "url":
                            temp.setUrl(item2.getValue().toString());
                            break;
                        default:
                            break;
                    }
                }
                Qna_adapter.addItem(temp);
                Qna_adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });



        //AnswerID들을 모으는 작업
        final ArrayList<String> ansList =new ArrayList<String>();
        ansList.clear();
        final boolean[] push_flag = {false};
        qdatabase.addChildEventListener(new ChildEventListener(){
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {


            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                if(Qna_adapter.getFlag(3)){
                    Log.d("33333", "3333   ");
                    Qna_adapter.list_sel(dataSnapshot.getValue().toString());
                    Qna_adapter.notifyDataSetChanged();
                    Qna_adapter.setFlag(3,false);
                }

                if(Qna_adapter.getFlag(1))
                {
                    Qna_adapter.setFlag(1,false);
                    Log.d("AAAA","1234");
                }
                else if(Qna_adapter.getFlag(2))
                {
                    Qna_adapter.setFlag(2,false);
                    Log.d("AAAA","4321");
                }
                else
                {
                    String temp_aid = dataSnapshot.getKey();
                    if(temp_aid.equals("answer"))
                    {
                        Qna_adapter.notifyDataSetChanged();
                        Log.d("AAAA", temp_aid);

                    }
                    else
                    {
//                            Log.d("BBBB", temp_aid);
//                        Qna_adapter.list_modify(temp_aid, dataSnapshot.child("content").getValue().toString());
//                        Qna_adapter.notifyDataSetChanged();
                    }
                }

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
            }
        });

        FirebaseDatabase.getInstance().getReference().addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ansList.clear();
                Qna_adapter.clearList();
                for(DataSnapshot item : dataSnapshot.child("question").child(qid).child("answer").getChildren()){
                    String tempstr = new String();
                    tempstr = item.getKey();
                    ansList.add(tempstr);
                }
                if(!selected.equals(""))
                {
                    int point = 0;
                    for(int i =0; i<ansList.size();i++)
                    {
                        if(ansList.get(i).equals(selected)){
                            point=i;
                            break;
                        }
                    }
                    ansList.add(0, ansList.get(point));
                    ansList.remove(point+1);
                }
                if(ansList.size()!=0)
                    for(final String str : ansList){
                                int a;
                                Log.d("good",Integer.toString(temp.getGood()));
                                Log.d("bad",Integer.toString(temp.getBad()));
                                for(DataSnapshot item : dataSnapshot.child("question").child(qid).child("answer").child(str).getChildren()){
                                    switch(item.getKey()) {
                                        case "uid":
                                            temp.setUid(item.getValue().toString());
                                            break;
                                        case "content":
                                            temp.setContent(item.getValue().toString());
                                            break;
                                        case "good":
                                            a = Integer.parseInt(item.child("points").getValue().toString());
                                            temp.setGood(a);
                                            break;
                                        case "bad":
                                            a = Integer.parseInt(item.child("points").getValue().toString());
                                            temp.setBad(a);
                                            break;
                                        default:
                                            break;
                                    }
                                }
                                temp.setIsComment(1);
                                temp.setAid(str);
                                Log.d("aidaid:",temp.getAid());
                                for(DataSnapshot item : dataSnapshot.child("Users").child(temp.getUid()).getChildren()){
                                    switch (item.getKey()){
                                        case "userName":
                                            temp.setName(item.getValue().toString());
                                            Log.d("name",temp.getName());
                                            break;
                                        case "url":
                                            temp.setUrl(item.getValue().toString());
                                            Log.d("url",temp.getUrl());
                                            break;
                                        default:
                                            break;
                                    }
                                }
                        Log.d("namename",temp.getName());
                        Log.d("urlurl",temp.getUrl());
                                Qna_adapter.addItem(temp);
                                Qna_adapter.notifyDataSetChanged();

                    }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        btnClick=(Button) findViewById(R.id.activity_qna_button);
        btnClick.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                final EditText content = findViewById(R.id.activity_qna_content);
                final QnaList temp = new QnaList();
                tempContent = content.getText().toString();
                final String[] quid = new String[1];
               FirebaseDatabase.getInstance().getReference().addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if(tempContent.length()<10){
                            new AlertDialog.Builder(QnaActivity.this)
                                    .setTitle("글자수 제한")
                                    .setMessage("10자 이상 작성해주십시오.\n\n")
                                    .setNeutralButton("확인", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dlg, int sumthin) {
                                        }
                                    })
                                    .show();

                        }
                        else if(uid.equals(qid)){
                            new AlertDialog.Builder(QnaActivity.this)
                                    .setTitle("주의")
                                    .setMessage("본인 글에는 답변을 달 수 없습니다.\n\n")
                                    .setNeutralButton("확인", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dlg, int sumthin) {
                                        }
                                    })
                                    .show();
                        }
                        else {
                            temp.setIsComment(1);
                            temp.setUid(uid);
                            temp.setContent(tempContent);
                            DatabaseReference tempref = adatabase.push();
                            tempref.setValue(temp);

                            temp.setAid(tempref.getKey());
                            for(DataSnapshot item : dataSnapshot.child("Users").child(uid).getChildren()){
                                switch (item.getKey()){
                                    case "userName":
                                        temp.setName(item.getValue().toString());
                                        break;
                                    case "url":
                                        temp.setUrl(item.getValue().toString());
                                        break;
                                }
                            }
                            Qna_adapter.addItem(temp);
                            Qna_adapter.setFlag(1,true);
                            tempref.child("bad").child("points").setValue(0);
                            Qna_adapter.setFlag(2,true);
                            tempref.child("good").child("points").setValue(0);
                            content.setText("");
                            Qna_adapter.notifyDataSetChanged();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

            }
        });
        //모은 AnswerID들을 이제 순차적으로 방문하여 직접 item으로 추가해주는 작업


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==3000) {
            if(resultCode==Activity.RESULT_OK){
                String aid = data.getStringExtra("aid");
                String content = data.getStringExtra("content");
                Qna_adapter.list_modify(aid, content);
                Qna_adapter.notifyDataSetChanged();
            }
        }
    }
}

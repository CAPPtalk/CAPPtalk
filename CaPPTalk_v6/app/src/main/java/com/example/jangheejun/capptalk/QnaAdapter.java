package com.example.jangheejun.capptalk;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import static android.support.v4.app.ActivityCompat.startActivityForResult;

public class QnaAdapter extends BaseAdapter {
    List<QnaList> list = new ArrayList<QnaList>();
    boolean flag1=false;
    boolean flag2=false;
    boolean flag3=false;
    String uid = null;
    String qid=null;
    String aid=null;
    public  void getQid(String qid){this.qid=qid;}
    public void clearList(){
        QnaList beforeClear = list.get(0);
        list.clear();
        list.add(beforeClear);
    }

    public void setFlag(int i,boolean flag){
        if(i==1){
            this.flag1=flag;
        }
        else if(i==2){
            this.flag2=flag;
        }
        else if(i==3){
            this.flag3=flag;
        }
    }

    public boolean getFlag(int i){
        if(i==1){
            return this.flag1;
        }
        else if(i==2){
            return this.flag2;
        }
        else if(i==3){
            return this.flag3;
        }
        else return false;
    }

    public void list_modify(String aid, String content){
        Log.d("BBBB", aid);
        Log.d("BBBB",content);
        for(int i =1; i<list.size();i++)
        {
            if(list.get(i).getAid().equals(aid)){
                list.get(i).setContent(content);
            }
        }
    }

    public void list_remove(String aid){
        int point = 0;
        for(int i =1; i<list.size();i++)
        {
            if(list.get(i).getAid().equals(aid)){
                point=i;
                break;
            }
        }
        list.remove(point);
    }

    public void list_sel(String aid){
        int point = 0;
        for(int i =1; i<list.size();i++)
        {
            if(list.get(i).getAid().equals(aid)){
                point=i;
                break;
            }
        }
        list.add(1, list.get(point));
        list.remove(point+1);
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final Context context = parent.getContext();
        int IsComment;
        final QnaList qnalist = list.get(position);
        final String[] Q_uid = new String[1];

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        uid = user.getUid();

        IsComment=qnalist.getIsComment();
        if(IsComment==0){
            LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.item_qna_q,parent,false);

        }
        else if(convertView == null || IsComment!=0){
            LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.item_qna,parent,false);
            /*
            if(!aid.equals(myUid)){
                ImageButton btn = (ImageButton)convertView.findViewById(R.id.item_qna_like);
                btn.setVisibility(View.VISIBLE);
                btn.setImageResource(R.drawable.like);
                btn.setOnClickListener(new ImageButton.OnClickListener(){
                    @Override
                    public void onClick(View v) {
                    }
                });
            }
            */
        }
        final TextView content = (TextView)convertView.findViewById(R.id.item_qna_content);
        final TextView name = (TextView) convertView.findViewById(R.id.item_qna_name);

        final ImageView imageItem = (ImageView)convertView.findViewById(R.id.item_qna_image);

        FirebaseDatabase.getInstance().getReference().addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Q_uid[0] = dataSnapshot.child("question").child(qid).child("uid").getValue().toString();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        if(qnalist.getName() != null){
            name.setText(qnalist.getName());
        }
        content.setText(String.valueOf(qnalist.getContent()));

        if(qnalist.getIsComment()==0){
            final TextView title = (TextView) convertView.findViewById(R.id.item_qna_title);
            title.setText(qnalist.getTitle());
        }


        if(qnalist.getUrl() == null){
            Picasso.with(context)
                    .load(R.drawable.user)
                    .fit()
                    .centerInside()
                    .into(imageItem, new Callback.EmptyCallback() {
                        public void onSuccess() {
                        }
                    });
        }
        else{
            Picasso.with(context)
                    .load(qnalist.getUrl())
                    .fit()
                    .centerInside()
                    .into(imageItem, new Callback.EmptyCallback() {
                        public void onSuccess() {
                        }
                    });
        }

        if(IsComment==1){
            TextView good = (TextView) convertView.findViewById(R.id.item_qna_good);
            good.setText(String.valueOf(qnalist.getGood()));
            TextView bad = (TextView) convertView.findViewById(R.id.item_qna_bad);
            bad.setText(String.valueOf(qnalist.getBad()));
        }


        final String text = qnalist.getContent();
        final String A_uid = qnalist.getUid();

        if(qnalist.getIsComment()==1) {
            convertView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    PopupMenu pop = new PopupMenu(context.getApplicationContext(), view);
                    aid = qnalist.getAid();

                    if (uid.equals(Q_uid[0])) {
                        pop.getMenuInflater().inflate(R.menu.qa_1, pop.getMenu());
                        pop.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                            @Override
                            public boolean onMenuItemClick(MenuItem menuItem) {
                                final DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
                                switch (menuItem.getItemId()) {
                                    case R.id.accept:
                                        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(DataSnapshot dataSnapshot) {
                                                if (!dataSnapshot.child("question").child(qid).child("selected").getValue().toString().equals("")) {
                                                    Toast.makeText(context.getApplicationContext(), "이미 채택 완료된 질문입니다.", Toast.LENGTH_LONG).show();
                                                } else {
                                                    String point_str = dataSnapshot.child("Users").child(A_uid).child("points").getValue().toString();
                                                    int point = Integer.parseInt(point_str);
                                                    point += 5;
                                                    flag1=true;
                                                    flag2=true;
                                                    flag3=true;
                                                    mDatabase.child("question").child(qid).child("selected").setValue(aid);
                                                    mDatabase.child("Users").child(A_uid).child("points").setValue(point);

                                                }
                                            }

                                            @Override
                                            public void onCancelled(DatabaseError databaseError) {
                                            }
                                        });
                                        break;

                                    case R.id.report:
                                        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(DataSnapshot dataSnapshot) {
                                                boolean report_check = false;
                                                for (DataSnapshot snapshot : dataSnapshot.child("Users").child(A_uid).getChildren()) {
                                                    if (snapshot.getKey().toString().equals("report")) {
                                                        Log.d("AAAA", "1234" + snapshot.getKey().toString());
                                                        report_check = true;
                                                    }
                                                }
                                                if (!report_check) {
                                                    mDatabase.child("Users").child(A_uid).child("report").child("report_text").setValue(text);
                                                    mDatabase.child("Users").child(A_uid).child("report").child("report_acc_count").setValue(1);
                                                    mDatabase.child("Users").child(A_uid).child("report_count").setValue(1);
                                                    mDatabase.child("Users").child(A_uid).child("report_flag").setValue("true");
                                                } else {
                                                    String report_text = dataSnapshot.child("Users").child(A_uid).child("report").child("report_text").getValue().toString();
                                                    String report_acc_count = dataSnapshot.child("Users").child(A_uid).child("report").child("report_acc_count").getValue().toString();
                                                    if (report_text.equals(text) && Integer.parseInt(report_acc_count) < 5) {
                                                        int count = Integer.parseInt(report_acc_count) + 1;
                                                        mDatabase.child("Users").child(A_uid).child("report").child("report_acc_count").setValue(count);
                                                        if (count == 5) {
                                                            mDatabase.child("Users").child(A_uid).child("report_count").setValue(2);
                                                        }
                                                    } else if (!report_text.equals(text)) {
                                                        mDatabase.child("Users").child(A_uid).child("report2").child("report_text").setValue(text);
                                                        mDatabase.child("Users").child(A_uid).child("report_flag").setValue("true");
                                                        mDatabase.child("Users").child(A_uid).child("report_count").setValue(2);
                                                    }
                                                }
                                            }

                                            @Override
                                            public void onCancelled(DatabaseError databaseError) {

                                            }

                                        });
                                        break;
                                }
                                return false;
                            }
                        });
                        pop.show();
                        return false;
                    } else if (uid.equals(A_uid)) {
                        pop.getMenuInflater().inflate(R.menu.qa_2, pop.getMenu());
                        pop.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                            @Override
                            public boolean onMenuItemClick(MenuItem menuItem) {
                                final DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
                                switch (menuItem.getItemId()) {
                                    case R.id.remove:
                                        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(DataSnapshot dataSnapshot) {
                                                if (dataSnapshot.child("question").child(qid).child("selected").getValue().toString().equals(aid)) {
                                                    Toast.makeText(context.getApplicationContext(), "이미 채택 완료된 질문이여서 삭제 하실수 없습니다.", Toast.LENGTH_LONG).show();
                                                } else {
                                                    int point = 0;
                                                    for(int i =1; i<list.size();i++)
                                                    {
                                                        if(list.get(i).getAid().equals(aid)){
                                                            point=i;
                                                            break;
                                                        }
                                                    }
                                                    list.remove(point);
                                                    mDatabase.child("question").child(qid).child("answer").child(aid).removeValue();
                                                }
                                            }

                                            @Override
                                            public void onCancelled(DatabaseError databaseError) {
                                            }
                                        });

                                        break;

                                    case R.id.modify:
                                        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(DataSnapshot dataSnapshot) {
                                                if (dataSnapshot.child("question").child(qid).child("selected").getValue().toString().equals(aid)) {

                                                    Toast.makeText(context.getApplicationContext(), "이미 채택 완료된 질문이여서 수정 하실수 없습니다.", Toast.LENGTH_LONG).show();
                                                } else {

                                                    Intent intnet = new Intent(context.getApplicationContext(), Modification.class);
                                                    intnet.putExtra("qid", qid);
                                                    intnet.putExtra("aid", aid);
                                                    startActivityForResult((Activity) context, intnet, 3000, null);

                                                }
                                            }

                                            @Override
                                            public void onCancelled(DatabaseError databaseError) {
                                            }
                                        });
                                        break;
                                }
                                return false;
                            }
                        });
                        pop.show();
                        return false;
                    } else {
                        pop.getMenuInflater().inflate(R.menu.qa_3, pop.getMenu());
                        pop.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                            @Override
                            public boolean onMenuItemClick(MenuItem menuItem) {
                                final DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
                                switch (menuItem.getItemId()) {
                                    case R.id.recommend:
                                        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(DataSnapshot dataSnapshot) {
                                                boolean good_flag = false;
                                                for (DataSnapshot snapshot : dataSnapshot.child("question").child(qid).child("answer").child(aid).child("good").getChildren()) {
                                                    if (snapshot.getKey().toString().equals(uid))
                                                        good_flag = true;
                                                }
                                                if (good_flag) {
                                                    Toast.makeText(context.getApplicationContext(), "이미 해당 답변에 추천하신 상태입니다.", Toast.LENGTH_LONG).show();
                                                } else {
                                                    int good = Integer.parseInt(dataSnapshot.child("question").child(qid).child("answer").child(aid).child("good").child("points").getValue().toString());
                                                    good += 1;
                                                    flag2=true;
                                                    flag1=true;
                                                    mDatabase.child("question").child(qid).child("answer").child(aid).child("good").child("points").setValue(good);
                                                    mDatabase.child("question").child(qid).child("answer").child(aid).child("good").child(uid).setValue(true);
                                                    int points = Integer.parseInt(dataSnapshot.child("Users").child(A_uid).child("points").getValue().toString());
                                                    points += 1;
                                                    mDatabase.child("Users").child(A_uid).child("points").setValue(points);
                                                }
                                            }

                                            @Override
                                            public void onCancelled(DatabaseError databaseError) {
                                            }
                                        });
                                        break;

                                    case R.id.decommend:
                                        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(DataSnapshot dataSnapshot) {
                                                boolean bad_flag = false;
                                                for (DataSnapshot snapshot : dataSnapshot.child("question").child(qid).child("answer").child(aid).child("bad").getChildren()) {
                                                    if (snapshot.getKey().toString().equals(uid))
                                                        bad_flag = true;
                                                }
                                                if (bad_flag) {
                                                    Toast.makeText(context.getApplicationContext(), "이미 해당 답변에 비추천하신 상태입니다.", Toast.LENGTH_LONG).show();
                                                } else {
                                                    int bad = Integer.parseInt(dataSnapshot.child("question").child(qid).child("answer").child(aid).child("bad").child("points").getValue().toString());
                                                    bad += 1;
                                                    flag1=true;flag2=true;
                                                    mDatabase.child("question").child(qid).child("answer").child(aid).child("bad").child("points").setValue(bad);
                                                    mDatabase.child("question").child(qid).child("answer").child(aid).child("bad").child(uid).setValue(true);
                                                    int points = Integer.parseInt(dataSnapshot.child("Users").child(A_uid).child("points").getValue().toString());
                                                    points -= 1;
                                                    mDatabase.child("Users").child(A_uid).child("points").setValue(points);
                                                }
                                            }

                                            @Override
                                            public void onCancelled(DatabaseError databaseError) {

                                            }

                                        });
                                        break;
                                    case R.id.report:
                                        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(DataSnapshot dataSnapshot) {
                                                boolean report_check = false;
                                                for (DataSnapshot snapshot : dataSnapshot.child("Users").child(A_uid).getChildren()) {
                                                    if (snapshot.getKey().toString().equals("report")) {
                                                        Log.d("AAAA", "1234" + snapshot.getKey().toString());
                                                        report_check = true;
                                                    }
                                                }
                                                if (!report_check) {
                                                    mDatabase.child("Users").child(A_uid).child("report").child("report_text").setValue(text);
                                                    mDatabase.child("Users").child(A_uid).child("report").child("report_acc_count").setValue(1);
                                                    mDatabase.child("Users").child(A_uid).child("report_count").setValue(1);
                                                    mDatabase.child("Users").child(A_uid).child("report_flag").setValue("true");
                                                } else {
                                                    String report_text = dataSnapshot.child("Users").child(A_uid).child("report").child("report_text").getValue().toString();
                                                    String report_acc_count = dataSnapshot.child("Users").child(A_uid).child("report").child("report_acc_count").getValue().toString();
                                                    if (report_text.equals(text) && Integer.parseInt(report_acc_count) < 5) {
                                                        int count = Integer.parseInt(report_acc_count) + 1;
                                                        mDatabase.child("Users").child(A_uid).child("report").child("report_acc_count").setValue(count);
                                                        if (count == 5) {
                                                            mDatabase.child("Users").child(A_uid).child("report_count").setValue(2);
                                                        }
                                                    } else if (!report_text.equals(text)) {
                                                        mDatabase.child("Users").child(A_uid).child("report2").child("report_text").setValue(text);
                                                        mDatabase.child("Users").child(A_uid).child("report_flag").setValue("true");
                                                        mDatabase.child("Users").child(A_uid).child("report_count").setValue(2);
                                                    }
                                                }
                                            }

                                            @Override
                                            public void onCancelled(DatabaseError databaseError) {

                                            }

                                        });
                                        break;

                                }
                                return false;
                            }
                        });
                        pop.show();
                        return false;
                    }

                }
            });
        }

        return convertView;
    }

    public void addItem(QnaList param){
        QnaList templist = new QnaList();


        templist.setUid(param.getUid());
        templist.setContent(param.getContent());
        templist.setIsComment(param.getIsComment());
        templist.setTitle(param.getTitle());
        templist.setUrl(param.getUrl());
        templist.setTitle(param.getTitle());
        templist.setName(param.getName());
        templist.setAccept(param.getAccept());
        templist.setAid(param.getAid());
        templist.setGood(param.getGood());
        templist.setBad(param.getBad());
        list.add(templist);
    }
}

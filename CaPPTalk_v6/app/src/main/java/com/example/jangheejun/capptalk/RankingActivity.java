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
import android.widget.ListView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class RankingActivity extends Activity {

    private List<String> ranking;    // 데이터를 넣은 리스트변수
    private List<String> image_list;
    private List<String> name_list;
    private List<String> point_list;
    private ListView listView;          // 검색을 보여줄 리스트변수
    private Rankinglist adapter;      // 리스트뷰에 연결할 아답터
    private ArrayList<String> arraylist;

    private String uid;
    private String url;

    public void onCreate(Bundle saveInstanceState) {
        super.onCreate(saveInstanceState);
        setContentView(R.layout.activity_ranking);

        Intent intent = getIntent();
        uid = intent.getStringExtra("myUid");

        listView = (ListView) findViewById(R.id.lv_ranking);

        ranking = new ArrayList<String>();
        image_list = new ArrayList<String>();
        name_list = new ArrayList<String>();
        point_list = new ArrayList<String>();
        //arraylist = new ArrayList<String>();

        Log.d("log1", "test");

        getRankingList();
    }

    public void getRankingList() {
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
        Query RankingQuery = mDatabase.child("Users").orderByChild("points");
        RankingQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                ranking.clear();
                image_list.clear();
                name_list.clear();
                point_list.clear();
                int count = (int) dataSnapshot.getChildrenCount();

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

                    ranking.add(0, Integer.toString(count--));

                    if(snapshot.child("url").getValue() == null)
                        url = null;
                    else
                        url = snapshot.child("url").getValue().toString();

                    image_list.add(0, url);

                    name_list.add(0, snapshot.child("userName").getValue().toString());
                    point_list.add(0, snapshot.child("points").getValue().toString());
                    //arraylist.add(0, snapshot.child("userName").getValue().toString() + " / " + snapshot.child("points").getValue().toString());
                    adapter = new Rankinglist(ranking, image_list, name_list, point_list, RankingActivity.this);
                    listView.setAdapter(adapter);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }

        });



    }
}

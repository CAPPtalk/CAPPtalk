package com.example.jangheejun.capptalk;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import com.example.jangheejun.capptalk.fragments.ChatFragment;
import com.example.jangheejun.capptalk.fragments.GroupFragment;
import com.example.jangheejun.capptalk.fragments.ProfileFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class sample_activity_1 extends AppCompatActivity {

    Fragment fragment_profile = new ProfileFragment();
    Fragment fragment_chat = new ChatFragment();
    Fragment fragment_group = new GroupFragment();

    private MenuItem prevBottomView;
    private BottomNavigationView bottomnavigationview;

    DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();

    String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

    int number = 0;
    public int condition;
    public int click = 0;
    static int group_c;

    public static Activity SA;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sample_1);

        SA = sample_activity_1.this;

        bottomnavigationview = (BottomNavigationView) findViewById(R.id.underbar);

    }

    @Override
    protected void onStart() {
        super.onStart();

        mDatabase.child("Users").child(uid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.d("TAG","is this work well?");
                for(DataSnapshot snapshot: dataSnapshot.getChildren()){
                    String num = snapshot.getKey();
                    switch (num){
                        case "gotolist":
                            if(Integer.parseInt(snapshot.getValue().toString())== 1){
                                group_c = 1;
                            }
                            else group_c = 0;
                            Log.d("TAG_group_c",String.valueOf(group_c));
                            break;
                        default:
                            break;
                    }
                }

                condition = 1;
                if (click == 0) {
                    if(group_c==1){
                        View view = bottomnavigationview.findViewById(R.id.capptalk_group);
                        view.performClick();
                    }
                    else {
                        if (number == 0) {
                            View view = bottomnavigationview.findViewById(R.id.capptalk_profile);
                            view.performClick();
                            number = 1;
                        }
                        else {
                            if (condition == 1)
                                getFragmentManager().beginTransaction().detach(fragment_profile).attach(fragment_profile).commitAllowingStateLoss();
                        }
                    }
                }
                else if (click == 1) {
                    View view = bottomnavigationview.findViewById(R.id.capptalk_chat);
                    view.performClick();

                }
                else if (click == 2) {
                    View view = bottomnavigationview.findViewById(R.id.capptalk_group);
                    view.performClick();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        bottomnavigationview.setOnNavigationItemSelectedListener(
                new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.capptalk_profile:
                                getFragmentManager().beginTransaction().replace(R.id.frag_container, fragment_profile).commitAllowingStateLoss();
                                return true;
                            case R.id.capptalk_chat:
                                getFragmentManager().beginTransaction().replace(R.id.frag_container, fragment_chat).commitAllowingStateLoss();
                                return true;
                            case R.id.capptalk_group:
                                getFragmentManager().beginTransaction().replace(R.id.frag_container, fragment_group).commitAllowingStateLoss();
                                return true;
                        }
                        return false;
                    }
                }
        );
    }
}

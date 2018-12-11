package com.example.jangheejun.capptalk.fragments;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.example.jangheejun.capptalk.ChatActivity;
import com.example.jangheejun.capptalk.R;

public class SideBarView extends RelativeLayout {

    public SideBarView(Context context) {
        this(context, null);
        init();
    }

    public SideBarView(Context context, AttributeSet attrs) {
        super(context, attrs);

    }

    void init() {
        LayoutInflater.from(getContext()).inflate(R.layout.item_sidebar, this, true);

//        Button button = (Button) findViewById(R.id.itemSidebar_button_close);
//
//        BottomNavigationView bottomNavigationView = findViewById(R.id.itemSidebar_navigationView);
//        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
//            @Override
//            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
//
//                switch(item.getItemId()) {
//                    case R.id.action_qna:
//
//                        return true;
//
//                    case R.id.action_group:
//
//                        return true;
//                }
//
//                return false;
//            }
//        });

    }

}

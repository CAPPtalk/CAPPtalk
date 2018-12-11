package com.example.jangheejun.capptalk.fragments;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.jangheejun.capptalk.ChatActivity;
import com.example.jangheejun.capptalk.ChatListAdapter;
import com.example.jangheejun.capptalk.R;
import com.example.jangheejun.capptalk.sample_activity_1;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ChatFragment extends Fragment {

    ListView chatlist;
    ChatListAdapter adapter;

    String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
    String chatname;
    String chatTitle;
    String lastmessage;
    String classname;
    String tempImg;
    long timestamp;
    long chatcount;

    ArrayList namelist = new ArrayList();

    int limit;
    int count = 0;

    private DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();

    public ChatFragment(){
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.content_chat,container,false);

        ((sample_activity_1)getActivity()).condition = 0;
        ((sample_activity_1)getActivity()).click = 1;

        adapter = new ChatListAdapter();

        chatlist = (ListView)view.findViewById(R.id.fragment_chat) ;
        chatlist.setAdapter(adapter);

        ValueEventListener valueEventListener = mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                adapter.listclear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    switch (snapshot.getKey()) {

                        case "Users":
                            for (DataSnapshot ursnapshot : snapshot.child(uid).child("UserRooms").getChildren()) {
                                limit = (int) snapshot.child(uid).child("UserRooms").getChildrenCount();
                                String urname = ursnapshot.getValue().toString();
                                switch (urname) {
                                    case "true":
                                        chatname = ursnapshot.getKey();
                                        namelist.add(chatname);
                                        break;
                                    default:
                                        break;
                                }
                            }
                            break;
                        case "rooms":
                            for (DataSnapshot csnapshot : snapshot.getChildren()) {
                                classname = csnapshot.getKey();
                                if (namelist.contains(classname)) {
                                    for (DataSnapshot rsnapshot : snapshot.child(classname).getChildren()) {
                                        String rname = rsnapshot.getKey();
                                        switch (rname) {
                                            case "info":
                                                lastmessage = "채팅방에 메세지가 없습니다.";
                                                for (DataSnapshot isnapshot : rsnapshot.getChildren()) {
                                                    String iname = isnapshot.getKey();
                                                    switch (iname) {
                                                        case "lastMessage":
                                                            for (DataSnapshot lsnapshot : isnapshot.getChildren()) {
                                                                String lname = lsnapshot.getKey();
                                                                switch (lname) {
                                                                    case "message":
                                                                        lastmessage = lsnapshot.getValue().toString();
                                                                        break;
                                                                    case "timestamp":
                                                                        timestamp = Long.parseLong(lsnapshot.getValue().toString());
                                                                        break;
                                                                    default:
                                                                        break;
                                                                }
                                                            }
                                                            break;
                                                        case "name":
                                                            chatTitle = isnapshot.getValue().toString();
                                                            break;
                                                        case "image":
                                                            tempImg = isnapshot.getValue().toString();
                                                            break;
                                                        default:
                                                            break;
                                                    }
                                                }
                                                break;
                                            case "users":
                                                chatcount = rsnapshot.getChildrenCount();
                                                adapter.addItem(chatcount,
                                                        chatTitle,
                                                        lastmessage,
                                                        timestamp,
                                                        tempImg);
                                                adapter.notifyDataSetChanged();
                                                if (count < limit) count++;
                                                if (count == limit) count = 0;
                                                break;
                                            default:
                                                break;
                                        }
                                    }
                                }
                            }
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

        chatlist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String RoomID = String.valueOf(namelist.get(position).toString());
                Intent Chatintent = new Intent(view.getContext(),ChatActivity.class);
                Chatintent.putExtra("RoomID", RoomID);
                startActivity(Chatintent);
            }
        });

        return view;
    }
}
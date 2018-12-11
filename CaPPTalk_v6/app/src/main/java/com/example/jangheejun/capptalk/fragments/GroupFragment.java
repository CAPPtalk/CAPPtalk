package com.example.jangheejun.capptalk.fragments;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.jangheejun.capptalk.ChatListAdapter;
import com.example.jangheejun.capptalk.GroupChatActivity;
import com.example.jangheejun.capptalk.R;
import com.example.jangheejun.capptalk.sample_activity_1;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class GroupFragment extends Fragment {

    ListView grouplist;
    ChatListAdapter adapter;

    String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
    String studyname;
    String groupTitle;
    String lastmessage;
    String groupname;
    String tempImg;
    long timestamp;
    long groupcount;

    ArrayList namelist = new ArrayList();

    int limit;
    int count = 0;

    private DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();

    public GroupFragment(){
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.content_group,container,false);

        ((sample_activity_1)getActivity()).condition = 0;
        ((sample_activity_1)getActivity()).click = 2;

        adapter = new ChatListAdapter();

        grouplist = (ListView)view.findViewById(R.id.fragment_group) ;
        grouplist.setAdapter(adapter);

        ValueEventListener valueEventListener = mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                adapter.listclear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    switch (snapshot.getKey()) {

                        case "Users":
                            for (DataSnapshot grsnapshot : snapshot.child(uid).child("GroupRooms").getChildren()) {
                                limit = (int) snapshot.child(uid).child("GroupRooms").getChildrenCount();
                                String grname = grsnapshot.getValue().toString();
                                switch (grname) {
                                    case "true":
                                        studyname = grsnapshot.getKey();
                                        namelist.add(studyname);
                                        break;
                                    default:
                                        break;
                                }
                            }
                            break;
                        case "groups":
                            for (DataSnapshot gsnapshot : snapshot.getChildren()) {
                                groupname = gsnapshot.getKey();
                                if (namelist.contains(groupname)) {
                                    for (DataSnapshot rsnapshot : snapshot.child(groupname).getChildren()) {
                                        String rname = rsnapshot.getKey();
                                        switch (rname) {
                                            case "info":
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
                                                            groupTitle = isnapshot.getValue().toString();
                                                            break;
                                                        default:
                                                            break;
                                                    }
                                                }
                                                break;
                                            case "users":
                                                groupcount = rsnapshot.getChildrenCount();
                                                tempImg="logo";
                                                adapter.addItem(groupcount,
                                                        groupTitle,
                                                        lastmessage,
                                                        timestamp,tempImg);
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

        grouplist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String RoomID = String.valueOf(namelist.get(position).toString());
                Intent Chatintent = new Intent(view.getContext(),GroupChatActivity.class);
                Chatintent.putExtra("RoomID", RoomID);
                startActivity(Chatintent);
            }
        });

        return view;
    }
}
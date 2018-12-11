package com.example.jangheejun.capptalk.fragments;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.jangheejun.capptalk.AddGroupActivity;
import com.example.jangheejun.capptalk.ChatActivity;
import com.example.jangheejun.capptalk.JoinGroupActivity;
import com.example.jangheejun.capptalk.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;

public class SideBarGroupFragment extends Fragment {

    public String uid;
    public String RoomID;

    @Nullable
    @Override
    public View onCreateView(final LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

        uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        final String RoomID = getArguments().getString("RoomID");
        this.RoomID = RoomID;

        final View view = inflater.inflate(R.layout.fragment_sidebar_group, container, false);

//        FirebaseDatabase.getInstance().getReference().child("groups").addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//
//                RecyclerView recyclerView = view.findViewById(R.id.sidebarGroupFragment_recyclerView);
//                recyclerView.setAdapter(new SideBarGroupRecyclerViewAdapter(RoomID));
//                recyclerView.setLayoutManager(new LinearLayoutManager(inflater.getContext()));
//
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        });

        RecyclerView recyclerView = view.findViewById(R.id.sidebarGroupFragment_recyclerView);
        recyclerView.setAdapter(new SideBarGroupRecyclerViewAdapter(RoomID));
        recyclerView.setLayoutManager(new LinearLayoutManager(inflater.getContext()));

        return view;
    }

    private class SideBarGroupRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        private List<String> GroupIDs = new ArrayList<>();
        String groupID;
        public Map<String, Object> users = new HashMap<>();

        public SideBarGroupRecyclerViewAdapter(String roomID) {

            FirebaseDatabase.getInstance().getReference().child("rooms").child(roomID).child("groups").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    for(DataSnapshot item: dataSnapshot.getChildren()) {
                        GroupIDs.add(item.getKey());
                    }
                    notifyDataSetChanged();

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_sidebar_group, parent, false);

            return new CustomViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

            final CustomViewHolder customViewHolder = (CustomViewHolder)holder;
            final String groupID = GroupIDs.get(position);
            this.groupID = groupID;
            final Map<String, Object> uids = new HashMap<>();

            FirebaseDatabase.getInstance().getReference().child("groups").child(groupID).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    users.clear();
                    for(DataSnapshot item: dataSnapshot.child("users").getChildren()) {
                        users.put(item.getKey(), true);
                        uids.put(item.getKey(), true);
                    }
                    String creator = dataSnapshot.child("info").child("creator").getValue().toString();
                    String groupTitle = dataSnapshot.child("info").child("name").getValue().toString();
                    String maxUsesrSize = dataSnapshot.child("info").child("maxcount").getValue().toString();
                    String joinUserSize = String.valueOf(users.size());

                    customViewHolder.textView_creator.setText(creator);
                    customViewHolder.textView_title.setText(groupTitle);
                    customViewHolder.textView_maxUserSize.setText(maxUsesrSize);
                    customViewHolder.textView_joinUserSize.setText(joinUserSize);

                    if(!maxUsesrSize.equals(joinUserSize)) {
                        customViewHolder.button_join.setVisibility(View.VISIBLE);
                    }

//                    notifyDataSetChanged();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }

            });

            customViewHolder.button_join.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if(!uids.containsKey(uid)) {
                        Intent intent = new Intent(view.getContext(), JoinGroupActivity.class);
                        intent.putExtra("groupID", groupID);
                        intent.putExtra("roomID", RoomID);
                        startActivity(intent);
                    } else {

                        Toast.makeText(view.getContext(), "이미 참여한 스터디입니다.", Toast.LENGTH_SHORT).show();

                    }

                }
            });

            uids.clear();
        }

        @Override
        public int getItemCount() {
            return GroupIDs.size();
        }

        private class CustomViewHolder extends RecyclerView.ViewHolder {

            public TextView textView_title;
            public TextView textView_maxUserSize;
            public TextView textView_joinUserSize;
            public TextView textView_creator;
            public Button button_join;
//            public TextView textView_timestamp;


            public CustomViewHolder(View view) {
                super(view);

                textView_title = view.findViewById(R.id.sidebarGroupItem_textView_title);
                textView_maxUserSize = view.findViewById(R.id.sidebarGroupItem_textView_maxUserSize);
                textView_joinUserSize = view.findViewById(R.id.sidebarGroupItem_textView_joinUserSize);
                textView_creator = view.findViewById(R.id.sidebarGroupItem_textView_creator);
                button_join = view.findViewById(R.id.sidebarGroupItem_button_join);

//                textView_timestamp = view.findViewById(R.id.sidebarGroupItem_textView_timestamp);
            }
        }
    }

}

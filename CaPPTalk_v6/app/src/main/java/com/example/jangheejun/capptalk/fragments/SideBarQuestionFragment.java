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
import android.widget.TextView;

import com.example.jangheejun.capptalk.QnaActivity;
import com.example.jangheejun.capptalk.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import javax.annotation.Nullable;

public class SideBarQuestionFragment extends Fragment {

    private String RoomID;
    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy.MM.dd HH:mm");


    RecyclerView recyclerView;
    QuestionRecyclerViewAdapter questionRecyclerViewAdapter;

    public void que_update(String qid){
        Log.d("CCCC", "1234");
        questionRecyclerViewAdapter.QuestionIDs.add(qid);
        recyclerView.scrollToPosition(questionRecyclerViewAdapter.QuestionIDs.size() - 1);
        questionRecyclerViewAdapter.notifyDataSetChanged();
    }


    @Nullable
    @Override
    public View onCreateView(final LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

        final String RoomID = getArguments().getString("RoomID");
        this.RoomID = RoomID;

        final View view = inflater.inflate(R.layout.fragment_sidebar_question, container, false);

//        FirebaseDatabase.getInstance().getReference().child("question").addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//
//                RecyclerView recyclerView = view.findViewById(R.id.sidebarQuestionFragment_recyclerView);
//                recyclerView.setAdapter(new QuestionRecyclerViewAdapter(RoomID));
//                recyclerView.setLayoutManager(new LinearLayoutManager(inflater.getContext()));
//
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        });

        recyclerView = view.findViewById(R.id.sidebarQuestionFragment_recyclerView);
        questionRecyclerViewAdapter = new QuestionRecyclerViewAdapter(RoomID);
        recyclerView.setAdapter(questionRecyclerViewAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(inflater.getContext()));

        return view;
    }

    class QuestionRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        private List<String> QuestionIDs = new ArrayList<>();

        public QuestionRecyclerViewAdapter(String RoomID) {

            FirebaseDatabase.getInstance().getReference().child("rooms").child(RoomID).child("question").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    for(DataSnapshot item: dataSnapshot.getChildren()) {
                        QuestionIDs.add(item.getKey());
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

            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_sidebar_question, parent, false);

            return new CustomViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

            final CustomViewHolder customViewHolder = (CustomViewHolder)holder;
            final String questionID = QuestionIDs.get(position);

            if(QuestionIDs.size() > 0)  {
//            if(questionModels.get(position).questions.size() > 0) {
                FirebaseDatabase.getInstance().getReference().child("question").child(questionID).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        String questionTitle = dataSnapshot.child("Title").getValue().toString();
                        String questionContent = dataSnapshot.child("content").getValue().toString();
                        customViewHolder.textView_title.setText(questionTitle);
                        customViewHolder.textView_content.setText(questionContent);

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

                FirebaseDatabase.getInstance().getReference().child("rooms").child(RoomID).child("comments")
                        .child(questionID).child("timestamp").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        long unixTime = (long) dataSnapshot.getValue();
                        Date date = new Date(unixTime);
                        simpleDateFormat.setTimeZone(TimeZone.getTimeZone("Asia/Seoul"));
                        customViewHolder.textView_timestamp.setText(simpleDateFormat.format(date));

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }

            // start new activity
            customViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Intent intent = new Intent(view.getContext(), QnaActivity.class);
                    intent.putExtra("qid", questionID);
                    startActivity(intent);

                }
            });


        }

        @Override
        public int getItemCount() {
            return QuestionIDs.size();
        }

        private class CustomViewHolder extends RecyclerView.ViewHolder {

            public TextView textView_title;
            public TextView textView_content;
            public TextView textView_timestamp;

            public CustomViewHolder(View itemView) {
                super(itemView);

                textView_title = itemView.findViewById(R.id.sidebarQuestionItem_textView_title);
                textView_content = itemView.findViewById(R.id.sidebarQuestionItem_textView_content);
                textView_timestamp = itemView.findViewById(R.id.sidebarQuestionItem_textView_timestamp);

            }
        }
    }
}
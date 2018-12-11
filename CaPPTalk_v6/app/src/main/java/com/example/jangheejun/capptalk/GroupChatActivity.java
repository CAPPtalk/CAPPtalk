package com.example.jangheejun.capptalk;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.jangheejun.capptalk.models.ChatModel;
import com.example.jangheejun.capptalk.models.UserModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

public class GroupChatActivity extends AppCompatActivity {

    Map<String, UserModel> users = new HashMap<>();
    List<ChatModel.Comment> comments = new ArrayList<>();
    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy.MM.dd HH:mm");
    String RoomID;
    String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
    int peopleCount = 0;
    int check;

    TextView titleview;
    EditText editText;
    RecyclerView recyclerView;

    DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();

    private DatabaseReference databaseReference;
    private ValueEventListener valueEventListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_chat);

        check = getIntent().getIntExtra("check",0);
        if(check == 0){
            mDatabase.child("Users").child(uid).child("gotolist").setValue(0);
        }
        else mDatabase.child("Users").child(uid).child("gotolist").setValue(1);

        RoomID = getIntent().getStringExtra("RoomID");

        titleview = (TextView)findViewById(R.id.groupChatActivity_textView_toolbar_title);
        editText = (EditText)findViewById(R.id.groupChatActivity_editText);

        mDatabase.child("groups").child(RoomID).child("info").child("name").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                titleview.setText((CharSequence) dataSnapshot.getValue());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        FirebaseDatabase.getInstance().getReference().child("Users").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //users = (Map<String, UserModel>) dataSnapshot.getValue();
                for(DataSnapshot item : dataSnapshot.getChildren()) {
                    users.put(item.getKey(), item.getValue(UserModel.class));

                    recyclerView = findViewById(R.id.groupChatActivity_recyclerView);
                    recyclerView.setAdapter(new GroupChatRecyclerViewAdapter());
                    recyclerView.setLayoutManager(new LinearLayoutManager(GroupChatActivity.this));
                }

                //System.out.println(users.size());
                init();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

//        recyclerView = findViewById(R.id.groupChatActivity_recyclerView);
//        recyclerView.setAdapter(new GroupChatRecyclerViewAdapter());
//        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        Button btn = (Button)findViewById(R.id.group_button_exit);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("TAG",String.valueOf(check));
                mDatabase.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            String num = snapshot.getKey();
                            switch (num) {
                                case "Users":
                                    mDatabase.child("Users").child(uid).child("GroupRooms").child(RoomID).removeValue();
                                    break;
                                case "groups":
                                    long cond = snapshot.child(RoomID).child("users").getChildrenCount();
                                    if(cond == 1){
                                        mDatabase.child("groups").child(RoomID).removeValue();
                                        mDatabase.child("rooms").child(RoomID.substring(0,10)).child("groups").child(RoomID).removeValue();
                                    }
                                    else mDatabase.child("groups").child(RoomID).child("users").child(uid).removeValue();
                                    break;
                                default:
                                    break;
                            }
                        }
                        finish();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        });

    }

    void init() {

        Button button = (Button) findViewById(R.id.groupChatActivity_button_send);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final ChatModel.Comment comment = new ChatModel.Comment();
                comment.uid = uid;
                comment.message = editText.getText().toString();
                comment.timestamp = ServerValue.TIMESTAMP;

                if (!(comment.message.equals(""))) {
                    FirebaseDatabase.getInstance().getReference().child("groups").child(RoomID).child("comments")
                            .push().setValue(comment).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            FirebaseDatabase.getInstance().getReference().child("groups").child(RoomID).child("info").child("lastMessage")
                                    .setValue(comment).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {

                                    editText.setText("");

                                }
                            });

                        }
                    });

                }
            }

        });

    }

    class GroupChatRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        public GroupChatRecyclerViewAdapter() {

            getMessageList();

        }

        void getMessageList() {

            databaseReference = FirebaseDatabase.getInstance().getReference().child("groups").child(RoomID).child("comments");

            valueEventListener = databaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    comments.clear();
                    Map<String, Object> readUsersMap = new HashMap<>();

                    for(DataSnapshot item : dataSnapshot.getChildren()) {
                        String key = item.getKey();
                        ChatModel.Comment comment_origin = item.getValue(ChatModel.Comment.class);
                        ChatModel.Comment comment_modify = item.getValue(ChatModel.Comment.class);
                        comment_modify.readUsers.put(uid, true);

                        readUsersMap.put(key, comment_modify);
                        comments.add(comment_origin);
                    }

                    if(!(comments.size() == 0) && !comments.get(comments.size() - 1).readUsers.containsKey(uid)) {
                        FirebaseDatabase.getInstance().getReference().child("groups").child(RoomID).child("comments")
                                .updateChildren(readUsersMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {

                                // renew messages
                                notifyDataSetChanged();
                                recyclerView.scrollToPosition(comments.size() - 1);

                            }
                        });
                    } else {
                        // renew messages
                        notifyDataSetChanged();
                        recyclerView.scrollToPosition(comments.size() - 1);
                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_message, parent, false);

            return new GroupChatViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            GroupChatViewHolder messageViewHolder = ((GroupChatViewHolder)holder);

            // make chat bubble
            if(comments.get(position).uid.equals(uid)) {
                messageViewHolder.textView_name.setText(users.get(comments.get(position).uid).userName);
                messageViewHolder.textView_message.setText(comments.get(position).message);
                messageViewHolder.textView_message.setBackgroundResource(R.drawable.rightbubble);
                messageViewHolder.linearLayout_destination.setVisibility(View.GONE);
                messageViewHolder.textView_message.setTextSize(15);
                messageViewHolder.linearLayout_main.setGravity(Gravity.RIGHT);
                setReadCounter(position, messageViewHolder.textView_readCounter_left);
            } else {
//                make opponent's profile image
//                Glide.with(holder.itemView.getContext()).load(destinationUserModel.profileImageUrl).apply(new RequestOptions().circleCrop())
//                        .into(messageViewHolder.imageView_profile);

                if(users.get(comments.get(position).uid).url == null)
                    messageViewHolder.imageView_profile.setImageDrawable(ContextCompat.getDrawable(holder.itemView.getContext(), R.drawable.user));

                else{
                    Picasso.with(holder.itemView.getContext())
                            .load(users.get(comments.get(position).uid).url)
                            .fit()
                            .centerInside()
                            .into(messageViewHolder.imageView_profile, new Callback.EmptyCallback() {
                                public void onSuccess() {
                                }
                            });
                }

                messageViewHolder.textView_name.setText(users.get(comments.get(position).uid).userName);
                messageViewHolder.textView_message.setText(comments.get(position).message);
                messageViewHolder.textView_message.setBackgroundResource(R.drawable.leftbubble);
                messageViewHolder.linearLayout_destination.setVisibility(View.VISIBLE);
                messageViewHolder.textView_message.setTextSize(15);
                messageViewHolder.linearLayout_main.setGravity(Gravity.LEFT);
                setReadCounter(position, messageViewHolder.textView_readCounter_right);
            }

            long unixTime = (long) comments.get(position).timestamp;
            Date date = new Date(unixTime);
            simpleDateFormat.setTimeZone(TimeZone.getTimeZone("Asia/Seoul"));
            String time = (String) simpleDateFormat.format(date);
            messageViewHolder.textView_timestamp.setText(time);
        }

        void setReadCounter(final int position, final TextView textView) {

            if(peopleCount == 0) {
                FirebaseDatabase.getInstance().getReference().child("groups").child(RoomID).child("users").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        Map<String, Boolean> users = (Map<String, Boolean>) dataSnapshot.getValue();
                        peopleCount = users.size();
                        int count = peopleCount - comments.get(position).readUsers.size();

                        if (count > 0) {
                            textView.setVisibility(View.VISIBLE);
                            textView.setText(String.valueOf(count));
                        } else {
                            textView.setVisibility(View.INVISIBLE);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            } else {
                int count = peopleCount - comments.get(position).readUsers.size();

                if (count > 0) {
                    textView.setVisibility(View.VISIBLE);
                    textView.setText(String.valueOf(count));
                } else {
                    textView.setVisibility(View.INVISIBLE);
                }
            }
        }

        @Override
        public int getItemCount() {
            return comments.size();
        }

        private class GroupChatViewHolder extends RecyclerView.ViewHolder {

            public TextView textView_message;
            public TextView textView_name;
            public ImageView imageView_profile;
            public LinearLayout linearLayout_destination;
            public LinearLayout linearLayout_main;
            public TextView textView_timestamp;
            public TextView textView_readCounter_left;
            public TextView textView_readCounter_right;


            public GroupChatViewHolder(View view) {
                super(view);

                textView_message = view.findViewById(R.id.messageItem_textView_message);
                textView_name = view.findViewById(R.id.messageItem_textView_name);
                textView_timestamp = view.findViewById(R.id.messageItem_textView_timestamp);
                imageView_profile = view.findViewById(R.id.messageItem_imageView_profile);
                linearLayout_destination = view.findViewById(R.id.messageItem_linearLayout_destination);
                linearLayout_main = view.findViewById(R.id.messageItem_linearLayout_main);
                textView_readCounter_left = view.findViewById(R.id.messageItem_textView_readCounter_left);
                textView_readCounter_right = view.findViewById(R.id.messageItem_textView_readCounter_right);
            }

        }

    }
}

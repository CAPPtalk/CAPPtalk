package com.example.jangheejun.capptalk;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.example.jangheejun.capptalk.fragments.SideBarGroupFragment;
import com.example.jangheejun.capptalk.fragments.SideBarQuestionFragment;
import com.example.jangheejun.capptalk.fragments.SideBarView;
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

public class ChatActivity extends AppCompatActivity {

    Map<String, UserModel> users = new HashMap<>();
    List<ChatModel.Comment> comments = new ArrayList<>();
    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy.MM.dd HH:mm");
    String RoomID;
    String myUid;
    int peopleCount = 0;
    long check;
    long now;
    ArrayList userlist = new ArrayList();
    boolean isSideBarOn = false;

    EditText editText;
    RecyclerView recyclerView;
    TextView textView_roomName;
    FrameLayout frameLayout_sidebar_cover;
    FrameLayout frameLayout_sidebar_blank;
    FrameLayout frameLayout_sidebar_main;

    Fragment fragment_sidebar_question = new SideBarQuestionFragment();
    Fragment fragment_sidebar_group = new SideBarGroupFragment();

    private DatabaseReference databaseReference;
    private ValueEventListener valueEventListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        RoomID = getIntent().getStringExtra("RoomID");
        myUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        FirebaseDatabase.getInstance().getReference().child("Users").child(myUid).child("gotolist").setValue(0);

        Toolbar toolbar = (Toolbar) findViewById(R.id.chatActivity_toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);

        textView_roomName = (TextView) findViewById(R.id.chatActivity_textView_toolbar_title);
        FirebaseDatabase.getInstance().getReference().child("rooms").child(RoomID).child("info").child("name").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                textView_roomName.setText((CharSequence) dataSnapshot.getValue());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        editText = (EditText) findViewById(R.id.chatActivity_editText);
        FirebaseDatabase.getInstance().getReference().child("Users").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot item : dataSnapshot.getChildren()) {
                    users.put(item.getKey(), item.getValue(UserModel.class));

                    recyclerView = findViewById(R.id.chatActivity_recyclerView);
                    recyclerView.setAdapter(new ChatRecyclerViewAdapter());
                    recyclerView.setLayoutManager(new LinearLayoutManager(ChatActivity.this));

                }

                init();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        frameLayout_sidebar_cover = (FrameLayout) findViewById(R.id.chatActivity_frameLayout_sidebar_cover);
        frameLayout_sidebar_blank = (FrameLayout) findViewById(R.id.chatActivity_frameLayout_sidebar_blank);
        frameLayout_sidebar_blank.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                closeSideBar();

            }
        });
        frameLayout_sidebar_main = (FrameLayout) findViewById(R.id.chatActivity_frameLayout_sidebar_main);
        SideBarView sideBarView = new SideBarView(ChatActivity.this);
        frameLayout_sidebar_main.addView(sideBarView);

        final Button button = (Button) findViewById(R.id.itemSidebar_button_close);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                closeSideBar();

            }
        });

        final Button button_add = (Button) findViewById(R.id.itemSidebar_button_add);
        button_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(ChatActivity.this, AddQuestionActivity.class);
                intent.putExtra("RoomID", RoomID);
                startActivityForResult(intent, 1000);

            }
        });

        final Bundle bundle = new Bundle(1);
        bundle.putString("RoomID", RoomID);
        fragment_sidebar_question.setArguments(bundle);
        fragment_sidebar_group.setArguments(bundle);
        getFragmentManager().beginTransaction().replace(R.id.itemSidebar_frameLayout_content, fragment_sidebar_question).commit();

        BottomNavigationView bottomNavigationView = findViewById(R.id.itemSidebar_navigationView);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                switch(item.getItemId()) {
                    case R.id.action_qna:
//                        fragment_sidebar_question.setArguments(bundle);
                        getFragmentManager().beginTransaction().replace(R.id.itemSidebar_frameLayout_content, fragment_sidebar_question).commitAllowingStateLoss();
                        button_add.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {

                                Intent intent = new Intent(ChatActivity.this, AddQuestionActivity.class);
                                intent.putExtra("RoomID", RoomID);
                                startActivityForResult(intent, 1000);

                            }
                        });

                        return true;

                    case R.id.action_group:
//                        Fragment fragment_sidebar_group = new SideBarGroupFragment();
//                        fragment_sidebar_group.setArguments(bundle);
                        getFragmentManager().beginTransaction().replace(R.id.itemSidebar_frameLayout_content, fragment_sidebar_group).commitAllowingStateLoss();
                        button_add.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {

                                Intent intent = new Intent(ChatActivity.this, AddGroupActivity.class);
                                intent.putExtra("RoomID", RoomID);
                                startActivity(intent);

                            }
                        });


                        return true;
                }

                return false;
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==1000) {
            if(resultCode==Activity.RESULT_OK){
                String qid = data.getStringExtra("qid");
                Log.d("CCCC", qid);
                getFragmentManager().beginTransaction().replace(R.id.itemSidebar_frameLayout_content, fragment_sidebar_question,"fra_tag").commit();

                //!!HERE THE APP CRASHES (java.lang.NullPointerException = findFragmentByTag returns null
                ((SideBarQuestionFragment) getFragmentManager().findFragmentByTag("fra_tag")).que_update(qid);

            }
        }
    }

    void init() {

        Button button = (Button) findViewById(R.id.chatActivity_button_send);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final ChatModel.Comment comment = new ChatModel.Comment();
                comment.uid = myUid;
                comment.message = editText.getText().toString();
                comment.timestamp = ServerValue.TIMESTAMP;

                if (!(comment.message.equals(""))) {
                    FirebaseDatabase.getInstance().getReference().child("rooms").child(RoomID).child("comments")
                            .push().setValue(comment).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            FirebaseDatabase.getInstance().getReference().child("rooms").child(RoomID).child("info").child("lastMessage")
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

    class ChatRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        public ChatRecyclerViewAdapter() {

            getMessageList();

        }

        void getMessageList() {

            databaseReference = FirebaseDatabase.getInstance().getReference().child("rooms").child(RoomID).child("comments");

            valueEventListener = databaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    comments.clear();
                    Map<String, Object> readUsersMap = new HashMap<>();

                    for(DataSnapshot item : dataSnapshot.getChildren()) {
                        String key = item.getKey();
                        ChatModel.Comment comment_origin = item.getValue(ChatModel.Comment.class);
                        ChatModel.Comment comment_modify = item.getValue(ChatModel.Comment.class);
                        comment_modify.readUsers.put(myUid, true);

                        readUsersMap.put(key, comment_modify);
                        comments.add(comment_origin);
                    }

                    if(!(comments.size() == 0) && !comments.get(comments.size() - 1).readUsers.containsKey(myUid)) {
                        FirebaseDatabase.getInstance().getReference().child("rooms").child(RoomID).child("comments")
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

            return new ChatViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder, int position) {
            final ChatViewHolder messageViewHolder = ((ChatViewHolder)holder);

            final String text = comments.get(position).message;
            final String report_uid = comments.get(position).uid;
            final String qid = comments.get(position).qid;
            final String gid = comments.get(position).gid;

            DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
            int size = Math.round(50 * displayMetrics.density);
            int  count_size = Math.round(10 * displayMetrics.density);

            LinearLayout.LayoutParams myParam = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            LinearLayout.LayoutParams opParam = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            LinearLayout.LayoutParams countParam = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);

            myParam.leftMargin = size;
            opParam.rightMargin = size;
            countParam.leftMargin = count_size;

            // make chat bubble
            if(comments.get(position).uid.equals(myUid)) {
                messageViewHolder.textView_name.setVisibility(View.GONE);
                messageViewHolder.textView_message.setText(comments.get(position).message);
                messageViewHolder.textView_message.setBackgroundResource(R.drawable.rightbubble);
                messageViewHolder.linearLayout_destination.setVisibility(View.GONE);
                messageViewHolder.textView_message.setTextSize(15);
                messageViewHolder.linearLayout_main.setGravity(Gravity.RIGHT);
                messageViewHolder.textView_message.setLayoutParams(myParam);
                messageViewHolder.linearLayout_count.setLayoutParams(myParam);
                setReadCounter(position, messageViewHolder.textView_readCounter_left);
                messageViewHolder.textView_readCounter_right.setVisibility(View.GONE);
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

                messageViewHolder.textView_name.setVisibility(View.VISIBLE);
                messageViewHolder.textView_message.setLayoutParams(opParam);
                messageViewHolder.linearLayout_count.setLayoutParams(countParam);
                messageViewHolder.textView_name.setText(users.get(comments.get(position).uid).userName);
                messageViewHolder.textView_message.setText(comments.get(position).message);
                messageViewHolder.textView_message.setBackgroundResource(R.drawable.leftbubble);
                messageViewHolder.linearLayout_destination.setVisibility(View.VISIBLE);
                messageViewHolder.textView_message.setTextSize(15);
                messageViewHolder.linearLayout_main.setGravity(Gravity.LEFT);
                setReadCounter(position, messageViewHolder.textView_readCounter_right);
                messageViewHolder.textView_readCounter_left.setVisibility(View.GONE);
            }

            if(!report_uid.equals(myUid)){
                messageViewHolder.textView_message.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View view) {
                        PopupMenu pop = new PopupMenu(getApplicationContext(), view);
                        getMenuInflater().inflate(R.menu.report_popup, pop.getMenu());
                        pop.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                            @Override
                            public boolean onMenuItemClick(MenuItem menuItem) {
                                switch (menuItem.getItemId()){
                                    case R.id.report:
                                        final DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
                                        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(DataSnapshot dataSnapshot) {
                                                boolean report_check=false;
                                                for (DataSnapshot snapshot : dataSnapshot.child("Users").child(report_uid).getChildren()) {
                                                    if(snapshot.getKey().toString().equals("report")) {
                                                        Log.d("AAAA", "1234" + snapshot.getKey().toString());
                                                        report_check = true;
                                                    }
                                                }
                                                if(!report_check)
                                                {
                                                    mDatabase.child("Users").child(report_uid).child("report").child("report_text").setValue(text);
                                                    mDatabase.child("Users").child(report_uid).child("report").child("report_acc_count").setValue(1);
                                                    mDatabase.child("Users").child(report_uid).child("report_count").setValue(1);
                                                    mDatabase.child("Users").child(report_uid).child("report_flag").setValue("true");
                                                }
                                                else {
                                                    String report_text = dataSnapshot.child("Users").child(report_uid).child("report").child("report_text").getValue().toString();
                                                    String report_acc_count = dataSnapshot.child("Users").child(report_uid).child("report").child("report_acc_count").getValue().toString();
                                                    if (report_text.equals(text) && Integer.parseInt(report_acc_count) < 5) {
                                                        int count = Integer.parseInt(report_acc_count) + 1;
                                                        mDatabase.child("Users").child(report_uid).child("report").child("report_acc_count").setValue(count);
                                                        if (count == 5) {
                                                            mDatabase.child("Users").child(report_uid).child("report_count").setValue(2);
                                                        }
                                                    }
                                                    else if (!report_text.equals(text)) {
                                                        mDatabase.child("Users").child(report_uid).child("report2").child("report_text").setValue(text);
                                                        mDatabase.child("Users").child(report_uid).child("report_flag").setValue("true");
                                                        mDatabase.child("Users").child(report_uid).child("report_count").setValue(2);
                                                    }
                                                }

                                            }

                                            @Override
                                            public void onCancelled(DatabaseError databaseError) {

                                            }

                                        });

                                }
                                return false;
                            }
                        });
                        pop.show();
                        return false;
                    }
                });
            }
            else
            {
                messageViewHolder.textView_message.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View view) {
                        return false;
                    }
                });

            }

            messageViewHolder.textView_message.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    check=0;

                    if(qid != null) {
                        Intent intent = new Intent(ChatActivity.this, QnaActivity.class);
                        intent.putExtra("qid", qid);
                        startActivity(intent);
                        finish();
                    }

                    if(gid != null) {
                        FirebaseDatabase.getInstance().getReference().child("groups").child(gid).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                                    String num = snapshot.getKey();
                                    switch (num){
                                        case "info":
                                            for (DataSnapshot csnapshot : snapshot.getChildren()){
                                                String num2 = csnapshot.getKey();
                                                switch (num2){
                                                    case "maxcount":
                                                        check = Long.parseLong(csnapshot.getValue().toString());
                                                        break;
                                                    default:
                                                        break;
                                                }
                                            }
                                            break;
                                        case "users":
                                            now = snapshot.getChildrenCount();
                                            break;
                                        default:
                                            break;
                                    }
                                }
                                if(now<check){
                                    FirebaseDatabase.getInstance().getReference().child("groups").child(gid).child("users").addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                            for(DataSnapshot snapshot:dataSnapshot.getChildren()){
                                                userlist.add(snapshot.getKey());
                                            }
                                            if(userlist.contains(myUid)){
                                                Toast.makeText(ChatActivity.this, "이미 참여한 스터디입니다.", Toast.LENGTH_SHORT).show();
                                            }
                                            else {
                                                Intent intent = new Intent(ChatActivity.this, sample_activity_1.class);
                                                FirebaseDatabase.getInstance().getReference().child("Users").child(myUid).child("GroupRooms").child(gid).setValue(true);
                                                FirebaseDatabase.getInstance().getReference().child("groups").child(gid).child("users").child(myUid).setValue(true);
                                                FirebaseDatabase.getInstance().getReference().child("rooms").child(RoomID).child("groups").child(gid).child("users").child(myUid).setValue(true);
                                                FirebaseDatabase.getInstance().getReference().child("Users").child(myUid).child("gotolist").setValue(1);
                                                startActivity(intent);
                                                finish();
                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {

                                        }
                                    });
                                }
                                else if(check == 0){
                                    Toast.makeText(ChatActivity.this, "스터디가 더는 존재하지 않습니다.", Toast.LENGTH_SHORT).show();
                                }

                                else{
                                    Log.d("TTTTAG",String.valueOf(check));
                                    Toast.makeText(ChatActivity.this, "인원이 꽉 차 더는 참여할 수 없습니다.", Toast.LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                    }
                }
            });

            long unixTime = (long) comments.get(position).timestamp;
            Date date = new Date(unixTime);
            simpleDateFormat.setTimeZone(TimeZone.getTimeZone("Asia/Seoul"));
            String time = (String) simpleDateFormat.format(date);
            messageViewHolder.textView_timestamp.setText(time);
        }

        void setReadCounter(final int position, final TextView textView) {

            if(peopleCount == 0) {
                FirebaseDatabase.getInstance().getReference().child("rooms").child(RoomID).child("users").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        Map<String, Boolean> users = (Map<String, Boolean>) dataSnapshot.getValue();
                        peopleCount = users.size();
                        int count = peopleCount - comments.get(position).readUsers.size();

                        if (count > 0) {
                            textView.setVisibility(View.VISIBLE);
                            textView.setText(String.valueOf(count));
                        } else {
                            textView.setVisibility(View.GONE);
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
                    textView.setVisibility(View.GONE);
                }
            }
        }

        @Override
        public int getItemCount() {
            return comments.size();
        }

        private class ChatViewHolder extends RecyclerView.ViewHolder {

            public TextView textView_message;
            public TextView textView_name;
            public ImageView imageView_profile;
            public LinearLayout linearLayout_destination;
            public LinearLayout linearLayout_main;
            public LinearLayout linearLayout_count;
            public TextView textView_timestamp;
            public TextView textView_readCounter_left;
            public TextView textView_readCounter_right;

            public ChatViewHolder(View view) {
                super(view);

                textView_message = view.findViewById(R.id.messageItem_textView_message);
                textView_name = view.findViewById(R.id.messageItem_textView_name);
                textView_timestamp = view.findViewById(R.id.messageItem_textView_timestamp);
                imageView_profile = view.findViewById(R.id.messageItem_imageView_profile);
                linearLayout_destination = view.findViewById(R.id.messageItem_linearLayout_destination);
                linearLayout_main = view.findViewById(R.id.messageItem_linearLayout_main);
                linearLayout_count = view.findViewById(R.id.messageItem_linearLayout_count);
                textView_readCounter_left = view.findViewById(R.id.messageItem_textView_readCounter_left);
                textView_readCounter_right = view.findViewById(R.id.messageItem_textView_readCounter_right);

            }

        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_chat_room_option, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch(item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;

            case R.id.chatRoomOption_menu:
                Animation animation_show = AnimationUtils.loadAnimation(this, R.anim.show_sidebar);
                frameLayout_sidebar_main.startAnimation(animation_show);
                frameLayout_sidebar_blank.setVisibility(View.VISIBLE);
                frameLayout_sidebar_cover.setVisibility(View.VISIBLE);
                isSideBarOn = true;
                editText.setEnabled(false);
                return true;

        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {

        if(isSideBarOn) {

            closeSideBar();

        } else {

            finish();
            overridePendingTransition(R.anim.from_left, R.anim.to_right);

        }

    }

    public void closeSideBar() {

        Animation animation_hide = AnimationUtils.loadAnimation(this, R.anim.hide_sidebar);
        frameLayout_sidebar_main.startAnimation(animation_hide);
        animation_hide.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

                frameLayout_sidebar_blank.setVisibility(View.GONE);

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                frameLayout_sidebar_cover.setVisibility(View.GONE);
                isSideBarOn = false;
                editText.setEnabled(true);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }

}
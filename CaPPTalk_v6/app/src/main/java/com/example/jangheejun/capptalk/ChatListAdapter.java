package com.example.jangheejun.capptalk;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

public class ChatListAdapter extends BaseAdapter {
    private LayoutInflater inflater;
    private List<ChatList> list = new ArrayList<ChatList>();
    private int layout;
    private String tempLasMsg;

    public ChatListAdapter(/*Context context, int layout, List<ChatList> list*/){
        /*this.inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.list=list;
        this.layout = layout;
        */

    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position).getName();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public void listclear(){
        list.clear();
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        final Context context = parent.getContext();

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.item_chat, parent, false);
        }
        /*
        if(convertView==null){
            convertView=inflater.inflate(layout,parent,false);
        }
        */

        ChatList chatlist = list.get(position);

        TextView count = (TextView) convertView.findViewById(R.id.chat_count);
        count.setText(String.valueOf(chatlist.getCount()));

        TextView name = (TextView)convertView.findViewById(R.id.chat_text);
        name.setText(chatlist.getName());

        TextView lastmessage = (TextView)convertView.findViewById(R.id.itemchat_lastmsg);
        tempLasMsg = chatlist.getLastmessage();
        if(!("null".equals(String.valueOf(tempLasMsg)))) {
            if (tempLasMsg.length() > 20) {
                tempLasMsg = tempLasMsg.substring(0, 20);
                tempLasMsg = tempLasMsg.concat("...");
            }
            lastmessage.setText(tempLasMsg);
        }

        ImageView image=(ImageView)convertView.findViewById(R.id.imageView2);
        String tempImgName= chatlist.getImage();
        int id = context.getResources().getIdentifier(tempImgName, "drawable", context.getApplicationContext().getPackageName());
        Drawable drawable = context.getResources().getDrawable(id);
        Picasso.with(context)
                .load(id).resize(100,100).centerCrop().placeholder(R.drawable.placeholder).into(image);

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy.MM.dd HH:mm");
        long unixTime = chatlist.getTimestamp();
        Date date = new Date(unixTime);
        simpleDateFormat.setTimeZone(TimeZone.getTimeZone("Asia/Seoul"));
        TextView timestamp = (TextView)convertView.findViewById(R.id.chatItem_textView_timestamp);
        timestamp.setText(simpleDateFormat.format(date));

        return convertView;
    }

    public void addItem(long count, String name, String lastmessage, long timestamp,String img){
        ChatList chatList = new ChatList();

        chatList.setCount(count);
        chatList.setName(name);
        chatList.setLastMessage(lastmessage);
        chatList.setTimestamp(timestamp);
        chatList.setImage(img);
        list.add(chatList);
    }

}

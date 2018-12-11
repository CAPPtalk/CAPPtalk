package com.example.jangheejun.capptalk;

public class ChatList {
    private long count;
    private String name;
    private String lastmessage;
    private long timestamp;
    private String img;

    public void setCount(long count) {this.count = count;}
    public void setName(String name) {this.name = name;}
    public void setLastMessage(String lastmessage) {this.lastmessage = lastmessage;}
    public void setTimestamp(long timestamp){this.timestamp = timestamp;}
    public void setImage(String img){this.img=img;}

    public long getCount(){return count;}
    public String getName(){return name;}
    public String getLastmessage(){return lastmessage;}
    public long getTimestamp(){return timestamp;}
    public String getImage(){return img;}

}



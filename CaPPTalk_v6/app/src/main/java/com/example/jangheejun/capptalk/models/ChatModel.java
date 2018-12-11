package com.example.jangheejun.capptalk.models;

import java.util.HashMap;
import java.util.Map;

public class ChatModel {
    // users in chatting room
    public Map<String, Boolean> users = new HashMap<>();
    // contents in chatting room
    public Map<String, Comment> comments = new HashMap<>();

    public static class Comment {
        public String uid;
        public String message;
        public Object timestamp;
        public Map<String, Object> readUsers = new HashMap<>();
        public String qid = null;
        public String gid = null;
    }
}

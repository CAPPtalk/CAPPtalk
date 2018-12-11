package com.example.jangheejun.capptalk.models;

public class QuestionModel {

    public String uid;
    public String Title;
    public String content;
    public String selected;
    public String RoomID;

    public QuestionModel() {
    }

    public QuestionModel(String uid, String Title, String content, String RoomID) {

        this.uid = uid;
        this.Title = Title;
        this.content = content;
        this.selected = "";
        this.RoomID = RoomID;

    }

}

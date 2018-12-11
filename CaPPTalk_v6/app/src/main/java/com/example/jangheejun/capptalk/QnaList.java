package com.example.jangheejun.capptalk;

import java.util.HashMap;
import java.util.Map;


public class QnaList {
    private String content;
    private String uid;
    private int isComment;
    private String accept;
    private String title;
    private String aid;
    private int good;
    private int bad;
    private String url;
    private String name;

    public void setContent(String content) {
        this.content = content;
    }
    public void setUid(String uid) {
        this.uid = uid;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    public void setAccept(String accept) {
        this.accept = accept;
    }
    public void setIsComment(int comment){this.isComment=comment;}
    public void setGood(int good){this.good=good;}
    public void setBad(int bad){this.bad=bad;}
    public void setAid(String aid) {
        this.aid = aid;
    }
    public void setUrl(String url) {this.url = url;}
    public void setName(String name) {this.name = name;}


    public String getContent(){
        return content;
    }
    public String getUid(){
        return uid;
    }
    public String getTitle(){
        return title;
    }
    public String getAccept(){
        return accept;
    }
    public int getIsComment(){return isComment;}
    public String getAid() {
        return aid;
    }
    public int getGood(){return good;}
    public int getBad(){return bad;}
    public String getUrl() {
        return url;
    }
    public String getName() {
        return name;
    }
}

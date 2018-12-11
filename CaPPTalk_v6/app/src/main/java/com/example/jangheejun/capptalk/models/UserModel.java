package com.example.jangheejun.capptalk.models;

import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
public class UserModel {

    public String email;
    public String password;
    public String userName;
    public boolean firstLogin;
    public int points;
    public String url;

    public UserModel() {
        // Default constructor required for calls to DataSnapshot.getValue(UserModel.class)
    }

    public UserModel(String email, String password, String name) {
        this.email = email;
        this.password = password;
        this.userName = name;
        this.firstLogin = true;
        this.points = 3;
        this.url = null;
    }
}
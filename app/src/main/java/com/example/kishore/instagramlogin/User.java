package com.example.kishore.instagramlogin;

/**
 * Created by Kishore on 12/19/2016.
 */
public class User {

    String userid;
    String user;
    String accesstoken;
    String name;
    String email;

    public User(String email, String name) {
        this.email = email;
        this.name = name;
    }

    public User() {
    }

    public User(String userid, String user, String accesstoken) {
        this.userid = userid;
        this.user = user;
        this.accesstoken = accesstoken;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getAccesstoken() {
        return accesstoken;
    }

    public void setAccesstoken(String accesstoken) {
        this.accesstoken = accesstoken;
    }
}

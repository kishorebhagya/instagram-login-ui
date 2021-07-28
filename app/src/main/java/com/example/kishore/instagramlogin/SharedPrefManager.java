package com.example.kishore.instagramlogin;

import android.content.Context;
import android.content.SharedPreferences;


public class SharedPrefManager {

    private Context context;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    private static final String SHARED_PREF = "Instagramlogin";
    private static final String KEY_IS_USER_ADDED = "isuserlogin";
    private static final String PROFILE_PIC = "profilepic";
    private static final String PROFILE_INFO = "profileinfo";
    private static final String USERNAME = "username";
    private static final String KEY_FIREBASE = "firebase";
    private static final String ADD_USER = "users";

    public SharedPrefManager(Context context){
        this.context = context;
        sharedPreferences = context.getSharedPreferences(SHARED_PREF,Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    public boolean addUser(Boolean user){
        editor.putBoolean(KEY_IS_USER_ADDED,user);
        editor.apply();
        return true;
    }

    public void imageurl(String img){
        editor.putString(PROFILE_PIC, img);
        editor.commit();
    }

    public void details(String info){
        editor.putString(PROFILE_INFO, info);
        editor.commit();
    }

    public void addName(String name){
        editor.putString(USERNAME, name);
        editor.commit();
    }

    public void addUser(String name){
        editor.putString(ADD_USER, name);
        editor.commit();
    }

    public boolean addfirebase(Boolean user){
        editor.putBoolean(KEY_FIREBASE, user);
        editor.apply();
        return true;
    }

    public String getUser(){
        return sharedPreferences.getString(ADD_USER,"");
    }

    public String getUsername(){
        return sharedPreferences.getString(USERNAME,"");
    }

    public String getProfileInfo(){
        return sharedPreferences.getString(PROFILE_INFO,"");
    }

    public String getImage(){
        return sharedPreferences.getString(PROFILE_PIC,"");
    }

    public boolean isUserAdded(){
        return sharedPreferences.getBoolean(KEY_IS_USER_ADDED, false);
    }

    public boolean isUserfirst(){
        return sharedPreferences.getBoolean(KEY_FIREBASE, false);
    }
}

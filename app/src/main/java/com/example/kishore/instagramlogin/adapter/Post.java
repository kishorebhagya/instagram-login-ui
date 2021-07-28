package com.example.kishore.instagramlogin.adapter;

import com.google.firebase.database.Exclude;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Kishore on 12/22/2016.
 */

public class Post {

    //public String uid;
    public int starCount = 0;
    public Map<String, Boolean> stars = new HashMap<>();

    public Post() {

    }

    // [START post_to_map]
    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("starCount", starCount);
        result.put("stars", stars);

        return result;
    }
    // [END post_to_map]
}

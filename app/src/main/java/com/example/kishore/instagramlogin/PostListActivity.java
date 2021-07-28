package com.example.kishore.instagramlogin;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;

import com.example.kishore.instagramlogin.adapter.Post;
import com.example.kishore.instagramlogin.adapter.PostAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class PostListActivity extends AppCompatActivity {

    ListView listView;
    PostAdapter postAdapter;
    ArrayList<String> data = new ArrayList<>();
    ArrayList<String> img = new ArrayList<>();

    private SharedPrefManager sharedPrefManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_list);

        sharedPrefManager = new SharedPrefManager(this);

        listView = (ListView) findViewById(R.id.rv);


        data.add("Post 1");
        data.add("Post 2");
        data.add("Post 3");
        data.add("Post 4");
        data.add("Post 5");

        img.add("https://images.unsplash.com/photo-1463569643904-4fbae71ed917?dpr=1&auto=format&fit=crop&w=1500&h=1000&q=80&cs=tinysrgb&crop=");
        img.add("https://images.unsplash.com/photo-1450101499163-c8848c66ca85?dpr=1&auto=format&fit=crop&w=1500&h=1001&q=80&cs=tinysrgb&crop=");
        img.add("https://images.unsplash.com/photo-1474575981580-1ec7944df3b2?dpr=1&auto=format&fit=crop&w=1500&h=934&q=80&cs=tinysrgb&crop=");
        img.add("https://images.unsplash.com/photo-1458917816469-9499c76688e0?dpr=1&auto=format&fit=crop&w=1500&h=1000&q=80&cs=tinysrgb&crop=");
        img.add("https://images.unsplash.com/photo-1468778809102-ff4948dbba0d?dpr=1&auto=format&fit=crop&w=1500&h=1000&q=80&cs=tinysrgb&crop=");


        postAdapter = new PostAdapter(getApplicationContext(),data,img);
        listView.setAdapter(postAdapter);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int i = item.getItemId();
        if (i == R.id.action_logout) {
            sharedPrefManager.addUser("");
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(this, MainActivity.class));
            finish();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }


}

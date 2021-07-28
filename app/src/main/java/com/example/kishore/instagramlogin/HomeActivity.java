package com.example.kishore.instagramlogin;

import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.kishore.instagramlogin.shapeimageview.ShapeImageView;
import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class HomeActivity extends AppCompatActivity {


    private static final String TAG = MainActivity.class.getSimpleName();

    ImageView profilepic;
    Button b1, b2;
    String id, ln, imgurl;
    String imgpath;
    Dialog details_dialog;
    ShareDialog shareDialog;
    TextView details_txt, name;
    String fn, userid, accesstoken;
    private String uniqueid;

    private SharedPrefManager sharedPrefManager;
    private DatabaseReference mFirebaseDatabase;
    private FirebaseDatabase mFirebaseInstance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.activity_home);
        b1 = (Button) findViewById(R.id.detail);
        b2 = (Button) findViewById(R.id.share);
        profilepic = (ShapeImageView) findViewById(R.id.profilepic);
        shareDialog = new ShareDialog(this);
        details_dialog = new Dialog(this);
        details_dialog.setContentView(R.layout.dialog_details);
        details_dialog.setTitle("Details");
        details_txt = (TextView) details_dialog.findViewById(R.id.details);
        name = (TextView) findViewById(R.id.name);

        mFirebaseInstance = FirebaseDatabase.getInstance();

        // get reference to 'users' node
        mFirebaseDatabase = mFirebaseInstance.getReference("fb_users");

        sharedPrefManager = new SharedPrefManager(this);

       /* Intent intent = getIntent();
        if (null != intent) {

            id=intent.getStringExtra("profile id");
            fn=intent.getStringExtra("firstname");
            ln=intent.getStringExtra("Lastname");
            imgurl=intent.getStringExtra("imgurl");

        }*/
        Intent intent = getIntent();
        if (null != intent) {
            id = intent.getStringExtra("name");
            imgurl = intent.getStringExtra("imgurl");
            fn = intent.getStringExtra("firstname");
            userid = intent.getStringExtra("userid");
            accesstoken = intent.getStringExtra("accesstoken");
        }
        Log.e("Info", id + "");
        // Log.e("ID:-",id+"");
        Log.e("Name:-", fn + "");
        Log.e("ImageUrl:-", imgurl + "");


        imgpath = MyApplication.getInstance().getSharedPrefManager().getImage();
        Log.e("path:--", imgpath + "");

        if (imgurl != null)
            Picasso.with(getApplicationContext()).load(imgurl).into(profilepic);
        else
            Picasso.with(getApplicationContext()).load(imgpath).into(profilepic);

        if (fn != null)
            name.setText(fn);
        else
            name.setText(MyApplication.getInstance().getSharedPrefManager().getUsername());

        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (id != null)
                    details_txt.setText(Html.fromHtml(id));
                else
                    details_txt.setText(Html.fromHtml(MyApplication.getInstance().getSharedPrefManager().getProfileInfo()));
                details_dialog.show();
            }
        });

        b2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ShareDialog.canShow(ShareLinkContent.class)) {
                    ShareLinkContent linkContent = new ShareLinkContent.Builder()
                            .setContentTitle("Hello Facebook")
                            .setContentDescription(
                                    "The 'Hello Facebook' sample  showcases simple from Kishore Bhagya")
                            .setContentUrl(Uri.parse("http://www.google.com"))
                            .build();

                    shareDialog.show(linkContent);
                }

                    // Check for already existed userId
                if (TextUtils.isEmpty(uniqueid)) {
                    saveuser(userid, fn, accesstoken);
                } else {
                    updateUser(userid, fn, accesstoken);
                }


            }
        });






    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int i = item.getItemId();
        if (i == R.id.action_logout) {
            sharedPrefManager.addUser(false);
            sharedPrefManager.imageurl("");
            sharedPrefManager.details("");
            sharedPrefManager.addName("");
            LoginManager.getInstance().logOut();
            Intent out = new Intent(HomeActivity.this, MainActivity.class);
            startActivity(out);
            finish();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    private void saveuser(String id, String name, String access) {
        // TODO
        // In real apps this userId should be fetched
        // by implementing firebase auth
        if (TextUtils.isEmpty(uniqueid)) {
            uniqueid = mFirebaseDatabase.push().getKey();
        }

        User user = new User(id, name, access);

        mFirebaseDatabase.child(uniqueid).setValue(user);

        addUserChangeListener();
    }

    /**
     * User data change listener
     */
    private void addUserChangeListener() {
        // User data change listener
        mFirebaseDatabase.child(uniqueid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);

                // Check for null
                if (user == null) {
                    Log.e(TAG, "User data is null!");
                    return;
                }

                Log.e(TAG, "User data is changed!" + user.userid + ", " + user.user + ", " + user.accesstoken);

            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.e(TAG, "Failed to read user", error.toException());
            }
        });
    }

    private void updateUser(String id, String name, String access) {
        // updating the user via child nodes
        if (!TextUtils.isEmpty(id))
            mFirebaseDatabase.child(uniqueid).child("userid").setValue(id);

        if (!TextUtils.isEmpty(name))
            mFirebaseDatabase.child(uniqueid).child("user").setValue(name);

        if (!TextUtils.isEmpty(access))
            mFirebaseDatabase.child(uniqueid).child("accesstoken").setValue(access);
    }


}

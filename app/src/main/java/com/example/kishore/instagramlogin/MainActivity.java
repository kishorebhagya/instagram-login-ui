package com.example.kishore.instagramlogin;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.Html;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.kishore.instagramlogin.shapeimageview.ShapeImageView;
import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.Profile;
import com.facebook.ProfileTracker;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;

public class MainActivity extends AppCompatActivity {

    private GradientBackgroundPainter gradientBackgroundPainter;
    ShapeImageView siv;
    private TextView info;
    private EditText username, password;
    private Button login;
    private LoginButton loginButton;
    private CallbackManager callbackManager;
    private AccessTokenTracker accessTokenTracker;
    private ProfileTracker profileTracker;
    private String name, accesstoken, userid;
    private String uname, pass;
    private SharedPrefManager sharedPrefManager;
    private ProgressDialog mProgressDialog;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    //private FirebaseDatabase mFirebaseInstance;

    protected void facebookSDKInitialize() {
        FacebookSdk.sdkInitialize(getApplicationContext());
        callbackManager = CallbackManager.Factory.create();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        facebookSDKInitialize();
        setContentView(R.layout.activity_main);

        //mFirebaseInstance = FirebaseDatabase.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();

        siv = (ShapeImageView) findViewById(R.id.heart);
        info = (TextView) findViewById(R.id.info);
        username = (EditText) findViewById(R.id.username);
        password = (EditText) findViewById(R.id.password);
        login = (Button) findViewById(R.id.login);
        loginButton = (LoginButton) findViewById(R.id.login_button);

        sharedPrefManager = new SharedPrefManager(this);

        View backgroundImage = findViewById(R.id.root_view);

        if (sharedPrefManager.isUserAdded()) {
            startActivity(new Intent(this, HomeActivity.class));
            finish();
        }


        final int[] drawables = new int[4];
        drawables[0] = R.drawable.gradient_1;
        drawables[1] = R.drawable.gradient_2;
        drawables[2] = R.drawable.gradient_3;
        drawables[3] = R.drawable.gradient_4;

        gradientBackgroundPainter = new GradientBackgroundPainter(backgroundImage, drawables);
        gradientBackgroundPainter.start();

        siv.setImageResource(R.drawable.shapeimageview);

        loginButton.setReadPermissions(Arrays.asList("public_profile, email, user_birthday, user_friends"));

        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {

                Log.e("User ID:--", loginResult.getAccessToken().getUserId() + "");
                Log.e("Access Token:--", loginResult.getAccessToken().getToken() + "");

                userid = loginResult.getAccessToken().getUserId();
                accesstoken = loginResult.getAccessToken().getToken();

                GraphRequest request = GraphRequest.newMeRequest(
                        loginResult.getAccessToken(),
                        new GraphRequest.GraphJSONObjectCallback() {
                            @Override
                            public void onCompleted(JSONObject object, GraphResponse response) {
                                try {

                                JSONObject json = response.getJSONObject();
                                    if (json != null) {
                                        name = "<b>Name :</b> " + json.getString("name") + "<br><br><b>Email :</b> "
                                                + json.getString("email") + "<br><br><b>Profile link :</b> " + json.getString("link")
                                                + "<br><br><b>Profile Id :</b> " + json.getString("id") + "<br><br><b>Gender :</b> " + json.getString("gender");

                                        String imgurl = "https://graph.facebook.com/" + json.getString("id") + "/picture?type=large";
                                        String idname = json.getString("name");
                                        sharedPrefManager.addUser(true);
                                        sharedPrefManager.imageurl(imgurl);
                                        sharedPrefManager.details(name);
                                        sharedPrefManager.addName(idname);
                                        Intent i = new Intent(MainActivity.this, HomeActivity.class);
                                        i.putExtra("imgurl", imgurl);
                                        i.putExtra("firstname", idname);
                                        i.putExtra("name", name);
                                        i.putExtra("userid", userid);
                                        i.putExtra("accesstoken", accesstoken);
                                        startActivity(i);
                                        finish();

                                    }

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                            }
                        });


                Bundle parameters = new Bundle();
                parameters.putString("fields", "id,name,link,email,picture,gender,birthday");
                request.setParameters(parameters);
                request.executeAsync();

                /*AccessToken accessToken = loginResult.getAccessToken();
                Profile profile = Profile.getCurrentProfile();
                displayMessage(profile);
                accessTokenTracker.startTracking();
                info.setText("User ID: " + loginResult.getAccessToken().getUserId()
                                + "\n" + "Auth Token: " + loginResult.getAccessToken().getToken()

                );*/
            }

            @Override
            public void onCancel() {
                info.setText("Login attempt cancelled.");
            }

            @Override
            public void onError(FacebookException e) {
                info.setText("Login attempt failed.");
            }
        });

        /*accessTokenTracker = new AccessTokenTracker() {
            @Override
            protected void onCurrentAccessTokenChanged(AccessToken oldAccessToken,
                                                       AccessToken currentAccessToken) {
                if (currentAccessToken == null) {
                    //write your code here what to do when user logout
                    siv.setImageResource(R.drawable.shapeimageview);
                    info.setVisibility(View.GONE);
                    *//*info.setVisibility(View.GONE);*//*
                }
            }
        };*/

        /*profileTracker = new ProfileTracker() {
            @Override
            protected void onCurrentProfileChanged(Profile oldProfile, Profile newProfile) {
                displayMessage(newProfile);
            }
        };

        profileTracker.startTracking();
        accessTokenTracker.startTracking();*/


        password.addTextChangedListener(new MyTextWatcher(password));


        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signIn();
               /* if (!TextUtils.isEmpty(uname) && !TextUtils.isEmpty(pass))
                Toast.makeText(MainActivity.this, "Logging In", Toast.LENGTH_SHORT).show();*/
            }
        });


    }

    private void signIn() {
        Log.d("MainActivity", "signIn");
        if (!validateForm()) {
            return;
        }

        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setCancelable(false);
            mProgressDialog.setMessage("Loading...");
        }

        mProgressDialog.show();

        String email = username.getText().toString();
        String pass = password.getText().toString();

        mAuth.signInWithEmailAndPassword(email, pass)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d("MainActivity", "signIn:onComplete:" + task.isSuccessful());

                        if (mProgressDialog != null && mProgressDialog.isShowing()) {
                            mProgressDialog.dismiss();
                        }

                        if (task.isSuccessful()) {
                            onAuthSuccess(task.getResult().getUser());
                        } else {
                            Toast.makeText(MainActivity.this, "Sign In Failed",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void onAuthSuccess(FirebaseUser user) {
        String username = usernameFromEmail(user.getEmail());

        sharedPrefManager.addUser(username);

        // Write new user
        writeNewUser(user.getUid(), username, user.getEmail());

        // Go to MainActivity
        startActivity(new Intent(MainActivity.this, PostListActivity.class));
        finish();
    }

    private String usernameFromEmail(String email) {
        if (email.contains("@")) {
            return email.split("@")[0];
        } else {
            return email;
        }
    }

    // [START basic_write]
    private void writeNewUser(String userId, String name, String email) {
        User user = new User(email, name);

        mDatabase.child("users").child(userId).setValue(user);
    }
    // [END basic_write]

    private boolean validateForm() {
        boolean result = true;
        if (TextUtils.isEmpty(username.getText().toString())) {
            username.setError("Required");
            result = false;
        } else {
            username.setError(null);
        }

        if (TextUtils.isEmpty(password.getText().toString())) {
            password.setError("Required");
            result = false;
        } else {
            password.setError(null);
        }

        return result;
    }

    @Override
    public void onStart() {
        super.onStart();

        // Check auth on Activity start
        if (mAuth.getCurrentUser() != null) {
            onAuthSuccess(mAuth.getCurrentUser());
        }
    }

    private class MyTextWatcher implements TextWatcher {

        private View view;

        private MyTextWatcher(View view) {
            this.view = view;
        }

        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        public void afterTextChanged(Editable editable) {
            switch (view.getId()) {
                case R.id.password:
                    uname = username.getText().toString().trim();
                    pass = password.getText().toString().trim();

                    if (!TextUtils.isEmpty(uname) && !TextUtils.isEmpty(pass)) {
                        login.setTextColor(getResources().getColor(R.color.hintcolor1));
                    }else {
                        login.setTextColor(getResources().getColor(R.color.hintcolor));
                    }

                    break;
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        gradientBackgroundPainter.stop();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);

        Log.e("data:-->>", data.toString());
    }

    private void displayMessage(Profile profile) {
        if (profile != null) {
            info.setText(profile.getName());
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        AppEventsLogger.activateApp(this);

    }

    @Override
    protected void onPause() {
        super.onPause();
        AppEventsLogger.deactivateApp(this);
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent KEvent) {
        int keyaction = KEvent.getAction();

        if (keyaction == KeyEvent.ACTION_DOWN) {
            int keycode = KEvent.getKeyCode();
            int keyunicode = KEvent.getUnicodeChar(KEvent.getMetaState());
            char character = (char) keyunicode;

            System.out.println("DEBUG MESSAGE KEY=" + character + " KEYCODE=" + keycode);
        }


        return super.dispatchKeyEvent(KEvent);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 1) {
            finish();
            int keycode = event.getKeyCode();
            int keyunicode = event.getUnicodeChar(event.getMetaState());
            char character = (char) keyunicode;

            System.out.println("DEBUG MESSAGE KEY=" + character + " KEYCODE=" + keycode);
            return true;
        }

        return super.onKeyDown(keyCode, event);
    }

}

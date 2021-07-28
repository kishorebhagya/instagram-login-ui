
package com.example.kishore.instagramlogin.adapter;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.kishore.instagramlogin.CommentActivity;
import com.example.kishore.instagramlogin.R;
import com.example.kishore.instagramlogin.SharedPrefManager;
import com.example.kishore.instagramlogin.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;



public class PostAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<String> sub=new ArrayList<>();
    private ArrayList<String> img=new ArrayList<>();

    private DatabaseReference mDatabase;
    private Post post = new Post();
    private SharedPrefManager sharedPrefManager;



    public PostAdapter(Context a, ArrayList<String> d,ArrayList<String> b) {
        context = a;
        sub = d;
        img = b;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.custom_row, null);

            ViewHolder viewHolder = new ViewHolder(convertView, position);
            //holder = new ViewHolder();
            convertView.setTag(viewHolder);
        }
        final ViewHolder holder = (ViewHolder) convertView.getTag();


        mDatabase = FirebaseDatabase.getInstance().getReference();

        sharedPrefManager = new SharedPrefManager(context);

        mDatabase.child("posts").child(sub.get(position)).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Post user = dataSnapshot.getValue(Post.class);

                // Check for null
                if (user == null) {
                    Log.e("Post List Adapter", "User data is null!");
                    return;
                }

                Log.e("Post List Adapter", "User data is changed!" + user.stars.containsKey(getUid()) + ", " + user.starCount);
                //Toast.makeText(context, "Start Count==>"+user.starCount, Toast.LENGTH_SHORT).show();

                holder.textView1.setText(String.valueOf(user.starCount));

                if (user.stars.containsKey(getUid())) {
                    holder.iv.setImageResource(R.drawable.heart1);
                } else {
                    holder.iv.setImageResource(R.drawable.heart);
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.e("Post List Adapter", "Failed to read user", error.toException());
            }
        });

        final DatabaseReference globalPostRef = mDatabase.child("posts").child(sub.get(position));

        holder.textView.setText(sub.get(position));

        Picasso.with(context).load(img.get(position)).placeholder(R.drawable.placeholder).into(holder.iv2);


        holder.iv3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent comment = new Intent(context, CommentActivity.class);
                comment.putExtra("post",sub.get(position));
                comment.putExtra("img",img.get(position));
                comment.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(comment);
            }
        });

        holder.iv4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(context, "Send", Toast.LENGTH_SHORT).show();
            }
        });

        holder.iv5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(context, "Option Menu", Toast.LENGTH_SHORT).show();
            }
        });


        if(!sharedPrefManager.isUserfirst()) {

            Map<String, Object> postValues = post.toMap();

            Map<String, Object> childUpdates = new HashMap<>();

            childUpdates.put("/posts/" + sub.get(position), postValues);

            mDatabase.updateChildren(childUpdates);
        }

        holder.iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {

                sharedPrefManager.addfirebase(true);

                onStarClicked(globalPostRef);

                mDatabase.child("posts").child(sub.get(position)).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Post user = dataSnapshot.getValue(Post.class);

                        // Check for null
                        if (user == null) {
                            Log.e("Post List Adapter", "User data is null!");
                            return;
                        }

                        Log.e("Post List Adapter", "User data is changed!" + user.stars.containsKey(getUid()) + ", " + user.starCount);
                        //Toast.makeText(context, "Start Count==>"+user.starCount, Toast.LENGTH_SHORT).show();

                       holder.textView1.setText(String.valueOf(user.starCount));

                        if (user.stars.containsKey(getUid())) {
                            ((ImageView)view).setImageResource(R.drawable.heart1);
                        } else {
                            ((ImageView)view).setImageResource(R.drawable.heart);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError error) {
                        // Failed to read value
                        Log.e("Post List Adapter", "Failed to read user", error.toException());
                    }
                });

            }
        });

        return convertView;
    }

    // [START post_stars_transaction]
    public void onStarClicked(DatabaseReference postRef) {
        postRef.runTransaction(new Transaction.Handler() {
            @Override
            public Transaction.Result doTransaction(MutableData mutableData) {
                Post p = mutableData.getValue(Post.class);
                if (p == null) {
                    return Transaction.success(mutableData);
                }

                if (p.stars.containsKey(getUid())) {
                    // Unstar the post and remove self from stars
                    p.starCount = p.starCount - 1;
                    p.stars.remove(getUid());
                } else {
                    // Star the post and add self to stars
                    p.starCount = p.starCount + 1;
                    p.stars.put(getUid(), true);
                }

                // Set value and report transaction success
                mutableData.setValue(p);
                return Transaction.success(mutableData);
            }

            @Override
            public void onComplete(DatabaseError databaseError, boolean b,
                                   DataSnapshot dataSnapshot) {
                // Transaction completed
                Log.d("Post Adapter:--", "postTransaction:onComplete:" + databaseError);
            }
        });
    }
    // [END post_stars_transaction]


    private String getUid() {
        return FirebaseAuth.getInstance().getCurrentUser().getUid();
    }


    public static class ViewHolder {


        TextView textView,textView1;
        ImageView iv,iv2,iv3,iv4,iv5;


        public ViewHolder(View v, final int position){

            this.textView = (TextView) v.findViewById(R.id.post);
            this.textView1 = (TextView) v.findViewById(R.id.starcount);
            this.iv = (ImageView) v.findViewById(R.id.star);
            this.iv2 = (ImageView) v.findViewById(R.id.images);
            this.iv3 = (ImageView) v.findViewById(R.id.comment);
            this.iv4 = (ImageView) v.findViewById(R.id.reply);
            this.iv5 = (ImageView) v.findViewById(R.id.option);
        }


    }
    @Override
    public int getCount() {
        return sub.size();
    }
    @Override
    public Object getItem(int position) {
        return position;
    }
    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
    }
}




package com.example.a15017470.solemates;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

public class MyPostsActivity extends AppCompatActivity {

    RecyclerView blogList;
    DatabaseReference database;
    DatabaseReference databaseUsers;
    DatabaseReference databaseLike;
    DatabaseReference databaseCurrentUser;
    FirebaseAuth auth;
    FirebaseAuth.AuthStateListener authListener;
    Query queryCurrentUser;

    boolean processLike = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_posts);


        auth = FirebaseAuth.getInstance();
        authListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if (firebaseAuth.getCurrentUser() == null) {
                    Intent loginIntent = new Intent(MyPostsActivity.this, LoginActivity.class);
                    loginIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(loginIntent);
                }
            }
        };

        database = FirebaseDatabase.getInstance().getReference().child("Blog");
        databaseUsers = FirebaseDatabase.getInstance().getReference().child("Users");
        databaseLike = FirebaseDatabase.getInstance().getReference().child("Likes");

        String currentUserId = auth.getCurrentUser().getUid();

        queryCurrentUser = database.orderByChild("uid").equalTo(currentUserId);

        database.keepSynced(true);
        databaseUsers.keepSynced(true);

        blogList = (RecyclerView) findViewById(R.id.my_blog_list);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);

        blogList.setHasFixedSize(true);
        blogList.setLayoutManager(new LinearLayoutManager(this));

        checkUserExist();
    }

    @Override
    protected void onStart() {
        super.onStart();

        auth.addAuthStateListener(authListener);

        FirebaseRecyclerAdapter<Blog, MainActivity.BlogViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Blog, MainActivity.BlogViewHolder>(
                Blog.class,
                R.layout.blog_row,
                MainActivity.BlogViewHolder.class,
                queryCurrentUser
        ) {
            @Override
            protected void populateViewHolder(MainActivity.BlogViewHolder viewHolder, Blog model, int position) {

                final String post_key = getRef(position).getKey();

                viewHolder.setBrand(model.getBrand());
                viewHolder.setModel(model.getModel());
                viewHolder.setImage(getApplicationContext(), model.getImage());
                viewHolder.setUsername(model.getUsername());
                viewHolder.setLikeBtn(post_key);

                viewHolder.view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
//                        Toast.makeText(MainActivity.this, post_key, Toast.LENGTH_SHORT).show();
                        Intent singleBlogIntent = new Intent(MyPostsActivity.this, BlogSingleActivity.class);
                        singleBlogIntent.putExtra("blog_id", post_key);
                        startActivity(singleBlogIntent);
                    }
                });

                viewHolder.likeBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        processLike = true;

                        databaseLike.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if (processLike) {
                                    if (dataSnapshot.child(post_key).hasChild(auth.getCurrentUser().getUid())) {
                                        databaseLike.child(post_key).child(auth.getCurrentUser().getUid()).removeValue();
                                        processLike = false;
                                    } else {
                                        databaseLike.child(post_key).child(auth.getCurrentUser().getUid()).setValue("RandomValue");
                                        processLike = false;
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                    }
                });
            }
        };

        blogList.setAdapter(firebaseRecyclerAdapter);
    }

    private void checkUserExist() {
        if (auth.getCurrentUser() != null) {
            final String user_id = auth.getCurrentUser().getUid();
            databaseUsers.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (!dataSnapshot.hasChild(user_id)) {
                        Intent setupIntent = new Intent(MyPostsActivity.this, SetupActivity.class);
                        setupIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(setupIntent);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
    }

    public static class BlogViewHolder extends RecyclerView.ViewHolder {

        View view;
        ImageButton likeBtn;

        DatabaseReference databaseLike;
        FirebaseAuth auth;

        public BlogViewHolder(View itemView) {
            super(itemView);
            view = itemView;

            likeBtn = (ImageButton) view.findViewById(R.id.like_btn);

            databaseLike = FirebaseDatabase.getInstance().getReference().child("Likes");
            auth = FirebaseAuth.getInstance();

            databaseLike.keepSynced(true);
        }

        public void setLikeBtn(final String post_key) {
            databaseLike.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.child(post_key).hasChild(auth.getCurrentUser().getUid())) {
                        likeBtn.setImageResource(R.mipmap.ic_thumb_up_red_24dp);
                    } else {
                        likeBtn.setImageResource(R.mipmap.ic_thumb_up_gray_24dp);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }

        public void setBrand(String brand) {
            TextView post_brand = (TextView) view.findViewById(R.id.singlePostBrand);
            post_brand.setText(brand);
        }

        public void setModel(String model) {
            TextView post_model = (TextView) view.findViewById(R.id.post_model);
            post_model.setText(model);
        }

        public void setUsername(String username) {
            TextView post_username = (TextView) view.findViewById(R.id.post_username);
            post_username.setText("Posted by: " + username);
        }

        public void setImage(final Context ctx, final String image) {
            final ImageView post_image = (ImageView) view.findViewById(R.id.post_image);
            Picasso.with(ctx).load(image).into(post_image);

            Picasso.with(ctx).load(image).networkPolicy(NetworkPolicy.OFFLINE).into(post_image, new Callback() {
                @Override
                public void onSuccess() {

                }

                @Override
                public void onError() {
                    Picasso.with(ctx).load(image).into(post_image);
                }
            });
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_add) {
            startActivity(new Intent(MyPostsActivity.this, PostActivity.class));
        }

        if (item.getItemId() == R.id.action_logout) {
            logout();
        }

        if (item.getItemId() == R.id.action_myPosts) {
            startActivity(new Intent(MyPostsActivity.this, PostActivity.class));
        }

        return super.onOptionsItemSelected(item);
    }

    private void logout() {
        auth.signOut();
    }
}

package com.example.a15017470.solemates;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class BlogSingleActivity extends AppCompatActivity {

    String post_key = null;

    DatabaseReference database;
    FirebaseAuth auth;

    ImageView blogSingleImage;
    TextView blogSingleBrand;
    TextView blogSingleModel;
    TextView blogSingleUser;
    Button blogSingleRemoveBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blog_single);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        database = FirebaseDatabase.getInstance().getReference().child("Blog");
        auth = FirebaseAuth.getInstance();

        post_key = getIntent().getExtras().getString("blog_id");

        blogSingleImage = (ImageView) findViewById(R.id.singleBlogImage);
        blogSingleBrand = (TextView) findViewById(R.id.singlePostBrand);
        blogSingleModel = (TextView) findViewById(R.id.singlePostModel);
        blogSingleUser = (TextView) findViewById(R.id.singlePostUser);
        blogSingleRemoveBtn = (Button) findViewById(R.id.singleRemoveBtn);

        database.child(post_key).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String post_brand = (String) dataSnapshot.child("brand").getValue();
                String post_model = (String) dataSnapshot.child("model").getValue();
                String post_image = (String) dataSnapshot.child("image").getValue();
                String post_user = (String) dataSnapshot.child("username").getValue();
                String post_uid = (String) dataSnapshot.child("uid").getValue();

                blogSingleBrand.setText(post_brand);
                blogSingleModel.setText(post_model);
                blogSingleUser.setText(post_user);

                Picasso.with(BlogSingleActivity.this).load(post_image).into(blogSingleImage);

                if (auth.getCurrentUser().getUid().equals(post_uid)) {
                    blogSingleRemoveBtn.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        blogSingleRemoveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                database.child(post_key).removeValue();

                Intent mainIntent = new Intent(BlogSingleActivity.this, MainActivity.class);
                startActivity(mainIntent);
            }
        });
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }
}

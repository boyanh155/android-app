package com.k19.socialmediaapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.app.Notification;
import android.content.Intent;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.k19.socialmediaapp.Adapter.commentAdapter;
import com.k19.socialmediaapp.Models.User;
import com.k19.socialmediaapp.Models.commentModel;
import com.k19.socialmediaapp.Models.notificationModel;
import com.k19.socialmediaapp.Models.postModel;
import com.k19.socialmediaapp.databinding.ActivityCommentBinding;
import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class CommentActivity extends AppCompatActivity {
    ActivityCommentBinding binding;
    Intent intent;
    String postID;
    String postedBy;
    FirebaseDatabase database;
    FirebaseAuth auth;
    ArrayList<commentModel> list = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        binding = ActivityCommentBinding.inflate( getLayoutInflater() );
        setSupportActionBar( binding.toolbar2 );
        CommentActivity.this.setTitle( "Bình luận." );
        getSupportActionBar().setDisplayHomeAsUpEnabled( true  );
        setContentView( binding.getRoot() );
        intent=getIntent();
        postID=intent.getStringExtra( "postID" );
        postedBy=intent.getStringExtra( "postedBy" );
        database=FirebaseDatabase.getInstance();
        auth=FirebaseAuth.getInstance();
        database.getReference()
                .child( "post" )
                .child( postID ).addValueEventListener( new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        postModel post = snapshot.getValue(postModel.class);
                        Picasso.get()
                                .load( post.getPostImage() )
                                .placeholder( R.drawable.placehoder )
                                .into( binding.postImg );
                        binding.time.setText( post.getPostedAt() );
                        binding.storyPost.setText( post.getPostDescription() );
                        binding.love.setText( post.getPostLove()+"" );
                        binding.cmt.setText( post.getCommentCount()+"" );
                        String time =calculateTimeago(post.getPostedAt());
                        binding.time.setText(time );
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                } );
        database.getReference()
                .child( "User" )
                .child( postedBy ).addListenerForSingleValueEvent( new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        User user = snapshot.getValue(User.class);
                        Picasso.get()
                                .load( user.getProfileImage() )
                                .placeholder( R.drawable.placehoder )
                                .into( binding.profileImage );
                        binding.userName.setText( user.getName() );

                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                } );
        binding.commentPostBtn.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                commentModel comment = new commentModel();
                comment.setCommentBody(binding.commentET.getText().toString()  );
                Date date = new Date();
                SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
                 String strDate = formatter.format( date );

                comment.setCommentAt( strDate );
                comment.setCommentBy( FirebaseAuth.getInstance().getUid() );
                database.getReference()
                        .child( "post" )
                        .child( postID )
                        .child( "comments" )
                        .push()
                        .setValue( comment ).addOnSuccessListener( new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                database.getReference()
                                        .child( "post" )
                                        .child( postID )
                                        .child( "commentCount" ).addListenerForSingleValueEvent( new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                int commentCount = 0;
                                                if (snapshot.exists()){
                                                     commentCount=snapshot.getValue(Integer.class);
                                                }
                                                database.getReference()
                                                         .child( "post" )
                                                        .child( postID )
                                                        .child( "commentCount")
                                                        .setValue(commentCount+1).addOnSuccessListener( new OnSuccessListener<Void>() {
                                                            @Override
                                                            public void onSuccess(Void unused) {
                                                                Toast.makeText( CommentActivity.this,"Bạn đã bình luận",Toast.LENGTH_LONG ).show();
                                                                binding.commentET.setText( "" );
                                                                notificationModel notification =new notificationModel();
                                                                notification.setNotificationBy( FirebaseAuth.getInstance().getUid() );
                                                                FirebaseDatabase.getInstance().getReference()
                                                                        .child( "User" )
                                                                        .child( notification.getNotificationBy() ).addListenerForSingleValueEvent( new ValueEventListener() {
                                                                            @Override
                                                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                                                if (snapshot.exists()){
                                                                                    User u = snapshot.getValue(User.class);
                                                                                    notification.setNotification( "<b>"+u.getName()+"</b>"+" đã bình luận về bài viết của bạn" );
                                                                                    FirebaseDatabase.getInstance().getReference()
                                                                                            .child( "post" )
                                                                                            .child( postID).addListenerForSingleValueEvent( new ValueEventListener() {
                                                                                                @Override
                                                                                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                                                                    if (snapshot.exists()){
                                                                                                        postModel p = snapshot.getValue(postModel.class);
                                                                                                        FirebaseDatabase.getInstance().getReference()
                                                                                                                .child( "User" )
                                                                                                                .child( p.getPostedBy() )
                                                                                                                .child("notification" )
                                                                                                                .push()
                                                                                                                .setValue( notification );
                                                                                                    }
                                                                                                }
                                                                                                @Override
                                                                                                public void onCancelled(@NonNull DatabaseError error) {

                                                                                                }
                                                                                            } );
                                                                                }
                                                                            }

                                                                            @Override
                                                                            public void onCancelled(@NonNull DatabaseError error) {

                                                                            }
                                                                        } );

                                                            }
                                                        } );
                                            }
                                            @Override
                                            public void onCancelled(@NonNull DatabaseError error) {

                                            }
                                        } );
                            }
                        } );

            }
        } );
        commentAdapter adapter = new commentAdapter( this,list );
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        binding.commentRv.setLayoutManager( layoutManager );
        binding.commentRv.setAdapter( adapter );
        database.getReference()
                .child( "post" )
                .child( postID )
                .child( "comments" ).addValueEventListener( new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        list.clear();
                        for (DataSnapshot dataSnapshot:snapshot.getChildren()){
                            commentModel comment = dataSnapshot.getValue(commentModel.class);
                            list.add( comment );
                        }
                        adapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                } );


    }

    private String calculateTimeago(String postedAt) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");

        try {
            long time = sdf.parse(postedAt).getTime();
            long now = System.currentTimeMillis();
            CharSequence ago =
                    DateUtils.getRelativeTimeSpanString(time, now, DateUtils.MINUTE_IN_MILLIS);
            return ago +"";
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return  "";
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        finish();
        return super.onOptionsItemSelected( item );
    }
}
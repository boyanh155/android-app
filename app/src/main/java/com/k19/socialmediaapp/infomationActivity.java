package com.k19.socialmediaapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.k19.socialmediaapp.Adapter.highlightStoryAdapter;
import com.k19.socialmediaapp.Adapter.postAdapter;
import com.k19.socialmediaapp.Adapter.showFriendAdapter;
import com.k19.socialmediaapp.Models.User;
import com.k19.socialmediaapp.Models.UserStories;
import com.k19.socialmediaapp.Models.addfriend;
import com.k19.socialmediaapp.Models.highlightStory;
import com.k19.socialmediaapp.Models.notificationModel;
import com.k19.socialmediaapp.Models.postModel;
import com.k19.socialmediaapp.databinding.ActivityInfomationBinding;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Date;

public class infomationActivity extends AppCompatActivity {
    ActivityInfomationBinding binding;
    String userid;
    Intent intent;
    FirebaseDatabase database ;
    FirebaseAuth auth;
    ArrayList <highlightStory> list;
    FirebaseStorage storage;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        binding = ActivityInfomationBinding.inflate( getLayoutInflater() );
        setContentView( binding.getRoot() );
        intent=getIntent();
        userid=intent.getStringExtra( "userId" );
        database=FirebaseDatabase.getInstance();
        auth=FirebaseAuth.getInstance();
        getInfoUser();
        setListener();
    }
    private void getInfoUser(){
        database.getReference().child( "User" )
                .child( userid ).addListenerForSingleValueEvent( new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.exists()){
                            User user = snapshot.getValue(User.class);
                            Picasso.get().load(user.getCoverPhoto()).placeholder(R.drawable.placehoder)
                                    .into(binding.coverPhoto);
                            Picasso.get().load(user.getProfileImage()).placeholder(R.drawable.placehoder)
                                    .into(binding.profileImage);
                            binding.userName.setText(user.getName());
                            binding.profestion.setText(user.getProfesion());
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                } );

        //        recyclerView = view.findViewById(R.id.highlightStoryRV);
        list = new ArrayList<>();
        highlightStoryAdapter adapter = new highlightStoryAdapter(list,this);
        LinearLayoutManager linearLayoutManager =new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false);
//        recyclerView.setLayoutManager(linearLayoutManager);
        binding.highlightStoryRV.setLayoutManager(linearLayoutManager);
        binding.highlightStoryRV.setAdapter(adapter);
        database.getReference().child( "stories" ).child( userid ).addValueEventListener( new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    highlightStory story = new highlightStory();
                    story.setStoryBy( userid);
                    story.setStoryAt( snapshot.child( "postedBy" ).getValue(String.class) );
                    ArrayList<UserStories> stories=new ArrayList<>();
                    for (DataSnapshot dataSnapshot :snapshot.child( "userStories" ).getChildren()){
                        if (dataSnapshot.exists()){
                            UserStories userStories=dataSnapshot.getValue(UserStories.class);
                            stories.add( userStories );
                        }
                        story.setStories(  stories );
                    }
                    list.add( story );
                    adapter.notifyDataSetChanged();

                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        } );
        //get friend
        ArrayList<User> listFriend = new ArrayList<>();
        showFriendAdapter Adapter = new showFriendAdapter (this, listFriend);
        LinearLayoutManager layoutManager= new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false);
        binding.friendRV.setLayoutManager(layoutManager);
        binding.friendRV.setAdapter(Adapter);
        database.getReference()
                .child( "User" )
                .child( userid)
                .child( "friendList" )
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        for (DataSnapshot dataSnapshot :snapshot.getChildren()){
                            User user = dataSnapshot.getValue(User.class);
                            user.setUserID(dataSnapshot.getKey());
                            FirebaseDatabase.getInstance().getReference()
                                    .child( "User" )
                                    .child( user.getUserID() ).addListenerForSingleValueEvent( new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            for (DataSnapshot dataSnapshot1:snapshot.getChildren()){
                                                if (dataSnapshot1.getKey().equals( "name" )){
                                                    user.setName(dataSnapshot1.getValue(String.class));
                                                }
                                                else if (dataSnapshot1.getKey().equals( "profileImage" )){
                                                    user.setProfileImage( dataSnapshot1.getValue(String.class) );
                                                }
                                            }
                                            listFriend.add( user );
                                            Adapter.notifyDataSetChanged();
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
                });
        //get post
        ArrayList<postModel> post =new ArrayList<>();
        postAdapter postAdapter = new postAdapter( post, this );
        LinearLayoutManager linear = new LinearLayoutManager( this);
        binding.dashbordRV.addItemDecoration( new DividerItemDecoration( binding.dashbordRV.getContext() ,DividerItemDecoration.VERTICAL) );
        binding.dashbordRV.setNestedScrollingEnabled(false);
        binding.dashbordRV.setLayoutManager( linear );
        binding.dashbordRV.setAdapter( postAdapter );
        database.getReference()
                .child( "post" ).addValueEventListener( new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        post.clear();
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                            postModel p = dataSnapshot.getValue(postModel.class);
                            p.setPostID( dataSnapshot.getKey() );
                            if (p.getPostedBy().equals( userid )){
                                post.add( p );
                            }

                        }
                        postAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                } );
        //check friend
        checkWaFriend( userid );

    }
    private void checkWaFriend(String userid){
        database.getReference()
                .child( "User" )
                .child(auth.getUid() )
                .child( "friendList" ).addValueEventListener( new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot dataSnapshot:snapshot.getChildren()){
                            if (userid.equals( dataSnapshot.getKey() )){
                                binding.btnFriend.setVisibility( View.VISIBLE );
                                binding.btnAddFriend.setVisibility( View.GONE );
                            }
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                } );
    }
    private  void  setListener(){
        binding.imageBack.setOnClickListener( v -> onBackPressed() );
        binding.btnAddFriend.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                binding.btnAddFriend.setVisibility( View.GONE );
                binding.btnWait.setVisibility( View.VISIBLE );
                addfriend fr = new addfriend();
                fr.setAddBy( auth.getUid() );
                fr.setAddAt( new Date().getTime() );
                database.getReference()
                        .child( "User" )
                        .child( userid )
                        .child( "invitation" )
                        .child( auth.getUid() )
                        .setValue( fr );
                notificationModel notificationModel = new notificationModel();
                notificationModel.setNotificationBy( auth.getUid() );
                database.getReference()
                        .child( "User" )
                        .child( auth.getUid() ).addListenerForSingleValueEvent( new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if (snapshot.exists()){
                                    User u =snapshot.getValue(User.class);
                                    notificationModel.setNotification( "<b>"+u.getName()+"</b> " +"Đã gởi lời mời kết bạn đến bạn." );
                                    database.getReference()
                                            .child( "User" )
                                            .child( userid )
                                            .child( "notification" )
                                            .push()
                                            .setValue(  notificationModel);
                                }
                            }
                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        } );
            }
        } );
        binding.btnWait.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                binding.btnAddFriend.setVisibility( View.VISIBLE );
                binding.btnWait.setVisibility( View.GONE );
                database.getReference()
                        .child( "User" )
                        .child( userid )
                        .child( auth.getUid() )
                        .removeValue();
            }
        } );
        binding.btnMess.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(),chatActivity.class);
                intent.putExtra( "userId" ,userid);
                intent.setFlags( Intent.FLAG_ACTIVITY_CLEAR_TASK );
                startActivity( intent );
            }
        } );

    }
}
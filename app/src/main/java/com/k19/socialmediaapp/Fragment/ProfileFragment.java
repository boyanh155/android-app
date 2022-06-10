package com.k19.socialmediaapp.Fragment;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageException;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.k19.socialmediaapp.Adapter.StoryAdapter;
import com.k19.socialmediaapp.Adapter.firendAdapter;
import com.k19.socialmediaapp.Adapter.highlightStoryAdapter;
import com.k19.socialmediaapp.Adapter.postAdapter;
import com.k19.socialmediaapp.Adapter.showFriendAdapter;
import com.k19.socialmediaapp.Adapter.userAdapter;
import com.k19.socialmediaapp.Models.StoryModel;
import com.k19.socialmediaapp.Models.User;
import com.k19.socialmediaapp.Models.UserStories;
import com.k19.socialmediaapp.Models.friendModel;
import com.k19.socialmediaapp.Models.highlightStory;
import com.k19.socialmediaapp.Models.postModel;
import com.k19.socialmediaapp.R;
import com.k19.socialmediaapp.databinding.FragmentProfileBinding;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;


public class ProfileFragment extends Fragment {
    RecyclerView recyclerView,friendrv,friendrvLine2;
    ArrayList <highlightStory> list;
    ArrayList<friendModel> listFriend,listFriendLine2;
    FragmentProfileBinding binding;
    FirebaseAuth auth;
    FirebaseStorage storage;
    FirebaseDatabase database;

    ActivityResultLauncher<String> galleryLauncher;


    public ProfileFragment() {
        // Required empty public constructor

    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        auth = FirebaseAuth.getInstance();
        storage = FirebaseStorage.getInstance();
        database = FirebaseDatabase.getInstance();


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        binding=FragmentProfileBinding.inflate(inflater,container,false);
        database.getReference().child("User").child(auth.getUid())
                 .addListenerForSingleValueEvent(new ValueEventListener() {
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
        });
       //get friend
        ArrayList<User> listFriend = new ArrayList<>();
        showFriendAdapter Adapter = new showFriendAdapter (getContext(),listFriend);
        LinearLayoutManager layoutManager= new LinearLayoutManager(getContext(),LinearLayoutManager.HORIZONTAL,false);
        binding.friendRV.setLayoutManager(layoutManager);
        binding.friendRV.setAdapter(Adapter);
        database.getReference()
                .child( "User" )
                .child( auth.getUid())
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
        postAdapter postAdapter = new postAdapter( post, getContext() );
        LinearLayoutManager linear = new LinearLayoutManager( getContext());
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
                            if (p.getPostedBy().equals( auth.getUid() )){
                                post.add( p );
                            }

                        }
                        postAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                } );


//        recyclerView = view.findViewById(R.id.highlightStoryRV);
        list = new ArrayList<>();
        highlightStoryAdapter adapter = new highlightStoryAdapter(list,getContext());
        LinearLayoutManager linearLayoutManager =new LinearLayoutManager(getContext(),LinearLayoutManager.HORIZONTAL,false);
//        recyclerView.setLayoutManager(linearLayoutManager);
        binding.highlightStoryRV.setLayoutManager(linearLayoutManager);
        binding.highlightStoryRV.setAdapter(adapter);
        database.getReference().child( "stories" ).child( auth.getUid() ).addValueEventListener( new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    list.clear();
                    highlightStory story = new highlightStory();
                    story.setStoryBy( auth.getUid() );
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

        binding.btnchangepage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent ,11);

            }
        });
        binding.btnchangeavt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent= new Intent();
                intent.setAction(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent,22);
            }
        });
        binding.addPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent ,33);

            }
        });


        return  binding.getRoot();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==11){
            if (data!=null){
                Uri uri =data.getData();
                binding.coverPhoto.setImageURI(uri);
                final StorageReference reference = storage.getReference().child("cover_photo")
                        .child(FirebaseAuth.getInstance().getUid());
                reference.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Toast.makeText(getContext(),"Avata photo saved",Toast.LENGTH_SHORT).show();
                        reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                database.getReference().child("User").child(auth.getUid()).child("coverPhoto").setValue(uri.toString());
                            }
                        });
                    }
                });
            }
        }
        else if(requestCode==22) {
            if (data!=null){
                Uri uri =data.getData();
                binding.profileImage.setImageURI(uri);
                final StorageReference reference2 = storage.getReference().child("profile_image")
                        .child(FirebaseAuth.getInstance().getUid());
                reference2.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Toast.makeText(getContext(),"Avata photo saved",Toast.LENGTH_SHORT).show();
                        reference2.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                database.getReference().child("User").child(auth.getUid()).child("profileImage").setValue(uri.toString());
                            }
                        });
                    }
                });
            }
        }
        else if (requestCode==33){
            if (data!=null){
                Uri uri=data.getData();
                final StorageReference reference = storage.getReference().child( "stories")
                        .child( FirebaseAuth.getInstance().getUid() )
                        .child( new Date().getTime()+"" );
                reference.putFile( uri ).addOnSuccessListener( new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        reference.getDownloadUrl().addOnSuccessListener( new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                StoryModel story =new StoryModel();
                                Date date = new Date();
                                SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
                                String strDate = formatter.format( date );
                                story.setStoryAt( strDate );
                                story.setStoryBy( FirebaseAuth.getInstance().getUid());
                                database.getReference()
                                        .child( "stories" )
                                        .child( FirebaseAuth.getInstance().getUid() )
                                        .child( "postedBy" )
                                        .setValue(story.getStoryAt()).addOnSuccessListener( new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void unused) {
                                                UserStories stories =new UserStories(uri.toString(),story.getStoryAt());
                                                database.getReference()
                                                        .child("stories")
                                                        .child( FirebaseAuth.getInstance().getUid() )
                                                        .child("userStories")
                                                        .push()
                                                        .setValue( stories  ).addOnSuccessListener( new OnSuccessListener<Void>() {
                                                            @Override
                                                            public void onSuccess(Void unused) {

                                                            }
                                                        } );
                                            }
                                        } );
                            }
                        } );

                    }
                } );
            }
        }

    }
}
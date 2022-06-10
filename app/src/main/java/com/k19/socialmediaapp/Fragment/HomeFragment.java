package com.k19.socialmediaapp.Fragment;

import android.app.ProgressDialog;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContract;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.cooltechworks.views.shimmer.ShimmerRecyclerView;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.k19.socialmediaapp.Adapter.postAdapter;
import com.k19.socialmediaapp.Adapter.StoryAdapter;
import com.k19.socialmediaapp.Models.StoryModel;
import com.k19.socialmediaapp.Models.UserStories;
import com.k19.socialmediaapp.Models.postModel;
import com.k19.socialmediaapp.R;
import com.k19.socialmediaapp.databinding.FragmentHomeBinding;
import com.makeramen.roundedimageview.RoundedImageView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;


public class HomeFragment extends Fragment {


    RecyclerView storyRv;
    ArrayList<StoryModel> list;
    ArrayList<postModel> dashbordList ;
    ShimmerRecyclerView dashbordRV;
    FirebaseDatabase database;
    FirebaseAuth auth;
    FragmentHomeBinding binding;
    RoundedImageView addStory;
    FirebaseStorage storage;
    ActivityResultLauncher<String> galleryLauncher;
    ProgressDialog dialog;
    // TODO: Rename and change types of parameters


    public HomeFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters


    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        dialog=new ProgressDialog(getContext());



    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_home, container, false);
        binding = FragmentHomeBinding.inflate( inflater, container, false );
        dashbordRV=view.findViewById(R.id.dashbordRV);
        dashbordRV.showShimmerAdapter();
        database = FirebaseDatabase.getInstance();
        auth = FirebaseAuth.getInstance();
        storage=FirebaseStorage.getInstance();
        dialog.setProgressStyle( ProgressDialog.STYLE_SPINNER );
        dialog.setTitle( "Story Uploading..." );
        dialog.setMessage("Please Wait..."   );
        dialog.setCancelable(false);
        storyRv = view.findViewById(R.id.storyRV);
        list = new ArrayList<>();
        StoryAdapter adapter = new StoryAdapter(list,getContext());
        LinearLayoutManager linearLayoutManager =new LinearLayoutManager(getContext(),LinearLayoutManager.HORIZONTAL,false);
        storyRv.setLayoutManager(linearLayoutManager );
        storyRv.setNestedScrollingEnabled(false);
        storyRv.setAdapter(adapter);
        database.getReference()
                .child( "stories" ).addValueEventListener( new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.exists()){
                            list.clear();
                            for (DataSnapshot dataSnapshot:snapshot.getChildren()){
                                StoryModel story=new StoryModel();
                                story.setStoryBy( dataSnapshot.getKey() );
                                story.setStoryAt( dataSnapshot.child( "postedBy" ).getValue(String.class) );
                                ArrayList<UserStories> stories=new ArrayList<>();
                                if(dataSnapshot.exists()){
                                    for (DataSnapshot snapshot1:dataSnapshot.child("userStories").getChildren() ){
                                        if (snapshot1.exists()){
                                            UserStories userStories=snapshot1.getValue(UserStories.class);
                                            stories.add( userStories );
                                        }
                                    }
                                    story.setStories(  stories );
                                    list.add( story );
                                }

                            }
                            adapter.notifyDataSetChanged();
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                } );


        dashbordList = new ArrayList<>();
        postAdapter listPost = new postAdapter(dashbordList,getContext());
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        dashbordRV.setLayoutManager(layoutManager);
        dashbordRV.addItemDecoration( new DividerItemDecoration( dashbordRV.getContext() ,DividerItemDecoration.VERTICAL) );
        dashbordRV.setNestedScrollingEnabled(false);

        database.getReference().child( "post" )
                .addValueEventListener( new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        dashbordList.clear();
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                            postModel post =  dataSnapshot.getValue(postModel.class);
                            post.setPostID( dataSnapshot.getKey() );
                            dashbordList.add( post );

                        }
                        dashbordRV.setAdapter(listPost);
                        dashbordRV.hideShimmerAdapter();
                        listPost.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                } );
    addStory=view.findViewById( R.id.addStory );
    addStory.setOnClickListener( new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            galleryLauncher.launch( "image/*" );
        }
    } );
    galleryLauncher=registerForActivityResult( new ActivityResultContracts.GetContent()
            , new ActivityResultCallback<Uri>() {
                @Override
                public void onActivityResult(Uri result) {
                    addStory.setImageURI(result  );
                    dialog.show();
                    Date date = new Date();
                    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
                    String strDate = formatter.format( date );
                    final StorageReference reference = storage.getReference()
                            .child( "stories" )
                            .child( FirebaseAuth.getInstance().getUid() )
                            .child( new Date().getTime()+"" );
                    reference.putFile( result ).addOnSuccessListener( new OnSuccessListener<UploadTask.TaskSnapshot>() {
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
                                                                    dialog.dismiss();
                                                                }
                                                            } );
                                                }
                                            } );
                                }
                            } );
                        }
                    } );
                }
            } );
        return  view;
    }
}
package com.k19.socialmediaapp.Fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.k19.socialmediaapp.Adapter.addFriendAdapter;
import com.k19.socialmediaapp.Adapter.userAdapter;
import com.k19.socialmediaapp.Models.User;
import com.k19.socialmediaapp.Models.addfriend;
import com.k19.socialmediaapp.databinding.FragmentSearchBinding;

import java.util.ArrayList;

public class SearchFragment extends Fragment {



FragmentSearchBinding binding;
FirebaseAuth auth;
FirebaseDatabase database,db;

    public SearchFragment() {
        // Required empty public constructor
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        auth = FirebaseAuth.getInstance();
        database=FirebaseDatabase.getInstance();
        db=FirebaseDatabase.getInstance();
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding=FragmentSearchBinding.inflate(inflater, container, false);
//         Inflate the layout for this fragment
//
       // get invitation
        ArrayList<User> arr = new ArrayList<>();
        addFriendAdapter UserAdapter  = new addFriendAdapter( arr, getContext() );
        LinearLayoutManager layoutManager1=new LinearLayoutManager( getContext() );
        binding.invitationRV.setLayoutManager(layoutManager1);
        binding.invitationRV.setAdapter(UserAdapter);
        database.getReference().child( "User" )
                .child(auth.getUid())
                .child("invitation")
                .addListenerForSingleValueEvent( new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        arr.clear();
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                            User user =dataSnapshot.getValue(User.class);
                            user.setUserID( dataSnapshot.getKey() );
                            FirebaseDatabase.getInstance().getReference("User")
                                            .child( user.getUserID()).addListenerForSingleValueEvent( new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {

                                            for (DataSnapshot dataSnapshot1:snapshot.getChildren()){
                                               if(dataSnapshot1.getKey().equals( "email" )){
                                                   user.setEmail( dataSnapshot1.getValue(String.class) );

                                               }else if(dataSnapshot1.getKey().equals( "name" )){
                                                   user.setName( dataSnapshot1.getValue(String.class) );
                                               }
                                               else if(dataSnapshot1.getKey().equals("profileImage" )){
                                                   user.setProfileImage( dataSnapshot1.getValue(String.class) );
                                               }
                                            }
                                            arr.add(user);
                                            UserAdapter.notifyDataSetChanged();
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
        //get user
        ArrayList<User> list= new ArrayList<>();
        userAdapter adapter = new userAdapter (getContext(),list);
        LinearLayoutManager layoutManager= new LinearLayoutManager(getContext());
        binding.userRV.setLayoutManager(layoutManager);
        binding.userRV.setAdapter(adapter);
        database.getReference().child("User").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                list.clear();
                 for (DataSnapshot dataSnapshot :snapshot.getChildren()){
                     User user = dataSnapshot.getValue(User.class);
                     user.setUserID(dataSnapshot.getKey());
                     if(!FirebaseAuth.getInstance().getUid().equals( user.getUserID() ) ){
                         FirebaseDatabase.getInstance().getReference()
                                 .child( "User" )
                                 .child( auth.getUid() )
                                 .child( "friendList" )
                                 .orderByKey().endAt( user.getUserID() ).addValueEventListener( new ValueEventListener() {
                                     @Override
                                     public void onDataChange(@NonNull DataSnapshot snapshot) {
                                         if (!snapshot.exists()){
                                             list.add( user );
//                                             Toast.makeText( getContext(),user.getName(),Toast.LENGTH_LONG ).show();
                                             adapter.notifyDataSetChanged();
                                         }

                                     }
                                     @Override
                                     public void onCancelled(@NonNull DatabaseError error) {

                                     }
                                 } );


                     }
                 }

            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
        return binding .getRoot();
    }
    public  void checkFriend(String userID,String friendID ){
        FirebaseDatabase.getInstance().getReference()
                .child( "User" )
                .child( userID )
                .child( "friendList" )
                .orderByKey().endAt( friendID ).addValueEventListener( new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()){
                            Toast.makeText( getContext(),"have "+friendID,Toast.LENGTH_LONG ).show();
                        }
                        else {
                            Toast.makeText( getContext(),"not "+friendID,Toast.LENGTH_LONG ).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                } );

    }
}
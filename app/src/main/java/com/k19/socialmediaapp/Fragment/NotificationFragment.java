package com.k19.socialmediaapp.Fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.k19.socialmediaapp.Adapter.notificationAdapter;
import com.k19.socialmediaapp.Models.User;
import com.k19.socialmediaapp.Models.notificationModel;
import com.k19.socialmediaapp.R;

import java.util.ArrayList;

import com.k19.socialmediaapp.databinding.ActivityMainBinding;
import com.k19.socialmediaapp.databinding.FragmentNotificationBinding;

public class NotificationFragment extends Fragment {
    FragmentNotificationBinding binding;

    ArrayList<notificationModel> notificationModels;
    FirebaseDatabase database;
    FirebaseAuth auth;



    public NotificationFragment() {
        // Required empty public constructor
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        database = FirebaseDatabase.getInstance();
        auth = FirebaseAuth.getInstance();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        binding=FragmentNotificationBinding.inflate(inflater, container, false);

        notificationModels = new ArrayList<>();

        notificationAdapter adapter = new notificationAdapter( notificationModels,getContext() );
        LinearLayoutManager layoutManager1=new LinearLayoutManager( getContext() );
        binding.notificationRV.setLayoutManager( layoutManager1 );
        binding.notificationRV.setAdapter( adapter );
        database.getReference().child( "User" )
                .child( auth.getUid() )
                .child( "notification" )
                .addValueEventListener( new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        notificationModels.clear();
                        for (DataSnapshot dataSnapshot:snapshot.getChildren()){
                            notificationModel notification = dataSnapshot.getValue(notificationModel.class);
                            database.getReference().child( "User")
                                    .child( notification.getNotificationBy() )
                                    .addListenerForSingleValueEvent( new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            for (DataSnapshot dataSnapshot1:snapshot.getChildren()){
                                                if(dataSnapshot1.getKey().equals("profileImage" )){
                                                    notification.setProfile( dataSnapshot1.getValue(String.class) );
                                                    break;
                                                }
                                            }
                                            notificationModels.add( notification );
                                            adapter.notifyDataSetChanged();

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


        return binding.getRoot();

    }
}
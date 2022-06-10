package com.k19.socialmediaapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.k19.socialmediaapp.Adapter.chatAdapter;
import com.k19.socialmediaapp.Models.User;
import com.k19.socialmediaapp.Models.addfriend;
import com.k19.socialmediaapp.Models.chatModel;
import com.k19.socialmediaapp.databinding.ActivityUserBinding;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class UserActivity extends AppCompatActivity {
    private ActivityUserBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        binding = ActivityUserBinding.inflate(getLayoutInflater() );
        setContentView(binding.getRoot() );
        setListener();
        getUser();
    }
    private  void setListener(){
        binding.imageBack.setOnClickListener( v -> onBackPressed() );
    }
    private  void getUser(){
        loading( true );
        ArrayList<User> list = new ArrayList<>();
        chatAdapter adapter = new chatAdapter(this,list);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        binding.usersRecyclerView.setLayoutManager( layoutManager );
        binding.usersRecyclerView.setAdapter( adapter );
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        FirebaseAuth auth = FirebaseAuth.getInstance();
        database.getReference()
                .child( "User" )
                .child( auth.getUid() )
                .child( "friendList" )
                .addListenerForSingleValueEvent( new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot dataSnapshot:snapshot.getChildren()){
                            addfriend friend = dataSnapshot.getValue(addfriend.class);
                            friend.setAddBy( dataSnapshot.getKey() );
                            User user=new User();

                            database.getReference()
                                    .child( "User" )
                                    .child( friend.getAddBy() ).addListenerForSingleValueEvent( new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            User user = snapshot.getValue(User.class);
                                            user.setUserID(snapshot.getKey());
                                            list.add( user );
                                            if (list.size()>0){
                                                adapter.notifyDataSetChanged();
                                            }
                                            else {
                                                showErrorMessage();
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
    private  void showErrorMessage(){
        binding.textErrorMessage.setText( String.format( "%s","no User available" ) );
        binding.textErrorMessage.setVisibility( View.VISIBLE );
    }
    private  void loading(Boolean isLoading){
        if (isLoading){
            binding.progressBar.setVisibility( View.INVISIBLE );
        }
        else {
            binding.progressBar.setVisibility( View.INVISIBLE );
        }
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        finish();
        return super.onOptionsItemSelected( item );
    }
}
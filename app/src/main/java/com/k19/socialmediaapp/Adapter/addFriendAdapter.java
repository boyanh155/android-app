package com.k19.socialmediaapp.Adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.k19.socialmediaapp.Fragment.HomeFragment;
import com.k19.socialmediaapp.Fragment.SearchFragment;
import com.k19.socialmediaapp.MainActivity;
import com.k19.socialmediaapp.Models.User;
import com.k19.socialmediaapp.Models.addfriend;
import com.k19.socialmediaapp.Models.notificationModel;
import com.k19.socialmediaapp.R;
import com.k19.socialmediaapp.databinding.InvitationRvBinding;
import com.k19.socialmediaapp.databinding.UserRvBinding;
import com.k19.socialmediaapp.infomationActivity;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Objects;

public class addFriendAdapter extends RecyclerView.Adapter<addFriendAdapter.viewHolder> {
    ArrayList<User>list;
    Context context;
    private LayoutInflater layoutInflater;

    public addFriendAdapter(ArrayList<User> list, Context context) {
        this.list = list;
        this.context = context;

    }

    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate( R.layout.invitation_rv,parent,false);
        return new viewHolder( view );
    }
    @Override
    public void onBindViewHolder(@NonNull viewHolder holder, int position) {
        User user=list.get(position);
        Picasso.get()
                .load(user.getProfileImage()).placeholder(R.drawable.placehoder).into(holder.binding.profileImage);
        holder.binding.name.setText(user.getName());
        holder.binding.acceptBtn.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            FirebaseDatabase.getInstance().getReference("User")
                    .child( Objects.requireNonNull( FirebaseAuth.getInstance().getUid() ) )
                    .child("invitation")
                    .child(user.getUserID()).removeValue().addOnCompleteListener( new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                holder.binding.acceptBtn.setText("Đã đồng ý lời mời" );
                                holder.binding.removeBtn.setVisibility( View.GONE );
                                addfriend Fr =new addfriend();
                                Fr.setAddBy(user.getUserID() );
                                Fr.setAddAt( new Date().getTime() );
                                FirebaseDatabase.getInstance().getReference("User")
                                        .child( FirebaseAuth.getInstance().getUid())
                                        .child("friendList")
                                        .child( user.getUserID() )
                                        .setValue(Fr).addOnCompleteListener( new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                addfriend fr = new addfriend();
                                                fr.setAddBy( FirebaseAuth.getInstance().getUid() );
                                                fr.setAddAt( Fr.getAddAt() );
                                                FirebaseDatabase.getInstance().getReference().child( "User" )
                                                        .child( user.getUserID() )
                                                        .child( "friendList" )
                                                        .child( FirebaseAuth.getInstance().getUid() )
                                                        .setValue(fr );
                                            }
                                        } );
                            }
                        }
                    } ).addOnFailureListener( new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText( view.getContext(), e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                        }
                    } );

            }
        } );
        holder.binding.removeBtn.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                holder.binding.acceptBtn.setVisibility( View.GONE );
                holder.binding.removeBtn.setText( "Đã xoá lời mời" );
                FirebaseDatabase.getInstance().getReference("User")
                        .child(FirebaseAuth.getInstance().getUid() )
                        .child( "invitation" )
                        .child( user.getUserID() )
                        .removeValue().addOnCompleteListener( new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful()){
                                    Toast.makeText( context,"Đã xoá lời mời",Toast.LENGTH_LONG ).show();

                                }
                            }
                        } ).addOnFailureListener( new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText( context,"Đã xảy ra lỗi, vui lòng thử lại!!",Toast.LENGTH_LONG ).show();
                            }
                        } );

            }
        } );
        holder.binding.profileImage.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, infomationActivity.class );
                intent.putExtra( "userId" ,user.getUserID());
                intent.setFlags( Intent.FLAG_ACTIVITY_CLEAR_TASK );
                context.startActivity( intent );
            }
        } );
        holder.binding.name.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, infomationActivity.class );
                intent.putExtra( "userId" ,user.getUserID());
                intent.setFlags( Intent.FLAG_ACTIVITY_CLEAR_TASK );
                context.startActivity( intent );
            }
        } );
    }
    @Override
    public int getItemCount() {
        return list.size();
    }

    public  class viewHolder extends RecyclerView.ViewHolder {
        InvitationRvBinding binding;
        public viewHolder(@NonNull View itemView) {
            super(itemView);
             binding=InvitationRvBinding.bind( itemView );

        }
    }

}

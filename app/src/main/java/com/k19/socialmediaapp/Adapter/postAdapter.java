package com.k19.socialmediaapp.Adapter;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.k19.socialmediaapp.CommentActivity;
import com.k19.socialmediaapp.Models.User;
import com.k19.socialmediaapp.Models.notificationModel;
import com.k19.socialmediaapp.Models.postModel;
import com.k19.socialmediaapp.R;
import com.k19.socialmediaapp.databinding.ActivityCommentBinding;
import com.k19.socialmediaapp.databinding.DasboardRvBinding;
import com.k19.socialmediaapp.infomationActivity;
import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Objects;

public class postAdapter extends RecyclerView.Adapter<postAdapter.viewHolder> {

    ArrayList<postModel> list;
    Context context;
    FirebaseDatabase database;
    FirebaseAuth auth;

    public postAdapter(ArrayList<postModel> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.dasboard_rv,parent,false);
        return new viewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull viewHolder holder, int position) {
        postModel model = list.get( position );
        String description=model.getPostDescription();
        if (description.equals( "" )){
            holder.binding.storyPost.setVisibility( View.GONE );
        }else {
            holder.binding.storyPost.setText( model.getPostDescription() );
            holder.binding.storyPost.setVisibility( View.VISIBLE );
        }
        Picasso.get()
                .load( model.getPostImage() ).placeholder( R.drawable.placehoder ).into( holder.binding.postImg );
        String time =calculateTimeago(model.getPostedAt());
        holder.binding.time.setText( time);
        holder.binding.love.setText( model.getPostLove() +"");
        holder.binding.cmt.setText( model.getCommentCount()+"" );
        FirebaseDatabase.getInstance().getReference()
                .child( "User" )
                .child( model.getPostedBy()).addValueEventListener( new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        User user = snapshot.getValue(User.class);
                        Picasso.get()
                                .load(user.getProfileImage()).placeholder(R.drawable.placehoder).into(holder.binding.profileImage);
                        holder.binding.userName.setText( user.getName() );
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                } );
        FirebaseDatabase.getInstance().getReference()
                        .child( "post" )
                        .child( model.getPostID() )
                        .child( "loves" )
                        .child( Objects.requireNonNull( FirebaseAuth.getInstance().getUid() ) )
                        .addListenerForSingleValueEvent( new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()){
                            holder.binding.love.setCompoundDrawablesWithIntrinsicBounds( R.drawable.heart2,0,0,0 );
                            holder.binding.love.setOnClickListener( new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    FirebaseDatabase.getInstance().getReference()
                                            .child( "post" )
                                            .child( model.getPostID() )
                                            .child( "loves" )
                                            .child( FirebaseAuth.getInstance().getUid() )
                                            .removeValue().addOnSuccessListener( new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void unused) {
                                                    FirebaseDatabase.getInstance().getReference()
                                                            .child( "post" )
                                                            .child( model.getPostID() )
                                                            .child("postLove" )
                                                            .setValue( model.getPostLove()-1 ).addOnSuccessListener( new OnSuccessListener<Void>() {
                                                                @Override
                                                                public void onSuccess(Void unused) {
                                                                    holder.binding.love.setCompoundDrawablesWithIntrinsicBounds( R.drawable.heart,0,0,0 );

                                                                }
                                                            } );
                                                }
                                            } );
                                }
                            } );

                        }else {
                            holder.binding.love.setOnClickListener( new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    FirebaseDatabase.getInstance().getReference()
                                            .child( "post" )
                                            .child( model.getPostID() )
                                            .child( "loves" )
                                            .child( FirebaseAuth.getInstance().getUid() )
                                            .setValue( true ).addOnSuccessListener( new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void unused) {
                                                    FirebaseDatabase.getInstance().getReference()
                                                            .child(  "post" )
                                                            .child( model.getPostID() )
                                                            .child( "postLove" )
                                                            .setValue( model.getPostLove()+1 ).addOnSuccessListener( new OnSuccessListener<Void>() {
                                                                @Override
                                                                public void onSuccess(Void unused) {
                                                                    holder.binding.love.setCompoundDrawablesWithIntrinsicBounds( R.drawable.heart2,0,0,0 );
                                                                    if (!model.getPostedBy().equals( FirebaseAuth.getInstance().getUid() )){
                                                                        notificationModel notificationModel =new notificationModel();
                                                                        notificationModel.setNotificationBy( FirebaseAuth.getInstance().getUid() );
                                                                        FirebaseDatabase.getInstance().getReference()
                                                                                        .child( "User" )
                                                                                        .child( FirebaseAuth.getInstance().getUid() ).addListenerForSingleValueEvent( new ValueEventListener() {
                                                                                    @Override
                                                                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                                                        if (snapshot.exists()){
                                                                                            User u = snapshot.getValue(User.class);
                                                                                            notificationModel.setNotification( "<b>"+u.getName()+"</b>" + "Đã bày tỏ cảm xúc về bài post của bạn."  );
                                                                                            FirebaseDatabase.getInstance().getReference()
                                                                                                    .child( "User" )
                                                                                                    .child( model.getPostedBy() )
                                                                                                    .child( "notification" )
                                                                                                    .push()
                                                                                                    .setValue( notificationModel );
                                                                                        }
                                                                                    }

                                                                                    @Override
                                                                                    public void onCancelled(@NonNull DatabaseError error) {

                                                                                    }
                                                                                } );



                                                                    }
                                                                }
                                                            } );
                                                }
                                            } );
                                }
                            } );
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                } );
        holder.binding.cmt.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent( context, CommentActivity.class );
                intent.putExtra( "postID",model.getPostID() );
                intent.putExtra( "postedBy",model.getPostedBy() );
                intent.setFlags( Intent.FLAG_ACTIVITY_CLEAR_TASK );
                context.startActivity( intent );

            }
        } );
        holder.binding.profileImage.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context,infomationActivity.class );
                intent.putExtra( "userId" ,model.getPostedBy());
                intent.setFlags( Intent.FLAG_ACTIVITY_CLEAR_TASK );
                context.startActivity( intent );
            }
        } );
        holder.binding.userName.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, infomationActivity.class );
                intent.putExtra( "userId" ,model.getPostedBy());
                intent.setFlags( Intent.FLAG_ACTIVITY_CLEAR_TASK );
                context.startActivity( intent );
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
        return "";
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class viewHolder extends RecyclerView.ViewHolder{
    DasboardRvBinding binding;
        public viewHolder(@NonNull View itemView) {
            super(itemView);
            binding = DasboardRvBinding.bind( itemView );


        }
    }
}

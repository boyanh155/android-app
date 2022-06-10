package com.k19.socialmediaapp.Adapter;

import android.content.Context;
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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.k19.socialmediaapp.Fragment.HomeFragment;
import com.k19.socialmediaapp.Fragment.ProfileFragment;
import com.k19.socialmediaapp.Fragment.SearchFragment;
import com.k19.socialmediaapp.MainActivity;
import com.k19.socialmediaapp.Models.User;
import com.k19.socialmediaapp.Models.addfriend;
import com.k19.socialmediaapp.Models.notificationModel;
import com.k19.socialmediaapp.R;
import com.k19.socialmediaapp.databinding.ActivityMainBinding;
import com.k19.socialmediaapp.databinding.UserRvBinding;
import com.k19.socialmediaapp.databinding.UserSampleBinding;
import com.k19.socialmediaapp.infomationActivity;
import com.squareup.picasso.Picasso;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Date;
import java.util.Objects;

public class userAdapter extends RecyclerView.Adapter<userAdapter.viewHodler> {
    Context context;
    ArrayList<User>list;

    public userAdapter(Context context, ArrayList<User> list) {
        this.context = context;
        this.list = list;
    }
    @NonNull
    @Override
    public viewHodler onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.user_rv,parent,false);
        return new viewHodler(view);
    }
    @Override
    public void onBindViewHolder(@NonNull viewHodler holder, int position) {
        User user=list.get(position);
        Picasso.get()
                .load(user.getProfileImage()).placeholder(R.drawable.placehoder).into(holder.binding.profileImage);
        holder.binding.name.setText(user.getName());
        holder.binding.addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                holder.binding.addBtn.setVisibility( View.GONE );
                holder.binding.removeBtn.setVisibility(View.GONE );
                holder.binding.CancelBtn.setVisibility( View.VISIBLE );

                addfriend Addfriend = new addfriend();
                Addfriend.setAddBy(Objects.requireNonNull( FirebaseAuth.getInstance().getUid() ));
                if(!Addfriend.getAddBy().equals( user.getUserID() )){
                    Addfriend.setAddAt( new Date().getTime() );
                    FirebaseDatabase.getInstance().getReference()
                            .child( "User" )
                            .child( user.getUserID())
                            .child("invitation" )
                            .child(FirebaseAuth.getInstance().getUid())
                            .setValue(Addfriend);
                    Toast.makeText(context,"You add"+user.getUserID(),Toast.LENGTH_LONG ).show();
                    notificationModel notification = new notificationModel();
                    notification.setNotificationBy( FirebaseAuth.getInstance().getUid() );
                    FirebaseDatabase.getInstance().getReference()
                                    .child( "User" ).child( FirebaseAuth.getInstance().getUid() )
                                    .addListenerForSingleValueEvent( new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            for (DataSnapshot dataSnapshot1:snapshot.getChildren()){
                                                if(dataSnapshot1.getKey().equals( "name" )){
                                                    notification.setNotification("<b>"+dataSnapshot1.getValue(String.class)
                                                                   +"</b>" +" Đã gởi lời mời kết bạn.");Toast.makeText(context,"notofication"+notification.getNotification(),Toast.LENGTH_LONG ).show();
                                                    break;
                                                }
                                            }
                                            FirebaseDatabase.getInstance().getReference()
                                                    .child( "User" )
                                                    .child( user.getUserID() )
                                                    .child( "notification")
                                                    .push()
                                                    .setValue(notification );
                                        }
                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {

                                        }
                                    } );

                }
            }
        });
        holder.binding.CancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                holder.binding.addBtn.setVisibility( View.VISIBLE );
                holder.binding.removeBtn.setVisibility(View.VISIBLE );
                holder.binding.CancelBtn.setVisibility( View.GONE );

                FirebaseDatabase.getInstance().getReference()
                        .child( "User" )
                        .child( user.getUserID())
                        .child("invitation" )
                        .child(FirebaseAuth.getInstance().getUid() )
                        .removeValue().addOnCompleteListener( new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()){
                                    Toast.makeText(context,"Hoàn tác thành công",Toast.LENGTH_LONG ).show();
                                }
                            }
                        } ).addOnFailureListener( new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(context,"Đã xảy ra lỗi vui lòng thử lại",Toast.LENGTH_LONG ).show();
                            }
                        } );


            }
        });
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

    public  class viewHodler extends RecyclerView.ViewHolder {
        UserRvBinding binding;
        public viewHodler(@NonNull View itemView) {
            super(itemView);
           binding=UserRvBinding.bind(itemView);
        }
    }
}

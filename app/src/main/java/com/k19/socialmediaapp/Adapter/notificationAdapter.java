package com.k19.socialmediaapp.Adapter;

import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.k19.socialmediaapp.Models.User;
import com.k19.socialmediaapp.databinding.ActivityMainBinding;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.k19.socialmediaapp.Models.notificationModel;
import com.k19.socialmediaapp.R;
import com.k19.socialmediaapp.databinding.NotificationRvBinding;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class notificationAdapter extends RecyclerView.Adapter<notificationAdapter.viewHolder> {
    ArrayList<notificationModel> list;
    Context context;

    public notificationAdapter(ArrayList<notificationModel> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.notification_rv,parent,false);
        return new viewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull viewHolder holder, int position) {
       notificationModel model= list.get(position);
        Picasso.get()
                .load(model.getProfile()).placeholder(R.drawable.placehoder).into(holder.binding.profileImage);
//        FirebaseDatabase.getInstance().getReference()
//                        .child( "User" )
//                        .child( model.getNotificationBy() ).addListenerForSingleValueEvent( new ValueEventListener() {
//                    @Override
//                    public void onDataChange(@NonNull DataSnapshot snapshot) {
//                        if (snapshot.exists()){
//                            User u = snapshot.getValue(User.class);
//                            model.setProfile( u.getProfileImage() );
//
//                        }
//                    }
//                    @Override
//                    public void onCancelled(@NonNull DatabaseError error) {
//                    }
//                } );


        holder.binding.notification.setText( Html.fromHtml(model.getNotification()  ) );
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class viewHolder extends RecyclerView.ViewHolder {
        NotificationRvBinding binding;
        public viewHolder(@NonNull View itemView) {
            super(itemView);
            binding= NotificationRvBinding.bind( itemView );
        }
    }
}

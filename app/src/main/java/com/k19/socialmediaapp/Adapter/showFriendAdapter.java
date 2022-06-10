package com.k19.socialmediaapp.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.k19.socialmediaapp.Models.User;
import com.k19.socialmediaapp.R;
import com.k19.socialmediaapp.databinding.FriendRvBinding;
import com.k19.socialmediaapp.infomationActivity;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class showFriendAdapter extends RecyclerView.Adapter<showFriendAdapter.viewHodler> {
    Context context;
    ArrayList<User> list;

    public showFriendAdapter(Context context, ArrayList<User> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public viewHodler onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate( R.layout.friend_rv,parent,false);
        return new viewHodler( view );
    }

    @Override
    public void onBindViewHolder(@NonNull viewHodler holder, int position) {
        User user=list.get(position);
        Picasso.get()
                .load(user.getProfileImage()).placeholder(R.drawable.placehoder).into(holder.binding.profileImage);
        holder.binding.name.setText(user.getName());
        holder.binding.profileImage.setOnClickListener( new View.OnClickListener() {
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

    public  class  viewHodler extends RecyclerView.ViewHolder {
        FriendRvBinding binding;
        public viewHodler(@NonNull View itemView) {
            super( itemView );
            binding=FriendRvBinding.bind( itemView );
        }
    }
}

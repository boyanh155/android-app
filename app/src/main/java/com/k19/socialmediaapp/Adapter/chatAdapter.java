package com.k19.socialmediaapp.Adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Parcelable;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.k19.socialmediaapp.Models.User;
import com.k19.socialmediaapp.Models.chatModel;
import com.k19.socialmediaapp.R;
import com.k19.socialmediaapp.chatActivity;
import com.k19.socialmediaapp.databinding.ItemContainerUserBinding;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class chatAdapter  extends  RecyclerView.Adapter<chatAdapter.viewHolder>{
    Context context;
    ArrayList<User> list;


    public chatAdapter(Context context, ArrayList<User> list) {
        this.context = context;
        this.list = list;

    }

    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from( context ).inflate( R.layout.item_container_user,parent,false );
        return new viewHolder( view );
    }

    @Override
    public void onBindViewHolder(@NonNull viewHolder holder, int position) {
        User user = list.get( position );
        Picasso.get()
                .load( user.getProfileImage() )
                .placeholder( R.drawable.placehoder )
                .into( holder.binding.profileImage );
        holder.binding.Name.setText( Html.fromHtml( "<b>"+user.getName()+"</b>" ) );
        holder.binding.mess.setText( user.getEmail() );

        holder.binding.User.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, chatActivity.class);
                intent.putExtra( "userId", user.getUserID());
                intent.setFlags( Intent.FLAG_ACTIVITY_CLEAR_TASK );
                context.startActivity( intent );

            }
        } );

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class viewHolder extends RecyclerView.ViewHolder {
        ItemContainerUserBinding binding;
        public viewHolder(@NonNull View itemView) {
            super( itemView );

            binding = ItemContainerUserBinding.bind( itemView );
        }
    }

}

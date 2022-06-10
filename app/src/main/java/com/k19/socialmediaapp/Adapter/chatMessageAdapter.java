package com.k19.socialmediaapp.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.k19.socialmediaapp.Models.User;
import com.k19.socialmediaapp.Models.chatMessage;
import com.k19.socialmediaapp.R;
import com.k19.socialmediaapp.chatActivity;
import com.k19.socialmediaapp.databinding.ItemContainerReciviedMessageBinding;
import com.k19.socialmediaapp.databinding.ItemContainerSendMessageBinding;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class chatMessageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final List<chatMessage> chatMessage;
    private Context context ;


    public  static final int SMG_TYPE_RIGHT =1;
    public  static final int SMG_TYPE_LEFT=0;
    FirebaseUser fuser;
    public chatMessageAdapter(List<chatMessage> chatMessage, Context context) {
        this.chatMessage = chatMessage;
        this.context = context;

    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if(viewType==SMG_TYPE_RIGHT){
            view = LayoutInflater.from( context ).inflate( R.layout.item_container_send_message, parent, false );
            return new SentMessageviewHolder( view );
        }else {
            view = LayoutInflater.from( context ).inflate( R.layout.item_container_recivied_message, parent, false );
            return new RecevideMessageviewHodler( view );
        }


    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (getItemViewType( position )==SMG_TYPE_RIGHT){
            ((SentMessageviewHolder)holder).setData( chatMessage.get( position ) );
        }else {
            ((RecevideMessageviewHodler)holder).setData( chatMessage.get( position ) );
        }

    }

//    @Override
//    public void onBindViewHolder(@NonNull viewHolder holder, int position) {
//        chatMessage chat = chatMessage.get( position );
//        holder.show_message.setText( chat.getMess() );
//        FirebaseDatabase.getInstance().getReference()
//                        .child( "User" )
//                                .child( chat.getReceiverId() ).addListenerForSingleValueEvent( new ValueEventListener() {
//                    @Override
//                    public void onDataChange(@NonNull DataSnapshot snapshot) {
//                        User user = snapshot.getValue(User.class);
//                        Picasso.get()
//                                .load( user.getProfileImage() )
//                                .placeholder( R.drawable.placehoder )
//                                .into( holder.image_profile );
//                    }
//                    @Override
//                    public void onCancelled(@NonNull DatabaseError error) {
//
//                    }
//                } );
//
//
//    }


    @Override
    public int getItemCount() {
        return chatMessage.size();
    }
    @Override
    public int getItemViewType(int position){
        fuser= FirebaseAuth.getInstance().getCurrentUser();
        if (chatMessage.get( position ).getSenderId().equals( fuser.getUid() )){
            return SMG_TYPE_RIGHT;
        }else {
            return SMG_TYPE_LEFT;
        }

    }

    public class  SentMessageviewHolder extends RecyclerView.ViewHolder {
        ItemContainerSendMessageBinding binding;
        public SentMessageviewHolder(@NonNull View itemView) {
            super( itemView );
            binding = ItemContainerSendMessageBinding.bind( itemView );
        }
        void setData(chatMessage chatMessage){
            binding.textMessage.setText( chatMessage.getMess() );
            binding.textDateTime.setText( chatMessage.getSendAdd() );
        }
    }
    public class RecevideMessageviewHodler extends  RecyclerView.ViewHolder {
        ItemContainerReciviedMessageBinding binding;
        public RecevideMessageviewHodler(@NonNull View itemView) {
            super( itemView );
            binding = ItemContainerReciviedMessageBinding.bind( itemView );
        }
        void setData(chatMessage chatMessage){
            binding.textMessage.setText( chatMessage.getMess() );
            binding.textDateTime.setText( chatMessage.getSendAdd() );
            FirebaseDatabase.getInstance().getReference()
                    .child( "User" )
                    .child( chatMessage.senderId ).addListenerForSingleValueEvent( new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            User user = snapshot.getValue(User.class);
                            Picasso.get()
                                    .load( user.getProfileImage() )
                                    .placeholder( R.drawable.placehoder )
                                    .into(binding.profileImage );
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    } );
        }
    }

}

package com.k19.socialmediaapp.Adapter;

import android.content.Context;
import android.text.Html;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.k19.socialmediaapp.Models.User;
import com.k19.socialmediaapp.Models.commentModel;
import com.k19.socialmediaapp.R;
import com.k19.socialmediaapp.databinding.CommentRvBinding;
import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.TimeZone;

public class commentAdapter extends RecyclerView.Adapter<commentAdapter.viewHodler> {
    Context context;
    ArrayList<commentModel> lis;

    public commentAdapter(Context context, ArrayList<commentModel> lis) {
        this.context = context;
        this.lis = lis;
    }

    @NonNull
    @Override
    public viewHodler onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from( context ).inflate(R.layout.comment_rv,parent,false );
        return new viewHodler( view );
    }

    @Override
    public void onBindViewHolder(@NonNull viewHodler holder, int position) {

        commentModel comment = lis.get( position );
        String time =calculateTimeago(comment.getCommentAt());

        holder.binding.time.setText( time );

        FirebaseDatabase.getInstance()
                .getReference()
                .child( "User" )
                .child( comment.getCommentBy() ).addListenerForSingleValueEvent( new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        User user = snapshot.getValue(User.class);

                        Picasso.get()
                                .load( user.getProfileImage() )
                                .placeholder( R.drawable.placehoder )
                                .into( holder.binding.profileImage );
                        holder.binding.comment.setText( Html.fromHtml("<b>"+user.getName()+"</b><br>" +comment.getCommentBody()));

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                } );


    }

    private String calculateTimeago(String commentAt) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");

        try {
            long time = sdf.parse(commentAt).getTime();
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
        return lis.size();
    }

    public class viewHodler extends RecyclerView.ViewHolder {
        CommentRvBinding binding;
        public viewHodler(@NonNull View itemView) {
            super( itemView );
            binding = CommentRvBinding.bind( itemView );
        }
    }
}

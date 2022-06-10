package com.k19.socialmediaapp.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.k19.socialmediaapp.Models.DasboardModel;
import com.k19.socialmediaapp.Models.friendModel;
import com.k19.socialmediaapp.R;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class firendAdapter extends RecyclerView.Adapter<firendAdapter.viewHolder> {
    ArrayList<friendModel> list;
    Context context;

    public firendAdapter(ArrayList<friendModel> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.friend_rv,parent,false);

        return new viewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull viewHolder holder, int position) {
        friendModel model = (friendModel) list.get(position);
        holder.profile.setImageResource(model.getProfile());
        holder.name.setText(model.getName());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class viewHolder extends RecyclerView.ViewHolder {
        ImageView profile;
        TextView name;
        public viewHolder(@NonNull View itemView) {
            super(itemView);
            profile = itemView.findViewById(R.id.profile_image);
            name=itemView.findViewById(R.id.name);
        }
    }
}

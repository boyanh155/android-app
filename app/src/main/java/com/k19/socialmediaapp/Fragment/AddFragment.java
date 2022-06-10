package com.k19.socialmediaapp.Fragment;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.core.Context;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageException;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.k19.socialmediaapp.MainActivity;
import com.k19.socialmediaapp.Models.User;
import com.k19.socialmediaapp.Models.postModel;
import com.k19.socialmediaapp.R;
import com.k19.socialmediaapp.databinding.FragmentAddBinding;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Date;


public class AddFragment extends Fragment {
    FragmentAddBinding binding;
    Uri uri=null;
    FirebaseDatabase database;
    FirebaseAuth auth;
    FirebaseStorage storage;
    ProgressDialog dialog;


    public AddFragment() {
        // Required empty public constructor
    }




    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        database = FirebaseDatabase.getInstance();
        auth = FirebaseAuth.getInstance();
        storage = FirebaseStorage.getInstance();
        dialog=new ProgressDialog(getContext() );

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        binding=FragmentAddBinding.inflate(inflater, container, false);
        dialog.setProgressStyle( ProgressDialog.STYLE_SPINNER );
        dialog.setTitle( "Post Uploading" );
        dialog.setMessage( "Wait..." );
        dialog.setCancelable( false );
        dialog.setCanceledOnTouchOutside( false );
        database.getReference().child( "User" )
                        .child( FirebaseAuth.getInstance().getUid() ).addListenerForSingleValueEvent( new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()){
                            User user = snapshot.getValue(User.class);
                            Picasso.get()
                                    .load(user.getProfileImage())
                                    .placeholder( R.drawable.placehoder )
                                    .into( binding.profileImage );
                            binding.name.setText( user.getName() );
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                } );

        binding.postDescription.addTextChangedListener( new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String description = binding.postDescription.getText().toString();
                if (!description.isEmpty()){
                    binding.postBtn.setBackground( ContextCompat.getDrawable(getContext(),R.drawable.shapebuttonfriend ) );
                    binding.postBtn.setTextColor( getContext().getResources().getColor(R.color.white) );
                    binding.postBtn.setEnabled( true );

                }
                else {
                    binding.postBtn.setBackground( ContextCompat.getDrawable(getContext(),R.drawable.shape ) );
                    binding.postBtn.setTextColor( getContext().getResources().getColor(R.color.white) );
                    binding.postBtn.setEnabled( false );
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        } );
        binding.addStoryImage.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setAction( Intent.ACTION_GET_CONTENT );
                intent.setType( "image/*" );
                startActivityForResult( intent,10 );

            }
        } );
        binding.postBtn.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.show();
                final StorageReference reference = storage.getReference().child( "post" )
                        .child( FirebaseAuth.getInstance().getUid() )
                        .child( new Date().getTime()+"" );
                if (uri!=null){

                        reference.putFile( uri ).addOnSuccessListener( new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                reference.getDownloadUrl().addOnSuccessListener( new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        postModel post = new postModel();
                                        post.setPostImage( uri.toString() );
                                        post.setPostedBy(FirebaseAuth.getInstance().getUid()  );
                                        post.setPostDescription( binding.postDescription.getText().toString() );
                                        Date date = new Date();
                                        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
                                        String strDate = formatter.format( date );
                                        post.setPostedAt(  strDate);
    //                                post.setPostedAt( new Date( ).getTime() );
                                        database.getReference().child( "post" )
                                                .push()
                                                .setValue(post ).addOnSuccessListener( new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void unused) {
                                                        dialog.dismiss();
                                                        Toast.makeText( getContext(),"Bạn đã đăng lên tường thành công",Toast.LENGTH_LONG ).show();
                                                        Intent intent = new Intent(getContext(), MainActivity.class );
                                                        startActivity( intent );
                                                    }
                                                } );
                                    }
                                } );
                            }
                        } );
                }
                else {
                    postModel model = new postModel();
                    model.setPostedBy( auth.getUid() );
                    model.setPostDescription( binding.postDescription.getText().toString() );
                    Date date = new Date();
                    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
                    String strDate = formatter.format( date );
                    model.setPostedAt(  strDate);
                    FirebaseDatabase.getInstance().getReference()
                            .child( "post" )
                            .push()
                            .setValue( model ).addOnSuccessListener( new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    dialog.dismiss();
                                    Toast.makeText( getContext(),"Bạn đã đăng lên tường thành công",Toast.LENGTH_LONG ).show();
                                    Intent intent = new Intent(getContext(), MainActivity.class );
                                    startActivity( intent );
                                }
                            } );
                }

            }
        } );
        return binding.getRoot();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult( requestCode, resultCode, data );
        if(data.getData()!=null){
             uri =data.getData();
             binding.postImage.setImageURI(  uri );
             binding.postImage.setVisibility( View.VISIBLE );
             binding.postBtn.setBackground( ContextCompat.getDrawable(getContext(),R.drawable.shapebuttonfriend ) );
            binding.postBtn.setTextColor( getContext().getResources().getColor(R.color.white) );
            binding.postBtn.setEnabled( true );


        }
    }
}
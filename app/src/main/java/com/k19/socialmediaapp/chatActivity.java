package com.k19.socialmediaapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.messaging.FirebaseMessaging;
import com.k19.socialmediaapp.Adapter.chatMessageAdapter;
import com.k19.socialmediaapp.Models.User;
import com.k19.socialmediaapp.Models.chatMessage;
import com.k19.socialmediaapp.Models.chatModel;
import com.k19.socialmediaapp.Models.postModel;
import com.k19.socialmediaapp.databinding.ActivityChatBinding;
import com.k19.socialmediaapp.notification.FcmNotificationsSender;

import java.sql.Array;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class chatActivity extends AppCompatActivity {
    private static final int NOTIFICATION_ID =1 ;
    private ActivityChatBinding binding;
private User reciverUser;
private  String userId;
private Intent intent;
private List<chatMessage> chatMessage;
private chatMessageAdapter chatAdapter;
private FirebaseUser fuser;
DatabaseReference reference;
private PreferenceManager preferenceManager;
private FirebaseDatabase database;
private FirebaseAuth auth;
chatMessageAdapter messageAdapter;
List<chatModel> chat;
RecyclerView recyclerView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        binding = ActivityChatBinding.inflate( getLayoutInflater() );
        setContentView( binding.getRoot() );
//        FirebaseMessaging.getInstance().subscribeToTopic("all");

        recyclerView=findViewById( R.id.chatRecyclerView );
        recyclerView.setHasFixedSize( true );
        LinearLayoutManager manager= new LinearLayoutManager( getApplicationContext() );
        manager.setStackFromEnd( true );
        recyclerView.setLayoutManager( manager );
        database=FirebaseDatabase.getInstance();
        auth=FirebaseAuth.getInstance();
        intent=getIntent();
        userId = intent.getStringExtra( "userId" );
        loadReciverUser();
        setListener();
    }
    private  void SendMessage(){

        if (binding.inputMessage.getText().toString().equals( "" )){
            Toast.makeText( this,"Hãy nhắn gì đó cho bạn của bạn." ,Toast.LENGTH_LONG).show();
        }else {
            chatMessage ChatMessage = new chatMessage();
            ChatMessage.setMess( binding.inputMessage.getText().toString() );
            ChatMessage.setSenderId( FirebaseAuth.getInstance().getUid() );
            ChatMessage.setReceiverId( userId );
            Date date = new Date();
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
            String strDate = formatter.format( date );
            ChatMessage.setSendAdd(strDate  );
            database.getReference()
                    .child( "chat" )
                    .push()
                    .setValue( ChatMessage );
            binding.inputMessage.setText( "" );
//            Bitmap bitmap= BitmapFactory.decodeResource(getResources(),R.mipmap.ic_launcher  );
//            Notification notification= new Notification.Builder(chatActivity.this)
//                    .setContentTitle("Tittle push notification"  )
//                    .setContentText( "Text push notification" )
//                    .setSmallIcon( R.drawable.apple )
//                    .setLargeIcon( bitmap )
//                    .build();
//            NotificationManager notificationManager =(NotificationManager) getSystemService( Context.NOTIFICATION_SERVICE );
//            if (notificationManager!=null){
//                notificationManager.notify(NOTIFICATION_ID,notification);
//            }
//            FcmNotificationsSender notificationsSender= new FcmNotificationsSender( "/topics/all","notification" ,
//                    "SMS",getApplicationContext(),chatActivity.this );
//            notificationsSender.SendNotifications();

        }

    }


    private  void loadReciverUser(){
        FirebaseDatabase.getInstance().getReference()
                .child( "User" )
                .child( userId ).addValueEventListener( new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        User user = snapshot.getValue(User.class);
                        binding.textName.setText( user.getName() );
                        readMessages( auth.getUid(),userId );
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                } );

    }
    private void setListener(){
        binding.imageBack.setOnClickListener( v -> onBackPressed() );
        binding.layoutSend.setOnClickListener( v->SendMessage() );
    }
    private  String getRealDateTime(Date date){
        return new SimpleDateFormat("MMMM dd, yyyy - hh:mm a", Locale.getDefault()).format( date );
    }
    private void readMessages(String myid,String userid){
        chatMessage = new ArrayList<>();
        reference=FirebaseDatabase.getInstance().getReference("chat");
        reference.addValueEventListener( new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                chatMessage.clear();
                for (DataSnapshot dataSnapshot:snapshot.getChildren()){
                    chatMessage chat = dataSnapshot.getValue(chatMessage.class);
                    if (chat.getReceiverId().equals( myid )&&chat.getSenderId().equals( userid )
                            ||chat.getReceiverId().equals( userid )&&chat.getSenderId().equals( myid )){
                        chatMessage.add( chat );

                    }
                    messageAdapter = new chatMessageAdapter( chatMessage,chatActivity.this );
                    recyclerView.setAdapter( messageAdapter );
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        } );

    }
}
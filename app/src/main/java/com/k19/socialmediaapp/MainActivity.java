package com.k19.socialmediaapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.databinding.DataBindingComponent;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.iammert.library.readablebottombar.ReadableBottomBar;
import com.k19.socialmediaapp.Fragment.AddFragment;
import com.k19.socialmediaapp.Fragment.HomeFragment;
import com.k19.socialmediaapp.Fragment.NotificationFragment;
import com.k19.socialmediaapp.Fragment.ProfileFragment;
import com.k19.socialmediaapp.Fragment.SearchFragment;

import com.k19.socialmediaapp.databinding.ActivityMainBinding;


public class MainActivity extends AppCompatActivity {
    ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding= ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setSupportActionBar(binding.toolbar);

        FragmentTransaction  transaction = getSupportFragmentManager().beginTransaction();
        binding.toolbar.setVisibility(View.GONE);
        transaction.replace(R.id.container,new HomeFragment());
        transaction.commit();
        binding.ReadableBottomBar.setOnItemSelectListener(new ReadableBottomBar.ItemSelectListener() {
            @Override
            public void onItemSelected(int i) {
                FragmentTransaction  transaction = getSupportFragmentManager().beginTransaction();
//                NAV handler
                switch (i){
                    case 0:

                        binding.toolbar.setVisibility(View.GONE);
                        transaction.replace(R.id.container,new HomeFragment());

                        break;
                    case 1:
                        binding.toolbar.setVisibility(View.GONE);
                        transaction.replace(R.id.container,new NotificationFragment());
                        break;
                    case 2:
                        binding.toolbar.setVisibility(View.GONE);
                        transaction.replace(R.id.container,new AddFragment());
                        break;
                    case 3:
                        binding.toolbar.setVisibility(View.GONE);
                        transaction.replace(R.id.container,new SearchFragment());

                        break;
                    case 4:
                        MainActivity.this.setTitle("My profile");
                        binding.toolbar.setVisibility(View.VISIBLE);
                        transaction.replace(R.id.container,new ProfileFragment());

                        break;
                    case 5:
                        Intent intent = new Intent(MainActivity.this,UserActivity.class);
                        startActivity( intent );
                        break;
                    case 6:
                        Intent intent1 = new Intent(MainActivity.this,UserActivity.class);
                        startActivity( intent1 );
                        break;
                }
                transaction.commit();
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_item,menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.setting:
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(MainActivity.this,SplashActivity.class);
                startActivity( intent );
                break;

        }
        return super.onOptionsItemSelected( item );
    }

    public void click(View view) {

    }
}
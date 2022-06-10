package com.k19.socialmediaapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.widget.FrameLayout;

import com.k19.socialmediaapp.Fragment.CreateAccountFragment;
import com.k19.socialmediaapp.Fragment.LoginFragment;

public class FragmentReplacerActivity extends AppCompatActivity {

    private FrameLayout frameLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fragment_replacer);

        frameLayout = findViewById(R.id.frameLayout);

        setFrameLayout(new LoginFragment());
    }

    public void setFrameLayout(Fragment fragment) {

        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
        if(fragment instanceof CreateAccountFragment) {
            fragmentTransaction.addToBackStack(null);
        }
        fragmentTransaction.replace(frameLayout.getId(), fragment);
        fragmentTransaction.commit();
    }
}
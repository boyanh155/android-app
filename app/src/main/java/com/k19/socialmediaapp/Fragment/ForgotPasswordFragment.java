package com.k19.socialmediaapp.Fragment;

import static com.k19.socialmediaapp.Fragment.CreateAccountFragment.validate;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.k19.socialmediaapp.FragmentReplacerActivity;
import com.k19.socialmediaapp.R;

public class ForgotPasswordFragment extends Fragment {

    private EditText emailET;
    private ProgressBar progressBar;
    private Button recoverBTN;
    private TextView loginTV;

    private FirebaseAuth auth;

    public ForgotPasswordFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_forgot_password, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        init(view);

        clickListener();
    }

    private void init(View view) {
        emailET = view.findViewById(R.id.emailET);
        progressBar = view.findViewById(R.id.progressBar);
        recoverBTN = view.findViewById(R.id.recoverBtn);
        loginTV = view.findViewById(R.id.loginTV);

        auth = FirebaseAuth.getInstance();
    }

    private void clickListener() {
        loginTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((FragmentReplacerActivity) getActivity()).setFrameLayout(new LoginFragment());
            }
        });

        recoverBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = emailET.getText().toString();

                if(email.isEmpty() || !validate(email)) {
                    emailET.setError("Input valid email");
                    return;
                }

                progressBar.setVisibility(View.VISIBLE);

                auth.sendPasswordResetEmail(email)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(getContext(), "Password reset email send successfully", Toast.LENGTH_SHORT).show();
                                    emailET.setText("");
                                } else {
                                    String errMsg = task.getException().getMessage();
                                    Toast.makeText(getContext(), errMsg, Toast.LENGTH_SHORT).show();
                                }

                                progressBar.setVisibility(View.GONE);
                            }
                        });
            }
        });
    }
}
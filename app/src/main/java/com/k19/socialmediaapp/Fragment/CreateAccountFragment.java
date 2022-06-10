package com.k19.socialmediaapp.Fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;
import com.k19.socialmediaapp.FragmentReplacerActivity;
import com.k19.socialmediaapp.MainActivity;
import com.k19.socialmediaapp.Models.User;
import com.k19.socialmediaapp.R;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CreateAccountFragment extends Fragment {

    private EditText nameET, emailET, passwordET, confirmPasswordET;
    private ProgressBar progressBar;
    private TextView loginTV;
    private Button signUpBtn;
    private FirebaseAuth auth;

    public static final Pattern VALID_EMAIL_ADDRESS_REGEX =
            Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);

    public CreateAccountFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_create_account, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        init(view);

        clickListener();
    }

    private void init(View view) {
        nameET = view.findViewById(R.id.nameET);
        emailET = view.findViewById(R.id.emailET);
        passwordET = view.findViewById(R.id.passwordET);
        confirmPasswordET = view.findViewById(R.id.confirmPassET);
        progressBar = view.findViewById(R.id.progressBar);
        loginTV = view.findViewById(R.id.loginTV);
        signUpBtn = view.findViewById(R.id.signUpBtn);

        auth = FirebaseAuth.getInstance();

    }

    private void clickListener() {

        loginTV.setOnClickListener(view -> ((FragmentReplacerActivity) getActivity()).setFrameLayout(new LoginFragment()));

        signUpBtn.setOnClickListener(view -> {

            String name = nameET.getText().toString();
            String email = emailET.getText().toString();
            String password = passwordET.getText().toString();
            String confirmPassword = confirmPasswordET.getText().toString();

            if(name.isEmpty() || name.equals(" ")) {
                nameET.setError("Please input valid name");
                return;
            }

            if(email.isEmpty() || !validate(email)) {
                emailET.setError("Please input valid email");
                return;
            }

            if(password.isEmpty() || password.length() < 6) {
                passwordET.setError("Please input valid password");
                return;
            }

            if(!password.equals(confirmPassword)) {
                confirmPasswordET.setError("Password not match");
                return;
            }

            progressBar.setVisibility(View.VISIBLE);

            createAccount(name, email, password);
        });
    }

    private void createAccount(String name, String email, String password) {
        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                         User User = new User(email,name);
                         String id=task.getResult().getUser().getUid();
                        FirebaseDatabase.getInstance().getReference().child("User").child(id).setValue(User);

                        FirebaseUser user = auth.getCurrentUser();

                        user.sendEmailVerification()
                                .addOnCompleteListener(task1 -> {
                                    if (task1.isSuccessful()) {
                                        Toast.makeText(getContext(), "Email verification link send", Toast.LENGTH_SHORT).show();
                                    }
                                });

                        uploadUser(user, name, email);

                    } else {
                        progressBar.setVisibility(View.GONE);
                        String exception = task.getException().getMessage();
                        Toast.makeText(getContext(), "Error: " + exception, Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void uploadUser(FirebaseUser user, String name, String email) {

        Map<String, Object> map = new HashMap<>();

        map.put("name", name);
        map.put("email", email);
        map.put("profileImage", " ");
        map.put("uid", user.getUid());
        map.put("following", 0);
        map.put("followers", 0);
        map.put("status", " ");

        FirebaseFirestore.getInstance().collection("Users").document(user.getUid())
                .set(map)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        assert getActivity() != null;
                        progressBar.setVisibility(View.GONE);
                        startActivity(new Intent(getActivity().getApplicationContext(), MainActivity.class));
                        getActivity().finish();
                    } else {
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(getContext(), "Error" + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    public static boolean validate(String emailStr) {
        Matcher matcher = VALID_EMAIL_ADDRESS_REGEX.matcher(emailStr);
        return matcher.find();
    }
}
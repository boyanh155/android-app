package com.k19.socialmediaapp.Fragment;

import static com.k19.socialmediaapp.Fragment.CreateAccountFragment.validate;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthCredential;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.FirebaseFirestore;
import com.k19.socialmediaapp.FragmentReplacerActivity;
import com.k19.socialmediaapp.MainActivity;
import com.k19.socialmediaapp.R;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class LoginFragment extends Fragment {

    private EditText emailET, passwordET;
    private TextView signUpTV, forgotPasswordTV;
    private Button loginBTN, googleSignInBTN;
    private LoginButton facebookSignInBTN;
    private ProgressBar progressBar;

    private static final int RC_SIGN_IN_GOOGLE = 1;
    private static final int RC_SIGN_IN_FACEBOOK = 2;
    private GoogleSignInClient gsc;

    private FirebaseAuth auth;

    private CallbackManager callbackManager;

    public LoginFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_login, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        init(view);

        clickListener();
    }

    private void init(View view) {
        emailET = view.findViewById(R.id.emailET);
        passwordET = view.findViewById(R.id.passwordET);
        loginBTN = view.findViewById(R.id.loginBtn);
        googleSignInBTN = view.findViewById(R.id.googleSignInBtn);
        facebookSignInBTN = view.findViewById(R.id.facebookSignInBtn);
        signUpTV = view.findViewById(R.id.signUpTV);
        forgotPasswordTV = view.findViewById(R.id.forgotTV);
        progressBar = view.findViewById(R.id.progressBar);

        auth = FirebaseAuth.getInstance();

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken("265537979726-eg0tlv4tgi2jgqiuea52lq28g60uq5fn.apps.googleusercontent.com")
                .requestEmail()
                .build();

        gsc = GoogleSignIn.getClient(getActivity(), gso);

        callbackManager = CallbackManager.Factory.create();
        facebookSignInBTN.setReadPermissions("email", "public_profile");
        facebookSignInBTN.registerCallback(callbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        handleFacebookAccessToken(loginResult.getAccessToken());
                    }

                    @Override
                    public void onCancel() {
                        Toast.makeText(getContext(), "Login cancelled", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onError(FacebookException exception) {
                        // App code
                    }
                });
    }

    private void clickListener() {
        loginBTN.setOnClickListener(view -> {
            String email = emailET.getText().toString();
            String password = passwordET.getText().toString();

            if(email.isEmpty() || !validate(email)) {
                emailET.setError("Input valid email");
                return;
            }

            if(password.isEmpty() || password.length() < 6) {
                passwordET.setError("Input 6 digit valid password");
                return;
            }

            progressBar.setVisibility(View.VISIBLE);

            auth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {

                            FirebaseUser user = auth.getCurrentUser();

                            if(!user.isEmailVerified()) {
                                Toast.makeText(getContext(), "Please verify your email", Toast.LENGTH_SHORT).show();
                            }

                            sendUserToMainActivity();

                        } else {
                            String exception = "Error" + task.getException().getMessage();
                            Toast.makeText(getContext(), exception, Toast.LENGTH_SHORT).show();
                            progressBar.setVisibility(View.GONE);
                        }
                    });
        });

        googleSignInBTN.setOnClickListener(view -> SignInGoogle());

        signUpTV.setOnClickListener(view -> ((FragmentReplacerActivity) getActivity()).setFrameLayout(new CreateAccountFragment()));

        forgotPasswordTV.setOnClickListener(view -> ((FragmentReplacerActivity) getActivity()).setFrameLayout(new ForgotPasswordFragment()));
    }

    private void sendUserToMainActivity() {

        if (getActivity() == null)
            return;

        progressBar.setVisibility(View.GONE);

        startActivity(new Intent(getActivity().getApplicationContext(), MainActivity.class));

        getActivity().finish();
    }

    private void SignInGoogle() {
        Intent intent = gsc.getSignInIntent();
        startActivityForResult(intent, RC_SIGN_IN_GOOGLE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == RC_SIGN_IN_GOOGLE) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                assert account != null;
                firebaseAuthWithGoogle(account.getIdToken());
            } catch (ApiException e) {
                Toast.makeText(getContext(), "Error", Toast.LENGTH_SHORT).show();
            }
        }

        callbackManager.onActivityResult(requestCode, resultCode, data);

    }

    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        auth.signInWithCredential(credential)
                .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser user = auth.getCurrentUser();
                            updateUI(user);
                        } else {
                            Log.w("TAG", "signInWithCredential:failure", task.getException());
                        }
                    }
                });
    }

    private void handleFacebookAccessToken(AccessToken accessToken) {
        AuthCredential credential = FacebookAuthProvider.getCredential(accessToken.getToken());
        auth.signInWithCredential(credential)
                .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(getContext(), "Login Succeed", Toast.LENGTH_SHORT).show();

                            sendUserToMainActivity();
                        } else {
                            Log.w("TAG", "signInWithCredential:failure", task.getException());
                        }
                    }
                });
    }

    private void updateUI(FirebaseUser user) {

        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(getActivity());

        Map<String, Object> map = new HashMap<>();

        map.put("name", account.getDisplayName());
        map.put("email", account.getEmail());
        map.put("profileImage", String.valueOf(account.getPhotoUrl()));
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
                        sendUserToMainActivity();
                    } else {
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(getContext(), "Error" + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

    }
}
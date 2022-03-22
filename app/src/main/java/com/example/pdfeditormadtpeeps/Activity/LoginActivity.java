package com.example.pdfeditormadtpeeps.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.LinearLayoutCompat;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.pdfeditormadtpeeps.R;
import com.example.pdfeditormadtpeeps.Utility.Utils;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

public class LoginActivity extends AppCompatActivity {
    TextView tvSignUp;
    Button btn_login;
    private FirebaseAuth auth;
    ProgressDialog progressDialog;

    //Login Authentication
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;
    private static final int RC_SIGN_IN = 9001;

    //Components
    private EditText et_username, et_pwd;
    LinearLayoutCompat ll_fb, ll_google;
    private GoogleSignInClient mGoogleSignInClient;
    private CallbackManager mCallbackManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.activity_login);
        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        //Get Firebase auth instance
        auth = FirebaseAuth.getInstance();
        if (auth.getCurrentUser() != null) {
            openMainActivity();
        }
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN | WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        mCallbackManager = CallbackManager.Factory.create();
        LoginButton loginButton = findViewById(R.id.login_button);
        loginButton.setReadPermissions("email", "public_profile");
        loginButton.registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.d("PDFEDITOR", "facebook:onSuccess:" + loginResult);
                handleFacebookAccessToken(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {
                Log.d("PDFEDITOR", "facebook:onCancel");
            }

            @Override
            public void onError(FacebookException error) {
                Log.d("PDFEDITOR", "facebook:onError", error);
            }
        });

        tvSignUp = findViewById(R.id.tvSignUp);
        btn_login = findViewById(R.id.btn_login);
        et_pwd = findViewById(R.id.et_pwd);
        et_username = findViewById(R.id.et_username);
        ll_fb = findViewById(R.id.ll_fb);
        ll_google = findViewById(R.id.ll_google);
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please wait...");

        tvSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(LoginActivity.this, SignUpActivity.class);
                startActivity(i);
                finish();
            }
        });

        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validateLogin();
            }
        });

        ll_fb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loginButton.performClick();
            }
        });

        ll_google.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent signInIntent = mGoogleSignInClient.getSignInIntent();
                startActivityForResult(signInIntent, RC_SIGN_IN);
            }
        });
    }

    private void validateLogin() {
        boolean isError = false;
        String email = et_username.getText().toString();
        final String password = et_pwd.getText().toString();
        if (TextUtils.isEmpty(et_username.getText().toString().trim())) {
            et_username.setError("Please enter username");
            isError = true;
        }
        if (TextUtils.isEmpty(et_pwd.getText().toString().trim())) {
            et_pwd.setError("Please enter password");
            isError = true;
        }
        if (isError) return;
//        User user = dao.login(et_username.getText().toString(), et_pwd.getText().toString());
//        if (user != null) {
//            Toast.makeText(getApplicationContext(), "Login Successfully!", Toast.LENGTH_LONG).show();
//            createLoginSession(et_username.getText().toString());
//            openMainActivity(user);
//        } else {
//            Toast.makeText(getApplicationContext(), "Login Failed!", Toast.LENGTH_LONG).show();
//        }
        progressDialog.show();
        //authenticate user
        auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        progressDialog.dismiss();
                        if (!task.isSuccessful()) {
                            // there was an error
                            if (password.length() < 6) {
                                et_pwd.setError("Password too short, enter minimum 6 characters!");
                            } else {
                                Toast.makeText(LoginActivity.this, "Authentication failed, check your email and password or sign up", Toast.LENGTH_LONG).show();
                            }
                        } else {
                            Toast.makeText(getApplicationContext(), "Login Successfully!", Toast.LENGTH_LONG).show();
                            openMainActivity();
                        }
                    }
                });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                Log.d("PDFEDITOR", "firebaseAuthWithGoogle:" + account.getId());
                firebaseAuthWithGoogle(account.getIdToken());
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Toast.makeText(LoginActivity.this, "Google SignIn failed", Toast.LENGTH_LONG).show();

            }
        } else {
            mCallbackManager.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void handleFacebookAccessToken(AccessToken token) {
        Log.d("PDFEDITOR", "handleFacebookAccessToken:" + token);

        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        auth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("PDFEDITOR", "signInWithCredential:success");
                            Toast.makeText(getApplicationContext(), "Login Successfully!", Toast.LENGTH_LONG).show();
                            openMainActivity();

                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("PDFEDITOR", "signInWithCredential:failure", task.getException());
                            Toast.makeText(LoginActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        auth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Toast.makeText(getApplicationContext(), "Login Successfully!", Toast.LENGTH_LONG).show();
                            openMainActivity();
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("PDFEDITOR", "signInWithCredential:failure", task.getException());
                            Toast.makeText(LoginActivity.this, "Google SignIn failed", Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    private void openMainActivity() {
        Intent loginSuccessIntent = new Intent(LoginActivity.this, MainActivity.class);
        loginSuccessIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        loginSuccessIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(loginSuccessIntent);
        finish();
    }
}
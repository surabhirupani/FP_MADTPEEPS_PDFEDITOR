package com.example.pdfeditormadtpeeps.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.pdfeditormadtpeeps.R;
import com.example.pdfeditormadtpeeps.Utility.Utils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.gson.Gson;

public class SignUpActivity extends AppCompatActivity {
    TextView tv_login;
    Button btn_register;
    private EditText et_fullname, et_username, et_email, et_pwd;
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;
    private FirebaseAuth auth;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        //Get Firebase auth instance
        auth = FirebaseAuth.getInstance();

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN | WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        preferences = getSharedPreferences(Utils.APP_NAME, MODE_PRIVATE);
        tv_login = findViewById(R.id.tv_login);
        btn_register = findViewById(R.id.btn_register);
        et_pwd = findViewById(R.id.et_pwd);
        et_fullname = findViewById(R.id.et_fullname);
        et_username = findViewById(R.id.et_username);
        et_email = findViewById(R.id.et_email);
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please wait...");

        initToolbar();

        tv_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(SignUpActivity.this, LoginActivity.class);
                startActivity(i);
                finish();
            }
        });

        btn_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validateFields();
            }
        });
    }

    private void validateFields() {
        boolean isError = false;
        if (TextUtils.isEmpty(et_fullname.getText().toString().trim())) {
            et_fullname.setError("Please enter full name");
            isError = true;
        }
        if (TextUtils.isEmpty(et_username.getText().toString().trim())) {
            et_username.setError("Please enter username");
            isError = true;
        }
        String email = et_email.getText().toString().trim();
        String password = et_pwd.getText().toString().trim();
        if (TextUtils.isEmpty(password)) {
            et_pwd.setError("Please enter password");
            isError = true;
        }
        if (TextUtils.isEmpty(email)) {
            et_email.setError("please enter email");
            isError = true;
        }

        if (password.length() < 6) {
            Toast.makeText(getApplicationContext(), "Password too short, enter minimum 6 characters!", Toast.LENGTH_SHORT).show();
            isError = true;
        }



        if (isError) return;
        progressDialog.show();
        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(SignUpActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        progressDialog.dismiss();
                        if (!task.isSuccessful()) {
                            Toast.makeText(SignUpActivity.this, "Authentication failed." + task.getException(),
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getApplicationContext(), "Sign Up Successfully!", Toast.LENGTH_LONG).show();
                            createLoginSession(et_username.getText().toString());
                            openMainActivity();
                        }
                    }
                });
    }

    private void openMainActivity() {
        Intent signupSuccessIntent = new Intent(SignUpActivity.this, MainActivity.class);
        signupSuccessIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        signupSuccessIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(signupSuccessIntent);
        finish();
    }


    public void createLoginSession(String userName) {
        editor = preferences.edit();
        editor.putBoolean(Utils.loginControlKey, true);
        editor.putString(Utils.loginUserNameKey, userName);
        editor.apply();
    }

    private void initToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(null);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            Intent i = new Intent(SignUpActivity.this, LoginActivity.class);
            startActivity(i);
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
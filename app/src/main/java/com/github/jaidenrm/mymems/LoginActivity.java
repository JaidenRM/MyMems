package com.github.jaidenrm.mymems;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    //UI
    EditText username;
    EditText password;
    Button loginButton;
    ProgressBar loading;

    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mAuth = FirebaseAuth.getInstance();

        username = findViewById(R.id.login_text_username);
        password = findViewById(R.id.login_text_password);
        loginButton = findViewById(R.id.login_button_login);
        loading = findViewById(R.id.login_loading);
        loading.setVisibility(View.INVISIBLE);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(username.getText().length() != 0 && password.getText().length() != 0) {
                    loginButton.setVisibility(View.INVISIBLE);
                    loading.setVisibility(View.VISIBLE);
                    mAuth.signInWithEmailAndPassword(
                            username.getText().toString(),
                            password.getText().toString())
                            .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                                @Override
                                public void onSuccess(AuthResult authResult) {
                                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                    startActivity(intent);
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            CreateAlertDialog();
                        }
                    });
                }
                else {
                    Toast.makeText(LoginActivity.this, "Please enter an email AND password!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void CreateAlertDialog() {
        AlertDialog.Builder builder;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder = new AlertDialog.Builder(this, android.R.style.Theme_DeviceDefault_Light_Dialog_NoActionBar);
        } else {
            builder = new AlertDialog.Builder(this);
        }
        builder.setTitle("Login Error")
                .setMessage("Your email/password doesn't match any of our records." +
                        " Would you like to sign up with these credentials?")
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // sign up user
                        if(username.getText().length() != 0 && password.getText().length() != 0) {
                            mAuth.createUserWithEmailAndPassword(
                                    username.getText().toString(),
                                    password.getText().toString())
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(LoginActivity.this,
                                                    "Those details already match a current user or are incorrect! Please try again",
                                                    Toast.LENGTH_SHORT).show();
                                        }
                                    })
                                    .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                                        @Override
                                        public void onSuccess(AuthResult authResult) {
                                            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                            startActivity(intent);
                                        }
                                    });
                        }
                        else{
                            Toast.makeText(LoginActivity.this,
                                    "Empty email/password!",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // do nothing
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }
}

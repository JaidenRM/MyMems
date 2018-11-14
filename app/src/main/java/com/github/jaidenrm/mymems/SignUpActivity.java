package com.github.jaidenrm.mymems;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.ZonedDateTime;
import java.util.Date;

public class SignUpActivity extends AppCompatActivity {

    //UI
    private EditText firstName;
    private EditText lastName;
    private EditText username;
    private Button signup;
    private ProgressBar loading;

    //firebase
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        InitUI();
    }

    private void InitUI() {

        firstName = findViewById(R.id.signup_firstName);
        lastName = findViewById(R.id.signup_lastName);
        username = findViewById(R.id.signup_username);
        signup = findViewById(R.id.signup_button_signup);
        loading = findViewById(R.id.signup_loading);

        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signup.setVisibility(View.INVISIBLE);
                loading.setVisibility(View.VISIBLE);
                User newUser =
                        new User(firstName.getText().toString(),
                                lastName.getText().toString(),
                                username.getText().toString(),
                                mAuth.getCurrentUser().getEmail(),
                                mAuth.getUid()
                        );
                db.collection("users").document(mAuth.getUid())
                        .set(newUser)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful()) {
                                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                    startActivity(intent);
                                }
                                else {
                                    Toast.makeText(SignUpActivity.this, "Sign Up FAILED :(", Toast.LENGTH_SHORT).show();
                                    signup.setVisibility(View.VISIBLE);
                                    loading.setVisibility(View.INVISIBLE);
                                }
                            }
                        });
            }
        });
    }



}

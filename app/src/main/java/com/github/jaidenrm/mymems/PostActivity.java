package com.github.jaidenrm.mymems;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class PostActivity extends AppCompatActivity {

    private EditText title;
    private TextView description;
    private ImageButton camera;
    private Button submit;
    private ProgressBar loading;

    private FirebaseFirestore db;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        InitUI();
    }

    private void InitUI() {
        title = findViewById(R.id.post_title);
        description = findViewById(R.id.post_description);
        camera = findViewById(R.id.post_camera_button);
        submit = findViewById(R.id.post_submit_button);
        loading = findViewById(R.id.post_loading);

        camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        //TODO: work out camera photos to fix the null value
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loading.setVisibility(View.VISIBLE);
                submit.setVisibility(View.INVISIBLE);
                Post newPost =
                        new Post(title.getText().toString(),
                                 description.getText().toString(),
                                 null);
                db.collection("posts").document(mAuth.getUid())
                        .set(newPost)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful()) {
                                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                    startActivity(intent);
                                }
                                else {
                                    Toast.makeText(PostActivity.this, "Post unsuccessful :(", Toast.LENGTH_SHORT).show();
                                    loading.setVisibility(View.INVISIBLE);
                                    submit.setVisibility(View.VISIBLE);
                                }
                            }
                        });

            }
        });
    }
}

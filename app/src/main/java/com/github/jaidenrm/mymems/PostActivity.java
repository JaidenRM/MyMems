package com.github.jaidenrm.mymems;

import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class PostActivity extends AppCompatActivity {

    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private final String TAG = "Post-Activity";

    private EditText title;
    private TextView description;
    private ImageButton camera;
    private Button submit;
    private ProgressBar loading;
    private ImageView testImg;

    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private StorageReference storageRef;

    private Uri filePath;
    private String mCurrentPhotoPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        storageRef = FirebaseStorage.getInstance().getReference();

        InitUI();
    }

    private void TakePhoto() throws IOException {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        //this is to prevent app crashing as startActForRes can crash on null
        if(intent.resolveActivity(getPackageManager()) != null) {
            File photoFile = null;
            try {
                photoFile = CreateImageFile();
            } catch (IOException e) {
                Log.e(TAG, "TakePhoto: ", e);
            }
            if(photoFile != null) {
                filePath = FileProvider.getUriForFile(this,
                                "com.github.jaidenrm.camera_fp",
                                          photoFile);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, filePath);
                startActivityForResult(intent, REQUEST_IMAGE_CAPTURE);
            }

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            //TODO: move this elsewhere so it triggers when SUBMIT is clicked
            storageRef.child("images/"+filePath.getLastPathSegment()).putFile(filePath)
                    .addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                            if(task.isSuccessful()) {
                                Toast.makeText(PostActivity.this, "SUCCESSFUL upload", Toast.LENGTH_SHORT);
                            }
                            else {
                                Toast.makeText(PostActivity.this, "UNSUCCESSFUL upload", Toast.LENGTH_SHORT);
                            }
                        }
                    });
        }
    }

    private File CreateImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,
                ".jpg",
                storageDir
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }

    private void InitUI() {
        title = findViewById(R.id.post_title);
        description = findViewById(R.id.post_description);
        camera = findViewById(R.id.post_camera_button);
        submit = findViewById(R.id.post_submit_button);
        loading = findViewById(R.id.post_loading);
        testImg = findViewById(R.id.post_test_image);

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
                        new Post(mAuth.getUid(),
                                 title.getText().toString(),
                                 description.getText().toString(),
                                 null);
                db.collection("posts").document()
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

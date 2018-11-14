package com.github.jaidenrm.mymems;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
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

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
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
    private static final int REQUEST_LOCATION = 2;
    //if noone has asked for our location we will ask for after this time
    private final long UPDATE_INTERVAL = 10 * 1000; /* 10 * 1 sec */
    //if someone else has updated location after this time, lets use it, might as well
    private final long FASTEST_INTERVAL = 2 * 1000;
    private final String TAG = "Post-Activity";

    private EditText title;
    private TextView description;
    private ImageButton camera;
    private Button submit;
    private ProgressBar loading;
    private ImageView testImg;

    private FirebaseFirestore db;
    private FirebaseAuth auth;
    private StorageReference storageRef;

    private Uri filePath;
    private String currentPhotoPath;
    private FusedLocationProviderClient fusedLocationClient;
    private LocationRequest locationRequest;
    private Location lastKnownLocation;

    @SuppressLint("MissingPermission")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        storageRef = FirebaseStorage.getInstance().getReference();
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        StartLocationUpdates();
        InitUI();
    }

    private void TakePhoto() throws IOException {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        //this is to prevent app crashing as startActForRes can crash on null
        if (intent.resolveActivity(getPackageManager()) != null) {
            File photoFile = null;
            try {
                photoFile = CreateImageFile();
            } catch (IOException e) {
                Log.e(TAG, "TakePhoto: ", e);
            }
            if (photoFile != null) {
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
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            //TODO: move this elsewhere so it triggers when SUBMIT is clicked

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
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }

    private void StartLocationUpdates() {
        //start location requests
        locationRequest = new LocationRequest();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(UPDATE_INTERVAL);
        locationRequest.setFastestInterval(FASTEST_INTERVAL);
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
                try {
                    TakePhoto();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        //TODO: work out camera photos to fix the null value
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loading.setVisibility(View.VISIBLE);
                submit.setVisibility(View.INVISIBLE);

                if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getApplicationContext(),
                        Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    fusedLocationClient.getLastLocation()
                            .addOnCompleteListener(new OnCompleteListener<Location>() {
                                @Override
                                public void onComplete(@NonNull Task<Location> task) {
                                    if(task.isSuccessful()) {
                                        lastKnownLocation = task.getResult();
                                        Log.e(TAG, "onComplete: " + task.getResult().toString() );
                                    }
                                }
                            });
                }
                else {
                    ActivityCompat.requestPermissions(PostActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                                            Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION);
                }
                //TODO: work out a check for filePath being null (when user doesn't upload photo
                final StorageReference imageRef = storageRef.child("images/"+filePath.getLastPathSegment());
                imageRef.putFile(filePath)
                        .addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                                if(task.isSuccessful()) {
                                    imageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                        @Override
                                        public void onSuccess(Uri uri) {
                                            Post newPost =
                                                    new Post(auth.getUid(),
                                                            title.getText().toString(),
                                                            description.getText().toString(),
                                                            uri.toString(),
                                                            lastKnownLocation);
                                            Toast.makeText(PostActivity.this, "SUCCESSFUL upload", Toast.LENGTH_SHORT);
                                            db.collection("posts").document()
                                                    .set(newPost)
                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {
                                                            if (task.isSuccessful()) {
                                                                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                                                startActivity(intent);
                                                            } else {
                                                                Toast.makeText(PostActivity.this, "Post unsuccessful :(", Toast.LENGTH_SHORT).show();
                                                                loading.setVisibility(View.INVISIBLE);
                                                                submit.setVisibility(View.VISIBLE);
                                                            }
                                                        }
                                                    });
                                        }
                                    });

                                }
                                else {
                                    Toast.makeText(PostActivity.this, "UNSUCCESSFUL upload", Toast.LENGTH_SHORT);
                                }
                            }
                        });
            }
        });
    }
}

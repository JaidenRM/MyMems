package com.github.jaidenrm.mymems;

import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnInfoWindowClickListener {

    private final String TAG = "Maps-Activity";

    private GoogleMap mMap;
    private List<Post> myPosts;
    private HashMap<String, String> idToImage;

    private FirebaseFirestore db;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        myPosts = new ArrayList<>();
        idToImage = new HashMap<>();
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(-34, 151);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
        GetPostsInfo();
    }

    //finds all user posts and gathers the information needed from them to be used on the markers
        //fetches title, description and image link
    private void GetPostsInfo() {
        db.collection("posts").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()) {
                    for(QueryDocumentSnapshot documentSnapshot : task.getResult()) {
                        Log.e(TAG, documentSnapshot.getString("userID") + " vs " + auth.getUid());
                        if(documentSnapshot.getString("userID").equals(auth.getUid())) {
                            Post myPost = documentSnapshot.toObject(Post.class);
                            AddMarkers(myPost);
                        }
                    }
                }
                else {
                    Log.d(TAG, "Error getting documents: ", task.getException());
                }
            }
        });
    }

    /**
     * Uses the information from GetPostsInfo() to create markers to the map
     */
    private void AddMarkers(Post post) {
        Log.e(TAG, post.picture);
        MarkerOptions myMarkerOptions =
            new MarkerOptions()
                .position(new LatLng(post.myLoc.getLatitude(), post.myLoc.getLongitude()))
                .title(post.title)
                .snippet(post.description);
        Marker myMarker = mMap.addMarker(myMarkerOptions);
        idToImage.put(myMarker.getId(), post.picture);
        mMap.setInfoWindowAdapter(new CustomInfoWindowAdapter(MapsActivity.this, idToImage));
    }

    @Override
    public void onInfoWindowClick(Marker marker) {
    }
}

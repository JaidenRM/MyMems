package com.github.jaidenrm.mymems;

import android.location.Location;
import android.media.Image;

import com.google.firebase.firestore.GeoPoint;

import java.sql.Timestamp;
import java.util.Date;

public class Post {

    public String title;
    public String description;
    public String picture;
    public Date dateCreated;
    public String userID;
    public GeoPoint myLoc;

    public Post(String userID, String title, String description, String picture, Location myLoc) {
        this.userID = userID;
        this.title = title;
        this.description = description;
        this.picture = picture;
        this.myLoc = new GeoPoint(myLoc.getLatitude(), myLoc.getLongitude());
        this.dateCreated = new Timestamp(new Date().getTime());
    }

    public Post() {}
}

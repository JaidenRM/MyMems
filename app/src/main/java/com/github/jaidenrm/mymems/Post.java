package com.github.jaidenrm.mymems;

import android.media.Image;

import java.sql.Timestamp;
import java.util.Date;

public class Post {

    public String title;
    public String description;
    public String picture;
    public Timestamp dateCreated;
    public String userID;

    public Post(String userID, String title, String description, String picture) {
        this.userID = userID;
        this.title = title;
        this.description = description;
        this.picture = picture;
        this.dateCreated = new Timestamp(new Date().getTime());
    }
}

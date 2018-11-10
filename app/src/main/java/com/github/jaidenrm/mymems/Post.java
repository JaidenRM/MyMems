package com.github.jaidenrm.mymems;

import android.media.Image;

import java.sql.Timestamp;
import java.util.Date;

public class Post {

    private String title;
    private String description;
    private String[] picture;
    private Timestamp dateCreated;

    public Post(String title, String description, String[] picture) {
        this.title = title;
        this.description = description;
        this.picture = picture;
        this.dateCreated = new Timestamp(new Date().getTime());
    }
}

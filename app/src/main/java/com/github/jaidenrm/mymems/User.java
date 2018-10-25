package com.github.jaidenrm.mymems;

import java.sql.Timestamp;
import java.util.Date;

public class User {
    public String firstName;
    public String lastName;
    public String username;
    public String email;
    public String userID;
    Timestamp dateCreated;

    public User() {}

    public User(String firstName, String lastName, String username, String email, String userID) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.username = username;
        this.email = email;
        this.userID = userID;
        this.dateCreated = new Timestamp(new Date().getTime());
    }

}
package com.example.hd.model;

public class FeedbackRequest {
    public String uid;
    public String username;
    public String comment;

    public FeedbackRequest(String uid, String username, String comment) {
        this.uid = uid;
        this.username = username;
        this.comment = comment;
    }
}


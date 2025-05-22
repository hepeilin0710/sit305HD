package com.example.hd.model;

public class AskRequest {
    public String uid;
    public String username;
    public String goal;
    public String preference;
    public String allergy;
    public String question;

    public AskRequest(String uid, String username, String goal, String preference, String allergy, String question) {
        this.uid = uid;
        this.username = username;
        this.goal = goal;
        this.preference = preference;
        this.allergy = allergy;
        this.question = question;
    }
}

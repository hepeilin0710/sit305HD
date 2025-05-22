package com.example.hd.model;

public class GoalRequest {
    public String uid;
    public String username;
    public String goal_description;

    public GoalRequest(String uid, String username, String goal_description) {
        this.uid = uid;
        this.username = username;
        this.goal_description = goal_description;
    }
}

package com.example.hd.model;

public class LogMealRequest {
    public String uid;
    public String username;
    public String goal;
    public String meal_content;

    public LogMealRequest(String uid, String username, String goal, String meal_content) {
        this.uid = uid;
        this.username = username;
        this.goal = goal;
        this.meal_content = meal_content;
    }
}


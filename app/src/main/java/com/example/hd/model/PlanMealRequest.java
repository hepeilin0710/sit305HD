package com.example.hd.model;

public class PlanMealRequest {
    public String uid;
    public String username;
    public String goal;
    public String preference;
    public String allergy;
    public String mood;
    public String custom_preference;

    public PlanMealRequest(String uid, String username, String goal, String preference,
                           String allergy, String mood, String custom_preference) {
        this.uid = uid;
        this.username = username;
        this.goal = goal;
        this.preference = preference;
        this.allergy = allergy;
        this.mood = mood;
        this.custom_preference = custom_preference;
    }
}


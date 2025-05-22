package com.example.hd.network;

import com.example.hd.model.AskRequest;
import com.example.hd.model.AskResponse;
import com.example.hd.model.FeedbackRequest;
import com.example.hd.model.FeedbackResponse;
import com.example.hd.model.GoalRequest;
import com.example.hd.model.GoalResponse;
import com.example.hd.model.LogMealRequest;
import com.example.hd.model.LogMealResponse;
import com.example.hd.model.PlanMealRequest;
import com.example.hd.model.PlanMealResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface ApiService {
    @POST("/set-goal")
    Call<GoalResponse> setGoal(@Body GoalRequest request);
    @POST("/plan-meal")
    Call<PlanMealResponse> planMeal(@Body PlanMealRequest request);

    @POST("/log-meal")
    Call<LogMealResponse> logMeal(@Body LogMealRequest request);

    @POST("/feedback")
    Call<FeedbackResponse> sendFeedback(@Body FeedbackRequest request);

    @POST("/ask")
    Call<AskResponse> askQuestion(@Body AskRequest request);




}

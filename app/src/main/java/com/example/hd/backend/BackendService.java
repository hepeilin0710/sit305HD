package com.example.hd.backend;

import android.content.Context;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.hd.model.AskRequest;
import com.example.hd.model.AskResponse;
import com.example.hd.model.FeedbackRequest;
import com.example.hd.model.FeedbackResponse;
import com.example.hd.model.GoalRequest;
import com.example.hd.model.GoalResponse;
import com.example.hd.model.GoalAdvice;
import com.example.hd.model.LogMealRequest;
import com.example.hd.model.LogMealResponse;
import com.example.hd.model.PlanMealRequest;
import com.example.hd.model.PlanMealResponse;
import com.example.hd.network.ApiService;
import com.example.hd.network.RetrofitClient;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.*;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BackendService {

    private final FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final ApiService apiService = RetrofitClient.getInstance().create(ApiService.class);

    public interface GoalCallback {
        void onGoalResponse(String formattedAdviceText);
    }

    public interface PlanMealCallback {
        void onPlanReceived(PlanMealResponse response);
    }

    public interface LogMealCallback {
        void onMealAnalyzed(LogMealResponse response);
    }

    public interface FeedbackCallback {
        void onFeedbackResponse(String insightText);
    }

    public interface AskCallback {
        void onReplyReceived(String answer);
    }

    public void requestAsk(String question, Context context, AskCallback callback) {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String uid = auth.getCurrentUser().getUid();

        db.collection("users").document(uid).get()
                .addOnSuccessListener(doc -> {
                    String username = doc.getString("username");
                    String preference = doc.getString("preference");
                    String allergy = doc.getString("allergy");

                    String goal = "";
                    if (doc.contains("goals")) {
                        List<Map<String, Object>> goals = (List<Map<String, Object>>) doc.get("goals");
                        if (goals != null && !goals.isEmpty()) {
                            goal = (String) goals.get(goals.size() - 1).get("goal");
                        }
                    }

                    AskRequest request = new AskRequest(uid, username, goal, preference, allergy, question);
                    ApiService api = RetrofitClient.getInstance().create(ApiService.class);

                    api.askQuestion(request).enqueue(new Callback<AskResponse>() {
                        @Override
                        public void onResponse(Call<AskResponse> call, Response<AskResponse> response) {
                            if (response.isSuccessful() && response.body() != null) {
                                callback.onReplyReceived(response.body().reply);
                            } else {
                                Toast.makeText(context, "Failed to get response", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<AskResponse> call, Throwable t) {
                            Toast.makeText(context, "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                });
    }


    public void sendFeedback(String comment, Context context, FeedbackCallback callback) {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String uid = auth.getCurrentUser().getUid();

        db.collection("users").document(uid).get()
                .addOnSuccessListener(doc -> {
                    String username = doc.getString("username");

                    FeedbackRequest request = new FeedbackRequest(uid, username, comment);

                    ApiService api = RetrofitClient.getInstance().create(ApiService.class);
                    api.sendFeedback(request).enqueue(new Callback<FeedbackResponse>() {
                        @Override
                        public void onResponse(Call<FeedbackResponse> call, Response<FeedbackResponse> response) {
                            if (response.isSuccessful() && response.body() != null) {


                                Map<String, Object> entry = new HashMap<>();
                                entry.put("comment", comment);

                                db.collection("users").document(uid)
                                        .update("feedback", FieldValue.arrayUnion(entry))
                                        .addOnSuccessListener(unused -> {
                                            callback.onFeedbackResponse(response.body().insight);
                                        });

                            } else {
                                Toast.makeText(context, "Failed to send feedback", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<FeedbackResponse> call, Throwable t) {
                            Toast.makeText(context, "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                });
    }


    public void requestLogMeal(String mealContent, Context context, LogMealCallback callback) {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String uid = auth.getCurrentUser().getUid();

        db.collection("users").document(uid).get()
                .addOnSuccessListener(doc -> {
                    String username = doc.getString("username");

                    String goal = "";
                    if (doc.contains("goals")) {
                        List<Map<String, Object>> goals = (List<Map<String, Object>>) doc.get("goals");
                        if (goals != null && !goals.isEmpty()) {
                            goal = (String) goals.get(goals.size() - 1).get("goal");
                        }
                    }

                    LogMealRequest request = new LogMealRequest(uid, username, goal, mealContent);

                    ApiService api = RetrofitClient.getInstance().create(ApiService.class);
                    api.logMeal(request).enqueue(new Callback<LogMealResponse>() {
                        @Override
                        public void onResponse(Call<LogMealResponse> call, Response<LogMealResponse> response) {
                            if (response.isSuccessful() && response.body() != null) {

                                Map<String, Object> logEntry = new HashMap<>();
                                logEntry.put("content", mealContent);
                                logEntry.put("time", new Date());

                                db.collection("users").document(uid)
                                        .update("meal_logs", FieldValue.arrayUnion(logEntry));

                                callback.onMealAnalyzed(response.body());
                            } else {
                                Toast.makeText(context, "Failed to analyze meal", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<LogMealResponse> call, Throwable t) {
                            Toast.makeText(context, "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                });
    }



    public void requestPlanMeal(String uid, String username, String goal, String preference, String allergy,
                                String mood, String customPreference, Context context, PlanMealCallback callback) {

        PlanMealRequest request = new PlanMealRequest(uid, username, goal, preference, allergy, mood, customPreference);

        ApiService api = RetrofitClient.getInstance().create(ApiService.class);
        api.planMeal(request).enqueue(new Callback<PlanMealResponse>() {
            @Override
            public void onResponse(Call<PlanMealResponse> call, Response<PlanMealResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onPlanReceived(response.body());
                } else {
                    Toast.makeText(context, "Meal plan failed", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<PlanMealResponse> call, Throwable t) {
                Toast.makeText(context, "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


    public void submitGoal(String goalText, Context context, GoalCallback callback) {
        String uid = mAuth.getCurrentUser().getUid();

        db.collection("users").document(uid).get()
                .addOnSuccessListener(doc -> {
                    String username = doc.getString("username");

                    GoalRequest request = new GoalRequest(uid, username, goalText);
                    apiService.setGoal(request).enqueue(new Callback<GoalResponse>() {
                        @Override
                        public void onResponse(@NonNull Call<GoalResponse> call, @NonNull Response<GoalResponse> response) {
                            if (response.isSuccessful() && response.body() != null) {
                                GoalAdvice advice = response.body().goal_advice;

                                StringBuilder sb = new StringBuilder();
                                sb.append("💡 ").append(advice.explanation).append("\n\nStrategies:\n");
                                for (String s : advice.strategies) {
                                    sb.append("• ").append(s).append("\n");
                                }
                                String adviceText = sb.toString();

                                Map<String, Object> goalItem = new HashMap<>();
                                goalItem.put("goal", goalText);
                                goalItem.put("goal_advice", adviceText);

                                db.collection("users").document(uid)
                                        .update("goals", FieldValue.arrayUnion(goalItem))
                                        .addOnSuccessListener(unused -> {
                                            Toast.makeText(context, "✅ Goal saved!", Toast.LENGTH_SHORT).show();
                                            callback.onGoalResponse(adviceText);
                                        })
                                        .addOnFailureListener(e ->
                                                Toast.makeText(context, "❌ Save failed: " + e.getMessage(), Toast.LENGTH_SHORT).show()
                                        );
                            } else {
                                Toast.makeText(context, "❌ API response error", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(@NonNull Call<GoalResponse> call, @NonNull Throwable t) {
                            Toast.makeText(context, "❌ Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                });
    }
}

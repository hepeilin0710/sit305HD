package com.example.hd;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.*;

import androidx.appcompat.app.AppCompatActivity;

import com.example.hd.backend.BackendService;
import com.example.hd.model.PlanMealResponse;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.*;

import java.util.List;
import java.util.Map;

public class PlanMealActivity extends AppCompatActivity {

    private EditText editPreference, editAllergy, editMood, editCustom;
    private Button btnSaveProfile, btnGenerate;
    private TextView textMealPlan;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private String uid;
    private String username, goal;

    private BackendService backendService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plan_meal);

        editPreference = findViewById(R.id.editPreference);
        editAllergy = findViewById(R.id.editAllergy);
        editMood = findViewById(R.id.editMood);
        editCustom = findViewById(R.id.editCustom);
        btnSaveProfile = findViewById(R.id.btnSaveProfile);
        btnGenerate = findViewById(R.id.btnGenerate);
        textMealPlan = findViewById(R.id.textMealPlan);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        backendService = new BackendService();
        uid = mAuth.getCurrentUser().getUid();

        // 🔄 读取用户信息
        db.collection("users").document(uid).get()
                .addOnSuccessListener(doc -> {
                    username = doc.getString("username");
                    if (doc.contains("goals")) {
                        List<Map<String, Object>> goals = (List<Map<String, Object>>) doc.get("goals");
                        if (goals != null && !goals.isEmpty()) {
                            goal = (String) goals.get(goals.size() - 1).get("goal");
                        } else {
                            goal = "";
                            Toast.makeText(this, "⚠️ No goals found. Please set a goal first.", Toast.LENGTH_LONG).show();
                        }
                    } else {
                        goal = "";
                        Toast.makeText(this, "⚠️ No goals field. Please set a goal first.", Toast.LENGTH_LONG).show();
                    }
                    editPreference.setText(doc.getString("preference"));
                    editAllergy.setText(doc.getString("allergy"));
                });

        // ✍️ 保存用户修改的偏好和过敏信息
        btnSaveProfile.setOnClickListener(v -> {
            String newPref = editPreference.getText().toString().trim();
            String newAllergy = editAllergy.getText().toString().trim();

            db.collection("users").document(uid)
                    .update("preference", newPref, "allergy", newAllergy)
                    .addOnSuccessListener(aVoid ->
                            Toast.makeText(this, "Saved successfully!", Toast.LENGTH_SHORT).show())
                    .addOnFailureListener(e ->
                            Toast.makeText(this, "Failed to save: " + e.getMessage(), Toast.LENGTH_SHORT).show());
        });

        Button btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> {
            startActivity(new Intent(this, HomeActivity.class));
            finish();
        });


        // 📡 生成膳食建议
        btnGenerate.setOnClickListener(v -> {
            String pref = editPreference.getText().toString().trim();
            String allergy = editAllergy.getText().toString().trim();
            String mood = editMood.getText().toString().trim();
            String custom = editCustom.getText().toString().trim();

            if (TextUtils.isEmpty(pref) || TextUtils.isEmpty(username)) {
                Toast.makeText(this, "User info missing", Toast.LENGTH_SHORT).show();
                return;
            }

            backendService.requestPlanMeal(uid, username, goal, pref, allergy, mood, custom, this, result -> {
                StringBuilder sb = new StringBuilder();
                sb.append("🥣 Breakfast: ").append(result.breakfast).append("\n\n");
                sb.append("🥗 Lunch: ").append(result.lunch).append("\n\n");
                sb.append("🍛 Dinner: ").append(result.dinner).append("\n\n");
                sb.append("🍎 Snacks: ").append(result.snacks).append("\n\n");
                sb.append("💡 Advice: ").append(result.advice);

                textMealPlan.setText(sb.toString());
                textMealPlan.setVisibility(View.VISIBLE);
            });
        });
    }
}


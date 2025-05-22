package com.example.hd;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.*;

import androidx.appcompat.app.AppCompatActivity;

import com.example.hd.backend.BackendService;
import com.example.hd.model.LogMealResponse;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.*;

import java.text.SimpleDateFormat;
import java.util.*;

public class LogMealActivity extends AppCompatActivity {

    private EditText editMealContent;
    private Button btnSubmitMeal, btnBack, btnShowHistory;
    private TextView textAnalysis;
    private LinearLayout layoutHistory;

    private BackendService backendService;
    private FirebaseFirestore db;
    private String uid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_meal);

        editMealContent = findViewById(R.id.editMealContent);
        btnSubmitMeal = findViewById(R.id.btnSubmitMeal);
        btnBack = findViewById(R.id.btnBack);
        btnShowHistory = findViewById(R.id.btnShowHistory);
        textAnalysis = findViewById(R.id.textAnalysis);
        layoutHistory = findViewById(R.id.layoutHistory);

        backendService = new BackendService();
        db = FirebaseFirestore.getInstance();
        uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // 🔙 返回按钮
        btnBack.setOnClickListener(v -> {
            startActivity(new Intent(this, HomeActivity.class));
            finish();
        });

        // 📤 提交饮食内容
        btnSubmitMeal.setOnClickListener(v -> {
            String content = editMealContent.getText().toString().trim();
            if (TextUtils.isEmpty(content)) {
                Toast.makeText(this, "Please enter your meal log.", Toast.LENGTH_SHORT).show();
                return;
            }

            backendService.requestLogMeal(content, this, response -> {
                StringBuilder sb = new StringBuilder();
                sb.append("🧾 Summary:\n").append(response.summary).append("\n\n");
                sb.append("⚠️ Issues:\n").append(response.issues).append("\n\n");
                sb.append("✅ Suggestions:\n").append(response.suggestions);

                textAnalysis.setText(sb.toString());
                textAnalysis.setVisibility(View.VISIBLE);
            });
        });

        // 🕘 查看历史记录
        btnShowHistory.setOnClickListener(v -> {
            layoutHistory.removeAllViews();
            db.collection("users").document(uid).get()
                    .addOnSuccessListener(doc -> {
                        if (doc.contains("meal_logs")) {
                            List<Map<String, Object>> logs = (List<Map<String, Object>>) doc.get("meal_logs");
                            if (logs == null || logs.isEmpty()) {
                                Toast.makeText(this, "No meal history.", Toast.LENGTH_SHORT).show();
                                return;
                            }

                            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());

                            for (Map<String, Object> log : logs) {
                                String content = (String) log.get("content");
                                Date time = log.get("time") instanceof Timestamp ?
                                        ((Timestamp) log.get("time")).toDate() : new Date();

                                String label = "🍽 " + sdf.format(time) + "\n" + content;

                                TextView text = new TextView(this);
                                text.setText(label);
                                text.setPadding(16, 16, 16, 16);
                                text.setTextSize(15f);
                                text.setBackgroundColor(0xFFF4F6F8);

                                Button deleteBtn = new Button(this);
                                deleteBtn.setText("Delete");
                                deleteBtn.setBackgroundColor(0xFFE57373);
                                deleteBtn.setTextColor(0xFFFFFFFF);

                                deleteBtn.setOnClickListener(del -> {
                                    db.collection("users").document(uid)
                                            .update("meal_logs", FieldValue.arrayRemove(log))
                                            .addOnSuccessListener(vv -> {
                                                Toast.makeText(this, "Deleted", Toast.LENGTH_SHORT).show();
                                                layoutHistory.removeView(text);
                                                layoutHistory.removeView(deleteBtn);
                                            });
                                });

                                layoutHistory.addView(text);
                                layoutHistory.addView(deleteBtn);
                            }

                            layoutHistory.setVisibility(View.VISIBLE);
                        } else {
                            Toast.makeText(this, "No meal logs found.", Toast.LENGTH_SHORT).show();
                        }
                    });
        });
    }
}


package com.example.hd;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.view.View;
import android.widget.*;

import androidx.appcompat.app.AppCompatActivity;

import com.example.hd.backend.BackendService;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.*;

import java.util.List;
import java.util.Map;

public class SetGoalActivity extends AppCompatActivity {

    private EditText editGoal;
    private TextView textAdvice;
    private Button btnSubmit, btnBack, btnShowHistory;
    private LinearLayout layoutHistory;

    private BackendService backendService;
    private FirebaseFirestore db;
    private String uid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_goal);

        editGoal = findViewById(R.id.editGoal);
        textAdvice = findViewById(R.id.textAdvice);
        btnSubmit = findViewById(R.id.btnSubmitGoal);
        btnBack = findViewById(R.id.btnBack);
        btnShowHistory = findViewById(R.id.btnShowHistory);
        layoutHistory = findViewById(R.id.layoutHistory);

        backendService = new BackendService();
        db = FirebaseFirestore.getInstance();
        uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        btnSubmit.setOnClickListener(v -> {
            String goal = editGoal.getText().toString().trim();
            if (TextUtils.isEmpty(goal)) {
                Toast.makeText(this, "Please enter your goal", Toast.LENGTH_SHORT).show();
                return;
            }

            backendService.submitGoal(goal, this, adviceText -> {
                textAdvice.setVisibility(View.VISIBLE);
                animateTyping(textAdvice, adviceText, 25);
            });
        });

        btnBack.setOnClickListener(v -> {
            startActivity(new Intent(this, HomeActivity.class));
            finish();
        });

        btnShowHistory.setOnClickListener(v -> {
            layoutHistory.removeAllViews();
            db.collection("users").document(uid).get()
                    .addOnSuccessListener(doc -> {
                        if (doc.contains("goals")) {
                            List<Map<String, Object>> goals = (List<Map<String, Object>>) doc.get("goals");

                            for (Map<String, Object> goalItem : goals) {
                                String goal = (String) goalItem.get("goal");
                                String advice = (String) goalItem.get("goal_advice");
                                String fullText = "• " + goal + "\n\n" + advice;

                                TextView item = new TextView(this);
                                item.setText(fullText);
                                item.setTextSize(15f);
                                item.setPadding(16, 16, 16, 16);
                                item.setBackgroundColor(0xFFF4F6F8);

                                Button deleteBtn = new Button(this);
                                deleteBtn.setText("Delete");
                                deleteBtn.setBackgroundColor(0xFFE57373);
                                deleteBtn.setTextColor(0xFFFFFFFF);
                                deleteBtn.setOnClickListener(del -> {
                                    db.collection("users").document(uid)
                                            .update("goals", FieldValue.arrayRemove(goalItem))
                                            .addOnSuccessListener(aVoid -> {
                                                Toast.makeText(this, "Deleted", Toast.LENGTH_SHORT).show();
                                                layoutHistory.removeView(item);
                                                layoutHistory.removeView(deleteBtn);
                                            });
                                });

                                layoutHistory.addView(item);
                                layoutHistory.addView(deleteBtn);
                            }
                        } else {
                            Toast.makeText(this, "No history found.", Toast.LENGTH_SHORT).show();
                        }
                        layoutHistory.setVisibility(View.VISIBLE);
                    });
        });
    }

    //animateTyping
    private void animateTyping(TextView textView, String fullText, long delayMillis) {
        textView.setText("");
        final int[] index = {0};
        Handler handler = new Handler(Looper.getMainLooper());

        Runnable characterAdder = new Runnable() {
            @Override
            public void run() {
                if (index[0] <= fullText.length()) {
                    textView.setText(fullText.substring(0, index[0]));
                    index[0]++;
                    handler.postDelayed(this, delayMillis);
                }
            }
        };

        handler.post(characterAdder);
    }
}

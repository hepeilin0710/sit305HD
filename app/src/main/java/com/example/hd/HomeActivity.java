package com.example.hd;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.app.AlertDialog;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class HomeActivity extends AppCompatActivity {

    private TextView textWelcome;
    private Button btnSetGoal, btnViewPlan, btnLogMeal, btnChat, btnFeedback, btnLogout, btnGuidance;

    private Button[] guidanceButtons;
    private String[] guidanceTexts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        textWelcome = findViewById(R.id.textWelcome);
        btnSetGoal = findViewById(R.id.btnSetGoal);
        btnViewPlan = findViewById(R.id.btnViewPlan);
        btnLogMeal = findViewById(R.id.btnLogMeal);
        btnChat = findViewById(R.id.btnChat);
        btnFeedback = findViewById(R.id.btnFeedback);
        btnLogout = findViewById(R.id.btnLogout);
        btnGuidance = findViewById(R.id.btnGuidance);

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String uid = mAuth.getCurrentUser().getUid();

        db.collection("users").document(uid).get()
                .addOnSuccessListener(doc -> {
                    String username = doc.getString("username");
                    textWelcome.setText(username != null ? "Welcome, " + username : "Welcome!");
                })
                .addOnFailureListener(e -> {
                    textWelcome.setText("Welcome!");
                    Toast.makeText(this, "Failed to load user data", Toast.LENGTH_SHORT).show();
                });

        btnSetGoal.setOnClickListener(v -> startActivity(new Intent(this, SetGoalActivity.class)));
        btnViewPlan.setOnClickListener(v -> startActivity(new Intent(this, PlanMealActivity.class)));
        btnLogMeal.setOnClickListener(v -> startActivity(new Intent(this, LogMealActivity.class)));
        btnChat.setOnClickListener(v -> startActivity(new Intent(this, ChatActivity.class)));
        btnFeedback.setOnClickListener(v -> startActivity(new Intent(this, FeedbackActivity.class)));

        btnLogout.setOnClickListener(v -> {
            new AlertDialog.Builder(this)
                    .setTitle("Log Out")
                    .setMessage("Are you sure you want to log out?")
                    .setPositiveButton("Yes", (dialog, which) -> {
                        FirebaseAuth.getInstance().signOut();
                        Intent intent = new Intent(this, LoginActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        finish();
                    })
                    .setNegativeButton("Cancel", null)
                    .show();
        });

        btnGuidance.setOnClickListener(v -> startGuidance());
    }

    private void startGuidance() {
        guidanceButtons = new Button[]{btnSetGoal, btnViewPlan, btnLogMeal, btnChat, btnFeedback};
        guidanceTexts = new String[]{
                "Set Goal: Define your health goal like losing weight or gaining muscle.",
                "View Meal Plan: Generate meals based on your goal, mood and diet.",
                "Log Meals: Track what you eat and get nutritional feedback.",
                "Chat with AI: Ask any food or health-related question.",
                "Provide Feedback: Help improve suggestions with your thoughts."
        };
        Toast.makeText(this, "Tap 'Next' to explore each feature", Toast.LENGTH_SHORT).show();
        showGuidanceStep(0);
    }

    private void showGuidanceStep(int index) {
        if (index >= guidanceButtons.length) {
            resetButtonColors();
            return;
        }

        resetButtonColors();

        Button current = guidanceButtons[index];
        current.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.highlight_blue)));

        Snackbar snackbar = Snackbar.make(current, guidanceTexts[index], Snackbar.LENGTH_INDEFINITE)
                .setAction(index == guidanceButtons.length - 1 ? "Finish" : "Next", v -> {
                    showGuidanceStep(index + 1);
                });

        snackbar.setActionTextColor(getResources().getColor(R.color.white));
        snackbar.show();
    }

    private void resetButtonColors() {
        int color = getResources().getColor(R.color.transparent_blue);
        for (Button btn : new Button[]{btnSetGoal, btnViewPlan, btnLogMeal, btnChat, btnFeedback}) {
            btn.setBackgroundTintList(ColorStateList.valueOf(color));
        }
    }
}

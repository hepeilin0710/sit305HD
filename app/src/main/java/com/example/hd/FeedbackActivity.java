package com.example.hd;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.hd.backend.BackendService;

public class FeedbackActivity extends AppCompatActivity {

    private EditText editFeedback;
    private Button btnSubmit, btnBack;
    private TextView textThankYou;
    private BackendService backendService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);

        editFeedback = findViewById(R.id.editFeedback);
        btnSubmit = findViewById(R.id.btnSubmitFeedback);
        btnBack = findViewById(R.id.btnBack);
        textThankYou = findViewById(R.id.textThankYou);

        backendService = new BackendService();


        btnBack.setOnClickListener(v -> {
            startActivity(new Intent(this, HomeActivity.class));
            finish();
        });

        // submit
        btnSubmit.setOnClickListener(v -> {
            String comment = editFeedback.getText().toString().trim();

            if (TextUtils.isEmpty(comment)) {
                Toast.makeText(this, "Please enter your feedback.", Toast.LENGTH_SHORT).show();
                return;
            }

            btnSubmit.setEnabled(false);

            backendService.sendFeedback(comment, this, insight -> {
                showThankYouMessage();
                editFeedback.setText("");
                btnSubmit.setEnabled(true);
            });
        });
    }

    // Animated thank you reminder
    private void showThankYouMessage() {
        textThankYou.setText("✅ Thank you for your feedback!");
        textThankYou.setVisibility(View.VISIBLE);
        AlphaAnimation fadeIn = new AlphaAnimation(0.0f, 1.0f);
        fadeIn.setDuration(800);
        textThankYou.startAnimation(fadeIn);
    }
}

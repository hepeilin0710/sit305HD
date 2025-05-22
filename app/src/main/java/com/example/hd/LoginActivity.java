package com.example.hd;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private EditText inputEmail, inputPassword;
    private Button loginBtn, registerBtn;
    private SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        inputEmail = findViewById(R.id.editEmail);
        inputPassword = findViewById(R.id.editPassword);
        loginBtn = findViewById(R.id.btnLogin);
        registerBtn = findViewById(R.id.btnToRegister);

        mAuth = FirebaseAuth.getInstance();
        prefs = getSharedPreferences("NutriBotPrefs", MODE_PRIVATE);

        loginBtn.setOnClickListener(v -> {
            String email = inputEmail.getText().toString();
            String password = inputPassword.getText().toString();

            if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
                Toast.makeText(this, "Email and password are required", Toast.LENGTH_SHORT).show();
                return;
            }

            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnSuccessListener(authResult -> {
                        FirebaseUser user = mAuth.getCurrentUser();
                        if (user != null) {
                            showLoginOptionDialog(email);
                        }
                    })
                    .addOnFailureListener(e ->
                            Toast.makeText(this, "Login failed: " + e.getMessage(), Toast.LENGTH_SHORT).show()
                    );
        });

        registerBtn.setOnClickListener(v -> {
            startActivity(new Intent(this, RegisterActivity.class));
        });
    }

    private void showLoginOptionDialog(String email) {
        SharedPreferences.Editor editor = prefs.edit();

        new AlertDialog.Builder(this)
                .setTitle("Keep me signed in?")
                .setMessage("Would you like to stay signed in for next time?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    editor.putBoolean("stayLoggedIn", true);
                    editor.apply();
                    proceedToHome(email);
                })
                .setNegativeButton("No", (dialog, which) -> {
                    editor.putBoolean("stayLoggedIn", false);
                    editor.apply();
                    proceedToHome(email);
                })
                .show();
    }

    private void proceedToHome(String email) {
        String username = email.split("@")[0];
        Intent intent = new Intent(this, HomeActivity.class);
        intent.putExtra("username", username);
        startActivity(intent);
        finish();
    }
}

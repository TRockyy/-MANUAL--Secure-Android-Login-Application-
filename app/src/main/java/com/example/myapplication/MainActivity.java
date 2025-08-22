package com.example.myapplication;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private static final int MAX_ATTEMPTS = 4;
    private int loginAttempts = 0;

    EditText usernameEditText, passwordEditText;
    Button loginButton, createAccountButton;

    private CountDownTimer countDownTimer;
    private static final long LOCKOUT_TIME = 30000L; // 30 seconds lockout
    private SharedPreferences sharedPreferences;
    private static final String PREFS_NAME = "UserPrefs";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        usernameEditText = findViewById(R.id.usernameEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        loginButton = findViewById(R.id.loginButton);
        createAccountButton = findViewById(R.id.buttonCreateAccount); // Account creation button

        sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);

        // Check if user is locked out when app starts
        long lockoutEndTime = sharedPreferences.getLong("lockoutEndTime", 0);
        if (lockoutEndTime > System.currentTimeMillis()) {
            // User is locked out, display the remaining time
            long remainingLockoutTime = (lockoutEndTime - System.currentTimeMillis()) / 1000;
            loginButton.setEnabled(false);
            loginButton.setText("Locked (" + remainingLockoutTime + "s)");
            startCountDownTimer(remainingLockoutTime);
        }

        // Login Button OnClickListener
        loginButton.setOnClickListener(v -> {
            String user = usernameEditText.getText().toString().trim();
            String pass = passwordEditText.getText().toString().trim();

            if (user.isEmpty() || pass.isEmpty()) {
                Toast.makeText(this, "Please fill in both fields.", Toast.LENGTH_SHORT).show();
                return;
            }

            // Retrieve stored username and password
            String storedUsername = sharedPreferences.getString("username", "");
            String storedPassword = sharedPreferences.getString("password", "");

            if (user.equals(storedUsername) && HashUtil.checkPassword(pass, storedPassword)) {
                // Password matches
                Toast.makeText(MainActivity.this, "Login successful!", Toast.LENGTH_SHORT).show();
                loginAttempts = 0;

                Intent intent = new Intent(MainActivity.this, Dashboard.class);
                startActivity(intent);
                finish();
            } else {
                // Incorrect credentials
                loginAttempts++;
                int attemptsLeft = MAX_ATTEMPTS - loginAttempts;

                if (attemptsLeft > 0) {
                    Toast.makeText(MainActivity.this, "Incorrect credentials. Attempts left: " + attemptsLeft, Toast.LENGTH_SHORT).show();
                } else {
                    lockUserOut();
                }
            }
        });

        // Account Creation Button OnClickListener
        createAccountButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, accountcreation.class);
            startActivity(intent);
        });
    }

    private void lockUserOut() {
        // Lock user out and store lockout end time
        Toast.makeText(MainActivity.this, "Too many failed attempts. Locked out for 30 seconds.", Toast.LENGTH_LONG).show();
        loginButton.setEnabled(false);

        // Store the lockout end time in SharedPreferences
        long lockoutEndTime = System.currentTimeMillis() + LOCKOUT_TIME;
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putLong("lockoutEndTime", lockoutEndTime);
        editor.apply();

        // Start countdown timer for lockout
        countDownTimer = new CountDownTimer(LOCKOUT_TIME, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                long secondsRemaining = millisUntilFinished / 1000;
                loginButton.setText("Locked (" + secondsRemaining + "s)");
            }

            @Override
            public void onFinish() {
                loginButton.setEnabled(true);
                loginButton.setText("Login");
                loginAttempts = 0;
            }
        }.start();
    }

    private void startCountDownTimer(long remainingLockoutTime) {
        countDownTimer = new CountDownTimer(remainingLockoutTime * 1000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                long secondsRemaining = millisUntilFinished / 1000;
                loginButton.setText("Locked (" + secondsRemaining + "s)");
            }

            @Override
            public void onFinish() {
                loginButton.setEnabled(true);
                loginButton.setText("Login");
                loginAttempts = 0;
            }
        }.start();
    }
}

package com.example.myapplication;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;

public class accountcreation extends AppCompatActivity {

    EditText usernameEditText, passwordEditText, confirmPasswordEditText;
    Button registerButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.account_creation);

        usernameEditText = findViewById(R.id.editTextUsername);
        passwordEditText = findViewById(R.id.editTextPassword);
        confirmPasswordEditText = findViewById(R.id.editTextConfirmPassword);
        registerButton = findViewById(R.id.buttonCreateAccount);

        registerButton.setOnClickListener(v -> {
            String username = usernameEditText.getText().toString().trim();
            String password = passwordEditText.getText().toString().trim();
            String confirmPassword = confirmPasswordEditText.getText().toString().trim();

            // Check for empty fields
            if (username.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
                Toast.makeText(accountcreation.this, "Please fill in all fields.", Toast.LENGTH_SHORT).show();
                return;
            }

            // Check if passwords match
            if (!password.equals(confirmPassword)) {
                Toast.makeText(accountcreation.this, "Passwords do not match.", Toast.LENGTH_SHORT).show();
                return;
            }

            // Hash password before storing
            String hashedPassword = HashUtil.hashPassword(password);

            if (hashedPassword != null) {
                // Save hashed password in SharedPreferences
                SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("username", username);
                editor.putString("password", hashedPassword);
                editor.apply();

                Toast.makeText(accountcreation.this, "Account created successfully!", Toast.LENGTH_SHORT).show();

                // Navigate to the login screen
                Intent intent = new Intent(accountcreation.this, MainActivity.class);
                startActivity(intent);
                finish();
            } else {
                Toast.makeText(accountcreation.this, "Error creating account. Please try again.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}

package com.example.tiktok;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.tiktok.databinding.ActivityLoginBinding;

public class LoginActivity extends AppCompatActivity {

    public static final String USER = "user";
    // Tag
    private static final String TAG = "LoginActivity";
    EditText email, password;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //MyUtil.setLightStatusBar(this);

        TextView register = findViewById(R.id.txtRegister);
        Button login = findViewById(R.id.btnLogin);
        ImageView loginGoogle = findViewById(R.id.loginGoogle);
        ImageView loginFacebook = findViewById(R.id.loginFacebook);
        email = findViewById(R.id.txt_email);
        password = findViewById(R.id.txtPassword);
        TextView forgotPassword = findViewById(R.id.txt_forgot_password);

        // Register
        register.setOnClickListener(v -> handleRegister());

        // Login
        login.setOnClickListener(v -> handleLogin());

        // Forgot password
        forgotPassword.setOnClickListener(v -> handleForgotPassword());
    }
    private void handleForgotPassword() {
        String emailText = email.getText().toString();
        if (emailText.isEmpty()) {
            email.setError("Vui lòng nhập email hoặc username!");
        } else if (emailText.contains("@")) {
            //sendEmailResetPassword(emailText);
        } else {

            }
        }

    private void handleRegister() {
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        intent.putExtra(MainActivity.EXTRA_REGISTER, true);
        setResult(RESULT_CANCELED, intent);
        finish();
    }

    // Login
    private void handleLogin() {
        // Check if email and password are empty
        if (isValidInput()) {
            String emailText = email.getText().toString();
            String passwordText = password.getText().toString();
            if (emailText.contains("@")) {
            }
            else {

                };
            }
    }
    private boolean isValidInput() {
        if (email.getText().toString().isEmpty()) {
            email.setError("Username is required");
            email.requestFocus();
            return false;
        } else if (password.getText().toString().isEmpty()) {
            password.setError("Password is required");
            password.requestFocus();
            return false;
        } else {
            return true;
        }
}
}

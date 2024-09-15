package com.example.tiktok;

import android.content.Intent;
import android.os.Bundle;

import com.example.tiktok.models.Data;
import com.example.tiktok.models.Root;
import com.example.tiktok.models.User;
import com.example.tiktok.service.ApiInterface;
import com.example.tiktok.service.RetrofitClient;
import com.example.tiktok.utils.MyUtil;
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

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    public static final String USER = "user";
    // Tag
    private static final String TAG = "LoginActivity";
    EditText username, password;
    User newUser;
    final ApiInterface apitiktok = RetrofitClient.getInstance().create(ApiInterface.class);
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        MyUtil.setLightStatusBar(this);

        TextView register = findViewById(R.id.txtRegister);
        Button login = findViewById(R.id.btnLogin);
        ImageView loginGoogle = findViewById(R.id.loginGoogle);
        ImageView loginFacebook = findViewById(R.id.loginFacebook);
        username = findViewById(R.id.txt_email);
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
        String emailText = username.getText().toString();
        if (emailText.isEmpty()) {
            username.setError("Vui lòng nhập email hoặc username!");
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
            String usernameText = username.getText().toString();
            String passwordText = password.getText().toString();
            Intent intent = new Intent(this, MainActivity.class);
            apitiktok.login(usernameText,passwordText).enqueue(new Callback<Root<User>>() {
                @Override
                public void onResponse(Call<Root<User>> call, Response<Root<User>> response) {
                    Log.d(TAG, "onResponse: " + response.body().data);
                    if (response.message() !="Tài khoản không đúng") {
                        MyUtil.user_current = response.body().data;
                        intent.putExtra(USER, newUser);
                        setResult(RESULT_OK, intent);
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(LoginActivity.this, "Đăng nhập thất bại, vui lòng kiểm tra lại thông tin đăng nhập", Toast.LENGTH_SHORT).show();
                        password.setError("Vui lòng kiểm tra lại mật khẩu");
                    }
                }

                @Override
                public void onFailure(Call<Root<User>> call, Throwable t) {
                    Toast.makeText(LoginActivity.this, "Đăng nhập thất bại, vui lòng kiểm tra lại thông tin đăng nhập", Toast.LENGTH_SHORT).show();
                    password.setError("Vui lòng kiểm tra lại mật khẩu");
                }
            });
            }
    }
    private boolean isValidInput() {
        if (username.getText().toString().isEmpty()) {
            username.setError("Username is required");
            username.requestFocus();
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

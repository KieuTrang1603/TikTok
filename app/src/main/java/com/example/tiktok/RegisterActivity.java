package com.example.tiktok;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.tiktok.fragment.InputAccountRegister;
import com.example.tiktok.fragment.InputInfoRegister;
import com.example.tiktok.models.Root;
import com.example.tiktok.models.User;
import com.example.tiktok.service.ApiInterface;
import com.example.tiktok.service.RetrofitClient;
import com.example.tiktok.utils.MyUtil;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterActivity extends AppCompatActivity {
    public static final String USER = "user";
    // tag
    private static final String TAG = "RegisterActivity";

    public User newUser;
    Fragment activeFragment;
    final ApiInterface apitiktok = RetrofitClient.getInstance().create(ApiInterface.class);
    final InputAccountRegister inputAccountRegister = InputAccountRegister.getInstance();
    final InputInfoRegister inputInfoRegister = InputInfoRegister.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register);
        //MyUtil.setLightStatusBar(this);
//        apitiktok = RetrofitClient.getInstance().create(ApiInterface.class);
        //tạo nhưng ẩn đi
        getSupportFragmentManager().beginTransaction()
                .add(R.id.input_fragment, inputAccountRegister, InputAccountRegister.TAG).hide(inputAccountRegister)
                .addToBackStack(InputInfoRegister.TAG)
                .add(R.id.input_fragment, inputInfoRegister, InputInfoRegister.TAG).hide(inputInfoRegister)
                .addToBackStack(InputAccountRegister.TAG)
                .commit();

        //after transaction you must call the executePendingTransaction giao dịch được thực thi ngay lập tức
        getSupportFragmentManager().executePendingTransactions();

        getSupportFragmentManager().beginTransaction()
                .show(inputInfoRegister)
                .commit();
        activeFragment = inputInfoRegister;

        TextView txt_login = findViewById(R.id.txt_login);
        txt_login.setOnClickListener(v -> {
            Intent intent = new Intent(this, LoginActivity.class);
            intent.putExtra(MainActivity.EXTRA_LOGIN, true);
            setResult(RESULT_CANCELED, intent);
            startActivity(intent);
            finish();
        });
        newUser = new User();
    }

    public void finishRegister(boolean isSuccess, String email, String fullName, String phoneNumber,String username, String password){
    //    Log.d("Tag", newUser.toString());
        Intent intent = new Intent(this, MainActivity.class);
        apitiktok.signin(email,fullName,phoneNumber,username,password).enqueue(new Callback<Root<User>>() {
            @Override
            public void onResponse(Call<Root<User>> call, Response<Root<User>> response) {
                Log.d(TAG, "onResponse: " + response.body().data);
                if (response.isSuccessful()) {
//                    newUser = response.body().data;
//                    Log.d(TAG, "onResponse: " + newUser);
                    MyUtil.user_current = response.body().data;
                    intent.putExtra(USER, newUser);
                    setResult(RESULT_OK, intent);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(RegisterActivity.this, "Authentication failed.",
                            Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call<Root<User>> call, Throwable t) {
                setResult(RESULT_CANCELED, intent);
                finish();
            }
        });
    }
    public void openInputAccountRegister() {
        getSupportFragmentManager().beginTransaction()
                .hide(activeFragment)
                .show(inputAccountRegister)
                .commit();
        activeFragment = inputAccountRegister;
    }

    public void openInputInfoRegister() {
        getSupportFragmentManager().beginTransaction()
                .hide(activeFragment)
                .show(inputInfoRegister)
                .commit();
        activeFragment = inputInfoRegister;
    }
}
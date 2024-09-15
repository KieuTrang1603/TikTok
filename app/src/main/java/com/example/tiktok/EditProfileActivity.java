package com.example.tiktok;

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.tiktok.models.Root;
import com.example.tiktok.models.User;
import com.example.tiktok.service.ApiInterface;
import com.example.tiktok.service.RetrofitClient;
import com.example.tiktok.utils.MyUtil;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EditProfileActivity extends AppCompatActivity {
    EditText edtFullName, edtEmail, edtPhoneNumber;
    TextView txtChangePassword, txtDeleteAccount, txtSave;
    ImageView icBack;
    final ApiInterface apitiktok = RetrofitClient.getInstance().create(ApiInterface.class);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_edit_profile);

        // Get user's information
        User user = MainActivity.getCurrentUser();

        // Bind views
        edtFullName = findViewById(R.id.edt_fullname);
        edtEmail = findViewById(R.id.edt_email);
        edtPhoneNumber = findViewById(R.id.edt_phonenumber);
        txtChangePassword = findViewById(R.id.txt_change_password);
        txtDeleteAccount = findViewById(R.id.txt_delete_account);
        icBack = findViewById(R.id.ic_back);
        txtSave = findViewById(R.id.txt_save);

        // Set user's information
        edtFullName.setText(user.getFullName());
        edtEmail.setText(user.getEmail());
        edtPhoneNumber.setText(user.getPhoneNumber());

        // Set onclick listener
        icBack.setOnClickListener(v -> finish());
        txtSave.setOnClickListener(v -> handleSave(user));
        txtChangePassword.setOnClickListener(v -> handleChangePassword());
//        txtDeleteAccount.setOnClickListener(v -> {
//            Toast.makeText(this, "Chức năng này chưa hoàn thiện!", Toast.LENGTH_SHORT).show();
////			handleDeleteAccount(user);
//        });

        MyUtil.setLightStatusBar(this);
    }

    private void handleSave(User user) {
        // Get user's information
        String fullName = edtFullName.getText().toString();
        String email = edtEmail.getText().toString();
        String phoneNumber = edtPhoneNumber.getText().toString();

        if (isValid(fullName, email, phoneNumber)) {
            // Set user's information
            user.setFullName(fullName);
            user.setEmail(email);
            user.setPhoneNumber(phoneNumber);
            apitiktok.updateUser(user.getUser_id(),fullName,email,phoneNumber).enqueue(new Callback<Root<User>>() {
                @Override
                public void onResponse(Call<Root<User>> call, Response<Root<User>> response) {
                    // Go to profile activity
//                    Toast.makeText(this, "Lưu thành công", Toast.LENGTH_SHORT).show();
//                    finish();
                }

                @Override
                public void onFailure(Call<Root<User>> call, Throwable t) {

                }
            });
        } else
            Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
    }
    private boolean isValid(String fullName, String email, String phoneNumber) {
        if (fullName.isEmpty()) {
            edtFullName.setError("Hãy nhập họ tên");
            return false;
        } else if (email.isEmpty()) {
            edtEmail.setError("Không được để trống");
            return false;
        } else if (phoneNumber.isEmpty()) {
            edtPhoneNumber.setError("Không được để trống");
            return false;
        }
        return true;
    }

    private void handleChangePassword() {
        Intent intent = new Intent(this, ChangePasswordActivity.class);
        startActivity(intent);
    }

}
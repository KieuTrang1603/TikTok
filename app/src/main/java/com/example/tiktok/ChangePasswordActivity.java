package com.example.tiktok;

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

public class ChangePasswordActivity extends AppCompatActivity {
    EditText edtOldPassword, edtNewPassword, edtConfirmPassword;
    TextView txtChangePassword;
    ImageView icBack;
    User user;
    final ApiInterface apitiktok = RetrofitClient.getInstance().create(ApiInterface.class);
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_change_password);

        MyUtil.setLightStatusBar(this);
        // Get user's information
//        user = MainActivity.getCurrentUser();

        // Bind views
        edtOldPassword = findViewById(R.id.edt_old_password);
        edtNewPassword = findViewById(R.id.edt_new_password);
        edtConfirmPassword = findViewById(R.id.edt_confirm_password);
        txtChangePassword = findViewById(R.id.txt_change_password);
        icBack = findViewById(R.id.ic_back);

        // Set onclick listener
        txtChangePassword.setOnClickListener(v -> handleChangePassword());
        icBack.setOnClickListener(v -> finish());
    }

    private void handleChangePassword() {
        if (isValidInput()) {
            String oldPassword = edtOldPassword.getText().toString();
            String newPassword = edtNewPassword.getText().toString();

            user = MainActivity.getCurrentUser();
            if (user != null) {
            apitiktok.changePassword(user.getUser_id(), oldPassword, newPassword).enqueue(new Callback<Root<User>>() {
                @Override
                public void onResponse(Call<Root<User>> call, Response<Root<User>> response) {

                }

                @Override
                public void onFailure(Call<Root<User>> call, Throwable t) {

                }
            });
//                assert email != null;
//                AuthCredential credential = EmailAuthProvider.getCredential(email, oldPassword);
//
//                user.reauthenticate(credential).addOnCompleteListener(task -> {
//                    if (task.isSuccessful()) {
//                        user.updatePassword(newPassword).addOnCompleteListener(task1 -> {
//                            if (!task1.isSuccessful()) {
//                                Toast.makeText(ChangePasswordActivity.this, "Cập nhật mật khẩu thất bại! \n Lỗi: " + task1.getException().getMessage(), Toast.LENGTH_SHORT).show();
//                            } else {
//                                Toast.makeText(ChangePasswordActivity.this, "Cập nhật mật khẩu thành công", Toast.LENGTH_SHORT).show();
//                                finish();
//                            }
//                        });
//                    } else {
//                        edtOldPassword.setError("Mật khẩu cũ không đúng!");
//                    }
//                });
            } else {
                Toast.makeText(ChangePasswordActivity.this, "Không tìm thấy tài khoản", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private boolean isValidInput() {
        String oldPassword = edtOldPassword.getText().toString();
        String newPassword = edtNewPassword.getText().toString();
        String confirmPassword = edtConfirmPassword.getText().toString();

        if (oldPassword.isEmpty()) {
            edtOldPassword.setError("Vui lòng nhập mật khẩu cũ");
            return false;
        } else if (newPassword.isEmpty()) {
            edtNewPassword.setError("Vui lòng nhập mật khẩu mới");
            return false;
        } else if (newPassword.length() < 6) {
            edtNewPassword.setError("Mật khẩu phải có ít nhất 6 ký tự");
            return false;
        }
        else if (confirmPassword.isEmpty()) {
            edtConfirmPassword.setError("Vui lòng nhập lại mật khẩu mới");
            return false;
        } else if (!newPassword.equals(confirmPassword)) {
            edtConfirmPassword.setError("Mật khẩu mới và xác nhận mật khẩu mới không khớp");
            return false;
        } else {
            return true;
        }
    }
}
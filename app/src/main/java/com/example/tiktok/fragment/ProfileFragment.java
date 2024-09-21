package com.example.tiktok.fragment;

import static android.app.Activity.RESULT_OK;
import static com.example.tiktok.MainActivity.REQUEST_ADD_VIDEO;
import static com.example.tiktok.MainActivity.REQUEST_CHANGE_AVATAR;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.widget.NestedScrollView;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.tiktok.ChangePasswordActivity;
import com.example.tiktok.EditProfileActivity;
import com.example.tiktok.MainActivity;
import com.example.tiktok.R;
import com.example.tiktok.models.Root;
import com.example.tiktok.models.UploadResponse;
import com.example.tiktok.models.User;
import com.example.tiktok.service.ApiInterface;
import com.example.tiktok.service.RetrofitClient;
import com.example.tiktok.utils.MyUtil;
import com.google.android.material.navigation.NavigationView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import okhttp3.Call;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Callback;
import retrofit2.Response;


public class ProfileFragment extends Fragment {

    public static final String TAG = "ProfileFragment";
    TextView fullName, num_followers, num_following, num_like, username, txt_post_video;
    ImageView avatar, ic_menu, ic_edit, ic_edit_avatar, ic_add_video;
    RecyclerView recyclerView;
    Context context;
    NestedScrollView scrollView;
    ConstraintLayout layoutProfile;
    NavigationView navigationView;
    EditText edt_video_content;
    final ApiInterface apitiktok = RetrofitClient.getInstance().create(ApiInterface.class);

    Uri videoUri;

    private static final ProfileFragment instance = new ProfileFragment();

    public ProfileFragment() {
    }

    public static ProfileFragment getInstance() {
        return instance;
    }

    @SuppressLint("SetTextI18n")
    public void updateUI(User user) {
        if (user != null) {
            Log.i(TAG, "updateUI: " + user.toString());
//            if (user.isAdmin())
//                navigationView.getMenu().findItem(R.id.admin_panel).setVisible(true);
            fullName.setText(user.getFullName());
            num_followers.setText(user.getNum_followers() + "");
            num_following.setText(user.getNum_following() + "");
            num_like.setText(user.getNum_like() + "");
            username.setText(user.getUsername());
            try {
                Glide.with(context)
                        .load(user.getAvatar())
                        .error(R.drawable.default_avatar)
                        .into(avatar);
            } catch (Exception e) {
                Log.w(TAG, "Glide error: " + e.getMessage());
            }

            //prepareRecyclerView(user);
        }
    }

    @SuppressLint("SetTextI18n")
    public void updateUI() {
        User user = MainActivity.getCurrentUser();
//        Log.d(TAG, "updateUI: " + user.toString());
        updateUI(user);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        this.context = view.getContext();

        // Bind view
        fullName = view.findViewById(R.id.txt_fullname);
        num_followers = view.findViewById(R.id.txt_num_followers);
        num_following = view.findViewById(R.id.txt_num_following);
        num_like = view.findViewById(R.id.txt_num_likes);
        username = view.findViewById(R.id.txt_email);
        avatar = view.findViewById(R.id.img_avatar);
        recyclerView = view.findViewById(R.id.recycler_view_videos_grid);
        scrollView = view.findViewById(R.id.scroll_view);
        layoutProfile = view.findViewById(R.id.layout_profile);
        ic_menu = view.findViewById(R.id.ic_menu);
        ic_edit = view.findViewById(R.id.ic_edit);
        ic_edit_avatar = view.findViewById(R.id.ic_edit_avatar);
        navigationView = view.findViewById(R.id.nav_view);
        DrawerLayout drawer = view.findViewById(R.id.drawer_layout);
        txt_post_video = view.findViewById(R.id.txt_post_video);
        edt_video_content = view.findViewById(R.id.edt_video_content);
        ic_add_video = view.findViewById(R.id.ic_add_video);

        ic_add_video.setOnClickListener(v -> handleAddVideo());

        //txt_post_video.setOnClickListener(v -> handlePostVideo());

        ic_menu.setOnClickListener(v -> drawer.open());

        ic_edit.setOnClickListener(v -> openEditProfileActivity());

        ic_edit_avatar.setOnClickListener(v -> handleEditAvatar());

        navigationView.setNavigationItemSelectedListener(v -> {
            switch (v.getItemId()) {
//                case R.id.admin_panel:
//                    openAdminActivity();
//                    break;
                case R.id.edit_profile:
                    openEditProfileActivity();
                    break;
                case R.id.change_password:
                    openChangePasswordActivity();
                    break;
                case R.id.log_out:
                    MainActivity mainActivity = (MainActivity) requireActivity();
                    mainActivity.logOut();
                    Toast.makeText(context, "Log out successfully", Toast.LENGTH_SHORT).show();
                    break;
            }

            drawer.close();
            return false;
        });

        updateUI();

        return view;
    }

    private void openChangePasswordActivity() {
        Intent intent = new Intent(context, ChangePasswordActivity.class);
        startActivity(intent);
    }

    private void openEditProfileActivity() {
        Intent intent = new Intent(context, EditProfileActivity.class);
        startActivity(intent);
    }

//    private void openAdminActivity() {
//        Intent intent = new Intent(context, AdminActivity.class);
//        startActivity(intent);
//    }

    private void handleEditAvatar() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(intent, REQUEST_CHANGE_AVATAR);
    }

    private void handleAddVideo() {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1000);
        } else {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("video/*");
            startActivityForResult(intent, REQUEST_ADD_VIDEO);
        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CHANGE_AVATAR && resultCode == RESULT_OK && data != null) {
            Uri uri = data.getData();
<<<<<<< HEAD
            uploadImage(uri);
=======
//            uploadImage(uri);
>>>>>>> 4941626fbe9ef82cf6b0013c8719f39c9fbe436a
        } else if (requestCode == REQUEST_ADD_VIDEO && resultCode == RESULT_OK && data != null) {
            videoUri = data.getData();
            try {
                Glide.with(context).load(videoUri).into(ic_add_video);
            } catch (Exception e) {
                Log.w(TAG, "Glide error: " + e.getMessage());
            }
            uploadVideoToApi(videoUri);
        }
    }

    public void uploadVideoToApi(Uri videoUri) {

        // Khởi tạo API service
//        ApiInterface apiService = retrofit.create(ApiService.class);

        // Tạo File từ Uri
        File file = null;
        try {
            file = getFileFromUri(videoUri); // Hàm getPathFromUri được giải thích bên dưới
        } catch (Exception e) {

        }
<<<<<<< HEAD

        if (file == null) {
            Log.e(TAG, "File tạo không thành công");
            return;
        }
=======
>>>>>>> 4941626fbe9ef82cf6b0013c8719f39c9fbe436a
        // Tạo RequestBody cho file
        RequestBody requestFile = RequestBody.create(MediaType.parse("video/*"), file);

        // Tạo MultipartBody.Part từ file
<<<<<<< HEAD
        MultipartBody.Part body = MultipartBody.Part.createFormData("videoFile", file.getName(), requestFile);

=======
        MultipartBody.Part body = MultipartBody.Part.createFormData(
                "videoFile",
                file.getName(),
                requestFile
        );
>>>>>>> 4941626fbe9ef82cf6b0013c8719f39c9fbe436a
        // Gọi API để upload video
//        Call<UploadResponse> call = apitiktok.uploadVideo(body);
        apitiktok.uploadVideo(body).enqueue(new Callback<UploadResponse>() {
            @Override
            public void onResponse(retrofit2.Call<UploadResponse> call, Response<UploadResponse> response) {
<<<<<<< HEAD
                if (response.isSuccessful()) {
                    Log.d(TAG, "Upload thành công: " + response.body());
                } else {
                    try {
                        Log.e(TAG, "Upload thất bại: " + response.errorBody().string());
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
=======
                Log.d(TAG, "Upload thành công: " + response.body());
>>>>>>> 4941626fbe9ef82cf6b0013c8719f39c9fbe436a
            }

            @Override
            public void onFailure(retrofit2.Call<UploadResponse> call, Throwable t) {
                Log.d(TAG, "Upload thất bại: " + t);
            }
        });
//        call.enqueue(new Callback<UploadResponse>() {
//            @Override
//            public void onResponse(Call<UploadResponse> call, Response<UploadResponse> response) {
//                if (response.isSuccessful()) {
//                    // Xử lý khi upload thành công
//                    Log.d(TAG, "Upload thành công: " + response.body().getMessage());
//                } else {
//                    Log.e(TAG, "Upload thất bại");
//                }
//            }
//
//            @Override
//            public void onFailure(Call<UploadResponse> call, Throwable t) {
//                Log.e(TAG, "Lỗi khi upload: " + t.getMessage());
//            }
//        });
    }

    public File getFileFromUri(Uri contentUri) throws IOException, FileNotFoundException {
        // Create a new file in the app's cache directory
        File tempFile = new File(context.getCacheDir(), "temp_video_file.mp4");
        tempFile.createNewFile();

        // Try to copy the file content from the URI to the temporary file
        try (InputStream inputStream = context.getContentResolver().openInputStream(contentUri);
             OutputStream outputStream = new FileOutputStream(tempFile)) {
            byte[] buffer = new byte[4096];
            int read;
            while ((read = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, read);
            }
        }

        return tempFile;
    }
<<<<<<< HEAD
    public void uploadImage(Uri uri) {

        // Khởi tạo API service
//        ApiInterface apiService = retrofit.create(ApiService.class);

        // Tạo File từ Uri
        File file = null;
        try {
            file = getFileFromUri(uri); // Hàm getPathFromUri được giải thích bên dưới
        } catch (Exception e) {

        }
        // Tạo RequestBody cho file
        RequestBody requestFile = RequestBody.create(MediaType.parse("image/*"), file);

        // Tạo MultipartBody.Part từ file
        MultipartBody.Part body = MultipartBody.Part.createFormData("file", file.getName(), requestFile);

        // Gọi API để upload video
//        Call<UploadResponse> call = apitiktok.uploadVideo(body);
        apitiktok.uploadVideo(body).enqueue(new Callback<UploadResponse>() {
            @Override
            public void onResponse(retrofit2.Call<UploadResponse> call, Response<UploadResponse> response) {
                if (response.isSuccessful()) {
                    Log.d(TAG, "Upload thành công: " + response.body());
                } else {
                    try {
                        Log.e(TAG, "Upload thất bại: " + response.errorBody().string());
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            }

            @Override
            public void onFailure(retrofit2.Call<UploadResponse> call, Throwable t) {
                Log.d(TAG, "Upload thất bại: " + t);
            }
        });
    }
=======
>>>>>>> 4941626fbe9ef82cf6b0013c8719f39c9fbe436a
}
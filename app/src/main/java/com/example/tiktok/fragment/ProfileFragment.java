package com.example.tiktok.fragment;

import static android.app.Activity.RESULT_OK;
import static com.example.tiktok.MainActivity.REQUEST_ADD_VIDEO;
import static com.example.tiktok.MainActivity.REQUEST_CHANGE_AVATAR;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
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
import androidx.recyclerview.widget.GridLayoutManager;
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
import com.example.tiktok.adapters.VideoGridAdapter;
import com.example.tiktok.models.Data;
import com.example.tiktok.models.Root;
import com.example.tiktok.models.UploadResponse;
import com.example.tiktok.models.User;
import com.example.tiktok.models.Video;
import com.example.tiktok.service.ApiInterface;
import com.example.tiktok.service.RetrofitClient;
import com.example.tiktok.utils.KeyboardUtil;
import com.example.tiktok.utils.MyUtil;
import com.google.android.material.navigation.NavigationView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
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
    Video video = new Video();
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
                String avatarUrl = user.getAvatar();
                if (avatarUrl != null && !avatarUrl.isEmpty()) {
                    Glide.with(context)
                            .load(avatarUrl)
                            .error(R.drawable.default_avatar)
                            .into(avatar);
                } else {
                    // Hiển thị ảnh mặc định khi avatarUrl là null hoặc chuỗi rỗng
                    avatar.setImageResource(R.drawable.default_avatar);
                }
            } catch (Exception e) {
                Log.w(TAG, "Glide error: " + e.getMessage());
            }

            prepareRecyclerView(user);
        }
    }

    @SuppressLint("SetTextI18n")
    public void updateUI() {
        User user = MainActivity.getCurrentUser();
//        Log.d(TAG, "updateUI: " + user.toString());
        updateUI(user);
    }

    private void prepareRecyclerView(User user) {
        apitiktok.getAllVideo(null,user.getUser_id(),null,null).enqueue(new Callback<Root<Data<Video>>>() {
            @Override
            public void onResponse(Call<Root<Data<Video>>> call, Response<Root<Data<Video>>> response) {
                if(response.isSuccessful()){
                    List<Video> listVideos =  response.body().data.content;
                    VideoGridAdapter adapter = (VideoGridAdapter) recyclerView.getAdapter();
                    if (adapter != null) {
                        adapter.setVideos(listVideos);
                    } else {
                        adapter = new VideoGridAdapter(listVideos, context);
                        recyclerView.setAdapter(adapter);
                        recyclerView.setLayoutManager(new GridLayoutManager(context, 3));
                    }
                }
            }

            @Override
            public void onFailure(Call<Root<Data<Video>>> call, Throwable t) {
                Log.e("Load loi", t.getMessage());
            }
        });
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

        txt_post_video.setOnClickListener(v -> handlePostVideo());

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
            uploadImage(uri);
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

        // Tạo File từ Uri
        File file = null;
        try {
            file = getFileFromUri(videoUri); // Hàm getPathFromUri được giải thích bên dưới
        } catch (Exception e) {

        }

        if (file == null) {
            Log.e(TAG, "File tạo không thành công");
            return;
        }
        // Tạo RequestBody cho file
        RequestBody requestFile = RequestBody.create(MediaType.parse("video/*"), file);

        // Tạo MultipartBody.Part từ file
        MultipartBody.Part body = MultipartBody.Part.createFormData("videoFile", file.getName(), requestFile);

        // Gọi API để upload video
        apitiktok.uploadVideo(body).enqueue(new Callback<UploadResponse>() {
            @Override
            public void onResponse(retrofit2.Call<UploadResponse> call, Response<UploadResponse> response) {
                if (response.isSuccessful()) {
//                    Log.e(TAG, "Upload thành công111111: " + response.body());
//                    Log.e(TAG,"Upload thành công222222222222222222222222222222:"+response.body().getData());
                    video.setFileName(response.body().getData());
//                    video.setFileName("2f07e934-882f-4489-a6c8-3937448f534f.mp4");
//                    Log.e(TAG, video.getFileName());
                } else {
                    try {
                        Log.e(TAG, "Upload thất bại: " + response.errorBody().string());
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
                Log.d(TAG, "Upload thành công: " + response.body());
            }

            @Override
            public void onFailure(retrofit2.Call<UploadResponse> call, Throwable t) {
                Log.d(TAG, "Upload thất bại: " + t);
            }
        });
//
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
    public void uploadImage(Uri uri) {

        // Tạo File từ Uri
        File file = null;
        try {
            file = getFileFromUri(uri); // Hàm getPathFromUri được giải thích bên dưới
        } catch (Exception e) {

        }

//        String mimeType = "";
//        if (imageFilePath.endsWith(".jpg") || imageFilePath.endsWith(".jpeg")) {
//            mimeType = "image/jpeg";
//        } else if (imageFilePath.endsWith(".png")) {
//            mimeType = "image/png";
//        }
        // Tạo RequestBody cho file
        RequestBody requestFile = RequestBody.create(MediaType.parse("image/*"), file);

        // Tạo MultipartBody.Part từ file
        MultipartBody.Part body = MultipartBody.Part.createFormData("file", file.getName(), requestFile);

        // Gọi API để upload video
//        Call<UploadResponse> call = apitiktok.uploadVideo(body);
        apitiktok.uploadImage(body).enqueue(new Callback<UploadResponse>() {
            @Override
            public void onResponse(retrofit2.Call<UploadResponse> call, Response<UploadResponse> response) {
                if (response.isSuccessful()) {
                    Log.d(TAG, "Upload thành công: " + response.body());
                    MyUtil.user_current.setAvatar(response.body().getData());
                    String imgURL = RetrofitClient.getBaseUrl() +"/api/file/image/view?fileName=" + MyUtil.user_current.getAvatar();
                    try {
                        if (MyUtil.user_current.getAvatar() != null && !MyUtil.user_current.getAvatar().isEmpty()) {
                            Glide.with(context)
                                    .load(imgURL)
                                    .error(R.drawable.default_avatar)
                                    .into(avatar);
                        }else
                            // Hiển thị ảnh mặc định khi avatarUrl là null hoặc chuỗi rỗng
                            avatar.setImageResource(R.drawable.default_avatar);
                    } catch (Exception e) {
                        Log.w(TAG, "Glide error: " + e.getMessage());
                    }
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

    private void handlePostVideo() {
        //KeyboardUtil.hideKeyboard(requireActivity());
        try {
        if (videoUri != null) {
            String content = edt_video_content.getText().toString().trim();
//            uploadVideo(content,video.getFileName(), MyUtil.user_current.getUser_id());
            if (content.isEmpty()) {
            AlertDialog dialog = getConfirmationDialogBuilder(content).create();
            dialog.show();
            Log.e("nd",content);
            }
            if (content.isEmpty()) {
                AlertDialog.Builder builder = getConfirmationDialogBuilder(content);
//                builder = new AlertDialog.Builder(context);
//                builder.setTitle("Đăng video không có nội dung?");
//                builder.setMessage("Bạn có chắc chắn muốn đăng video mà không có nội dung?");
//                builder.setPositiveButton("Đồng ý", (dialog, which) -> {
//                    dialog.dismiss();
//                    uploadVideo(content,video.getFileName(), MyUtil.user_current.getUser_id());
//                });
//                builder.setNegativeButton("Hủy", (dialog, which) -> dialog.dismiss());
                builder.show();
            } else {
                uploadVideo(content,video.getFileName(), MyUtil.user_current.getUser_id());
            }
        } else {
            Toast.makeText(context, "Bạn chưa chọn video", Toast.LENGTH_SHORT).show();
        }
        } catch (Exception e) {
            Log.e("EditText Error", "Lỗi khi hiển thị EditText: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private AlertDialog.Builder getConfirmationDialogBuilder(String content) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Đăng video không có nội dung?");
        builder.setMessage("Bạn có chắc chắn muốn đăng video mà không có nội dung?");
        builder.setPositiveButton("Đồng ý", (dialog, which) -> {
            uploadVideo(content,video.getFileName(), MyUtil.user_current.getUser_id());
            Toast.makeText(context, "Bạn đã chọn Đồng ý", Toast.LENGTH_SHORT).show();
            dialog.dismiss();
        });
        builder.setNegativeButton("Hủy", (dialog, which) -> dialog.dismiss());

        return builder;
    }

    private void uploadVideo(String content, String fileName, String user_id) {
        if (videoUri != null) {
            // Progress bar
            final ProgressDialog progressDialog = new ProgressDialog(context);
            progressDialog.setTitle("Đang đăng video");
            progressDialog.setMessage("Vui lòng chờ...");
            progressDialog.show();
            apitiktok.addvideo(content, fileName, user_id).enqueue(new Callback<Root<Video>>() {
                @Override
                public void onResponse(Call<Root<Video>> call, Response<Root<Video>> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        Log.d(TAG, "Upload thành công: " + response.body());
                        Video video1 = new Video();
                        video1 = response.body().data;
                        Toast.makeText(context, "Đăng video thành công", Toast.LENGTH_SHORT).show();
                        try {
                            Glide.with(context).load(R.drawable.ic_add_video).into(ic_add_video);
                        } catch (Exception e) {
                            Log.w(TAG, "Glide error: " + e.getMessage());
                        }
                        edt_video_content.setText("");
                        VideoGridAdapter videoGridAdapter = (VideoGridAdapter) recyclerView.getAdapter();
                        if (videoGridAdapter != null) {
                                // Thêm video mới vào danh sách video trong adapter
                                videoGridAdapter.getVideos().add(video1);

                                // Thông báo cho adapter rằng có một phần tử mới
                                videoGridAdapter.notifyItemInserted(videoGridAdapter.getItemCount());
                        }
                    } else {
                        // Xử lý khi response không thành công, ví dụ: mã lỗi 400, 500
                        Log.d(TAG, "Upload thất bại: " + response.errorBody());
                        Toast.makeText(context, "Đăng video thất bại: " + response.message(), Toast.LENGTH_SHORT).show();
                    }
                    // Đảm bảo ProgressDialog luôn được ẩn sau khi có phản hồi
                    progressDialog.dismiss();
                }

                @Override
                public void onFailure(Call<Root<Video>> call, Throwable t) {
                    Log.d(TAG, "Upload thất bại: " + t.getMessage());
                    Toast.makeText(context, "Có lỗi xảy ra, vui lòng thử lại!", Toast.LENGTH_SHORT).show();
                    // Ẩn ProgressDialog ngay cả khi yêu cầu thất bại
                    progressDialog.dismiss();
                }
            });
        }
    }
}
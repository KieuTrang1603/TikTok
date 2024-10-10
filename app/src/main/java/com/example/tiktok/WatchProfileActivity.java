package com.example.tiktok;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.tiktok.adapters.VideoGridAdapter;
import com.example.tiktok.models.Data;
import com.example.tiktok.models.Root;
import com.example.tiktok.models.User;
import com.example.tiktok.models.Video;
import com.example.tiktok.service.ApiInterface;
import com.example.tiktok.service.RetrofitClient;
import com.example.tiktok.utils.MyUtil;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class WatchProfileActivity extends AppCompatActivity {

    private static final String TAG = "WatchProfileActivity";

    private static final int HEADER_HEIGHT = 8 * 2 + 32 + 1;
    TextView fullname, numFollowers, numFollowing, numLikes, username, txt_follow_status;
    ImageView avatar, ic_back;
    RecyclerView recyclerView;
    Context context;
    NestedScrollView scrollView;
    ConstraintLayout layoutProfile;
    User user;
    List<Video> videos = new ArrayList<>();
    final ApiInterface apitiktok = RetrofitClient.getInstance().create(ApiInterface.class);
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_watch_profile);

        context = this;

        // Get the Intent that started this activity and extract the string
        Bundle extras = getIntent().getExtras();
        String user_idText = extras.getString(User.TAG);
        Log.d("WatchProfileActivity", "usernameText: " + user_idText);

        // Bind view
        fullname = findViewById(R.id.txt_fullname);
        numFollowers = findViewById(R.id.txt_num_followers);
        numFollowing = findViewById(R.id.txt_num_following);
        numLikes = findViewById(R.id.txt_num_likes);
        username = findViewById(R.id.txt_email);
        avatar = findViewById(R.id.img_avatar);
        recyclerView = findViewById(R.id.recycler_view_videos_grid);
        scrollView = findViewById(R.id.scroll_view);
        layoutProfile = findViewById(R.id.layout_profile);
        txt_follow_status = findViewById(R.id.txt_follow_status);

        ic_back = findViewById(R.id.ic_back);
        ic_back.setOnClickListener(v -> finish());

        txt_follow_status.setOnClickListener(v -> {
//            if (MainActivity.getCurrentUser().isFollowing(user.getUser_id())) {
//                UserFirebase.unfollowUser(user.getUsername());
                apitiktok.follow(user.getUser_id(),MainActivity.getCurrentUser().getUser_id()).enqueue(new Callback<Root<User>>() {
                    @Override
                    public void onResponse(Call<Root<User>> call, Response<Root<User>> response) {
                        User user1 = response.body().data;
                        Log.d("UnFollow thanh cong", response.message());
                        MainActivity.setCurrent(user1);
                        if(response.body().message.contains("Đã bỏ follow " + user.getFullName())){
                            int follower = user.getNum_followers();
                            user.setNum_followers(follower - 1);
                            updateUI(user);
                        }
                        else {
                            int follower = user.getNum_followers();
                            user.setNum_followers(follower + 1);
                            updateUI(user);
                        }
//                        MainActivity.setCurrent(user1);
                    }

                    @Override
                    public void onFailure(Call<Root<User>> call, Throwable t) {
                        Log.d("UnFollow that bai", t.getMessage());
                    }
                });
        });

        loadData(user_idText);

        MyUtil.setLightStatusBar(this);
    }

    private void loadData(String user_idText) {
        apitiktok.getByIdUser(user_idText).enqueue(new Callback<Root<User>>() {
            @Override
            public void onResponse(Call<Root<User>> call, Response<Root<User>> response) {
                Toast.makeText(context, "Loading...", Toast.LENGTH_SHORT).show();
                User user = response.body().data;
                updateUI(user);
            }

            @Override
            public void onFailure(Call<Root<User>> call, Throwable t) {
                Log.e(TAG, "loadData: " + t.getMessage());
            }
        });
        // For first time load data
//		query.get().addOnSuccessListener(snapshot -> {
//			if (snapshot.exists()) {
//				Toast.makeText(this, "Loading...", Toast.LENGTH_SHORT).show();
//				User newUser = new User(snapshot.getChildren().iterator().next());
//				updateUI(newUser);
//			}
//		});
//        UserFirebase.getUserByUsername(usernameText,
//                this::updateUI, error -> {
//                    Toast.makeText(context, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
//                    Log.e(TAG, "loadData: " + error.getMessage());
//                });
    }

    @SuppressLint("SetTextI18n")
    private void updateUI(User user) {
        this.user = user;
        fullname.setText(user.getFullName());
        numFollowers.setText(user.getNum_followers() + "");
        numFollowing.setText(user.getNum_following() + "");
        numLikes.setText(user.getNum_like() + "");
        username.setText(user.getUsername());
        if (MainActivity.isLoggedIn())
            setFollowStatus(MainActivity.getCurrentUser().isFollowing(user.getUser_id()));
        else
            txt_follow_status.setVisibility(View.GONE);
        try {
            String imgURL = RetrofitClient.getBaseUrl() +"/api/file/image/view?fileName=" + user.getAvatar();
            if (user.getAvatar() != null && !user.getAvatar().isEmpty()) {
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

        prepareRecyclerView(user);
    }

    private void setFollowStatus(boolean following) {
        if (following) {
            txt_follow_status.setText(getResources().getString(R.string.following));
            txt_follow_status.setTextColor(getResources().getColor(R.color.secondary_color));
            txt_follow_status.setBackground(getResources().getDrawable(R.drawable.text_box_bg_second));
        } else {
            txt_follow_status.setText(getResources().getString(R.string.follow));
            txt_follow_status.setTextColor(getResources().getColor(R.color.main_color));
            txt_follow_status.setBackground(getResources().getDrawable(R.drawable.text_box_bg));
        }
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
}
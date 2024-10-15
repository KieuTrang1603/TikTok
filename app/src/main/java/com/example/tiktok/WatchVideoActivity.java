package com.example.tiktok;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SnapHelper;

import com.bumptech.glide.Glide;
import com.example.tiktok.adapters.VideoFragmentAdapter;
import com.example.tiktok.models.Comment;
import com.example.tiktok.models.Root;
import com.example.tiktok.models.Video;
import com.example.tiktok.service.ApiInterface;
import com.example.tiktok.service.RetrofitClient;
import com.example.tiktok.utils.KeyboardUtil;
import com.example.tiktok.utils.MyUtil;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class WatchVideoActivity extends FragmentActivity {
    // Tag
    private static final String TAG = "WatchVideoActivity";

    RecyclerView recyclerView;
    Video video;
    final ApiInterface apitiktok = RetrofitClient.getInstance().create(ApiInterface.class);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_watch_video);
        getWindow().setNavigationBarColor(Color.WHITE);
        MyUtil.setDarkStatusBar(this);
        Context context = this;

        // Get the Intent that started this activity and extract the string
        Bundle extras = getIntent().getExtras();
        String videoId = extras.getString(Video.VIDEO_ID);

        String commentId = extras.getString(Comment.COMMENT_ID);
        recyclerView = findViewById(R.id.recycler_view_videos);

        ImageView img_avatar = findViewById(R.id.img_avatar);
        String imgURL = RetrofitClient.getBaseUrl() + "/api/file/image/view?fileName=" + MyUtil.user_current.getAvatar();
        try {
            if (MainActivity.isLoggedIn())
                Glide.with(this)
                        .load(imgURL)
                        .error(R.drawable.default_avatar)
                        .into(img_avatar);
            else
                Glide.with(this)
                        .load(R.drawable.default_avatar)
                        .into(img_avatar);
        } catch (Exception e) {
            Log.w(TAG, "Glide error: " + e.getMessage());
        }
        apitiktok.getVideoById(videoId).enqueue(new Callback<Root<Video>>() {
            @Override
            public void onResponse(Call<Root<Video>> call, Response<Root<Video>> response) {
                video = response.body().data;
                if (recyclerView.getAdapter() == null) {
                        Log.i(TAG, "onDataChange: " + video.getVideo_id());
                        List<Video> videos = new ArrayList<>();
                        videos.add(video);
                        VideoFragmentAdapter adapter = new VideoFragmentAdapter(videos, context);
                        if (commentId != null)
                            adapter.openCommentFragment(video, commentId);
                        recyclerView.setAdapter(adapter);
                        recyclerView.setLayoutManager(new LinearLayoutManager(context));
                    } else {
                        Log.i(TAG, "onDataChange: update");
                        VideoFragmentAdapter adapter = (VideoFragmentAdapter) recyclerView.getAdapter();
                        adapter.notifyItemChanged(0, video);
                    }
            }

            @Override
            public void onFailure(Call<Root<Video>> call, Throwable t) {
                Log.e(TAG, "Lay 1 video bi loi: " + t.getMessage());
            }
        });

        SnapHelper snapHelper = new PagerSnapHelper();
        snapHelper.attachToRecyclerView(recyclerView);

        ImageView backButton = findViewById(R.id.ic_back);
        backButton.setOnClickListener(v -> finish());

        EditText txt_comment_input = findViewById(R.id.txt_comment_input);
        ImageView ic_send_comment = findViewById(R.id.ic_send_comment);
        txt_comment_input.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int count, int after) {
                String content = charSequence.toString().trim();
                if (content.length() > 0)
                    ic_send_comment.setVisibility(View.VISIBLE);
                else ic_send_comment.setVisibility(View.GONE);
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        // Set onClickListener for ic_send_comment
        ic_send_comment.setOnClickListener(v -> handleSendComment(txt_comment_input));
    }

    private void handleSendComment(EditText txt_comment_input) {
        // Get comment content
        String content = txt_comment_input.getText().toString().trim();
        Comment newComment = new Comment();
        newComment.setContent(content);
        newComment.setTime(MyUtil.dateTimeToString(new Date()));
        newComment.setUser_id(MainActivity.getCurrentUser().getUser_id());
        newComment.setVideo_id(video.getVideo_id());

        // Add comment to firebase
//        CommentFirebase.addCommentToVideo(newComment, video);

        // Toast message
        Toast.makeText(this, "Bình luận thành công!", Toast.LENGTH_SHORT).show();

        // Clear comment input
        txt_comment_input.setText("");

        // Hide keyboard
        KeyboardUtil.hideKeyboard(this);
    }

    public void hideCommentFragment() {
        if (findViewById(R.id.fragment_comment_container) != null && findViewById(R.id.fragment_comment_container).getVisibility() == View.VISIBLE) {
            Log.i(TAG, "onBackPressed: HIDE KEYBOARD");
            KeyboardUtil.hideKeyboard(this);
            Log.i(TAG, "onBackPressed: HIDE LAYOUT_COMMENT");
            findViewById(R.id.fragment_comment_container).setVisibility(View.GONE);

            // Enable scroll
            RecyclerView recycler_view_videos = findViewById(R.id.recycler_view_videos);
            recycler_view_videos.removeOnItemTouchListener(VideoFragmentAdapter.disableTouchListener);
        }
    }
}
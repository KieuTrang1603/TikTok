package com.example.tiktok.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.tiktok.MainActivity;
import com.example.tiktok.R;
import com.example.tiktok.fragment.CommentFragment;
import com.example.tiktok.models.Root;
import com.example.tiktok.models.User;
import com.example.tiktok.models.Video;
import com.example.tiktok.service.ApiInterface;
import com.example.tiktok.service.RetrofitClient;
import com.example.tiktok.utils.MyUtil;
import com.example.tiktok.utils.RecyclerViewDisabler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class VideoFragmentAdapter extends RecyclerView.Adapter<VideoFragmentAdapter.VideoViewHolder> {

    private static final String TAG = "VideoFragementAdapter";
    public static RecyclerView.OnItemTouchListener disableTouchListener = new RecyclerViewDisabler() ;  // vô hiệu hoas sự kiện chạm
    Context context;
    Map<String, Boolean> isVideoInitiated = new HashMap<>();
    private final List<Video> videos;
    private final List<VideoViewHolder> holders = new ArrayList<>();
    final ApiInterface apitiktok = RetrofitClient.getInstance().create(ApiInterface.class);

    public VideoFragmentAdapter(List<Video> videos, Context context) {
        this.videos = videos;
        this.context = context;
    }

    //được gọi lai khi mo window
    @Override
    public void onViewAttachedToWindow(@NonNull VideoViewHolder holder) {
        super.onViewAttachedToWindow(holder);
    }
    @NonNull
    @Override
    public VideoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.video_layout, parent, false);

        return new VideoViewHolder(view);
    }
    public void openCommentFragment(Video video, String comment_id) {
        handleClickComment(video, comment_id);
    }
    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onBindViewHolder(@NonNull VideoFragmentAdapter.VideoViewHolder holder, int position) {
        // Check video is exist or not
        if (videos.get(position) == null)
            return;

        holders.add(holder);

        // Set info video once
        Video video = videos.get(position);
        holder.txt_username.setText(video.getUsername());
        holder.txt_content.setText(video.getContent());
        apitiktok.getByIdUser(video.getUser_id()).enqueue(new Callback<Root<User>>() {
            @Override
            public void onResponse(Call<Root<User>> call, Response<Root<User>> response) {
                User user = response.body().data;
                String imgURL = RetrofitClient.getBaseUrl() +"/api/file/image/view?fileName=" + user.getAvatar();
                try {
                    if (user.getAvatar() != null && !user.getAvatar().isEmpty()) {
                        Glide.with(context)
                                .load(imgURL)
                                .error(R.drawable.default_avatar)
                                .into(holder.img_avatar);
                    }else
                        // Hiển thị ảnh mặc định khi avatarUrl là null hoặc chuỗi rỗng
                        holder.img_avatar.setImageResource(R.drawable.default_avatar);
                } catch (Exception e) {
                    Log.w(TAG, "Glide error: " + e.getMessage());
                }
            }

            @Override
            public void onFailure(Call<Root<User>> call, Throwable t) {
                Log.d("Tai nguoi dung dang video that bai" , t.getMessage());
            }
        });
//        String imgURL = RetrofitClient.getBaseUrl() +"/api/file/image/view?fileName=" + video.getAvatar();
//        try {
//            if (video.getAvatar() != null && !video.getAvatar().isEmpty()) {
//                Glide.with(context)
//                        .load(imgURL)
//                        .error(R.drawable.default_avatar)
//                        .into(holder.img_avatar);
//            }else
//                // Hiển thị ảnh mặc định khi avatarUrl là null hoặc chuỗi rỗng
//                holder.img_avatar.setImageResource(R.drawable.default_avatar);
//        } catch (Exception e) {
//            Log.w(TAG, "Glide error: " + e.getMessage());
//        }
        // Xây dựng URL đầy đủ từ filename (đường dẫn tĩnh hoặc API trả về)
//        String videoUrl = RetrofitClient.getBaseUrl() +"/api/file/video/view?fileName=" + video.getFileName();
////        String videoUrl = "https://www.youtube.com/watch?v=CaJxE-isE5s&ab_channel=NinhD%C6%B0%C6%A1ngStory";
//        // Thiết lập VideoView với URL
//        Log.d(TAG, "Video URL: " + videoUrl);
//        holder.videoView.setVideoURI(Uri.parse(videoUrl));

        try {
            // Tự động phát video khi nó được cuộn vào view
            holder.videoView.setOnPreparedListener(mp -> {
                holder.videoView.start();
                mp.setLooping(true); // Nếu muốn video lặp lại
            });
        }catch (Exception e) {
            Log.e(TAG, "Hien thi video: " + e.getMessage());
        }
        updateUI(holder, video,position);

        boolean isLoaded = false;
        if (isVideoInitiated.containsKey(video.getVideo_id())) {
            isLoaded = Boolean.TRUE.equals(isVideoInitiated.get(video.getVideo_id()));
        } else {
            isVideoInitiated.put(video.getVideo_id(), false);
        }

        if (isLoaded)
            playVideo(holder.videoView, holder.img_pause);
        else {
            Log.w(TAG, "onBindViewHolder: Initiate video");
            //api them 1 view
            apitiktok.addview(video.getVideo_id()).enqueue(new Callback<Root<Video>>() {
                @Override
                public void onResponse(Call<Root<Video>> call, Response<Root<Video>> response) {
                    Log.d("Thanh cong",response.message());
                }

                @Override
                public void onFailure(Call<Root<Video>> call, Throwable t) {
                    Log.d("That bai",t.getMessage());
                }
            });
            initVideo(holder.videoView, video);
        }
    }
    private void handleClickAvatar(Video video) {
        MyUtil.goToUser(context, video.getUser_id());
    }

    private void updateUI(VideoViewHolder holder, Video video, int position) {
        // Set info video if change
        holder.txt_num_likes.setText(MyUtil.getNumberToText(video.getNum_like(), 2));
        holder.txt_num_comments.setText(MyUtil.getNumberToText(video.getNum_comments(), 2));

        // Set img_follow
        if (!MainActivity.isLoggedIn() || MainActivity.getCurrentUser().getUser_id().equals(video.getUser_id())) {
            holder.img_follow.setVisibility(View.GONE);
        } else {
            Log.d("Nguoi dung hien tại", MainActivity.getCurrentUser().toString());
            if (MainActivity.getCurrentUser().isFollowing(video.getUser_id())) {
                holder.img_follow.setImageResource(R.drawable.ic_following);
            } else {
                holder.img_follow.setImageResource(R.drawable.ic_follow);
            }
        }

//        // Check if video is liked or not
        if (!MainActivity.isLoggedIn() || !video.isLiked()) {
            holder.img_like.setImageResource(R.drawable.ic_like);
        } else {
            holder.img_like.setImageResource(R.drawable.ic_liked);
        }

        // Set onClickListener for video
        holder.videoView.setOnClickListener(v ->
                handleClickVideo(holder.videoView, holder.img_pause));

        // Set onClickListener for img_comment
        holder.img_comment.setOnClickListener(v ->
                handleClickComment(video, null));

        // Set onClickListener for img_like
        holder.img_like.setOnClickListener(v ->
                handleClickLike(holder,video ,position ));

        // Set onClickListener for img_follow
        holder.img_follow.setOnClickListener(v ->
                handleClickFollow(holder,video,position));

        // Set onClickListener for img_avatar
        holder.img_avatar.setOnClickListener(v -> handleClickAvatar(video));

        // Set onClickListener for img_share
        holder.img_share.setOnClickListener(v -> handleClickShare(video));
    }

    private void handleClickShare(Video video) {
        // Share video
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, video.getFileName());
        context.startActivity(Intent.createChooser(shareIntent, context.getResources().getString(R.string.share)));
    }

    private void handleClickFollow(VideoViewHolder holder, Video video, int position) {
        if (!MainActivity.isLoggedIn())
            Toast.makeText(context, "Bạn cần đăng nhập để thực hiện chức năng này", Toast.LENGTH_SHORT).show();
        else {
            User user = MainActivity.getCurrentUser();
                apitiktok.follow(video.getUser_id(),user.getUser_id()).enqueue(new Callback<Root<User>>() {
                    @Override
                    public void onResponse(Call<Root<User>> call, Response<Root<User>> response) {
                        User user1 = response.body().data;
                        Log.d("UnFollow thanh cong", response.message());
                        MainActivity.setCurrent(user1);
                        updateUI(holder,video,position);
                    }

                    @Override
                    public void onFailure(Call<Root<User>> call, Throwable t) {
                        Log.d("UnFollow that bai", t.getMessage());
                    }
                });
//            }
////                UserFirebase.unfollowUser(video.getUsername());
//            else{
//                apitiktok.follow(video.getUser_id(),user.getUser_id(),true).enqueue(new Callback<Root<User>>() {
//                    @Override
//                    public void onResponse(Call<Root<User>> call, Response<Root<User>> response) {
//                        Log.d("Follow thanh cong", response.message());
//                        //goi api thong bao
//                    }
//
//                    @Override
//                    public void onFailure(Call<Root<User>> call, Throwable t) {
//                        Log.d("Follow that bai", t.getMessage());
//                    }
//                });
//            }
//                UserFirebase.followUser(video.getUsername());
        }
    }

    private void handleClickLike(VideoViewHolder holder, Video video, int position) {
        if (MainActivity.isLoggedIn()) {
            User user = MainActivity.getCurrentUser();
//            if (video.isLiked()){
                apitiktok.like(video.getVideo_id(),user.getUser_id()).enqueue(new Callback<Root<Video>>() {
                    @Override
                    public void onResponse(Call<Root<Video>> call, Response<Root<Video>> response) {
                        Log.d(" thanh cong", response.message());
                        Video video1 = new Video();
                        video1 = response.body().data;
                        videos.set(position, video1);
                        notifyItemChanged(position);
                        updateUI(holder,video1,position);
//                        video= video1;
                    }

                    @Override
                    public void onFailure(Call<Root<Video>> call, Throwable t) {
                        Log.d(" that bai", t.getMessage());
                    }
                });
        } else
            Toast.makeText(context, "Bạn cần đăng nhập để thực hiện chức năng này", Toast.LENGTH_SHORT).show();
    }

    private void handleClickComment(Video video, String commentId) {
        FragmentActivity activity = (FragmentActivity) context;

        CommentFragment commentFragment;

        if (commentId == null)
            commentFragment = CommentFragment.getInstance(video, context);
        else
            commentFragment = CommentFragment.getInstance(video, context, commentId);

        // Add layout_comment to MainActivity
        activity.getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_comment_container, commentFragment)
                .commit();

        // Show layout_comment
        activity.findViewById(R.id.fragment_comment_container).setVisibility(View.VISIBLE);
    }

    private void handleClickVideo(VideoView videoView, ImageView img_pause) {
        if (videoView.isPlaying()) {
            Log.i(TAG, "onClick: pause");
            pauseVideo(videoView, img_pause);
        } else {
            Log.i(TAG, "onClick: play");
            playVideo(videoView, img_pause);
        }
    }

    @Override
    public int getItemCount() {
        return videos.size();
    }
    public List<Video> getVideos() {
        return videos;
    }

    public void setVideos(List<Video> newVideos) {
        for (Video video : newVideos) {
            if (!videos.contains(video)) {
                videos.add(video);
                notifyItemInserted(videos.size() - 1);
            } else {
                int index = videos.indexOf(video);
                notifyItemChanged(index, video);
            }
        }

//        for (Video video : videos) {
//            if (!newVideos.contains(video)) {
//                int index = videos.indexOf(video);
//                videos.remove(video);
//                notifyItemRemoved(index);
//            }
//        }
        Iterator<Video> iterator = videos.iterator();
        while (iterator.hasNext()) {
            Video video = iterator.next();
            if (!newVideos.contains(video)) {
                int index = videos.indexOf(video);
                iterator.remove(); // Sử dụng iterator để xóa video an toàn
                notifyItemRemoved(index);
            }
        }
    }
    @Override
    public long getItemId(int position) {
        Video video = videos.get(position);
        return video.getVideo_id().hashCode();
    }

    private void initVideo(VideoView videoView, Video video) {
        if (video.getFileName() != null) {
            String videoUrl = RetrofitClient.getBaseUrl() +"/api/file/video/view?fileName=" + video.getFileName();
            videoView.setVideoURI(Uri.parse(videoUrl));
            videoView.requestFocus();
            videoView.start();
            Log.i(TAG, "initVideo: " + video.getVideo_id());
            isVideoInitiated.put(video.getVideo_id(), true);
        } else {
            Toast.makeText(context, "Video không tồn tại", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onViewRecycled(@NonNull VideoViewHolder holder) {
        super.onViewRecycled(holder);
    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    public void playVideo(VideoView videoView, ImageView imgPause) {
        Log.i(TAG, "playVideo: ");
        videoView.start();
        imgPause.setVisibility(View.GONE);
    }

    public void pauseVideo(VideoView videoView, ImageView imgPause) {
        Log.i(TAG, "pauseVideo: ");
        videoView.pause();
        imgPause.setVisibility(View.VISIBLE);
    }

    public void pauseVideo() {
        for (VideoViewHolder holder : holders) {
            pauseVideo(holder.videoView, holder.img_pause);
        }
    }

    public class VideoViewHolder extends RecyclerView.ViewHolder {
        TextView txt_username, txt_content, txt_num_likes, txt_num_comments;
        VideoView videoView;
        ImageView img_comment, img_pause, img_like, img_follow, img_avatar, img_share;

        public VideoViewHolder(@NonNull View itemView) {
            super(itemView);
            txt_username = itemView.findViewById(R.id.txt_email);
            txt_content = itemView.findViewById(R.id.txt_content);
            txt_num_likes = itemView.findViewById(R.id.txt_num_likes);
            txt_num_comments = itemView.findViewById(R.id.txt_num_comments);
            videoView = itemView.findViewById(R.id.videoView);
            img_comment = itemView.findViewById(R.id.img_comment);
            img_pause = itemView.findViewById(R.id.img_pause);
            img_like = itemView.findViewById(R.id.img_like);
            img_follow = itemView.findViewById(R.id.img_follow);
            img_avatar = itemView.findViewById(R.id.img_avatar);
            img_share = itemView.findViewById(R.id.img_share);
//        return 0;
        }
    }
}

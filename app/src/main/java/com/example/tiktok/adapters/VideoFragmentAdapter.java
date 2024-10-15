package com.example.tiktok.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
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
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;

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
    public static RecyclerView.OnItemTouchListener disableTouchListener = new RecyclerViewDisabler();  // vô hiệu hoas sự kiện chạm
    Context context;
    Map<String, Boolean> isVideoInitiated = new HashMap<>();
    private final List<Video> videos;
    private final List<VideoViewHolder> holders = new ArrayList<>();
//    private ExoPlayer player;
    private final Map<String, SimpleExoPlayer> playerMap = new HashMap<>(); // Lưu trữ player cho mỗi video
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
    public void onBindViewHolder(@NonNull VideoViewHolder holder, int position) {
        // Check video is exist or not
        if (videos.get(position) == null)
            return;

        holders.add(holder);

        // Set info video once
        Video video = videos.get(position);
        holder.txt_username.setText(video.getUsername());
        holder.txt_content.setText(video.getContent());
        String imgURL = RetrofitClient.getBaseUrl() + "/api/file/image/view?fileName=" + video.getAvatar();
        try {
            if (video.getAvatar() != null && !video.getAvatar().isEmpty()) {
                Glide.with(context)
                        .load(imgURL)
                        .error(R.drawable.default_avatar)
                        .into(holder.img_avatar);
            } else
                // Hiển thị ảnh mặc định khi avatarUrl là null hoặc chuỗi rỗng
                holder.img_avatar.setImageResource(R.drawable.default_avatar);
        } catch (Exception e) {
            Log.w(TAG, "Glide error: " + e.getMessage());
        }
        updateUI(holder, video,position);
//        boolean isLoaded = isVideoInitiated.getOrDefault(video.getVideo_id(), false);;
////        if (isVideoInitiated.containsKey(video.getVideo_id())) {
////            isLoaded = Boolean.TRUE.equals(isVideoInitiated.get(video.getVideo_id()));
////        } else {
////            isVideoInitiated.put(video.getVideo_id(), false);
////        }
//        if (isLoaded) {
//           playVideo(holder.playerView,holder.img_pause);
//        } else {
//            // Tăng view cho video
//            apitiktok.addview(video.getVideo_id()).enqueue(new Callback<Root<Video>>() {
//                @Override
//                public void onResponse(Call<Root<Video>> call, Response<Root<Video>> response) {
//                    Log.d("Add View Success", response.message());
//                }
//
//                @Override
//                public void onFailure(Call<Root<Video>> call, Throwable t) {
//                    Log.e("Add View Failed", t.getMessage());
//                }
//            });
//
//            // Khởi tạo video khi nó chưa được tải trước đó
//            initVideo(holder.playerView, video);
//        }
        SimpleExoPlayer player = playerMap.get(video.getVideo_id());

        if (player == null) {
            initVideo(holder.playerView, video);
        } else {
            holder.playerView.setPlayer(player); // Gắn lại player vào PlayerView khi tái sử dụng
            if (Boolean.TRUE.equals(isVideoInitiated.get(video.getVideo_id()))) {
                playVideo(holder.playerView, holder.img_pause);
            } else {
                // Tăng view cho video chỉ khi chưa được khởi tạo lần đầu
                apitiktok.addview(video.getVideo_id()).enqueue(new Callback<Root<Video>>() {
                    @Override
                    public void onResponse(Call<Root<Video>> call, Response<Root<Video>> response) {
                        Log.d("Add View Success", response.message());
                    }

                    @Override
                    public void onFailure(Call<Root<Video>> call, Throwable t) {
                        Log.e("Add View Failed", t.getMessage());
                    }
                });
                isVideoInitiated.put(video.getVideo_id(), true); // Đánh dấu video đã được khởi tạo
                playVideo(holder.playerView, holder.img_pause); // Chỉ phát video nếu nó chưa được phát
            }
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
        if (video.getTypeUser() == 2) {
            holder.img_follow.setVisibility(View.GONE);
        } else if (video.getTypeUser() == 1) {
            holder.img_follow.setImageResource(R.drawable.ic_following);
        } else {
            holder.img_follow.setImageResource(R.drawable.ic_follow);
        }

//        // Check if video is liked or not
        if (!MainActivity.isLoggedIn() || !video.isLiked()) {
            holder.img_like.setImageResource(R.drawable.ic_like);
        } else {
            holder.img_like.setImageResource(R.drawable.ic_liked);
        }

        // Set onClickListener for video
//        holder.playerView.setOnClickListener(v ->
//                handleClickVideo(holder.playerView, holder.img_pause));

        holder.img_pause.setOnClickListener(v -> {
            if (holder.player != null && holder.player.isPlaying()) {
                pauseVideo(holder.playerView, holder.img_pause);
            } else {
                playVideo(holder.playerView, holder.img_pause);
            }
        });

        // Set onClickListener for img_comment
        holder.img_comment.setOnClickListener(v ->
                handleClickComment(video, null));

        // Set onClickListener for img_like
        holder.img_like.setOnClickListener(v ->
                handleClickLike(holder, video, position));

        // Set onClickListener for img_follow
        holder.img_follow.setOnClickListener(v ->
                handleClickFollow(holder, video, position));

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
            apitiktok.follow(video.getUser_id(), user.getUser_id()).enqueue(new Callback<Root<User>>() {
                @Override
                public void onResponse(Call<Root<User>> call, Response<Root<User>> response) {
                    User user1 = response.body().data;
                    Log.d("UnFollow thanh cong", response.message());
                    MainActivity.setCurrent(user1);
                    if(response.body().data.isFollow()){
                        MyUtil.user_current.follow(user.getUser_id());
                        updateUI(holder, video, position);
                    } else {
                        MyUtil.user_current.unfollow(video.getUser_id());
                        updateUI(holder, video, position);
                    }
                }

                @Override
                public void onFailure(Call<Root<User>> call, Throwable t) {
                    Log.d("UnFollow that bai", t.getMessage());
                }
            });
        }
    }

    private void handleClickLike(VideoViewHolder holder, Video video, int position) {
        if (MainActivity.isLoggedIn()) {
            User user = MainActivity.getCurrentUser();
//            if (video.isLiked()){
            apitiktok.like(video.getVideo_id(), user.getUser_id()).enqueue(new Callback<Root<Video>>() {
                @Override
                public void onResponse(Call<Root<Video>> call, Response<Root<Video>> response) {
                    Log.d(" thanh cong", response.message());
                    Video video1 = new Video();
                    video1 = response.body().data;
                    if(video1.isLike()) {
                        video.addlike(user.getUser_id());
                        updateUI(holder, video1, position);
//                        video= video1;
                    }
                    else {
                        video.unlike(user.getUser_id());
                        updateUI(holder, video1, position);
                    }
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
                .commitAllowingStateLoss();

        // Show layout_comment
        activity.findViewById(R.id.fragment_comment_container).setVisibility(View.VISIBLE);
    }

//    private void handleClickVideo(PlayerView playerView, ImageView img_pause) {
//        if (player != null && player.isPlaying()) {
//            Log.i(TAG, "onClick: pause");
//            pauseVideo(playerView, img_pause);
//        } else {
//            Log.i(TAG, "onClick: play");
//            playVideo(playerView, img_pause);
//        }
//    }

    @Override
    public int getItemCount() {
        return videos.size();
    }

    public List<Video> getVideos() {
        return videos;
    }

    public void setVideos(List<Video> newVideos, boolean isUpdate) {

        videos.clear();
        videos.addAll(newVideos);
        notifyDataSetChanged();
    }

    @Override
    public long getItemId(int position) {
        Video video = videos.get(position);
        return video.getVideo_id().hashCode();
    }

    public void initVideo(PlayerView playerView, Video video) {
        try {
            if (video.getFileName() != null) {
                SimpleExoPlayer player = playerMap.get(video.getVideo_id());
                if (player == null) { // Nếu chưa khởi tạo player cho video
                    player = new SimpleExoPlayer.Builder(context).build();
                    playerView.setPlayer(player);
                    playerMap.put(video.getVideo_id(), player); // Lưu player vào map

                    String videoUrl = RetrofitClient.getBaseUrl() + "/api/file/video/view-hls/" + video.getFileName() + "/master.m3u8";
                    MediaItem mediaItem = MediaItem.fromUri(Uri.parse(videoUrl));
                    player.setMediaItem(mediaItem);
                    player.prepare();
                    player.setRepeatMode(ExoPlayer.REPEAT_MODE_ONE);
                    isVideoInitiated.put(video.getVideo_id(), true);
                }
            } else {
                Toast.makeText(playerView.getContext(), "Video không tồn tại", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Log.e(TAG, "Hien thi video: " + e.getMessage());
        }
    }


    @Override
    public void onViewRecycled(@NonNull VideoViewHolder holder) {
        super.onViewRecycled(holder);
        SimpleExoPlayer player = playerMap.get(videos.get(holder.getBindingAdapterPosition()).getVideo_id());
        if (player != null) {
            player.release(); // Giải phóng ExoPlayer
            playerMap.remove(videos.get(holder.getBindingAdapterPosition()).getVideo_id()); // Xóa player khỏi map
        }
        isVideoInitiated.put(videos.get(holder.getBindingAdapterPosition()).getVideo_id(), false); // Đánh dấu video đã được tái sử dụngdeo đã được tái sử dụng
    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    public void playVideo(PlayerView PlayerView, ImageView imgPause) {
        Log.i(TAG, "playVideo: ");
//
        SimpleExoPlayer player = (SimpleExoPlayer) PlayerView.getPlayer();
        if (player != null) {
            player.play();
            imgPause.setVisibility(View.GONE); // Ẩn biểu tượng tạm dừng
        } else {
            Log.e(TAG, "Player is null in playVideo");
        }
    }

    public void pauseVideo(PlayerView PlayerView, ImageView imgPause) {
        Log.i(TAG, "pauseVideo: ");
//        if (PlayerView.getPlayer() != null) {
//            PlayerView.getPlayer().pause();
//            imgPause.setVisibility(View.VISIBLE); // Hiển thị biểu tượng tạm dừng
//        }
        SimpleExoPlayer player = (SimpleExoPlayer) PlayerView.getPlayer();
        if (player != null) {
            player.pause();
            imgPause.setVisibility(View.VISIBLE); // Hiển thị biểu tượng tạm dừng
        } else {
            Log.e(TAG, "Player is null in pauseVideo");
        }
    }


    public void pauseVideo() {
        for (VideoViewHolder holder : holders) {
            pauseVideo(holder.playerView, holder.img_pause);
        }
    }

    public class VideoViewHolder extends RecyclerView.ViewHolder {
        TextView txt_username, txt_content, txt_num_likes, txt_num_comments;
        VideoView videoView;
        ImageView img_comment, img_like, img_follow, img_avatar, img_share;
        public  ImageView img_pause;
        private boolean isPlaying = true;
        public PlayerView playerView;
        private SimpleExoPlayer player;

        public VideoViewHolder(@NonNull View itemView) {
            super(itemView);
            txt_username = itemView.findViewById(R.id.txt_email);
            txt_content = itemView.findViewById(R.id.txt_content);
            txt_num_likes = itemView.findViewById(R.id.txt_num_likes);
            txt_num_comments = itemView.findViewById(R.id.txt_num_comments);
            playerView = itemView.findViewById(R.id.playerView);
            img_comment = itemView.findViewById(R.id.img_comment);
            img_pause = itemView.findViewById(R.id.img_pause);
            img_like = itemView.findViewById(R.id.img_like);
            img_follow = itemView.findViewById(R.id.img_follow);
            img_avatar = itemView.findViewById(R.id.img_avatar);
            img_share = itemView.findViewById(R.id.img_share);
        }
    }
    private void handleItemClick(VideoViewHolder holder, Video video, int position) {
        updateUI(holder, video, position);
    }
//    public void releasePlayer() {
//        if (player != null) {
//            player.release();
//            player = null;
//        }
//    }

//    public void playWhenVisible(PlayerView playerView, ImageView imgPause, int position) {
//        if (isVisible(playerView)) {  // Kiểm tra nếu PlayerView hiển thị trên màn hình
//            player.setPlayWhenReady(true);  // Phát video khi xuất hiện trên màn hình
//        } else {
//            player.setPlayWhenReady(false);  // Tạm dừng nếu không ở trong tầm nhìn
//        }
//    }
//
//    // Hàm kiểm tra xem PlayerView có đang hiển thị trên màn hình hay không
//    private boolean isVisible(View view) {
//        Rect rect = new Rect();
//        view.getGlobalVisibleRect(rect);
//        Rect scrollBounds = new Rect();
//        ((RecyclerView) view.getParent()).getGlobalVisibleRect(scrollBounds);
//        return rect.bottom <= scrollBounds.bottom && rect.top >= scrollBounds.top;
//    }
}

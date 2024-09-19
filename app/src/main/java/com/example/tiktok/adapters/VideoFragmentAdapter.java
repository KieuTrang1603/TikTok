package com.example.tiktok.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tiktok.R;
import com.example.tiktok.models.Video;
import com.example.tiktok.utils.RecyclerViewDisabler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class VideoFragmentAdapter extends RecyclerView.Adapter<VideoFragmentAdapter.VideoViewHolder> {

    private static final String TAG = "VideoFragementAdapter";
    public static RecyclerView.OnItemTouchListener disableTouchListener = new RecyclerViewDisabler() ;  // vô hiệu hoas sự kiện chạm
    Context context;
    Map<String, Boolean> isVideoInitiated = new HashMap<>();
    private final List<Video> videos;
    private final List<VideoViewHolder> holders = new ArrayList<>();

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

    @Override
    public void onBindViewHolder(@NonNull VideoFragmentAdapter.VideoViewHolder holder, int position) {

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

        for (Video video : videos) {
            if (!newVideos.contains(video)) {
                int index = videos.indexOf(video);
                videos.remove(video);
                notifyItemRemoved(index);
            }
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
        }
    }
}

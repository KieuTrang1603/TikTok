package com.example.tiktok.adapters;

import static androidx.core.content.ContentProviderCompat.requireContext;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.tiktok.MainActivity;
import com.example.tiktok.R;
import com.example.tiktok.models.Root;
import com.example.tiktok.models.Video;
import com.example.tiktok.service.ApiInterface;
import com.example.tiktok.service.RetrofitClient;
import com.example.tiktok.utils.MyUtil;

import java.util.Iterator;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class VideoGridAdapter extends RecyclerView.Adapter<VideoGridAdapter.ViewHolder> {
    private static final String TAG = "VideoGridAdapter";

    List<Video> videos;
    Context context;
    final ApiInterface apitiktok = RetrofitClient.getInstance().create(ApiInterface.class);
    public VideoGridAdapter(List<Video> videos, Context context) {
        this.videos = videos;
        this.context = context;
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = View.inflate(parent.getContext(), R.layout.video_grid_item, null);
        return new ViewHolder(view);
    }
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Video video = videos.get(position);

        updateView(holder, video);

        //hide delete button if user xem
        if (MainActivity.getCurrentUser().getUser_id().equals(video.getUser_id())) {
            holder.txt_delete_video.setOnClickListener(v -> handleDeleteVideo(video, position));
        } else {
            holder.txt_delete_video.setVisibility(View.GONE);
        }

        holder.txt_delete_video.setOnClickListener(v -> handleDeleteVideo(video, position));
    }

    private void updateView(ViewHolder holder, Video video) {
        Log.i(TAG, "updateView: " + video.getContent());
        holder.txt_num_likes.setText(String.valueOf(video.getNum_like()));
        holder.txt_num_views.setText(String.valueOf(video.getNum_views()));
        String videoUrl = RetrofitClient.getBaseUrl() +"/api/file/video/view?fileName=" + video.getFileName();
        try {
            Glide.with(context)
                    .load(videoUrl)
                    .error(R.drawable.img_404)
                    .into(holder.img_preview);
        } catch (Exception e) {
            Log.w(TAG, "Glide error: " + e.getMessage());
        }


        holder.img_preview.setOnClickListener(v -> handleImageClick(video));
    }
    private void handleImageClick(Video video) {
        MyUtil.goToVideo((Activity) context, video.getVideo_id());
    }

    private void handleDeleteVideo(Video video, int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Xóa video")
                .setMessage("Bạn có chắc chắn muốn xóa video này?")
                .setPositiveButton("Có", (dialog, which) -> {
                    apitiktok.deletevideo(video.getVideo_id()).enqueue(new Callback<Root<Video>>() {
                        @Override
                        public void onResponse(Call<Root<Video>> call, Response<Root<Video>> response) {
                            if(response.isSuccessful()){
                                Toast.makeText(context, "Xóa video thành công", Toast.LENGTH_SHORT).show();
                                videos.remove(position);
                                notifyItemRemoved(position);
                            }
                        }

                        @Override
                        public void onFailure(Call<Root<Video>> call, Throwable t) {
                            Log.e("Xóa thất bại", t.getMessage());
                        }
                    });
                })
                .setNegativeButton("Hủy", (dialog, which) -> dialog.dismiss())
                .show();
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position, @NonNull List<Object> payloads) {
        if (payloads.isEmpty())
            super.onBindViewHolder(holder, position, payloads);
        else if (position >= videos.size())
            notifyItemInserted(position);
        else {
            if (payloads.get(0) instanceof Video) {
                Video video = (Video) payloads.get(0);
                updateView(holder, video);
            }
        }
    }
    public int getItemCount() {
        return videos.size();
    }
    public void setVideos(List<Video> newVideos) {
        //kiểm tra video đã có trong danh sách chưa
        for (Video video : newVideos) {
            if (!videos.contains(video)) {
                videos.add(video);
                notifyItemInserted(videos.size() - 1);
            } else {
                int index = videos.indexOf(video);
                notifyItemChanged(index, video);
            }
        }
//        videos.removeAll(newVideos);
//        notifyDataSetChanged();
//        for (Video video : videos) {
//            if (!newVideos.contains(video)) {
//                int index = videos.indexOf(video);
//                videos.remove(video);
//                notifyItemChanged(index, video);
////                break;
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
    public List<Video> getVideos() {
        return videos;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView txt_num_likes, txt_num_views, txt_delete_video;
        ImageView img_preview;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txt_num_likes = itemView.findViewById(R.id.txt_num_likes);
            txt_num_views = itemView.findViewById(R.id.txt_num_views);
            img_preview = itemView.findViewById(R.id.img_preview);
            txt_delete_video = itemView.findViewById(R.id.txt_delete_video);
        }
    }
}

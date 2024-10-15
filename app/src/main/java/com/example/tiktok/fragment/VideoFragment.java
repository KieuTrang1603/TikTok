package com.example.tiktok.fragment;

import android.content.Context;
import android.graphics.Rect;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SnapHelper;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.tiktok.R;
import com.example.tiktok.adapters.VideoFragmentAdapter;
import com.example.tiktok.models.Data;
import com.example.tiktok.models.Root;
import com.example.tiktok.models.Video;
import com.example.tiktok.service.ApiInterface;
import com.example.tiktok.service.RetrofitClient;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class VideoFragment extends Fragment {

    public static final String TAG = "VideoFragment";
    Context context;
    RecyclerView recyclerView;
    SwipeRefreshLayout swipeRefreshLayout;
    private List<Video> videos = new ArrayList<>();
    private VideoFragmentAdapter adapter;
    final ApiInterface apitiktok = RetrofitClient.getInstance().create(ApiInterface.class);

    private static final VideoFragment instance = new VideoFragment();

    public VideoFragment() { }

    public static VideoFragment getInstance() {
        return instance;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView: ");

        View view = inflater.inflate(R.layout.fragment_video, container, false);

        context = view.getContext();

        recyclerView = view.findViewById(R.id.recycler_view_videos);
        swipeRefreshLayout = view.findViewById(R.id.swipe_refresh);
        recyclerView.setHasFixedSize(true);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(view.getContext());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        recyclerView.setLayoutManager(linearLayoutManager);

        loadVideos();
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadVideos();
                swipeRefreshLayout.setRefreshing(false);
            }
        });

        SnapHelper snapHelper = new PagerSnapHelper();
        snapHelper.attachToRecyclerView(recyclerView);
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                handleVideoPlayback(); // Gọi hàm để kiểm soát phát video
            }
        });
//         Inflate the layout for this fragment
        return view;
    }

    public void loadVideos() {
        apitiktok.getAllVideo(null,null, null, null).enqueue(new Callback<Root<Data<Video>>>() {
            @Override
            public void onResponse(Call<Root<Data<Video>>> call, Response<Root<Data<Video>>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Video> listVideos =  response.body().data.content;
                    videos = listVideos;
//                    VideoFragmentAdapter adapter = (VideoFragmentAdapter) recyclerView.getAdapter();
                    if (adapter != null) {
                        adapter = new VideoFragmentAdapter(listVideos, context);
                        recyclerView.setAdapter(adapter);
                    } else {
                        adapter = new VideoFragmentAdapter(listVideos, context);
                        recyclerView.setAdapter(adapter);
                    }
                }
            }
            @Override
            public void onFailure(Call<Root<Data<Video>>> call, Throwable t) {
                Log.e(TAG, "loadVideos: " + t.getMessage());
            }
        });
}
    public void updateUI(boolean isUpdate) {
        if (recyclerView.getAdapter() != null) {
            VideoFragmentAdapter adapter = (VideoFragmentAdapter) recyclerView.getAdapter();
            adapter.setVideos(adapter.getVideos(), isUpdate);
        }
    }
    public void pauseVideo() {
        VideoFragmentAdapter adapter = (VideoFragmentAdapter) recyclerView.getAdapter();
        if (adapter != null) {
            adapter.pauseVideo();
        }
    }
//    @Override
//    public void onHiddenChanged(boolean hidden) {
//        super.onHiddenChanged(hidden);
//        if (!hidden) {
//            // Fragment đang hiển thị trở lại, cập nhật giao diện hoặc tải lại dữ liệu
//            updateUI(); // Hoặc loadVideos() nếu cần
//        }
//    }

    // Hàm này sẽ chỉ thêm một video mới vào danh sách đã có
    public void addNewVideo(Video newVideo) {
        if (videos.add(newVideo)) { // Kiểm tra nếu video đã được xóa thành công
            VideoFragmentAdapter adapter = (VideoFragmentAdapter) recyclerView.getAdapter();
            adapter.setVideos(videos, false);
            adapter.notifyDataSetChanged(); // Thông báo dữ liệu đã thay đổi
        } else {
            Log.d("addVideo", "Không thể them video.");
        }
    }

    public void deleteVideo(Video newVideo) {

        if (videos.remove(newVideo)) { // Kiểm tra nếu video đã được xóa thành công
            VideoFragmentAdapter adapter = (VideoFragmentAdapter) recyclerView.getAdapter();
            adapter.setVideos(videos, false);
            adapter.notifyDataSetChanged(); // Thông báo dữ liệu đã thay đổi
        } else {
            Log.d("deleteVideo", "Không thể xóa video: video không tồn tại trong danh sách.");
        }
    }
    public void reloadData(){
        updateUI(true);
    }

    private void handleVideoPlayback() {
        LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
        if (layoutManager != null) {
            int firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition();
            int lastVisibleItemPosition = layoutManager.findLastVisibleItemPosition();
            Log.d(TAG, "postion f video at position: " + firstVisibleItemPosition);
            Log.d(TAG, "postion l video at position: " + lastVisibleItemPosition);
            boolean hasPlayed = false; // Biến để theo dõi video đã phát hay chưa

            for (int i = firstVisibleItemPosition; i <= lastVisibleItemPosition; i++) {
                VideoFragmentAdapter.VideoViewHolder holder = (VideoFragmentAdapter.VideoViewHolder) recyclerView.findViewHolderForAdapterPosition(i);
                if (holder != null) {
                    // Kiểm tra xem video trong holder có nằm trong tầm nhìn không
                    if (isVisible(holder.itemView)) {
                        if (!hasPlayed) {
                            Log.d(TAG, "Playing video at position: " + i);
                            adapter.playVideo(holder.playerView, holder.img_pause); // Phát video
                            hasPlayed = true; // Đánh dấu rằng một video đã phát
                        } else {
                            Log.d(TAG, "Pausing video at position: " + i);
                            adapter.pauseVideo(holder.playerView, holder.img_pause); // Tạm dừng video này nếu đã có video khác đang phát
                        }
                    } else {
                        Log.d(TAG, "Pausing video at position: " + i);
                        adapter.pauseVideo(holder.playerView, holder.img_pause); // Tạm dừng video nếu không nằm trong tầm nhìn
                    }
                }
            }
        }
    }
    private boolean isVisible(View view) {
        Rect rect = new Rect();
        view.getGlobalVisibleRect(rect);
        Rect scrollBounds = new Rect();
        recyclerView.getGlobalVisibleRect(scrollBounds);
        return rect.bottom > scrollBounds.top && rect.top < scrollBounds.bottom;
    }
}
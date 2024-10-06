package com.example.tiktok.fragment;

import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SnapHelper;

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
    private List<Video> videos = new ArrayList<>();
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
        recyclerView.setHasFixedSize(true);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(view.getContext());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        recyclerView.setLayoutManager(linearLayoutManager);

        loadVideos();

        SnapHelper snapHelper = new PagerSnapHelper();
        snapHelper.attachToRecyclerView(recyclerView);

//         Inflate the layout for this fragment
        return view;
    }

    private void loadVideos() {
        apitiktok.getAllVideo(null,null, null, null).enqueue(new Callback<Root<Data<Video>>>() {
            @Override
            public void onResponse(Call<Root<Data<Video>>> call, Response<Root<Data<Video>>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Video> listVideos =  response.body().data.content;
                    videos = listVideos;
                    VideoFragmentAdapter adapter = (VideoFragmentAdapter) recyclerView.getAdapter();
                    if (adapter != null) {
                        adapter.setVideos(listVideos);
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
    public void updateUI() {
        if (recyclerView.getAdapter() != null) {
            VideoFragmentAdapter adapter = (VideoFragmentAdapter) recyclerView.getAdapter();
            adapter.setVideos(adapter.getVideos());
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
        // Thêm video mới vào danh sách hiện tại
        videos.add(newVideo);
        VideoFragmentAdapter adapter = (VideoFragmentAdapter) recyclerView.getAdapter();
        adapter.setVideos(videos);
    }
}
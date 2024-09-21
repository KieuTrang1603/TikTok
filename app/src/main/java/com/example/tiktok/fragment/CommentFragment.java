package com.example.tiktok.fragment;

import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.tiktok.R;
import com.example.tiktok.models.Comment;
import com.example.tiktok.models.Video;

import java.util.ArrayList;
import java.util.List;


public class CommentFragment extends Fragment {
    public static final String TAG = "CommentFragment";
    public static Comment newComment = new Comment();
    private final Video video;
    Context context;
    RecyclerView recycler_view_comments, recycler_view_videos;
    List<Comment> comments = new ArrayList<>();
    static String commentId;
    private static final List<CommentFragment> instances = new ArrayList<>();

    private CommentFragment(Video video, Context context) {
        this.video = video;
        this.context = context;
    }

    // New instance
    public static CommentFragment getInstance(Video video, Context context) {
        for (CommentFragment instance : instances) {
            if (instance.video.getVideo_id().equals(video.getVideo_id())) {
                return instance;
            }
        }
        CommentFragment instance = new CommentFragment(video, context);
        instances.add(instance);
        return instance;
    }

    public static CommentFragment getInstance(Video video, Context context, String cID) {
        commentId = cID;
        return getInstance(video, context);
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_comment, container, false);
    }
}
package com.example.tiktok.fragment;

import android.os.Bundle;

import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ScrollView;
import android.widget.TextView;

import com.example.tiktok.R;
import com.example.tiktok.models.User;
import com.example.tiktok.models.Video;

import java.util.ArrayList;


public class SearchFragment extends Fragment {

    public static final String TAG = "SearchFragment";
    private final ArrayList<User> users = new ArrayList<>();
    private final ArrayList<Video> videos = new ArrayList<>();

    private static final SearchFragment instance = new SearchFragment();

    public SearchFragment() {
        // Required empty public constructor
    }

    public static SearchFragment getInstance() {
        return instance;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_search, container, false);
        RecyclerView recyclerView = view.findViewById(R.id.recyclerSearchView);
        RecyclerView recyclerViewForVideo = view.findViewById(R.id.recyclerSearchViewforVideo);
        SearchView searchView = view.findViewById(R.id.searchView);
        ScrollView scrollView = view.findViewById(R.id.scrollViewSearch);
        TextView txtSearch = view.findViewById(R.id.txtSearch);
        TextView lblAccount = view.findViewById(R.id.labelAccounts);
        TextView lblVideo = view.findViewById(R.id.labelVideos);
        return view;


    }
}
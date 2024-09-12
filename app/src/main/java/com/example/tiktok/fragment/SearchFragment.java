package com.example.tiktok.fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.tiktok.R;

import java.util.ArrayList;


public class SearchFragment extends Fragment {

    public static final String TAG = "SearchFragment";

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
        return inflater.inflate(R.layout.fragment_search, container, false);
    }
}
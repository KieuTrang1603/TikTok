package com.example.tiktok.fragment;

import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.tiktok.R;

import java.util.ArrayList;


public class NotificationFragment extends Fragment {

    public static final String TAG = "NotificationFragment";
    //private final ArrayList<Notification> notifications = new ArrayList<>();

    RecyclerView recyclerView;
    Context context;

    private static final NotificationFragment instance = new NotificationFragment();

    public NotificationFragment() {

    }

    public static NotificationFragment getInstance() {
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
        return inflater.inflate(R.layout.fragment_notification, container, false);
    }
}
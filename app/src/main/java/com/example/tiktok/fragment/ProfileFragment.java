package com.example.tiktok.fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.widget.NestedScrollView;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.tiktok.ChangePasswordActivity;
import com.example.tiktok.EditProfileActivity;
import com.example.tiktok.MainActivity;
import com.example.tiktok.R;
import com.example.tiktok.models.User;
import com.example.tiktok.utils.MyUtil;
import com.google.android.material.navigation.NavigationView;


public class ProfileFragment extends Fragment {

    public static final String TAG = "ProfileFragment";
    TextView fullName, num_followers, num_following, num_like, username, txt_post_video;
    ImageView avatar, ic_menu, ic_edit, ic_edit_avatar, ic_add_video;
    RecyclerView recyclerView;
    Context context;
    NestedScrollView scrollView;
    ConstraintLayout layoutProfile;
    NavigationView navigationView;
    EditText edt_video_content;

    Uri videoUri;

    private static final ProfileFragment instance = new ProfileFragment();

    public ProfileFragment() {
    }

    public static ProfileFragment getInstance() {
        return instance;
    }

    @SuppressLint("SetTextI18n")
    public void updateUI(User user) {
        if (user != null) {
            Log.i(TAG, "updateUI: " + user.toString());
//            if (user.isAdmin())
//                navigationView.getMenu().findItem(R.id.admin_panel).setVisible(true);
            fullName.setText(user.getFullName());
            num_followers.setText(user.getNum_followers() + "");
            num_following.setText(user.getNum_following() + "");
            num_like.setText(user.getNum_like() + "");
            username.setText(user.getUsername());
            try {
                Glide.with(context)
                        .load(user.getAvatar())
                        .error(R.drawable.default_avatar)
                        .into(avatar);
            } catch (Exception e) {
                Log.w(TAG, "Glide error: " + e.getMessage());
            }

            //prepareRecyclerView(user);
        }
    }

    @SuppressLint("SetTextI18n")
    public void updateUI() {
        User user = MainActivity.getCurrentUser();
//        Log.d(TAG, "updateUI: " + user.toString());
        updateUI(user);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        this.context = view.getContext();

        // Bind view
        fullName = view.findViewById(R.id.txt_fullname);
        num_followers = view.findViewById(R.id.txt_num_followers);
        num_following = view.findViewById(R.id.txt_num_following);
        num_like = view.findViewById(R.id.txt_num_likes);
        username = view.findViewById(R.id.txt_email);
        avatar = view.findViewById(R.id.img_avatar);
        recyclerView = view.findViewById(R.id.recycler_view_videos_grid);
        scrollView = view.findViewById(R.id.scroll_view);
        layoutProfile = view.findViewById(R.id.layout_profile);
        ic_menu = view.findViewById(R.id.ic_menu);
        ic_edit = view.findViewById(R.id.ic_edit);
        ic_edit_avatar = view.findViewById(R.id.ic_edit_avatar);
        navigationView = view.findViewById(R.id.nav_view);
        DrawerLayout drawer = view.findViewById(R.id.drawer_layout);
        txt_post_video = view.findViewById(R.id.txt_post_video);
        edt_video_content = view.findViewById(R.id.edt_video_content);
        ic_add_video = view.findViewById(R.id.ic_add_video);

        //ic_add_video.setOnClickListener(v -> handleAddVideo());

        //txt_post_video.setOnClickListener(v -> handlePostVideo());

        ic_menu.setOnClickListener(v -> drawer.open());

        ic_edit.setOnClickListener(v -> openEditProfileActivity());

        //ic_edit_avatar.setOnClickListener(v -> handleEditAvatar());

        navigationView.setNavigationItemSelectedListener(v -> {
            switch (v.getItemId()) {
//                case R.id.admin_panel:
//                    openAdminActivity();
//                    break;
                case R.id.edit_profile:
                    openEditProfileActivity();
                    break;
                case R.id.change_password:
                    openChangePasswordActivity();
                    break;
                case R.id.log_out:
                    MainActivity mainActivity = (MainActivity) requireActivity();
                    mainActivity.logOut();
                    Toast.makeText(context, "Log out successfully", Toast.LENGTH_SHORT).show();
                    break;
             }

            drawer.close();
            return false;
        });

        updateUI();

        return view;
    }

    private void openChangePasswordActivity() {
        Intent intent = new Intent(context, ChangePasswordActivity.class);
        startActivity(intent);
    }
    private void openEditProfileActivity() {
        Intent intent = new Intent(context, EditProfileActivity.class);
        startActivity(intent);
    }

//    private void openAdminActivity() {
//        Intent intent = new Intent(context, AdminActivity.class);
//        startActivity(intent);
//    }
}
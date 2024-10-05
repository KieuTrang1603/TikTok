package com.example.tiktok;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.PermissionChecker;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.tiktok.adapters.VideoFragmentAdapter;
import com.example.tiktok.fragment.NotLoginProfileFragment;
import com.example.tiktok.fragment.NotificationFragment;
import com.example.tiktok.fragment.ProfileFragment;
import com.example.tiktok.fragment.SearchFragment;
import com.example.tiktok.fragment.VideoFragment;
import com.example.tiktok.models.User;
import com.example.tiktok.utils.KeyboardUtil;
import com.example.tiktok.utils.MyUtil;

import kotlin.Unit;
import np.com.susanthapa.curved_bottom_navigation.CbnMenuItem;
import np.com.susanthapa.curved_bottom_navigation.CurvedBottomNavigationView;

public class MainActivity extends FragmentActivity {
    public static final String EXTRA_REGISTER = "register";
    public static final String EXTRA_LOGIN = "login";
    private static final String TAG = "MainActivity", NAV_TAG = "Navigation";
    public static final int LOGIN_REQUEST_CODE = 1;
    public static final int REGISTER_REQUEST_CODE = 2;
    public static final int REQUEST_CHANGE_AVATAR = 3;
    public static final int REQUEST_ADD_VIDEO = 4;
    private static User currentUser;

    @SuppressLint("StaticFieldLeak")
    CurvedBottomNavigationView nav;
    CbnMenuItem[] items;
    Fragment activeFragment;


    public static User getCurrentUser() {
        return currentUser;
    }

    public void setCurrentUser(User user) {
        if (user != null) {
                if (user.getUser_id() != null) {
                    Log.d(TAG, "setCurrentUser: " + user.getUser_id());
                    currentUser = user;
                    Log.d(TAG, "setCurrentUser: " + currentUser.getUser_id());
                    updateUI();
                    // start service
                    //startService(new Intent(this, NotificationService.class));
                }
            }
        else {
            currentUser = null;
            updateUI();
            //mAuth.signOut();
            //stop service
            //stopService(new Intent(this, NotificationService.class));
            // Log
            Log.i(TAG, "setCurrentUser: logged out");
        }
    }
    public static boolean isLoggedIn() {
        return currentUser != null;
    }

//    private void getUser(){
//        if (MyUtil.user_current.getUser_id() == null){
//            updateUI();
//        }else {
//            updateUI();
//            setCurrentUser(MyUtil.user_current);
//            }
//        }

    //ghi đè
    protected void onStart() {
        super.onStart();
        if (activeFragment == null || activeFragment instanceof VideoFragment) {
            MyUtil.setDarkStatusBar(this);
        } else {
            MyUtil.setLightStatusBar(this);
        }
//        Log.d(TAG, "setCurrentUser: " + MyUtil.user_current.getUser_id());
//        Intent intent = getIntent();
//        User currentUser = (User) intent.getSerializableExtra("USER");
        //Log.d(TAG, "setCurrentUser: " + currentUser.getUser_id());
        User user = MyUtil.user_current;
        if (user.getUser_id() != null){
            setCurrentUser(user);
        }
    }
    public void updateUI() {
        VideoFragment.getInstance().updateUI();
        ProfileFragment.getInstance().updateUI();
        //NotificationFragment.getInstance().updateUI();
    }

    // Open login activity
    public void openLoginActivity() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivityForResult(intent, LOGIN_REQUEST_CODE);
    }

    // Open register activity
    public void openRegisterActivity() {
        Intent intent = new Intent(this, RegisterActivity.class);
        startActivityForResult(intent, REGISTER_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == LOGIN_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                if (data != null) {
                    User user = (User) data.getSerializableExtra(LoginActivity.USER);
                    setCurrentUser(user);
                    changeNavItem(0);
                    Toast.makeText(this, "Đăng nhập thành công!", Toast.LENGTH_SHORT).show();
                }
            } else {
                if (data != null && data.getBooleanExtra(EXTRA_REGISTER, false)) {
                    openRegisterActivity();
                } else {
                    Toast.makeText(this, "Đăng nhập thất bại!", Toast.LENGTH_SHORT).show();
                }
            }
        } else if (requestCode == REGISTER_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                if (data != null) {
                    User user = (User) data.getSerializableExtra(RegisterActivity.USER);
                    setCurrentUser(user);
                    changeNavItem(0);
                    Toast.makeText(this, "Đăng ký thành công!", Toast.LENGTH_SHORT).show();
                }
            } else {
                if (data != null && data.getBooleanExtra(EXTRA_LOGIN, false)) {
                    openLoginActivity();
                } else {
                    Toast.makeText(this, "Đăng ký thất bại!", Toast.LENGTH_SHORT).show();
                }
            }
        } else if (requestCode == REQUEST_CHANGE_AVATAR || requestCode == REQUEST_ADD_VIDEO) {
            // Don't do anything
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        init();
//        getUser();
    }
    private void init() {
        if (nav == null) {
            Log.w(TAG, "init: nav is null");
            // Bind the view using the id
            nav = findViewById(R.id.nav);

            // Set list of items
            items = new CbnMenuItem[]{
                    new CbnMenuItem(R.drawable.ic_video, R.drawable.ic_video_avd, 0),
                    new CbnMenuItem(R.drawable.ic_search, R.drawable.ic_search_avd, 0),
                    new CbnMenuItem(R.drawable.ic_notification, R.drawable.ic_notification_avd, 0),
                    new CbnMenuItem(R.drawable.ic_profile, R.drawable.ic_profile_avd, 0)
            };

            // Add fragment to the container
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.fragment_container, SearchFragment.getInstance(), SearchFragment.TAG).hide(SearchFragment.getInstance())
                    .addToBackStack(SearchFragment.TAG)
                    .add(R.id.fragment_container, NotificationFragment.getInstance(), NotificationFragment.TAG).hide(NotificationFragment.getInstance())
                    .addToBackStack(NotificationFragment.TAG)
                    .add(R.id.fragment_container, NotLoginProfileFragment.getInstance(), NotLoginProfileFragment.TAG).hide(NotLoginProfileFragment.getInstance())
                    .addToBackStack(NotLoginProfileFragment.TAG)
                    .add(R.id.fragment_container, ProfileFragment.getInstance(), ProfileFragment.TAG).hide(ProfileFragment.getInstance())
                    .addToBackStack(ProfileFragment.TAG)
                    .add(R.id.fragment_container, VideoFragment.getInstance(), VideoFragment.TAG).hide(VideoFragment.getInstance())
                    .addToBackStack(VideoFragment.TAG)
                    .commit();

            // Execute transaction
            getSupportFragmentManager().executePendingTransactions();

            // Set the items
            nav.setMenuItems(items, 0);

            // Set the listener
            nav.setOnMenuItemClickListener((cbnMenuItem, integer) -> handleMenuItemClick(cbnMenuItem));

            // Set the default fragment
            getSupportFragmentManager().beginTransaction()
                    .show(VideoFragment.getInstance())
                    .commit();
            activeFragment = VideoFragment.getInstance();

            // Set navigation bar color
            getWindow().setNavigationBarColor(Color.WHITE);

            getPermission();
        }
    }

    private Unit handleMenuItemClick(CbnMenuItem cbnMenuItem) {
        switch (cbnMenuItem.getIcon()) {
            case R.drawable.ic_video:
                getSupportFragmentManager().beginTransaction()
                        .runOnCommit(() -> MyUtil.setDarkStatusBar(this))
                        .hide(activeFragment).show(VideoFragment.getInstance())
                        .commitAllowingStateLoss();
                activeFragment = VideoFragment.getInstance();
                Log.i(NAV_TAG, "Change to video fragment");
                break;
            case R.drawable.ic_search:
                getSupportFragmentManager().beginTransaction()
                        .runOnCommit(() -> MyUtil.setLightStatusBar(this))
                        .hide(activeFragment).show(SearchFragment.getInstance())
                        .commit();
                VideoFragment.getInstance().pauseVideo();
                Log.i(NAV_TAG, "Change to search fragment");
                activeFragment = SearchFragment.getInstance();
                break;
            case R.drawable.ic_notification:
                getSupportFragmentManager().beginTransaction()
                        .runOnCommit(() -> MyUtil.setLightStatusBar(this))
                        .hide(activeFragment).show(NotificationFragment.getInstance())
                        .commit();
                activeFragment = NotificationFragment.getInstance();
                VideoFragment.getInstance().pauseVideo();
                Log.i(NAV_TAG, "Change to notification fragment");
                break;
            case R.drawable.ic_profile:
                if (isLoggedIn()) {
                    getSupportFragmentManager().beginTransaction()
                            .runOnCommit(() -> MyUtil.setLightStatusBar(this))
                            .hide(activeFragment).show(ProfileFragment.getInstance())
                            .commit();
                    activeFragment = ProfileFragment.getInstance();
                } else {
                    getSupportFragmentManager().beginTransaction()
                            .runOnCommit(() -> MyUtil.setLightStatusBar(this))
                            .hide(activeFragment).show(NotLoginProfileFragment.getInstance())
                            .commit();
                    activeFragment = NotLoginProfileFragment.getInstance();
                }
                VideoFragment.getInstance().pauseVideo();
                Log.i(NAV_TAG, "Change to profile fragment");
                break;
        }
        return null;
    }

    private void getPermission() {
        // Get INTERNET permission if needed
        if (PermissionChecker.checkSelfPermission(this, android.Manifest.permission.INTERNET) != PermissionChecker.PERMISSION_GRANTED)
            requestPermissions(new String[]{android.Manifest.permission.INTERNET}, 1);

    }

    public void changeNavItem(int position) {
        if (nav != null) {
            nav.onMenuItemClick(position);
        }
    }

    public void hideCommentFragment() {
        Log.i(TAG, "onBackPressed: HIDE KEYBOARD");
        KeyboardUtil.hideKeyboard(this);
        Log.i(TAG, "onBackPressed: HIDE LAYOUT_COMMENT");
        findViewById(R.id.fragment_comment_container).setVisibility(View.GONE);

        // Enable scroll
        RecyclerView recycler_view_videos = findViewById(R.id.recycler_view_videos);
        recycler_view_videos.removeOnItemTouchListener(VideoFragmentAdapter.disableTouchListener);
    }

    public void logOut() {
        setCurrentUser(null);
        changeNavItem(0);
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Log.i(TAG, "onBackPressed: fragment_comment_container shown? " + (findViewById(R.id.fragment_comment_container) != null && findViewById(R.id.fragment_comment_container).getVisibility() == View.VISIBLE));
        // Set onBackPressed when fragment_comment_container is showing
        if (findViewById(R.id.fragment_comment_container) != null &&
                findViewById(R.id.fragment_comment_container).getVisibility() == View.VISIBLE) {
            hideCommentFragment();
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage(R.string.exit_confirm);
            builder.setPositiveButton(R.string.yes, (dialog, which) -> finish());
            builder.setNegativeButton(R.string.no, (dialog, which) -> dialog.dismiss());
            builder.show();
        }
    }
}
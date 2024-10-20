package com.example.tiktok.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.tiktok.MainActivity;
import com.example.tiktok.WatchProfileActivity;
import com.example.tiktok.WatchVideoActivity;
import com.example.tiktok.models.User;
import com.example.tiktok.models.Video;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class MyUtil {

    public static User user_current = new User();
    public static final String
            STATUS_BAR_LIGHT_MODE = "status_bar_light_mode",
            STATUS_BAR_DARK_MODE = "status_bar_dark_mode";

    public static void setStatusBarColor(String mode, Activity activity) {
        if (mode.equals(STATUS_BAR_DARK_MODE)) {
            activity.getWindow().setStatusBarColor(Color.BLACK);
            activity.getWindow().getDecorView().setSystemUiVisibility(0);
        } else if (mode.equals(STATUS_BAR_LIGHT_MODE)) {
            activity.getWindow().setStatusBarColor(Color.WHITE);
            activity.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }
    }
    public static void setLightStatusBar(Activity activity) {
        setStatusBarColor(STATUS_BAR_LIGHT_MODE, activity);
    }

    public static void setDarkStatusBar(Activity activity) {
        setStatusBarColor(STATUS_BAR_DARK_MODE, activity);
    }

    // Format date
    public static final String YYYY_MM_DD = "yyyy-MM-dd";
    public static final String DD_MM_YYYY = "dd-MM-yyyy";
    public static final String DD_MM_YYYY_HH_MM_SS = "dd-MM-yyyy HH:mm:ss";
    public static final String YYYY_MM_DD_HH_MM_SS = "yyyy-MM-dd HH:mm:ss";


    // Date to string
    public static String dateToStringFormat(Date date, String format) {
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        return sdf.format(date);
    }

    public static String dateTimeToString(Date date) {
        return dateToStringFormat(date, DD_MM_YYYY_HH_MM_SS);
    }

    public static String dateToString(Date date) {
        return dateToStringFormat(date, YYYY_MM_DD);
    }

    // String to date
    public static Date stringToDateFormat(String dateString, String format) {
//        SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault());
//        inputFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        try {
            return sdf.parse(dateString);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Date stringToDateTime(String date) {
        return stringToDateFormat(date, DD_MM_YYYY_HH_MM_SS);
    }

    public static Date stringToDate(String dateString) {
        return stringToDateFormat(dateString, YYYY_MM_DD);
    }
    // get number to text if large
    @SuppressLint("DefaultLocale")
    public static String getNumberToText(long number, int digit) {
        if (number > 999 && number < 1000000) {
            return String.format("%." + digit + "f", number / 1000.0) + "k";
        } else if (number > 999999) {
            return String.format("%." + digit + "f", number / 1000000.0) + "m";
        } else {
            return String.valueOf(number);
        }
    }
    public static void goToUser(Context context, String user_id) {
        if (context instanceof MainActivity) {
            MainActivity mainActivity = (MainActivity) context;
            if(MyUtil.user_current.getUser_id() == null){
                Toast.makeText(context, "Bạn cần đăng nhập để thực hiện chức năng này", Toast.LENGTH_SHORT).show();
            }
            else {
            if (MainActivity.getCurrentUser().getUser_id().equals(user_id)) {
                mainActivity.changeNavItem(3);
            } else {
                Intent intent = new Intent(mainActivity, WatchProfileActivity.class);
                Log.d("forward to profile ", user_id);
                intent.putExtra(User.TAG, user_id);
                mainActivity.startActivity(intent);
            }
        }
        }else {
            Intent intent = new Intent(context, WatchProfileActivity.class);
            Log.d("forward to profile ", user_id);
            intent.putExtra(User.TAG, user_id);
            context.startActivity(intent);
        }
    }

    public static void goToVideo(Activity activity, String videoId) {
        Intent intent = new Intent(activity, WatchVideoActivity.class);
        intent.putExtra(Video.VIDEO_ID, videoId);
        activity.startActivity(intent);
    }

    public static String getTimeAgo(Date date) {
        Date now = new Date();
        long diff = now.getTime() - date.getTime();
        // if less than 1 minute
        if (diff < TimeUnit.MINUTES.toMillis(1)) {
            return "Vừa xong";
        }
        // if less than 1 hour
        else if (diff < TimeUnit.HOURS.toMillis(1)) {
            return TimeUnit.MILLISECONDS.toMinutes(diff) + " phút trước";
        }
        // if less than 1 day
        else if (diff < TimeUnit.DAYS.toMillis(1)) {
            return TimeUnit.MILLISECONDS.toHours(diff) + " giờ trước";
        }
        // if less than 1 week
        else if (diff < TimeUnit.DAYS.toMillis(7)) {
            return TimeUnit.MILLISECONDS.toDays(diff) + " ngày trước";
        }
        // if less than 1 month
        else if (diff < TimeUnit.DAYS.toMillis(30)) {
            return TimeUnit.MILLISECONDS.toDays(diff) / 7 + " tuần trước";
        }
        // if less than 1 year
        else if (diff < TimeUnit.DAYS.toMillis(365)) {
            return TimeUnit.MILLISECONDS.toDays(diff) / 30 + " tháng trước";
        }
        // if more than 1 year
        else {
            return TimeUnit.MILLISECONDS.toDays(diff) / 365 + " năm trước";
        }
    }

    // get time ago
    public static String getTimeAgo(String dateString) {
        Date date = stringToDateTime(dateString);
        if (date != null) {
            return getTimeAgo(date);
        } else {
            return "Date is null";
        }
    }
}

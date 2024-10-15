package com.example.tiktok.models;

import com.example.tiktok.utils.MyUtil;
import com.google.firebase.database.DataSnapshot;

import java.util.Date;
import java.util.HashMap;

public class Notification {
    public static final String TYPE_FOLLOW = "follow";
    public static final String TYPE_COMMENT = "comment";
    public static final String TYPE_LIKE = "like";
    private String notification_id, user_id, video_id, comment_id, content, type_notification, time = MyUtil.dateTimeToString(new Date()), redirectTo;
    private Boolean seen = false;

    public Notification(Boolean seen, String redirectTo, String time, String type_notification, String content, String comment_id, String video_id, String user_id, String notification_id) {
        this.seen = seen;
        this.redirectTo = redirectTo;
        this.time = time;
        this.type_notification = type_notification;
        this.content = content;
        this.comment_id = comment_id;
        this.video_id = video_id;
        this.user_id = user_id;
        this.notification_id = notification_id;
    }

    public Notification() {
    }

    public Notification(DataSnapshot dataSnapshot) {
        HashMap<String, Object> data = (HashMap<String, Object>) dataSnapshot.getValue();
        this.notification_id = dataSnapshot.getKey();
        this.user_id = (String) data.get("username");
        this.content = (String) data.get("content");
        this.type_notification = (String) data.get("type");
        this.time = (String) data.get("time");
        this.redirectTo = (String) data.get("redirectTo");
        this.seen = (Boolean) data.get("seen");
    }
    public String getNotification_id() {
        return notification_id;
    }

    public void setNotification_id(String notification_id) {
        this.notification_id = notification_id;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getVideo_id() {
        return video_id;
    }

    public void setVideo_id(String video_id) {
        this.video_id = video_id;
    }

    public String getComment_id() {
        return comment_id;
    }

    public void setComment_id(String comment_id) {
        this.comment_id = comment_id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getType_notification() {
        return type_notification;
    }

    public void setType_notification(String type_notification) {
        this.type_notification = type_notification;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getRedirectTo() {
        return redirectTo;
    }

    public void setRedirectTo(String redirectTo) {
        this.redirectTo = redirectTo;
    }

    public Boolean getSeen() {
        return seen;
    }

    public void setSeen(Boolean seen) {
        this.seen = seen;
    }
}

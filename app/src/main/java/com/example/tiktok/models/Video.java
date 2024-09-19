package com.example.tiktok.models;

import android.util.Log;

import androidx.annotation.Nullable;

import com.example.tiktok.utils.MyUtil;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;

public class Video implements Serializable {
    // TAG
    public static final String TAG = "Video";
    public static final String VIDEO_ID = "videoId";

    private String video_id, user_id, title, description, content, link_video;
    private int num_like, num_comments, num_views;
    //private String date_uploaded = MyUtil.dateTimeToString(new Date());
    Date date_uploaded = new Date();
    private HashMap<String, Boolean> likes;
    private HashMap<String, Boolean> comments;

    public Video() {
        likes = new HashMap<>();
        comments = new HashMap<>();
    }

    public Video(String video_id, String user_id, HashMap<String, Boolean> comments, String description, String title, String content, String link_video, int num_like, int num_comments, int num_views, Date date_uploaded, HashMap<String, Boolean> likes) {
        this.video_id = video_id;
        this.user_id = user_id;
        this.comments = comments;
        this.description = description;
        this.title = title;
        this.content = content;
        this.link_video = link_video;
        this.num_like = num_like;
        this.num_comments = num_comments;
        this.num_views = num_views;
        this.date_uploaded = date_uploaded;
        this.likes = likes;
    }

    public String getVideo_id() {
        return video_id;
    }

    public void setVideo_id(String video_id) {
        this.video_id = video_id;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getLink_video() {
        return link_video;
    }

    public void setLink_video(String link_video) {
        this.link_video = link_video;
    }

    public int getNum_like() {
        return num_like;
    }

    public void setNum_like(int num_like) {
        this.num_like = num_like;
    }

    public int getNum_comments() {
        return num_comments;
    }

    public void setNum_comments(int num_comments) {
        this.num_comments = num_comments;
    }

    public int getNum_views() {
        return num_views;
    }

    public void setNum_views(int num_views) {
        this.num_views = num_views;
    }

    public Date getDate_uploaded() {
        return date_uploaded;
    }

    public void setDate_uploaded(Date date_uploaded) {
        this.date_uploaded = date_uploaded;
    }

    public HashMap<String, Boolean> getLikes() {
        return likes;
    }

    public void setLikes(HashMap<String, Boolean> likes) {
        this.likes = likes;
    }

    public HashMap<String, Boolean> getComments() {
        return comments;
    }

    public void setComments(HashMap<String, Boolean> comments) {
        this.comments = comments;
    }

    @Override
    public String toString() {
        return "Video{" +
                "video_id='" + video_id + '\'' +
                ", user_id='" + user_id + '\'' +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", content='" + content + '\'' +
                ", link_video='" + link_video + '\'' +
                ", num_like=" + num_like +
                ", num_comments=" + num_comments +
                ", num_views=" + num_views +
                ", date_uploaded='" + date_uploaded + '\'' +
                ", likes=" + likes +
                ", comments=" + comments +
                '}';
    }
}


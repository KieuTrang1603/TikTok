package com.example.tiktok.models;

import android.util.Log;

import androidx.annotation.Nullable;

import com.example.tiktok.MainActivity;
import com.example.tiktok.utils.MyUtil;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class Video implements Serializable {
    // TAG
    public static final String TAG = "Video";
    public static final String VIDEO_ID = "videoId";

    private String video_id, user_id, content, fileName;
    private int num_like, num_comments, num_views;
    //private String date_uploaded = MyUtil.dateTimeToString(new Date());
    Date date_uploaded = new Date();
    private List<String> likes;
    private List<String> comments;
    String username, avatar;

    public Video() {
//        likes = new HashMap<>();
//        comments = new HashMap<>();
    }

    public Video(String video_id, String user_id, List<String> comments, String content, String fileName, int num_like, int num_comments, int num_views, Date date_uploaded, List<String> likes) {
        this.video_id = video_id;
        this.user_id = user_id;
        this.comments = comments;
        this.content = content;
        this.fileName = fileName;
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

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
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

    public List<String> getLikes() {
        return likes;
    }

    public void setLikes(List<String> likes) {
        this.likes = likes;
    }

    public List<String> getComments() {
        return comments;
    }

    public void setComments(List<String> comments) {
        this.comments = comments;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    @Override
    public String toString() {
        return "Video{" +
                "video_id='" + video_id + '\'' +
                ", user_id='" + user_id + '\'' +
                ", content='" + content + '\'' +
                ", fileName='" + fileName + '\'' +
                ", num_like=" + num_like +
                ", num_comments=" + num_comments +
                ", num_views=" + num_views +
                ", date_uploaded='" + date_uploaded + '\'' +
                ", likes=" + likes +
                ", comments=" + comments +
                ", username=" + username +
                ", avatar=" + avatar +
                '}';
    }

    // Like
    public boolean isLiked() {
        return likes != null && likes.contains(MainActivity.getCurrentUser().getUser_id());
    }

    public void addlike(String user_id) {
        // Add to followings
        if (likes == null) likes = new ArrayList<>();
        likes.add(user_id);

        // Update numFollowing
        num_like++;
    }
    public void unlike(String user_id) {
        // Remove from followings
        if (likes != null) likes.remove(user_id);

        // Update numFollowing
        num_like--;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Video video = (Video) o;
        return video_id.equals(video.video_id); // So sánh dựa trên videoId
    }

    @Override
    public int hashCode() {
        return Objects.hash(video_id);
    }
}


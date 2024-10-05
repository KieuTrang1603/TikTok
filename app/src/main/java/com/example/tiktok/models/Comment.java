package com.example.tiktok.models;

import com.example.tiktok.MainActivity;
import com.example.tiktok.utils.MyUtil;

import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class Comment {
    public static final String TAG = "Comment";
    public static final String COMMENT_ID = "commentId";

    private String comment_id, user_id, video_id, username, content, replyToCommentId;
    private int num_replies , num_like ;
    private String time = MyUtil.dateTimeToString(new Date());
    private HashMap<String, Boolean> replies;
    private HashMap<String, Boolean> likes;

    public Comment(String comment_id, String user_id, String video_id, String content, String replyToCommentId, int num_replies, int num_like, String time, HashMap<String, Boolean> replies, HashMap<String, Boolean> likes) {
        this.comment_id = comment_id;
        this.user_id = user_id;
        this.video_id = video_id;
        this.content = content;
        this.replyToCommentId = replyToCommentId;
        this.num_replies = num_replies;
        this.num_like = num_like;
        this.time = time;
        this.replies = replies;
        this.likes = likes;
    }

    public Comment() {
        replies = new HashMap<>();
        likes = new HashMap<>();
    }

    public String getComment_id() {
        return comment_id;
    }

    public void setComment_id(String comment_id) {
        this.comment_id = comment_id;
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

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getReplyToCommentId() {
        return replyToCommentId;
    }

    public void setReplyToCommentId(String replyToCommentId) {
        this.replyToCommentId = replyToCommentId;
    }

    public int getNum_replies() {
        return num_replies;
    }

    public void setNum_replies(int num_replies) {
        this.num_replies = num_replies;
    }

    public int getNum_like() {
        return num_like;
    }

    public void setNum_like(int num_like) {
        this.num_like = num_like;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public HashMap<String, Boolean> getReplies() {
        return replies;
    }

    public void setReplies(HashMap<String, Boolean> replies) {
        this.replies = replies;
    }

    public HashMap<String, Boolean> getLikes() {
        return likes;
    }

    public void setLikes(HashMap<String, Boolean> likes) {
        this.likes = likes;
    }

    public boolean isValid() {
        return content != null && !content.isEmpty() &&
                username != null && !username.isEmpty();
    }

    public static void sortByTimeNewsest(List<Comment> comments) {
        // Sort comments by time
        comments.sort((o1, o2) -> {
            Date time1 = MyUtil.stringToDateTime(o1.getTime());
            Date time2 = MyUtil.stringToDateTime(o2.getTime());
            if (time1 != null && time2 != null) {
                return time2.compareTo(time1);
            }
            return 0;
        });
    }

    public boolean isLiked() {
        return likes != null && MainActivity.getCurrentUser() != null
                && likes.containsKey(MainActivity.getCurrentUser().getUser_id());
    }
}

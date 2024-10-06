package com.example.tiktok.models;

import com.example.tiktok.MainActivity;
import com.example.tiktok.utils.MyUtil;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class Comment {
    public static final String TAG = "Comment";
    public static final String COMMENT_ID = "commentId";

    private String comment_id, user_id, video_id, username, content, parent_comment_id, avatar,video_content;
    private int num_replies , num_like ;
    private String time = MyUtil.dateTimeToString(new Date());
    private List<String> replies;
    private List<String> likes;

    public Comment(String comment_id, String user_id, String video_id, String content, String parent_comment_id, int num_replies, int num_like, String time, List<String>replies, List<String> likes) {
        this.comment_id = comment_id;
        this.user_id = user_id;
        this.video_id = video_id;
        this.content = content;
        this.parent_comment_id = parent_comment_id;
        this.num_replies = num_replies;
        this.num_like = num_like;
        this.time = time;
        this.replies = replies;
        this.likes = likes;
    }

    public Comment() {
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

    public List<String> getReplies() {
        return replies;
    }

    public void setReplies(List<String> replies) {
        this.replies = replies;
    }

    public List<String> getLikes() {
        return likes;
    }

    public void setLikes(List<String> likes) {
        this.likes = likes;
    }

    public String getParent_comment_id() {
        return parent_comment_id;
    }

    public void setParent_comment_id(String parent_comment_id) {
        this.parent_comment_id = parent_comment_id;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getVideo_content() {
        return video_content;
    }

    public void setVideo_content(String video_content) {
        this.video_content = video_content;
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
                && likes.contains(MainActivity.getCurrentUser().getUser_id());
    }

    public List<String> getCommentIds(List<Comment> comments) {
        List<String> commentIds = new ArrayList<>();

        for (Comment comment : comments) {
            commentIds.add(comment.getComment_id()); // Lấy ID của từng bình luận và thêm vào danh sách

            // Nếu bình luận này có phản hồi (replies), lấy ID của các phản hồi
//            if (!comment.getReplies().isEmpty()) {
//                commentIds.addAll(getCommentIds(comment.getReplies())); // Đệ quy để lấy ID của các bình luận lồng nhau
//            }
        }

        return commentIds;
    }
}

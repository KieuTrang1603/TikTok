package com.example.tiktok.utils.Firebase;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

public class FirebaseUtil {
    public static final String TABLE_USERS = "users",
            TABLE_COMMENTS = "comments",
            TABLE_NOTIFICATIONS = "notifications",
            TABLE_VIDEOS = "videos";
    private final static String TAG = "FirebaseUtil";

//    private final static String FIREBASE_URL = "https://tiktok-f2543-default-rtdb.firebaseio.com/";

    // Get Database reference
    public static DatabaseReference getDatabase(String tableName) {
        return FirebaseDatabase.getInstance().getReference(tableName);
    }

    //get notifications by username
    public static Query getNotificationsByUserID(String user_id) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference(TABLE_NOTIFICATIONS);
        return ref.orderByChild("userId").equalTo(user_id);
    }

    //	get comment by id
    public static Query getCommentById(String commentId) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference(TABLE_COMMENTS);
        return ref.orderByChild("commentId").equalTo(commentId);
    }

    //get video by id
    public static Query getVideoById(String videoId) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference(TABLE_VIDEOS);
        return ref.orderByChild("videoId").equalTo(videoId);
    }

    // Get video by username
    public static Query getVideoByUsername(String username) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference(TABLE_VIDEOS);
        return ref.orderByChild("username").equalTo(username);
    }

    // Get user by email
    public static Query getUserByEmail(String email) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference(TABLE_USERS);
        return ref.orderByChild("email").equalTo(email).limitToFirst(1);
    }

    // Get user by username
    public static Query getUserByUsername(String username) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference(TABLE_USERS);
        return ref.orderByChild("username").equalTo(username).limitToFirst(1);
    }


    //	get user by string like username
    public static Query getUserByStringLikeUsername(String username) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference(TABLE_USERS);
        return ref.orderByChild("username").startAt(username).endAt(username + "\uf8ff");
    }


}
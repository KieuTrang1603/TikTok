package com.example.tiktok.utils.Firebase;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.tiktok.models.Notification;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class NotificationFirebase {
    public static final String TAG = "NotificationFirebase";
    public static final DatabaseReference notificationRef = FirebaseUtil.getDatabase(FirebaseUtil.TABLE_NOTIFICATIONS);

    // Callback
    public interface NotificationCallback {
        void onCallback(Notification notification);
    }

    public interface ListNotificationCallback {
        void onCallback(List<Notification> notifications);
    }

    public interface FailureCallback {
        void onCallback(DatabaseError error);
    }

    // Add notification to firebase
    public static void addNotification(Notification notification) {
        if (notification.getNotification_id() == null)
            notification.setNotification_id(notificationRef.push().getKey());
        notificationRef.child(notification.getNotification_id()).setValue(notification);
        Log.i(TAG, "addNotification: " + notification.getNotification_id() + " added to firebase");
    }

    // Update notification to firebase
    public static void updateNotification(Notification notification) {
        notificationRef.child(notification.getNotification_id()).setValue(notification);
        Log.i(TAG, "updateNotification: " + notification.getNotification_id() + " updated to firebase");
    }

    // Delete notification to firebase
    public static void deleteNotification(Notification notification) {
        notificationRef.child(notification.getNotification_id()).removeValue();
        Log.i(TAG, "deleteNotification: " + notification.getNotification_id() + " deleted from firebase");
    }
    //delete notification by redirectTo
    public static void deleteNotificationByRedirectTo(String redirectTo) {
        notificationRef.orderByChild("redirectTo").equalTo(redirectTo).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    postSnapshot.getRef().removeValue();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e(TAG, "onCancelled: ", databaseError.toException());
            }
        });
    }
    //delete notification by notificationId
    public static void deleteNotificationByNotificationId(String notificationId) {
        notificationRef.child(notificationId).removeValue();
        Log.i(TAG, "deleteNotification: " + notificationId + " deleted from firebase");
    }
    // Get notification by username
    public static void getNotificationByUserId(String user_id, final ListNotificationCallback listNotificationCallback, final FailureCallback failureCallback) {
        notificationRef.orderByChild("userId").equalTo(user_id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<Notification> notifications = new ArrayList<>();
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    Notification notification = new Notification(postSnapshot);
                    notifications.add(notification);
                }
                listNotificationCallback.onCallback(notifications);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                failureCallback.onCallback(databaseError);
            }
        });
    }
}

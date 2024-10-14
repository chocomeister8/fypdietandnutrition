package com.fyp.dietandnutritionapplication;

import android.util.Log;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Notification {

    private String notificationId;
    private String message;
    private Timestamp date;
    private String type;
    private String userId;
    private boolean isRead;

    // Constructor
    public Notification(String notificationId, String message, Timestamp date, String type, String userId, boolean isRead) {
        this.notificationId = notificationId;
        this.message = message;
        this.date = date;
        this.type = type;
        this.userId = userId;
        this.isRead = isRead;
    }

    // Getters and Setters
    public String getNotificationId() {
        return notificationId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Timestamp getDate() {
        return date;
    }

    public void setDate(Timestamp date) {
        this.date = date;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getUserId() {
        return userId;
    }

    public void setNotificationId(String notificationId) {
        this.notificationId = notificationId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public boolean isRead() {
        return isRead;
    }

    public void setRead(boolean read) {
        isRead = read;
    }

    public void fetchNotification(String userId, OnNotificationsFetchedListener listener) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("Notifications")
                .whereEqualTo("userId", userId)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<Notification> notifications = new ArrayList<>();
                    for (DocumentSnapshot document : queryDocumentSnapshots) {
                        String notificationId = document.getString("notificationId");
                        String type = document.getString("type");
                        String message = document.getString("message");
                        Timestamp date = document.getDate("timestamp") != null ?
                                new Timestamp(document.getDate("timestamp").getTime()) : null;
                        String notificationUserId = document.getString("userId");
                        boolean isRead = document.getBoolean("isRead") != null && document.getBoolean("isRead");

                        Collections.sort(notifications, (n1, n2) -> {
                            if (n1.getDate() == null && n2.getDate() == null) return 0;
                            if (n1.getDate() == null) return 1;
                            if (n2.getDate() == null) return -1;
                            return n2.getDate().compareTo(n1.getDate()); // Newest to oldest
                        });

                        // Create a new Notification object and add it to the list
                        Notification notification = new Notification(notificationId, message, date, type, notificationUserId, isRead);
                        notifications.add(notification);
                    }
                    // Notify the listener with the fetched notifications
                    listener.onNotificationsFetched(notifications);
                })
                .addOnFailureListener(e -> {
                    Log.w("Notification", "Error fetching notifications", e);
                    listener.onNotificationsFetched(Collections.emptyList());
                });
    }

    public interface OnNotificationsFetchedListener {
        void onNotificationsFetched(List<Notification> notifications);
    }



    public interface OnNotificationCountFetchedListener {
        void onCountFetched(int count);
    }

    public void countNotification(String userId, OnNotificationCountFetchedListener listener) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("Notifications")
                .whereEqualTo("userId", userId)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    int unreadCount = 0;

                    for (DocumentSnapshot document : queryDocumentSnapshots) {

                        boolean isRead = document.getBoolean("isRead");
                        if (!isRead) {
                            unreadCount++;
                        }
                    }

                    listener.onCountFetched(unreadCount);
                })
                .addOnFailureListener(e -> {
                    Log.w("Notification", "Error fetching count notifications", e);
                    listener.onCountFetched(0);
                });
    }



}

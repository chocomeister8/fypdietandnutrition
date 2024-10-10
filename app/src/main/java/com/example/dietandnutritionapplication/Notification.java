package com.example.dietandnutritionapplication;

import android.util.Log;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class Notification {

    private String title;
    private String message;
    private Timestamp date;
    private String type;
    private String userId;

    // Constructor
    public Notification(String title, String message, Timestamp date, String type, String userId) {
        this.title = title;
        this.message = message;
        this.date = date;
        this.type = type;
        this.userId = userId;
    }

    // Getters and Setters
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
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

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void fetchNotification(String userId, OnNotificationsFetchedListener listener) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("Notifications")
                .whereEqualTo("userId", userId) // Assuming you want to fetch entries for the logged-in user
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<Notification> notifications = new ArrayList<>();
                    for (DocumentSnapshot document : queryDocumentSnapshots) {
                        // Retrieve data from each document and create Notification objects
                        String type = document.getString("type");
                        String message = document.getString("message");
                        Timestamp date = document.getDate("timestamp") != null ?
                                new Timestamp(document.getDate("timestamp").getTime()) : null;
                        String notificationUserId = document.getString("userId");

                        // Create a new Notification object and add it to the list
                        Notification notification = new Notification(type, message, date, type, notificationUserId);
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

}

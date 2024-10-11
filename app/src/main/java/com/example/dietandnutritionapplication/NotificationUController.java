package com.example.dietandnutritionapplication;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class NotificationUController {
    private Notification notification;

    public void fetchNotifications(String userId, Notification.OnNotificationsFetchedListener listener) {
        // You may want to provide default values for title, message, and type
        Timestamp defaultDate = new Timestamp(System.currentTimeMillis()); // Current time as default, or you can handle it in a different way.

        notification = new Notification("","", defaultDate, "", "", false);
        notification.fetchNotification(userId, listener);
    }
}
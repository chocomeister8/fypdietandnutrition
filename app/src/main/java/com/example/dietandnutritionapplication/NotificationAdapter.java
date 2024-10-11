package com.example.dietandnutritionapplication;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.ViewHolder> {

    private List<Notification> notifications;
    private Context context;

    public NotificationAdapter(List<Notification> notifications, Context context) {
        this.notifications = notifications;
        this.context = context; // Initialize context
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView typeTextView;
        public TextView messageTextView;
        public TextView dateTextView;

        public ViewHolder(View itemView) {
            super(itemView);
            typeTextView = itemView.findViewById(R.id.typeTextView);
            messageTextView = itemView.findViewById(R.id.messageTextView);
            dateTextView = itemView.findViewById(R.id.dateTextView);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.notification_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Notification notification = notifications.get(position);

        holder.typeTextView.setText(notification.getType());
        holder.messageTextView.setText(notification.getMessage());

        if (notification.getDate() != null) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
            dateFormat.setTimeZone(TimeZone.getTimeZone("GMT+8"));

            String formattedDate = dateFormat.format(new Date(notification.getDate().getTime()));
            holder.dateTextView.setText(formattedDate);
        } else {
            holder.dateTextView.setText(""); // Set empty if date is null
        }

        if (notification.isRead()) {
            holder.itemView.setBackgroundColor(Color.WHITE);
        } else {
            holder.itemView.setBackgroundColor(Color.parseColor("#dcdcdc"));
        }

        holder.itemView.setOnClickListener(v -> {
            // Ensure notificationId is not null
            if (notification.getNotificationId() != null) {
                // Check if the type is "recipe"
                if (notification.getType() != null && notification.getType().toLowerCase().contains("recipe")) {
                    NavPendingRecipesFragment navPendingRecipesFragment = new NavPendingRecipesFragment();

                    // Pass the notification ID to the fragment if needed
                    Bundle bundle = new Bundle();
                    bundle.putString("notificationId", notification.getNotificationId());
                    navPendingRecipesFragment.setArguments(bundle);

                    if (context instanceof FragmentActivity) {
                        ((FragmentActivity) context).getSupportFragmentManager().beginTransaction()
                                .replace(R.id.frame_layout, navPendingRecipesFragment) // Replace with your fragment container ID
                                .addToBackStack(null) // Add to back stack
                                .commit();
                    }

                    markNotificationAsRead(notification.getNotificationId());
                    notification.setRead(true);
                    notifyItemChanged(position);

                } /*else if ("booking".equals(notification.getType())) {
                    // Create a new instance of BookingFragment
                    BookingFragment bookingFragment = new BookingFragment();

                    // Pass the notification ID to the fragment if needed
                    Bundle bundle = new Bundle();
                    bundle.putString("notificationId", notification.getNotificationId());
                    bookingFragment.setArguments(bundle);

                    // Replace the current fragment with BookingFragment
                    requireActivity().getSupportFragmentManager().beginTransaction()
                            .replace(R.id.frame_layout, bookingFragment) // Replace with your fragment container ID
                            .addToBackStack(null) // Add to back stack
                            .commit();*/

            } else {
                Log.e("Notification", "Notification ID is null");
            }
        });
    }


    @Override
    public int getItemCount() {
        return notifications.size();
    }

    public void updateNotifications(List<Notification> newNotifications) {
        this.notifications.clear();
        this.notifications.addAll(newNotifications);
        notifyDataSetChanged();
    }

    private void markNotificationAsRead(String notificationId) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("Notifications").document(notificationId)
                .update("isRead", true) // Update isRead field in Firestore
                .addOnSuccessListener(aVoid -> {
                    Log.d("Notification", "Marked as read");
                })
                .addOnFailureListener(e -> {
                    Log.e("Notification", "Failed to mark as read", e);
                });
    }
}

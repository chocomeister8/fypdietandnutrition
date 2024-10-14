package com.fyp.dietandnutritionapplication;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.List;

public class NotificationUFragment extends Fragment {

    private NotificationUController notificationController;
    private RecyclerView recyclerView;
    private NotificationAdapter adapter;
    private List<Notification> notificationsList;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser currentUser;
    private String userId;
    private TextView noNotificationsTextView;

    public NotificationUFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_notifcation, container, false);

        firebaseAuth = FirebaseAuth.getInstance();
        currentUser = firebaseAuth.getCurrentUser();

        recyclerView = view.findViewById(R.id.notificationRecyclerView);
        noNotificationsTextView = view.findViewById(R.id.noNotificationsTextView);

        if (currentUser != null) {
            userId = currentUser.getUid();
            notificationController = new NotificationUController();

            notificationController.fetchNotifications(userId, new Notification.OnNotificationsFetchedListener() {
                @Override
                public void onNotificationsFetched(List<Notification> notifications) {
                    if (notifications.isEmpty()) {

                        recyclerView.setVisibility(View.GONE);
                        noNotificationsTextView.setVisibility(View.VISIBLE);

                    } else {

                        recyclerView.setVisibility(View.VISIBLE);
                        noNotificationsTextView.setVisibility(View.GONE);

                        if (adapter == null) {
                            adapter = new NotificationAdapter(notifications, getContext());
                            recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                            recyclerView.setAdapter(adapter);
                        } else {
                            adapter.updateNotifications(notifications);
                        }

                    }
                }
            });
        } else {
            // Handle case when user is not logged in (optional)
            noNotificationsTextView.setVisibility(View.VISIBLE);
            noNotificationsTextView.setText("Please log in to see notifications.");
        }

        return view;
    }
}
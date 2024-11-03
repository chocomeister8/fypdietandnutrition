package com.fyp.dietandnutritionapplication;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class BMIPastRecordController extends Fragment {

    private FirebaseAuth auth;
    private FirebaseFirestore db;
    private FirebaseUser currentUser;
    private RecyclerView recyclerView;
    private BMIRecordFragment adapter;
    private List<BMIEntity> bmiList;
    private NotificationUController notificationUController;
    private TextView notificationBadgeTextView;

    // Constructor to accept local BMI list
    public BMIPastRecordController(List<BMIEntity> localBmiList) {
        this.bmiList = localBmiList != null ? localBmiList : new ArrayList<>();
    }



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.bmi_report, container, false);

        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        currentUser = auth.getCurrentUser();

        recyclerView = view.findViewById(R.id.bmiRecord_recycler_view);
        adapter = new BMIRecordFragment(bmiList);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);


        if (currentUser != null) {
            String userId = currentUser.getUid();

            notificationBadgeTextView = view.findViewById(R.id.notificationBadgeTextView);

            notificationUController = new NotificationUController();
            notificationUController.fetchNotifications(userId, new Notification.OnNotificationsFetchedListener() {
                @Override
                public void onNotificationsFetched(List<Notification> notifications) {
                    // Notifications can be processed if needed

                    // After fetching notifications, count them
                    notificationUController.countNotifications(userId, new Notification.OnNotificationCountFetchedListener() {
                        @Override
                        public void onCountFetched(int count) {
                            if (count > 0) {
                                notificationBadgeTextView.setText(String.valueOf(count));
                                notificationBadgeTextView.setVisibility(View.VISIBLE);
                            } else {
                                notificationBadgeTextView.setVisibility(View.GONE);
                            }
                        }
                    });
                }
            });

        }

        ImageView notiImage = view.findViewById(R.id.noti_icon);
        notiImage.setOnClickListener(v -> {
            requireActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.frame_layout, new NotificationUFragment())
                    .addToBackStack(null)
                    .commit();

        });


        // Fetch BMI records from Firestore
        fetchBMIRecords();

        // Hide buttons
        if (getActivity() != null) {
            getActivity().findViewById(R.id.calculate_button).setVisibility(View.GONE);
            getActivity().findViewById(R.id.PastRecord_button).setVisibility(View.GONE);
            getActivity().findViewById(R.id.advice_column).setVisibility(View.GONE);
        }

        return view;
    }

    private void fetchBMIRecords() {
        String userId = auth.getCurrentUser().getUid();
        db.collection("BMIRecord")
                .whereEqualTo("user_id", userId) // Fetch records for the current user
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        bmiList.clear(); // Clear existing records
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            double bmi = document.getDouble("bmi");
                            long timestamp = document.getLong("timestamp");
                            // Add to the local list
                            bmiList.add(new BMIEntity(bmi, timestamp, userId));
                        }
                        adapter.notifyDataSetChanged(); // Notify the adapter to refresh the view
                    } else {
                        Toast.makeText(getContext(), "Error fetching records: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}

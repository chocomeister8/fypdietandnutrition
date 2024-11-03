package com.fyp.dietandnutritionapplication;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class userViewFAQFragment extends Fragment {

    private ExpandableListView faqExpandableListView;
    private FAQExpandableListAdapter faqAdapter;
    private ViewFAQController viewFAQController;
    private NotificationUController notificationUController;
    private TextView notificationBadgeTextView;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser currentUser;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_u_viewfaqs, container, false);

        // Initialize the ExpandableListView
        faqExpandableListView = view.findViewById(R.id.faqExpandableListView);
        viewFAQController = new ViewFAQController();

        firebaseAuth = FirebaseAuth.getInstance();
        currentUser = firebaseAuth.getCurrentUser();

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

        // Load FAQs
        loadFAQs();

        return view;
    }

    private void loadFAQs() {
        // Define the categories
        List<String> categories = Arrays.asList(
                "General Information",
                "Account and Profile Management",
                "Nutrition and Diet Tracking",
                "Meal Plans and Recipes",
                "Supported Diets and Preferences",
                "Health and Fitness Goals",
                "Technical Support"
        );

        viewFAQController.getAllFAQ(new FAQEntity.DataCallback() {
            @Override
            public void onSuccess(ArrayList<FAQ> faqs) {
                // Initialize data structures for categories
                List<String> groupTitles = new ArrayList<>(categories);
                HashMap<String, List<String>> childItems = new HashMap<>();
                HashMap<String, List<String>> answers = new HashMap<>();

                // Initialize childItems and answers for each category
                for (String category : categories) {
                    childItems.put(category, new ArrayList<>());
                    answers.put(category, new ArrayList<>());
                }

                // Populate the childItems and answers with FAQs
                for (FAQ faq : faqs) {
                    String category = faq.getCategory();
                    String question = faq.getQuestion();
                    String answer = faq.getAnswer();

                    // Check if the category exists
                    if (childItems.containsKey(category)) {
                        childItems.get(category).add(question);
                        answers.get(category).add(answer);
                    }
                }

                // Update the adapter with the fetched data
                faqAdapter = new FAQExpandableListAdapter(groupTitles, childItems, answers);
                faqExpandableListView.setAdapter(faqAdapter);

                // Set default message for empty categories
                for (String category : categories) {
                    if (childItems.get(category).isEmpty()) {
                        childItems.get(category).add("No FAQs added yet.");
                        answers.get(category).add(""); // Empty answer for "No FAQs added yet."
                    }
                }
            }

            @Override
            public void onFailure(Exception e) {
                e.printStackTrace();
                Toast.makeText(getContext(), "Failed to load FAQs.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}

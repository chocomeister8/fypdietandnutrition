package com.example.dietandnutritionapplication;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class AppReviewsFragment extends Fragment {
    private ListView reviewsListView;
    private Button filterAllButton;
    private Spinner filterSortSpinner;
    private TextView ratingTextView;
    private List<AppRatingsReviews> reviews = new ArrayList<>();
    private AppReviewController adapter;

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_guest_viewappratingsandreviews, container, false);

        // Populate some dummy reviews
        reviews.add(new AppRatingsReviews(
                "Great Tool for Healthy Living",
                "This app makes managing my nutrition easy with personalized tips, meal logging, and quick access to expert advice.",
                3.0f,
                "09-08-2024 15:55",
                "John Tan"));

        reviews.add(new AppRatingsReviews(
                "Useful App for Healthy Living",
                "It is a user-friendly app that helps me effortlessly track meals, manage calories, and stay healthy.",
                4.0f,
                "09-08-2024 17:55",
                "Lily"));

        reviews.add(new AppRatingsReviews(
                "Average App",
                "The app works fine but has some issues with performance.",
                5.0f,
                "09-08-2024 14:55",
                "Alex"));

        // Setup ListView
        reviewsListView = view.findViewById(R.id.reviewListView);
        adapter = new AppReviewController(getContext(), reviews);
        reviewsListView.setAdapter(adapter);

        // Setup Button and Spinner
        filterAllButton = view.findViewById(R.id.filterAllButton);
        filterSortSpinner = view.findViewById(R.id.filterSortSpinner);
        ratingTextView = view.findViewById(R.id.ratingTextView);

        // Set the button text with total reviews count
        filterAllButton.setText("All (" + reviews.size() + ")");

        // Set up Spinner for star ratings
        List<String> sortOptions = new ArrayList<>();
        sortOptions.add("Highest to Lowest Rating");
        sortOptions.add("Most Recent");
        ArrayAdapter<String> sortAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, sortOptions);
        sortAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        filterSortSpinner.setAdapter(sortAdapter);

        // Set the average rating manually for now with HTML-like formatting
        float averageRating = calculateAverageRating();
        ratingTextView.setTextSize(25);
        String ratingText = String.format("%.1f", averageRating);
        ratingTextView.setText(ratingText);

        // Sort reviews by highest rating initially
        sortReviewsByRating();

        // Set up Spinner item selected listener
        filterSortSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        // Sort by highest to lowest rating
                        sortReviewsByRating();
                        break;
                    case 1:
                        // Sort by most recent
                        sortReviewsByDate();
                        break;
                }
                // Update ListView with sorted reviews
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Handle case where no selection is made if needed
            }
        });

        // Set up button click listener
        filterAllButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Show all reviews when "All" button is clicked
                adapter = new AppReviewController(getContext(), reviews);
                reviewsListView.setAdapter(adapter);
                // Reset Spinner selection to default sorting option
                filterSortSpinner.setSelection(0);
            }
        });

        return view;
    }

    // Method to calculate the average rating
    private float calculateAverageRating() {
        if (reviews.isEmpty()) {
            return 0;
        }
        float totalRating = 0;
        for (AppRatingsReviews review : reviews) {
            totalRating += review.getRating();
        }
        return totalRating / reviews.size();
    }

    // Method to sort reviews by rating (highest to lowest)
    private void sortReviewsByRating() {
        Collections.sort(reviews, new Comparator<AppRatingsReviews>() {
            @Override
            public int compare(AppRatingsReviews r1, AppRatingsReviews r2) {
                return Float.compare(r2.getRating(), r1.getRating()); // Descending order
            }
        });
        // Notify the adapter of the change
        adapter.notifyDataSetChanged();
    }

    // Method to sort reviews by date (most recent)
    private void sortReviewsByDate() {
        Collections.sort(reviews, new Comparator<AppRatingsReviews>() {
            @Override
            public int compare(AppRatingsReviews r1, AppRatingsReviews r2) {
                // Assuming the date is in "dd-MM-yyyy HH:mm" format
                return r2.getDateTime().compareTo(r1.getDateTime()); // Most recent first
            }
        });
        // Notify the adapter of the change
        adapter.notifyDataSetChanged();
    }
}
package com.example.dietandnutritionapplication;

import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import java.util.ArrayList;
import java.util.List;

public class AppReviewsFragment extends Fragment {
    private ListView reviewsListView;
    private Button filterAllButton;
    private Spinner filterStarSpinner;
    private TextView ratingTextView;
    private List<AppRatingsReviews> reviews = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_guest_viewappratingsandreviews, container, false);

        // Populate some dummy reviews
        reviews.add(new AppRatingsReviews(
                "Great Tool for Healthy Living",
                "This app makes managing my nutrition easy with personalized tips, meal logging, and quick access to expert advice.",
                5.0f,
                "09-08-2024 17:55",
                "John Tan"));

        reviews.add(new AppRatingsReviews(
                "Useful App for Healthy Living",
                "It is a user-friendly app that helps me effortlessly track meals, manage calories, and stay healthy.",
                5.0f,
                "09-08-2024 17:55",
                "Lily"));

        // Setup ListView
        reviewsListView = view.findViewById(R.id.reviewListView);
        AppReviewController adapter = new AppReviewController(getContext(), reviews);
        reviewsListView.setAdapter(adapter);

        // Setup Button and Spinner
        filterAllButton = view.findViewById(R.id.filterAllButton);
        filterStarSpinner = view.findViewById(R.id.filterStarSpinner);
        ratingTextView = view.findViewById(R.id.ratingTextView);

        // Set the button text with total reviews count
        filterAllButton.setText("All (" + reviews.size() + ")");

        // Set up Spinner for star ratings
        List<String> starRatings = new ArrayList<>();
        for (int i = 1; i <= 5; i++) {
            starRatings.add(i + " Star");
        }
        ArrayAdapter<String> starAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, starRatings);
        starAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        filterStarSpinner.setAdapter(starAdapter);

        // Set the average rating manually for now with HTML-like formatting
        String ratingText = "<big>4.2</big>";
        ratingTextView.setText(Html.fromHtml(ratingText));

        // Set up button click listener
        filterAllButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle button click to show all reviews (currently just showing all reviews)
                reviewsListView.setAdapter(adapter);
            }
        });

        return view;
    }
}

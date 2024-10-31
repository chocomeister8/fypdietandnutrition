package com.fyp.dietandnutritionapplication;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import android.widget.ArrayAdapter;
import android.widget.Spinner;

public class userReviewAppFragment extends Fragment {

    private Spinner spinnerTitle;  // Replace TextInputEditText with Spinner
    private TextInputEditText etReviewContent;
    private RatingBar ratingBar;
    private Button submitRateButton;
    private String username;
    private FirebaseAuth firebaseAuth;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_user_review_app, container, false);

        firebaseAuth = FirebaseAuth.getInstance();
        spinnerTitle = view.findViewById(R.id.spinnerTitle);  // Initialize Spinner
        etReviewContent = view.findViewById(R.id.etReview);
        ratingBar = view.findViewById(R.id.ratingBar3);
        submitRateButton = view.findViewById(R.id.submitRateButton);

        // Set up Spinner adapter with predefined titles
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(),
                R.array.title_options, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerTitle.setAdapter(adapter);

        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        userReviewAppFragmentController.getUserById(currentUser.getUid(), new UserAccountEntity.UserFetchCallback() {
            @Override
            public void onUserFetched(User user) {
                username = user.getUsername();
            }

            @Override
            public void onFailure(String errorMessage) {
                Toast.makeText(getContext(), "Error loading profile: " + errorMessage, Toast.LENGTH_SHORT).show();
            }
        });

        submitRateButton.setOnClickListener(v -> submitReview());

        return view;
    }

    private void submitReview() {
        String title = spinnerTitle.getSelectedItem().toString();  // Get selected title from Spinner
        String review = etReviewContent.getText().toString().trim();
        float star = ratingBar.getRating();
        String date = new SimpleDateFormat("dd-MM-yyyy HH:mm", Locale.getDefault()).format(new Date());
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();

        if (currentUser == null) {
            Toast.makeText(getContext(), "User not logged in", Toast.LENGTH_SHORT).show();
            return;
        }

        if (title.isEmpty() || review.isEmpty() || star == 0) {
            Toast.makeText(getContext(), "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        AppRatingsReviews newReview = new AppRatingsReviews(title, review, star, date, username);
        userReviewAppFragmentController userReviewAppFragmentController = new userReviewAppFragmentController();
        userReviewAppFragmentController.submitUserReview(newReview.getTitle(), newReview.getReview(), newReview.getRating(), newReview.getDateTime(), newReview.getUsername(), getContext(), new AppRatingReviewEntity.DataCallback() {
            @Override
            public void onSuccess(ArrayList<AppRatingsReviews> ratingList) {
                Toast.makeText(getContext(), "Review submitted successfully!", Toast.LENGTH_SHORT).show();
                spinnerTitle.setSelection(0);  // Reset Spinner selection
                etReviewContent.setText("");
                ratingBar.setRating(0);
            }

            @Override
            public void onFailure(Exception e) {
                Toast.makeText(getContext(), "Failed to submit review: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
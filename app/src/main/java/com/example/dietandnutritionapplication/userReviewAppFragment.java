package com.example.dietandnutritionapplication;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
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

public class userReviewAppFragment extends Fragment {

    private AppRatingReviewEntity appRatingReviewEntity;
    private FirebaseAuth firebaseAuth;
    private TextInputEditText etReviewTitle;
    private TextInputEditText etReviewContent;
    private RatingBar ratingBar;
    private Button submitRateButton;
    private String username;

    public userReviewAppFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        appRatingReviewEntity = new AppRatingReviewEntity();
        firebaseAuth = FirebaseAuth.getInstance();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_user_review_app, container, false);


        etReviewTitle = view.findViewById(R.id.etTitle);
        etReviewContent = view.findViewById(R.id.etReview);
        ratingBar = view.findViewById(R.id.ratingBar3);
        submitRateButton = view.findViewById(R.id.submitRateButton);
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
        String title = etReviewTitle.getText().toString().trim();
        String review = etReviewContent.getText().toString().trim();
        float star = ratingBar.getRating();
        String date = new SimpleDateFormat("dd-MM-yyyy HH:mm", Locale.getDefault()).format(new Date());
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();

        if (currentUser != null) {

        } else {
            Toast.makeText(getContext(), "User not logged in", Toast.LENGTH_SHORT).show();
        }


        if (title.isEmpty() || review.isEmpty() || star == 0) {
            Toast.makeText(getContext(), "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        // Create a new AppRatingsReviews instance
        AppRatingsReviews newReview = new AppRatingsReviews(title, review, star, date, username);
        userReviewAppFragmentController userReviewAppFragmentController = new userReviewAppFragmentController();
        userReviewAppFragmentController.submitUserReview(newReview.getTitle(), newReview.getReview(), newReview.getRating(), newReview.getDateTime(), newReview.getUsername(), getContext(), new AppRatingReviewEntity.DataCallback() {
            @Override
            public void onSuccess(ArrayList<AppRatingsReviews> ratingList) {
                Toast.makeText(getContext(), "Review submitted successfully!", Toast.LENGTH_SHORT).show();
                etReviewTitle.setText("");
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
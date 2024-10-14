package com.fyp.dietandnutritionapplication;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;


public class RateNutriFragment extends Fragment {
    private Profile selectedProfile;
    private Nutritionist selectedNutri;
    private String usernameNutri;
    private AppRatingReviewEntity appRatingReviewEntity;
    private FirebaseAuth firebaseAuth;
    private TextInputEditText etReviewTitle;
    private TextInputEditText etReviewContent;
    private RatingBar ratingBar;
    private Button submitRateButton;
    private String username;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        appRatingReviewEntity = new AppRatingReviewEntity();
        firebaseAuth = FirebaseAuth.getInstance();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_rate_nutri, container, false);

        if (getArguments() != null) {
            selectedProfile = (Profile) getArguments().getSerializable("selectedProfile");
            if (selectedProfile instanceof Nutritionist) {
                selectedNutri= ((Nutritionist) selectedProfile);
            }
        }
        TextView fullNameTextView = view.findViewById(R.id.fullName);
        TextView emailTextView = view.findViewById(R.id.email);
        TextView educationTextView = view.findViewById(R.id.education);
        TextView bioTextView = view.findViewById(R.id.bio);
        TextView phonenumberTextView = view.findViewById(R.id.phoneNumber);
        TextView expertiseTextView = view.findViewById(R.id.expertise);

        if (selectedNutri != null) {
            fullNameTextView.setText("Full Name - " + selectedNutri.getFullName());
            emailTextView.setText("Email - " + selectedNutri.getEmail());
            educationTextView.setText("Education - " + selectedNutri.getEducation());
            bioTextView.setText("Bio - "+ selectedNutri.getBio());
            phonenumberTextView.setText("Phone - "+ selectedNutri.getPhoneNumber());
            expertiseTextView.setText("Expertise - " + selectedNutri.getExpertise());
        }
        usernameNutri = selectedNutri.getUsername();

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

        NutritionistRating nutritionistRating = new NutritionistRating(title,review,star,date,username,selectedNutri.getUsername());
        RateNutritionistController rateNutritionistController = new RateNutritionistController();
        rateNutritionistController.submitUserReview(nutritionistRating.getTitle(), nutritionistRating.getReview(), nutritionistRating.getRating(), nutritionistRating.getDateTime(), nutritionistRating.getUser(), nutritionistRating.getNutriName(), getContext(), new AppRatingReviewEntity.DataCallback() {
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
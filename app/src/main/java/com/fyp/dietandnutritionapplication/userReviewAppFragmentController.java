package com.fyp.dietandnutritionapplication;

import android.content.Context;
import android.widget.Toast;

import java.util.ArrayList;

public class userReviewAppFragmentController {
    public userReviewAppFragmentController(){

    }
    public void submitUserReview(String title, String review, float star, String date, String username, Context context, final AppRatingReviewEntity.DataCallback callback){
        AppRatingsReviews newReview = new AppRatingsReviews(title, review, star, date, username);
        AppRatingReviewEntity appRatingReviewEntity= new AppRatingReviewEntity();
        appRatingReviewEntity.storeNewReview(newReview.getTitle(), newReview.getReview(), newReview.getRating(), newReview.getDateTime(), newReview.getUsername(), new AppRatingReviewEntity.DataCallback() {
            @Override
            public void onSuccess(ArrayList<AppRatingsReviews> ratingList) {
                Toast.makeText(context, "Review submitted successfully!", Toast.LENGTH_SHORT).show();
                if (context instanceof MainActivity) {
                    ((MainActivity) context).replaceFragment(new userHomePageFragment());
                } else {
                    Toast.makeText(context, "Error: Context is not an instance of MainActivity", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Exception e) {
                Toast.makeText(context, "Failed to submit review: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    public static void getUserById(String userId, UserAccountEntity.UserFetchCallback callback) {
        UserAccountEntity userAccountEntity= new UserAccountEntity();
        userAccountEntity.getUserById(userId, callback);
    }
}

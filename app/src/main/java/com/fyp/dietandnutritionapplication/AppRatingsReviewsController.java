package com.fyp.dietandnutritionapplication;

import java.util.ArrayList;

public class AppRatingsReviewsController {
    private ArrayList<AppRatingsReviews> ratings = new ArrayList<>();

    public void retrieveRatings(final AppRatingReviewEntity.DataCallback callback) {
        AppRatingReviewEntity appRatingReviewEntity = new AppRatingReviewEntity();
        appRatingReviewEntity.retrieveAndStoreRatings(new AppRatingReviewEntity.DataCallback() {
            @Override
            public void onSuccess(ArrayList<AppRatingsReviews> ratingList) {
                // Clear the current ratings and add the retrieved ones
                ratings.clear();
                ratings.addAll(ratingList);
                // Pass the result back through the callback
                callback.onSuccess(ratings);
            }

            @Override
            public void onFailure(Exception e) {
                // Print the exception and notify the callback about the failure
                e.printStackTrace();
                callback.onFailure(e);
            }
        });
    }
}
package com.example.dietandnutritionapplication;

import java.util.ArrayList;

public class ViewDetailAndRateNutriProfileController {
    private ArrayList<AppRatingsReviews> ratings = new ArrayList<>();

    public void retrieveRatings(final NutritionistRatingEntity.DataCallback callback) {
        NutritionistRatingEntity nutritionistRatingEntity = new NutritionistRatingEntity();
        nutritionistRatingEntity.retrieveAndStoreRatings(new NutritionistRatingEntity.DataCallback() {
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

    public void retrieveRatingsByNutritionist(String nutritionistUsername, final NutritionistRatingEntity.DataCallback callback) {
        NutritionistRatingEntity nutritionistRatingEntity = new NutritionistRatingEntity();
        nutritionistRatingEntity.retrieveRatingsByNutritionist(nutritionistUsername, new NutritionistRatingEntity.DataCallback() {
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

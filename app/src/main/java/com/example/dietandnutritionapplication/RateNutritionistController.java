package com.example.dietandnutritionapplication;

import android.content.Context;
import android.widget.Toast;

import java.util.ArrayList;

public class RateNutritionistController {
    public RateNutritionistController(){

    }
    public void submitUserReview(String title, String review, float star, String date, String username,String nutriName, Context context, final AppRatingReviewEntity.DataCallback callback){
//        AppRatingsReviews newReview = new AppRatingsReviews(title, review, star, date, username);
//        AppRatingReviewEntity appRatingReviewEntity= new AppRatingReviewEntity();
        NutritionistRating nutritionistRating = new NutritionistRating(title, review, star, date, username,nutriName);
        NutritionistRatingEntity nutritionistRatingEntity = new NutritionistRatingEntity();
        nutritionistRatingEntity.storeNewReview(nutritionistRating.getTitle(), nutritionistRating.getReview(), nutritionistRating.getRating(), nutritionistRating.getDateTime(), nutritionistRating.getUser(), nutritionistRating.getNutriName(), new NutritionistRatingEntity.DataCallback() {
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
}

package com.fyp.dietandnutritionapplication;

import android.content.Context;
import android.util.Log;

public class NutriProfileController {
    Nutritionist nutritionistProfile = new Nutritionist();

    public NutriProfileController() {
    }

    public void getNutriProfile(String userId, final UserAccountEntity.NutritionistCallback callback) {
        UserAccountEntity userAccountEntity = new UserAccountEntity();
        userAccountEntity.retrieveNutritionistByUid(userId, new UserAccountEntity.NutritionistCallback() {
            @Override
            public void onSuccess(Nutritionist nutritionist) {
                nutritionistProfile = nutritionist;
                Log.d("Firestore", "Nutritionist data retrieved: " + nutritionist.toString());
                callback.onSuccess(nutritionist);  // Call the callback to notify the caller
            }

            @Override
            public void onFailure(Exception e) {
                Log.e("Firestore", "Failed to load profile data", e);
                callback.onFailure(e);  // Notify failure via the callback
            }
        });
    }

    public void updateProfile(String userId, String username, String firstName, String lastName,
                              String email, String contactInfo, String education,
                              String expertise, String bio, Context context){
        UserAccountEntity userAccountEntity = new UserAccountEntity();
        userAccountEntity.updateNutritionistProfile(userId,username,firstName,lastName,email,contactInfo,education,expertise,bio,context);
    }
}
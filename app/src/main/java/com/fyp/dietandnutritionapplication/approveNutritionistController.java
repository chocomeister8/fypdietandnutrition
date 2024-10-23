package com.fyp.dietandnutritionapplication;

import android.util.Log;

public class approveNutritionistController {
    private UserAccountEntity userAccountEntity; // Reference to your entity class

    public approveNutritionistController() {
        userAccountEntity = new UserAccountEntity(); // Initialize entity class
    }

    public void approveNutritionist(String username) {
        // Check for null or empty username
        if (username == null || username.isEmpty()) {
            Log.e("approveNutritionistController", "Username cannot be null or empty");
            return;
        }

        Log.d("approveNutritionistController", "Approving Nutritionist: " + username);

        // Call the update method from the UserAccountEntity
        userAccountEntity.approveNutritionist(username, new UserAccountEntity.RegisterCallback() {
            @Override
            public void onSuccess(String successMessage) {
                Log.d("approveNutritionistController", "Nutritionist approved successfully.");
            }

            @Override
            public void onFailure(String errorMessage) {
                Log.e("approveNutritionistController", "Failed to approve Nutritionist: " + errorMessage);
            }
        });
    }
}
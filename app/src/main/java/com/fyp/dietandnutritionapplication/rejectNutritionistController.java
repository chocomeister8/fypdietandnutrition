package com.fyp.dietandnutritionapplication;

import android.util.Log;

public class rejectNutritionistController {
    private UserAccountEntity userAccountEntity; // Reference to your entity class

    public rejectNutritionistController() {
        userAccountEntity = new UserAccountEntity(); // Initialize entity class
    }

    public void rejectNutritionist(String username) {
        // Check for null or empty username
        if (username == null || username.isEmpty()) {
            Log.e("rejectNutritionistController ", "Username cannot be null or empty");
            return;
        }

        Log.d("rejectNutritionistController ", "Rejecting Nutritionist: " + username);

        // Call the update method from the UserAccountEntity
        userAccountEntity.rejectNutritionist(username, new UserAccountEntity.RegisterCallback() {
            @Override
            public void onSuccess(String successMessage) {
                Log.d("approveNutritionistController", "Nutritionist rejected.");
            }

            @Override
            public void onFailure(String errorMessage) {
                Log.e("approveNutritionistController", "Failed to reject Nutritionist: " + errorMessage);
            }
        });
    }
}

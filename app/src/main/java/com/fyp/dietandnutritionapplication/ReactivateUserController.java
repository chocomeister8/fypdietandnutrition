package com.fyp.dietandnutritionapplication;

import android.util.Log;

public class ReactivateUserController {
    private UserAccountEntity userAccountEntity; // Reference to your entity class

    public ReactivateUserController() {
        userAccountEntity = new UserAccountEntity(); // Initialize entity class
    }

    public void ReactivateUser(String username, ReactivateUserCallback callback) {
        // Check for null or empty username
        if (username == null || username.isEmpty()) {
            Log.e("ReactivateUserController", "Username cannot be null or empty");
            return;
        }

        Log.d("ReactivateUserController", "Reactivating user: " + username);

        // Call the update method from the UserAccountEntity
        userAccountEntity.reactivateUserProfileByUsername(username, new UserAccountEntity.UserProfileUpdateCallback() {
            @Override
            public void onSuccess() {
                Log.d("ReactivateUserController", "User profile reactivated successfully.");
                if (callback != null) {
                    callback.onSuccess(); // Notify success
                }
            }

            @Override
            public void onFailure(String errorMessage) {
                Log.e("ReactivateUserController", "Failed to reactivate user profile: " + errorMessage);
                if (callback != null) {
                    callback.onFailure(errorMessage); // Notify failure
                }
            }
        });
    }
    public interface ReactivateUserCallback {
        void onSuccess();
        void onFailure(String errorMessage);
    }
}
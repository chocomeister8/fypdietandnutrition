package com.fyp.dietandnutritionapplication;

import android.util.Log;

public class SuspendUserController {
    private UserAccountEntity userAccountEntity; // Reference to your entity class

    public SuspendUserController() {
        userAccountEntity = new UserAccountEntity(); // Initialize entity class
    }

    public void suspendUser(String username, SuspendUserCallback callback) {
        // Check for null or empty username
        if (username == null || username.isEmpty()) {
            Log.e("SuspendUserController", "Username cannot be null or empty");
            if (callback != null) {
                callback.onFailure("Username cannot be null or empty");
            }
            return;
        }

        Log.d("SuspendUserController", "Suspending user: " + username);

        // Call the update method from the UserAccountEntity
        userAccountEntity.suspendUserProfileByUsername(username, new UserAccountEntity.UserProfileUpdateCallback() {
            @Override
            public void onSuccess() {
                Log.d("SuspendUserController", "User profile suspended successfully.");
                if (callback != null) {
                    callback.onSuccess(); // Notify success
                }
            }

            @Override
            public void onFailure(String errorMessage) {
                Log.e("SuspendUserController", "Failed to suspend user profile: " + errorMessage);
                if (callback != null) {
                    callback.onFailure(errorMessage); // Notify failure
                }
            }
        });
    }

    public interface SuspendUserCallback {
        void onSuccess();
        void onFailure(String errorMessage);
    }
}
package com.example.dietandnutritionapplication;

import static android.app.PendingIntent.getActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class ViewUserProfileController {

    private UserAccountEntity userAccountEntity;
    private MainActivity mainActivity;

    public ViewUserProfileController(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
        this.userAccountEntity = new UserAccountEntity();
    }
    public void getUserById(String userId, UserAccountEntity.UserFetchCallback callback) {
        userAccountEntity.getUserById(userId, callback);
    }

    public void uploadProfilePic(String imageUrl,Context context) {
        Log.d("UploadProfilePic", "Image URL: " + imageUrl);
        userAccountEntity.saveProfileImageUriToFirestore(imageUrl, context);
    }

    public void checkUserProfileCompletion(String userId, Context context, MainActivity mainActivity) {
       userAccountEntity.fetchUserProfile(userId, new UserAccountEntity.UserProfileCallback() {
            @Override
            public void onUserProfileFetched(User user) {

                boolean isIncomplete = isProfileIncomplete(user);

                if (isIncomplete) {
                    if (isNotOnProfileUFragment()) {
                        mainActivity.hideBottomNavigationView();
                        mainActivity.replaceFragment(new ProfileUFragment());
                        Toast.makeText(context, "Please complete your profile.", Toast.LENGTH_SHORT).show();
                    }
                }
            }
            @Override
            public void onFailure(String errorMessage) {
                Log.e("ProfileUFragment", "Error fetching user profile: " + errorMessage);
                Toast.makeText(context, "Failed to load profile.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public boolean isProfileIncomplete(User user) {
        return user.getCurrentWeight() == 0 ||
                user.getCurrentHeight() == 0 ||
                user.getActivityLevel() == null ||
                user.getActivityLevel().equals("Select your activity level") ||
                user.getHealthGoal() == null ||
                user.getActivityLevel() == "" ||
                user.getHealthGoal() == "" ||
                user.getHealthGoal().equals("Select your health goal") ||
                user.getDietaryPreference() == null;
    }



    public void updateUserProfile(String userId, User updatedUser, Context context) {
        userAccountEntity.getUserById(userId, new UserAccountEntity.UserFetchCallback() {
            @Override
            public void onUserFetched(User existingUser) {
                if (existingUser == null) {
                    Toast.makeText(context, "User not found", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Prepare a map for the fields that need updating
                Map<String, Object> updatedFields = new HashMap<>();

                // Compare and add updated fields to the map
                if (!updatedUser.getUsername().equals(existingUser.getUsername())) {
                    updatedFields.put("username", updatedUser.getUsername());
                }
                if (!updatedUser.getFirstName().equals(existingUser.getFirstName())) {
                    updatedFields.put("firstName", updatedUser.getFirstName());
                }
                if (!updatedUser.getLastName().equals(existingUser.getLastName())) {
                    updatedFields.put("lastName", updatedUser.getLastName());
                }
                if (!updatedUser.getDob().equals(existingUser.getDob())) {
                    updatedFields.put("dob", updatedUser.getDob());
                }
                if (!updatedUser.getEmail().equals(existingUser.getEmail())) {
                    updatedFields.put("email", updatedUser.getEmail());
                }
                if (!updatedUser.getGender().equals(existingUser.getGender())) {
                    updatedFields.put("gender", updatedUser.getGender());
                }
                if (!updatedUser.getPhoneNumber().equals(existingUser.getPhoneNumber())) {
                    updatedFields.put("phoneNumber", updatedUser.getPhoneNumber());
                }
                if (!updatedUser.getHealthGoal().equals(existingUser.getHealthGoal())) {
                    updatedFields.put("healthGoal", updatedUser.getHealthGoal());
                }
                if (updatedUser.getCurrentWeight() != existingUser.getCurrentWeight()) {
                    updatedFields.put("currentWeight", updatedUser.getCurrentWeight());
                }
                if (updatedUser.getCurrentHeight() != existingUser.getCurrentHeight()) {
                    updatedFields.put("currentHeight", updatedUser.getCurrentHeight());
                }
                if (!updatedUser.getDietaryPreference().equals(existingUser.getDietaryPreference())) {
                    updatedFields.put("dietaryPreference", updatedUser.getDietaryPreference());
                }
                if (!updatedUser.getFoodAllergies().equals(existingUser.getFoodAllergies())) {
                    updatedFields.put("foodAllergies", updatedUser.getFoodAllergies());
                }
                if (!updatedUser.getActivityLevel().equals(existingUser.getActivityLevel())) {
                    updatedFields.put("activityLevel", updatedUser.getActivityLevel());
                }
                if (updatedUser.getCalorieLimit() != existingUser.getCalorieLimit()) {
                    updatedFields.put("calorieLimit", updatedUser.getCalorieLimit());
                }


                // Perform the update if there are any fields to update
                if (!updatedFields.isEmpty()) {
                    userAccountEntity.updateUserProfile(userId, updatedFields, new UserAccountEntity.UserProfileUpdateCallback() {
                        @Override
                        public void onSuccess() {
                            mainActivity.showBottomNavigationView();
                            Toast.makeText(context, "Profile updated successfully.", Toast.LENGTH_SHORT).show();

                        }

                        @Override
                        public void onFailure(String errorMessage) {
                            Toast.makeText(context, "Error updating profile: " + errorMessage, Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    Toast.makeText(context, "No changes made to the profile.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(String errorMessage) {
                Toast.makeText(context, "Error fetching user data: " + errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }

    public boolean isNotOnProfileUFragment() {
        Fragment currentFragment = mainActivity.getSupportFragmentManager().findFragmentById(R.id.frame_layout);
        return !(currentFragment instanceof ProfileUFragment);
    }

}





package com.example.dietandnutritionapplication;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import java.util.HashMap;
import java.util.Map;

public class ViewAdminProfileController {

    private UserAccountEntity userAccountEntity;
    private MainActivity mainActivity;

    public ViewAdminProfileController(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
        this.userAccountEntity = new UserAccountEntity();
    }

    public void getAdminById(String adminId, UserAccountEntity.AdminFetchCallback callback) {
        userAccountEntity.getAdminById(adminId, callback);
    }

    public void uploadProfilePic(String imageUrl, Context context) {
        Log.d("UploadProfilePic", "Image URL: " + imageUrl);
        userAccountEntity.saveProfileImageUriToFirestore(imageUrl, context);
    }

    public void updateAdminProfile(String adminId, Admin updatedAdmin, Context context, UserAccountEntity.UpdateCallback callback) {
        userAccountEntity.getAdminById(adminId, new UserAccountEntity.AdminFetchCallback() {
            @Override
            public void onAdminFetched(Admin existingAdmin) {
                if (existingAdmin == null) {
                    Toast.makeText(context, "Admin not found", Toast.LENGTH_SHORT).show();
                    callback.onFailure("Admin not found");
                    return;
                }

                Map<String, Object> updatedFields = new HashMap<>();

                // Compare and add updated fields to the map
                if (!updatedAdmin.getUsername().equals(existingAdmin.getUsername())) {
                    updatedFields.put("username", updatedAdmin.getUsername());
                }
                if (!updatedAdmin.getFirstName().equals(existingAdmin.getFirstName())) {
                    updatedFields.put("firstName", updatedAdmin.getFirstName());
                }
                if (!updatedAdmin.getLastName().equals(existingAdmin.getLastName())) {
                    updatedFields.put("lastName", updatedAdmin.getLastName());
                }
                if (!updatedAdmin.getEmail().equals(existingAdmin.getEmail())) {
                    updatedFields.put("email", updatedAdmin.getEmail());
                }
                if (!updatedAdmin.getGender().equals(existingAdmin.getGender())) {
                    updatedFields.put("gender", updatedAdmin.getGender());
                }
                if (!updatedAdmin.getPhoneNumber().equals(existingAdmin.getPhoneNumber())) {
                    updatedFields.put("phoneNumber", updatedAdmin.getPhoneNumber());
                }


                if (!updatedFields.isEmpty()) {
                    userAccountEntity.updateAdminProfile(adminId, updatedFields, new UserAccountEntity.AdminProfileUpdateCallback() {
                        @Override
                        public void onSuccess() {
                            mainActivity.showBottomNavigationView();
                            Toast.makeText(context, "Profile updated successfully.", Toast.LENGTH_SHORT).show();
                            callback.onSuccess();
                        }

                        @Override
                        public void onFailure(String errorMessage) {
                            Toast.makeText(context, "Error updating profile: " + errorMessage, Toast.LENGTH_SHORT).show();
                            callback.onFailure(errorMessage);
                        }
                    });
                } else {
                    Toast.makeText(context, "No changes made to the profile.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(String errorMessage) {
                Toast.makeText(context, "Error fetching admin data: " + errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }


}
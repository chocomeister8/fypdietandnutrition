package com.fyp.dietandnutritionapplication;

import android.graphics.Bitmap;

public class CreateNProfileController {
    private NutriAccount nutriAccount;

    public CreateNProfileController(NutriAccount nutriAccount) {
        this.nutriAccount = nutriAccount;
    }

    public boolean createProfile(String firstName,String username,String name,String phoneNumber, String password, String email,String gender, String role, String dateJoined,String education, String contactInfo, String expertise, String bio, String profilePicture) {
        // For demonstration purposes, we'll use email as the unique identifier
        String gemail = generateEmailFromName(firstName);

        // Create Nutritionist object
        Nutritionist nutritionist = new Nutritionist(firstName,username,name,phoneNumber,email,gender,role,dateJoined,education,contactInfo,expertise,bio,profilePicture);

        // Save the nutritionist profile (assuming nutriAccount has a method to save a Nutritionist profile)
        return nutriAccount.saveProfile(nutritionist);
    }

    private String generateEmailFromName(String firstName) {
        // Simple email generation, adjust according to your needs
        return firstName.toLowerCase() + "@example.com";
    }
}


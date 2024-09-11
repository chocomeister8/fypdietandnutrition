package com.example.dietandnutritionapplication;

import android.graphics.Bitmap;

public class CreateNProfileController {
    private NutriAccount nutriAccount;

    public CreateNProfileController(NutriAccount nutriAccount) {
        this.nutriAccount = nutriAccount;
    }

    public boolean createProfile(String firstName, String education, String contactInfo, String expertise, String bio, Bitmap profilePicture) {
        // For demonstration purposes, we'll use email as the unique identifier
        String email = generateEmailFromName(firstName);

        // Create Nutritionist object
        Nutritionist nutritionist = new Nutritionist(email, firstName, education, contactInfo, expertise, bio, profilePicture);

        // Save the nutritionist profile (assuming nutriAccount has a method to save a Nutritionist profile)
        return nutriAccount.saveProfile(nutritionist);
    }

    private String generateEmailFromName(String firstName) {
        // Simple email generation, adjust according to your needs
        return firstName.toLowerCase() + "@example.com";
    }
}


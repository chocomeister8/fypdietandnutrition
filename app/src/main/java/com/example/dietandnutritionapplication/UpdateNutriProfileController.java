package com.example.dietandnutritionapplication;

import android.graphics.Bitmap;

public class UpdateNutriProfileController {
    private NutriAccount nutriAccount;

    public UpdateNutriProfileController(NutriAccount nutriAccount) {
        this.nutriAccount = nutriAccount;
    }

    public boolean updateProfile(String email, String name, String education, String contactInfo, String expertise, String bio, Bitmap profilePicture) {
        Nutritionist nutritionist = nutriAccount.getProfile(email); // Get the profile using email
        if (nutritionist != null) {
            nutritionist.setName(name);
            nutritionist.setEducation(education);
            nutritionist.setContactInfo(contactInfo);
            nutritionist.setExpertise(expertise);
            nutritionist.setBio(bio);
            nutritionist.setProfilePicture(profilePicture); // Set the Bitmap
            nutriAccount.saveProfile(nutritionist); // Save or update
            return true;
        }
        return false;
    }

    public boolean updateProfile(String name, String education, String contactInfo, String expertise, String bio, Bitmap profilePicturePath) {
        Nutritionist nutritionist = nutriAccount.getProfile(name); // Assuming you use `name` or another unique identifier
        if (nutritionist != null) {
            nutritionist.setName(name);
            nutritionist.setEducation(education);
            nutritionist.setContactInfo(contactInfo);
            nutritionist.setExpertise(expertise);
            nutritionist.setBio(bio);
            nutritionist.setProfilePicture(profilePicturePath); // Save the file path
            nutriAccount.saveProfile(nutritionist); // Save or update
            return true;
        }
        return false;
    }

}

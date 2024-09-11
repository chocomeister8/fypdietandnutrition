package com.example.dietandnutritionapplication;

import android.graphics.Bitmap;

public class Nutritionist extends Profile {
    private String education;
    private String contactInfo;
    private String expertise;
    private String bio;
    private Bitmap profilePicture;
    private String name;

    // Default constructor
    public Nutritionist() {
        super();
        this.setRole("nutritionist");
        this.setStatus("active");
    }

    // Parameterized constructor
    public Nutritionist(String email, String name, String education, String contactInfo, String expertise, String bio, Bitmap profilePicture) {
        super();
        this.setUsername(email); // Assuming username is set in Profile class
        this.setName(name); // Assuming firstName is a property of Profile
        this.education = education;
        this.contactInfo = contactInfo;
        this.expertise = expertise;
        this.bio = bio;
        this.profilePicture = profilePicture;
        this.setRole("nutritionist");
        this.setStatus("active");
    }

    // Getter and Setter methods
    public String getEducation() {
        return education;
    }

    public void setEducation(String education) {
        this.education = education;
    }

    public String getContactInfo() {
        return contactInfo;
    }

    public void setContactInfo(String contactInfo) {
        this.contactInfo = contactInfo;
    }

    public String getExpertise() {
        return expertise;
    }

    public void setExpertise(String expertise) {
        this.expertise = expertise;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public Bitmap getProfilePicture() {
        return profilePicture;
    }

    public void setProfilePicture(Bitmap profilePicture) {
        this.profilePicture = profilePicture;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}

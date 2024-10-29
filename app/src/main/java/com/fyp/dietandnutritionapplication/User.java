package com.fyp.dietandnutritionapplication;

import java.util.ArrayList;

public class User extends Profile {
    private int calorieLimit;
    private String dietaryPreference;
    private String foodAllergies;
    private String healthGoal;
    private double currentWeight;
    private double currentHeight;
    private double weightGoal;
    private String activityLevel;
    private String dob;
    private String profileImageUrl;

    public User(){
        this.setRole("user");
        this.setStatus("active");
    }

    public User(String firstName, String lastName, String username, String phoneNumber, String email, String gender, String role, String dateJoined, int calorieLimit, String dob, String dietaryPreference, String foodAllergies, String healthGoal, double currentWeight, double currentHeight, double weightGoal, String activityLevel, String profileImageUrl) {
        super(firstName, lastName, username, phoneNumber, email, gender, role, dateJoined);
        this.dob = dob;
        this.calorieLimit = calorieLimit;
        this.dietaryPreference = dietaryPreference;
        this.foodAllergies = foodAllergies;
        this.healthGoal = healthGoal;
        this.currentWeight = currentWeight;
        this.currentHeight = currentHeight;
        this.weightGoal = weightGoal;
        this.activityLevel = activityLevel;
        this.profileImageUrl = profileImageUrl;
    }

    public User(String username, String firstName, String lastName, String email,
                String gender, String phoneNumber, String healthGoal, double currentWeight,
                double currentHeight, double weightGoal, String dietaryPreference, String foodAllergies,
                String activityLevel) {
        super(firstName, lastName, username, phoneNumber, email, gender);
        this.healthGoal = healthGoal;
        this.currentWeight = currentWeight;
        this.currentHeight = currentHeight;
        this.weightGoal = weightGoal;
        this.dietaryPreference = dietaryPreference;
        this.foodAllergies = foodAllergies;
        this.activityLevel = activityLevel;
    }

    // Getters and Setters for all fields
    public int getCalorieLimit() {
        return calorieLimit;
    }

    public void setCalorieLimit(int calorieLimit) {
        this.calorieLimit = calorieLimit;
    }

    public String getDietaryPreference() {
        return dietaryPreference;
    }

    public void setDietaryPreference(String dietaryPreference) {
        this.dietaryPreference = dietaryPreference;
    }

    public String getFoodAllergies() {
        return foodAllergies;
    }

    public void setFoodAllergies(String foodAllergies) {
        this.foodAllergies = foodAllergies;
    }

    public String getHealthGoal() {
        return healthGoal;
    }

    public void setHealthGoal(String healthGoal) {
        this.healthGoal = healthGoal;
    }

    public double getCurrentWeight() {
        return currentWeight;
    }

    public void setCurrentWeight(double currentWeight) {
        this.currentWeight = currentWeight;
    }

    public double getCurrentHeight() {
        return currentHeight;
    }

    public void setCurrentHeight(double currentHeight) {
        this.currentHeight = currentHeight;
    }

    public double getWeightGoal() {
        return weightGoal;
    }

    public void setWeightGoal(double weightGoal) {
        this.weightGoal = weightGoal;
    }

    public String getActivityLevel() {
        return activityLevel;
    }

    public void setActivityLevel(String activityLevel) {
        this.activityLevel = activityLevel;
    }

    public String getDob() { return dob; }

    public void setDob(String dob) { this.dob = dob; }

    public String getProfileImageUrl() {
        return profileImageUrl;
    }

    public void setProfileImageUrl(String profileImageUrl) {
        this.profileImageUrl = profileImageUrl;
    }
}

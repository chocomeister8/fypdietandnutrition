package com.example.dietandnutritionapplication;

import java.util.ArrayList;

public class User extends Profile {
    private int calorieLimit;
    private String dietaryPreference;
    private String foodAllergies;
    private String healthGoal;
    private double currentWeight;
    private double currentHeight;
    private String activityLevel;

    public User(){
        this.setRole("user");
        this.setStatus("active");
    }

    public User(String firstName, String lastName, String username, String phoneNumber, String password, String email, String gender, String role, String dateJoined, int calorieLimit, String dietaryPreference, String foodAllergies, String healthGoal, double currentWeight, double currentHeight, String activityLevel) {
        super(firstName, lastName, username, phoneNumber, password, email, gender, role, dateJoined);
        this.calorieLimit = calorieLimit;
        this.dietaryPreference = dietaryPreference;
        this.foodAllergies = foodAllergies;
        this.healthGoal = healthGoal;
        this.currentWeight = currentWeight;
        this.currentHeight = currentHeight;
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

    public String getActivityLevel() {
        return activityLevel;
    }

    public void setActivityLevel(String activityLevel) {
        this.activityLevel = activityLevel;
    }
}

package com.example.dietandnutritionapplication;

import java.util.ArrayList;

public class User extends Profile {
    private String gender;
    private String phoneNumber;
    private int calorieLimit;
    private String dietPreference;
    private String foodAllergies;
    private String healthGoal;
    private double currentWeight;
    private double currentHeight;

    // Default constructor
    public User() { super();
        this.setRole("user");// Calls the default constructor of the Profile class
    }

    // Parameterized constructor
    public User(String firstName, String lastName, String username, String email, String gender,
                String phoneNumber, int calorieLimit, String dietPreference, String foodAllergies, String healthGoal,
                double currentWeight, double currentHeight) {

        this.gender = gender;
        this.phoneNumber = phoneNumber;
        this.calorieLimit = calorieLimit;
        this.dietPreference = dietPreference;
        this.foodAllergies = foodAllergies;
        this.healthGoal = healthGoal;
        this.currentWeight = currentWeight;
        this.currentHeight = currentHeight;
        this.setRole("user");
        this.setStatus("active");
    }

    // Getters and Setters for all fields

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public int getCalorieLimit() {
        return calorieLimit;
    }

    public void setCalorieLimit(int calorieLimit) {
        this.calorieLimit = calorieLimit;
    }

    public String getDietPreference() {
        return dietPreference;
    }

    public void setDietPreference(String dietPreference) {
        this.dietPreference = dietPreference;
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

}

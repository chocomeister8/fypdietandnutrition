package com.example.dietandnutritionapplication;

import java.util.ArrayList;

public class User extends Profile {
    private int calorieLimit;
    private String dietPreference;
    private String foodAllergies;
    private String healthGoal;
    private double currentWeight;
    private double currentHeight;

    public User(){
        this.setRole("user");
        this.setStatus("active");
    }

    public User(String firstName, String lastName, String username, String phoneNumber, String password, String email, String gender, String role, String dateJoined, int calorieLimit, String dietPreference, String foodAllergies, String healthGoal, double currentWeight, double currentHeight) {
        super(firstName, lastName, username, phoneNumber, password, email, gender, role, dateJoined);
        this.calorieLimit = calorieLimit;
        this.dietPreference = dietPreference;
        this.foodAllergies = foodAllergies;
        this.healthGoal = healthGoal;
        this.currentWeight = currentWeight;
        this.currentHeight = currentHeight;
    }
    // Getters and Setters for all fields


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

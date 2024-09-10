package com.example.dietandnutritionapplication;

public class User extends Profile {
    private String password;
    private String gender;
    private String phoneNumber;
    private int calorieLimit;
    private String dietPreference;
    private String foodAllergies;
    private String healthGoal;
    private double currentWeight;
    private double currentHeight;
    private String role;
    private String status;

    // Default constructor
    public User() {
        super();  // Calls the default constructor of the Profile class
    }

    // Parameterized constructor
    public User(String firstName, String lastName, String username, String email, String password, String gender,
                String phoneNumber, int calorieLimit, String dietPreference, String foodAllergies, String healthGoal,
                double currentWeight, double currentHeight, String role, String status) {
        super(firstName, lastName, username, email);  // Calls the parameterized constructor of Profile
        this.password = password;
        this.gender = gender;
        this.phoneNumber = phoneNumber;
        this.calorieLimit = calorieLimit;
        this.dietPreference = dietPreference;
        this.foodAllergies = foodAllergies;
        this.healthGoal = healthGoal;
        this.currentWeight = currentWeight;
        this.currentHeight = currentHeight;
        this.role = role;
        this.status = status;
    }

    // Getters and Setters for all fields

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

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

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}

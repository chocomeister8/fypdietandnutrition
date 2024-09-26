package com.example.dietandnutritionapplication;

import android.graphics.Bitmap;

import com.google.firebase.Timestamp;

import java.util.Date;

public class MealRecord {
    private String mealName;
    private double calories;
    private String imageUrl;
    private String mealType;  // This indicates whether it's breakfast, lunch, dinner, or snack
    private double proteins;
    private double carbs;
    private double fats;
    private Timestamp createdDate; // Date when the meal was logged
    private Timestamp modifiedDate;
    private String servingSize;
    private String username;

    public MealRecord() {
    }

    // Constructor
    public MealRecord(String mealName, double calories, String imageUrl, String mealType,
                      double carbs, double proteins, double fats, Timestamp createdDate,
                      Timestamp modifiedDate, String servingSize, String username) {
        this.mealName = mealName;
        this.calories = calories;
        this.imageUrl = imageUrl; // Use the URL for the image
        this.mealType = mealType;
        this.carbs = carbs;
        this.proteins = proteins;
        this.fats = fats;
        this.createdDate = createdDate;
        this.modifiedDate = modifiedDate;
        this.servingSize = servingSize;
        this.username = username;
    }

    // Getters
    public String getMealName() {
        return mealName;
    }

    public Double getCalories() {
        return calories;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getMealType() {
        return mealType;
    }

    public double getCarbs() {
        return carbs;
    }

    public double getProteins() {
        return proteins;
    }

    public double getFats() {
        return fats;
    }

    public Timestamp getCreatedDate() {
        return createdDate;
    }

    public Timestamp getModifiedDate() {
        return modifiedDate;
    }

    public String getServingSize() {
        return servingSize; // Return the serving size
    }

    public String getUsername() {
        return username; // Return the username
    }

    // Setters
    public void setMealName(String mealName) {
        this.mealName = mealName;
    }

    public void setCalories(double calories) {
        this.calories = calories;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public void setMealType(String mealType) {
        this.mealType = mealType;
    }

    public void setCarbs(double carbs) {
        this.carbs = carbs;
    }

    public void setProteins(double proteins) {
        this.proteins = proteins;
    }

    public void setFats(double fats) {
        this.fats = fats;
    }

    public void setCreatedDate(Timestamp createdDate) {
        this.createdDate = createdDate;
    }

    public void setModifiedDate(Timestamp modifiedDate) {
        this.modifiedDate = modifiedDate;
    }

    public void setServingSize(String servingSize) {
        this.servingSize = servingSize;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
package com.example.dietandnutritionapplication;

import android.graphics.Bitmap;

public class Meal {
    private String title;
    private int calorie;
    private Bitmap image;  // You can use Bitmap for image data or another format based on your needs
    private String mealType; // e.g., Breakfast, Lunch, Dinner

    // Constructor, getters, and setters
    public Meal(String title, int calorie, Bitmap image, String mealType) {
        this.title = title;
        this.calorie = calorie;
        this.image = image;
        this.mealType = mealType;
    }

    // Getters and Setters
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public int getCalorie() { return calorie; }
    public void setCalorie(int calorie) { this.calorie = calorie; }
    public Bitmap getImage() { return image; }
    public void setImage(Bitmap image) { this.image = image; }
    public String getMealType() { return mealType; }
    public void setMealType(String mealType) { this.mealType = mealType; }
}


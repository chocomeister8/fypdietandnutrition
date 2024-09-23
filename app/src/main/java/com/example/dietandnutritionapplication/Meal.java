package com.example.dietandnutritionapplication;

import android.graphics.Bitmap;

public class Meal {
    private String title;
    private int calorie;
    private Bitmap image;
    private String mealType;
    private int protein;
    private int carbs;
    private int fat;

    // Constructor, getters, and setters
    public Meal(String title, int calorie, Bitmap image, String mealType, int protein, int carbs, int fat) {
        this.title = title;
        this.calorie = calorie;
        this.image = image;
        this.mealType = mealType;
        this.protein = protein;
        this.carbs = carbs;
        this.fat = fat;
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
    public int getProtein() { return protein; }
    public void setProtein(int protein) { this.protein = protein; }
    public int getCarbs() { return carbs; }
    public void setCarbs(int carbs) { this.carbs = carbs; }
    public int getFat() { return fat; }
    public void setFat(int fat) { this.fat = fat; }
}


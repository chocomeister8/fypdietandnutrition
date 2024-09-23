package com.example.dietandnutritionapplication;

import java.util.List;

public class Recipe {
    private String label; // The title of the recipe
    private String image; // URL of the recipe image
    private List<String> mealType; // This should be a list since it's an array in the JSON
    private String url; // Link to the full recipe
    private List<String> ingredientLines; // List of ingredients
    private double calories; // Calorie count

    // Getters for the fields
    public String getLabel() {
        return label;
    }

    public String getImage() {
        return image;
    }

    public List<String> getMealType(){ return mealType;}

    public String getUrl() {
        return url;
    }

    public List<String> getIngredientLines() {
        return ingredientLines;
    }

    // Optionally, add a constructor
    public Recipe(String label, String image, List<String> mealType, String url, List<String> ingredientLines, double calories) {
        this.label = label;
        this.image = image;
        this.mealType = mealType;
        this.url = url;
        this.ingredientLines = ingredientLines;
        this.calories = calories;
    }

    public double getCalories() {
        return calories;
    }

    // Setter for calories
    public void setCalories(double calories) {
        this.calories = calories;
    }
}

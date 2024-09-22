package com.example.dietandnutritionapplication;

import java.util.List;

public class Recipe {
    private String label; // The title of the recipe
    private String image; // URL of the recipe image
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

    public String getUrl() {
        return url;
    }

    public List<String> getIngredientLines() {
        return ingredientLines;
    }

    public double getCalories() {
        return calories;
    }

    // Optionally, add a constructor
    public Recipe(String label, String image, String url, List<String> ingredientLines, double calories) {
        this.label = label;
        this.image = image;
        this.url = url;
        this.ingredientLines = ingredientLines;
        this.calories = calories;
    }
}

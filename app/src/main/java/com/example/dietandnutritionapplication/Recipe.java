package com.example.dietandnutritionapplication;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

public class Recipe implements Parcelable {
    private String label; // The title of the recipe
    private String image; // URL of the recipe image
    private List<String> mealType; // Meal type (e.g., lunch, dinner)
    private List<String> cuisineType; // Cuisine type (e.g., Italian, Asian)
    private List<String> dishType; // Dish type (e.g., main course, appetizer)
    private List<String> dietLabels; // Diet labels (e.g., gluten-free, vegetarian)
    private List<String> healthLabels; // Health labels (e.g., low-fat, high-protein)
    private String url; // Link to the full recipe
    private List<String> ingredientLines; // List of ingredients
    private double calories; // Calorie count
    private int totalTime; // Total cooking time in minutes

    // Getters for the fields
    public String getLabel() {
        return label;
    }

    public String getImage() {
        return image;
    }

    public List<String> getMealType() {
        return mealType;
    }

    public List<String> getCuisineType() {
        return cuisineType;
    }

    public List<String> getDishType() {
        return dishType;
    }

    public List<String> getDietLabels() {
        return dietLabels;
    }

    public List<String> getHealthLabels() {
        return healthLabels;
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

    public int getTotalTime() {
        return totalTime;
    }

    // Optionally, add a constructor
    public Recipe(String label, String image, List<String> mealType, List<String> cuisineType, List<String> dishType, List<String> dietLabels,
                  List<String> healthLabels, String url, List<String> ingredientLines, double calories, int totalTime) {
        this.label = label;
        this.image = image;
        this.mealType = mealType;
        this.cuisineType = cuisineType;
        this.dishType = dishType;
        this.dietLabels = dietLabels;
        this.healthLabels = healthLabels;
        this.url = url;
        this.ingredientLines = ingredientLines;
        this.calories = calories;
        this.totalTime = totalTime;
    }

    // Parcelable implementation for passing Recipe between fragments
    protected Recipe(Parcel in) {
        label = in.readString();
        image = in.readString();
        mealType = in.createStringArrayList();
        cuisineType = in.createStringArrayList();
        dishType = in.createStringArrayList();
        dietLabels = in.createStringArrayList();
        healthLabels = in.createStringArrayList();
        url = in.readString();
        ingredientLines = in.createStringArrayList();
        calories = in.readDouble();
        totalTime = in.readInt();
    }

    public static final Creator<Recipe> CREATOR = new Creator<Recipe>() {
        @Override
        public Recipe createFromParcel(Parcel in) {
            return new Recipe(in);
        }

        @Override
        public Recipe[] newArray(int size) {
            return new Recipe[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(label);
        dest.writeString(image);
        dest.writeStringList(mealType);
        dest.writeStringList(cuisineType);
        dest.writeStringList(dishType);
        dest.writeStringList(dietLabels);
        dest.writeStringList(healthLabels);
        dest.writeString(url);
        dest.writeStringList(ingredientLines);
        dest.writeDouble(calories);
        dest.writeInt(totalTime);
    }
}

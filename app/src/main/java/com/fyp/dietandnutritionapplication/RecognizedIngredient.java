package com.fyp.dietandnutritionapplication;

import com.google.gson.annotations.SerializedName;

public class RecognizedIngredient {

    @SerializedName("food_id")
    public String foodId;

    @SerializedName("display_name")
    public String displayName;

    @SerializedName("calories")
    public float calories;

    @SerializedName("carbs")
    public float carbs;

    @SerializedName("proteins")
    public float proteins;

    @SerializedName("fats")
    public float fats;

    @SerializedName("fibers")
    public float fibers;

    // Getters and Setters
    public String getFoodId() {
        return foodId;
    }

    public void setFoodId(String foodId) {
        this.foodId = foodId;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public float getCalories() {
        return calories;
    }

    public void setCalories(float calories) {
        this.calories = calories;
    }

    public float getCarbs() {
        return carbs;
    }

    public void setCarbs(float carbs) {
        this.carbs = carbs;
    }

    public float getProteins() {
        return proteins;
    }

    public void setProteins(float proteins) {
        this.proteins = proteins;
    }

    public float getFats() {
        return fats;
    }

    public void setFats(float fats) {
        this.fats = fats;
    }

    public float getFibers() {
        return fibers;
    }

    public void setFibers(float fibers) {
        this.fibers = fibers;
    }
}

package com.fyp.dietandnutritionapplication;

import com.google.gson.annotations.SerializedName;

public class RecognizedIngredient {


    @SerializedName("food_id")
    public String foodId;

    @SerializedName("display_name")
    public String displayName;

    @SerializedName("g_per_serving")
    public float gramsPerServing;

    @SerializedName("nutrition")
    public Nutrition nutrition;

    public static class Nutrition {
        @SerializedName("calories_100g")
        public float calories;

        @SerializedName("proteins_100g")
        public float proteins;

        @SerializedName("fat_100g")
        public float fats;

        @SerializedName("carbs_100g")
        public float carbs;

        @SerializedName("fibers_100g")
        public float fibers;

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

    public float getGramsPerServing() {
        return gramsPerServing;
    }

    public void setGramsPerServing(float gramsPerServing) {
        this.gramsPerServing = gramsPerServing;
    }

    public Nutrition getNutrition() {
        return nutrition;
    }

    public void setNutrition(Nutrition nutrition) {
        this.nutrition = nutrition;
    }


}

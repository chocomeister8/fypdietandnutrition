package com.example.dietandnutritionapplication;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class FoodResponse {
    @SerializedName("hints")
    private List<Hint> hints;

    public List<Hint> getHints() {
        return hints;
    }

    public static class Hint {
        @SerializedName("food")
        private Food food;

        public Food getFood() {
            return food;
        }
    }

    public static class Food {
        @SerializedName("label")
        private String label;

        @SerializedName("nutrients")
        private Nutrients nutrients;

        public String getLabel() {
            return label;
        }

        public Nutrients getNutrients() {
            return nutrients;
        }
    }

    public static class Nutrients {
        @SerializedName("ENERC_KCAL")
        private double calories;

        @SerializedName("PROCNT")
        private double protein;

        @SerializedName("FAT")
        private double fat;

        @SerializedName("CHOCDF")
        private double carbohydrates;

        @SerializedName("SERVING_SIZE")
        private double servingSize; // If applicable, otherwise, consider how to represent this

        public double getCalories() {
            return calories;
        }

        public double getProtein() {
            return protein;
        }

        public double getFat() {
            return fat;
        }

        public double getCarbohydrates() {
            return carbohydrates;
        }

        public double getServingSize() {
            return servingSize;
        }
    }
}

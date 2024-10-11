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
        @SerializedName("foodId")
        private String foodId; // Optional, depending on API response
        @SerializedName("uri")
        private String uri; // Unique identifier
        @SerializedName("label")
        private String label; // Name of the food
        @SerializedName("image")
        private String image; // URL of the food image
        @SerializedName("nutrients")
        private Nutrients nutrients; // Nutritional information
        @SerializedName("brand")
        private String brand; // Brand of the food item, if applicable
        @SerializedName("category")
        private String category; // Category of the food item

        // Getters and setters
        public String getFoodId() {
            return foodId;
        }

        public String getUri() {
            return uri;
        }

        public String getLabel() {
            return label;
        }

        public String getImage() {
            return image;
        }

        public Nutrients getNutrients() {
            return nutrients;
        }

        public String getBrand() {
            return brand;
        }

        public String getCategory() {
            return category;
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

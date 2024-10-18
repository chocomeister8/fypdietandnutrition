package com.fyp.dietandnutritionapplication;

import android.icu.util.Measure;

import com.google.gson.annotations.SerializedName;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FoodResponse {
    @SerializedName("hints")
    private List<Hint> hints;

    public List<Hint> getHints() {
        return hints;
    }

    public static class Hint {
        @SerializedName("food")
        private Food food;

        @SerializedName("measures")  // This should match the API response field exactly
        private List<Measure> measures;

        public Food getFood() {
            return food;
        }

        public List<Measure> getMeasures() {
            return measures;
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

        @SerializedName("measures")
        private List<Measure> measures;

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

        public List<Measure> getMeasures() {
            return measures;
        }

        public String getCategory() {
            return category;
        }

    }

    public static class Nutrients {
        @SerializedName("ENERC_KCAL")
        private double calories; // Energy in kilocalories

        @SerializedName("PROCNT")
        private double protein; // Protein in grams

        @SerializedName("FAT")
        private double fat; // Total fat in grams

        @SerializedName("CHOCDF")
        private double carbohydrates; // Carbohydrates in grams

        @SerializedName("FIBTG")
        private double fiber; // Fiber in grams

        @SerializedName("SUGAR")
        private double sugar; // Sugars in grams

        @SerializedName("CHOLE")
        private double cholesterol; // Cholesterol in milligrams

        @SerializedName("NA")
        private double sodium; // Sodium in milligrams

        @SerializedName("CA")
        private double calcium; // Calcium in milligrams

        @SerializedName("FE")
        private double iron; // Iron in milligrams

        @SerializedName("VITC")
        private double vitaminC; // Vitamin C in milligrams

        @SerializedName("VITD")
        private double vitaminD; // Vitamin D in micrograms

        @SerializedName("TOCPHA")
        private double vitaminE; // Vitamin E in milligrams

        @SerializedName("VITK1")
        private double vitaminK; // Vitamin K in micrograms

        // Getters for all nutrients

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

        public double getFiber() {
            return fiber;
        }

        public double getSugar() {
            return sugar;
        }

        public double getCholesterol() {
            return cholesterol;
        }

        public double getSodium() {
            return sodium;
        }

        public double getCalcium() {
            return calcium;
        }

        public double getIron() {
            return iron;
        }

        public double getVitaminC() {
            return vitaminC;
        }

        public double getVitaminD() {
            return vitaminD;
        }

        public double getVitaminE() {
            return vitaminE;
        }

        public double getVitaminK() {
            return vitaminK;
        }

        public Map<String, Double> getAllNutrients() {
            Map<String, Double> nutrientMap = new HashMap<>();
            nutrientMap.put("Calories", calories);
            nutrientMap.put("Protein", protein);
            nutrientMap.put("Fat", fat);
            nutrientMap.put("Carbohydrates", carbohydrates);
            nutrientMap.put("Fiber", fiber);
            nutrientMap.put("Sugar", sugar);
            nutrientMap.put("Cholesterol", cholesterol);
            nutrientMap.put("Sodium", sodium);
            nutrientMap.put("Calcium", calcium);
            nutrientMap.put("Iron", iron);
            nutrientMap.put("Vitamin C", vitaminC);
            nutrientMap.put("Vitamin D", vitaminD);
            nutrientMap.put("Vitamin E", vitaminE);
            nutrientMap.put("Vitamin K", vitaminK);

            return nutrientMap;
        }
    }


    public static class Measure {
        @SerializedName("uri")
        private String uri;
        @SerializedName("label")
        private String label;  // Example: "cup", "gram"
        @SerializedName("weight")
        private double weight;  // Example: 250 grams for 1 cup
        @SerializedName("qualified")
        private List<WeightedMeasure> qualified;  // Qualified measures

        public String getUri() {
            return uri;
        }

        public String getLabel() {
            return label;
        }

        public double getWeight() {
            return weight;
        }

        public List<WeightedMeasure> getQualified() {
            return qualified;
        }
    }

    public static class WeightedMeasure {
        @SerializedName("qualifiers")
        private List<Term> qualifiers;  // List of terms (qualifiers)
        @SerializedName("weight")
        private double weight;  // Weight of the qualified measure

        public List<Term> getQualifiers() {
            return qualifiers;
        }

        public double getWeight() {
            return weight;
        }
    }

    public static class Term {
        @SerializedName("uri")
        private String uri;
        @SerializedName("label")
        private String label;  // Example: "large", "small", etc.

        public String getUri() {
            return uri;
        }

        public String getLabel() {
            return label;
        }
    }
}

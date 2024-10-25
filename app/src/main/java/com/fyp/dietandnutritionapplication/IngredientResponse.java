package com.fyp.dietandnutritionapplication;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class IngredientResponse {
    @SerializedName("items")

    private List<RecognizedIngredient> results;

    public List<RecognizedIngredient> getResults() {
        return results;
    }
}
